package com.aritxonly.bdfun.ui

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.icons.AppGlyph
import com.aritxonly.teak.common.icons.LucideIcons
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.*
import io.github.suqi8.coui.kmp.icon.extended.*

/** 播放器设置行的小图标，统一 22dp 线性风格。 */

@Composable
fun AutoPlayNextGlyph(tint: Color = Color.White) {
    AppGlyph(LucideIcons.CircleFadingPlay, tint, size = 22.dp)
}

@Composable
fun LoopGlyph(tint: Color = Color.White) {
    AppGlyph(LucideIcons.Repeat2, tint, size = 22.dp)
}

@Composable
fun MirrorGlyph(tint: Color = Color.White) {
    AppGlyph(LucideIcons.MirrorRectangular, tint, size = 22.dp)
}

@Composable
fun BackgroundListenGlyph(tint: Color = Color.White) {
    AppGlyph(LucideIcons.BookHeadphonesFilled, tint, size = 22.dp)
}

@Composable
internal fun ResizeGlyph(tint: Color = Color.White) {
    AppGlyph(COUIIcons.ExpandMore, tint, size = 22.dp)
}

@Composable
internal fun ColorAssistGlyph(tint: Color = Color.White) {
    AppGlyph(LucideIcons.CirclePile, tint, size = 22.dp)
}

@Composable
fun SpeedGlyph(tint: Color = Color.White) {
    AppGlyph(LucideIcons.Gauge, tint, size = 22.dp)
}

@Composable
fun TimerGlyph(tint: Color = Color.White) {
    AppGlyph(LucideIcons.Timer, tint, size = 22.dp)
}

@Composable
fun SubtitleGlyph(tint: Color = Color.White) {
    AppGlyph(LucideIcons.Captions, tint, size = 22.dp)
}

@Composable
fun DanmakuSettingGlyph(tint: Color = Color.White) {
    AppGlyph(COUIIcons.Tune, tint, size = 22.dp)
}

@Composable
fun DislikeGlyph(tint: Color = Color.White, sizeDp: androidx.compose.ui.unit.Dp = 24.dp) {
    AppGlyph(LucideIcons.FaceSlightlyFrowning, tint, size = sizeDp)
}

@Composable
internal fun WatchLaterGlyph(tint: Color = Color.White, sizeDp: androidx.compose.ui.unit.Dp = 24.dp) {
    AppGlyph(LucideIcons.ClockFading, tint, size = sizeDp)
}

@Composable
internal fun CacheGlyphSmall(tint: Color = Color.White, sizeDp: androidx.compose.ui.unit.Dp = 24.dp) {
    AppGlyph(COUIIcons.Download, tint, size = sizeDp)
}

@Composable
fun PipGlyphSmall(tint: Color = Color.White, sizeDp: androidx.compose.ui.unit.Dp = 24.dp) {
    AppGlyph(LucideIcons.PictureInPicture2, tint, size = sizeDp)
}

@Composable
fun CastGlyphSmall(tint: Color = Color.White, sizeDp: androidx.compose.ui.unit.Dp = 24.dp) {
    AppGlyph(COUIIcons.ScreenMirroring, tint, size = sizeDp)
}
