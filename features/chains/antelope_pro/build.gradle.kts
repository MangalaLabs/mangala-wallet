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
                implementation(libs.multiplatform.material.icons.core)
                implementation(compose.material3)
                implementation(compose.runtime)

                implementation(libs.kotlinx.datetime)
                implementation(projects.antelope.antelopeAction)
                implementation(projects.antelope.antelopeBalance)
                implementation(projects.antelope.antelopeCore)
                implementation(project(":antelope:antelope_key_manager"))
                implementation(project(":antelope:antelope_rpc"))
                implementation(project(":common:mokoresources"))
                implementation(project(":common:ui"))
                implementation(project(":common:utils"))
                implementation(project(":common:utils"))
                implementation(project(":core:pin"))
                implementation(project(":data:local"))
                implementation(project(":data:model"))
                implementation(project(":data:remote"))
                implementation(project(":domain"))
                implementation(project(":features:chains:antelope_base"))
                implementation(project(":features:chains:antelope_qr"))
                implementation(project(":features:chart"))
                implementation(project(":features:qrcode"))
                implementation(project(":libraries:chart"))
                implementation(project(":libraries:scanqr"))
                implementation(project(":features:iap"))
                implementation(project(":features:wallet_tab"))

                implementation(libs.koin.core)
                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.bottomSheetNavigator)
//                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.paging3)

                implementation(libs.uuid)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.ktorfit)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)
                implementation(libs.kotlinx.datetime)
                implementation(libs.napier)
                implementation(libs.multiplatformSettings.core)
                implementation(libs.multiplatformSettings.noarg)
                implementation(libs.multiplatformSettings.coroutines)
                implementation(libs.multiplatformPaging.common)
                implementation(libs.multiplatformPaging.composeui)
                implementation(libs.stately.common)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)
                implementation(libs.mokoParcelize)

                implementation(libs.napier)

                implementation(libs.bignum)

                implementation(libs.datetime.wheel.picker)
            }
        }

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
//        commonMain.dependencies {
//            //put your multiplatform dependencies here
//        }
//        commonTest.dependencies {
//            implementation(libs.kotlin.test)
//        }
        val androidMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
//                implementation(libs.composeUi)
                implementation(libs.composeActivity)
//                implementation(libs.compose.material3)
//                implementation(libs.koin.android)
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
