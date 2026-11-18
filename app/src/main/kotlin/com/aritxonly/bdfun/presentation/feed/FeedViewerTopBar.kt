package com.aritxonly.bdfun.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.icons.AppGlyph
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.*
import io.github.suqi8.coui.kmp.icon.extended.*

@Composable
internal fun FeedViewerTopBar(dismissAnimated: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppGlyph(COUIIcons.Close, size = 26.dp, tint = Color.White,

            modifier = Modifier
                .clip(CircleShape)
                .clickable(interactionSource = null, indication = null, onClick = dismissAnimated)
                .padding(6.dp))
        Spacer(Modifier.weight(1f))
        AppGlyph(COUIIcons.More, size = 22.dp, tint = Color.White,

            modifier = Modifier.padding(6.dp))
    }
}
