# COUI 界面风格接入说明（v1）

本文记录「把 bdFun 的界面外观底层换成 COUI（ColorOS 凝光视效）」这一版的接入方式、边界与后续步骤。

## 一句话结论

COUI 不能整体替换 Miuix（Miuix 提供 preference 设置页、pager、ProgressiveBlur/LayerBackdrop 模糊引擎、squircle、glass、NavTransitions，COUI 没有这些），
所以本版采用**新增一个 `TeakStyle.Coui` 变体**的方式：结构、导航、数据流全部沿用 bdFun 原有实现，只把
**主题色桥接、底栏、弹窗、底部面板**这四层换成 COUI 的实现。默认风格不受影响，可随时切回。

## 启用方式

设置 → 外观 → 界面风格 → **COUI**（存储值 `interfaceStyle = 3`）。

- `TeakUI/src/main/kotlin/com/aritxonly/teak/common/theme/TeakStyle.kt`：`TeakStyle` 新增成员 `Coui(TeakDesign.Modern, TeakLayout.BottomSearch, isCoui = true)`，
  `fromStoredValue(3)` 返回它。复用了 HyperOS 4 的「底部搜索栏」版式，因此布局代码不需要再分叉。
- 之所以用「新增枚举成员 + `isCoui` 标记」而不是新增 `TeakDesign` 成员：`TeakDesign` 有 2 处
  exhaustive `when`（`TeakComposerBackground.kt`、`TeakSectionSurface.kt`），加成员会牵连无关文件；
  加 `isCoui` 布尔则零破坏。

## 四层桥接

| 层 | 文件 | 做法 |
| --- | --- | --- |
| 主题色 | `TeakUI/.../coui/TeakCouiTheme.kt` | 把 `MaterialTheme.colorScheme`（bdFun 已有的稳定契约）映射成 COUI 的 12 个颜色角色，再套 `CouiTheme`。**不改现有色板文件**，粉色/蓝色主色自动带过去。 |
| 底栏 | `TeakUI/.../coui/TeakCouiTabBar.kt` + `app/.../shell/MainCouiNavigation.kt` | API 34+ 用 COUI 的浮动 `NavigationBar`（带背景采样玻璃）；API 33 走自绘胶囊回退。 |
| 弹窗 | `TeakUI/.../coui/TeakCouiDialog.kt` + `modal/AlertDialog.kt` 路由 | 保留原 `AlertDialog(...)` 签名，内部按风格分流到 COUI Dialog 视觉（28dp 圆角、392dp 最大宽、250ms 缓动）。 |
| 底部面板 | `TeakUI/.../coui/TeakCouiDialog.kt` 的 `TeakCouiActionSheet` + `modal/TeakActionSheet.kt` 路由 | 12 处底部面板调用点一行都不用改，统一在壳里分流。 |

## 关键技术决策

1. **Compose 版本对齐**：`third_party/coui` 的依赖改成跟随宿主的 `org.jetbrains.compose`（CMP 1.12.2），
   而不是 COUI 上游的 compose-bom。副作用是 COUI 里用到了更新版 `PopupProperties` 的
   `blurBehindRadius` / `scrimAlpha` 两个参数，本仓库没有——已在
   `third_party/coui/src/main/java/com/itos/coui/menu/Menu.kt` 去掉这两个具名参数（菜单模糊由 COUI 自身材质绘制，视觉等价）。
2. **图标**：COUI 的 `CouiIcons` 只有约 70 个图标，且**没有 Home**。底栏「首页」用自绘的
   `TeakCouiGlyphHome`（标准 material home-outlined 路径），「动态」用 `CouiIcons.Filled.Album`，
   「我的」用 `CouiIcons.Filled.UserCircle`。其余图标按需在 `TeakCouiGlyphs.kt` 里补。
3. **API 34 门槛**：COUI 的浮动 `NavigationBar` / FAB / TopAppBarButton 标注 `@RequiresApi(34)`，
   本工程 minSdk 33，所以 33 上走自绘胶囊回退（外观接近，但没有真玻璃采样）。
4. **两套采样源并存**：COUI 风格下内容 Box 同时挂着 Miuix 的 `layerBackdrop` 与 COUI 的 `couiBackdrop`。
   v1 接受这点冗余（成本是一次额外绘制），后续可在 COUI 风格下跳过 Miuix 的采样。

## 还没换的部分（下一版）

- 顶栏：现在只换配色，素材与动效仍是 TeakProgressiveTopBar 的实现。
- 设置页控件（滑块、开关、下拉、分段）尚未换 COUI 组件，约 11 处 / 5 个文件。
- 图标只换了底栏 3 颗，全项目 30 个语义图标待逐一映射。
- 搜索结果页、搜索一镜到底动效仍是原实现。

## 回滚

改回「官方 / 经典 / HyperOS 4」任意风格即可，COUI 代码路径完全不会执行；删除
`third_party/coui*` 与上述四个桥接文件也能干净退回。
