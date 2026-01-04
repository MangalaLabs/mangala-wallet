package com.mangala.wallet

import MainView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.mangala.wallet.di.initKoin
import com.mangala.wallet.utils.getBuildType
import com.mangala.wallet.utils.currentFlavor
import com.mangala.wallet.viewmodel.ApplicationViewModel
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.ImageLoaderConfigBuilder
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberAsyncImagePainter
import java.io.File
import okio.Path.Companion.toOkioPath
//import MainView

import org.koin.core.Koin
import org.koin.core.context.KoinContextHandler
import java.util.Locale

lateinit var koin: Koin

fun main() {
    val buildType = getBuildType().name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val currentFlavor = currentFlavor.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    singleWindowApplication(
        title = "Mangala Wallet $currentFlavor $buildType",
        state = WindowState(width = 1280.dp, height = 768.dp),
        icon = BitmapPainter(useResource("ic_launcher.png", ::loadImageBitmap)),
    ) {
        if (KoinContextHandler.getOrNull() == null) {
            initKoin(enableNetworkLogs = true) { }
        }
        val koin = KoinContextHandler.get()
        val viewModel: ApplicationViewModel = koin.get()
        CompositionLocalProvider(
            LocalImageLoader provides generateImageLoader()
        ) {
            MainView(viewModel)
        }
    }
}

fun ImageLoaderConfigBuilder.commonConfig() {
    interceptor {
//        addInterceptor(BlurInterceptor())
    }
}

private fun generateImageLoader(): ImageLoader {
    return ImageLoader {
//        commonConfig()
        components {
            // add(ImageIODecoder.Factory())
            setupDefaultComponents(imageScope)
        }
        interceptor {
            memoryCacheConfig {
                // Set the max size to 25% of the app's available memory.
                maxSizePercent(0.25)
            }
            diskCacheConfig {
                directory(getCacheDir().resolve("image_cache").toOkioPath())
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }
}

enum class OperatingSystem {
    Android, IOS, Windows, Linux, MacOS, Unknown
}

private val currentOperatingSystem: OperatingSystem
    get() {
        val operSys = System.getProperty("os.name").lowercase()
        return if (operSys.contains("win")) {
            OperatingSystem.Windows
        } else if (operSys.contains("nix") || operSys.contains("nux") ||
            operSys.contains("aix")
        ) {
            OperatingSystem.Linux
        } else if (operSys.contains("mac")) {
            OperatingSystem.MacOS
        } else {
            OperatingSystem.Unknown
        }
    }

private fun getCacheDir() = when (currentOperatingSystem) {
    OperatingSystem.Windows -> File(System.getenv("AppData"), "$ApplicationName/cache")
    OperatingSystem.Linux -> File(System.getProperty("user.home"), ".cache/$ApplicationName")
    OperatingSystem.MacOS -> File(System.getProperty("user.home"), "Library/Caches/$ApplicationName")
    else -> throw IllegalStateException("Unsupported operating system")
}

private val ApplicationName = "MangalaWallet"