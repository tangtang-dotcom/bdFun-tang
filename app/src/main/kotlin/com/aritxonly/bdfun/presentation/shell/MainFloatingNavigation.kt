package com.aritxonly.bdfun

import com.aritxonly.teak.common.theme.TeakStyle

import com.aritxonly.teak.common.chrome.TeakChromeDefaults
import com.aritxonly.teak.common.chrome.isWideFoldTopBar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.navigation.TeakFloatingTabItem
import com.aritxonly.bdfun.feature.settings.presentation.SettingsBindings
import com.aritxonly.teak.common.interaction.blockTouches

/**
 * 悬浮底栏装配。
 *
 * 现在内容交给 COUI 原生 FloatingNavigationBar（见 MainCouiNavigationBar），
 * 这里只保留壳层职责：整条底栏的透明度/位移/触摸穿透，以及「底栏上浮」的内边距。
 */
@Composable
internal fun androidx.compose.foundation.layout.BoxScope.MainFloatingNavigation(
    settings: SettingsBindings, destinations: List<Destination>, tabItems: List<TeakFloatingTabItem>,
    selectedKey: String, onBarItem: (TeakFloatingTabItem) -> Unit, visual: MainBottomNavigationVisual,
    onBottomSearch: () -> Unit,
) {

    val wideFold = isWideFoldTopBar()
    val hasSearch = TeakStyle.fromStoredValue(settings.interfaceStyle).layout.hasBottomSearch
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .graphicsLayer {
                alpha = visual.tabBarAlpha
                translationX = -visual.discSlot * visual.discShift.toPx()
            }
            .blockTouches(visual.tabBarCovered)
            .padding(
                top = 12.dp,
                bottom = if (settings.bottomBarRaise) TeakChromeDefaults.RaisedBottomBarExtra else 0.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MainCouiNavigationBar(
            destinations = destinations,
            selectedKey = selectedKey,
            onSelect = { key -> tabItems.firstOrNull { it.key == key }?.let(onBarItem) },
            showSearch = hasSearch,
            onSearch = onBottomSearch,
            horizontalPadding = if (wideFold) 12.dp else 16.dp,
        )
    }
}
