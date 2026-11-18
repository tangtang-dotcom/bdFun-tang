package com.aritxonly.bdfun

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.itos.coui.icons.CouiIcons

/**
 * COUI 风格下底栏使用的图标。
 *
 * ColorOS 的底栏图标是线性、圆角端点的一套；这里直接取 COUI-Kit 的 Filled 集，
 * 和官方 Miuix 图标区分开。CouiIcons 里没有 Home，用 Grid 代替首页。
 */
@Composable
internal fun couiTabGlyph(key: String, fallback: ImageVector): ImageVector = when (key) {
    "home" -> CouiIcons.Filled.Grid
    "dynamics" -> CouiIcons.Components
    "mine" -> CouiIcons.Filled.UserCircle
    else -> fallback
}
