import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.nativeCoroutines)
    alias(libs.plugins.mokoResources)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedLogic"
            isStatic = true
            // Lets Swift use moko's helpers: desc(), localized(), getUIColor(), readText().
            // (Not moko-graphics: its `Color` class would clash with SwiftUI's `Color`.)
            export(libs.moko.resources)
        }
    }

    android {
       namespace = "com.github.kittinunf.aiqua_testing.sharedLogic"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

       compilerOptions {
           jvmTarget = JvmTarget.JVM_21
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }

    compilerOptions {
        // The generated `expect object MR` (moko-resources) is an expect/actual class.
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        all {
            // Required by KMP-NativeCoroutines for the Swift-facing names it generates.
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }
        commonMain.dependencies {
            api(libs.androidx.lifecycle.viewmodel)
            api(libs.kotlinx.coroutines.core)
            api(libs.moko.resources)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

// Strings, colors and product.json shared by both apps live in src/commonMain/moko-resources.
multiplatformResources {
    resourcesPackage.set("com.github.kittinunf.aiqua_testing.resources")
}
