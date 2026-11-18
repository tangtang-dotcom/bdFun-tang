package com.aritxonly.bdfun.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
internal fun androidx.compose.foundation.layout.BoxScope.GridCoverSelection(selectionEnabled: Boolean, selected: Boolean) {
            // 批量管理：右上角勾选圈
            if (selectionEnabled) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) COUITheme.colorScheme.primary else Color(0x66000000),
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.9f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (selected) {
                        AppGlyph(COUIIcons.Ok, size = 13.dp, tint = Color.White)
                    }
                }
            }
}
