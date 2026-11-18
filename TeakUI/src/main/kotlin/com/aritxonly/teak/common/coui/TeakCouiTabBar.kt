package com.aritxonly.teak.common.coui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.theme.CouiTheme

/** 底栏项：只持有矢量图标，选中/未选中可给不同素材。 */
data class TeakCouiTabItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
)

/** COUI 胶囊的统一圆角，和弹窗的 28dp 保持同一族。 */
private val CouiTabBarShape = RoundedCornerShape(28.dp)

/** 单个 Tab 的选中胶囊圆角，比外框小一圈才不会顶到边框。 */
private val CouiTabItemShape = RoundedCornerShape(22.dp)

/**
 * COUI 风格底栏（自绘）。
 *
 * 为什么不用 COUI 的 NavigationBar：它必须吃一个 CouiBackdrop，拿不到完整窗口采样时
 * 整条栏会被画成黑块；而且它每帧都要录一遍屏做玻璃，叠加本项目已有的 Miuix
 * layerBackdrop 后掉帧明显。这里自绘等价外观（胶囊 + 描边 + 阴影 + 选中色块），
 * 采样成本为零，样式和动效由我们自己控制。
 */
@Composable
fun TeakCouiTabBar(
    items: List<TeakCouiTabItem>,
    selectedKey: String,
    onItemSelected: (TeakCouiTabItem) -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = CouiTheme.isDark,
) {
    if (items.isEmpty()) return
    val index = items.indexOfFirst { it.key == selectedKey }.coerceAtLeast(0)
    val scheme = CouiTheme.colorScheme
    val container = if (dark) scheme.container else scheme.surface
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(elevation = 10.dp, shape = CouiTabBarShape, clip = false)
            .clip(CouiTabBarShape)
            .background(container)
            .border(0.5.dp, scheme.outlineVariant.copy(alpha = 0.7f), CouiTabBarShape)
            .padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items.forEachIndexed { i, item ->
            TeakCouiTabBarItem(
                item = item,
                selected = i == index,
                onClick = { onItemSelected(item) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TeakCouiTabBarItem(
    item: TeakCouiTabItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = CouiTheme.colorScheme
    // 选中态的胶囊和颜色一起渐变，避免切 tab 时“啪”地跳变。
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "couiTabSelection",
    )
    val tint = lerp(scheme.onSurfaceVariant, scheme.accent, progress)
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 6.dp)
            .clip(CouiTabItemShape)
            .background(scheme.accent.copy(alpha = 0.16f * progress))
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.icon,
            contentDescription = item.label,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.label,
            color = tint,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1,
        )
    }
}

/** COUI 风格的独立圆形搜索钮，和底栏同材质、同高度。 */
@Composable
fun TeakCouiSearchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = CouiTheme.isDark,
) {
    val scheme = CouiTheme.colorScheme
    val container = if (dark) scheme.container else scheme.surface
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(elevation = 10.dp, shape = CircleShape, clip = false)
            .clip(CircleShape)
            .background(container)
            .border(0.5.dp, scheme.outlineVariant.copy(alpha = 0.7f), CircleShape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = teakCouiSearch(),
            contentDescription = "搜索",
            tint = scheme.onSurface,
            modifier = Modifier.size(22.dp),
        )
    }
}
