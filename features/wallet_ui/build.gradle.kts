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
            baseName = "features_wallet_tab"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common:resources"))
                implementation(projects.common.ui)
                implementation(projects.common.utils)
                implementation(project(":core:address"))
                implementation(project(":core:hdwallet"))
                implementation(project(":data:model"))
                implementation(project(":domain"))
                implementation(project(":features:chains:bitcoin"))
                implementation(project(":features:chains:evmcompatible"))
                implementation(project(":features:chart"))
                implementation(project(":features:dex:uniswap"))
                implementation(project(":features:eticket"))
                implementation(project(":features:home_base"))
                implementation(project(":features:qrcode"))
                implementation(project(":features:settings:contacts"))
                implementation(project(":features:wallet_tab"))
                implementation(project(":libraries:chart"))
                implementation(project(":libraries:scanqr"))

                implementation(libs.uuid)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.multiplatformSettings.core)
                implementation(libs.multiplatformPaging.common)
                implementation(libs.multiplatformPaging.composeui)

                implementation(libs.stately.common)
                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.runtime)

                implementation(libs.hyperdrive.multiplatformx.api)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)
                implementation(libs.bignum)
                implementation(libs.napier)
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
                implementation(project(":domain"))

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