package com.mangala.wallet.auth

import android.content.Context
import android.util.Log
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityManager
import com.google.android.play.core.integrity.StandardIntegrityManager.StandardIntegrityTokenProvider
import kotlinx.coroutines.tasks.await

object GooglePlayIntegrityManager {

    private lateinit var integrityTokenProvider: StandardIntegrityTokenProvider

    suspend fun initialize(applicationContext: Context) {
        val standardIntegrityManager: StandardIntegrityManager =
            IntegrityManagerFactory.createStandard(applicationContext)
        val cloudProjectNumber = BuildKonfig.GOOGLE_CLOUD_PROJECT_NUMBER.toLong()

        // Prepare integrity token. Can be called once in a while to keep internal
        // state fresh.
        try {
            integrityTokenProvider = standardIntegrityManager.prepareIntegrityToken(
                StandardIntegrityManager.PrepareIntegrityTokenRequest.builder()
                    .setCloudProjectNumber(cloudProjectNumber)
                    .build()
            ).await()

            Log.d("PlayIntegrityMgr", "Integrity token prepared successfully")
        } catch (e: Exception) {
            Log.d("PlayIntegrityMgr", "Integrity token preparation failed $e")
        }
    }

    suspend fun getIntegrityVerdict() {
        try {
            val response = integrityTokenProvider.request(
                StandardIntegrityManager.StandardIntegrityTokenRequest.builder()
                    .setRequestHash("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
                    .build()
            ).await()

            val token = response.token()

            Log.d("PlayIntegrityMgr", "Generated integrity token $token")
        } catch (e: Exception) {
            Log.d("PlayIntegrityMgr", "Generate integrity token error $e")
        }
    }
}