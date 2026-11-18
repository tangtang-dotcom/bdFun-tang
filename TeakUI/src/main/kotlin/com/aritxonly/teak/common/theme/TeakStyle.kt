package com.aritxonly.teak.common.theme

import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 风格入口与存储编号兼容；现代设计由官方与 HyperOS 4 共享。 */
enum class TeakStyle(
    val design: TeakDesign,
    val layout: TeakLayout,
    /**
     * 材质/动效/图标是否交给 COUI（ColorOS 17 凝光视效）。
     * 不改 [design]，这样既有 TeakDesign 分支（设置卡片、输入框底色）无需改动；
     * 只有显式判断 isCoui 的装配点（底栏、弹窗、面板、图标）才换皮质。
     */
    val isCoui: Boolean = false,
) {
    Official(TeakDesign.Modern, TeakLayout.Floating),
    Classic(TeakDesign.Classic, TeakLayout.Edge),
    HyperOs4(TeakDesign.Modern, TeakLayout.BottomSearch),

    /** COUI：直接复用 HyperOS 4 的排列（悬浮胶囊 + 底部搜索一镜到底），只换外观。 */
    Coui(TeakDesign.Modern, TeakLayout.BottomSearch, isCoui = true);

    companion object {
        fun fromStoredValue(value: Int): TeakStyle = when (value) {
            1 -> Classic
            2 -> HyperOs4
            3 -> Coui
            else -> Official
        }
    }
}

/** 只承载设计差异，圆角类型与各页面原有尺寸由调用方保留。 */
enum class TeakDesign {
    Modern, Classic;

    val sectionTitleStart: Dp get() = if (this == Classic) 12.dp else 14.dp
    val sectionTitleTop: Dp get() = if (this == Classic) 12.dp else 0.dp

    fun cardShape(modern: Shape): Shape = when (this) {
        Modern -> modern
        Classic -> RectangleShape
    }

    fun contentInset(modern: Dp): Dp = when (this) {
        Modern -> modern
        Classic -> 0.dp
    }

    fun itemSpacing(modern: Dp, classic: Dp = 0.dp): Dp = when (this) {
        Modern -> modern
        Classic -> classic
    }
}

/** 排列与材质独立，避免新排列复制整套现代设计。 */
enum class TeakLayout(val hasBottomSearch: Boolean, val edgeNavigation: Boolean) {
    Floating(false, false),
    Edge(false, true),
    BottomSearch(true, false);
}
