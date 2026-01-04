package com.mangala.wallet

import android.os.StrictMode
import androidx.work.Configuration
import co.touchlab.crashkios.crashlytics.enableCrashlytics
import com.mangala.app.global.KoinWorkerFactory
import com.mangala.app.global.MangalaBaseApplication
import com.mangala.wallet.auth.FirebaseAppCheckManager
import com.mangala.wallet.core.notification.NotificationInitializer
import com.mangala.wallet.core.notification.initNotificationListener
import com.mangala.wallet.features.chains.antelope.domain.usecase.account.UpdatePurchasedAccountsStateUseCase
import com.mangala.wallet.utils.AppLifecycleObserver
import com.mangala.wallet.utils.remoteconfig.FirebaseRemoteConfigUtils
import com.wallet.iap.purchases.device.IapManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.logger.Level
import java.io.Serializable

class MangalaWalletApplication: MangalaBaseApplication(), Serializable, Configuration.Provider {

    @Transient
    lateinit var koin: KoinApplication

    override fun onCreate() {
        super.onCreate()

        koin = initKoin {
            androidLogger(level = Level.ERROR)
            androidContext(androidContext = this@MangalaWalletApplication)
            modules(getDiModule())
            initNotificationListener()
        }

        FirebaseAppCheckManager.init(this)
        initializeFirebaseRemoteConfig()

        NotificationInitializer.onApplicationStart()

        val updatePurchasedAccountsStateUseCase: UpdatePurchasedAccountsStateUseCase =
            koin.koin.get()
        IapManager.init(this, onBillingSetupFinished = {
            GlobalScope.launch {
                updatePurchasedAccountsStateUseCase()
            }
        })

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()  // Detect reading from disk on main thread
                .detectDiskWrites() // Detect writing to disk on main thread
                .detectNetwork()    // Detect network operations on main thread
                .penaltyLog()       // Log the violation in Logcat
//                .penaltyFlashScreen() // Flash the screen when a violation occurs
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects() // Detect unclosed SQLite objects
                .detectLeakedClosableObjects() // Detect other unclosed resources
                .penaltyLog()    // Log the violation in Logcat
//                .penaltyDeath()  // Crash the app (useful for debugging)
                .build()
        )

//        GlobalScope.launch {
//            GooglePlayIntegrityManager.initialize(this@MangalaWalletApplication)
//        }

//        AppInitializer.onApplicationStart()

        val appLifeCycleObserver: AppLifecycleObserver = koin.koin.get()
        appLifeCycleObserver.onAppOpened()

        initBrowserApplication()

        enableCrashlytics()
    }

    private fun initializeFirebaseRemoteConfig() {
        FirebaseRemoteConfigUtils.initialize()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()
    }
}