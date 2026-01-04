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

public typealias PayloadData = Map<String, *>

/**
 * Class represents push notification such as Firebase Push Notification
 */
public interface PushNotifier {

    /**
     * @return current push notification token
     */
    public suspend fun getToken(): String?

    /**
     * Deletes user push notification. For log out cases for example
     */
    public suspend fun deleteMyToken()

    /**
     * Subscribing user to group.
     * @param topic  Topic name
     */
    public suspend fun subscribeToTopic(topic: String)

    /**
     * Unsubscribe user from group.
     * @param topic  Topic name
     */
    public suspend fun unSubscribeFromTopic(topic: String)

}