import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.mangala.kotlin.multiplatform.feature)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    id("com.codingfeline.buildkonfig")
}

val ktorfitVersion = libs.versions.ktorfit.get()
val ktorfitKspVersion = libs.versions.ktorfitKsp.get()

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
            baseName = "evmcompatible"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.bignum)
                implementation(libs.cryptohash)
                implementation(libs.krypto)
                implementation(libs.okio)
                implementation(libs.secp256k1)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)

                implementation(project(":common:mokoresources"))
                implementation(project(":common:ui"))
                implementation(project(":common:utils"))
                implementation(project(":features:chains"))
                implementation(project(":features:qrcode"))

                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(libs.multiplatform.material.icons.core)
                implementation(compose.runtime)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)

                implementation(libs.ktorfit)

                implementation(project(":data:model"))
                implementation(project(":data:remote"))
                implementation(project(":domain"))
                implementation(project(":core:hdwallet"))
                implementation(project(":core:ai"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(project(":common:test"))
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.mockk.common)
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies{
                implementation(libs.ktor.client.ios)
                implementation(libs.ktor.client.darwin)
            }
        }
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
        val jvmMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(libs.secp256k1.jvm)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.mockk)
            }
        }
    }
}

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    namespace = "com.mangala.wallet.features.chains.evmcompatible"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

buildkonfig {
    packageName = "com.mangala.wallet.features.chains.evmcompatible"
    defaultConfigs {
        buildConfigField(
            STRING,
            "INFURA_SECRET_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("INFURA_SECRET_KEY") ?: ""
        )

        buildConfigField(
            STRING,
            "INFURA_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("INFURA_API_KEY") ?: ""
        )
    }
}