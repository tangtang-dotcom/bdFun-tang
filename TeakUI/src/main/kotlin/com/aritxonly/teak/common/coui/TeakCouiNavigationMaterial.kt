package com.aritxonly.teak.common.coui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.drawWithContent

/**
 * COUI（ColorOS 凝光）底栏材质。
 *
 * ColorOS 原厂是把底栏做成一层硬件合成表面，直接对**背景内容**做 luminosity + overlay 混色
 * （`CouiFloatingMaterial` 的两段色 + 边缘光 + 内发光）。这里用等价的手绘公式复现：
 * - 底色取原厂 secondColor 的提亮值，并给 92% 不透明度（原厂靠采样提亮，这里靠自发光，避免矩形采样带）
 * - 顶边 0.75dp 高光描边（原厂 edgeAlpha 0.25/0.8 的近似）
 * - 顶部内发光竖向渐变（原厂 innerGlowAlpha 0.1/0.03）
 * - 12dp 投影（原厂阴影由原生 tessellator 生成，跨机型会失真，这里用 Compose elevation）
 */
fun Modifier.couiNavigationMaterial(isDark: Boolean, shape: Shape): Modifier = this
    .shadow(elevation = 12.dp, shape = shape, clip = false)
    .clip(shape)
    .background(if (isDark) Color(0xF21B1B1B) else Color(0xF2FAFAFA))
    .border(
        width = 0.75.dp,
        brush = Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = if (isDark) 0.22f else 0.90f),
                Color.White.copy(alpha = if (isDark) 0.04f else 0.35f),
            ),
        ),
        shape = shape,
    )
    .drawWithContent {
        drawContent()
        // 顶部内发光：原厂材质里 innerGlow 把上沿再提亮一档，让悬浮条“有厚度”。
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color.White.copy(alpha = if (isDark) 0.07f else 0.40f),
                0.55f to Color.Transparent,
            ),
        )
    }

/** COUI 底栏的圆角（原厂 FloatingToolbarDefaults.CornerRadius = 24dp）。 */
val CouiNavigationCornerRadius = 24.dp

/** 供盒子式布局使用的默认形状。 */
val CouiNavigationShape: Shape = RoundedCornerShape(CouiNavigationCornerRadius)
