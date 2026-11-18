package com.aritxonly.teak.common.coui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * COUI（ColorOS）弹窗：居中悬浮卡片。
 *
 * 动效参数照抄 COUI-Kit `CouiDialogWindow`：进入 250ms、退出 180ms，
 * 曲线 cubic-bezier(0.30, 0, 0.10, 1)，配合 0.92 -> 1 的缩放和透明度淡入。
 */
private val CouiDialogEnterEasing = CubicBezierEasing(0.30f, 0f, 0.10f, 1f)
private const val CouiDialogEnterMs = 250
private const val CouiDialogExitMs = 180

@Composable
fun TeakCouiAlertDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
) {
    if (!show) return
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(CouiDialogEnterMs, easing = CouiDialogEnterEasing))
    }
    DifferentialDialog(onDismissRequest, modifier) {
        val p = progress.value
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.32f * p))
                .clickable(onClick = onDismissRequest),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 392.dp)
                    .padding(horizontal = 24.dp)
                    .graphicsLayer {
                        alpha = p
                        scaleX = 0.92f + 0.08f * p
                        scaleY = 0.92f + 0.08f * p
                    }
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = com.itos.coui.dialog.CouiDialogDefaults.shape,
                    )
                    .padding(horizontal = 22.dp, vertical = 22.dp),
            ) {
                if (title != null) {
                    title()
                    Spacer(Modifier.height(12.dp))
                }
                if (text != null) {
                    text()
                    Spacer(Modifier.height(22.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (dismissButton != null) {
                        Box(Modifier.weight(1f)) { dismissButton() }
                    }
                    Box(Modifier.weight(1f)) { confirmButton() }
                }
            }
        }
    }
}

@Composable
private fun DifferentialDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) { content() }
}

/** COUI 弹窗标题排印：17sp / SemiBold。 */
@Composable
fun TeakCouiDialogTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
}

/** COUI 弹窗正文排印：14sp / 65% 不透明。 */
@Composable
fun TeakCouiDialogText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
