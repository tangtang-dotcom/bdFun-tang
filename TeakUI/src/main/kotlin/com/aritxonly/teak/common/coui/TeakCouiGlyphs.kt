package com.aritxonly.teak.common.coui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp
import com.itos.coui.icons.CouiIcons

/**
 * COUI 图标只有约 70 个语义名，缺 Home/Folder/Play/Refresh 等。
 * 缺失的用 Material Icons 的 24dp 轮廓路径补齐，保证底栏三颗图标笔画一致，
 * 而不是一半 COUI 一半 Miuix 混着显示。
 */
private fun vector(name: String, pathData: String): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).addPath(
    pathData = PathParser().parsePathString(pathData).toNodes(),
    fill = SolidColor(Color.Black),
).build()

/** 首页：COUI 没有 Home，自绘 Material home-outlined。 */
val TeakCouiGlyphHome: ImageVector by lazy {
    vector("TeakCouiHome", "M12,5.69l5,4.5V18h-2v-6H9v6H7v-7.81l5,-4.5M12,3L2,12h3v8h6v-6h2v6h6v-8h3L12,3z")
}

/** 动态/列表（COUI 有 Album，但底栏第二项语义是「动态」，用 Album 与官方一致）。 */
@Composable
fun teakCouiAlbum(): ImageVector = CouiIcons.Filled.Album

/** 我的（COUI 无 ContactsCircle，用 UserCircle 近似）。 */
@Composable
fun teakCouiUser(): ImageVector = CouiIcons.Filled.UserCircle

@Composable
fun teakCouiSearch(): ImageVector = CouiIcons.Outlined.Search

@Composable
fun teakCouiClose(): ImageVector = CouiIcons.Filled.Close

@Composable
fun teakCouiMore(): ImageVector = CouiIcons.Filled.More

@Composable
fun teakCouiSetting(): ImageVector = CouiIcons.Filled.Setting
