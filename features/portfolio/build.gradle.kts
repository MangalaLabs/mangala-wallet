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
            baseName = "portfolio"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common:mokoresources"))
                implementation(project(":common:ui"))
                implementation(project(":common:utils"))
                implementation(project(":core:hdwallet"))
                implementation(project(":data:local"))
                implementation(project(":data:model"))
                implementation(project(":data:remote"))
                implementation(project(":domain"))
                implementation(project(":features:chains"))
                implementation(project(":features:chains:antelope_base"))
                implementation(project(":features:chains:bitcoin"))
                implementation(project(":features:chains:evmcompatible"))
                implementation(project(":features:chart"))
                implementation(project(":features:settings:contacts"))
                implementation(project(":libraries:scanqr"))
                implementation(project(":libraries:chart"))
                implementation(project(":features:wallet_tab"))
                implementation(project(":antelope:antelope_rpc"))
                implementation(projects.antelope.antelopeBalance)

                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.paging3)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.multiplatformSettings.core)

                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.runtime)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
//                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)

                implementation(libs.bignum)

                implementation(libs.imageLoader)

                implementation(libs.napier)
            }
        }
        val commonTest by getting {
        }
//        val androidMain by getting {
//            kotlin.srcDirs("src/jvmMain/kotlin")
//            dependencies {
//                dependsOn(commonMain)
//                api(libs.activity.compose)
//                api(libs.appcompat)
//                api(libs.androidx.core.ktx)
//                implementation(libs.androidx.core.ktx)
//            }
//        }
//        val iosArm64Main by getting
//        val iosSimulatorArm64Main by getting
//        val iosMain by getting {
//            dependsOn(commonMain)
//            iosArm64Main.dependsOn(this)
//            iosSimulatorArm64Main.dependsOn(this)
//            dependencies {
//            }
//        }
//
        val jvmMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependsOn(commonMain)
            dependencies {
                implementation(compose.desktop.common)
            }
        }
    }
}

android {
    namespace = "com.mangala.wallet.features.portfolio"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
