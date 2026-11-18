package com.aritxonly.teak.common.coui

import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.icons.CouiIcons
import com.itos.coui.navigation.CouiNavigationItem
import com.itos.coui.navigation.NavigationBar
import com.itos.coui.theme.CouiColorScheme
import com.itos.coui.theme.CouiTheme

/** 底栏一项：钥匙 + 文案 + 两个状态的图标。 */
data class TeakCouiBarItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
)

/**
 * COUI 底栏：直接使用 COUI-Kit 的 [NavigationBar]。
 *
 * 材质（两段混色 + 边缘光 + 内发光 + 焦散 + 柔和阴影）、动效（触摸跟手形变、按压、
 * 选中融球迁移）全部来自库本身，这里只做「宿主数据 -> 库数据结构」的翻译。
 * 注意：这个组件的绘制走 Src patch 直接合成到窗口，**祖先不能有 graphicsLayer**，
 * 否则会离屏合成导致采样为空、底栏变黑块。
 */
@RequiresApi(34)
@Composable
fun TeakCouiBottomBar(
    items: List<TeakCouiBarItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    backdrop: CouiBackdrop,
    modifier: Modifier = Modifier,
    dark: Boolean = CouiTheme.isDark,
    onSearch: (() -> Unit)? = null,
) {
    if (items.isEmpty()) return
    val selectedIndex = items.indexOfFirst { it.key == selectedKey }.coerceAtLeast(0)
    CouiTheme(darkTheme = dark, colorScheme = teakCouiColorScheme()) {
        NavigationBar(
            modifier = modifier,
            backdrop = backdrop,
            items = items.map { CouiNavigationItem(it.label, it.icon, it.selectedIcon) },
            selectedIndex = selectedIndex,
            rightIcon = if (onSearch != null) CouiIcons.Outlined.Search else null,
            rightDescription = if (onSearch != null) "搜索" else null,
            rightVisible = onSearch != null,
            onRightClick = onSearch,
            dark = dark,
            onSelection = { index -> items.getOrNull(index)?.let { onSelect(it.key) } },
        )
    }
}

/** 把宿主的 Material3 色板折成 COUI 的 12 个角色（COUI 默认是官方蓝，必须映射）。 */
@Composable
internal fun teakCouiColorScheme(): CouiColorScheme {
    val m = MaterialTheme.colorScheme
    return CouiColorScheme(
        accent = m.primary,
        onAccent = m.onPrimary,
        surface = m.background,
        container = m.surfaceContainer,
        onSurface = m.onSurface,
        onSurfaceVariant = m.onSurfaceVariant,
        outlineVariant = m.outlineVariant,
        accentOnSurface = m.primary,
        menuSurface = m.surfaceContainer,
        checkboxAccent = m.primary,
        menuCheckboxAccent = m.onPrimary,
        background = m.background,
        card = m.surfaceContainer,
    )
}
