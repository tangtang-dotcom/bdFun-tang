package com.aritxonly.bdfun.ui

import com.aritxonly.teak.common.coui.TeakCouiIcons
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.chrome.teakGlassGlyphColor
import com.aritxonly.teak.common.icons.AppGlyph
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.*
import top.yukonga.miuix.kmp.icon.extended.*

/* ---------------------------------------------------------------- glyphs */

/** 快捷入口三个图标统一按这个尺寸绘制，视觉大小保持一致。 */
internal val ShortcutGlyphSize = 28.dp

@Composable
internal fun MarkReadGlyph() {
    AppGlyph(TeakCouiIcons.Community, teakGlassGlyphColor(), size = 21.dp)
}

@Composable
internal fun ThreeDotsGlyph() {
    AppGlyph(TeakCouiIcons.More, teakGlassGlyphColor(), size = 21.dp)
}
