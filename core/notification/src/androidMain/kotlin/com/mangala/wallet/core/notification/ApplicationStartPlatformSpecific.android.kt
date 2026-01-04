package com.mangala.wallet.core.notification

import android.content.Context
import androidx.startup.AppInitializer
import com.mmk.kmpnotifier.di.ContextInitializer
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

actual class ApplicationStartPlatformSpecific(private val applicationContext: Context) {
    actual fun onApplicationStartPlatformSpecific() {
        val channelId = "fcm_default_channel"

        // Fixes the crash when notification, because we have androidx app startup auto init disabled
        // https://github.com/mirzemehdi/KMPNotifier/issues/53
        AppInitializer.getInstance(applicationContext)
            .initializeComponent(ContextInitializer::class.java)

        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.baseline_android_24,
                notificationIconColorResId = R.color.color_notification_background,
                showPushNotification = true,
                notificationChannelData = NotificationPlatformConfiguration.Android.NotificationChannelData(
                    id = channelId,
                    name = "Default Channel"
                )
            )
        )
    }
}