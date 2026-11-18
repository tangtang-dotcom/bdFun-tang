package com.aritxonly.teak.common.coui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.theme.TeakUiStyle
import com.itos.coui.theme.CouiTheme

/**
 * COUI 风格的小控件表面（顶栏圆钮、搜索胶囊、分段胶囊）。
 *
 * 为什么不直接用 `couiFloatingSurface`：那套是 AGSL 浮动材质，材质层通过
 * `GraphicsLayer.record` 参与窗口合成，在这台机器上会把控件内容一起吞掉（v18/v19 都翻车）。
 * 这里取库同一份材质 token（surfaceContainer / outlineVariant / 阴影半径），
 * 用普通 Compose 画在内容下面——观感一致，内容永远不会被盖住。
 */
@Composable
fun Modifier.teakCouiControlSurface(
    shape: Shape,
    enabled: Boolean = TeakUiStyle.isCoui,
    elevation: Dp = 8.dp,
): Modifier {
    if (!enabled) return this
    val scheme = CouiTheme.colorScheme
    val dark = CouiTheme.isDark
    return this
        .shadow(elevation, shape, clip = false)
        .clip(shape)
        .background(scheme.container.copy(alpha = if (dark) 0.72f else 0.88f))
        .border(
            width = 0.75.dp,
            color = scheme.outlineVariant.copy(alpha = if (dark) 0.45f else 0.6f),
            shape = shape,
        )
}

/** 圆形/胶囊控件：材质在下，内容在上。 */
@Composable
fun TeakCouiControlBox(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    enabled: Boolean = TeakUiStyle.isCoui,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.teakCouiControlSurface(shape, enabled), contentAlignment = contentAlignment) {
        content()
    }
}
