package com.aritxonly.teak.common.coui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.navigation.CouiNavigationItem
import com.itos.coui.navigation.NavigationBar
import com.itos.coui.theme.CouiTheme

/** 底栏项：COUI 的 NavigationBar 吃 ImageVector，这里直接持有矢量，免去 Painter 转换。 */
data class TeakCouiTabItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
)

/**
 * COUI 底栏。COUI 的浮动 NavigationBar 是 @RequiresApi(34)，而本项目 minSdk 33，
 * 所以 API 33 走一个同外观的降级实现（胶囊白底 + 选中色块），34+ 才交给 COUI 的
 * 三主体玻璃渲染器；两条路径的尺寸、颜色、圆角一致，切机型不会跳变。
 */
@Composable
fun TeakCouiTabBar(
    items: List<TeakCouiTabItem>,
    selectedKey: String,
    onItemSelected: (TeakCouiTabItem) -> Unit,
    backdrop: CouiBackdrop,
    modifier: Modifier = Modifier,
    dark: Boolean = CouiTheme.isDark,
) {
    if (items.isEmpty()) return
    val index = items.indexOfFirst { it.key == selectedKey }.coerceAtLeast(0)
    if (Build.VERSION.SDK_INT >= 34) {
        TeakCouiTabBarFloating(items, index, onItemSelected, backdrop, modifier, dark)
    } else {
        TeakCouiTabBarCompat(items, index, onItemSelected, modifier, dark)
    }
}

@RequiresApi(34)
@Composable
private fun TeakCouiTabBarFloating(
    items: List<TeakCouiTabItem>,
    index: Int,
    onItemSelected: (TeakCouiTabItem) -> Unit,
    backdrop: CouiBackdrop,
    modifier: Modifier,
    dark: Boolean,
) {
    NavigationBar(
        modifier = modifier,
        backdrop = backdrop,
        items = items.map { CouiNavigationItem(it.label, it.icon, it.selectedIcon) },
        selectedIndex = index,
        dark = dark,
        onSelection = { onItemSelected(items[it]) },
    )
}

/** API 33 降级：COUI 的胶囊轮廓 + 选中态色块，不含玻璃采样。 */
@Composable
private fun TeakCouiTabBarCompat(
    items: List<TeakCouiTabItem>,
    index: Int,
    onItemSelected: (TeakCouiTabItem) -> Unit,
    modifier: Modifier,
    dark: Boolean,
) {
    val scheme = CouiTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(if (dark) scheme.container else scheme.surface)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items.forEachIndexed { i, item ->
            val selected = i == index
            val tint = if (selected) scheme.accent else scheme.onSurfaceVariant
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .then(
                        if (selected) Modifier.background(scheme.accent.copy(alpha = 0.14f), RoundedCornerShape(26.dp))
                        else Modifier,
                    )
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = if (selected) item.selectedIcon else item.icon,
                    contentDescription = item.label,
                    tint = tint,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = item.label,
                    color = tint,
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                )
            }
        }
    }
}
