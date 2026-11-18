package com.aritxonly.bdfun

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aritxonly.teak.common.interaction.blockTouches
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Search
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * COUI 风格的悬浮底栏。
 *
 * 之所以自绘而不是直接用 coui 的 FloatingNavigationBar：那个组件的内层 Row 会
 * fillMaxWidth，且 FloatingNavigationBarItem 只画图标不画文字，接进 bdFun 会变成
 * 一条横贯全屏、没有文字的空壳条。这里用 COUI 的色板与图标，按 ColorOS 的形态
 * 收紧到「胶囊 + 图标 + 小字」，宽度固定上限，保证视觉与手感都可控。
 */
@Composable
internal fun MainCouiNavigationBar(
    destinations: List<Destination>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    covered: Boolean = false,
    showSearch: Boolean = false,
    onSearch: () -> Unit = {},
    horizontalPadding: androidx.compose.ui.unit.Dp = 16.dp,
) {
    val scheme = COUITheme.colorScheme
    val container = scheme.surfaceContainer
    val indicator = scheme.primary
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha }
            .blockTouches(covered)
            .navigationBarsPadding()
            .padding(horizontal = horizontalPadding, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .shadow(12.dp, RoundedCornerShape(28.dp), clip = false)
                .clip(RoundedCornerShape(28.dp))
                .background(container)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            destinations.forEach { destination ->
                CouiTabItem(
                    label = destination.label,
                    selected = destination.key == selectedKey,
                    selectedColor = indicator,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelect(destination.key) },
                ) { tint -> Icon(destination.icon, contentDescription = destination.label, tint = tint, modifier = Modifier.size(22.dp)) }
            }
            if (showSearch) {
                CouiTabItem(
                    label = "搜索",
                    selected = false,
                    selectedColor = indicator,
                    modifier = Modifier.weight(1f),
                    onClick = onSearch,
                ) { tint -> Icon(COUIIcons.Search, contentDescription = "搜索", tint = tint, modifier = Modifier.size(22.dp)) }
            }
        }
    }
}

@Composable
private fun CouiTabItem(
    label: String,
    selected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: @Composable (Color) -> Unit,
) {
    val scheme = COUITheme.colorScheme
    val contentColor = if (selected) selectedColor else scheme.onBackgroundVariant
    val pill = if (selected) selectedColor.copy(alpha = 0.14f) else Color.Transparent
    Column(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(pill)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        icon(contentColor)
        Text(
            text = label,
            color = contentColor,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}
