plugins {
    alias(libs.plugins.mangala.kotlin.multiplatform.feature)
    alias(libs.plugins.sqldelight)
}

kotlin {
    val currentFlavor: String by project

    androidTarget()

    jvm("desktop") {
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
            baseName = "features_conversation_ui"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.antelope.antelopeBalance)
                implementation(projects.antelope.antelopeRpc)
                implementation(projects.common.mokoresources)
                implementation(projects.common.ui)
                implementation(projects.common.utils)
                implementation(projects.core.ai)
                implementation(projects.core.auth)
                implementation(projects.core.biometry)
                implementation(projects.core.pin)
                implementation(projects.core.security)
                implementation(projects.core.websocketChat)
                implementation(projects.core.wallet)
                implementation(projects.data.model)
                implementation(projects.domain)
                implementation(projects.features.addressbook)
                implementation(projects.features.auth)
                implementation(projects.features.chains.antelopeBase)
                implementation(projects.features.chains.antelopeCreateAccount)
                implementation(projects.features.chains.evmcompatible)
                implementation(projects.features.receive)
                implementation(projects.features.sendBase)
                implementation(projects.libraries.scanqr)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.multiplatformSettings.core)
                implementation(libs.uuid)

                implementation(libs.stately.common)
                implementation(libs.koin.core)

                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                implementation(compose.runtime)

                implementation(libs.voyager.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.koin)

                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.websockets)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.paging3)
                implementation(libs.bignum)
                implementation(libs.koin.core)
                implementation(libs.koin.androidx.compose)
                implementation(compose.material3)
                implementation(libs.imageLoader)

                implementation(libs.multiplatform.markdown.renderer.m3)
                implementation(libs.multiplatform.markdown.renderer.code)

                implementation(libs.calf.file.picker)
                implementation(libs.napier)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.mockk.common)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.material)
                implementation(libs.sqldelight.driver.android)
                implementation(libs.androidx.activity.compose)
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
                implementation(libs.ktor.client.darwin)
                implementation(libs.sqldelight.driver.ios)
            }
        }

        val desktopMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                implementation(libs.sqldelight.driver.desktop)
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(libs.mockk)
            }
        }
    }
}

android {
    namespace = "com.mangala.wallet.features.conversationui"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

sqldelight {
    linkSqlite = true

    databases {
        create("ConversationUiDatabase") {
            packageName.set("com.mangala.wallet.features.conversationui.database")
        }
    }
}