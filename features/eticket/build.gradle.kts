plugins {
    alias(libs.plugins.mangala.kotlin.multiplatform.feature)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.sqldelight)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

val ktorfitVersion = libs.versions.ktorfit.get()
val ktorfitKspVersion = libs.versions.ktorfitKsp.get()

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
                implementation(libs.bignum)
                implementation(libs.cryptohash)
                implementation(libs.krypto)
                implementation(libs.okio)
                implementation(libs.secp256k1)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)

                implementation(projects.common.mokoresources)
                implementation(projects.common.ui)
                implementation(projects.common.utils)
                implementation(project(":core:pin"))

                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(libs.multiplatform.material.icons.core)
                implementation(compose.runtime)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.ktorfit)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)
                implementation(libs.napier)

                implementation(project(":data:model"))
                implementation(project(":data:remote"))
                implementation(project(":data:local"))
                implementation(project(":domain"))
                implementation(project(":core:hdwallet"))
                implementation(project(":features:chains:evmcompatible"))
                implementation(project(":features:chains:antelope_base"))
                implementation(project(":antelope:antelope_rpc"))
                implementation(project(":antelope:antelope_key_manager"))
                implementation(projects.antelope.antelopeCore)
                implementation(project(":core:address"))

                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.paging3)
                implementation(libs.multiplatformPaging.common)
                implementation(libs.multiplatformPaging.composeui)
                implementation("com.soywiz.korlibs.krypto:krypto:2.4.8")

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.mockk.common)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.driver.android)
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
            dependencies {
                implementation(libs.sqldelight.driver.ios)
            }
        }
        val jvmMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(libs.secp256k1.jvm)
                implementation(libs.sqldelight.driver.desktop)
                implementation(libs.credentialSecureStorage)
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
    namespace = "com.mangala.eticket"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

sqldelight {
    linkSqlite = true

    databases {
        create("ETicketDatabase") {
            packageName.set("com.mangala.eticket.database")
        }
    }

}