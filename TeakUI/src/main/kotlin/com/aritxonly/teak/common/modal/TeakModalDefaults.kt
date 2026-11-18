// Migrated from Deadliner 3b561656 working tree; see docs/deadliner-modal-migration.md.
package com.aritxonly.teak.common.modal

import androidx.compose.ui.unit.dp

internal object TeakModalDefaults {
    // 对齐 COUI（ColorOS 17）的大圆角与卡片宽度：弹窗 32dp / 按钮 24dp / 输入 22dp / 上限 392dp
    val ButtonCornerRadius = 24.dp
    val InputCornerRadius = 22.dp
    val FormInputCornerRadius = 26.dp
    val DialogMaxWidth = 392.dp
    val DialogCornerRadius = 32.dp
    val DialogActionSpacing = 12.dp
    val DialogActionTopSpacing = 24.dp
}
