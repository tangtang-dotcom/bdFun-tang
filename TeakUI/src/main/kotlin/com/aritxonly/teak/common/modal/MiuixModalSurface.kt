// Migrated from Deadliner 3b561656 working tree; see docs/deadliner-modal-migration.md.
package com.aritxonly.teak.common.modal

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aritxonly.teak.common.material.TeakModalKind
import com.aritxonly.teak.common.material.miuixModalMaterial
import com.aritxonly.teak.common.material.GlassMaterialRecipe
import com.aritxonly.teak.common.material.GlassTintStyle
import com.aritxonly.teak.common.material.teakGlassMaterial
import com.aritxonly.teak.common.material.softGlassTintColor
import com.aritxonly.teak.common.material.LocalTeakBackdrop
import com.aritxonly.teak.common.material.LocalTeakMaterialSpec
import io.github.suqi8.coui.kmp.blur.Backdrop
import io.github.suqi8.coui.kmp.theme.COUITheme

@Immutable
data class COUIModalSurface(
    val modifier: Modifier,
    val backgroundColor: Color,
)

/**
 * Resolves one material policy for every Miuix modal surface.
 *
 * Advanced material off uses an opaque container; enabled material uses shared soft-glass optics.
 */
@Composable
fun rememberMiuixModalSurface(
    kind: TeakModalKind,
    shape: Shape,
    recipe: GlassMaterialRecipe,
    backdrop: Backdrop? = null,
): COUIModalSurface {
    val advancedMaterial = LocalTeakMaterialSpec.current
    val resolvedBackdrop = backdrop ?: LocalTeakBackdrop.current
        ?: LocalAdvancedMaterialDialogBlurHostState.current?.modalBackdrop
    val container = COUITheme.colorScheme.surfaceContainer
    val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
    val modalMaterial = advancedMaterial.fineTuning.miuixModalMaterial(kind)

    if (!advancedMaterial.textureBlurEnabled) {
        return COUIModalSurface(Modifier, container)
    }

    if (resolvedBackdrop == null) return COUIModalSurface(Modifier, container)
    val tint = if (kind == TeakModalKind.Dialog) container
        else advancedMaterial.fineTuning.softGlassTintColor(isDark)
    return COUIModalSurface(
        modifier = Modifier.teakGlassMaterial(
            advancedMaterial = advancedMaterial, backdrop = resolvedBackdrop, shape = shape,
            tint = tint, tintStyle = GlassTintStyle.Neutral, isDark = isDark, recipe = recipe,
            blurRadiusOverride = modalMaterial.blurRadiusDp,
            tintAlphaOverride = modalMaterial.backgroundAlpha,
        ),
        backgroundColor = Color.Transparent,
    )
}

/** Root-hosted Miuix dialog; conditional callers may disappear before its exit finishes. */
@Composable
fun COUIDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    title: String? = null,
    summary: String? = null,
    modifier: Modifier = Modifier,
    maxWidth: Dp = TeakModalDefaults.DialogMaxWidth,
    content: @Composable () -> Unit,
) {
    val host = LocalAdvancedMaterialDialogBlurHostState.current
    val request = COUIDialogRequest(title, summary, modifier, minOf(maxWidth, TeakModalDefaults.DialogMaxWidth),
        currentCompositionLocalContext, onDismissRequest, content)
    if (host == null) {
        COUIDialogSurface(show, request, onDismissFinished = {})
        return
    }
    val id = remember { Any() }
    SideEffect { if (show) host.present(id, request) else host.hide(id) }
    DisposableEffect(host, id) { onDispose { host.hide(id) } }
}
