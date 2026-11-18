package com.aritxonly.teak.common.coui

import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.icons.CouiIcons
import com.itos.coui.navigation.CouiNavigationItem
import com.itos.coui.navigation.NavigationBar
import com.itos.coui.theme.CouiColorScheme
import com.itos.coui.theme.CouiTheme

/** 底栏一项：key + 文案 + 两态图标。 */
data class TeakCouiBarItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
)

/**
 * COUI 风格底栏 —— 直接使用 COUI-Kit 的 [NavigationBar]。
 *
 * 布局（16dp 悬浮留白 + 胶囊 + 左右附件）、材质（AGSL 凝光着色器）、阴影、融球动效、
 * 跟手形变、点击反馈全部由库实现，这里只做 bdFun → 库的数据翻译。
 *
 * 库的材质 patch 默认用 `BlendMode.Src` 把矩形区域重画一遍；在非 OPPO 校准机型上会擦成
 * 透明并露出窗口黑底（用户看到的「黑条」）。所以调库时打开 `foregroundComposition = true`，
 * 让它用 SrcOver 只叠前景（见 third_party/coui/.../NavigationBar.kt 的补丁说明）。
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
            items = items.map { CouiNavigationItem(title = it.label, icon = it.icon, selectedIcon = it.selectedIcon) },
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
