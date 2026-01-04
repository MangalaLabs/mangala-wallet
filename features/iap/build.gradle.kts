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
            baseName = "features_iap"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.antelope.antelopeCore)
                implementation(project(":core:auth"))
                implementation(project(":common:utils"))
                implementation(project(":data:model"))
                implementation(project(":features:chains:antelope_base"))

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.multiplatformSettings.core)

                implementation(libs.stately.common)
                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
                implementation(compose.material3)

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.core)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.krypto)

                implementation(libs.mokoParcelize)
            }
        }
//        val commonTest by getting {
//            dependencies {
//                implementation(kotlin("test"))
//                implementation(libs.mockk.common)
//            }
//        }
        val androidMain by getting {
            dependencies {
                dependsOn(commonMain)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.activity.ktx)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
                implementation(libs.androidx.activity.ktx)
                implementation(libs.koin.core)
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)

                implementation(libs.android.billing)
                implementation(libs.android.billing.ktx)

                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.installations)
                implementation(libs.firebase.auth)
            }
        }
//        val androidUnitTest by getting
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
//        val jvmTest by getting {
//            dependencies {
//
//            }
//        }
    }
}

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    namespace = "com.mangala.wallet.iap"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}