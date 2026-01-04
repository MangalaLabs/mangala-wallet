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
            baseName = "antelope"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)

                implementation(libs.kotlinx.datetime)
                implementation(project(":common:utils"))
                implementation(project(":data:local"))
                implementation(project(":data:model"))
                implementation(project(":data:remote"))
                implementation(project(":common:ui"))
                implementation(project(":common:utils"))
                implementation(project(":common:mokoresources"))
                implementation(project(":antelope:antelope_key_manager"))
                implementation(projects.antelope.antelopeCore)
                implementation(projects.antelope.antelopeAction)
                implementation(project(":libraries:scanqr"))
                implementation(project(":features:chains:antelope_qr"))
                implementation(project(":features:chains:antelope_base"))
                implementation(project(":features:qrcode"))

                implementation(libs.koin.core)
                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
//                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)
            }
        }
//        commonMain.dependencies {
//            //put your multiplatform dependencies here
//        }
//        commonTest.dependencies {
//            implementation(libs.kotlin.test)
//        }
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.ios)
            }
        }
        val androidMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
//                implementation(libs.composeUi)
                implementation(libs.composeActivity)
//                implementation(libs.compose.material3)
//                implementation(libs.koin.android)
            }
        }
    }
}

android {
    namespace = "com.mangala.wallet.features.chains.antelope"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
