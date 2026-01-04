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
            baseName = "home"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common:mokoresources"))
                implementation(project(":common:ui"))
                implementation(project(":common:utils"))
                implementation(project(":data:local"))
                implementation(project(":data:model"))
                implementation(project(":data:remote"))
                implementation(project(":domain"))
                implementation(project(":features:chains"))
                implementation(project(":features:chains:evmcompatible"))
//                implementation(project(":features:dex:uniswap"))  // TODO: Reenable
                implementation(project(":features:home_base"))
                implementation(project(":features:wallet_${currentFlavor}"))
                implementation(project(":features:nft_base"))
                implementation(project(":features:nft_${currentFlavor}"))
                implementation(project(":features:swap_base"))
                implementation(project(":features:browser_tab"))
                implementation(project(":features:chains:antelope_base"))
                implementation(project(":features:chains:antelope_pro")) //TODO: remove it because we should not place pro module here
                implementation(project(":features:conversationui"))
                implementation(project(":features:settings:menu_pro"))
                implementation(project(":features:addressbook"))

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

                implementation(libs.multiplatformPaging.common)
//                implementation(libs.multiplatformPaging.composeui) // TODO: Reenable

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
//                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)

                implementation(libs.imageLoader)

                implementation(libs.napier)

                implementation(project(":libraries:kmpnotifier"))
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
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
    namespace = "com.mangala.wallet.features.home"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
