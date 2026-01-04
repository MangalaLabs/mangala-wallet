import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.mangala.kotlin.multiplatform.feature)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    id("com.codingfeline.buildkonfig")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"
            }
        }
    }

    jvm {
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
            baseName = "bitcoin"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ai)
            implementation(projects.common.mokoresources)
            implementation(projects.common.ui)
            implementation(projects.common.utils)
            implementation(projects.core.address)
            implementation(projects.core.cryptography)
            implementation(projects.core.hdwallet)
            implementation(projects.data.model)
            implementation(projects.data.remote)
            implementation(projects.data.local)
            implementation(projects.domain)

            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.runtime)

            implementation(libs.cryptohash)
            implementation(libs.koin.core)
            implementation(libs.krypto)
            implementation(libs.okio)
            implementation(libs.secp256k1)
            implementation(libs.bitcoin.kmp)
            implementation(libs.lightning.kmp)

            implementation(libs.koin.core)
            implementation(libs.voyager.core)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.bottomSheetNavigator)
            implementation(libs.voyager.tabNavigator)
//                implementation(libs.voyager.transitions)
            implementation(libs.voyager.koin)

            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.paging3)

            implementation(libs.ktorfit)

            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.ktor.utils)

            implementation(libs.bignum)

            implementation(libs.moko.resources)
            implementation(libs.moko.resources.compose)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(libs.mockk.common)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.driver.android)

            implementation(libs.ktor.network)
            implementation(libs.ktor.network.tls)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.driver.ios)
        }
        jvmMain.dependencies {
            implementation(libs.sqldelight.driver.desktop)
            implementation(libs.secp256k1.jvm)
        }
    }
}

sqldelight {
    linkSqlite = true

    databases {
        create("BitcoinDatabase") {
            packageName.set("com.mangala.wallet.features.chains.bitcoin.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/com/mangala/wallet/features/chains/bitcoin"))
        }
    }
}

android {
    namespace = "com.mangala.wallet.features.chains.bitcoin"
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
    packageName = "com.mangala.wallet.features.chains.bitcoin"
    defaultConfigs {

    }
}