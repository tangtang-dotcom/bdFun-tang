package com.aritxonly.teak.common.coui

import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.backdrop.drawCouiBackdrop

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
