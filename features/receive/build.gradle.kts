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
            baseName = "receive"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core:address"))
                implementation(project(":core:ai"))
                implementation(project(":core:hdwallet"))
                implementation(project(":data:model"))
                implementation(project(":data:local"))
                implementation(project(":domain"))
                implementation(project(":common:mokoresources"))
                implementation(project(":common:ui"))
                implementation(project(":common:utils"))
                implementation(project(":libraries:scanqr"))
                implementation(project(":features:qrcode"))
                implementation(project(":features:chains:evmcompatible"))
                implementation(project(":features:settings:contacts"))
                implementation(project(":features:chains:antelope_base"))

                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.runtime)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)
                implementation(libs.napier)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)

                implementation(libs.bignum)
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

    namespace = "com.mangala.wallet.features.receive"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}