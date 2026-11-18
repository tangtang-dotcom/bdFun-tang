// Migrated from Deadliner 3b561656 working tree; see docs/deadliner-modal-migration.md.
package com.aritxonly.teak.common.modal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.aritxonly.teak.common.coui.TeakCouiAlertDialog
import com.aritxonly.teak.common.theme.TeakUiStyle

/**
 * Deadliner 基础 AlertDialog 组件
 * 使用 MIUIX 的 state-based 显示机制。
 */
@Composable
fun AlertDialog(
    // 💥 核心修改：强制要求传入 show 状态，用来兼容 MIUIX 的动画生命周期
    show: Boolean,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    renderTextContentInMiuix: Boolean = false,

    // ==========================================
    // MIUIX 专属辅助参数
    // ==========================================
    miuixTitle: String? = null,
    miuixSummary: String? = null,
    miuixMaxWidth: Dp = TeakModalDefaults.DialogMaxWidth,
) {
    // COUI 风格：整只弹窗换成 COUI 的材质与进场动效，调用方参数完全不用改。
    if (TeakUiStyle.isCoui) {
        // Miuix 的纯文本参数在这里补成 Composable 槽，保持老调用点能显示标题/摘要。
        val couiTitle: (@Composable () -> Unit)? = title ?: miuixTitle?.let { t -> { Text(t) } }
        val couiText: (@Composable () -> Unit)? = text ?: miuixSummary?.let { m -> { Text(m) } }
        TeakCouiAlertDialog(
            show = show,
            onDismissRequest = onDismissRequest,
            confirmButton = confirmButton,
            modifier = modifier,
            dismissButton = dismissButton,
            title = couiTitle,
            text = couiText,
        )
        return
    }
    val showInlineTitle = title != null && miuixTitle == null
    val showInlineText = text != null && (renderTextContentInMiuix || miuixSummary == null)
    val showInlineContent = showInlineTitle || showInlineText
    val hasTextAboveActions = showInlineContent || miuixTitle != null || miuixSummary != null
    MiuixDialog(
        show = show,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = miuixTitle,
        summary = miuixSummary,
        maxWidth = miuixMaxWidth,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (showInlineTitle) title?.invoke()
            if (showInlineText) text?.invoke()
            MiuixDialogActions(
                modifier = Modifier.fillMaxWidth().padding(
                    top = if (hasTextAboveActions) TeakModalDefaults.DialogActionTopSpacing else 0.dp,
                ),
                secondaryButton = dismissButton,
                primaryButton = confirmButton,
            )
        }
    }
}

/** Equal-width, filled action buttons used by Miuix dialog content. */
@Composable
internal fun MiuixDialogActions(
    primaryButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    secondaryButton: @Composable (() -> Unit)? = null,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        if (secondaryButton != null) {
            MiuixDialogActionSlot(
                style = MiuixDialogActionStyle.Secondary,
                content = secondaryButton,
            )
            Spacer(modifier = Modifier.width(TeakModalDefaults.DialogActionSpacing))
        }
        MiuixDialogActionSlot(
            style = MiuixDialogActionStyle.Primary,
            content = primaryButton,
        )
    }
}

@Composable
private fun RowScope.MiuixDialogActionSlot(
    style: MiuixDialogActionStyle,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalMiuixDialogActionStyle provides style,
            content = content,
        )
    }
}
