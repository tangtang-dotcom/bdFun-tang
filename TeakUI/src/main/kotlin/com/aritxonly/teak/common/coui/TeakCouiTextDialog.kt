package com.aritxonly.teak.common.coui

import androidx.compose.runtime.Composable
import com.itos.coui.dialog.CouiDialogAction
import com.itos.coui.dialog.CouiDialogActionStyle
import com.itos.coui.dialog.CouiDialogDefaults
import com.itos.coui.dialog.Dialog
import com.itos.coui.theme.CouiTheme

/**
 * 文本型确认弹窗：完全由库的 [Dialog] 渲染。
 *
 * 卡片圆角（45dp）、遮罩、进出场动画、按钮聚光与按压反馈、45dp 视觉半径全部走库默认，
 * 这里只把宿主的标题/正文/按钮文案翻译成库的 action 模型。
 */
@Composable
fun TeakCouiTextDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    dismissText: String,
    onDismissClick: () -> Unit,
    confirmEnabled: Boolean = true,
    dismissEnabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
) {
    TeakCouiTheme(dark) {
        Dialog(
            onDismissRequest = onDismissRequest,
            confirmAction = CouiDialogAction(
                text = confirmText,
                onClick = onConfirm,
                enabled = confirmEnabled,
                style = CouiDialogActionStyle.Primary,
                colors = CouiDialogDefaults.primaryActionColors(CouiTheme.colorScheme.accent),
            ),
            dismissAction = CouiDialogAction(
                text = dismissText,
                onClick = onDismissClick,
                enabled = dismissEnabled,
            ),
            title = title,
            message = message,
            dark = dark,
            visible = show,
        )
    }
}
