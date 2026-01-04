plugins {
    alias(libs.plugins.mangala.kotlin.multiplatform.feature)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

kotlin {
    androidTarget()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "evm_snap"
            isStatic = true
        }
    }


    jvm() {
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(libs.multiplatform.material.icons.core)
                implementation(compose.runtime)

                implementation(libs.krypto)
                implementation(libs.ktorfit)

                implementation(libs.kotlinx.datetime)
                implementation(project(":core:auth"))
                implementation(project(":common:utils"))
                implementation(project(":data:local"))
                implementation(project(":data:model"))
                implementation(project(":data:remote"))
                implementation(project(":domain"))
                implementation(project(":common:ui"))
                implementation(project(":common:utils"))
                implementation(project(":common:mokoresources"))
                implementation(projects.antelope.antelopeCore)
                implementation(project(":features:chains:antelope_base"))
                implementation(project(":features:chains:antelope_pro"))
                implementation(project(":antelope:antelope_key_manager"))
                implementation(libs.koin.core)
                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.koin)
                implementation(libs.multiplatformPaging.common)
                implementation(libs.bignum)
                implementation(libs.uuid)
                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)
                implementation(project(":features:chains:evmcompatible"))
                implementation(project(":core:hdwallet"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
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
        val androidMain by getting {
            kotlin.srcDirs("src/androidMain/kotlin")
            dependencies {
                implementation(libs.composeActivity)
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.functions)
                implementation(libs.integrity)
            }
        }
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
            kotlin.srcDirs("src/desktopMain/kotlin")
            dependencies {
                implementation(libs.credentialSecureStorage)
            }
        }
    }
}

android {
    namespace = "com.mangala.wallet.features.crypto_payment"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.mockk.common)
    androidTestImplementation(libs.mockk)
}