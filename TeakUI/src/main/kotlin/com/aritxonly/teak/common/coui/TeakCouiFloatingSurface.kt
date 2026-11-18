package com.aritxonly.teak.common.coui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.effect.CouiFloatingSurface
import com.itos.coui.effect.CouiFloatingRenderer
import com.itos.coui.effect.LocalCouiContourGlowEnabled
import com.itos.coui.effect.couiFloatingSurfaces
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.shape.CouiShape
import com.itos.coui.theme.CouiTheme
import com.itos.coui.topappbar.CouiTopAppBarButtonDefaults
import androidx.compose.ui.unit.dp

/** 顶栏按钮/胶囊的采样源（由 MainTopBarChrome 在 TopAppBarSurface 内容里提供）。 */
val LocalTeakCouiToolbarBackdrop = staticCompositionLocalOf<CouiBackdrop?> { null }

/**
 * 顶栏小控件表面：走**底栏同一套**三主体 `couiFloatingSurfaces` 合成（一条真实可见面 + 两条透明面），
 * 内容作为普通子节点画在材质之后 —— 既不吞内容，也拿得到库的凝光描边/内发光/阴影。
 */
@RequiresApi(34)
@Composable
fun TeakCouiFloatingSurface(
    backdrop: CouiBackdrop,
    modifier: Modifier = Modifier,
    capsule: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current.density
    val contour = LocalCouiContourGlowEnabled.current
    val renderer = remember { runCatching { CouiFloatingRenderer() }.getOrNull() }
    if (renderer == null) {
        Box(modifier, contentAlignment) { content() }
        return
    }
    val material = CouiTopAppBarButtonDefaults.systemMaterial(dark, capsule)
    val shadow = CouiTopAppBarButtonDefaults.shadow(dark, capsule, density, contour)
    val shape = if (capsule) CouiCapsuleShape else CouiShape(999.dp, cornerWeight = 2f)
    val materials = remember(material) {
        listOf(
            material.copy(index = 1),
            material.copy(index = 2),
            material.copy(
                index = 2,
                firstColor = Color.Transparent,
                secondColor = Color.Transparent,
                edgeAlpha = 0f,
                innerGlowAlpha = 0f,
            ),
        )
    }
    val shapes = remember(shape) { listOf(shape, shape, shape) }
    val shadows = remember(shadow) { listOf(shadow, shadow, shadow) }
    var boundsSize by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
            .onSizeChanged { boundsSize = it }
            .couiFloatingSurfaces(
                backdrop = backdrop,
                renderer = renderer,
                materials = materials,
                shadows = shadows,
                shapes = shapes,
                contentLayers = List(3) { rememberGraphicsLayer() },
                contourGlowEnabled = contour,
                foregroundComposition = false,
            ) {
                val bounds = Rect(
                    Offset.Zero,
                    Size(boundsSize.width.toFloat(), boundsSize.height.toFloat()),
                )
                listOf(
                    CouiFloatingSurface(bounds),
                    CouiFloatingSurface(bounds, alpha = 0f, visible = false),
                    CouiFloatingSurface(bounds, alpha = 0f, visible = false),
                )
            },
        contentAlignment = contentAlignment,
    ) {
        content()
    }
}

/** 当前是否可用 COUI 浮动材质（风格开、API 34+、并且有采样源）。 */
@Composable
fun teakCouiFloatingAvailable(): Boolean =
    Build.VERSION.SDK_INT >= 34 && LocalTeakCouiToolbarBackdrop.current != null
