plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    alias(libs.plugins.composeCompiler)
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
    iosArm64()
    iosSimulatorArm64()
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "antelope_base"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
//    implementation(compose.material)
                implementation(compose.runtime)

                implementation(projects.antelope.antelopeRpc)
                implementation(projects.antelope.antelopeCore)
                implementation(projects.antelope.antelopeAction)
                implementation(projects.antelope.antelopeBalance)
                implementation(projects.antelope.antelopeKeyManager)

                implementation(libs.voyager.koin)
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

                implementation(libs.composeUi)
//                implementation(libs.composeUiToolPreview)
                implementation(libs.androidx.activity.compose)

//    implementation(libs.compose.ui)
//    implementation(libs.compose.ui.tooling.preview)
                implementation(compose.material3)
                implementation(libs.koin.android)
//                implementation(project(":core:biometry"))
            }
        }
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        
        val jvmMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
            }
        }
    }
}

android {
    namespace = "com.mangala.antelope_base"
    val androidMinSdk: String by project
    val androidTargetSdk: String by project
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packagingOptions {
        resources.excludes += "META-INF/antelope_base_release.kotlin_module"
    }
}
