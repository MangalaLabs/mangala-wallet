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

// import cocoapods.FirebaseMessaging.FIRMessaging
// import cocoapods.FirebaseMessaging.FIRMessagingDelegateProtocol
import com.mmk.kmpnotifier.notification.NotifierManagerImpl
import com.mmk.kmpnotifier.notification.PushNotifier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@OptIn(ExperimentalForeignApi::class)
public object FirebasePushNotifierImpl : PushNotifier {

    public var swiftDelegate: PushNotifier? = null

    // init {
    //     MainScope().launch {
    //         println("FirebasePushNotifier is initialized")
    //         UIApplication.sharedApplication.registerForRemoteNotifications()
    //         // FIRMessaging.messaging().delegate = FirebaseMessageDelegate()
    //     }

    // }

    override suspend fun getToken(): String? = swiftDelegate?.getToken()

    override suspend fun deleteMyToken() {
        swiftDelegate?.deleteMyToken()
    }

    override suspend fun subscribeToTopic(topic: String) {
        swiftDelegate?.subscribeToTopic(topic)
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        swiftDelegate?.unSubscribeFromTopic(topic)
    }


    // override suspend fun getToken(): String? = suspendCoroutine { cont ->

    //     // FIRMessaging.messaging().tokenWithCompletion { token, error ->
    //     //     cont.resume(token)
    //     //     error?.let { println("Error while getting token: $error") }
    //     // }
    // }

    // override suspend fun deleteMyToken() = suspendCoroutine { cont ->
    //     // FIRMessaging.messaging().deleteTokenWithCompletion {
    //          cont.resume(Unit)
    //     // }
    // }

    // override suspend fun subscribeToTopic(topic: String) {
    //     // FIRMessaging.messaging().subscribeToTopic(topic)
    // }

    // override suspend fun unSubscribeFromTopic(topic: String) {
    //     // FIRMessaging.messaging().unsubscribeFromTopic(topic)
    // }


    // private class FirebaseMessageDelegate : FIRMessagingDelegateProtocol, NSObject() {
        // private val notifierManager by lazy { NotifierManagerImpl }
        // override fun messaging(messaging: FIRMessaging, didReceiveRegistrationToken: String?) {
            // didReceiveRegistrationToken?.let { token ->
                // println("FirebaseMessaging: onNewToken is called")
                // notifierManager.onNewToken(didReceiveRegistrationToken)
            // }
        // }
    // }
}
