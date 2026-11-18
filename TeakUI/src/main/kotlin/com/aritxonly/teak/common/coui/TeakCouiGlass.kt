package com.aritxonly.teak.common.coui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.backdrop.drawCouiBackdrop
import com.itos.coui.effect.CouiFloatingMaterial
import com.itos.coui.effect.CouiFloatingRenderer
import com.itos.coui.effect.CouiFloatingShadow
import com.itos.coui.effect.CouiFloatingSurface
import com.itos.coui.effect.couiFloatingSurface

/** 外壳层提供的 COUI 采样源；只有 COUI 风格下非空。 */
val LocalTeakCouiBackdrop = staticCompositionLocalOf<CouiBackdrop?> { null }

/**
 * 小面积 COUI 材质：直接走库的 [drawCouiBackdrop]（采样 + blur），
 * 表面叠一层半透明容器色 + 顶部高光 + 0.75dp 亮边。
 *
 * 与库的浮动材质不同，这里不绘制矩形阴影补丁，因此不会在非校准机型上涂黑；
 * 观感与 `CouiSegmentedControlDefaults` / `CouiMenuMaterial` 的容器一致。
 */
@Composable
fun Modifier.teakCouiGlassSurface(
    backdrop: CouiBackdrop,
    shape: Shape,
    dark: Boolean,
    blurRadius: Dp = 25.dp,
    surfaceAlpha: Float = if (dark) 0.72f else 0.66f,
): Modifier = this
    .drawCouiBackdrop(
        backdrop = backdrop,
        shape = { shape },
        effects = { blur(blurRadius.toPx()) },
        onDrawSurface = {
            drawRect(
                if (dark) Color(0xFF1B1B1B).copy(alpha = surfaceAlpha)
                else Color.White.copy(alpha = surfaceAlpha),
            )
            drawRect(
                Brush.verticalGradient(
                    0f to Color.White.copy(alpha = if (dark) 0.10f else 0.55f),
                    1f to Color.Transparent,
                ),
            )
        },
    )
    .border(
        width = 0.75.dp,
        color = if (dark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.65f),
        shape = shape,
    )

/**
 * 库的完整浮动材质（CouiFloatingMaterial + CouiFloatingShadow + 采样背景 + 轮廓光），
 * 只作为背景层铺在内容下面，内容照常作为 Box 子节点绘制。
 *
 * 与 `TeakCouiGlassSurface` 的区别：这一层用的是库自己的 AGSL 材质管线
 * （`couiFloatingSurface`），会带边缘高光、内发光、轮廓光与 Mesh 阴影，
 * 观感与底栏完全一致。API < 34 时只画普通容器色。
 */
@Composable
fun TeakCouiFloatingBackground(
    backdrop: CouiBackdrop,
    shape: Shape,
    material: CouiFloatingMaterial,
    shadow: CouiFloatingShadow,
    modifier: Modifier = Modifier,
    dark: Boolean = com.itos.coui.theme.CouiTheme.isDark,
    contourGlowEnabled: Boolean = false,
    /** 材质之上再叠一层原厂容器色（分段条要 0x0F000000 那层灰，否则白底上像纯白）。 */
    tint: Color? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    if (android.os.Build.VERSION.SDK_INT < 34) {
        Box(
            modifier = modifier
                .background(com.itos.coui.theme.CouiTheme.colorScheme.container.copy(alpha = 0.90f), shape)
                .border(0.75.dp, Color.White.copy(alpha = if (dark) 0.14f else 0.65f), shape),
        ) { content() }
        return
    }
    TeakCouiFloatingBackgroundApi34(backdrop, shape, material, shadow, modifier, contourGlowEnabled, tint, content)
}

@androidx.annotation.RequiresApi(34)
@Composable
private fun TeakCouiFloatingBackgroundApi34(
    backdrop: CouiBackdrop,
    shape: Shape,
    material: CouiFloatingMaterial,
    shadow: CouiFloatingShadow,
    modifier: Modifier,
    contourGlowEnabled: Boolean,
    tint: Color?,
    content: @Composable BoxScope.() -> Unit,
) {
    val renderer = remember { CouiFloatingRenderer.standalone() }
    val contentLayer = rememberGraphicsLayer()
    var size by remember { mutableStateOf(IntSize.Zero) }
    Box(modifier = modifier.onSizeChanged { size = it }) {
        if (size.width > 0 && size.height > 0) {
            Spacer(
                Modifier
                    .matchParentSize()
                    .couiFloatingSurface(
                        backdrop = backdrop,
                        renderer = renderer,
                        material = material,
                        shadow = shadow,
                        shape = shape,
                        contentLayer = contentLayer,
                        contourGlowEnabled = contourGlowEnabled,
                        foregroundComposition = false,
                        surface = {
                            CouiFloatingSurface(
                                bounds = Rect(0f, 0f, size.width.toFloat(), size.height.toFloat()),
                            )
                        },
                    ),
            )
        }
        if (tint != null) {
            Spacer(Modifier.matchParentSize().background(tint, shape))
        }
        content()
    }
}

/** 库默认的「无阴影」配置，和 SegmentedControl 内部一致。 */
val TeakCouiNoShadow: CouiFloatingShadow
    get() = CouiFloatingShadow(
        elevation = 0f, color = Color.Transparent, lightY = 0f, lightZ = 0f, lightRadius = 0f,
        causticColor = Color.Transparent, layout = Rect.Zero, sdf = 0f,
    )


/** 分段条轨道：库 CouiSegmentedControlDefaults.containerColor 的两个色阶 + 顶部内高光 + 亮边。 */
fun Modifier.couiSegmentedTrack(dark: Boolean): Modifier = this
    .background(
        brush = Brush.verticalGradient(
            listOf(
                if (dark) Color(0x33FFFFFF) else Color(0x1F000000),
                if (dark) Color(0x1FFFFFFF) else Color(0x0D000000),
            ),
        ),
        shape = com.itos.coui.shape.CouiCapsuleShape,
    )
    .border(
        width = 0.75.dp,
        color = if (dark) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.55f),
        shape = com.itos.coui.shape.CouiCapsuleShape,
    )
