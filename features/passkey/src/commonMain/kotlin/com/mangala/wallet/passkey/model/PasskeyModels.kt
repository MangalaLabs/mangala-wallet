package com.mangala.wallet.passkey.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class PasskeyCredential(
    val id: String,
    val rawId: ByteArray,
    val type: String = "public-key",
    val authenticatorAttachment: String? = null,
    val clientExtensionResults: Map<String, JsonElement>? = null,
    val response: AuthenticatorResponse
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PasskeyCredential) return false

        if (id != other.id) return false
        if (!rawId.contentEquals(other.rawId)) return false
        if (type != other.type) return false
        if (authenticatorAttachment != other.authenticatorAttachment) return false
        if (clientExtensionResults != other.clientExtensionResults) return false
        if (response != other.response) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + rawId.contentHashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (authenticatorAttachment?.hashCode() ?: 0)
        result = 31 * result + (clientExtensionResults?.hashCode() ?: 0)
        result = 31 * result + response.hashCode()
        return result
    }
}

@Serializable
sealed interface AuthenticatorResponse

@Serializable
data class AuthenticatorAttestationResponse(
    val clientDataJSON: ByteArray,
    val attestationObject: ByteArray
) : AuthenticatorResponse {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthenticatorAttestationResponse) return false

        if (!clientDataJSON.contentEquals(other.clientDataJSON)) return false
        if (!attestationObject.contentEquals(other.attestationObject)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clientDataJSON.contentHashCode()
        result = 31 * result + attestationObject.contentHashCode()
        return result
    }
}

@Serializable
data class AuthenticatorAssertionResponse(
    val clientDataJSON: ByteArray,
    val authenticatorData: ByteArray,
    val signature: ByteArray,
    val userHandle: ByteArray? = null
) : AuthenticatorResponse {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthenticatorAssertionResponse) return false

        if (!clientDataJSON.contentEquals(other.clientDataJSON)) return false
        if (!authenticatorData.contentEquals(other.authenticatorData)) return false
        if (!signature.contentEquals(other.signature)) return false
        if (userHandle != null) {
            if (other.userHandle == null) return false
            if (!userHandle.contentEquals(other.userHandle)) return false
        } else if (other.userHandle != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clientDataJSON.contentHashCode()
        result = 31 * result + authenticatorData.contentHashCode()
        result = 31 * result + signature.contentHashCode()
        result = 31 * result + (userHandle?.contentHashCode() ?: 0)
        return result
    }
}

@Serializable
data class RegistrationOptions(
    val challenge: ByteArray,
    val rp: RelyingParty,
    val user: User,
    val pubKeyCredParams: List<PublicKeyCredentialParameters>,
    val timeout: Long = 60000,
    val excludeCredentials: List<PublicKeyCredentialDescriptor> = emptyList(),
    val authenticatorSelection: AuthenticatorSelectionCriteria? = null,
    val attestation: AttestationConveyancePreference = AttestationConveyancePreference.NONE
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RegistrationOptions) return false

        if (!challenge.contentEquals(other.challenge)) return false
        if (rp != other.rp) return false
        if (user != other.user) return false
        if (pubKeyCredParams != other.pubKeyCredParams) return false
        if (timeout != other.timeout) return false
        if (excludeCredentials != other.excludeCredentials) return false
        if (authenticatorSelection != other.authenticatorSelection) return false
        if (attestation != other.attestation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = challenge.contentHashCode()
        result = 31 * result + rp.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + pubKeyCredParams.hashCode()
        result = 31 * result + timeout.hashCode()
        result = 31 * result + excludeCredentials.hashCode()
        result = 31 * result + (authenticatorSelection?.hashCode() ?: 0)
        result = 31 * result + attestation.hashCode()
        return result
    }
}

@Serializable
data class AuthenticationOptions(
    val challenge: ByteArray,
    val rpId: String,
    val timeout: Long = 60000,
    val allowCredentials: List<PublicKeyCredentialDescriptor> = emptyList(),
    val userVerification: UserVerificationRequirement = UserVerificationRequirement.PREFERRED
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthenticationOptions) return false

        if (!challenge.contentEquals(other.challenge)) return false
        if (rpId != other.rpId) return false
        if (timeout != other.timeout) return false
        if (allowCredentials != other.allowCredentials) return false
        if (userVerification != other.userVerification) return false

        return true
    }

    override fun hashCode(): Int {
        var result = challenge.contentHashCode()
        result = 31 * result + rpId.hashCode()
        result = 31 * result + timeout.hashCode()
        result = 31 * result + allowCredentials.hashCode()
        result = 31 * result + userVerification.hashCode()
        return result
    }
}

@Serializable
data class RelyingParty(
    val id: String,
    val name: String
)

@Serializable
data class User(
    val id: ByteArray,
    val name: String,
    val displayName: String,
    val originalId: String? = null // Store the original Base64 userId from backend
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (!id.contentEquals(other.id)) return false
        if (name != other.name) return false
        if (displayName != other.displayName) return false
        if (originalId != other.originalId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + (originalId?.hashCode() ?: 0)
        return result
    }
}

@Serializable
data class PublicKeyCredentialParameters(
    val type: String = "public-key",
    val alg: Long
)

@Serializable
data class PublicKeyCredentialDescriptor(
    val type: String = "public-key",
    val id: ByteArray,
    val transports: List<AuthenticatorTransport> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PublicKeyCredentialDescriptor) return false

        if (type != other.type) return false
        if (!id.contentEquals(other.id)) return false
        if (transports != other.transports) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + id.contentHashCode()
        result = 31 * result + transports.hashCode()
        return result
    }
}

@Serializable
data class AuthenticatorSelectionCriteria(
    val authenticatorAttachment: AuthenticatorAttachment? = null,
    val residentKey: ResidentKeyRequirement = ResidentKeyRequirement.PREFERRED,
    val requireResidentKey: Boolean = false,
    val userVerification: UserVerificationRequirement = UserVerificationRequirement.PREFERRED
)

@Serializable
enum class AuthenticatorAttachment {
    PLATFORM,
    CROSS_PLATFORM
}

@Serializable
enum class ResidentKeyRequirement {
    DISCOURAGED,
    PREFERRED,
    REQUIRED
}

@Serializable
enum class UserVerificationRequirement {
    REQUIRED,
    PREFERRED,
    DISCOURAGED
}

@Serializable
enum class AttestationConveyancePreference {
    NONE,
    INDIRECT,
    DIRECT,
    ENTERPRISE
}

@Serializable
enum class AuthenticatorTransport {
    USB,
    NFC,
    BLE,
    INTERNAL,
    HYBRID
}

@Serializable
data class PasskeyChallenge(
    val challenge: ByteArray,
    val expiresAt: Instant
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PasskeyChallenge) return false

        if (!challenge.contentEquals(other.challenge)) return false
        if (expiresAt != other.expiresAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = challenge.contentHashCode()
        result = 31 * result + expiresAt.hashCode()
        return result
    }
}

@Serializable
data class AuthenticationResult(
    val credentialId: String,
    val userId: String,
    val verified: Boolean,
    val authenticatorData: ByteArray,
    val signature: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthenticationResult) return false

        if (credentialId != other.credentialId) return false
        if (userId != other.userId) return false
        if (verified != other.verified) return false
        if (!authenticatorData.contentEquals(other.authenticatorData)) return false
        if (!signature.contentEquals(other.signature)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = credentialId.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + verified.hashCode()
        result = 31 * result + authenticatorData.contentHashCode()
        result = 31 * result + signature.contentHashCode()
        return result
    }
}

data class StoredCredential(
    val id: String,
    val rpId: String,
    val userName: String,
    val createdAt: Long
)

/**
 * API Error Response DTO
 * Example: {"error":{"code":"SYS_005","message":"[SYS_005] External service unavailable","timestamp":"2025-11-11T15:05:54.296257465Z","request_id":"req_f4ced8a4"}}
 */
@Serializable
data class ApiErrorResponse(
    val error: ErrorDetail? = null
)

@Serializable
data class ErrorDetail(
    val code: String? = null,
    val message: String? = null,
    val timestamp: String? = null,
    val request_id: String? = null
)