package com.aritxonly.bdfun.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aritxonly.bdfun.core.model.comment.CommentItem
import com.aritxonly.teak.common.icons.AppGlyph
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.*
import io.github.suqi8.coui.kmp.icon.extended.*
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
internal fun CommentPreviewOpenLink(item: CommentItem, onOpen: () -> Unit) {
    val colors = COUITheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(interactionSource = null, indication = null, onClick = onOpen)
            .padding(top = 5.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "共${item.replyCount}条回复",
            color = colors.onBackgroundVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
        )
        Spacer(Modifier.width(2.dp))
        AppGlyph(COUIIcons.Basic.ArrowRight, size = 11.dp, tint = colors.onBackgroundVariant)
    }
}
