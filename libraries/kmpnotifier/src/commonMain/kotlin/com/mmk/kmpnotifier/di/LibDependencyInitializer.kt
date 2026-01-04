/*
 * Copyright 2023 Mirzamehdi Karimov
 * Copyright 2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file has been modified from the original KMPNotifier library.
 */
package com.mmk.kmpnotifier.di


import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module


internal object LibDependencyInitializer {
    var koinApp: KoinApplication? = null
        private set


    fun initialize(configuration: NotificationPlatformConfiguration) {
        if (isInitialized()) return
        val configModule = module {
            single { configuration }
        }
        koinApp = koinApplication {
            modules(configModule + platformModule)
        }.also {
            it.koin.onLibraryInitialized()
        }

    }

    fun isInitialized() = koinApp != null


}

private fun Koin.onLibraryInitialized() {
    println("Library is initialized")
    val permissionUtil by inject<PermissionUtil>()
    val platform by inject<Platform>()
    val configuration by inject<NotificationPlatformConfiguration>()

    get<PushNotifier>() //This will make sure that that when lib is initialized, init method is called

    when (platform) {
        Platform.Android, Platform.Desktop -> Unit //In Android platform permission should be asked in activity
        Platform.Ios -> {
            val askNotificationPermissionOnStart =
                (configuration as? NotificationPlatformConfiguration.Ios)?.askNotificationPermissionOnStart
                    ?: true
            if (askNotificationPermissionOnStart) permissionUtil.askNotificationPermission()
        }

        Platform.Web -> {
            val askNotificationPermissionOnStart =
                (configuration as? NotificationPlatformConfiguration.Web)?.askNotificationPermissionOnStart
                    ?: true
            if (askNotificationPermissionOnStart) permissionUtil.askNotificationPermission()
        }

    }
}

