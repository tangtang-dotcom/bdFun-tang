package com.aritxonly.bdfun

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aritxonly.bdfun.presentation.shell.transition.LiveTransitionSourceView
import com.aritxonly.bdfun.presentation.shell.transition.findHostActivity
import com.aritxonly.teak.common.coui.teakCouiColorScheme
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.effect.CouiFloatingRenderer
import com.itos.coui.icons.CouiIcons
import com.itos.coui.navigation.CouiDockBar
import com.itos.coui.navigation.CouiDockDefaults
import com.itos.coui.navigation.CouiDockItem
import com.itos.coui.navigation.CouiDockPlusStyle
import com.itos.coui.navigation.CouiDockStyle
import com.itos.coui.theme.CouiTheme

/**
 * COUI 风格底栏：完全照 SleepDown 的 CouiHomeDockChrome 写法。
 *
 * - CouiDockBar（菜单胶囊 + 右附件 FAB 双主体，renderer 必须 pair）
 * - plus 用真实 View（LiveTransitionSourceView）呈现，View 的可见性驱动收起/展开；
 *   Compose 只画除 plus 区域外的像素，两者共用同一张 GraphicsLayer，接缝不可见。
 */
@RequiresApi(34)
@Composable
internal fun BoxScope.MainCouiBottomBar(
    destinations: List<Destination>,
    selectedKey: String,
    dark: Boolean,
    backdrop: CouiBackdrop,
    onSelect: (String) -> Unit,
    onSearch: (() -> Unit)?,
) {
    if (destinations.isEmpty()) return
    val renderer = remember { runCatching { CouiFloatingRenderer.pair() }.getOrNull() } ?: return
    val items = destinations.map { destination ->
        CouiDockItem(title = destination.label, icon = destination.icon, selectedIcon = destination.icon)
    }
    val selectedIndex = destinations.indexOfFirst { it.key == selectedKey }.coerceAtLeast(0)
    val scheme = teakCouiColorScheme()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val context = LocalContext.current
    val activity = remember(context) { context.findHostActivity() }
    val liveLayer = rememberGraphicsLayer()
    val plusRadiusPx = with(density) { CouiDockDefaults.plusSize.toPx() / 2f }
    val plusView = remember(activity, liveLayer, plusRadiusPx, density, layoutDirection) {
        activity?.let { LiveTransitionSourceView(it, liveLayer, density, layoutDirection, plusRadiusPx) }
    }
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // SleepDown 的 CouiHomeDockChrome：plusVisible = selectedIndex < 2（最后一个 tab 收起）。
    val plusVisible = onSearch != null && selectedIndex < destinations.lastIndex

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
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bottomInset + 8.dp),
        ) {
            if (plusView != null) {
                AndroidView(
                    factory = { plusView },
                    modifier = Modifier.align(Alignment.CenterEnd).size(CouiDockDefaults.plusSize),
                    update = { it.updatePresentation(density, layoutDirection, render = plusVisible) },
                )
            }
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
                        icon = CouiIcons.Search,
                        iconDescription = "搜索",
                    )
                }
                CouiDockBar(
                    modifier = Modifier.drawWithContent {
                        val button = plusView?.takeIf { plusVisible && it.isAttachedToWindow && it.width > 0 }
                        if (button == null) {
                            drawContent()
                        } else {
                            val width = button.width.toFloat()
                            val left = if (layoutDirection == LayoutDirection.Ltr) size.width - width else 0f
                            liveLayer.record(density, layoutDirection, IntSize(size.width.roundToInt(), size.height.roundToInt())) {
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
                    selectedIndex = selectedIndex,
                    style = style,
                    plusStyle = plusStyle,
                    plusVisible = plusVisible,
                    plusEnabled = plusVisible,
                    menuWidth = dockWidth,
                    showPlusSurface = { plusView?.showsOverflow != false },
                    onSelection = { index -> destinations.getOrNull(index)?.let { onSelect(it.key) } },
                    onPlusClick = { onSearch?.invoke() },
                )
            }
        }
    }
}
