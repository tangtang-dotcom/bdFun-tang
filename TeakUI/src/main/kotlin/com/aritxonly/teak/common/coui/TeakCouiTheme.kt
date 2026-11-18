package com.aritxonly.teak.common.coui

import androidx.compose.runtime.Composable
import com.itos.coui.theme.CouiTheme

/**
 * COUI 主题作用域。
 *
 * 库的组件（底栏、顶栏按钮、弹窗）都从 `CouiTheme.colorScheme` / `CouiTheme.materialTintFor` 取色，
 * 不套这一层就会全部退回库自带的官方蓝。这里把宿主的 Material3 色板折成 COUI 的 12 个角色，
 * 材质染色则保持库默认（Transparent），避免把页面色强行混进凝光材质。
 */
@Composable
fun TeakCouiTheme(dark: Boolean, content: @Composable () -> Unit) {
    CouiTheme(
        darkTheme = dark,
        colorScheme = teakCouiColorScheme(),
        content = content,
    )
}
