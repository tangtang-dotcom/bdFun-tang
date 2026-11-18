package com.aritxonly.teak.common.coui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.aritxonly.teak.common.theme.TeakUiStyle
import com.itos.coui.icons.CouiIcons
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.*
import top.yukonga.miuix.kmp.icon.basic.*

/**
 * 全局图标统一入口：COUI 风格下用 COUI 图标集，其它风格回落到 Miuix 原图标。
 *
 * 之所以用 object + 逐项映射而不是改每个调用点，是因为 Miuix 的图标是
 * `MiuixIcons.X` 成员，二者类型都是 ImageVector，调用处只需把接收者换个名字。
 */
object TeakCouiIcons {
    private val coui: Boolean @Composable get() = TeakUiStyle.isCoui

    val Close: ImageVector @Composable get() = if (coui) CouiIcons.Close else MiuixIcons.Close
    val Search: ImageVector @Composable get() = if (coui) CouiIcons.Search else MiuixIcons.Search
    val More: ImageVector @Composable get() = if (coui) CouiIcons.More else MiuixIcons.More
    val Settings: ImageVector @Composable get() = if (coui) CouiIcons.Settings else MiuixIcons.Settings
    val ChevronBackward: ImageVector @Composable get() = if (coui) CouiIcons.Back else MiuixIcons.ChevronBackward
    val ChevronForward: ImageVector @Composable get() = if (coui) CouiIcons.Next else MiuixIcons.ChevronForward
    val SelectorArrow: ImageVector @Composable get() = if (coui) CouiIcons.Next else MiuixIcons.ChevronForward
    val ContactsCircle: ImageVector @Composable get() = if (coui) CouiIcons.Filled.UserCircle else MiuixIcons.ContactsCircle
    val Home: ImageVector @Composable get() = if (coui) CouiIcons.Filled.Grid else MiuixIcons.Home
    val Album: ImageVector @Composable get() = if (coui) CouiIcons.Featured else MiuixIcons.Album
    val SearchDevice: ImageVector @Composable get() = if (coui) CouiIcons.SearchField else MiuixIcons.SearchDevice
    val Delete: ImageVector @Composable get() = if (coui) CouiIcons.Delete else MiuixIcons.Delete
    val Info: ImageVector @Composable get() = if (coui) CouiIcons.Info else MiuixIcons.Info
    val Ok: ImageVector @Composable get() = if (coui) CouiIcons.Check else MiuixIcons.Ok
    val Filter: ImageVector @Composable get() = if (coui) CouiIcons.Outlined.SliderCustom else MiuixIcons.Filter
    val Messages: ImageVector @Composable get() = MiuixIcons.Messages
    val Play: ImageVector @Composable get() = MiuixIcons.Play
    val Refresh: ImageVector @Composable get() = MiuixIcons.Refresh
    val VolumeUp: ImageVector @Composable get() = MiuixIcons.VolumeUp
    val VolumeOff: ImageVector @Composable get() = MiuixIcons.VolumeOff
    val Reply: ImageVector @Composable get() = MiuixIcons.Reply
    val Folder: ImageVector @Composable get() = MiuixIcons.Folder
    val Copy: ImageVector @Composable get() = MiuixIcons.Copy
    val Download: ImageVector @Composable get() = MiuixIcons.Download
    val Recent: ImageVector @Composable get() = MiuixIcons.Recent
    val Promotions: ImageVector @Composable get() = MiuixIcons.Promotions
    val Community: ImageVector @Composable get() = MiuixIcons.Community
    val ContactsBook: ImageVector @Composable get() = MiuixIcons.ContactsBook
    val Blocklist: ImageVector @Composable get() = MiuixIcons.Blocklist
    val Report: ImageVector @Composable get() = MiuixIcons.Report
    val Reset: ImageVector @Composable get() = MiuixIcons.Reset
    val Image: ImageVector @Composable get() = MiuixIcons.Image
    val All: ImageVector @Composable get() = MiuixIcons.All
    val Background: ImageVector @Composable get() = MiuixIcons.Background
}
