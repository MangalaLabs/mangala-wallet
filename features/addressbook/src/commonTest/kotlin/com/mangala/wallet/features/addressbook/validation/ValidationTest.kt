package com.mangala.wallet.features.addressbook.validation

import com.mangala.wallet.features.addressbook.domain.validation.WalletAddressValidator
import com.mangala.wallet.features.addressbook.domain.validation.WalletValidationResult
import kotlin.test.Test
import kotlin.test.assertTrue

class ValidationTest {
    
    private val validator = WalletAddressValidator()
    
    @Test
    fun testEthereumAddressValidation() {
        // Test case from user - 39 hex characters (missing 1)
        val invalidAddress = "0x742d35Cc6634C0532925a3b844Bc9e7595f8fA9"
        val result1 = validator.validateAddress(invalidAddress, "ETH")
        println("Test 39 chars: $invalidAddress -> $result1")
        assertTrue(result1 is WalletValidationResult.Error, "39 char address should be invalid")
        
        // Valid 40 hex characters
        val validAddress = "0x742d35Cc6634C0532925a3b844Bc9e7595f8fA9b"
        val result2 = validator.validateAddress(validAddress, "ETH")
        println("Test 40 chars: $validAddress -> $result2")
        assertTrue(result2 is WalletValidationResult.Success || result2 is WalletValidationResult.Warning, 
            "40 char address should be valid or warning")
        
        // 41 hex characters (too many)
        val tooLongAddress = "0x742d35Cc6634C0532925a3b844Bc9e7595f8fA9bb"
        val result3 = validator.validateAddress(tooLongAddress, "ETH")
        println("Test 41 chars: $tooLongAddress -> $result3")
        assertTrue(result3 is WalletValidationResult.Error, "41 char address should be invalid")
        
        // All lowercase (should be valid)
        val lowercaseAddress = "0x742d35cc6634c0532925a3b844bc9e7595f8fa9b"
        val result4 = validator.validateAddress(lowercaseAddress, "ETH")
        println("Test lowercase: $lowercaseAddress -> $result4")
        assertTrue(result4 is WalletValidationResult.Success, "Lowercase address should be valid")
        
        // All uppercase (currently gives warning)
        val uppercaseAddress = "0x742D35CC6634C0532925A3B844BC9E7595F8FA9B"
        val result5 = validator.validateAddress(uppercaseAddress, "ETH")
        println("Test uppercase: $uppercaseAddress -> $result5")
        // This gives warning due to mixed case check
        
        // Mixed case (gives warning)
        val mixedCaseAddress = "0x742d35Cc6634C0532925a3b844Bc9e7595f8fA9b"
        val result6 = validator.validateAddress(mixedCaseAddress, "ETH")
        println("Test mixed case: $mixedCaseAddress -> $result6")
        assertTrue(result6 is WalletValidationResult.Warning, "Mixed case should give warning")
    }
    
    @Test
    fun testCleanAddress() {
        // Test cleaning function
        val dirtyAddress = "  \"0x742d35Cc6634C0532925a3b844Bc9e7595f8fA9b\"  "
        val cleaned = validator.cleanAddress(dirtyAddress)
        println("Cleaned: '$dirtyAddress' -> '$cleaned'")
        assertTrue(cleaned == "0x742d35Cc6634C0532925a3b844Bc9e7595f8fA9b", "Should remove quotes and spaces")
        
        // Test with zero-width spaces
        val withZeroWidth = "0x742d\u200B35Cc\u200B6634C0532925a3b844Bc9e7595f8fA9b"
        val cleaned2 = validator.cleanAddress(withZeroWidth)
        println("Cleaned zero-width: ${withZeroWidth.length} chars -> ${cleaned2.length} chars")
        assertTrue(cleaned2 == "0x742d35Cc6634C0532925a3b844Bc9e7595f8fA9b", "Should remove zero-width spaces")
    }
}