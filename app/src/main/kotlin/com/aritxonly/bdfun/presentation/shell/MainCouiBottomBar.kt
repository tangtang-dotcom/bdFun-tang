package com.aritxonly.bdfun

import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.aritxonly.bdfun.core.model.video.VideoItem
import com.aritxonly.bdfun.feature.settings.presentation.SettingsBindings
import com.aritxonly.bdfun.ui.BackgroundPlayDisc
import com.aritxonly.bdfun.ui.BackgroundPlayback
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
 * 搜索键 = CouiDockBar 的 plus 面（库原生材质 + 原生收起动画）。
 * 唱片也画在同一个 plus 面里（plusContent），因此它和搜索键共用：
 * 同一颗节点、同一份材质、同一根 attachmentSpring、同一套 press/跟手动画。
 * 下滑切换由 plusInputModifier 上挂的手势处理，轻点仍交给库的 clickable。
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
    LaunchedEffect(playing) { manualDisc = null }
    // 没播放时永远显示搜索键，不允许翻出空唱片。
    val discMode = if (!playing) false else manualDisc ?: true
    // 设置页（最后一个 tab）永远收起，不管有没有唱片。
    val showSlot = selectedIndex < destinations.lastIndex

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
                .graphicsLayer { translationY = imeCompensation.toFloat() },
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
                    modifier = Modifier,
                    backdrop = backdrop,
                    renderer = renderer,
                    items = items,
                    selectedIndex = selectedIndex.coerceIn(0, destinations.lastIndex),
                    style = style,
                    plusStyle = plusStyle,
                    // plus 面的可见性 = 槽位是否显示：搜索和唱片共用这一个面。
                    plusVisible = showSlot,
                    plusEnabled = showSlot,
                    menuWidth = dockWidth,
                    onSelection = { index -> destinations.getOrNull(index)?.let { onSelect(it.key) } },
                    onPlusClick = {
                        if (discMode) BackgroundPlayback.video?.let(onOpenVideo) else onBottomSearch()
                    },
                    plusInputModifier = Modifier.pointerInput(discMode, playing) {
                        // Initial pass 先看手势：不抢轻点（库的 clickable/press 动画照常），
                        // 只在纵向拖动超过 20dp 后消费并切换，避免被库的 consumeDrag 吃掉。
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                            var total = 0f
                            var fired = false
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                total += change.position.y - change.previousPosition.y
                                if (!fired && abs(total) > 20.dp.toPx()) {
                                    fired = true
                                    if (discMode) manualDisc = false
                                    else if (playing) manualDisc = true
                                }
                                if (fired) change.consume()
                                if (!change.pressed) break
                            }
                        }
                    },
                    plusContent = if (discMode) {
                        {
                            BackgroundPlayDisc(
                                settings = settings,
                                dependencies = AppGraph.discDependencies,
                                modifier = Modifier.fillMaxSize(),
                                size = CouiDockDefaults.plusSize,
                                outerSize = CouiDockDefaults.plusSize,
                                breathEnabled = false,
                                shadowEnabled = false,
                                onOpenFallback = onOpenVideo,
                            )
                        }
                    } else null,
                )
            }
        }
    }
}
