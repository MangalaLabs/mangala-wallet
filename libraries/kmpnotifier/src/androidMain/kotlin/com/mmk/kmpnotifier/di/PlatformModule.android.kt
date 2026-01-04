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

import android.content.Context
import androidx.startup.Initializer
import com.mmk.kmpnotifier.firebase.FirebasePushNotifierImpl
import com.mmk.kmpnotifier.notification.AndroidNotifier
import com.mmk.kmpnotifier.notification.NotificationChannelFactory
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.AndroidMockPermissionUtil
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal lateinit var applicationContext: Context
    private set

public class ContextInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        applicationContext = context.applicationContext
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}


internal actual val platformModule = module {
    factory { Platform.Android } bind Platform::class
    single { applicationContext }
    factoryOf(::AndroidMockPermissionUtil) bind PermissionUtil::class
    factory {
        val configuration =
            get<NotificationPlatformConfiguration>() as NotificationPlatformConfiguration.Android
        AndroidNotifier(
            context = get(),
            androidNotificationConfiguration = configuration,
            notificationChannelFactory = NotificationChannelFactory(
                context = get(),
                channelData = configuration.notificationChannelData
            ),
            permissionUtil = get()
        )
    } bind Notifier::class

    factoryOf(::FirebasePushNotifierImpl) bind PushNotifier::class

}


