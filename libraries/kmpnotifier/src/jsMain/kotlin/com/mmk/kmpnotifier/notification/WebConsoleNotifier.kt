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
package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import kotlinx.browser.window
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationOptions
import kotlin.random.Random


internal class WebConsoleNotifier(
    private val permissionUtil: PermissionUtil,
    private val configuration: NotificationPlatformConfiguration.Web
) : Notifier {

    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify(notificationID, title, body, payloadData)
        return notificationID
    }


    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        if (isNotificationSupported().not()) {
            alertNotification(body)
            return
        }
        permissionUtil.askNotificationPermission {
            permissionUtil.hasNotificationPermission { hasPermission ->
                if (hasPermission) showNotification(title = title, body = body)
                else alertNotification(body)
            }
        }
    }

    override fun remove(id: Int) {
        println("remove notification is not implemented ")

    }

    override fun removeAll() {
        println("remove notification is not implemented ")
    }

    private fun showNotification(title: String, body: String) {
        val options = NotificationOptions(body = body, icon = configuration.notificationIconPath)
        Notification(title, options)
    }

    private fun alertNotification(message: String) {
        window.alert(message)
    }

    private fun isNotificationSupported(): Boolean {
        return js("typeof Notification !== 'undefined'").unsafeCast<Boolean>()
    }

}