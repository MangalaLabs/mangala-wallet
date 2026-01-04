plugins {
    alias(libs.plugins.mangala.kotlin.multiplatform.feature)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"
            }
        }
    }

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
            baseName = "antelope_create_account"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.antelope.antelopeCore)
                implementation(projects.domain)
                implementation(projects.antelope.antelopeKeyManager)
                implementation(projects.antelope.antelopeRpc)
                implementation(projects.common.mokoresources)
                implementation(projects.common.ui)
                implementation(projects.common.utils)
                implementation(projects.core.ai)
                implementation(projects.core.pin)
                implementation(projects.data.model)
                implementation(projects.domain)
                implementation(projects.features.chains.antelopeBase)
                implementation(projects.features.chains.antelopeQr)
                implementation(projects.features.iap)
                implementation(projects.features.qrcode)
                implementation(projects.libraries.scanqr)

                implementation(libs.koin.core)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.koin)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(libs.multiplatform.material.icons.core)
                implementation(compose.runtime)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)

                implementation(libs.kotlinx.datetime)
            }
        }
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.composeActivity)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.mockk.common)
                implementation(libs.mockk)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

android {
    namespace = "com.mangala.wallet.features.chains.antelope.create_account"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
