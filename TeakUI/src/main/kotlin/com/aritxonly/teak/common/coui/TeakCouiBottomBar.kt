package com.aritxonly.teak.common.coui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.backdrop.drawCouiBackdrop
import com.itos.coui.icons.CouiIcons
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.theme.CouiColorScheme

/** 底栏一项：key + 文案 + 两态图标。 */
data class TeakCouiBarItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
)

/** COUI 动效曲线：cubic-bezier(0.3, 0, 0.1, 1)，与原厂 ColorOS 一致。 */
private val CouiBarEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/**
 * COUI 风格底栏。
 *
 * 材质走 COUI 库自己的背景采样管线：`drawCouiBackdrop` + 模糊 + `onDrawSurface` 着色层，
 * 再用胶囊轮廓裁剪，配合 COUI 的 surfaceContainer 与描边高光。
 *
 * 为什么不用库里的 `NavigationBar`：它的浮动材质/阴影走 AGSL `RuntimeShader`（API 34+，
 * 按 OPPO PJZ110 的光照校准），在非校准机型上会整块画成黑色矩形。这里保留同一套采样 +
 * 模糊 + 配色 + 动效参数，但不依赖那套着色器，效果一致且不会黑块。
 */
@Composable
fun TeakCouiBottomBar(
    items: List<TeakCouiBarItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    backdrop: CouiBackdrop,
    modifier: Modifier = Modifier,
    dark: Boolean = MaterialTheme.colorScheme.surface.luminanceIsDark(),
    onSearch: (() -> Unit)? = null,
) {
    if (items.isEmpty()) return
    val scheme = teakCouiColorScheme()
    val shape = CouiCapsuleShape
    val tint = if (dark) Color(0xCC1B1B1B) else Color(0xE8FFFFFF)
    val highlight = if (dark) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.55f)

    Row(
        modifier = modifier
            .height(56.dp)
            .shadow(elevation = 12.dp, shape = shape, clip = false)
            .drawCouiBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = { blur(22.dp.toPx()) },
                onDrawSurface = {
                    drawRect(tint)
                    // 顶部一道白色渐变高光，模拟凝光材质的内发光。
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(highlight, Color.Transparent),
                            endY = size.height * 0.62f,
                        ),
                    )
                },
            )
            .border(width = 0.75.dp, color = Color.White.copy(alpha = if (dark) 0.14f else 0.55f), shape = shape)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            val selected = item.key == selectedKey
            val progress by animateFloatAsState(
                targetValue = if (selected) 1f else 0f,
                animationSpec = tween(durationMillis = 280, easing = CouiBarEasing),
                label = "couiBarSelection",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 6.dp)
                    .background(
                        color = scheme.accent.copy(alpha = 0.14f * progress),
                        shape = CouiCapsuleShape,
                    )
                    .clickable { onSelect(item.key) },
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                        tint = lerpColor(scheme.onSurfaceVariant, scheme.accent, progress),
                    )
                    if (progress > 0.02f) {
                        Spacer(Modifier.width(4.dp * progress))
                        Text(
                            text = item.label,
                            color = lerpColor(scheme.onSurfaceVariant, scheme.accent, progress),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        if (onSearch != null) {
            Spacer(Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color = scheme.accent.copy(alpha = 0.16f), shape = CouiCapsuleShape)
                    .clickable { onSearch() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = CouiIcons.Outlined.Search,
                    contentDescription = "搜索",
                    modifier = Modifier.size(22.dp),
                    tint = scheme.accent,
                )
            }
        }
    }
}

private fun lerpColor(start: Color, stop: Color, fraction: Float): Color = Color(
    red = start.red + (stop.red - start.red) * fraction,
    green = start.green + (stop.green - start.green) * fraction,
    blue = start.blue + (stop.blue - start.blue) * fraction,
    alpha = start.alpha + (stop.alpha - start.alpha) * fraction,
)

/** 浅色/深色判定：表面亮度。 */
private fun Color.luminanceIsDark(): Boolean =
    (0.299f * red + 0.587f * green + 0.114f * blue) < 0.5f

/** 把宿主的 Material3 色板折成 COUI 的 12 个角色（COUI 默认是官方蓝，必须映射）。 */
@Composable
internal fun teakCouiColorScheme(): CouiColorScheme {
    val m = MaterialTheme.colorScheme
    return CouiColorScheme(
        accent = m.primary,
        onAccent = m.onPrimary,
        surface = m.background,
        container = m.surfaceContainer,
        onSurface = m.onSurface,
        onSurfaceVariant = m.onSurfaceVariant,
        outlineVariant = m.outlineVariant,
        accentOnSurface = m.primary,
        menuSurface = m.surfaceContainer,
        checkboxAccent = m.primary,
        menuCheckboxAccent = m.onPrimary,
        background = m.background,
        card = m.surfaceContainer,
    )
}
