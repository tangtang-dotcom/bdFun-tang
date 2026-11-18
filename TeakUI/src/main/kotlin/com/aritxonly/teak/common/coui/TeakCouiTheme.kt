package com.aritxonly.teak.common.coui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.itos.coui.theme.CouiColorScheme
import com.itos.coui.theme.CouiTheme

/**
 * 把宿主已经算好的 Material3 色板折算成 COUI 的 12 个角色。
 *
 * 为什么从 MaterialTheme 取而不是从 Miuix 取：Miuix 的 Colors 收在 ThemeController 里，
 * 字段随版本变动；Material3 ColorScheme 由 AppTheme.kt 里 appMaterialColors() 统一映射，
 * 是唯一稳定的跨模块契约。这样 COUI 组件不会退回官方蓝 #0080FF。
 */
@Composable
fun TeakCouiTheme(dark: Boolean, content: @Composable () -> Unit) {
    val m = MaterialTheme.colorScheme
    val scheme = CouiColorScheme(
        accent = m.primary,
        onAccent = m.onPrimary,
        surface = m.background,
        container = m.surfaceContainer,
        onSurface = m.onSurface,
        onSurfaceVariant = m.onSurfaceVariant,
        outlineVariant = m.outlineVariant,
        background = m.background,
        card = m.surfaceContainer,
    )
    CouiTheme(darkTheme = dark, colorScheme = scheme, content = content)
}
