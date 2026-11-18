package com.aritxonly.bdfun

import com.aritxonly.teak.common.chrome.TeakChromeDefaults
import com.aritxonly.teak.common.chrome.isWideFoldTopBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import com.aritxonly.bdfun.ui.ScrollTopButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import com.aritxonly.teak.common.search.teakSearchUnderlay
import com.aritxonly.teak.common.navigation.TeakFloatingTabBarDefaults
import com.aritxonly.bdfun.feature.settings.presentation.SettingsBindings
import top.yukonga.miuix.kmp.blur.layerBackdrop
import com.aritxonly.teak.common.theme.TeakStyle
import com.itos.coui.backdrop.couiBackdrop
import com.itos.coui.backdrop.rememberCouiBackdrop

@Composable
internal fun MainScaffold(settings: SettingsBindings, shell: MainShellState, dark: Boolean, background: Color) {
    MainDockScrollScope(settings, shell) { modifier ->
        MainScaffoldContent(settings, shell, dark, background, modifier)
    }
}

@Composable
private fun MainScaffoldContent(settings: SettingsBindings, shell: MainShellState, dark: Boolean, background: Color, modifier: Modifier) {
    val ui = shell.ui
    val session = shell.feeds.session
    val homeFeed = shell.feeds.home
    val dynamicFeed = shell.feeds.dynamic
    val tabPagerState = shell.navigation.pager
    val backdrop = shell.backdrops.main
    val wideNavigation = shell.backdrops.wideNavigation
    val floatingBottomSpacing = if (isWideFoldTopBar()) 0.dp else 4.dp
    val pageFloat = (tabPagerState.currentPage + tabPagerState.currentPageOffsetFraction)
        .coerceAtLeast(0f)
    // COUI 底栏的玻璃采样源：整页内容。
    val couiStyle = TeakStyle.fromStoredValue(settings.interfaceStyle).coui
    val couiBackdrop = if (couiStyle) rememberCouiBackdrop() else null
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background),
    ) {
        Box(Modifier.fillMaxSize().background(background).teakSearchUnderlay(
                shell.bottom.bottomSearchProgress.value, shell.bottom.bottomUnderlayAlpha.value,
                shell.bottom.bottomBlurProgress.value,
        )) {
            // 底栏玻璃的采样源：这一层就是整个页面内容。
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .layerBackdrop(backdrop)
                    // COUI 底栏的采样源就是这一层「整页内容」；底栏本身必须画在它外面，
                    // 否则底栏会采样到自己（自我录制）导致黑块甚至递归崩溃。
                    .then(
                        if (couiBackdrop != null) Modifier.couiBackdrop(couiBackdrop) else Modifier,
                    ),
            ) {
                // 三个 tab：横向相连的 pager（可拖拽，切换是同一套非线性动画）
                // 外面包一层更大的触摸阈值，手指轻抖不会误翻页。
                // 分段只在 HyperOS 4 风格下可见；切回官方/经典时强制回到推荐，
                // 否则会停在一个没有入口可以离开的空页面上。
                MainTabPager(tabPagerState, homeFeed, dynamicFeed, session, settings, ui.dynamicRefreshTick,
                    shell.chrome.mineList,
                    // 三种界面风格都保留首页的推荐/直播/番剧分段。
                    homeSection = ui.homeSection,
                    homeInteractive = !ui.showSearch,
                    onHomeSectionChange = { ui.homeSection = it },
                    onHomeSectionPositionChange = { ui.homeSectionPosition.floatValue = it })

            }

            ScrollTopButton(
                settings,
                bottomObstruction = if (ui.showSearch || shell.bottom.showBottomSearch) 0.dp else
                TeakFloatingTabBarDefaults.CurrentHeight + when {
                    wideNavigation -> MainWideNavigationEdgePadding
                    settings.bottomBarRaise -> floatingBottomSpacing + TeakChromeDefaults.RaisedBottomBarExtra
                    else -> floatingBottomSpacing
                },
            )

            MainSearchLayer(settings, shell)
        }
        // 两个顶栏都挂在采集层之外：底栏搜索的一镜到底会把内容整体上移并模糊，
        // 顶栏只跟着缩放和淡出，不应该再跟着上下位移一次，否则会双重运动。
        MainHomeChrome(settings, shell, pageFloat)
        MainMineChrome(shell, pageFloat)
        MainFeedChrome(shell, pageFloat)

        if (couiBackdrop != null) {
            // COUI 风格：底栏按 Src patch 直接合成到窗口，自身不能套 graphicsLayer，
            // 且必须与上面的采样源同级（不能包进采样层里）。
            MainShellNavigation(settings, shell, dark, couiBackdrop)
        } else {
            Box(Modifier.fillMaxSize().graphicsLayer {
                    val progress = shell.bottom.bottomTabBarProgress.value
                    translationY = 96.dp.toPx() * progress
                    scaleX = 1f - 0.4f * progress
                    scaleY = scaleX
                    alpha = 1f - progress
            }) { MainShellNavigation(settings, shell, dark) }
        }
        MainBottomSearchLayer(settings, shell)
        // 换页时返回键回到"纯箭头"状态（pageKey / pageEnter 在上面声明）

    }
}
