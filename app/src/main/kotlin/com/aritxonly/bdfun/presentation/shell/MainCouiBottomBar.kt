package com.aritxonly.bdfun

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.aritxonly.bdfun.core.model.video.VideoItem
import com.aritxonly.bdfun.feature.settings.presentation.SettingsBindings
import com.aritxonly.teak.common.coui.teakCouiColorScheme
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.effect.CouiFloatingRenderer
import com.itos.coui.navigation.CouiDockBar
import com.itos.coui.navigation.CouiDockDefaults
import com.itos.coui.floatingactionbutton.Fab
import com.itos.coui.navigation.CouiDockItem
import com.itos.coui.navigation.CouiDockPlusStyle
import com.itos.coui.navigation.CouiDockStyle
import com.itos.coui.theme.CouiTheme

/**
 * COUI 风格底栏：菜单胶囊交给 COUI-Kit 的 CouiDockBar，右侧槽位复用上游的 MainDockStack。
 *
 * 上游把「搜索键 / 唱片」做成共用同一个槽位的两张卡（下滑切换），COUI 风格原先直接
 * return 掉了整条底栏，连唱片一起丢掉；这里把槽位接回来，保持与 HyperOS 4 相同的交互。
 * CouiDockBar 自带的 plus 面保持收起，避免出现两个搜索键。
 */
@RequiresApi(34)
@Composable
internal fun BoxScope.MainCouiBottomBar(
    settings: SettingsBindings,
    visual: MainBottomNavigationVisual,
    destinations: List<Destination>,
    selectedKey: String,
    dark: Boolean,
    backdrop: CouiBackdrop,
    onSelect: (String) -> Unit,
    onBottomSearch: () -> Unit,
    onOpenVideo: (VideoItem) -> Unit,
) {
    if (destinations.isEmpty()) return
    val renderer = remember { runCatching { CouiFloatingRenderer.pair() }.getOrNull() } ?: return
    val items = destinations.map { destination ->
        CouiDockItem(title = destination.label, icon = destination.icon, selectedIcon = destination.icon)
    }
    val selectedIndex = destinations.indexOfFirst { it.key == selectedKey }.coerceAtLeast(0)
    val scheme = teakCouiColorScheme()
    val density = LocalDensity.current
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BoxWithConstraints(
        Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .drawWithContent { runCatching { drawContent() } },
    ) {
        val available = (maxWidth - 36.dp).coerceAtLeast(0.dp)
        val dockWidth = minOf(
            CouiDockDefaults.menuWidth,
            (available - CouiDockDefaults.plusSlot).coerceAtLeast(120.dp),
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bottomInset + 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(CouiDockDefaults.plusGap),
        ) {
            CouiTheme(darkTheme = dark, colorScheme = scheme) {
                val style = remember(dark, scheme.accent, scheme.onSurfaceVariant, density) {
                    CouiDockStyle.reference(
                        dark = dark,
                        selectedColor = scheme.accent,
                        unselectedColor = scheme.onSurfaceVariant.copy(alpha = 184f / 255f),
                        density = density.density,
                        contourGlowEnabled = true,
                    )
                }
                val plusStyle = remember(dark, scheme.accent, density) {
                    CouiDockPlusStyle.reference(
                        accent = scheme.accent,
                        dark = dark,
                        density = density.density,
                        contourGlowEnabled = true,
                        icon = com.itos.coui.icons.CouiIcons.Search,
                        iconDescription = "搜索",
                    )
                }
                CouiDockBar(
                    modifier = Modifier.width(dockWidth),
                    backdrop = backdrop,
                    renderer = renderer,
                    items = items,
                    selectedIndex = selectedIndex,
                    style = style,
                    plusStyle = plusStyle,
                    plusVisible = false,
                    plusEnabled = false,
                    menuWidth = dockWidth,
                    onSelection = { index -> destinations.getOrNull(index)?.let { onSelect(it.key) } },
                    onPlusClick = {},
                )
            }
            MainDockStack(
                settings = settings,
                visual = visual,
                onSearch = onBottomSearch,
                onOpenVideo = onOpenVideo,
                searchContent = {
                    // COUI 风格保留库里的 Fab：凝光材质 + 跟手形变 + 按压回弹都由库实现。
                    CouiTheme(darkTheme = dark, colorScheme = scheme) {
                        Fab(
                            modifier = Modifier.fillMaxSize(),
                            backdrop = backdrop,
                            onClick = onBottomSearch,
                            dark = dark,
                            contentLabel = "搜索",
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = com.itos.coui.icons.CouiIcons.Search,
                                contentDescription = "搜索",
                                tint = CouiTheme.colorSchemeFor(dark).onAccent,
                            )
                        }
                    }
                },
            )
        }
    }
}
