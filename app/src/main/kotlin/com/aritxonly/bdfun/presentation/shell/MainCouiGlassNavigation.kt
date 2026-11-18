package com.aritxonly.bdfun

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aritxonly.teak.common.interaction.blockTouches
import com.aritxonly.teak.common.navigation.TeakFloatingTabItem
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.navigation.CouiNavigationItem
import com.itos.coui.navigation.NavigationBar
import com.itos.coui.theme.CouiColorScheme
import com.itos.coui.theme.CouiTheme
import com.itos.coui.icons.CouiIcons

/**
 * COUI（ColorOS 凝光）底栏：直接用 COUI-Kit 的 NavigationBar，
 * 也就是 BiliCO 用的那套——多层悬浮材质（overlay/color-dodge 混色 + 描边光晕 + 焦散阴影）
 * 加上触摸跟随形变（CouiTouchMotion）与按压弹簧（CouiPressMotion）。
 *
 * 为什么不用 TeakFloatingTabBar：那套是 Miuix 玻璃，视觉上不是 ColorOS。
 * 为什么单独一个文件：RuntimeShader 路径需要 API 34，低版本继续走原底栏。
 */
@RequiresApi(34)
@Composable
internal fun BoxScope.MainCouiGlassNavigation(
    destinations: List<Destination>,
    tabItems: List<TeakFloatingTabItem>,
    selectedKey: String,
    dark: Boolean,
    alpha: Float,
    covered: Boolean,
    backdrop: CouiBackdrop,
    onBarItem: (TeakFloatingTabItem) -> Unit,
    onSearch: () -> Unit,
) {
    if (destinations.isEmpty()) return
    val material = MaterialTheme.colorScheme
    // COUI 色板只有 12 个角色，这里从宿主 Material3 色板映射，保持 App 的主题色不变。
    val scheme = CouiColorScheme(
        accent = material.primary,
        onAccent = material.onPrimary,
        surface = material.surfaceContainer,
        container = material.surfaceContainer,
        onSurface = material.onSurface,
        onSurfaceVariant = material.onSurfaceVariant,
        outlineVariant = material.outlineVariant,
        background = material.background,
        card = material.surfaceContainer,
    )
    val selectedIndex = destinations.indexOfFirst { it.key == selectedKey }.coerceAtLeast(0)
    val items = destinations.map {
        val icon = when (it.key) {
            "home" -> CouiIcons.Filled.Grid
            "dynamics" -> CouiIcons.Filled.Album
            "mine" -> CouiIcons.Filled.UserCircle
            else -> it.icon
        }
        CouiNavigationItem(title = it.label, icon = icon, selectedIcon = icon)
    }
    CouiTheme(darkTheme = dark, colorScheme = scheme) {
        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .blockTouches(covered),
            backdrop = backdrop,
            items = items,
            selectedIndex = selectedIndex,
            rightIcon = CouiIcons.Outlined.Search,
            rightDescription = "搜索",
            rightVisible = true,
            onRightClick = onSearch,
            dark = dark,
            onSelection = { index -> tabItems.getOrNull(index)?.let(onBarItem) },
        )
    }
}

/** 当前系统是否支持 COUI 底栏（RuntimeShader 需要 API 34）。 */
internal fun couiGlassNavigationSupported(): Boolean = Build.VERSION.SDK_INT >= 34
