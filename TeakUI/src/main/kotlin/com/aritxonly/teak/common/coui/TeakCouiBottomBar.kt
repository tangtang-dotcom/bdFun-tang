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

/** COUI 底栏条目；[icon] 与 [selectedIcon] 都是 ImageVector，便于直接换成 CouiIcons 素材。 */
data class TeakCouiBarItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
)

/**
 * COUI（ColorOS 17 凝光视效）底栏。
 *
 * 直接调用 COUI-Kit 的 [NavigationBar]，材质链路完全由库承担：
 * 背景采样 → 模糊 → luminosity/overlay 混色 → 描边光晕 + 焦散 → 多层 SDF 阴影，
 * 动效由 CouiTouchMotion（跟手形变）与 CouiPressMotion（按压弹簧）驱动。
 *
 * 注意：调用方必须保证本组件所在子树**没有** graphicsLayer 祖先（alpha/scale 会触发离屏合成，
 * 使材质的 Src patch 拿不到窗口内容而画成黑块），并且 [backdrop] 挂在正文内容节点上。
 */
@RequiresApi(34)
@Composable
fun TeakCouiBottomBar(
    items: List<TeakCouiBarItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    backdrop: CouiBackdrop,
    modifier: Modifier = Modifier,
    dark: Boolean = false,
    onSearch: (() -> Unit)? = null,
) {
    if (items.isEmpty()) return
    val selectedIndex = items.indexOfFirst { it.key == selectedKey }.coerceAtLeast(0)
    CouiTheme(darkTheme = dark, colorScheme = teakCouiColorScheme(dark)) {
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

/** COUI 色板只有 12 个角色；从宿主 Material3 色板映射，保持 App 主题色不变。 */
@Composable
internal fun teakCouiColorScheme(dark: Boolean): CouiColorScheme {
    val material = MaterialTheme.colorScheme
    return CouiColorScheme(
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
}
