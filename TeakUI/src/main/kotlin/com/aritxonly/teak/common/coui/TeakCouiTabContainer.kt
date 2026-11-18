package com.aritxonly.teak.common.coui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.itos.coui.theme.CouiTheme

/**
 * 分段条容器的 COUI 材质：与库 `SegmentedControlDefaults.containerColor` 同源的半透明底色、
 * 0.75dp 亮边与 10dp 悬浮阴影；不改变宿主原有的尺寸、布局与滑动逻辑。
 */
@androidx.compose.runtime.Composable
fun Modifier.couiTabContainer(dark: Boolean): Modifier {
    val scheme = CouiTheme.colorSchemeFor(dark)
    val shape = RoundedCornerShape(percent = 50)
    return this
        .shadow(elevation = 8.dp, shape = shape, clip = false)
        .clip(shape)
        .background(if (dark) Color(0x1FFFFFFF) else Color(0x0F000000), shape)
        .border(0.75.dp, scheme.outlineVariant.copy(alpha = 0.6f), shape)
}
