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
import com.mmk.kmpnotifier.notification.impl.JOptionPaneNotifier
import com.mmk.kmpnotifier.notification.impl.TrayNotifier

internal object DesktopNotifierFactory {
    fun getNotifier(configuration: NotificationPlatformConfiguration.Desktop): Notifier {
        return when {
            TrayNotifier.isSupported -> TrayNotifier(configuration = configuration)
            //TODO for now return JOptionPaneNotifier for not supported platforms
            else -> JOptionPaneNotifier(configuration = configuration)
        }
    }
}