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


internal class EmptyPushNotifierImpl : PushNotifier {
    override suspend fun getToken(): String? {
        println("Not implemented: Get firebase token returns null")
        return null
    }

    override suspend fun deleteMyToken() {
        println("Not implemented: Delete firebase token is called")
    }

    override suspend fun subscribeToTopic(topic: String) {
        println("Not implemented: Subscribe firebase topic is called")
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        println("Not implemented: Unsubscribe firebase topic is called")
    }
}
