package com.aritxonly.teak.common.coui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.itos.coui.theme.CouiTheme

// COUI 官方弹窗的进场曲线与时长（同 CouiDialogDefaults，那里是 internal，这里复刻一份）。
private val CouiEnterEasing = CubicBezierEasing(.3f, 0f, .1f, 1f)
private const val CouiEnterDuration = 250

/**
 * COUI 版 AlertDialog：签名与 TeakUI 原 AlertDialog 完全一致，调用点零改动，
 * 只把 Miuix 弹窗换成 COUI 的材质（45dp 圆角 + 容器色）与进场动效（缩放进 + 淡入）。
 */
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
    val scheme = CouiTheme.colorScheme
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) { appear.animateTo(1f, tween(CouiEnterDuration, easing = CouiEnterEasing)) }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = modifier
                    .widthIn(max = 392.dp)
                    .fillMaxWidth()
                    .graphicsLayer {
                        val p = appear.value
                        alpha = p
                        scaleX = 0.92f + 0.08f * p
                        scaleY = 0.92f + 0.08f * p
                    }
                    .clip(RoundedCornerShape(28.dp))
                    .background(scheme.container)
                    .padding(horizontal = 22.dp, vertical = 20.dp),
            ) {
                if (title != null) title()
                if (title != null && text != null) Spacer(Modifier.height(10.dp))
                if (text != null) text()
                Spacer(Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (dismissButton != null) {
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { dismissButton() }
                    }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { confirmButton() }
                }
            }
        }
    }
}

/**
 * COUI 版底部操作面板：COUI 没有独立的 BottomSheet 组件，用底部贴边的圆角容器
 * 复刻官方「操作弹层」的观感（上圆角 + 容器色 + 从下淡入上移），
 * 对外签名沿用 TeakActionSheet，调用点无需修改。
 */
@Composable
fun TeakCouiActionSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String? = null,
    allowDismiss: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visible) return
    val scheme = CouiTheme.colorScheme
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) { appear.animateTo(1f, tween(CouiEnterDuration, easing = CouiEnterEasing)) }
    Dialog(
        onDismissRequest = { if (allowDismiss) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            // 遮罩：点空白关闭，和 Miuix 面板行为一致。
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = appear.value * 0.32f }
                    .background(androidx.compose.ui.graphics.Color.Black)
                    .then(if (allowDismiss) Modifier.clickable(onClick = onDismiss) else Modifier),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val p = appear.value
                        alpha = p
                        translationY = (1f - p) * 64f * density
                    }
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(scheme.container)
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                if (title != null) {
                    Text(text = title, color = scheme.onSurface, modifier = Modifier.padding(bottom = 8.dp))
                    Spacer(Modifier.height(4.dp))
                }
                content()
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
