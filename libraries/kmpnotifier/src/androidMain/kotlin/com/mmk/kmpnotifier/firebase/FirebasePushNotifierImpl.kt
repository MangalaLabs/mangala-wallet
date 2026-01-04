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
package com.mmk.kmpnotifier.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.mmk.kmpnotifier.notification.PushNotifier
import kotlinx.coroutines.tasks.asDeferred

internal class FirebasePushNotifierImpl : PushNotifier {

    init {
        println("FirebasePushNotifier is initialized")
    }
    override suspend fun getToken(): String? {
        return FirebaseMessaging.getInstance().token.asDeferred().await()
    }

    override suspend fun deleteMyToken() {
        FirebaseMessaging.getInstance().deleteToken()
    }

    override suspend fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }


}