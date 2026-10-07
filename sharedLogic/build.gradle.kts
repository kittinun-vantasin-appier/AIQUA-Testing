import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI
import java.util.zip.ZipInputStream

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.nativeCoroutines)
    alias(libs.plugins.mokoResources)
}

// The AIQUA iOS SDK's headers, from the same release the iOS app links through Swift Package Manager.
val appierIosVersion = libs.versions.appier.ios.get()
val appierIosHeaders = layout.buildDirectory.dir("appier-ios/$appierIosVersion")
val downloadAppierIosHeaders by tasks.registering(DownloadAppierIosHeaders::class) {
    version.set(appierIosVersion)
    outputDir.set(appierIosHeaders)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        // Kotlin bindings for the AIQUA iOS SDK, generated from its Objective-C headers (see appier.def).
        iosTarget.compilations.getByName("main").cinterops.create("appier") {
            definitionFile.set(project.file("src/nativeInterop/cinterop/appier.def"))
            val slice = if (iosTarget.name == "iosArm64") "ios-arm64" else "ios-arm64_x86_64-simulator"
            compilerOpts("-F${appierIosHeaders.get().asFile}/Appier.xcframework/$slice")
            tasks.named(interopProcessingTaskName) { dependsOn(downloadAppierIosHeaders) }
        }
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
        androidMain.dependencies {
            implementation(libs.appier.android)
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

/** Downloads only the headers and module maps of github.com/appier/appier-ios-framework at a given release. */
abstract class DownloadAppierIosHeaders : DefaultTask() {
    @get:Input
    abstract val version: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun download() {
        val root = outputDir.get().asFile.apply { deleteRecursively(); mkdirs() }
        val url = URI("https://github.com/appier/appier-ios-framework/archive/refs/tags/v${version.get()}.zip").toURL()
        ZipInputStream(url.openStream()).use { zip ->
            generateSequence { zip.nextEntry }.forEach { entry ->
                // Entries look like "appier-ios-framework-8.11.3/Appier.xcframework/<slice>/Appier.framework/Headers/QGSdk.h".
                val path = entry.name.substringAfter('/')
                val wanted = path.startsWith("Appier.xcframework/") &&
                    ("/Appier.framework/Headers/" in path || "/Appier.framework/Modules/" in path)
                if (wanted && !entry.isDirectory) {
                    val file = root.resolve(path).apply { parentFile.mkdirs() }
                    file.outputStream().use { zip.copyTo(it) }
                }
            }
        }
    }
}
