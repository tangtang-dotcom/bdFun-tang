package com.aritxonly.teak.common.coui

import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.itos.coui.segmentbutton.SegmentedControl
import com.itos.coui.theme.CouiTheme

/**
 * 顶栏分段条：完全交给库的 [SegmentedControl]。
 *
 * 库内部负责选中指示器胶囊、跟手/按压形变、聚光、透明材质与文字度量；
 * 这里只把采样源换成顶栏提供的工具栏 backdrop。
 */
@RequiresApi(34)
@Composable
fun TeakCouiSegmented(
    labels: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = CouiTheme.isDark,
) {
    val backdrop = LocalTeakCouiToolbarBackdrop.current
    SegmentedControl(
        modifier = modifier,
        labels = labels,
        selectedIndex = selectedIndex,
        onSelected = onSelected,
        backdrop = backdrop,
        dark = dark,
    )
}
