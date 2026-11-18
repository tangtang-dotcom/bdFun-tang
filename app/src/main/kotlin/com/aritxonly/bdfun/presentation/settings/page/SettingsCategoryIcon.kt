package com.aritxonly.bdfun.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.aritxonly.teak.common.icons.AppGlyph
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aritxonly.bdfun.core.model.settings.SettingsEntry
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.*

@Composable
internal fun SettingsCategoryIcon(entry: SettingsEntry) {
    val icon = when (entry.icon) {
        "Background" -> COUIIcons.Background
        "Play" -> COUIIcons.Play
        "Messages" -> COUIIcons.Messages
        "Home" -> COUIIcons.Home
        "ContactsCircle" -> COUIIcons.ContactsBook
        "Info" -> COUIIcons.Info
        else -> COUIIcons.Settings
    }
    Box(Modifier.padding(end = 16.dp).size(32.dp)
        .background(Color(0xFF000000 or entry.color.removePrefix("#").toLong(16)), RoundedCornerShape(9.dp)),
        contentAlignment = Alignment.Center) {
        AppGlyph(icon, contentDescription = null, tint = Color.White, size = 23.dp)
    }
}
