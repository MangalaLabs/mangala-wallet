package com.mangala.wallet.passkey

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.passkey.model.*
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.io.ByteArrayOutputStream
import java.net.URI
import java.util.*

class PasskeyManagerImpl : PasskeyManager {
    private var lastAuthenticationCredential: PasskeyCredential? = null
    
    override suspend fun isSupported(): Boolean {
        // Desktop always supports QR code bridge method
        return true
    }
    
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential = withContext(Dispatchers.IO) {
        try {
            // For desktop, we'll use a QR code bridge approach
            // Generate a registration URL that can be scanned by mobile device
            val registrationUrl = generateRegistrationUrl(
                userId = userId,
                challenge = challenge,
                rpId = rpId,
                rpName = rpName,
                userName = userName,
                userDisplayName = userDisplayName
            )
            
            // Option 1: Open in default browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI(registrationUrl))
            }
            
            // Option 2: Generate QR code for mobile scanning
            val qrCodeData = generateQRCode(registrationUrl)
            
            // In a real implementation, you would:
            // 1. Display the QR code in a dialog
            // 2. Poll the server for registration completion
            // 3. Return the credential once registration is complete
            
            // For now, we'll simulate the flow
            simulateRegistrationFlow(userId, challenge)
        } catch (e: Exception) {
            Napier.e("Desktop registration failed", e)
            throw PasskeyException.UnknownError("Registration failed", e)
        }
    }
    
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult = withContext(Dispatchers.IO) {
        try {
            // Generate authentication URL for QR code bridge
            val authenticationUrl = generateAuthenticationUrl(
                challenge = challenge,
                rpId = rpId,
                allowCredentials = allowCredentials
            )
            
            // Option 1: Open in default browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI(authenticationUrl))
            }
            
            // Option 2: Generate QR code for mobile scanning
            val qrCodeData = generateQRCode(authenticationUrl)
            
            // In a real implementation, you would:
            // 1. Display the QR code in a dialog
            // 2. Poll the server for authentication completion
            // 3. Return the result once authentication is complete
            
            // For now, we'll simulate the flow
            simulateAuthenticationFlow()
        } catch (e: Exception) {
            Napier.e("Desktop authentication failed", e)
            throw PasskeyException.UnknownError("Authentication failed", e)
        }
    }
    
    override suspend fun deleteCredential(credentialId: String) {
        // Desktop doesn't store credentials locally
        Napier.w("Delete credential not applicable for desktop")
    }
    
    override fun getLastAuthenticationCredential(): PasskeyCredential? {
        return lastAuthenticationCredential
    }
    
    private fun generateRegistrationUrl(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): String {
        val base64Challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(challenge)
        val params = buildMap {
            put("action", "register")
            put("userId", userId)
            put("challenge", base64Challenge)
            put("rpId", rpId)
            put("rpName", rpName)
            put("userName", userName)
            put("userDisplayName", userDisplayName)
        }
        
        val queryString = params.entries.joinToString("&") { (key, value) ->
            "$key=${java.net.URLEncoder.encode(value, "UTF-8")}"
        }
        
        return "https://$rpId/passkey?$queryString"
    }
    
    private fun generateAuthenticationUrl(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): String {
        val base64Challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(challenge)
        val params = buildMap {
            put("action", "authenticate")
            put("challenge", base64Challenge)
            put("rpId", rpId)
            if (allowCredentials.isNotEmpty()) {
                val credentialIds = allowCredentials.joinToString(",") { cred ->
                    Base64.getUrlEncoder().withoutPadding().encodeToString(cred.id)
                }
                put("allowCredentials", credentialIds)
            }
        }
        
        val queryString = params.entries.joinToString("&") { (key, value) ->
            "$key=${java.net.URLEncoder.encode(value, "UTF-8")}"
        }
        
        return "https://$rpId/passkey?$queryString"
    }
    
    private fun generateQRCode(content: String): ByteArray {
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300)
        
        val outputStream = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream)
        return outputStream.toByteArray()
    }
    
    private suspend fun simulateRegistrationFlow(
        userId: String,
        challenge: ByteArray
    ): PasskeyCredential {
        // Simulate waiting for mobile device to complete registration
        delay(2000)
        
        // In a real implementation, this would come from server polling
        val credentialId = UUID.randomUUID().toString()
        val rawId = credentialId.toByteArray()
        
        return PasskeyCredential(
            id = credentialId,
            rawId = rawId,
            type = "public-key",
            response = AuthenticatorAttestationResponse(
                clientDataJSON = "{}".toByteArray(), // Placeholder
                attestationObject = ByteArray(0) // Placeholder
            )
        )
    }
    
    private suspend fun simulateAuthenticationFlow(): AuthenticationResult {
        // Simulate waiting for mobile device to complete authentication
        delay(2000)
        
        // In a real implementation, this would come from server polling
        val credentialId = UUID.randomUUID().toString()
        
        // Store the authentication credential for later retrieval
        lastAuthenticationCredential = PasskeyCredential(
            id = credentialId,
            rawId = credentialId.toByteArray(),
            type = "public-key",
            response = AuthenticatorAssertionResponse(
                clientDataJSON = "{}".toByteArray(), // Placeholder
                authenticatorData = ByteArray(0), // Placeholder
                signature = ByteArray(0), // Placeholder
                userHandle = "desktop-user".toByteArray()
            )
        )
        
        return AuthenticationResult(
            credentialId = credentialId,
            userId = "desktop-user",
            verified = true,
            authenticatorData = ByteArray(0), // Placeholder
            signature = ByteArray(0) // Placeholder
        )
    }
}