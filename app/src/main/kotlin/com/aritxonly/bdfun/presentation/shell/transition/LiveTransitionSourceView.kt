package com.aritxonly.bdfun.presentation.shell.transition

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * 从 SleepDown 的 transition/LiveTransitionSourceView.kt 移植（只保留底栏 plus 需要的部分）。
 *
 * 作用：库的 CouiDockBar 把 plus 画在 Compose 图层里，隐藏时只有 alpha/scale 两个弹簧；
 * SleepDown 额外用一个真实 View 呈现同一块像素，并由 View 的 alpha 驱动 nativeVisible，
 * 让收起/展开走 View 的可见性切换，观感更接近 ColorOS 原厂。
 */
internal class LiveTransitionSourceView(
    private val activity: Activity,
    private val drawing: GraphicsLayer,
    private var drawingDensity: Density,
    private var direction: LayoutDirection,
    private val cornerRadiusPx: Float? = null,
) : View(activity) {
    private val scope = CanvasDrawScope()
    private var cropLeft = 0f
    private var renderButton = true
    private var nativeVisible by mutableStateOf(true)
    var hiddenForLegacyMorph by mutableStateOf(false)
        private set
    val showsOverflow: Boolean get() = nativeVisible && !hiddenForLegacyMorph

    init {
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius(view.width, view.height))
            }
        }
    }

    override fun setAlpha(alpha: Float) {
        super.setAlpha(alpha)
        nativeVisible = alpha > 0f
    }

    private fun radius(w: Int, h: Int): Float {
        val maximum = minOf(w, h) / 2f
        return cornerRadiusPx?.coerceIn(0f, maximum) ?: maximum
    }

    fun updatePresentation(density: Density, layoutDirection: LayoutDirection, render: Boolean) {
        if (drawingDensity == density && direction == layoutDirection && renderButton == render) return
        drawingDensity = density
        direction = layoutDirection
        renderButton = render
        invalidate()
    }

    fun updateCrop(left: Float) {
        if (cropLeft == left) return
        cropLeft = left
        invalidate()
    }

    override fun onDraw(canvas: AndroidCanvas) {
        if (!renderButton || hiddenForLegacyMorph) return
        if (!canvas.isHardwareAccelerated || drawing.isReleased) return
        val count = canvas.save()
        canvas.clipRect(0, 0, width, height)
        scope.draw(drawingDensity, direction, Canvas(canvas), Size(width.toFloat(), height.toFloat())) {
            translate(-cropLeft, 0f) { drawLayer(drawing) }
        }
        canvas.restoreToCount(count)
    }
}

internal tailrec fun Context.findHostActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findHostActivity()
    else -> null
}
