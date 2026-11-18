// COUI（ColorOS 17 凝光视效）源码模块，MPL-2.0，见 third_party/COUI-LICENSE。
// 与上游差异：去掉 mavenPublish/dokka；Compose 依赖跟随宿主的 org.jetbrains.compose 1.12.2，
// 避免 CMP 版本族错配；minSdk 从 31 提到 33 以对齐 bdFun。
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.itos.coui"
    compileSdk = 37
    buildToolsVersion = "37.0.0"

    defaultConfig {
        minSdk = 33
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        optIn.add("androidx.compose.foundation.style.ExperimentalFoundationStyleApi")
    }
}

dependencies {
    api(libs.compose.ui)
    api(libs.compose.foundation)
    api(libs.compose.animation)
    implementation(libs.compose.runtime)
    implementation(libs.androidx.core)
    implementation("androidx.core:core-ktx:1.18.0")
    implementation(libs.androidx.dynamicanimation)
    implementation(libs.coroutines.android)
}
