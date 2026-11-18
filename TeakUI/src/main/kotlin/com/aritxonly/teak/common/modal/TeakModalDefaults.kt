// Migrated from Deadliner 3b561656 working tree; see docs/deadliner-modal-migration.md.
package com.aritxonly.teak.common.modal

import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.theme.TeakUiStyle

internal object TeakModalDefaults {
    /** COUI（ColorOS）的按钮/输入框圆角是 24dp 的胶囊，官方/经典沿用原来的 20dp。 */
    val ButtonCornerRadius get() = if (TeakUiStyle.isCoui) 24.dp else 20.dp
    val InputCornerRadius get() = if (TeakUiStyle.isCoui) 22.dp else 20.dp
    val FormInputCornerRadius get() = if (TeakUiStyle.isCoui) 26.dp else 24.dp
    val DialogMaxWidth get() = if (TeakUiStyle.isCoui) 392.dp else 360.dp
    val DialogCornerRadius get() = if (TeakUiStyle.isCoui) 32.dp else 40.dp
    val DialogActionSpacing = 12.dp
    val DialogActionTopSpacing get() = if (TeakUiStyle.isCoui) 20.dp else 24.dp
}
