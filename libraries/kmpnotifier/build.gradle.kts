import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
}

kotlin {
    explicitApi()
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm()
    iosArm64()
    iosSimulatorArm64()


    cocoapods {
        ios.deploymentTarget = "15"
        framework {
            baseName = "KMPNotifier"
            isStatic = true
        }
        noPodspec()
        // pod("FirebaseMessaging") 
        // {
            // extraOpts -= listOf("-framework", "\"FirebaseMessaging\"") //Extra opts is important
        // }

        xcodeConfigurationToNativeBuildType["DebugCold"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["DebugPro"] = NativeBuildType.DEBUG
        // xcodeConfigurationToNativeBuildType["ReleaseUi"] = NativeBuildType.RELEASE
        xcodeConfigurationToNativeBuildType["ReleasePro"] = NativeBuildType.RELEASE
        xcodeConfigurationToNativeBuildType["ReleaseCold"] = NativeBuildType.RELEASE
    }



    sourceSets {

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.startup.runtime)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.activity.ktx)
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.messaging)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                api(libs.koin.core)
            }
        }
    }
}

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    namespace = "com.mmk.kmpnotifier"
    compileSdk = androidCompileSdk.toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = androidMinSdk.toInt()
        targetSdk = androidTargetSdk.toInt()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

