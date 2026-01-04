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
            baseName = "send"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.mokoresources)
                implementation(projects.common.ui)
                implementation(projects.common.utils)
                implementation(project(":core:address"))
                implementation(project(":core:hdwallet"))
                implementation(project(":data:local"))
                implementation(project(":data:model"))
                implementation(project(":domain"))
                implementation(project(":features:chains:antelope_base"))
                implementation(project(":features:chains:bitcoin"))
                implementation(project(":features:chains:evmcompatible"))
                implementation(projects.antelope.antelopeBalance)
                implementation(project(":antelope:antelope_rpc"))
                implementation(project(":features:qrcode"))
                implementation(project(":features:send_base"))
                implementation(project(":features:settings:contacts"))
                implementation(project(":features:transactionqr_${currentFlavor}"))
                implementation(project(":libraries:scanqr"))

                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)

                implementation(libs.bignum)

                implementation(libs.bitcoin.kmp)
                implementation(libs.lightning.kmp)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)

                implementation(libs.kotlinx.datetime)
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

    namespace = "com.mangala.wallet.features.send"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}