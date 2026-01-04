import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.mangala.kotlin.multiplatform.feature)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    id("com.codingfeline.buildkonfig")
}

val ktorfitVersion = libs.versions.ktorfit.get()
val ktorfitKspVersion = libs.versions.ktorfitKsp.get()

kotlin {
    androidTarget()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "antelope_base"
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
                implementation(compose.material3)
                implementation(compose.runtime)

                implementation(libs.krypto)
                implementation(libs.ktorfit)

                implementation(libs.bitcoinj)
                implementation(libs.kotlinx.datetime)

                implementation(projects.antelope.antelopeAction)
                implementation(projects.antelope.antelopeBalance)
                implementation(projects.antelope.antelopeCore)
                implementation(projects.antelope.antelopeKeyManager)
                implementation(projects.antelope.antelopeRpc)
                implementation(projects.common.mokoresources)
                implementation(projects.common.test)
                implementation(projects.common.ui)
                implementation(projects.common.utils)
                implementation(projects.core.ai)
                implementation(projects.core.auth)
                implementation(projects.core.cryptography)
                implementation(projects.data.local)
                implementation(projects.data.model)
                implementation(projects.data.remote)
                implementation(projects.domain)
                implementation(projects.libraries.kmpnotifier)

                implementation(libs.koin.core)
                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
//                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.multiplatformPaging.common)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.paging3)

                implementation(libs.firebase.kmp.remote.config)
                implementation(libs.firebase.kmp.functions)
                implementation(libs.firebase.kmp.installations)

                implementation(libs.uuid)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.bignum)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)
                implementation(libs.mokoParcelize)
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
                implementation(libs.firebase.perf)
                implementation(libs.koin.android)
//                implementation(libs.composeUi)
                implementation(libs.composeActivity)
//                implementation(libs.compose.material3)
//                implementation(libs.koin.android)

                implementation(libs.sqldelight.driver.android)

                implementation(project.dependencies.platform(libs.firebase.bom))
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
                implementation(libs.sqldelight.driver.ios)
            }
        }
        val jvmMain by getting {
            kotlin.srcDirs("src/desktopMain/kotlin")
            dependencies {
                implementation(libs.sqldelight.driver.desktop)
                implementation(libs.credentialSecureStorage)
            }
        }
    }
}

android {
    namespace = "com.mangala.wallet.features.chains.antelope_base"
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

sqldelight {
    linkSqlite = true

    databases {
        create("AntelopeDatabase") {
            packageName.set("com.mangala.wallet.features.chains.antelope_base.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/com/mangala/wallet/features/chains/antelope_base"))
        }
    }
}

buildkonfig {
    packageName = "com.mangala.wallet.features.chains.antelope_base"
    defaultConfigs {
        buildConfigField(
            STRING,
            "CREATE_ACCOUNT_FUNCTION",
            gradleLocalProperties(rootDir, providers).getProperty("CREATE_ACCOUNT_FUNCTION") ?: ""
        )
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "21"
}
