# 原生迁移保留 ProGuard 默认规则

-keep class io.github.suqi8.coui.kmp.** { *; }
-keep class com.kyant.** { *; }
-keep class com.aritxonly.deadliner.** { *; }
-dontwarn org.json.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn coil.**
-dontwarn androidx.media3.**
