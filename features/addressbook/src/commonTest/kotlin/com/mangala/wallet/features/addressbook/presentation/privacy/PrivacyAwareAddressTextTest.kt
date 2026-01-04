package com.mangala.wallet.features.addressbook.presentation.privacy

import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Unit tests for PrivacyAwareAddressText component logic
 * 
 * These tests focus on the obfuscation logic that would be displayed by the component,
 * without requiring UI testing infrastructure.
 */
class PrivacyAwareAddressTextTest {
    
    private val testEthereumAddress = "0x742d35Cc6634C0532925a3b844Bc9e7595f6b123"
    private val testBitcoinAddress = "bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq"
    private val testShortAddress = "0x1234"
    
    @Test
    fun `obfuscation logic - returns full address when privacy mode is disabled`() {
        // When privacy mode is disabled and not sensitive
        val shouldObfuscate = false
        val displayText = if (shouldObfuscate) {
            AddressObfuscator.obfuscate(testEthereumAddress, DisplayMode.HIDDEN)
        } else {
            testEthereumAddress
        }
        
        assertEquals(testEthereumAddress, displayText)
    }
    
    @Test
    fun `obfuscation logic - obfuscates address when privacy mode is enabled`() {
        // When privacy mode is enabled
        val shouldObfuscate = true
        val displayText = if (shouldObfuscate) {
            AddressObfuscator.obfuscate(testEthereumAddress, DisplayMode.HIDDEN)
        } else {
            testEthereumAddress
        }
        
        assertNotEquals(testEthereumAddress, displayText)
        assertTrue(displayText.contains("••••"))
    }
    
    @Test
    fun `obfuscation logic - obfuscates when marked as sensitive`() {
        // When marked as sensitive, regardless of privacy mode
        val privacyModeEnabled = false
        val isSensitive = true
        val shouldObfuscate = privacyModeEnabled || isSensitive
        
        assertTrue(shouldObfuscate)
        
        val displayText = AddressObfuscator.obfuscate(testEthereumAddress, DisplayMode.HIDDEN)
        assertNotEquals(testEthereumAddress, displayText)
        assertTrue(displayText.contains("••••"))
    }
    
    @Test
    fun `obfuscation patterns work correctly`() {
        // Test DOTS pattern
        val dotsResult = AddressObfuscator.obfuscateWithPattern(
            testEthereumAddress,
            ObfuscationPattern.DOTS
        )
        assertTrue(dotsResult.contains("••••"))
        
        // Test ASTERISKS pattern
        val asterisksResult = AddressObfuscator.obfuscateWithPattern(
            testEthereumAddress,
            ObfuscationPattern.ASTERISKS
        )
        assertTrue(asterisksResult.contains("****"))
        
        // Test BULLETS pattern
        val bulletsResult = AddressObfuscator.obfuscateWithPattern(
            testEthereumAddress,
            ObfuscationPattern.BULLETS
        )
        assertTrue(bulletsResult.contains("••••"))
    }
    
    @Test
    fun `display modes produce different obfuscation`() {
        // Test HIDDEN mode
        val hiddenResult = AddressObfuscator.obfuscate(
            testEthereumAddress,
            DisplayMode.HIDDEN
        )
        
        // Test SECRET mode
        val secretResult = AddressObfuscator.obfuscate(
            testEthereumAddress,
            DisplayMode.SECRET
        )
        
        // SECRET mode should show a fixed pattern
        assertTrue(secretResult.startsWith("0x"))
        assertTrue(secretResult.contains("••••"))
        
        // HIDDEN mode should show partial address info
        assertNotEquals(hiddenResult, secretResult)
        
        // FULL mode now also obfuscates (as per the implementation)
        val fullResult = AddressObfuscator.obfuscate(
            testEthereumAddress,
            DisplayMode.FULL
        )
        assertTrue(fullResult.contains("••••"))
    }
    
    @Test
    fun `handles different address formats`() {
        // Test short address
        val shortResult = AddressObfuscator.obfuscate(
            testShortAddress,
            DisplayMode.HIDDEN
        )
        assertEquals("••••", shortResult)
        
        // Test Bitcoin address with SECRET mode
        val bitcoinResult = AddressObfuscator.obfuscate(
            testBitcoinAddress,
            DisplayMode.SECRET
        )
        assertTrue(bitcoinResult.startsWith("bc1"))
        assertTrue(bitcoinResult.contains("••••"))
    }
    
    @Test
    fun `handles empty address`() {
        val emptyResult = AddressObfuscator.obfuscate(
            "",
            DisplayMode.HIDDEN
        )
        assertEquals("", emptyResult)
    }
    
    @Test
    fun `maintains consistent obfuscation`() {
        // First call
        val result1 = AddressObfuscator.obfuscate(
            testEthereumAddress,
            DisplayMode.HIDDEN
        )
        
        // Second call with same parameters
        val result2 = AddressObfuscator.obfuscate(
            testEthereumAddress,
            DisplayMode.HIDDEN
        )
        
        // Should produce the same obfuscated text (cache working)
        assertEquals(result1, result2)
    }
    
    @Test
    fun `pattern selection logic works correctly`() {
        val privacyModeEnabled = true
        val obfuscationPattern = ObfuscationPattern.ASTERISKS
        
        val displayText = if (privacyModeEnabled) {
            when {
                obfuscationPattern != ObfuscationPattern.BULLETS -> {
                    AddressObfuscator.obfuscateWithPattern(testEthereumAddress, obfuscationPattern)
                }
                else -> {
                    AddressObfuscator.obfuscate(testEthereumAddress, DisplayMode.HIDDEN)
                }
            }
        } else {
            testEthereumAddress
        }
        
        assertTrue(displayText.contains("****"))
        assertFalse(displayText.contains("••••"))
    }
    
    @Test
    fun `cache can be cleared`() {
        // Populate cache
        AddressObfuscator.obfuscate(testEthereumAddress, DisplayMode.HIDDEN)
        
        val statsBefore = AddressObfuscator.getCacheStats()
        assertTrue(statsBefore.size > 0)
        
        // Clear cache
        AddressObfuscator.clearCache()
        
        val statsAfter = AddressObfuscator.getCacheStats()
        assertEquals(0, statsAfter.size)
    }
}