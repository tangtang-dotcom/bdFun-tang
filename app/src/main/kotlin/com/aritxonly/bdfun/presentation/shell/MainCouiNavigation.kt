package com.aritxonly.bdfun

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.chrome.isWideFoldTopBar
import com.aritxonly.teak.common.coui.TeakCouiGlyphHome
import com.aritxonly.teak.common.coui.TeakCouiSearchButton
import com.aritxonly.teak.common.coui.TeakCouiTabBar
import com.aritxonly.teak.common.coui.TeakCouiTabItem
import com.aritxonly.teak.common.coui.teakCouiAlbum
import com.aritxonly.teak.common.coui.teakCouiUser
import com.aritxonly.teak.common.interaction.blockTouches

/**
 * COUI 底栏装配。
 *
 * 宽度不再沿用 HyperOS 4 的 320dp 上限（实测装上去只有中间一小段胶囊），
 * 改成“能用多宽用多宽、最多 480dp”；搜索入口和 HyperOS 4 一样以独立圆钮挂在右侧，
 * 这样切风格时位置不会突然消失。
 */
@Composable
internal fun BoxScope.MainCouiNavigation(
    destinations: List<Destination>,
    selectedKey: String,
    visual: MainBottomNavigationVisual,
    dark: Boolean,
    showSearch: Boolean,
    onSearch: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val items = destinations.map { d ->
        val icon = couiGlyphFor(d)
        TeakCouiTabItem(key = d.key, label = d.label, icon = icon, selectedIcon = icon)
    }
    val wideFold = isWideFoldTopBar()
    val horizontalPadding = if (wideFold) 12.dp else 16.dp
    // 有搜索钮时给它留出 56dp + 8dp 间隙。
    val maximumWidth = if (showSearch) 480.dp else 420.dp
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .graphicsLayer { alpha = visual.tabBarAlpha }
            .blockTouches(visual.tabBarCovered)
            .navigationBarsPadding()
            .padding(start = horizontalPadding, end = horizontalPadding, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .widthIn(max = maximumWidth)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TeakCouiTabBar(
                items = items,
                selectedKey = selectedKey,
                onItemSelected = { onSelect(it.key) },
                modifier = Modifier.weight(1f),
                dark = dark,
            )
            if (showSearch) {
                Spacer(Modifier.width(8.dp))
                TeakCouiSearchButton(onClick = onSearch, dark = dark)
            }
        }
    }
}

@Composable
private fun couiGlyphFor(d: Destination): ImageVector = when (d.key) {
    "home" -> TeakCouiGlyphHome
    "dynamics" -> teakCouiAlbum()
    "mine" -> teakCouiUser()
    else -> d.icon
}
