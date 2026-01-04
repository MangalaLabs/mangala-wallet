package com.mangala.wallet.menu_base.presentation.dev

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperConstants
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.ClipboardFactory
import com.mmk.kmpnotifier.notification.NotifierManager
import com.wallet.iap.purchases.PaymentInfo
import com.wallet.iap.purchases.PurchaseState
import com.wallet.iap.purchases.device.PurchaseManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class DevMenuScreenModel(
    private val secureStorageWrapper: SecureStorageWrapper,
    private val purchaseManager: PurchaseManager,
    private val clipboardFactory: ClipboardFactory
) : BaseScreenModel() {

    private val _fcmToken = mutableStateOf<String?>(null)
    val fcmToken: State<String?> = _fcmToken

    private val _iapData = mutableStateOf<List<PaymentInfo>>(emptyList())
    val iapData: State<List<PaymentInfo>> = _iapData

    init {
        screenModelScope.launch {
            _fcmToken.value = try {
                NotifierManager.getPushNotifier().getToken()
            } catch (e: Exception) {
                println("NotifierManager Failed to get FCM token: $e")
                null
            }
        }
        screenModelScope.launch {
            getAllPurchases()
        }
    }

    private suspend fun getAllPurchases() {
        _iapData.value = purchaseManager.getPurchases().getOrNull() ?: emptyList()
    }

    fun copyPurchaseToken(paymentInfo: PaymentInfo) {
        clipboardFactory.copyText("Purchase token", paymentInfo.purchaseToken.orEmpty())
    }

    fun clearPin() {
        secureStorageWrapper.saveValue(SecureStorageWrapperConstants.PIN_KEY, "")
    }

    fun logout() {
        screenModelScope.launch {
            try {
                Napier.d("DevMenuScreenModel: Session logout (preserving passkeys for testing)")

                // Clear PIN
                secureStorageWrapper.saveValue(SecureStorageWrapperConstants.PIN_KEY, "")
                Napier.d("✅ Cleared PIN")

                // Clear session tokens
                secureStorageWrapper.remove("auth_session")
                secureStorageWrapper.remove("user_session")
                secureStorageWrapper.remove("access_token")
                secureStorageWrapper.remove("refresh_token")
                Napier.d("✅ Cleared session tokens")

                // Clear other potential auth data
                secureStorageWrapper.remove("biometric_enabled")
                secureStorageWrapper.remove("auth_state")
                Napier.d("✅ Cleared additional auth data")

                // NOTE: NOT clearing completed_passkey_ids so you can test authentication with existing passkeys

                Napier.w("🚀 SESSION LOGOUT COMPLETED - Passkeys preserved for testing!")
                Napier.w("📱 App should redirect to sign-in screen but passkeys should still work")
                println("✅ SESSION LOGOUT PRESSED - Passkeys preserved for testing")

            } catch (e: Exception) {
                Napier.e("❌ Failed to logout: ${e.message}", e)
                println("❌ LOGOUT FAILED - Check console logs above for error details")
            }
        }
    }

    fun clearAllData() {
        screenModelScope.launch {
            try {
                Napier.d("DevMenuScreenModel: Complete cleanup including passkeys")

                // Clear PIN
                secureStorageWrapper.saveValue(SecureStorageWrapperConstants.PIN_KEY, "")
                Napier.d("✅ Cleared PIN")

                // Clear completed passkeys storage
                secureStorageWrapper.remove("completed_passkey_ids")
                Napier.d("✅ Cleared completed passkeys storage")

                // Clear session tokens
                secureStorageWrapper.remove("auth_session")
                secureStorageWrapper.remove("user_session")
                secureStorageWrapper.remove("access_token")
                secureStorageWrapper.remove("refresh_token")
                Napier.d("✅ Cleared session tokens")

                // Clear other potential auth data
                secureStorageWrapper.remove("biometric_enabled")
                secureStorageWrapper.remove("auth_state")
                Napier.d("✅ Cleared additional auth data")

                Napier.w("🚀 COMPLETE CLEANUP COMPLETED - All data cleared!")
                Napier.w("📱 App is now in fresh state for new registrations")
                println("✅ COMPLETE CLEANUP PRESSED - All data cleared")

            } catch (e: Exception) {
                Napier.e("❌ Failed to clear all data: ${e.message}", e)
                println("❌ COMPLETE CLEANUP FAILED - Check console logs above for error details")
            }
        }
    }

    fun testPasskeyStorage() {
        screenModelScope.launch {
            try {
                Napier.e("🧪 TEST: Starting passkey storage test")

                // Test 1: Check what's currently in storage
                val existing = secureStorageWrapper.getValue("completed_passkey_ids")
                Napier.e("🧪 TEST: Current storage content: ${existing?.length ?: 0} chars")
                if (existing != null) {
                    Napier.e("🧪 TEST: Raw JSON: $existing")
                }

                // Test 2: Try to save a test passkey
                val testPasskey = """[{"credentialId":"test123","userId":"testuser","deviceId":"testdevice","deviceName":"TestDevice","createdAt":${System.currentTimeMillis()}}]"""
                Napier.e("🧪 TEST: Saving test passkey JSON: $testPasskey")

                secureStorageWrapper.saveValue("completed_passkey_ids", testPasskey)
                Napier.e("🧪 TEST: Save completed")

                // Test 3: Immediately read it back
                val readBack = secureStorageWrapper.getValue("completed_passkey_ids")
                Napier.e("🧪 TEST: Read back result: ${readBack?.length ?: 0} chars")
                if (readBack != null) {
                    Napier.e("🧪 TEST: Read back content: $readBack")
                    if (readBack == testPasskey) {
                        Napier.e("🧪 TEST: ✅ SUCCESS - Storage persistence is working!")
                    } else {
                        Napier.e("🧪 TEST: ❌ MISMATCH - Saved and read data don't match")
                    }
                } else {
                    Napier.e("🧪 TEST: ❌ FAILED - Read back returned null")
                }

                println("🧪 PASSKEY STORAGE TEST COMPLETED - Check logs above for results")

            } catch (e: Exception) {
                Napier.e("🧪 TEST: ❌ Exception during test: ${e.message}", e)
                println("🧪 PASSKEY STORAGE TEST FAILED - Check logs above for error details")
            }
        }
    }
}