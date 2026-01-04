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
package com.mmk.kmpnotifier.notification.impl

import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import javax.swing.ImageIcon
import javax.swing.JOptionPane

internal class JOptionPaneNotifier(private val configuration: NotificationPlatformConfiguration.Desktop) :
    Notifier {

    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        val id = -1
        notify(id = id, title = title, body = body, payloadData)
        return id
    }

    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        JOptionPane.showMessageDialog(
            null,
            body,
            title,
            JOptionPane.INFORMATION_MESSAGE,
            ImageIcon(configuration.notificationIconPath)
        )
    }

    override fun remove(id: Int) {
        println("No remove functionality")
    }

    override fun removeAll() {
        println("No removeAll functionality")
    }
}