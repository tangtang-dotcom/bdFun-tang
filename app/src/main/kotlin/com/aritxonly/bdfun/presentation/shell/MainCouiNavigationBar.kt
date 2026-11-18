package com.aritxonly.bdfun

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.interaction.blockTouches
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Search

/**
 * COUI 原生浮动底栏。
 *
 * 直接使用 coui 的 FloatingNavigationBar：玻璃胶囊、选中态滑块、左右附件位都由库内部实现，
 * 因此视觉与 ColorOS 原生一致，不再依赖 bdFun 自绘的 TeakFloatingTabBar。
 * 库自带 26dp + 导航栏 inset 的下边距，外层不要再叠 navigationBarsPadding。
 */
@Composable
internal fun MainCouiNavigationBar(
    destinations: List<Destination>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    covered: Boolean = false,
    showSearch: Boolean = false,
    onSearch: () -> Unit = {},
    horizontalPadding: Dp = 16.dp,
) {
    FloatingNavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha }
            .blockTouches(covered),
        cornerRadius = 26.dp,
        horizontalAlignment = Alignment.CenterHorizontally,
        horizontalOutSidePadding = horizontalPadding,
        shadowElevation = 12.dp,
        showDivider = false,
        defaultWindowInsetsPadding = true,
    ) {
        destinations.forEach { destination ->
            FloatingNavigationBarItem(
                selected = destination.key == selectedKey,
                onClick = { onSelect(destination.key) },
                icon = destination.icon,
                label = destination.label,
            )
        }
        // HyperOS4 版式原有底部搜索入口，COUI 底栏用附件位承载，避免功能丢失。
        if (showSearch) {
            FloatingNavigationBarItem(
                selected = false,
                onClick = onSearch,
                icon = COUIIcons.Search,
                label = "搜索",
            )
        }
    }
}
