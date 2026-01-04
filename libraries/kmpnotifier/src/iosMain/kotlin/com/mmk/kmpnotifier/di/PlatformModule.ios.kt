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

import com.mmk.kmpnotifier.firebase.FirebasePushNotifierImpl
import com.mmk.kmpnotifier.notification.IosNotifier
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.permission.IosPermissionUtil
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.UserNotifications.UNUserNotificationCenter


internal actual val platformModule = module {
    factory { Platform.Ios } bind Platform::class
    factory { IosPermissionUtil(notificationCenter = UNUserNotificationCenter.currentNotificationCenter()) } bind PermissionUtil::class
    factory {
        IosNotifier(
            permissionUtil = get(),
            notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
        )
    } bind Notifier::class

    factory {
        FirebasePushNotifierImpl
    } bind PushNotifier::class


}
