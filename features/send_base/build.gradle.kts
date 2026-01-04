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
            baseName = "send_base"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.antelope.antelopeBalance)
                implementation(project(":antelope:antelope_rpc"))
                implementation(projects.common.mokoresources)
                implementation(project(":common:ui"))
                implementation(project(":common:utils"))
                implementation(project(":core:address"))
                implementation(project(":core:ai"))
                implementation(project(":core:hdwallet"))
                implementation(project(":core:security"))
                implementation(project(":data:local"))
                implementation(project(":data:model"))
                implementation(project(":domain"))
                implementation(project(":features:addressbook"))
                implementation(project(":features:chains:antelope_base"))
                implementation(project(":features:chains:bitcoin"))
                implementation(project(":features:chains:evmcompatible"))
                implementation(project(":features:qrcode"))
                implementation(project(":features:settings:contacts"))
                implementation(project(":features:wallet_tab"))
                implementation(project(":libraries:scanqr"))

                implementation(libs.napier)

                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(libs.multiplatform.material.icons.core)
                implementation(compose.runtime)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.uuid)

                implementation(libs.bignum)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)
                
                implementation(libs.multiplatformPaging.common)
                implementation(libs.multiplatformPaging.composeui)

                implementation(libs.kotlinx.coroutines.core)
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
            }
        }
    }
}

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    namespace = "com.mangala.wallet.send_base"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}