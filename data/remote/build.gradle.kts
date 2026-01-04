import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-serialization")
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    id("com.codingfeline.buildkonfig")
}

val ktorfitVersion = libs.versions.ktorfit.get()
val ktorfitKspVersion = libs.versions.ktorfitKsp.get()

//configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
//    version = ktorfitVersion
//}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
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
            baseName = "remote"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.utils)
                implementation(projects.core.auth)
                implementation(projects.data.model)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.ktorfit)
                implementation(libs.kotlinx.coroutines.core)
//                implementation(libs.ktor.client.core)
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
                implementation(libs.koin.core)
                implementation(libs.stately.common)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            kotlin.srcDirs("src/androidMain/kotlin")
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.koin.android)
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

//        val jvmMain by getting {
//            dependencies {
//            }
//        }

        val jvmMain by getting {
            kotlin.srcDirs("src/desktopMain/kotlin")
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }
    }
}

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    namespace = "com.mangala.wallet.remote"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    dependencies {
        debugImplementation(libs.chucker)
        releaseImplementation(libs.chucker.noop)
    }
}

buildkonfig {
    packageName = "com.mangala.wallet.remote"
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

        buildConfigField(
            STRING,
            "COVALENTHQ_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("COVALENTHQ_API_KEY") ?: ""
        )

        buildConfigField(
            STRING,
            "COINGECKO_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("COINGECKO_API_KEY") ?: ""
        )

        buildConfigField(
            STRING,
            "ALCHEMY_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("ALCHEMY_API_KEY") ?: ""
        )

        buildConfigField(
            STRING,
            "MORALIS_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("MORALIS_API_KEY") ?: ""
        )
    }
}