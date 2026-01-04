import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import java.util.Properties
import com.google.firebase.perf.plugin.FirebasePerfExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("native.cocoapods")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

kotlin {
    val currentFlavor: String by project

    tasks.matching { it.name == "syncComposeResourcesForIos" }.configureEach { enabled = false }
    tasks.matching { it.name == "syncPodComposeResourcesForIos" }.configureEach { enabled = false }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {

        }
    }

    cocoapods {
        summary = "Shared code for the sample"
        homepage = "https://github.com/JetBrains/compose-jb"
        ios.deploymentTarget = "15"
        podfile = project.file("../iosApp/Podfile")
        version = "1.0-SNAPSHOT"

        framework {
            baseName = "composeApp"
            linkerOpts += "-ld_classic"
            isStatic = true
        }

        // xcodeConfigurationToNativeBuildType["DebugUi"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["DebugCold"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["DebugPro"] = NativeBuildType.DEBUG
        // xcodeConfigurationToNativeBuildType["ReleaseUi"] = NativeBuildType.RELEASE
        xcodeConfigurationToNativeBuildType["ReleasePro"] = NativeBuildType.RELEASE
        xcodeConfigurationToNativeBuildType["ReleaseCold"] = NativeBuildType.RELEASE

        pod("FirebaseCore", linkOnly = true)
        pod("FirebaseRemoteConfig", linkOnly = true)

//        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
//        extraSpecAttributes["exclude_files"] = "['src/commonMain/resources/MR/**']"
    }
    
    jvm()
    
    sourceSets {
        val jvmMain by getting
        
        androidMain.dependencies {
            implementation(libs.mangala.browser.app)

            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            implementation(libs.androidx.core.splashscreen)

            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.messaging)
            implementation(libs.firebase.installations)
            implementation(libs.firebase.appcheck.playintegrity)
            implementation(libs.firebase.appcheck.debug)
            implementation(libs.firebase.remote.config)
            implementation(libs.firebase.perf)
            implementation(libs.crashkios.crashlytics)

            implementation("com.journeyapps:zxing-android-embedded:4.1.0")
            implementation("com.google.zxing:core:3.3.0")

            implementation("androidx.work:work-runtime-ktx:2.7.1")
            implementation("androidx.work:work-multiprocess:2.7.1")
            implementation("androidx.work:work-rxjava2:2.7.1")
        }
        iosMain.dependencies {
            implementation(libs.crashkios.crashlytics)
        }
        commonMain.dependencies {
            // Modules marked with api need to be accessed from iOS Swift code
            api(libs.imageLoader)
            api(projects.common.mokoresources)
            api(projects.core.notification)
            api(projects.features.browserTab)
            api(projects.features.iap)
            api(projects.libraries.kmpnotifier)
            api(projects.libraries.scanqr)
            api(projects.features.chains.antelopeBase)

            implementation(projects.antelope.antelopeKeyManager)
            implementation(projects.antelope.antelopeRpc)
            implementation(projects.common.ui)
            implementation(projects.common.utils)
            implementation(projects.core.address)
            implementation(projects.core.auth)
            implementation(projects.core.biometry)
            implementation(projects.core.hdwallet)
            implementation(projects.core.pin)
            implementation(projects.core.wallet)
            implementation(projects.core.ai)
            implementation(projects.core.twofactorauth)
            implementation(projects.data.local)
            implementation(projects.data.model)
            implementation(projects.data.remote)
            implementation(projects.domain)
            implementation(projects.features.auth)
            implementation(project(":features:browser_bridge_${currentFlavor}"))
            implementation(projects.features.browserBridgeBase)
            implementation(projects.features.browserTab)
            implementation(project(":features:chains:antelope_${currentFlavor}"))
            implementation(projects.features.chains.antelopeCreateAccount)
            implementation(projects.features.chains.antelopeQr)
            implementation(projects.features.chains.antelopeRam)
            implementation(projects.features.chains.bitcoin)
            implementation(projects.features.chains.evmcompatible)
            implementation(projects.features.conversationui)
//            implementation(project(":features:contract_wizard"))
            implementation(projects.features.cryptoPayment)
            implementation(projects.features.onboarding)
            implementation(projects.features.dex.uniswap)
            implementation(projects.features.eticket)
            implementation(project(":features:home_${currentFlavor}"))
            implementation(projects.features.homeBase)
            implementation(projects.features.manageaccount)
            implementation(project(":features:nft_${currentFlavor}"))
            implementation(projects.features.nftBase)
            implementation(projects.features.passkey)
            implementation(projects.features.portfolio)
            implementation(projects.features.qrcode)
            implementation(projects.features.receive)
            implementation(project(":features:send_${currentFlavor}"))
            implementation(projects.features.sendBase)
            implementation(projects.features.addressbook)
            implementation(projects.features.settings.contacts)
            implementation(projects.features.settings.currency)
            implementation(project(":features:settings:menu_${currentFlavor}"))
            implementation(projects.features.settings.menuBase)
            implementation(projects.features.settings.network)
            implementation(project(":features:swap_${currentFlavor}"))
            implementation(projects.features.swapBase)
            implementation(projects.features.transactionhistory)
            implementation(project(":features:transactionqr_${currentFlavor}"))
            implementation(project(":features:wallet_${currentFlavor}"))
            implementation(projects.features.walletTab)
            implementation(projects.features.walletconnect)
            implementation(projects.libraries.scanqr)
            implementation(projects.features.evmSnap)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(libs.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.voyager.core)
            implementation(libs.voyager.navigator)
//            implementation(libs.voyager.bottomSheetNavigator)
            implementation(libs.voyager.tabNavigator)
//                implementation(libs.voyager.transitions)
            implementation(libs.voyager.koin)

            implementation(libs.koin.core)

            implementation(libs.moko.resources)
            implementation(libs.moko.resources.compose)

            implementation(libs.hyperdrive.multiplatformx.api)

            api(libs.rinku)
            implementation(libs.rinku.compose.ext)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.koin.core)
            implementation(projects.common.utils)
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
            linkerOpts.add("-lsqlite3")
            export(libs.moko.resources)
            export(libs.rinku)
            export(projects.common.mokoresources)
            export(projects.libraries.scanqr)
            export(projects.libraries.kmpnotifier)
            export(projects.features.browserTab)
            export(projects.features.iap)
            export(projects.core.notification)
            export(projects.features.chains.antelopeBase)
        }
    }
}

val keystoreProperties = Properties().apply {
    load(rootProject.file("keystore.properties").inputStream())
}

android {
    val modeDimension = "mode"
    val environmentDimension = "environment"

    val androidMinSdk: String by project
    val androidTargetSdk: String by project
    val androidCompileSdk: String by project

    val versionCodeProperty = project.findProperty("versionCode")?.toString()?.toIntOrNull() ?: 26
    val versionNameEnv = System.getenv("VERSION_NAME") ?: "0.0.6"

    namespace = "com.mangala.wallet"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.mangala.wallet"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = versionCodeProperty
        versionName = versionNameEnv
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/antelope_base_release.kotlin_module"
        }
        jniLibs {
            pickFirst("lib/arm64-v8a/libsqlcipher.so")
            pickFirst("lib/x86/libsqlcipher.so")
            pickFirst("lib/x86_64/libsqlcipher.so")
            pickFirst("lib/armeabi-v7a/libsqlcipher.so")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["KEY_ALIAS"].toString()
            keyPassword = keystoreProperties["KEY_PASSWORD"].toString()
            storeFile = file(keystoreProperties["STORE_FILE"].toString())
            storePassword = keystoreProperties["STORE_PASSWORD"].toString()
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
            resValue("string", "app_name", "Mangala Debug")
            configure<FirebasePerfExtension> {
                setInstrumentationEnabled(false)
            }
        }
    }
    flavorDimensions += listOf(modeDimension, environmentDimension)
    productFlavors {
        create("pro") {
            dimension = modeDimension
            applicationId = "com.mangala.prowallet"
            isDefault = true
        }
        create("ui") {
            dimension = modeDimension
        }
        create("cold") {
            dimension = modeDimension
            applicationId = "com.mangala.coldwallet"
        }

        create("dev") {
            dimension = environmentDimension
            applicationIdSuffix = ".dev"
        }
        create("stg") {
            dimension = environmentDimension
        }
        create("uat") {
            dimension = environmentDimension
        }
        create("prod") {
            dimension = environmentDimension
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

configurations {
    getByName("jvmMainImplementation") {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-android") // #57 Fix some library loading coroutines for Android -> JVM getting Main dispatcher for Android -> exception
    }
    getByName("jvmTestImplementation") {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-android")
    }
}

compose.desktop {
    val currentFlavor: String by project
    val desktopBuildType: String by project

    application {
        mainClass = "com.mangala.wallet.MainKt"

        nativeDistributions {
            val packageNameTypeSuffix = if (desktopBuildType == "release") "" else "-$desktopBuildType"
            val packageNameSuffix = when(currentFlavor) {
                "cold" -> "-cold"
                "pro" -> "-pro"
                else -> ""
            }

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MangalaWallet$packageNameSuffix$packageNameTypeSuffix"
            packageVersion = "1.0.0"

            windows {
                menu = true
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "AF792DA6-2EA3-495A-95E5-C3C6CBCB9948"
            }

            macOS {
                // Use -Pcompose.desktop.mac.sign=true to sign and notarize.
                bundleID = when(currentFlavor) {
                    "cold" -> "com.mangala.coldwallet"
                    "pro" -> "com.mangala.prowallet"
                    else -> "com.mangala.wallet"
                }

            }
        }
    }
}

multiplatformResources {
    resourcesPackage.set("com.mangala.wallet")
    resourcesClassName.set("SharedMR")
}
