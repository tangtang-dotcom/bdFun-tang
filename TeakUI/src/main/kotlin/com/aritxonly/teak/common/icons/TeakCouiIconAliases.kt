package com.aritxonly.teak.common.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.aritxonly.teak.common.theme.TeakUiStyle
import com.itos.coui.icons.CouiIcons
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Close as OriginalClose
import top.yukonga.miuix.kmp.icon.extended.Search as OriginalSearch
import top.yukonga.miuix.kmp.icon.extended.More as OriginalMore
import top.yukonga.miuix.kmp.icon.extended.Settings as OriginalSettings
import top.yukonga.miuix.kmp.icon.extended.ChevronBackward as OriginalChevronBackward
import top.yukonga.miuix.kmp.icon.extended.ChevronForward as OriginalChevronForward
import top.yukonga.miuix.kmp.icon.extended.Album as OriginalAlbum
import top.yukonga.miuix.kmp.icon.extended.Home as OriginalHome
import top.yukonga.miuix.kmp.icon.extended.Lock as OriginalLock
import top.yukonga.miuix.kmp.icon.extended.Contacts as OriginalContacts
import top.yukonga.miuix.kmp.icon.extended.ContactsCircle as OriginalContactsCircle
import top.yukonga.miuix.kmp.icon.extended.SearchDevice as OriginalSearchDevice
import top.yukonga.miuix.kmp.icon.extended.Tune as OriginalTune
import top.yukonga.miuix.kmp.icon.extended.Filter as OriginalFilter

/**
 * COUI 风格的图标覆盖层。
 *
 * 用法不变（还是 `MiuixIcons.Search`），只把 import 指到本包；
 * 运行时按 [TeakUiStyle.isCoui] 决定用 COUI 的图标还是官方 Miuix 的。
 * 这样官方/经典/HyperOS 风格一个像素都不受影响，只有 COUI 风格换一套字形。
 */
/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.Close: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.Close else MiuixIcons.OriginalClose

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.Search: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Outlined.Search else MiuixIcons.OriginalSearch

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.More: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.More else MiuixIcons.OriginalMore

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.Settings: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.Setting else MiuixIcons.OriginalSettings

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.ChevronBackward: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.Back else MiuixIcons.OriginalChevronBackward

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.ChevronForward: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.Next else MiuixIcons.OriginalChevronForward

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.Album: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.Album else MiuixIcons.OriginalAlbum

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.Home: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.Grid else MiuixIcons.OriginalHome

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.Lock: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.Lock else MiuixIcons.OriginalLock

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.Contacts: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.Users else MiuixIcons.OriginalContacts

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.ContactsCircle: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.UserCircle else MiuixIcons.OriginalContactsCircle

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.SearchDevice: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.Scan else MiuixIcons.OriginalSearchDevice

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.Tune: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Outlined.SliderCustom else MiuixIcons.OriginalTune

/** COUI 风格用 COUI 图标，其它风格保持 Miuix 原图。 */
val MiuixIcons.Filter: ImageVector
    @Composable get() = if (TeakUiStyle.isCoui) CouiIcons.Filled.ListFilter else MiuixIcons.OriginalFilter
