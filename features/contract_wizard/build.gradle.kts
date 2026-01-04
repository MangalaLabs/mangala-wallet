plugins {
    alias(libs.plugins.mangala.kotlin.multiplatform.feature)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
}

val ktorfitVersion = libs.versions.ktorfit.get()
val ktorfitKspVersion = libs.versions.ktorfitKsp.get()

configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    version = ktorfitVersion
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
            baseName = "contract_wizard"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.mokoresources)
                implementation(projects.common.ui)
                implementation(project(":data:remote"))
                implementation(project(":data:model"))
                implementation(project(":domain"))
                implementation(project(":features:browser_bridge_base"))
                implementation(project(":features:chains:evmcompatible"))

                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.koin)

                implementation(libs.kotlinx.datetime)

                implementation(libs.bignum)

                implementation(libs.kotlinx.serialization.core)

                implementation(libs.ktorfit)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
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
    namespace = "com.mangala.wallet.features.contract_wizard"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
