import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-serialization")
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    id("com.codingfeline.buildkonfig")
    id("org.jetbrains.compose")
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm() {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "core_ai"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.ui)
                implementation(projects.common.mokoresources)
                implementation(projects.core.security)
                implementation(libs.moko.resources)

                implementation(libs.koin.core)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)

                implementation(libs.kotlinx.datetime)

                implementation(libs.ktorfit)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.napier)
                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.material)
                implementation(compose.runtime)
                
                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.android)

                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.okhttp)
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
                implementation(libs.ktor.client.cio)
//                implementation(project(":core:biometry"))
            }
        }

        val jvmMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }
    }
}

dependencies {
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.noop)
}

android {
    namespace = "com.mangala.wallet.core.ai"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

buildkonfig {
    packageName = "com.mangala.wallet.core.ai"

    defaultConfigs {
        buildConfigField(
            STRING,
            "GEMINI_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("GEMINI_API_KEY") ?: ""
        )
        buildConfigField(
            STRING,
            "OPENAI_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("OPENAI_API_KEY") ?: ""
        )
        buildConfigField(
            STRING,
            "HELICONE_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("HELICONE_API_KEY") ?: ""
        )
    }
}