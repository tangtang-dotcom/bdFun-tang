# UI 底层换 COUI（KMP 版）

## 换的是什么

`top.yukonga.miuix.kmp` → `io.github.suqi8.coui.kmp`。后者是 Miuix 的 ColorOS（凝光视效）皮肤重写版：
包结构与 API 一一对应，只是包名与符号前缀改了（`MiuixTheme` → `COUITheme`、`MiuixIcons` → `COUIIcons`）。

## 依赖（Maven Central，1.1.0）

```toml
coui = "1.1.0"
coui-ui / coui-preference / coui-nav / coui-blur / coui-icons / coui-shader / coui-squircle / coui-core
```

不再需要本地 Miuix 源码（`tools/setup-miuix.py` 与 `includeBuild` 已移除）。

## glass 模块

coui 上游没有发布 glass 模块，而 TeakUI 的玻璃材质（`TeakGlassSurface` / `glassPanel` /
`GlassDropdownPreference` / `GlassDropdownPopup`）依赖它的七个入口：
`glass` / `glassPanel` / `GlassShape` / `GlassMaterials` / `GlassDefaults` / `GlassStrokes` / `GlassDropdownPreference`。

处理方式：新建 `:third_party:teak-glass`，从同源（Apache-2.0）的 glass 实现移植，
包名保持 `io.github.suqi8.coui.kmp.glass`，让调用方零改动；只保留 bdFun 实际用到的文件，
`GlassDialog*` / `GlassBottomSheet*` / `GlassTopAppBar` / `GlassIcon*` / `GlassTabRow` /
`GlassNavigationBar*` 等未使用且有 API 漂移的文件已裁掉。

## 回滚

`git checkout main`（或 `feat/coui-ui-layer` 上的适配层版本）。
