package com.aritxonly.bdfun


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.size
import com.aritxonly.teak.common.navigation.TeakFloatingTabItem
import com.aritxonly.teak.common.theme.TeakUiStyle
import com.aritxonly.bdfun.feature.settings.presentation.SettingsBindings
import com.aritxonly.bdfun.core.model.video.VideoItem
import com.aritxonly.teak.common.interaction.blockTouches

@Composable
internal fun androidx.compose.foundation.layout.BoxScope.MainBottomNavigation(
    settings: SettingsBindings, dark: Boolean, wideNavigation: Boolean, searchVisible: Boolean,
    tabItems: List<TeakFloatingTabItem>, selectedKey: String,
    onBarItem: (TeakFloatingTabItem) -> Unit, onSearch: () -> Unit, onOpenVideo: (VideoItem) -> Unit,
    onBottomSearch: () -> Unit,
    destinations: List<Destination> = emptyList(),
    couiBackdrop: com.itos.coui.backdrop.CouiBackdrop? = null,
) {
    val visual = rememberMainBottomNavigationVisual(settings, dark, wideNavigation, searchVisible, tabItems.size)
    if (TeakUiStyle.isCoui && couiBackdrop != null && android.os.Build.VERSION.SDK_INT >= 34) {
        // COUI 风格：底栏交给 COUI-Kit 的 CouiDockBar（材质 + 融球 + 跟手形变都是库自带的）。
        MainCouiBottomBar(
            destinations = destinations,
            selectedKey = selectedKey,
            dark = dark,
            backdrop = couiBackdrop,
            onSelect = { key -> tabItems.firstOrNull { it.key == key }?.let(onBarItem) },
            onSearch = if (searchVisible) onBottomSearch else null,
        )
        // 唱片仍由上游的 MainNavigationDisc 绘制（COUI 底栏只接管菜单/搜索，不吞掉唱片）。
        if (visual.discSlot > 0.01f) MainNavigationDisc(settings, visual, onOpenVideo)
        return
    }
    if (visual.tabBarAlpha > 0.01f) {
        MainBottomNavigationShade(settings, wideNavigation, visual.tabBarAlpha)
        if (wideNavigation) {
            if (TeakUiStyle.current.layout.edgeNavigation) {
                // 经典样式在宽屏上也保持贴边底栏，不用悬浮胶囊和单独搜索钮。
                ClassicTabBar(
                    items = tabItems,
                    selectedKey = selectedKey,
                    onItemSelected = onBarItem,
                    selectedContentColor = visual.barSelectedColor,
                    showDivider = true,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .graphicsLayer { alpha = visual.tabBarAlpha }
                        .blockTouches(visual.tabBarCovered)
                        .navigationBarsPadding(),
                )
            } else {
                MainWideNavigation(tabItems, selectedKey, onBarItem, visual, onSearch)
            }
        } else if (TeakUiStyle.current.layout.edgeNavigation) {

            // 经典样式：底栏贴着屏幕底边，上方一条分割线，不做悬浮胶囊。
            ClassicTabBar(
                items = tabItems,
                selectedKey = selectedKey,
                onItemSelected = onBarItem,
                selectedContentColor = visual.barSelectedColor,
                showDivider = true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = visual.tabBarAlpha
                        translationX = -visual.discSlot * visual.discShift.toPx()
                    }
                    .blockTouches(visual.tabBarCovered)
                    .navigationBarsPadding(),
            )
        } else {
            MainFloatingNavigation(settings, tabItems, selectedKey, onBarItem, visual, onBottomSearch, onOpenVideo)
        }
        // HyperOS 4 窄屏把唱片叠进搜索键，不再额外占 bottom-end 的位置。
        val discStackedInSearch = settings.interfaceStyle == 2 && !wideNavigation && !TeakUiStyle.isClassic
        if (!discStackedInSearch && visual.discSlot > 0.01f) MainNavigationDisc(settings, visual, onOpenVideo)
    }
}
