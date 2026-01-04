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
            baseName = "features_address_book"
        }
//        it.compilations.all {
//            cinterops {
//                create("Security")
//                create("Foundation")
//            }
//        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common:ui"))
                implementation(project(":common:utils"))
                implementation(project(":common:mokoresources"))
                implementation(project(":core:biometry"))
                implementation(project(":core:pin"))
                implementation(project(":core:security"))
                implementation(project(":data:local"))
                implementation(project(":data:model"))
                implementation(project(":core:twofactorauth"))
                implementation(project(":core:ai"))
                implementation(project(":libraries:scanqr"))
                implementation(project(":features:chains:bitcoin"))
                implementation(project(":features:chains:evmcompatible"))
                implementation(project(":features:chains:antelope_base"))
                implementation(projects.antelope.antelopeRpc)
                implementation(projects.data.remote)
                implementation(projects.domain)

                implementation(libs.uuid)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.multiplatformSettings.core)

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
                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.paging3)
                implementation(libs.multiplatformPaging.common)
                implementation(libs.multiplatformPaging.composeui)
                implementation(libs.bignum)
                implementation(libs.koin.core)
                implementation(libs.koin.androidx.compose)
                implementation(compose.material3)
                implementation(libs.imageLoader)
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
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.material)
                implementation(libs.sqldelight.driver.android)
                implementation(libs.androidx.activity.compose)
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
    namespace = "com.mangala.wallet.features.addressbook"
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
        create("AddressBookDatabase") {
            packageName.set("com.mangala.wallet.features.addressbook.database")
//            schemaOutputDirectory.set(file("src/commonMain/sqldelight/com/mangala/wallet/features/addressbook/database/"))
        }
    }
}