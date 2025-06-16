import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.properties.loadProperties
import java.util.Properties
import java.io.FileInputStream

val properties = Properties()
try {
    val file = File("local.properties")
    FileInputStream(file).use {fis ->
        properties.load(fis)
        properties.forEach {key, value -> println("   $key = $value")}
    }
} catch (e: Exception) {
    throw e
}

//val properties = loadProperties("local.properties")

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
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

            // Add these for Compose UI on iOS
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.runtime)
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

            // Supabase | https://github.com/supabase/supabase-kt | Apache-2.0
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.kt)
            implementation(libs.storage.kt)

            // Multiplatform Settings | https://github.com/russhwolf/multiplatform-settings | Apache-2.0
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.serialization)

            // Kotlin Date-Time | https://github.com/Kotlin/kotlinx-datetime | Apache-2.0
            implementation(libs.kotlinx.datetime)

            // Barcode Scanner | https://github.com/kalinjul/EasyQRScan | Apache-2.0
            implementation(libs.scanner)
        }
    }
}

android {
    namespace = "dev.lancy.drp25"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.lancy.drp25"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

compose.resources {
    generateResClass = always
    packageOfResClass = "dev.lancy.drp25.shared.resources"
}


dependencies {
    debugImplementation(compose.uiTooling)
}
