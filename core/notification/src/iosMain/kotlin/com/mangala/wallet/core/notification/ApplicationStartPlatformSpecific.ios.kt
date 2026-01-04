package com.mangala.wallet.core.notification

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

actual class ApplicationStartPlatformSpecific {
    actual fun onApplicationStartPlatformSpecific() {
        println("onApplicationStartPlatformSpecific iOS")
        NotifierManager.initialize(NotificationPlatformConfiguration.Ios(showPushNotification = true))
    }
}