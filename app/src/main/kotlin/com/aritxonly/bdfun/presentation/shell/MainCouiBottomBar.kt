package com.aritxonly.bdfun

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import com.aritxonly.teak.common.coui.TeakCouiBarItem
import com.aritxonly.teak.common.coui.TeakCouiBottomBar
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.effect.CouiFloatingRenderer

/**
 * COUI 风格底栏。布局/材质/动效全部来自 COUI-Kit 的 NavigationBar，
 * 这里只把 bdFun 的 Destination 翻成库的 item（图标换 COUI 图标集）。
 */
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
    // 与 demo 一致：绘制器先建、失败就别用库底栏（不让首次绘制抛异常闪退）。
    val renderer = remember { runCatching { CouiFloatingRenderer.standalone() }.getOrNull() } ?: return
    val items = destinations.map { destination ->
        val glyph = couiTabGlyph(destination.key, destination.icon)
        TeakCouiBarItem(key = destination.key, label = destination.label, icon = glyph, selectedIcon = glyph)
    }
    // 绘制兜底：库内 AGSL 若在个别 GPU 上抛错，只丢这一帧绘制，不让整个 App 闪退。
    // 与 SleepDown 一致：底栏本体固定 CouiDockDefaults.menuWidth，不铺满屏宽。
    Box(
        Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 8.dp)
            .drawWithContent { runCatching { drawContent() } },
        contentAlignment = Alignment.BottomCenter,
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(com.itos.coui.navigation.CouiDockDefaults.plusGap),
        ) {
            TeakCouiBottomBar(
                items = items,
                selectedKey = selectedKey,
                onSelect = onSelect,
                backdrop = backdrop,
                renderer = renderer,
                dark = dark,
                modifier = Modifier.width(com.itos.coui.navigation.CouiDockDefaults.menuWidth),
            )
            if (onSearch != null) {
                com.itos.coui.floatingactionbutton.Fab(
                    backdrop = backdrop,
                    onClick = onSearch,
                    dark = dark,
                ) {
                    androidx.compose.material3.Icon(
                        com.itos.coui.icons.CouiIcons.Search,
                        contentDescription = "搜索",
                    )
                }
            }
        }
    }
}
