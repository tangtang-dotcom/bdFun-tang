package com.aritxonly.bdfun

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aritxonly.teak.common.coui.TeakCouiBarItem
import com.aritxonly.teak.common.coui.TeakCouiBottomBar
import com.itos.coui.backdrop.CouiBackdrop

/**
 * COUI 风格底栏。
 *
 * 布局、材质、动效全部由 COUI-Kit 的 NavigationBar 提供；这里只负责把 bdFun 的
 * Destination 翻译成库需要的 item（图标换 COUI 图标集）。
 */
@RequiresApi(34)
@Composable
internal fun BoxScope.MainCouiBottomBar(
    destinations: List<Destination>,
    selectedKey: String,
    dark: Boolean,
    backdrop: CouiBackdrop,
    onSelect: (String) -> Unit,
    onSearch: (() -> Unit)?,
) {
    if (destinations.isEmpty()) return
    val items = destinations.map { destination ->
        val glyph = couiTabGlyph(destination.key, destination.icon)
        TeakCouiBarItem(key = destination.key, label = destination.label, icon = glyph, selectedIcon = glyph)
    }
    TeakCouiBottomBar(
        items = items,
        selectedKey = selectedKey,
        onSelect = onSelect,
        backdrop = backdrop,
        modifier = Modifier.align(Alignment.BottomCenter),
        dark = dark,
        onSearch = onSearch,
    )
}
