package com.aritxonly.teak.common.coui

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.backdrop.drawCouiBackdrop
import com.itos.coui.effect.CouiFloatingMaterial
import com.itos.coui.theme.CouiTheme
import com.itos.coui.topappbar.CouiTopAppBarButtonDefaults

/** 顶栏按钮/胶囊的采样源（MainTopBarChrome 在 TopAppBarSurface 内容里提供）。 */
val LocalTeakCouiToolbarBackdrop = staticCompositionLocalOf<CouiBackdrop?> { null }

/**
 * 控件材质：照 SleepDown-Schedule 的 `CouiMaterialSurface` 写法——
 * 采样 + 模糊 + 两层混色合并成一个 RenderEffect，挂在 `drawCouiBackdrop` 的采样层上，
 * 内容作为普通子节点画在材质之后。不用 `couiFloatingSurface`（那个会把内容并进合成层）。
 */
@RequiresApi(31)
private fun controlMaterialEffect(material: CouiFloatingMaterial): RenderEffect? {
    var current: RenderEffect? = material.blurRadius
        .takeIf { it.isFinite() && it > 0f }
        ?.let { RenderEffect.createBlurEffect(it, it, Shader.TileMode.MIRROR) }
    current = chain(current, material.firstColor.toArgb(), material.firstBlendMode)
    current = chain(current, material.secondColor.toArgb(), material.secondBlendMode)
    return current
}

@RequiresApi(31)
private fun chain(input: RenderEffect?, argb: Int, blend: BlendMode): RenderEffect? {
    // 全透明 + SRC 之类是空操作，跳过能省一次离屏渲染。
    if ((argb ushr 24) == 0) return input
    val filter = BlendModeColorFilter(argb, blend)
    return if (input != null) {
        RenderEffect.createColorFilterEffect(filter, input)
    } else {
        RenderEffect.createColorFilterEffect(filter)
    }
}

/**
 * 给控件加 COUI 材质表面。`capsule=true` 用胶囊配方，否则用圆形按钮配方。
 * API < 31 或没有采样源时原样返回（调用方自行回退）。
 */
@Composable
fun Modifier.teakCouiMaterialSurface(
    backdrop: CouiBackdrop,
    shape: Shape,
    capsule: Boolean,
    dark: Boolean = CouiTheme.isDark,
): Modifier {
    if (Build.VERSION.SDK_INT < 31) return this
    val material = remember(dark, capsule) {
        CouiTopAppBarButtonDefaults.systemMaterial(dark, capsule).copy(blurRadius = 25f)
    }
    val effect = remember(material) { controlMaterialEffect(material)?.asComposeRenderEffect() }
    return this
        .graphicsLayer {
            this.shape = shape
            clip = true
        }
        .drawCouiBackdrop(
            backdrop = backdrop,
            shape = { shape },
            effects = {
                padding = material.blurRadius
                renderEffect = effect
            },
        )
}
