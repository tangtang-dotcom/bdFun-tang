package com.aritxonly.bdfun

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.icons.AppGlyph
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.*
import io.github.suqi8.coui.kmp.icon.extended.*
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
internal fun androidx.compose.foundation.layout.BoxScope.SpaceSignToggle(ui: SpaceSignStateHolder) {
    val colors = COUITheme.colorScheme
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .size(18.dp)
            .clip(CircleShape)
            .clickable(interactionSource = null, indication = null) {
                ui.expanded = !ui.expanded
            },
        contentAlignment = Alignment.Center,
    ) {
        AppGlyph(COUIIcons.Basic.ArrowUpDown, colors.onBackgroundVariant, size = 12.dp, modifier = Modifier.graphicsLayer { rotationZ = if (ui.expanded) 180f else 0f })
    }
}
