package com.aritxonly.bdfun.ui

import androidx.compose.runtime.*
import androidx.compose.material3.Text
import com.aritxonly.bdfun.feature.settings.application.SettingsBrowserStateHolder
import com.aritxonly.teak.common.modal.AlertDialog
import com.aritxonly.teak.common.modal.TextButton

@Composable
internal fun SettingsLogoutDialog(browser: SettingsBrowserStateHolder, onLogout: () -> Unit) {
    val state by browser.state.collectAsState()
    if (com.aritxonly.teak.common.theme.TeakUiStyle.isCoui) {
        com.aritxonly.teak.common.coui.TeakCouiTextDialog(
            show = state.confirmLogout,
            onDismissRequest = browser::cancelLogout,
            title = "退出登录？",
            message = "确认退出当前账号？",
            confirmText = "退出登录",
            onConfirm = {
                if (browser.state.value.confirmLogout) {
                    browser.cancelLogout()
                    onLogout()
                }
            },
            dismissText = "取消",
            onDismissClick = browser::cancelLogout,
        )
        return
    }
    AlertDialog(show = state.confirmLogout, onDismissRequest = browser::cancelLogout,
        miuixTitle = "退出登录？", miuixSummary = "确认退出当前账号？",
        confirmButton = {
            TextButton(onClick = {
                if (browser.state.value.confirmLogout) {
                    browser.cancelLogout()
                    onLogout()
                }
            }, miuixText = "退出登录", destructive = true) { Text("退出登录") }
        }, dismissButton = {
            TextButton(onClick = browser::cancelLogout, miuixText = "取消") { Text("取消") }
        })
}
