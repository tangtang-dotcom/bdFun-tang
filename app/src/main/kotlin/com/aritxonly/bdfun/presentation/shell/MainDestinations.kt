package com.aritxonly.bdfun

import com.aritxonly.teak.common.coui.TeakCouiIcons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ContactsCircle
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.icon.extended.Album

internal data class Destination(
    val key: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
internal fun rememberMainDestinations(): List<Destination> {
    // 图标是 @Composable getter，必须先取值再进 remember，否则不能再在普通 lambda 里读
    val homeIcon = TeakCouiIcons.Home
    val albumIcon = TeakCouiIcons.Album
    val mineIcon = TeakCouiIcons.ContactsCircle
    return remember(homeIcon, albumIcon, mineIcon) {
        listOf(
            Destination("home", "首页", homeIcon),
            Destination("dynamics", "动态", albumIcon),
            Destination("mine", "我的", mineIcon),
        )
    }
}
