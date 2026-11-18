// Migrated from Deadliner 3b561656 working tree; see docs/deadliner-modal-migration.md.
package com.aritxonly.teak.common.modal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Deadliner 基础 AlertDialog 组件。
 *
 * COUI 风格改造：不再复用 COUIDialog 的默认 28dp 圆角与小尺寸，
 * 改为 ColorOS 17 的悬浮卡片形态 —— 32dp 圆角、392dp 上限宽、32% 遮罩、
 * 260ms「缩小淡入 / 还原淡出」曲线（cubic-bezier(0.3, 0, 0.1, 1)）。
 * 进出场由本地 Animatable 驱动，所以关闭时仍保留退场动画。
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
    shape: Shape = RoundedCornerShape(TeakModalDefaults.DialogCornerRadius),
    containerColor: Color = AlertDialogDefaults.containerColor,
    renderTextContentInMiuix: Boolean = false,

    // ==========================================
    // 兼容旧调用的文本参数（现在直接渲染成标题/正文）
    // ==========================================
    miuixTitle: String? = null,
    miuixSummary: String? = null,
    miuixMaxWidth: Dp = TeakModalDefaults.DialogMaxWidth,
) {
    var retained by remember { mutableStateOf(show) }
    if (show) retained = true
    // 0f = 完全展开；1f = 完全收起
    val progress = remember { Animatable(if (show) 0f else 1f) }
    LaunchedEffect(show) {
        if (show) {
            progress.animateTo(0f, tween(CouiDialogEnterMillis, easing = CouiDialogEase))
        } else {
            progress.animateTo(1f, tween(CouiDialogExitMillis, easing = CouiDialogExitEase))
            retained = false
        }
    }
    if (!retained) return

    val visibleProgress = 1f - progress.value
    val showInlineTitle = title != null || miuixTitle != null
    val showInlineText = text != null || miuixSummary != null
    val hasTextAboveActions = showInlineTitle || showInlineText

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = CouiDialogScrimAlpha * visibleProgress))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = modifier
                    .padding(horizontal = 24.dp)
                    .widthIn(max = miuixMaxWidth)
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = visibleProgress
                        val scale = 0.92f + 0.08f * visibleProgress
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(shape)
                    .background(containerColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 22.dp),
                ) {
                    if (showInlineTitle) {
                        if (title != null) title() else TitleText(miuixTitle!!)
                    }
                    if (showInlineText) {
                        if (text != null) text() else SummaryText(miuixSummary!!)
                    }
                    COUIDialogActions(
                        modifier = Modifier.fillMaxWidth().padding(
                            top = if (hasTextAboveActions) TeakModalDefaults.DialogActionTopSpacing else 0.dp,
                        ),
                        secondaryButton = dismissButton,
                        primaryButton = confirmButton,
                    )
                }
            }
        }
    }
}

@Composable
private fun TitleText(text: String) {
    androidx.compose.material3.Text(
        text = text,
        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    )
}

@Composable
private fun SummaryText(text: String) {
    androidx.compose.material3.Text(
        text = text,
        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth(),
    )
}

/** Equal-width, filled action buttons used by Miuix dialog content. */
@Composable
internal fun COUIDialogActions(
    primaryButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    secondaryButton: (@Composable () -> Unit)? = null,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        if (secondaryButton != null) {
            COUIDialogActionSlot(
                style = COUIDialogActionStyle.Secondary,
                content = secondaryButton,
            )
            Spacer(modifier = Modifier.width(TeakModalDefaults.DialogActionSpacing))
        }
        COUIDialogActionSlot(
            style = COUIDialogActionStyle.Primary,
            content = primaryButton,
        )
    }
}

@Composable
private fun RowScope.COUIDialogActionSlot(
    style: COUIDialogActionStyle,
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

/** COUI 弹窗标准动效参数：250~260ms、cubic-bezier(0.3, 0, 0.1, 1)。 */
private const val CouiDialogEnterMillis = 260
private const val CouiDialogExitMillis = 180
private const val CouiDialogScrimAlpha = 0.32f
private val CouiDialogEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
private val CouiDialogExitEase = CubicBezierEasing(0.4f, 0f, 1f, 1f)
