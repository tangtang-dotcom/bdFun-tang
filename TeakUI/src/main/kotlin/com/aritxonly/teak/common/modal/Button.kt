// Migrated from Deadliner 3b561656 working tree; see docs/deadliner-modal-migration.md.
package com.aritxonly.teak.common.modal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

// 为官方和 MIUIX 的组件起别名，防止重名冲突
import androidx.compose.material3.LocalContentColor as Material3LocalContentColor
import io.github.suqi8.coui.kmp.basic.Button as COUIButton
import io.github.suqi8.coui.kmp.basic.ButtonColors as COUIButtonColors
import io.github.suqi8.coui.kmp.basic.ButtonDefaults as COUIButtonDefaults
import io.github.suqi8.coui.kmp.basic.TextButton as COUITextButton
import io.github.suqi8.coui.kmp.basic.TextButtonColors as COUITextButtonColors
import io.github.suqi8.coui.kmp.theme.LocalContentColor as COUILocalContentColor
import io.github.suqi8.coui.kmp.theme.COUITheme

internal enum class COUIDialogActionStyle {
    Default,
    Secondary,
    Primary,
}

internal val LocalMiuixDialogActionStyle = staticCompositionLocalOf {
    COUIDialogActionStyle.Default
}

@Composable
private fun resolveMiuixButtonColors(
    style: COUIDialogActionStyle, fallback: ButtonColors, destructive: Boolean = false,
): COUIButtonColors {
    if (style == COUIDialogActionStyle.Default) return COUIButtonDefaults.buttonColors(
        color = fallback.containerColor, contentColor = fallback.contentColor,
        disabledColor = fallback.disabledContainerColor, disabledContentColor = fallback.disabledContentColor)
    // 弹窗跟随 Miuix 中性色板，避免 Material 默认色板为按钮带入紫色。
    return COUIButtonDefaults.buttonColors(
        color = COUITheme.colorScheme.secondaryContainer.copy(alpha = .85f),
        disabledColor = COUITheme.colorScheme.secondaryContainer.copy(alpha = .85f),
        contentColor = if (destructive) MaterialTheme.colorScheme.error else COUITheme.colorScheme.onSecondaryContainer,
        disabledContentColor = COUITheme.colorScheme.disabledOnSurface)
}

@Composable
private fun resolveMiuixTextButtonColors(
    style: COUIDialogActionStyle, destructive: Boolean = false,
): COUITextButtonColors {
    if (style == COUIDialogActionStyle.Default) return COUIButtonDefaults.textButtonColors()
    return COUIButtonDefaults.textButtonColors(
        color = COUITheme.colorScheme.secondaryContainer.copy(alpha = .85f),
        disabledColor = COUITheme.colorScheme.secondaryContainer.copy(alpha = .85f),
        textColor = if (destructive) MaterialTheme.colorScheme.error else COUITheme.colorScheme.onSecondaryContainer,
        disabledTextColor = COUITheme.colorScheme.disabledOnSurface)
}

private fun Modifier.miuixDialogActionWidth(style: COUIDialogActionStyle): Modifier =
    if (style == COUIDialogActionStyle.Default) this else fillMaxWidth()

/**
 * Deadliner 基础 Button 组件 (实心按钮)
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    forceMaterial3: Boolean = false,
    destructive: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val dialogActionStyle = LocalMiuixDialogActionStyle.current
    val miuixColors = resolveMiuixButtonColors(dialogActionStyle, colors, destructive)
    val resolvedContentColor = if (enabled) miuixColors.contentColor else miuixColors.disabledContentColor
    COUIButton(
        onClick = onClick,
        modifier = modifier.miuixDialogActionWidth(dialogActionStyle),
        enabled = enabled,
        cornerRadius = TeakModalDefaults.ButtonCornerRadius,
        colors = miuixColors,
    ) {
        CompositionLocalProvider(
            Material3LocalContentColor provides resolvedContentColor,
            COUILocalContentColor provides resolvedContentColor,
        ) {
            content()
        }
    }
}

/**
 * Deadliner 基础 TextButton 组件 (文本按钮)
 */
@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.textShape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    forceMaterial3: Boolean = false,

    // MIUIX 专属辅助参数
    miuixText: String = "",
    useMiuixDialogActionColors: Boolean = true,

    destructive: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val dialogActionStyle = LocalMiuixDialogActionStyle.current
    val miuixModifier = modifier.miuixDialogActionWidth(dialogActionStyle)
    if (miuixText.isNotBlank()) {
        COUITextButton(
            text = miuixText,
            onClick = onClick,
            modifier = miuixModifier,
            enabled = enabled,
            cornerRadius = TeakModalDefaults.ButtonCornerRadius,
            colors = resolveMiuixTextButtonColors(dialogActionStyle, destructive),
        )
    } else {
        val miuixColors = resolveMiuixButtonColors(
            style = dialogActionStyle,
            fallback = colors, destructive = destructive,
        )
        val resolvedContentColor = if (enabled) miuixColors.contentColor else miuixColors.disabledContentColor
        COUIButton(
            onClick = onClick,
            modifier = miuixModifier,
            enabled = enabled,
            cornerRadius = TeakModalDefaults.ButtonCornerRadius,
            colors = miuixColors,
        ) {
            CompositionLocalProvider(
                Material3LocalContentColor provides resolvedContentColor,
                COUILocalContentColor provides resolvedContentColor,
            ) {
                content()
            }
        }
    }
}
