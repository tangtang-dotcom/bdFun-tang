package com.aritxonly.bdfun.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import io.github.suqi8.coui.kmp.blur.LayerBackdrop
import io.github.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.effect.bg.BgEffectBackground
import io.github.suqi8.coui.kmp.effect.bg.DeviceType
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
internal fun OnboardingBackground(
    playing: Boolean,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit = {},
) {
    val surface = COUITheme.colorScheme.surfaceContainer
    val shader = remember { isRuntimeShaderSupported() }
    BgEffectBackground(
        dynamicBackground = playing,
        deviceType = if (LocalConfiguration.current.screenWidthDp >= 600) {
            DeviceType.PAD
        } else {
            DeviceType.PHONE
        },
        isDarkTheme = surface.luminance() < 0.5f,
        surface = surface,
        modifier = modifier.fillMaxSize(),
        bgModifier = if (shader) Modifier.layerBackdrop(backdrop) else Modifier,
        isFullSize = true,
        content = content,
    )
}
