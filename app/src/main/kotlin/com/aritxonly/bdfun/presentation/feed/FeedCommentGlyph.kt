package com.aritxonly.bdfun.ui

import com.aritxonly.teak.common.coui.TeakCouiIcons
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.aritxonly.teak.common.icons.AppGlyph
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.*
import top.yukonga.miuix.kmp.icon.extended.*

@Composable
internal fun CommentGlyph(tint: Color, size: Dp) {
    AppGlyph(TeakCouiIcons.Reply, tint, size = size)
}
