package com.mangala.wallet.features.addressbook.domain.validation

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class QrCodeValidator {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    // Dangerous patterns that should be blocked
    private val maliciousPatterns = listOf(
        "javascript:",
        "<script",
        "data:text/html",
        "vbscript:",
        "file://",
        "ftp://",
        "ldap://",
        "gopher://",
        "mailto:",
        "tel:",
        "sms:",
        "callto:"
    )
    
    companion object {
        // Maximum length for QR data to prevent DoS
        private const val MAX_QR_DATA_LENGTH = 4000
    }
    
    /**
     * Validates if QR data is safe and well-formed
     */
    fun validateQrData(data: String): ValidationResult {
        // Check length
        if (data.length > MAX_QR_DATA_LENGTH) {
            return ValidationResult.Invalid("QR data too long")
        }
        
        // Check for malicious patterns
        val lowerData = data.lowercase()
        maliciousPatterns.forEach { pattern ->
            if (lowerData.contains(pattern)) {
                return ValidationResult.Invalid("Potentially malicious content detected")
            }
        }
        
        // Try to determine QR type and validate accordingly
        return when {
            isContactQr(data) -> validateContactQr(data)
            isJsonQr(data) -> validateJsonQr(data)
            isWalletAddress(data) -> validateWalletAddress(data)
            else -> ValidationResult.Valid(QrDataType.Unknown)
        }
    }
    
    /**
     * Sanitizes QR data by removing dangerous characters
     */
    fun sanitizeQrData(data: String): String {
        return data
            .replace(Regex("[<>\"'&]"), "") // Remove HTML-dangerous chars
            .replace(Regex("[\r\n\t]"), " ") // Replace newlines with spaces
            .trim()
            .take(MAX_QR_DATA_LENGTH) // Truncate if too long
    }
    
    private fun isContactQr(data: String): Boolean {
        return data.contains("|") && data.split("|").size >= 2
    }
    
    private fun isJsonQr(data: String): Boolean {
        return data.trim().startsWith("{") && data.trim().endsWith("}")
    }
    
    private fun isWalletAddress(data: String): Boolean {
        // Basic wallet address patterns
        return when {
            data.startsWith("0x") && data.length == 42 -> true // Ethereum
            data.startsWith("bc1") || data.startsWith("1") || data.startsWith("3") -> true // Bitcoin
            data.length in 26..35 && data.all { it.isLetterOrDigit() } -> true // Generic crypto
            else -> false
        }
    }
    
    private fun validateContactQr(data: String): ValidationResult {
        val parts = data.split("|")
        if (parts.size < 2) {
            return ValidationResult.Invalid("Invalid contact QR format")
        }
        
        val name = parts[0]
        val address = parts[1]
        val blockchain = parts.getOrNull(2)
        
        // Validate name
        if (name.isBlank() || name.length > 100) {
            return ValidationResult.Invalid("Invalid contact name")
        }
        
        // Validate address
        if (address.isBlank() || !isValidAddress(address)) {
            return ValidationResult.Invalid("Invalid wallet address")
        }
        
        return ValidationResult.Valid(QrDataType.Contact)
    }
    
    private fun validateJsonQr(data: String): ValidationResult {
        return try {
            val jsonElement = json.parseToJsonElement(data)
            val jsonObject = jsonElement.jsonObject
            
            val type = jsonObject["type"]?.jsonPrimitive?.content
            
            when (type) {
                "address" -> validateJsonAddress(jsonObject)
                "group" -> validateJsonGroup(jsonObject)
                "tag" -> validateJsonTag(jsonObject)
                else -> ValidationResult.Invalid("Unknown JSON QR type")
            }
        } catch (e: Exception) {
            ValidationResult.Invalid("Invalid JSON format")
        }
    }
    
    private fun validateJsonAddress(jsonObject: JsonObject): ValidationResult {
        val address = jsonObject["address"]?.jsonPrimitive?.content
        if (address.isNullOrBlank() || !isValidAddress(address)) {
            return ValidationResult.Invalid("Invalid address in JSON QR")
        }
        return ValidationResult.Valid(QrDataType.Address)
    }
    
    private fun validateJsonGroup(jsonObject: JsonObject): ValidationResult {
        val name = jsonObject["name"]?.jsonPrimitive?.content
        if (name.isNullOrBlank() || name.length > 100) {
            return ValidationResult.Invalid("Invalid group name in JSON QR")
        }
        return ValidationResult.Valid(QrDataType.Group)
    }
    
    private fun validateJsonTag(jsonObject: JsonObject): ValidationResult {
        val name = jsonObject["name"]?.jsonPrimitive?.content
        if (name.isNullOrBlank() || name.length > 50) {
            return ValidationResult.Invalid("Invalid tag name in JSON QR")
        }
        return ValidationResult.Valid(QrDataType.Tag)
    }
    
    private fun validateWalletAddress(data: String): ValidationResult {
        if (isValidAddress(data)) {
            return ValidationResult.Valid(QrDataType.Address)
        }
        return ValidationResult.Invalid("Invalid wallet address format")
    }
    
    private fun isValidAddress(address: String): Boolean {
        return when {
            // Ethereum address
            address.startsWith("0x") && address.length == 42 && 
            address.substring(2).all { it.isDigit() || it.lowercaseChar() in 'a'..'f' } -> true
            
            // Bitcoin Legacy (P2PKH)
            address.startsWith("1") && address.length in 26..35 && 
            address.all { it.isLetterOrDigit() && it != '0' && it != 'O' && it != 'I' && it != 'l' } -> true
            
            // Bitcoin Script (P2SH)
            address.startsWith("3") && address.length in 26..35 && 
            address.all { it.isLetterOrDigit() && it != '0' && it != 'O' && it != 'I' && it != 'l' } -> true
            
            // Bitcoin Bech32 (P2WPKH/P2WSH)
            address.startsWith("bc1") && address.length in 39..59 && 
            address.all { it.isLetterOrDigit() || it == '1' } -> true
            
            // Litecoin
            (address.startsWith("L") || address.startsWith("M") || address.startsWith("ltc1")) && 
            address.length in 26..59 -> true
            
            // Basic length and character validation for other cryptos
            address.length in 26..100 && address.all { it.isLetterOrDigit() || it in ".-_" } -> true
            
            else -> false
        }
    }
}

sealed class ValidationResult {
    data class Valid(val type: QrDataType) : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

enum class QrDataType {
    Contact,
    Address, 
    Group,
    Tag,
    Unknown
}