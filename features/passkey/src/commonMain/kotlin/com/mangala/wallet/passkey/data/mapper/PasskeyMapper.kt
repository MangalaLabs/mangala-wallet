package com.mangala.wallet.passkey.data.mapper

import com.mangala.wallet.passkey.domain.model.PasskeyAuthenticationResult
import com.mangala.wallet.passkey.domain.model.PasskeyCredentialDomain
import com.mangala.wallet.passkey.domain.model.PasskeyRegistrationResult
import com.mangala.wallet.passkey.model.StoredCredential
import com.mangala.wallet.passkey.model.UserVerificationRequirement as DataUserVerificationRequirement
import com.mangala.wallet.passkey.domain.model.UserVerificationRequirement as DomainUserVerificationRequirement
import com.mangala.wallet.passkey.repository.RegistrationVerificationResult
import com.mangala.wallet.passkey.repository.AuthenticationVerificationResult

/**
 * Mapper between data and domain models for passkey
 */
object PasskeyMapper {
    
    /**
     * Convert StoredCredential to PasskeyCredentialDomain
     */
    fun StoredCredential.toDomain(): PasskeyCredentialDomain {
        return PasskeyCredentialDomain(
            id = this.id,
            userId = this.userName, // Using userName as userId for now
            userName = this.userName,
            displayName = this.userName, // Using userName as displayName for now
            createdAt = this.createdAt
        )
    }
    
    /**
     * Convert UserVerificationRequirement from data to domain
     */
    fun DataUserVerificationRequirement.toDomain(): DomainUserVerificationRequirement {
        return when (this) {
            DataUserVerificationRequirement.REQUIRED -> DomainUserVerificationRequirement.REQUIRED
            DataUserVerificationRequirement.PREFERRED -> DomainUserVerificationRequirement.PREFERRED
            DataUserVerificationRequirement.DISCOURAGED -> DomainUserVerificationRequirement.DISCOURAGED
        }
    }
    
    /**
     * Convert UserVerificationRequirement from domain to data
     */
    fun DomainUserVerificationRequirement.toData(): DataUserVerificationRequirement {
        return when (this) {
            DomainUserVerificationRequirement.REQUIRED -> DataUserVerificationRequirement.REQUIRED
            DomainUserVerificationRequirement.PREFERRED -> DataUserVerificationRequirement.PREFERRED
            DomainUserVerificationRequirement.DISCOURAGED -> DataUserVerificationRequirement.DISCOURAGED
        }
    }
    
    /**
     * Map RegistrationVerificationResult to PasskeyRegistrationResult
     */
    fun RegistrationVerificationResult.toDomain(): PasskeyRegistrationResult {
        return PasskeyRegistrationResult(
            success = this.verified,
            credentialId = this.credentialId,
            userId = this.userId,
            accessToken = this.token,
            refreshToken = this.refreshToken,
            expiresIn = this.expiresIn,
            errorMessage = if (!this.verified) this.message else null
        )
    }
    
    /**
     * Map AuthenticationVerificationResult to PasskeyAuthenticationResult
     */
    fun AuthenticationVerificationResult.toDomain(): PasskeyAuthenticationResult {
        return PasskeyAuthenticationResult(
            success = this.verified,
            userId = this.userId,
            accessToken = this.token,
            refreshToken = this.refreshToken,
            expiresIn = null, // Not provided in current model
            errorMessage = if (!this.verified) this.message else null
        )
    }
}