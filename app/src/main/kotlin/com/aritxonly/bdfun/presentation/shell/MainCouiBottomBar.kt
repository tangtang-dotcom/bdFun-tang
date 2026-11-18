package com.aritxonly.bdfun

import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.aritxonly.bdfun.core.model.video.VideoItem
import com.aritxonly.bdfun.feature.settings.presentation.SettingsBindings
import com.aritxonly.bdfun.ui.BackgroundPlayDisc
import com.aritxonly.bdfun.ui.BackgroundPlayback
import com.aritxonly.bdfun.ui.DiscShadowRoom
import com.aritxonly.teak.common.coui.teakCouiColorScheme
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.effect.CouiFloatingRenderer
import com.itos.coui.effect.LocalCouiContourGlowEnabled
import com.itos.coui.floatingactionbutton.CouiFabDefaults
import com.itos.coui.icons.CouiIcons
import com.itos.coui.navigation.CouiDockBar
import com.itos.coui.navigation.CouiDockDefaults
import com.itos.coui.navigation.CouiDockItem
import com.itos.coui.navigation.CouiDockPlusStyle
import com.itos.coui.navigation.CouiDockStyle
import com.itos.coui.theme.CouiTheme
import kotlin.math.abs

/**
 * COUI 风格底栏。
 *
 * 搜索键完全交给库：CouiDockBar 自己的 plus 面（CouiDockPlusStyle + translucentMaterial），
 * 所以它的出现/收起动画就是 SleepDown 里那颗按钮的原生动画，一行都没重写。
 *
 * 唱片不占库的 plus 面，而是画在同一个槽位（menuWidth + plusGap）的独立层里；
 * 下滑手势在两颗之间切换。唱片层比 56dp 多留一圈 DiscShadowRoom，避免方形裁剪。
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

    val playing = visual.discSlot > 0.01f
    var manualDisc by remember { mutableStateOf<Boolean?>(null) }
    // 播放开始/结束自动翻面；手动切换后以手动为准，直到播放状态变化。
    LaunchedEffect(playing) { manualDisc = null }
    val discMode = manualDisc ?: playing

    // 设置页（最后一个 tab）永远收起，不管有没有唱片。
    val showSlot = selectedIndex < destinations.lastIndex
    val discAlpha by animateFloatAsState(
        targetValue = if (showSlot && discMode) 1f else 0f,
        animationSpec = CouiDockDefaults.attachmentSpring(),
        label = "CouiDiscAlpha",
    )
    val discScale by animateFloatAsState(
        targetValue = if (showSlot && discMode) 1f else 0.5f,
        animationSpec = CouiDockDefaults.attachmentSpring(),
        label = "CouiDiscScale",
    )

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val available = (maxWidth - 36.dp).coerceAtLeast(0.dp)
        val dockWidth = minOf(
            CouiDockDefaults.menuWidth,
            (available - CouiDockDefaults.plusSlot).coerceAtLeast(120.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bottomInset + 8.dp)
                .graphicsLayer { translationY = imeCompensation.toFloat() }
                .width(dockWidth + CouiDockDefaults.plusSlot)
                .height(CouiDockDefaults.barHeight),
        ) {
            CouiTheme(darkTheme = dark, colorScheme = scheme) {
                val style = remember(dark, scheme.accent, scheme.onSurfaceVariant, contour, density.density) {
                    CouiDockStyle.reference(
                        dark = dark,
                        selectedColor = scheme.accent,
                        unselectedColor = scheme.onSurfaceVariant.copy(alpha = 184f / 255f),
                        density = density.density,
                        sampleBrightness = 1f,
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
                    modifier = Modifier.align(Alignment.CenterStart),
                    backdrop = backdrop,
                    renderer = renderer,
                    items = items,
                    selectedIndex = selectedIndex.coerceIn(0, destinations.lastIndex),
                    style = style,
                    plusStyle = plusStyle,
                    // 库的 plus 面只在「搜索模式 + 该页需要槽位」时出现，动画全在库内。
                    plusVisible = showSlot && !discMode,
                    plusEnabled = showSlot && !discMode,
                    menuWidth = dockWidth,
                    onSelection = { index -> destinations.getOrNull(index)?.let { onSelect(it.key) } },
                    onPlusClick = { onBottomSearch() },
                )
            }
            // 唱片层：比 56dp 多留一圈阴影空间，graphicsLayer 不会把阴影裁成方块。
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = dockWidth + CouiDockDefaults.plusGap - DiscShadowRoom)
                    .size(CouiDockDefaults.plusSize + DiscShadowRoom * 2)
                    .graphicsLayer {
                        alpha = discAlpha
                        scaleX = discScale
                        scaleY = discScale
                    },
                contentAlignment = Alignment.Center,
            ) {
                BackgroundPlayDisc(
                    settings = settings,
                    dependencies = AppGraph.discDependencies,
                    modifier = Modifier.size(CouiDockDefaults.plusSize),
                    size = CouiDockDefaults.plusSize,
                    outerSize = CouiDockDefaults.plusSize,
                    breathEnabled = false,
                    shadowEnabled = false,
                    onOpenFallback = onOpenVideo,
                )
                // 点击唱片打开视频（手势层只吃拖动，点击交给这里）
                if (discMode && showSlot) {
                    Box(
                        Modifier
                            .size(CouiDockDefaults.plusSize)
                            .pointerInput(discMode) {
                                detectTapGestures { BackgroundPlayback.video?.let(onOpenVideo) }
                            },
                    )
                }
            }
            // 槽位手势层：只覆盖 56dp 搜索/唱片区域，点击和下滑都在这里处理。
            if (showSlot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = dockWidth + CouiDockDefaults.plusGap)
                        .size(CouiDockDefaults.plusSize)
                        .pointerInput(discMode) {
                            var total = 0f
                            detectVerticalDragGestures(
                                onDragStart = { total = 0f },
                                onDragEnd = {
                                    if (abs(total) > 20.dp.toPx()) manualDisc = !discMode
                                    total = 0f
                                },
                                onVerticalDrag = { _, delta -> total += delta },
                            )
                        }
                        .pointerInput(discMode) {
                            detectTapGestures {
                                if (discMode) BackgroundPlayback.video?.let(onOpenVideo) else onBottomSearch()
                            }
                        },
                )
            }
        }
    }
}
