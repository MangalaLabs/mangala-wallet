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
            baseName = "features_browser_bridge"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.ui)
                implementation(project(":common:utils"))
                implementation(projects.common.mokoresources)
                implementation(project(":core:address"))
                implementation(project(":core:hdwallet"))
                implementation(project(":core:pin"))
                implementation(project(":data:model"))
                implementation(project(":domain"))
                implementation(project(":features:chains"))
                implementation(project(":features:chains:evmcompatible"))
                implementation(project(":features:walletconnect"))
                implementation(project(":features:send_base"))
                if (currentFlavor != "cold") {
                    implementation(project(":features:send_${currentFlavor}"))
                }

                implementation(libs.uuid)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.multiplatformSettings.core)

                implementation(libs.stately.common)
                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
//                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)
                implementation(libs.bignum)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.ktor.client.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.mockk.common)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.material)
                implementation("com.mangala:browser-bridge-api:1.0")
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

    namespace = "com.mangala.wallet.features.browser_bridge"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}