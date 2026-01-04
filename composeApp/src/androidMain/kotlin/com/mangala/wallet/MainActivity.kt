package com.mangala.wallet

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.mangala.wallet.biometry.BiometryAuthenticator
import com.mangala.wallet.features.chains.antelope.domain.usecase.account.UpdatePurchasedAccountsStateUseCase
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.ui.imageloader.commonConfig
import com.mmk.kmpnotifier.permission.permissionUtil
import com.seiko.imageloader.ImageLoader
import com.wallet.iap.purchases.OpenIapScreen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import kotlinx.coroutines.launch
import okio.Path.Companion.toOkioPath
import com.google.zxing.integration.android.IntentIntegrator
import com.mangala.features.browser.OpenBrowser
import com.mangala.wallet.auth.BuildConfig
import com.mangala.wallet.features.addressbook.presentation.avatar.ProvideImageLoader
import com.mangala.wallet.passkey.PasskeyManager
import com.mangala.wallet.passkey.effect.BindPasskeyManagerEffect
import com.mangala.wallet.viewmodel.ApplicationViewModel
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.theolm.rinku.RinkuInit
import dev.theolm.rinku.compose.ext.Rinku

class MainActivity : AppCompatActivity(), KoinComponent {

    private val applicationViewModel: ApplicationViewModel by inject()
    private val bio: BiometryAuthenticator by inject()
    private val passkeyManager: PasskeyManager by inject()

    private val scanQRCode: ScanQRCode by inject()
    private val openIapScreen: OpenIapScreen by inject()
    private val openBrowser: OpenBrowser by inject()

    // TODO: Use plugin pattern so that we can build the app on variants other than pro
    private val updatePurchasedAccountsStateUseCase: UpdatePurchasedAccountsStateUseCase by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        NotifierManager.onCreateOrOnNewIntent(intent)
        // Keep the splash screen on-screen until the UI state is loaded. This condition is
        // evaluated each time the app needs to be redrawn so it should be fast to avoid blocking
        // the UI.
//        splashScreen.setKeepOnScreenCondition {
//            when (uiState) {
//                Loading -> true
//                is SnapshotApplyResult.Success -> false
//            }
//        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.TRANSPARENT,
//                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                Color.TRANSPARENT,
//                Color.TRANSPARENT,
            )
        )

//        applicationViewModel.lifecycle.removeFromParent()
//        root.addChild(applicationViewModel.lifecycle)

        setContent {
            CompositionLocalProvider(
                LocalImageLoader provides generateImageLoader(),
            ) {
                BindPasskeyManagerEffect(passkeyManager)
                LaunchedEffect(Unit) {
                    bio.bind(lifecycle = this@MainActivity.lifecycle, fragmentManager = this@MainActivity.supportFragmentManager)
                    openBrowser.bind(
                        lifecycle = this@MainActivity.lifecycle,
                        fragmentManager = this@MainActivity.supportFragmentManager
                    )
                    scanQRCode.bind(
                        lifecycle = this@MainActivity.lifecycle,
                        fragmentManager = this@MainActivity.supportFragmentManager
                    )
                    openIapScreen.bind(
                        lifecycle = this@MainActivity.lifecycle,
                        fragmentManager = this@MainActivity.supportFragmentManager
                    )
                }

                Rinku {
                    MainView(applicationViewModel)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val permissionUtil by permissionUtil()
        permissionUtil.askNotificationPermission()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            updatePurchasedAccountsStateUseCase()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {

            } else {
//                applicationViewModel.onScanResult(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        NotifierManager.onCreateOrOnNewIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Workaround for a crash we could not reproduce: https://console.firebase.google.com/project/droidcon-148cc/crashlytics/app/android:co.touchlab.droidcon.london/issues/8c559569e69164d7109bd6b1be99ade5
//        if (root.hasChild(applicationViewModel.lifecycle)) { // TODO: Reenable
//            root.removeChild(applicationViewModel.lifecycle)
//        }
    }

    private fun generateImageLoader(): ImageLoader {
        return ImageLoader {
            commonConfig()
            components {
                setupDefaultComponents(applicationContext)
            }
            interceptor {
                // Add bitmap memory cache
                bitmapMemoryCacheConfig {
                    maxSizePercent(applicationContext, 0.25)
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
                    maxSizePercent(applicationContext, 0.25)
                }
                diskCacheConfig {
                    directory(cacheDir.resolve("image_cache").toOkioPath())
                    maxSizeBytes(512L * 1024 * 1024) // 512MB
                }
            }
        }
    }
}