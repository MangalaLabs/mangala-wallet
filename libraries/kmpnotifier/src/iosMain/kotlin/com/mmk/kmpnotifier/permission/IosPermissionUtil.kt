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
package com.mmk.kmpnotifier.permission

import com.mmk.kmpnotifier.notification.IosNotifier
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

internal class IosPermissionUtil(private val notificationCenter: UNUserNotificationCenter) : PermissionUtil {
    companion object {
        val NOTIFICATION_PERMISSIONS =
            UNAuthorizationOptionAlert or
                    UNAuthorizationOptionSound or
                    UNAuthorizationOptionBadge
    }

    override fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit) {
        notificationCenter.getNotificationSettingsWithCompletionHandler {
            onPermissionResult(it?.authorizationStatus == UNAuthorizationStatusAuthorized)
        }
    }

    override fun askNotificationPermission(onPermissionGranted: () -> Unit) {
        notificationCenter.requestAuthorizationWithOptions(NOTIFICATION_PERMISSIONS) { isGranted, _ ->
            if (isGranted) {
                UNUserNotificationCenter.currentNotificationCenter().delegate =
                    IosNotifier.NotificationDelegate()
                onPermissionGranted()
            }
        }
    }
}