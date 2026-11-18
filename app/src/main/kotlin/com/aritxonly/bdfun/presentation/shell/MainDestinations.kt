package com.aritxonly.bdfun

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.ContactsCircle
import io.github.suqi8.coui.kmp.icon.extended.Home
import io.github.suqi8.coui.kmp.icon.extended.Album

internal data class Destination(
    val key: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
internal fun rememberMainDestinations(): List<Destination> {
    return remember {
        listOf(
            Destination("home", "首页", COUIIcons.Home),
            Destination("dynamics", "动态", COUIIcons.Album),
            Destination("mine", "我的", COUIIcons.ContactsCircle),
        )
    }
}
