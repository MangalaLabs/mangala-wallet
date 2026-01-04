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

/**
 * Class that represent local notification
 */
public interface Notifier {

    /**
     * Sends local notification to device
     * @param title Title part
     * @param body Body part
     * @param payloadData Extra payload data information.
     * @return notification id
     */
    public fun notify(
        title: String,
        body: String,
        payloadData: Map<String, String> = emptyMap()
    ): Int

    /**
     * Sends local notification to device with id
     * @param id notification id
     * @param title Title part
     * @param body Body part
     * @param payloadData Extra payload data information
     */
    public fun notify(
        id: Int,
        title: String,
        body: String,
        payloadData: Map<String, String> = emptyMap()
    )


    /**
     * Remove notification by id
     * @param id notification id
     */
    public fun remove(id: Int)

    /**
     * Removes all previously shown notifications
     * @see remove(id) for removing specific notification.
     */
    public fun removeAll()
}