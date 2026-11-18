package com.aritxonly.teak.common.coui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.itos.coui.theme.CouiColorScheme

/** 把宿主的 Material3 色板折成 COUI 的 12 个角色（COUI 默认是官方蓝，必须映射）。 */
@Composable
fun teakCouiColorScheme(): CouiColorScheme {
    val m = MaterialTheme.colorScheme
    return CouiColorScheme(
        accent = m.primary,
        onAccent = m.onPrimary,
        surface = m.background,
        container = m.surfaceContainer,
        onSurface = m.onSurface,
        onSurfaceVariant = m.onSurfaceVariant,
        outlineVariant = m.outlineVariant,
        accentOnSurface = m.primary,
        menuSurface = m.surfaceContainer,
        checkboxAccent = m.primary,
        menuCheckboxAccent = m.onPrimary,
        background = m.background,
        card = m.surfaceContainer,
    )
}
