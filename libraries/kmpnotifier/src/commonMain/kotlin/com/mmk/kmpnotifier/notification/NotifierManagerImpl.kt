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

import com.mmk.kmpnotifier.di.KMPKoinComponent
import com.mmk.kmpnotifier.di.LibDependencyInitializer
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.core.component.get

public object NotifierManagerImpl : KMPKoinComponent() {
    private val listeners = mutableListOf<NotifierManager.Listener>()

    public fun initialize(configuration: NotificationPlatformConfiguration) {
        LibDependencyInitializer.initialize(configuration)
    }

    public fun getConfiguration(): NotificationPlatformConfiguration = get()

    public fun getLocalNotifier(): Notifier {
        requireInitialization()
        return get()
    }

    public fun getPushNotifier(): PushNotifier {
        requireInitialization()
        return get()
    }

    public fun getPermissionUtil(): PermissionUtil {
        requireInitialization()
        return get()
    }

    public fun addListener(listener: NotifierManager.Listener) {
        println("NotifierManagerImpl added listener")
        listeners.add(listener)
    }

    public fun onNewToken(token: String) {
        listeners.forEach { it.onNewToken(token) }
    }

    public fun onPushPayloadData(data: PayloadData) {
        println("Received Push Notification payload data")
        if (listeners.size == 0) println("There is no listener to notify onPushPayloadData")
        listeners.forEach { it.onPayloadData(data) }
    }

    public fun onPushNotification(title: String?, body: String?) {
        println("Received Push Notification notification type message")
        if (listeners.size == 0) println("There is no listener to notify onPushNotification")
        listeners.forEach { it.onPushNotification(title = title, body = body) }
    }

    public fun onNotificationClicked(data: PayloadData) {
        println("Notification is clicked")
        if (listeners.size == 0) println("There is no listener to notify onPushPayloadData")
        listeners.forEach { it.onNotificationClicked(data) }
    }

    private fun requireInitialization() {
        if (LibDependencyInitializer.isInitialized().not()) throw IllegalStateException(
            "NotifierFactory is not initialized. " +
                    "Please, initialize NotifierFactory by calling #initialize method"
        )
    }

}