package com.aritxonly.bdfun.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
internal fun MineSettingsCard(onSettings: () -> Unit) {
    val colors = COUITheme.colorScheme
    val haptics = rememberTeakHaptics()
    Card(modifier = Modifier.fillMaxWidth(), cornerRadius = 22.dp,
        colors = CardDefaults.defaultColors(color = colors.surfaceContainer, contentColor = colors.onSurfaceContainer)) {
        ArrowPreference(title = "设置", onClick = { haptics.tap(); onSettings() },
            startAction = {
                Icon(COUIIcons.Settings, contentDescription = null,
                    tint = COUITheme.colorScheme.onSurfaceContainer, modifier = Modifier.size(22.dp))
            })
    }
}
