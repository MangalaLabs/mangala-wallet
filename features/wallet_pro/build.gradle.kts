plugins {
    alias(libs.plugins.mangala.kotlin.multiplatform.feature)
}

kotlin {
    val currentFlavor: String by project

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
            baseName = "features_wallet_tab"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.antelope.antelopeBalance)
                implementation(projects.antelope.antelopeRpc)
                implementation(projects.common.mokoresources)
                implementation(projects.common.ui)
                implementation(projects.common.utils)
                implementation(projects.core.address)
                implementation(projects.core.auth)
                implementation(projects.core.hdwallet)
                implementation(projects.core.pin)
                implementation(projects.data.model)
                implementation(projects.data.remote)
                implementation(projects.domain)
                implementation(project(":features:chains:antelope_${currentFlavor}"))
                implementation(projects.features.auth)
                implementation(projects.features.chains.antelopeBase)
                implementation(projects.features.chains.antelopeQr)
                implementation(projects.features.chains.bitcoin)
                implementation(projects.features.chains.evmcompatible)
                implementation(projects.features.chart)
//                implementation(projects.features.dex.uniswap) // TODO: Reenable
//                implementation(projects.features.eticket)
                implementation(projects.features.homeBase)
                implementation(projects.features.qrcode)
//                implementation(projects.features.settings.contacts)  // TODO: Reenable
                implementation(projects.features.walletTab)
                implementation(projects.libraries.chart)
                implementation(projects.libraries.scanqr)

                implementation(libs.uuid)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.multiplatformSettings.core)

                implementation(libs.stately.common)
                implementation(libs.koin.core)
                implementation(libs.koin.androidx.compose)
                implementation(projects.features.onboarding)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.runtime)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)
                implementation(libs.bignum)
                implementation(libs.napier)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)

                implementation(projects.libraries.kmpnotifier)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.mockk.common)
            }
        }
        val androidMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
//                implementation(libs.coil)
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
                implementation(libs.ktor.client.ios)
                implementation(libs.ktor.client.darwin)
            }
        }

        val jvmMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(projects.domain)

                implementation(libs.mockk)
            }
        }
    }
}

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    namespace = "com.mangala.wallet.features.wallet"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}