import java.io.FileInputStream
import java.util.Properties

rootProject.name = "MangalaWallet"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

val prop = Properties().apply {
    load(FileInputStream(File(rootProject.projectDir, "local.properties")))
}
val githubUsername: String = prop.getProperty("GITHUB_ACTOR")
val githubToken: String = prop.getProperty("GITHUB_TOKEN")

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://jitpack.io")
        gradlePluginPortal()
        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://maven.pkg.github.com/trustwallet/wallet-core")
                    credentials {
                        username = githubUsername
                        password = githubToken
                    }
                }
            }
            filter {
                includeGroup("com.trustwallet")
            }
        }
        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://maven.pkg.github.com/MangalaLabs/mangala-dependencies")
                    credentials {
                        username = githubUsername
                        password = githubToken
                    }
                }
            }
            filter {
                includeGroup("com.mangala")
                includeGroup("com.mangala.wallet.browser")
            }
        }
        exclusiveContent {
            forRepository {
                mavenLocal()
            }
            filter {
                includeGroup("com.mangala.wallet")
            }
        }
    }
}

include(":composeApp")

include(":antelope")
include(":antelope:antelope_action")
include(":antelope:antelope_balance")
include(":antelope:antelope_base")
include(":antelope:antelope_core")
include(":antelope:antelope_key_manager")
include(":antelope:antelope_rpc")
include(":common")
include(":common:mokoresources")
include(":common:test")
include(":common:ui")
include(":common:utils")
include(":core")
include(":core:address")
include(":core:auth")
include(":core:biometry")
include(":core:cryptography")
include(":core:hdwallet")
include(":core:notification")
include(":core:pin")
include(":core:wallet")
include(":core:websocket-chat")
include(":core:ai")
include(":core:security")
include(":core:twofactorauth")
include(":data")
include(":data:local")
include(":data:model")
include(":data:remote")
include(":domain")
include(":features")
include(":features:auth")
include(":features:browser_bridge_base")
//include(":features:browser_bridge_cold")
include(":features:browser_bridge_pro")
//include(":features:browser_bridge_ui")
include(":features:browser_tab")
include(":features:chains")
include(":features:chains:antelope_base")
//include(":features:chains:antelope_cold")
include(":features:chains:antelope_create_account")
include(":features:chains:antelope_pro")
include(":features:chains:antelope_qr")
include(":features:chains:antelope_ram")
//include(":features:chains:antelope_ui")
include(":features:chains:bitcoin")
include(":features:chains:evmcompatible")
include(":features:chart")
//include(":features:contract_wizard")
include(":features:conversationui")
include(":features:crypto_payment")
include(":features:dex")
include(":features:dex:uniswap")
include(":features:eticket")
include(":features:home_base")
//include(":features:home_cold")
include(":features:home_pro")
//include(":features:home_ui")
include(":features:iap")
include(":features:manageaccount")
include(":features:nft_base")
//include(":features:nft_cold")
include(":features:nft_pro")
//include(":features:nft_ui")
include(":features:onboarding")
include(":features:passkey")
include(":features:portfolio")
include(":features:qrcode")
include(":features:receive")
include(":features:send_base")
//include(":features:send_cold")
include(":features:send_pro")
//include(":features:send_ui")
include(":features:settings")
include(":features:settings:contacts")
include(":features:settings:currency")
include(":features:settings:menu_base")
//include(":features:settings:menu_cold")
include(":features:settings:menu_pro")
//include(":features:settings:menu_ui")
include(":features:settings:network")
include(":features:swap_base")
//include(":features:swap_cold")
include(":features:swap_pro")
//include(":features:swap_ui")
include(":features:transactionhistory")
//include(":features:transactionqr_cold")
include(":features:transactionqr_pro")
//include(":features:transactionqr_ui")
//include(":features:wallet_cold")
include(":features:wallet_pro")
include(":features:wallet_tab")
include(":features:walletconnect")
include(":libraries")
include(":libraries:chart")
include(":libraries:kmpnotifier")
include(":libraries:scanqr")
include(":libraries:walletconnect")
include(":features:evm_snap")
include(":features:addressbook")
