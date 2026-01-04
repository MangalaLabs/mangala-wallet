package com.mangala.wallet

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.mangala.wallet.features.receive.presentation.ReceiveTokenScreen
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.scanqr.QRCodeReceiveComposeView
import com.mangala.wallet.ui.ConfirmTransactionViewIOS
import com.mangala.wallet.ui.NetworkComposeView
import com.mangala.wallet.ui.SignPersonalMessageViewIOS
import com.mangala.wallet.ui.SwitchChainViewIOS
import com.mangala.wallet.ui.UnlockPinComposeView
import com.mangala.wallet.ui.imageloader.commonConfig
import com.mangala.wallet.viewmodel.ApplicationViewModel
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun MainViewController(viewModel: ApplicationViewModel) = ComposeUIViewController {
    CompositionLocalProvider(
        LocalImageLoader provides generateImageLoader(),
    ) {
        App(viewModel)
    }
}

fun unlockScreenController(isSuccess: (Boolean) -> Unit) = ComposeUIViewController { UnlockPinComposeView(isSuccess) }

fun confirmTransactionController(url: String,
                                 accountId: String,
                                 coinDecimals: Long,
                                 chainId: Long,
                                 callbackId: Long,
                                 value: String,
                                 recipient: String,
                                 payload: String,
                                 nonce: Long,
                                 isLegacyTransaction: Boolean,
                                 onSignMessageFail: () -> Unit,
                                 onSignMessageSuccessful: (callbackId: Long, signHex: String) -> Unit,
                                 onConfirm: (isOpenPin: Boolean) -> Unit,
                                 onDecline: () -> Unit) =
    ComposeUIViewController {
        ConfirmTransactionViewIOS(
            url = url,
            accountId = accountId,
            coinDecimals = coinDecimals,
            chainId = chainId,
            callbackId = callbackId,
            value = value,
            recipient = recipient,
            payload = payload,
            nonce = nonce,
            onSignMessageFail = onSignMessageFail,
            isLegacyTransaction = isLegacyTransaction,
            onSignMessageSuccessful = onSignMessageSuccessful,
            onConfirm = { onConfirm(it) },
            onDecline = { onDecline() }
        )
    }

fun signPersonalMessageController(url: String,
                                  callbackId: Long,
                                  message: ByteArray?,
                                  onSign: (message: String) -> Unit,
                                  onConfirm: (isOpenPin: Boolean) -> Unit,
                                  onDecline: () -> Unit) =
    ComposeUIViewController {
        SignPersonalMessageViewIOS(
            url = url,
            callbackId = callbackId,
            message = message,
            onSign = { onSign(it)},
            onConfirm = { onConfirm(it) },
            onDecline = { onDecline() }
        )
    }

fun switchChainController(currentChainId: Long,
                          newChainId: Long,
                          onConfirm: () -> Unit,
                          onDecline: () -> Unit,) =
    ComposeUIViewController {
        SwitchChainViewIOS(
            currentChainId = currentChainId,
            newChainId = newChainId,
            onConfirm = onConfirm,
            onDecline = onDecline
        )
    }

fun selectNetworkController(chainIdCallback: (Long) -> Unit) =
    ComposeUIViewController {
        NetworkComposeView(chainIdCallback)
    }

fun scanQrCodeReceiveController(accountId: String, networkType: NetworkType, initialBlockchainUid: String?, onBackPressed: () -> Unit) =
    ComposeUIViewController {
        QRCodeReceiveComposeView(ReceiveTokenScreen(accountId, address = null, networkType, initialBlockchainUid, onBackPressed))
    }

private fun generateImageLoader(): ImageLoader {
    return ImageLoader {
        commonConfig()
        components {
            setupDefaultComponents()
        }
        interceptor {
            // Add bitmap memory cache
            bitmapMemoryCacheConfig {
                maxSize(32 * 1024 * 1024) // 32MB
            }
            // Add image memory cache
            imageMemoryCacheConfig {
                maxSize(50)
            }
            // Add painter memory cache
            painterMemoryCacheConfig {
                maxSize(50)
            }
            memoryCacheConfig {
                // Set the max size to 25% of the app's available memory.
                maxSizePercent(0.25)
            }
            diskCacheConfig {
                directory(getCacheDir().toPath().resolve("image_cache"))
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun getCacheDir(): String {
    return NSFileManager.defaultManager.URLForDirectory(
        NSCachesDirectory,
        NSUserDomainMask,
        null,
        true,
        null,
    )!!.path.orEmpty()
}
