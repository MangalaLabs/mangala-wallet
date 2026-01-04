package com.mangala.wallet.passkey.exception

sealed class PasskeyException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    class NotSupported(
        message: String = "Passkey authentication is not supported on this device"
    ) : PasskeyException(message)
    
    class UserCancelled(
        message: String = "User cancelled passkey operation"
    ) : PasskeyException(message)
    
    class Timeout(
        message: String = "Passkey operation timed out"
    ) : PasskeyException(message)
    
    class InvalidChallenge(
        message: String = "Invalid or expired challenge"
    ) : PasskeyException(message)
    
    class CredentialNotFound(
        message: String = "No matching credential found"
    ) : PasskeyException(message)

    class PasskeyDecryptionFailed(
        message: String = "Failed to decrypt your passkey. Please log back in to your Google Account and try again."
    ) : PasskeyException(message)
    
    class NetworkError(
        message: String = "Network error occurred",
        cause: Throwable? = null
    ) : PasskeyException(message, cause)
    
    class ServerError(
        message: String = "Server error occurred",
        val statusCode: Int? = null
    ) : PasskeyException(message)
    
    class EmailConflict(
        message: String = "Email address is already registered"
    ) : PasskeyException(message)
    
    class InvalidState(
        message: String = "Invalid state for passkey operation"
    ) : PasskeyException(message)
    
    class UnknownError(
        message: String = "Unknown error occurred",
        cause: Throwable? = null
    ) : PasskeyException(message, cause)
}