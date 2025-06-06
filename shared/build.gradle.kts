import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.properties.loadProperties

val properties = loadProperties("local.properties")

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
    alias(libs.plugins.realm)
}

detekt {
    toolVersion = "1.23.8"
    config.setFrom(file("detekt.yml"))
    buildUponDefaultConfig = true
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        iosMain.dependencies {
            // [Common] Async Client | https://github.com/ktorio/ktor | Apache-2.0
            implementation(libs.ktor.client.darwin)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // [Common] Async Client | https://github.com/ktorio/ktor | Apache-2.0
            implementation(libs.ktor.client.okhttp)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.animation)
            implementation(compose.foundation)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.material3)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.coroutines)

            // Model-Driven Navigation | https://github.com/bumble-tech/appyx | Apache-2.0
            implementation(libs.appyx.interactions)
            implementation(libs.appyx.navigation)
            api(libs.appyx.backstack)
            api(libs.appyx.spotlight)
            implementation(libs.appyx.material3)
            implementation(libs.appyx.multiplatform)

            // Glass-morphism | https://github.com/chrisbanes/haze | Apache-2.0
            implementation(libs.haze)

            // Functional | https://arrow-kt.io/ | Apache 2.0
            implementation(libs.arrow.core)

            // Lucide Icons | https://github.com/composablehorizons/composeicons | MIT
            //              | https://lucide.dev/ | ISC
            implementation(libs.composables.icons.lucide)

            // Async Client | https://github.com/ktorio/ktor | Apache-2.0
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.ws)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Async Media Loading & Caching | https://github.com/Kamel-Media/Kamel | Apache-2.0
            implementation(libs.kamel.image)

            // Realm Database | https://github.com/realm/realm-kotlin | Apache-2.0
            implementation(libs.realm.base)
            implementation(libs.realm.sync)
        }
    }
}

android {
    namespace = "dev.lancy.drp25"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "dev.lancy.drp25"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = properties.getProperty("android.keystore.password")
            keyAlias = properties.getProperty("android.key.alias")
            keyPassword = properties.getProperty("android.key.password")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
