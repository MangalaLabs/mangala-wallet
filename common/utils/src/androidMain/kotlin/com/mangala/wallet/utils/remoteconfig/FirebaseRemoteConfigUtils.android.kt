package com.mangala.wallet.utils.remoteconfig

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.mangala.wallet.utils.CrashlyticsUtils
import com.mangala.wallet.utils.R

actual object FirebaseRemoteConfigUtils {

    actual fun initialize() {
        val remoteConfig = Firebase.remoteConfig

        remoteConfig.apply {
            setConfigSettingsAsync(remoteConfigSettings {
                minimumFetchIntervalInSeconds = FETCH_INTERVAL_SECONDS
            })
            addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                }
            })
            setDefaultsAsync(R.xml.remote_config_defaults)
            fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("Remote Config", "Config params updated: $updated")
                } else {
                    Log.e("Remote Config", "Failed to fetch config: ${task.exception}")
                    task.exception?.let { CrashlyticsUtils.logNonFatal(it) }
                }
            }
        }
    }
}