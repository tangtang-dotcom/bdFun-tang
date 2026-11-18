package com.aritxonly.bdfun

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.coui.TeakCouiGlyphHome
import com.aritxonly.teak.common.coui.TeakCouiTabBar
import com.aritxonly.teak.common.coui.TeakCouiTabItem
import com.aritxonly.teak.common.coui.teakCouiAlbum
import com.aritxonly.teak.common.coui.teakCouiUser
import com.aritxonly.teak.common.interaction.blockTouches
import com.itos.coui.backdrop.CouiBackdrop

/**
 * COUI 底栏装配。图标直接取 COUI 矢量（Home 用自绘补齐），
 * 尺寸沿用 HyperOS 4 的悬浮胶囊宽度约束，保证和 Hyper 风格切换时不跳变。
 */
@Composable
internal fun BoxScope.MainCouiNavigation(
    destinations: List<Destination>,
    selectedKey: String,
    visual: MainBottomNavigationVisual,
    dark: Boolean,
    couiBackdrop: CouiBackdrop?,
    onSelect: (String) -> Unit,
) {
    // 没有 backdrop（理论上只在非 COUI 风格发生）时直接不画，避免 COUI 的 checkNotNull 崩溃。
    val backdrop = couiBackdrop ?: return
    val items = destinations.map { d ->
        val icon = couiGlyphFor(d)
        TeakCouiTabItem(key = d.key, label = d.label, icon = icon, selectedIcon = icon)
    }
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .graphicsLayer { alpha = visual.tabBarAlpha }
            .blockTouches(visual.tabBarCovered)
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        TeakCouiTabBar(
            items = items,
            selectedKey = selectedKey,
            onItemSelected = { onSelect(it.key) },
            backdrop = backdrop,
            modifier = Modifier.wrapContentWidth().widthIn(max = 320.dp).fillMaxWidth(),
            dark = dark,
        )
    }
}

@Composable
private fun couiGlyphFor(d: Destination): ImageVector = when (d.key) {
    "home" -> TeakCouiGlyphHome
    "dynamics" -> teakCouiAlbum()
    "mine" -> teakCouiUser()
    else -> d.icon
}
