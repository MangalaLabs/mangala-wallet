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
            baseName = "walletconnect"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":data:local"))
                implementation(project(":common:mokoresources"))
                implementation(project(":common:ui"))
                implementation(project(":common:utils"))
                implementation(project(":libraries:walletconnect"))

                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(libs.multiplatform.material.icons.core)
                implementation(compose.runtime)

                implementation(libs.kotlinx.serialization.json)


                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)
                implementation(libs.ktor.io)
                implementation(libs.ktor.client.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.koin.android)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
                implementation(libs.androidx.activity.compose)
                implementation(libs.appcompat)
                implementation(libs.androidx.core.ktx)
                implementation("androidx.compose.ui:ui:1.4.0-rc01")
                implementation("androidx.compose.ui:ui-tooling-preview:1.4.0-rc01")
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

    namespace = "com.mangala.wallet.walletconnect"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
