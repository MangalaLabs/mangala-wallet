package com.mangala.wallet.features.addressbook.utils

/**
 * Validation utilities for Contact Information fields
 * Provides user-friendly validation with clear error messages
 */
object ContactValidationUtils {
    
    // Email validation
    data class EmailValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null,
        val suggestion: String? = null
    )
    
    fun validateEmail(email: String): EmailValidationResult {
        if (email.isBlank()) {
            return EmailValidationResult(true) // Empty is valid (optional field)
        }
        
        val trimmedEmail = email.trim().lowercase()
        
        // More strict email regex - proper domain validation
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9][A-Za-z0-9.-]*\\.[A-Za-z]{2,6}$"
        
        // First check basic structure
        if (!trimmedEmail.contains("@")) {
            return EmailValidationResult(
                false,
                "Email must contain @"
            )
        }
        
        if (trimmedEmail.endsWith("@")) {
            return EmailValidationResult(
                false,
                "Please complete the email address"
            )
        }
        
        val parts = trimmedEmail.split("@")
        if (parts.size != 2) {
            return EmailValidationResult(
                false,
                "Invalid email format - multiple @ symbols"
            )
        }
        
        val localPart = parts[0]
        val domainPart = parts[1]
        
        // Validate local part
        if (localPart.isEmpty()) {
            return EmailValidationResult(
                false,
                "Email username cannot be empty"
            )
        }
        
        // Validate domain part
        if (!domainPart.contains(".")) {
            return EmailValidationResult(
                false,
                "Missing domain extension (e.g. .com)"
            )
        }
        
        // Check for common typos in domain
        val commonTypos = mapOf(
            "gmial.com" to "gmail.com",
            "gmai.com" to "gmail.com", 
            "gmal.com" to "gmail.com",
            "gmil.com" to "gmail.com",
            "yahooo.com" to "yahoo.com",
            "yaho.com" to "yahoo.com",
            "hotmial.com" to "hotmail.com",
            "hotmal.com" to "hotmail.com",
            "outlok.com" to "outlook.com"
        )
        
        // Extract actual domain (handle subdomains)
        val domainParts = domainPart.split(".")
        if (domainParts.size >= 2) {
            val mainDomain = "${domainParts[domainParts.size - 2]}.${domainParts.last()}"
            
            // Check against common typos
            commonTypos[mainDomain]?.let { suggestion ->
                return EmailValidationResult(
                    false, // Mark as invalid to force user to fix typo
                    "Invalid domain. Did you mean @$suggestion?",
                    suggestion
                )
            }
        }
        
        // Final regex validation
        if (!trimmedEmail.matches(emailRegex.toRegex())) {
            // Check for specific issues
            return when {
                domainParts.last().length < 2 -> EmailValidationResult(
                    false,
                    "Invalid domain extension"
                )
                domainParts.last().length > 6 -> EmailValidationResult(
                    false,
                    "Domain extension too long (e.g. .com, .org)"
                )
                domainPart.contains("..") -> EmailValidationResult(
                    false,
                    "Invalid domain format - consecutive dots"
                )
                else -> EmailValidationResult(
                    false,
                    "Invalid email format"
                )
            }
        }
        
        return EmailValidationResult(true)
    }
    
    // Phone validation
    data class PhoneValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null,
        val formattedNumber: String? = null
    )
    
    fun validatePhone(phone: String): PhoneValidationResult {
        if (phone.isBlank()) {
            return PhoneValidationResult(true) // Empty is valid for optional field
        }
        
        val trimmedPhone = phone.trim()
        
        // Remove all formatting to check digits
        val digitsOnly = trimmedPhone.filter { it.isDigit() }
        
        // Check for invalid patterns like "34536324----"
        val consecutiveSpecialChars = Regex("[^0-9+]{3,}") // 3 or more consecutive non-digit chars (except +)
        if (consecutiveSpecialChars.containsMatchIn(trimmedPhone)) {
            return PhoneValidationResult(
                false,
                "Invalid phone format - too many special characters"
            )
        }
        
        // Check for proper formatting structure
        val validPhonePatterns = listOf(
            Regex("^\\+?[0-9]{7,15}$"), // Basic international
            Regex("^\\+[0-9]{1,3}[- ]?\\(?[0-9]{1,4}\\)?[- ]?[0-9]{1,4}[- ]?[0-9]{1,5}$"), // International with formatting
            Regex("^\\(?[0-9]{3}\\)?[- ]?[0-9]{3}[- ]?[0-9]{4}$"), // US format
            Regex("^[0-9]{3,4}[- ]?[0-9]{3,4}$") // Short local number
        )
        
        // Check if digits are in valid range
        if (digitsOnly.length < 7) {
            return PhoneValidationResult(
                false,
                "Phone number too short (minimum 7 digits)"
            )
        }
        
        if (digitsOnly.length > 15) {
            return PhoneValidationResult(
                false,
                "Phone number too long (maximum 15 digits)"
            )
        }
        
        // Check if it contains invalid characters
        val validChars = setOf('+', '-', '(', ')', ' ', '.')
        val hasInvalidChars = trimmedPhone.any { !it.isDigit() && it !in validChars }
        if (hasInvalidChars) {
            return PhoneValidationResult(
                false,
                "Phone can only contain numbers and +, -, (, ), space"
            )
        }
        
        // Must match at least one valid pattern
        val matchesPattern = validPhonePatterns.any { pattern -> pattern.matches(trimmedPhone) }
        if (!matchesPattern) {
            return PhoneValidationResult(
                false,
                "Invalid phone number format"
            )
        }
        
        // Format the number nicely
        val formatted = formatPhoneNumber(trimmedPhone)
        
        return PhoneValidationResult(
            true,
            null,
            formatted
        )
    }
    
    fun formatPhoneNumber(phone: String): String {
        val digitsOnly = phone.filter { it.isDigit() }
        val hasPlus = phone.startsWith("+")
        
        return when {
            // International format with country code
            hasPlus && digitsOnly.length >= 10 -> {
                // Try to find known country code (1-3 digits)
                val countryCode = when {
                    digitsOnly.isNotEmpty() && isKnownCountryCode(digitsOnly.substring(0, 1)) -> digitsOnly.substring(0, 1)
                    digitsOnly.length >= 2 && isKnownCountryCode(digitsOnly.substring(0, 2)) -> digitsOnly.substring(0, 2)
                    digitsOnly.length >= 3 && isKnownCountryCode(digitsOnly.substring(0, 3)) -> digitsOnly.substring(0, 3)
                    else -> if (digitsOnly.isNotEmpty()) digitsOnly.substring(0, 1) else "1" // Default to 1 digit
                }
                
                "+$countryCode " + formatLocalNumber(digitsOnly.drop(countryCode.length))
            }
            // US number without country code
            digitsOnly.length == 10 && digitsOnly.startsWith("1") -> {
                "(${digitsOnly.substring(0, 3)}) ${digitsOnly.substring(3, 6)}-${digitsOnly.substring(6)}"
            }
            // Default formatting
            else -> phone
        }
    }
    
    private fun isKnownCountryCode(code: String): Boolean {
        val knownCodes = setOf("1", "44", "84", "86", "91", "33", "49", "81", "82", "65")
        return code in knownCodes
    }
    
    private fun formatLocalNumber(number: String): String {
        return when (number.length) {
            10 -> "(${number.substring(0, 3)}) ${number.substring(3, 6)}-${number.substring(6)}"
            else -> number.chunked(3).joinToString(" ")
        }
    }
    
    // Address validation
    fun validateAddress(address: String): Pair<Boolean, String?> {
        if (address.isBlank()) {
            return true to null // Empty is valid
        }
        
        // Check for suspicious content
        val suspiciousPatterns = listOf(
            "<script", "javascript:", "onclick", "onerror"
        )
        
        if (suspiciousPatterns.any { address.contains(it, ignoreCase = true) }) {
            return false to "Invalid characters in address"
        }
        
        // Check length
        if (address.length > 500) {
            return false to "Address too long (max 500 characters)"
        }
        
        return true to null
    }
    
    // Social profile validation
    fun validateSocialProfile(platform: String, url: String): Pair<Boolean, String?> {
        if (url.isBlank()) {
            return true to null
        }
        
        val trimmedUrl = url.trim()
        
        // Platform-specific validation
        val (isValid, suggestion) = when (platform.lowercase()) {
            "facebook" -> {
                when {
                    trimmedUrl.matches(Regex("^[a-zA-Z0-9.]+$")) -> 
                        true to "facebook.com/$trimmedUrl"
                    trimmedUrl.contains("facebook.com") || trimmedUrl.contains("fb.com") -> 
                        true to null
                    else -> false to "Enter username or Facebook URL"
                }
            }
            "twitter", "x" -> {
                when {
                    trimmedUrl.startsWith("@") -> 
                        true to "x.com/${trimmedUrl.substring(1)}"
                    trimmedUrl.matches(Regex("^[a-zA-Z0-9_]+$")) -> 
                        true to "x.com/$trimmedUrl"
                    trimmedUrl.contains("twitter.com") || trimmedUrl.contains("x.com") -> 
                        true to null
                    else -> false to "Enter username or X/Twitter URL"
                }
            }
            "instagram" -> {
                when {
                    trimmedUrl.startsWith("@") -> 
                        true to "instagram.com/${trimmedUrl.substring(1)}"
                    trimmedUrl.matches(Regex("^[a-zA-Z0-9_.]+$")) -> 
                        true to "instagram.com/$trimmedUrl"
                    trimmedUrl.contains("instagram.com") -> 
                        true to null
                    else -> false to "Enter username or Instagram URL"
                }
            }
            "linkedin" -> {
                when {
                    trimmedUrl.contains("linkedin.com") -> true to null
                    trimmedUrl.matches(Regex("^[a-zA-Z0-9-]+$")) -> 
                        true to "linkedin.com/in/$trimmedUrl"
                    else -> false to "Enter username or LinkedIn URL"
                }
            }
            else -> {
                // Generic URL validation
                val urlRegex = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$"
                if (trimmedUrl.matches(urlRegex.toRegex())) {
                    true to null
                } else {
                    false to "Enter a valid URL"
                }
            }
        }
        
        return if (isValid) {
            true to suggestion
        } else {
            false to suggestion
        }
    }
    
    // Nickname validation  
    fun validateNickname(nickname: String): Pair<Boolean, String?> {
        if (nickname.isBlank()) {
            return true to null
        }
        
        // Check length
        if (nickname.length > 50) {
            return false to "Nickname too long (max 50 characters)"
        }
        
        // Check for inappropriate content (basic check)
        val inappropriateWords = listOf<String>(
            // Add inappropriate words to filter
        )
        
        if (inappropriateWords.any { nickname.contains(it, ignoreCase = true) }) {
            return false to "Please use appropriate language"
        }
        
        return true to null
    }
}