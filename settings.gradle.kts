pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public")
    }
}

rootProject.name = "bdFun"
include(":app")
include(":core:model")
include(":TeakUI")

// COUI 的 glass 模块：coui 上游没有发布，这里从其同源实现（Apache-2.0）移植，包名保持 io.github.suqi8.coui.kmp.glass
include(":third_party:teak-glass")
include(":danmaku-engine")

// COUI KMP（Miuix 的 ColorOS 皮肤重写版）从 Maven Central 引入，见 gradle/libs.versions.toml。

include(":core:common")

include(":core:domain")

include(":core:application")

include(":core:navigation")

include(":infra:bili")

include(":infra:storage")

include(":infra:playback")

include(":feature:home")
include(":tools:kotlin-audit")

include(":feature:video")
