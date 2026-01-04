plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.sqldelight)
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
    iosArm64()
    iosSimulatorArm64()
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "local"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.data.model)
                implementation(projects.common.utils)
                implementation(projects.common.mokoresources)
                implementation(kotlin("stdlib-common"))
                implementation(libs.koin.core)
                implementation(libs.moko.resources)
                implementation(libs.multiplatformPaging.common)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.paging3)
                implementation(libs.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
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
            dependencies {
                implementation(libs.sqldelight.driver.ios)
                implementation(libs.stately.isolate)
                implementation(libs.stately.iso.collections)
            }
        }

        val jvmMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                implementation(libs.sqldelight.driver.desktop)
                implementation(libs.credentialSecureStorage)
            }
        }
    }
}

android {
    namespace = "com.mangala.wallet.local"
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
        create("MangalaWalletDatabase") {
            packageName.set("com.mangala.wallet.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/com/mangala/wallet/database/"))

//            generateAsync.set(false)
//            sourceFolders.set(listOf("sqldelight"))
//            dialect("sqlite:3.24")
//            sourceFolders = listOf("sqldelight")
//        deriveSchemaFromMigrations = true
        }
    }

}