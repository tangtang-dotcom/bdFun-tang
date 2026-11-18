package com.aritxonly.bdfun.ui

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.chrome.teakGlassGlyphColor
import com.aritxonly.teak.common.icons.AppGlyph
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.*
import io.github.suqi8.coui.kmp.icon.extended.*

/* ---------------------------------------------------------------- glyphs */

/** 快捷入口三个图标统一按这个尺寸绘制，视觉大小保持一致。 */
internal val ShortcutGlyphSize = 28.dp

@Composable
internal fun MarkReadGlyph() {
    AppGlyph(COUIIcons.Community, teakGlassGlyphColor(), size = 21.dp)
}

@Composable
internal fun ThreeDotsGlyph() {
    AppGlyph(COUIIcons.More, teakGlassGlyphColor(), size = 21.dp)
}
