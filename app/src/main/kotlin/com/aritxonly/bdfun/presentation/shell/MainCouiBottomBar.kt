package com.aritxonly.bdfun

import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.aritxonly.bdfun.core.model.video.VideoItem
import com.aritxonly.bdfun.feature.settings.presentation.SettingsBindings
import com.aritxonly.teak.common.coui.teakCouiColorScheme
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.effect.CouiFloatingRenderer
import com.itos.coui.effect.LocalCouiContourGlowEnabled
import com.itos.coui.floatingactionbutton.CouiFabDefaults
import com.itos.coui.floatingactionbutton.Fab
import com.itos.coui.icons.CouiIcons
import com.itos.coui.navigation.CouiDockBar
import com.itos.coui.navigation.CouiDockDefaults
import com.itos.coui.navigation.CouiDockItem
import com.itos.coui.navigation.CouiDockPlusStyle
import com.itos.coui.navigation.CouiDockStyle
import com.itos.coui.theme.CouiTheme

/**
 * COUI 风格底栏：菜单胶囊 = CouiDockBar（库原生材质/描边/阴影/跟手形变），
 * 右侧 56dp 槽位 = 上游 MainDockStack（搜索↔唱片两张卡，下滑切换、后台播放自动翻面）。
 *
 * 槽位收起：完全复刻 CouiDockBar 内部 plus 的动画参数 —— 宽度 68dp→0、alpha 1→0、scale 1→0.5，
 * 全部走 CouiDockDefaults.attachmentSpring()，因此和库自带 plus 面的收起手感一致。
 */
@OptIn(ExperimentalLayoutApi::class)
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
    val contour = LocalCouiContourGlowEnabled.current
    val systemBottomPx = WindowInsets.navigationBarsIgnoringVisibility.getBottom(density)
    val bottomInset = with(density) { systemBottomPx.toDp() }
    val imeCompensation = (WindowInsets.ime.getBottom(density) - systemBottomPx).coerceAtLeast(0)

    // 与库内 CouiDockBar 的 plus 面完全相同的收起规则与弹簧。
    val showSlot = selectedIndex < destinations.lastIndex || visual.discSlot > 0.01f
    val slotWidth by animateDpAsState(
        targetValue = if (showSlot) CouiDockDefaults.plusSlot else 0.dp,
        animationSpec = CouiDockDefaults.attachmentSpring(),
        label = "CouiDockStackSlot",
    )
    val slotAlpha by animateFloatAsState(
        targetValue = if (showSlot) 1f else 0f,
        animationSpec = CouiDockDefaults.attachmentSpring(),
        label = "CouiDockStackAlpha",
    )
    val slotScale by animateFloatAsState(
        targetValue = if (showSlot) 1f else 0.5f,
        animationSpec = CouiDockDefaults.attachmentSpring(),
        label = "CouiDockStackScale",
    )

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val available = (maxWidth - 36.dp).coerceAtLeast(0.dp)
        val dockWidth = minOf(
            CouiDockDefaults.menuWidth,
            (available - CouiDockDefaults.plusSlot).coerceAtLeast(120.dp),
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bottomInset + 8.dp)
                .graphicsLayer { translationY = imeCompensation.toFloat() },
            verticalAlignment = Alignment.Bottom,
        ) {
            CouiTheme(darkTheme = dark, colorScheme = scheme) {
                val style = remember(dark, scheme.accent, scheme.onSurfaceVariant, contour, density.density) {
                    CouiDockStyle.reference(
                        dark = dark,
                        selectedColor = scheme.accent,
                        unselectedColor = scheme.onSurfaceVariant.copy(alpha = 184f / 255f),
                        density = density.density,
                        sampleBrightness = 0.5f,
                        contourGlowEnabled = contour,
                        lightFollowLightIntensity = 2f,
                        darkFollowLightIntensity = 0.3f,
                        spotLightColor = scheme.accent,
                    )
                }
                val plusStyle = remember(dark, scheme.accent, contour, density.density) {
                    CouiDockPlusStyle.reference(
                        accent = scheme.accent,
                        dark = dark,
                        density = density.density,
                        contourGlowEnabled = contour,
                        icon = CouiIcons.Search,
                        iconDescription = "搜索",
                        lightFollowLightIntensity = 1.2f,
                        darkFollowLightIntensity = 0.5f,
                    ).let { base ->
                        base.copy(
                            material = CouiFabDefaults.translucentMaterial(
                                scheme.accent, dark, base.material.extension,
                            ),
                        )
                    }
                }
                CouiDockBar(
                    modifier = Modifier.width(dockWidth),
                    backdrop = backdrop,
                    renderer = renderer,
                    items = items,
                    selectedIndex = selectedIndex.coerceIn(0, destinations.lastIndex),
                    style = style,
                    plusStyle = plusStyle,
                    plusVisible = false,
                    plusEnabled = false,
                    menuWidth = dockWidth,
                    onSelection = { index -> destinations.getOrNull(index)?.let { onSelect(it.key) } },
                    onPlusClick = {},
                )
            }
            Box(
                modifier = Modifier
                    .width(slotWidth)
                    .height(CouiDockDefaults.barHeight)
                    .clipToBounds(),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (slotWidth > 0.5.dp) {
                    Box(
                        modifier = Modifier
                            .width(CouiDockDefaults.plusSize)
                            .height(CouiDockDefaults.plusSize)
                            .graphicsLayer {
                                alpha = slotAlpha
                                scaleX = slotScale
                                scaleY = slotScale
                            },
                    ) {
                        MainDockStack(
                            settings = settings,
                            visual = visual,
                            onSearch = onBottomSearch,
                            onOpenVideo = onOpenVideo,
                            slotSize = CouiDockDefaults.plusSize,
                            searchContent = {
                                CouiTheme(darkTheme = dark, colorScheme = scheme) {
                                    Fab(
                                        modifier = Modifier.fillMaxSize(),
                                        backdrop = backdrop,
                                        onClick = onBottomSearch,
                                        dark = dark,
                                        contentLabel = "搜索",
                                    ) {
                                        androidx.compose.material3.Icon(
                                            imageVector = CouiIcons.Search,
                                            contentDescription = "搜索",
                                            tint = scheme.onAccent,
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
