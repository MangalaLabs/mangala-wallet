
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-serialization")
    kotlin("plugin.serialization")
    id("com.codingfeline.buildkonfig")
}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    kotlinOptions.jvmTarget = "21"
////    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
//}

kotlin {
    androidTarget()

    jvm("desktop") {
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
            baseName = "model"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.bignum)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(projects.common.utils)
                implementation(libs.moko.resources)
                implementation(projects.common.mokoresources)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(projects.common.test)
            }
        }
        val androidMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
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
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.mockk.common)
                implementation(libs.mockk)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        val desktopMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
            }
        }
    }
}

android {
    namespace = "com.mangala.wallet.model"
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
    packageName = "com.mangala.wallet.model"
    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "INFURA_SECRET_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("INFURA_SECRET_KEY") ?: ""
        )

        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "INFURA_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("INFURA_API_KEY") ?: ""
        )
    }
}

dependencies {
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.mockk.common)
    androidTestImplementation(libs.mockk)
}