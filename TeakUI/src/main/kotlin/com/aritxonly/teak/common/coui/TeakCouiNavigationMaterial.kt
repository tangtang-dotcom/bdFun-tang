package com.aritxonly.teak.common.coui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * COUI（ColorOS 凝光）底栏的着色层。
 *
 * ColorOS 原厂把底栏做成一层硬件合成表面：对背景内容做 blur -> luminosity -> overlay/color-dodge
 * 两段混色，再叠 0.25/0.8 的边缘光和 0.1/0.03 的内发光。
 * 这里保留底栏原有的玻璃采样（Miuix backdrop blur，等价于原厂的 blur 那一步），
 * 只把「凝光」那两段混色换成等价的自发光着色：高亮度半透明白 + 顶边高光 + 顶部内发光。
 * 好处是完全没有矩形采样 patch（原厂的采样层在非校准机型上会露出一条矩形色带）。
 */
fun Modifier.couiNavigationTint(isDark: Boolean, shape: Shape): Modifier = this
    .clip(shape)
    .background(
        if (isDark) Color(0xCC1B1B1B) else Color(0xE8FFFFFF),
    )
    .background(
        // 原厂 firstColor 的混色方向：浅色偏 COLOR_DODGE（提亮），深色偏 OVERLAY（压深）。
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = if (isDark) 0.06f else 0.35f),
                Color.Transparent,
            ),
        ),
    )
    .border(
        width = 0.75.dp,
        brush = Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = if (isDark) 0.28f else 0.95f),
                Color.White.copy(alpha = if (isDark) 0.05f else 0.45f),
            ),
        ),
        shape = shape,
    )
    .drawWithContent {
        drawContent()
        // 顶部内发光：让悬浮条上沿有厚度感。
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color.White.copy(alpha = if (isDark) 0.10f else 0.55f),
                0.45f to Color.Transparent,
            ),
        )
    }

/** COUI 底栏圆角（原厂 FloatingToolbarDefaults.CornerRadius = 24dp）。 */
val CouiNavigationCornerRadius = 24.dp

val CouiNavigationShape: Shape = RoundedCornerShape(CouiNavigationCornerRadius)
