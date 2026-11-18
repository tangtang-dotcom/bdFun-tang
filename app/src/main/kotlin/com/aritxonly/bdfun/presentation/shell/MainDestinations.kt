package com.aritxonly.bdfun

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import top.yukonga.miuix.kmp.icon.MiuixIcons
import com.aritxonly.teak.common.icons.ContactsCircle
import com.aritxonly.teak.common.icons.Home
import com.aritxonly.teak.common.icons.Album

internal data class Destination(
    val key: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
internal fun rememberMainDestinations(): List<Destination> {
    val homeIcon = MiuixIcons.Home
    val albumIcon = MiuixIcons.Album
    val mineIcon = MiuixIcons.ContactsCircle
    return remember(homeIcon, albumIcon, mineIcon) {
        listOf(
            Destination("home", "首页", homeIcon),
            Destination("dynamics", "动态", albumIcon),
            Destination("mine", "我的", mineIcon),
        )
    }
}
