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

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.mmk.kmpnotifier.extensions.hasPermission

/**
 * in Activity
 *
 * private val permissionUtil by permissionUtil()
 *
 * then #onCreate method
 * permissionUtil.askNotificationPermission {
 *  println("HasNotification Permission: $it")
 * }
 *
 */
public fun ComponentActivity.permissionUtil(): Lazy<AndroidPermissionUtil> = lazy(LazyThreadSafetyMode.NONE) {
    AndroidPermissionUtil(this)
}

/**
 * Android notification utility class for making it easier to ask permission from user.
 */
public class AndroidPermissionUtil(private val activity: ComponentActivity)  {

    private var mOnResult: ((Boolean) -> Unit)? = null

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            mOnResult?.invoke(isGranted)
        }

    /**
     * Asks notification permission from user
     * @param onResult lambda is called when notification permission is returned
     */
    public fun askNotificationPermission(onResult: (Boolean) -> Unit = {}) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askIfNotHasPermission(permission = Manifest.permission.POST_NOTIFICATIONS, onResult = onResult)
        } else onResult(true)
    }


    private fun askIfNotHasPermission(permission: String, onResult: (Boolean) -> Unit = {}) {
        if (activity.hasPermission(permission)) {
            onResult(true)
        } else {
            mOnResult = onResult
            requestPermissionLauncher.launch(permission)
        }
    }


}