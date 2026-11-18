package com.aritxonly.teak.common.coui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.itos.coui.theme.CouiTheme
import com.itos.coui.topappbar.CouiTopAppBarButtonDefaults
import com.itos.coui.topappbar.TopAppBarButton

/**
 * 顶栏搜索胶囊：完全交给库的 [TopAppBarButton]（text 模式）+ 库自己的 search 配方。
 * 材质/阴影/聚光/跟手形变都由库组件实现，这里只传内容。
 */
@RequiresApi(34)
@Composable
fun TeakCouiSearchCapsule(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = CouiTheme.isDark,
    content: @Composable RowScope.() -> Unit,
) {
    TopAppBarButton(
        onClick = onClick,
        modifier = modifier,
        text = true,
        style = CouiTopAppBarButtonDefaults.style(dark = dark, search = true),
        content = content,
    )
}
