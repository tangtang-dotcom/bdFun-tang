package com.aritxonly.bdfun

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aritxonly.bdfun.core.model.video.VideoItem
import com.aritxonly.bdfun.feature.settings.presentation.SettingsBindings
import com.aritxonly.bdfun.presentation.shell.transition.LiveTransitionSourceView
import com.aritxonly.bdfun.presentation.shell.transition.findHostActivity
import com.aritxonly.bdfun.ui.BackgroundPlayDisc
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
import kotlin.math.roundToInt

/**
 * COUI 风格底栏：与 SleepDown-Schedule 的 CouiHomeDockChrome 逐项对齐。
 *
 * 对齐点（全部照抄原实现，不做「近似」）：
 *  - CouiDockStyle.reference：sampleBrightness / lightFollowLightIntensity 2f / darkFollowLightIntensity 0.3f / spotLightColor
 *  - CouiDockPlusStyle：lightFollowLightIntensity 1.2f / darkFollowLightIntensity 0.5f + CouiFabDefaults.translucentMaterial
 *  - WindowInsets.navigationBarsIgnoringVisibility + WindowInsets.ime 补偿
 *  - LiveTransitionSourceView + AndroidView + liveLayer 裁剪（plus 用真实 View 呈现）
 *  - plusInputModifier = onGloballyPositioned + 按压态
 * 额外：播放时唱片占同一个 56dp 槽位（由 plusVisible 让位，BackgroundPlayDisc 淡入）。
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
    onSearch: (() -> Unit)?,
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
    val selectedColor = scheme.accent
    val unselectedColor = scheme.onSurfaceVariant.copy(alpha = 184f / 255f)
    val sampleBrightness = 0.5f
    val dockStyle = remember(dark, selectedColor, unselectedColor, sampleBrightness, contour, density.density) {
        CouiDockStyle.reference(
            dark = dark,
            selectedColor = selectedColor,
            unselectedColor = unselectedColor,
            density = density.density,
            sampleBrightness = sampleBrightness,
            contourGlowEnabled = contour,
            lightFollowLightIntensity = 2f,
            darkFollowLightIntensity = 0.3f,
            spotLightColor = selectedColor,
        )
    }
    val plusStyle = remember(dark, selectedColor, density, contour) {
        CouiDockPlusStyle.reference(
            accent = selectedColor,
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
                    selectedColor, dark, base.material.extension,
                ),
            )
        }
    }
    // SleepDown：plusVisible = selectedIndex < 2（最后一个 tab 收起）；COUI 里再让位给唱片。
    val discActive = visual.discSlot > 0.01f
    val plusVisible = onSearch != null && selectedIndex < destinations.lastIndex && !discActive
    val plusEnabled = plusVisible
    val systemBottomPx = WindowInsets.navigationBarsIgnoringVisibility.getBottom(density)
    val bottomInset = with(density) { systemBottomPx.toDp() }
    val imeCompensation = (WindowInsets.ime.getBottom(density) - systemBottomPx).coerceAtLeast(0)
    val activity = LocalContext.current.findHostActivity()
    val layoutDirection = LocalLayoutDirection.current
    val liveLayer = rememberGraphicsLayer()
    val plusView = remember(activity, liveLayer, density, layoutDirection) {
        activity?.let { LiveTransitionSourceView(it, liveLayer, density, layoutDirection) }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val available = (maxWidth - 36.dp).coerceAtLeast(0.dp)
        val dockWidth = minOf(
            CouiDockDefaults.menuWidth,
            (available - CouiDockDefaults.plusSlot).coerceAtLeast(120.dp),
        )
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bottomInset + 8.dp)
                .graphicsLayer { translationY = imeCompensation.toFloat() },
        ) {
            if (plusView != null && (plusVisible || discActive)) {
                AndroidView(
                    factory = { plusView },
                    modifier = Modifier.align(Alignment.CenterEnd).size(CouiDockDefaults.plusSize),
                    update = { it.updatePresentation(density, layoutDirection, render = plusVisible) },
                )
            }
            CouiTheme(darkTheme = dark, colorScheme = scheme) {
                CouiDockBar(
                    modifier = Modifier.drawWithContent {
                        val button = plusView?.takeIf { plusVisible && it.isAttachedToWindow && it.width > 0 }
                        if (button == null) {
                            drawContent()
                        } else {
                            val width = button.width.toFloat()
                            val left = if (layoutDirection == LayoutDirection.Ltr) size.width - width else 0f
                            liveLayer.record(
                                density,
                                layoutDirection,
                                IntSize(size.width.roundToInt(), size.height.roundToInt()),
                            ) {
                                this@drawWithContent.drawContent()
                            }
                            button.updateCrop(left)
                            clipRect(left, 0f, left + width, size.height, ClipOp.Difference) {
                                drawLayer(liveLayer)
                            }
                        }
                    },
                    backdrop = backdrop,
                    renderer = renderer,
                    items = items,
                    selectedIndex = selectedIndex.coerceIn(0, destinations.lastIndex),
                    style = dockStyle,
                    plusStyle = plusStyle,
                    plusVisible = plusVisible,
                    plusEnabled = plusEnabled,
                    menuWidth = dockWidth,
                    showPlusSurface = { plusView?.showsOverflow != false },
                    onSelection = { index -> destinations.getOrNull(index)?.let { onSelect(it.key) } },
                    plusInputModifier = Modifier.onGloballyPositioned { },
                    onPlusClick = { onSearch?.invoke() },
                )
            }
            // 唱片占同一个槽位：plus 面由库收起后，同一位置淡入。
            if (discActive) {
                BackgroundPlayDisc(
                    settings = settings,
                    dependencies = AppGraph.discDependencies,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(CouiDockDefaults.plusSize)
                        .graphicsLayer { alpha = visual.discSlot },
                    size = CouiDockDefaults.plusSize,
                    outerSize = CouiDockDefaults.plusSize,
                    breathEnabled = false,
                    shadowEnabled = false,
                    onOpenFallback = onOpenVideo,
                )
            }
        }
    }
}
