package com.aritxonly.bdfun

import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.coui.TeakCouiBarItem
import com.aritxonly.teak.common.coui.TeakCouiBottomBar
import com.aritxonly.teak.common.interaction.blockTouches
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.icons.CouiIcons

/**
 * COUI 风格的底栏装配：只做「Destination → CouiIcons 条目」的映射，
 * 材质与动效全部交给 TeakUI 的 [TeakCouiBottomBar]（也就是 COUI-Kit 自己的 NavigationBar）。
 *
 * COUI-Kit 的 Filled 集没有 Home 图标，首页用 Grid（九宫格）代替；
 * 需要严格房形图标时在 third_party/coui-icons 里按 24dp 规格补一枚即可。
 */
@RequiresApi(34)
@Composable
internal fun androidx.compose.foundation.layout.BoxScope.MainCouiBottomBar(
    destinations: List<Destination>,
    selectedKey: String,
    dark: Boolean,
    backdrop: CouiBackdrop,
    covered: Boolean,
    onSelect: (String) -> Unit,
    onSearch: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val items = destinations.map { destination ->
        val icon = when (destination.key) {
            "home" -> CouiIcons.Filled.Grid
            "dynamics" -> CouiIcons.Filled.Album
            "mine" -> CouiIcons.Filled.UserCircle
            else -> destination.icon
        }
        TeakCouiBarItem(key = destination.key, label = destination.label, icon = icon)
    }
    TeakCouiBottomBar(
        items = items,
        selectedKey = selectedKey,
        onSelect = onSelect,
        backdrop = backdrop,
        modifier = Modifier
            .align(androidx.compose.ui.Alignment.BottomCenter)
            .navigationBarsPadding()
            .padding(start = 12.dp, end = 12.dp, bottom = 6.dp)
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .then(modifier)
            .then(if (covered) Modifier.blockTouches(true) else Modifier),
        dark = dark,
        onSearch = onSearch,
    )
}
