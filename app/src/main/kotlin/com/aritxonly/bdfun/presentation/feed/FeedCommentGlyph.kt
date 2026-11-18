package com.aritxonly.bdfun.ui

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.aritxonly.teak.common.icons.AppGlyph
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.*
import io.github.suqi8.coui.kmp.icon.extended.*

@Composable
internal fun CommentGlyph(tint: Color, size: Dp) {
    AppGlyph(COUIIcons.Reply, tint, size = size)
}
