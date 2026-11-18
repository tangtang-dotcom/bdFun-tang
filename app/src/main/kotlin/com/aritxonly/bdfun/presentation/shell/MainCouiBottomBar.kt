package com.aritxonly.bdfun

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import com.aritxonly.teak.common.coui.TeakCouiBarItem
import com.aritxonly.teak.common.coui.TeakCouiBottomBar
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.effect.CouiFloatingRenderer

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
    renderer: CouiFloatingRenderer,
    onSelect: (String) -> Unit,
    onSearch: (() -> Unit)?,
) {
    if (destinations.isEmpty()) return
    val items = destinations.map { destination ->
        val glyph = couiTabGlyph(destination.key, destination.icon)
        TeakCouiBarItem(key = destination.key, label = destination.label, icon = glyph, selectedIcon = glyph)
    }
    // 绘制统一兜底：库内 AGSL/阴影若在个别 GPU 上抛错，只丢这一帧的绘制，不让整个 App 闪退。
    Box(
        Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .drawWithContent { runCatching { drawContent() } },
    ) {
        TeakCouiBottomBar(
            items = items,
            selectedKey = selectedKey,
            onSelect = onSelect,
            backdrop = backdrop,
            renderer = renderer,
            dark = dark,
            onSearch = onSearch,
        )
    }
}
