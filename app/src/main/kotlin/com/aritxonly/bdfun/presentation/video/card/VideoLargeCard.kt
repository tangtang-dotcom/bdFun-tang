package com.aritxonly.bdfun.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aritxonly.bdfun.core.model.video.VideoItem
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
internal fun VideoLargeCard(
    video: VideoItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    onLongClick: (() -> Unit)? = null,
) {
    val colors = COUITheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ContinuousCornerShape(14.dp))
            .background(colors.surfaceContainer)
            .combinedClickable(interactionSource = null, indication = null, onClick = onClick, onLongClick = onLongClick),
    ) {
        LargeVideoCover(video, progress)
        LargeVideoContent(video)
    }
}
