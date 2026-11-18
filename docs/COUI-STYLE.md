# COUI 界面风格（ColorOS 17 凝光视效）

本分支从官方上游 `ce0d91a` 重开，只**新增**一种界面风格 `COUI`，不改动官方 / 经典 / HyperOS 4 的任何行为。

## 接入方式
- `third_party/coui`、`third_party/coui-icons`：COUI-Kit（MPL-2.0，见 `third_party/COUI-LICENSE`）源码模块，原样引入，仅去掉 mavenPublish/dokka 并让 Compose 依赖跟随宿主版本。
- `settings.gradle.kts` / `TeakUI/build.gradle.kts` / `gradle/libs.versions.toml`：各加 1~2 行 include/依赖。

## 与上游的接触面（其余全部是新文件）
1. `TeakUI/.../theme/TeakStyle.kt`：枚举新增 `Coui(Modern, BottomSearch, coui = true)` 与 `3 -> Coui`（3 行）。
2. `app/.../presentation/onboarding/BasicPage.kt` 与 `app/src/main/assets/settings-index.json`：各加一个选项（各 1 行）。
3. `app/.../settings/application/AppearanceSettingsStateHolder.kt`：`coerceIn(0, 2)` → `(0, 3)`（1 行）。
4. `app/.../shell/MainBottomNavigation.kt`：`TeakUiStyle.current.coui` 时走 COUI 底栏并 return（10 行）。
5. `app/.../shell/MainShellNavigation.kt`：透传 `couiBackdrop`（2 行）。
6. `app/.../shell/MainScaffold.kt`：创建并挂载 COUI backdrop、COUI 风格下用 offset 代替 graphicsLayer（20 行）。

## 新增文件
- `TeakUI/.../coui/TeakCouiBottomBar.kt`：直接调用 COUI-Kit `NavigationBar`（多层悬浮材质 + 轮廓光 + 焦散阴影 + 跟手动效）。
- `app/.../shell/MainCouiBottomBar.kt`：Destination → CouiIcons 条目映射。

## 注意事项
- COUI 底栏材质以 `BlendMode.Src` 直接合成到窗口目标，**祖先节点不能有 graphicsLayer**（alpha/scale 会离屏合成 → 黑块）。
- `NavigationBar` 需要 API 34+；低版本自动回退原有底栏。
