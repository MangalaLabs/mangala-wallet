package com.mangala.wallet.passkey

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import java.security.MessageDigest

/**
 * Helper class to get debug information for passkey integration
 */
object PasskeyDebugInfo {
    
    /**
     * Get the APK certificate SHA256 fingerprint that's used in the Android origin
     */
    fun getApkCertificateFingerprint(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            
            val signatures = packageInfo.signatures
            if (signatures != null && signatures.isNotEmpty()) {
                val cert = signatures[0].toByteArray()
                val sha256 = MessageDigest.getInstance("SHA-256")
                val digest = sha256.digest(cert)
                
                // Convert to base64url without padding
                Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            } else {
                "No signatures found"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
    
    /**
     * Get the expected Android origin format
     */
    fun getExpectedAndroidOrigin(context: Context): String {
        val fingerprint = getApkCertificateFingerprint(context)
        return "android:apk-key-hash:$fingerprint"
    }
    
    /**
     * Generate the assetlinks.json content for the backend
     */
    fun generateAssetLinksJson(context: Context): String {
        val fingerprint = getApkCertificateFingerprint(context)
        val fingerprintHex = try {
            // Convert base64url to hex for assetlinks
            val decoded = Base64.decode(fingerprint, Base64.URL_SAFE)
            decoded.joinToString(":") { byte ->
                "%02X".format(byte)
            }
        } catch (e: Exception) {
            fingerprint
        }
        
        return """
        [{
          "relation": ["delegate_permission/common.handle_all_urls", "delegate_permission/common.get_login_creds"],
          "target": {
            "namespace": "android_app",
            "package_name": "${context.packageName}",
            "sha256_cert_fingerprints": ["$fingerprintHex"]
          }
        }]
        """.trimIndent()
    }
}