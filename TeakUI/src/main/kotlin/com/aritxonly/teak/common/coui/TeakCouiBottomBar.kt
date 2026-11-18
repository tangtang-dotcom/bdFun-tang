package com.aritxonly.teak.common.coui

import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.effect.CouiFloatingMaterial
import com.itos.coui.effect.CouiFloatingRenderer
import com.itos.coui.effect.CouiFloatingShadow
import com.itos.coui.effect.CouiFloatingSurface
import com.itos.coui.effect.couiFloatingSurface
import com.itos.coui.motion.couiPressMotion
import com.itos.coui.motion.rememberCouiPressMotion
import com.itos.coui.theme.CouiTheme

/** 底栏条目：key 用于回传，label 参与排版，icon 由调用方从 CouiIcons 选。 */
data class TeakCouiBarItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
)

private val CouiBarShape = RoundedCornerShape(26.dp)
private val CouiItemShape = RoundedCornerShape(20.dp)
private const val CouiBarHeightDp = 58

/**
 * COUI 风格底栏：材质、描边、轮廓光、按压回弹全部由 COUI-Kit 的浮动表面渲染
 * （[couiFloatingSurface] + [CouiFloatingMaterial]，与 ColorOS 官方底栏同一套着色器）。
 *
 * 与原厂实现的唯一差别：阴影不走 COUI 的原生 tessellator（它按 OPPO 机型的光照校准，
 * 在未校准的健康机型上会整块涂黑），改用 Compose 的 elevation 阴影，
 * 其余边缘光、内发光、凝光混色仍由 RuntimeShader 输出。
 */
@RequiresApi(34)
@Composable
fun TeakCouiBottomBar(
    items: List<TeakCouiBarItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    backdrop: CouiBackdrop,
    modifier: Modifier = Modifier,
    dark: Boolean = CouiTheme.isDark,
    onSearch: (() -> Unit)? = null,
) {
    if (items.isEmpty()) return
    val renderer = remember { CouiFloatingRenderer.standalone() }
    val material = remember(dark) {
        CouiFloatingMaterial(
            firstColor = if (dark) Color(0x80666666) else Color(0xA3666666),
            firstBlendMode = if (dark) android.graphics.BlendMode.OVERLAY else android.graphics.BlendMode.COLOR_DODGE,
            secondColor = if (dark) Color(0xB3262626) else Color(0x80F0F0F0),
            edgeAlpha = if (dark) 0.8f else 0.25f,
            innerGlowAlpha = if (dark) 0.03f else 0.1f,
            lightColor = Color.White,
            themeTint = Color.Transparent,
        )
    }
    val shadow = remember {
        CouiFloatingShadow(
            elevation = 0f, color = Color.Transparent,
            lightY = 0f, lightZ = 0f, lightRadius = 0f,
            causticColor = Color.Transparent,
            layout = Rect.Zero, sdf = 0f,
        )
    }
    val density = LocalDensity.current.density
    val press = rememberCouiPressMotion()
    var bounds by remember { mutableStateOf(IntSize.Zero) }
    val contentLayer = rememberGraphicsLayer()
    val scheme = CouiTheme.colorScheme

    Row(
        modifier = modifier
            .height(CouiBarHeightDp.dp)
            .onSizeChanged { bounds = it }
            .couiPressMotion(press, true, cancelOnConsumedMove = true)
            .shadow(elevation = 12.dp, shape = CouiBarShape, clip = false)
            .clip(CouiBarShape)
            .couiFloatingSurface(
                backdrop = backdrop,
                renderer = renderer,
                material = material,
                shadow = shadow,
                shape = CouiBarShape,
                contentLayer = contentLayer,
                contourGlowEnabled = true,
                foregroundComposition = true,
            ) {
                CouiFloatingSurface(
                    bounds = Rect(Offset.Zero, Size(bounds.width.toFloat(), bounds.height.toFloat())),
                    scaleX = press.scale.value,
                    scaleY = press.scale.value,
                    cornerRadius = 26.dp.value * density,
                )
            }
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .graphicsLayer {
                    scaleX = press.scale.value
                    scaleY = press.scale.value
                }
                .drawWithContent { contentLayer.record { this@drawWithContent.drawContent() } },
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                CouiBarItemView(
                    item = item,
                    selected = item.key == selectedKey,
                    unselectedColor = scheme.onSurface,
                    accentColor = scheme.accent,
                    onClick = { onSelect(item.key) },
                )
            }
        }
        if (onSearch != null) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(scheme.accent.copy(alpha = 0.12f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSearch,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = com.itos.coui.icons.CouiIcons.Outlined.Search,
                    contentDescription = "搜索",
                    tint = scheme.accent,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun RowScope.CouiBarItemView(
    item: TeakCouiBarItem,
    selected: Boolean,
    unselectedColor: Color,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = CouiBarEase),
        label = "coui-bar-item",
    )
    val tint = lerp(unselectedColor, accentColor, progress)
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(vertical = 6.dp)
            .clip(CouiItemShape)
            .background(accentColor.copy(alpha = 0.14f * progress))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.icon,
                contentDescription = item.label,
                tint = tint.copy(alpha = 0.98f),
                modifier = Modifier.size(21.dp),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.label,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                color = tint.copy(alpha = 0.92f),
            )
        }
    }
}

private val CouiBarEase = androidx.compose.animation.core.CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
