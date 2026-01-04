plugins {
    alias(libs.plugins.mangala.kotlin.multiplatform.feature)
}

kotlin {
    androidTarget()

    jvm() {
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "antelope_qr"
        }
    }

    // export correct artifact to use all classes of library directly from Swift
    targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java).all {
        binaries.withType(org.jetbrains.kotlin.gradle.plugin.mpp.Framework::class.java).all {
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.antelope.antelopeBase)
                implementation(projects.antelope.antelopeRpc)
                implementation(projects.antelope.antelopeCore)
                implementation(projects.antelope.antelopeKeyManager)
                implementation(projects.antelope.antelopeAction)
                implementation(projects.common.ui)
                implementation(projects.common.utils)
                implementation(projects.data.local)
                implementation(projects.data.model)
                implementation(projects.features.chains.antelopeBase)
                implementation(projects.features.qrcode)

                implementation(libs.koin.core)

                implementation(libs.kotlinx.datetime)

//                implementation(libs.stately.common)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.koin)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.zxing.android)
                implementation(libs.zxing.core)
                implementation(libs.android.constraint.layout)
                implementation(libs.composeActivity)
                implementation("androidx.compose.ui:ui:1.4.0-rc01")
                implementation("androidx.compose.ui:ui-tooling-preview:1.4.0-rc01")
            }
        }
        val androidUnitTest by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
            }
        }

        val jvmMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                implementation(libs.zxing.core)
            }
        }
    }
}

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    namespace = "com.mangala.wallet.features.chains.antelope_qr"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
