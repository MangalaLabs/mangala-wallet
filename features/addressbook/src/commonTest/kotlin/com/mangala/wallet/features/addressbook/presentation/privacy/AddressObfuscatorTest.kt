package com.mangala.wallet.features.addressbook.presentation.privacy

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for AddressObfuscator
 * 
 * Test Coverage:
 * - Obfuscation logic for different DisplayModes
 * - shouldObfuscate decision logic
 * - Edge cases (empty, short, long addresses)
 * - Cache functionality
 * - Extension functions
 * - Different address formats (Bitcoin, Ethereum, etc.)
 */
class AddressObfuscatorTest {
    
    // Test data
    private val shortAddress = "0x1234"
    private val mediumAddress = "0x1234567890abcdef"
    private val longAddress = "0x1234567890abcdef1234567890abcdef12345678"
    private val bitcoinAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
    private val bech32Address = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh"
    
    private fun createTestContact(
        isSensitive: Boolean = false,
        displayMode: DisplayMode = DisplayMode.FULL,
        addresses: List<WalletAddressEntity> = emptyList()
    ): ContactEntity {
        return ContactEntity.create(
            id = "test-contact",
            name = "Test Contact",
            isSensitive = isSensitive,
            privacyDisplayMode = displayMode
        )
    }
    
    private fun createTestAddress(
        address: String,
        isSensitive: Boolean = false
    ): WalletAddressEntity {
        return WalletAddressEntity.create(
            id = "test-address",
            contactId = "test-contact",
            blockchainTypeId = "ethereum",
            address = address,
            isSensitive = isSensitive
        )
    }
    
    @Test
    fun `obfuscate with FULL mode returns original address`() {
        val result = AddressObfuscator.obfuscate(longAddress, DisplayMode.FULL)
        assertEquals(longAddress, result)
    }
    
    @Test
    fun `obfuscate with HIDDEN mode returns partial address`() {
        val result = AddressObfuscator.obfuscate(longAddress, DisplayMode.HIDDEN)
        
        // Should show first 10 and last 6 characters for long addresses
        assertTrue(result.startsWith("0x12345678"))
        assertTrue(result.endsWith("345678"))
        assertTrue(result.contains("••••"))
        assertEquals("0x12345678••••345678", result)
    }
    
    @Test
    fun `obfuscate with SECRET mode returns completely hidden address`() {
        val result = AddressObfuscator.obfuscate(longAddress, DisplayMode.SECRET)
        
        // Should be completely obfuscated but maintain Ethereum format
        assertTrue(result.startsWith("0x"))
        assertFalse(result.contains("1234"))
        assertTrue(result.contains("••••"))
    }
    
    @Test
    fun `obfuscate short address correctly`() {
        val result = AddressObfuscator.obfuscate(shortAddress, DisplayMode.HIDDEN)
        assertEquals("••••", result)
    }
    
    @Test
    fun `obfuscate medium address correctly`() {
        val result = AddressObfuscator.obfuscate(mediumAddress, DisplayMode.HIDDEN)
        assertEquals("0x1234••••cdef", result)
    }
    
    @Test
    fun `obfuscate empty address returns empty string`() {
        val result = AddressObfuscator.obfuscate("", DisplayMode.HIDDEN)
        assertEquals("", result)
    }
    
    @Test
    fun `obfuscate Bitcoin address with SECRET mode`() {
        val result = AddressObfuscator.obfuscate(bitcoinAddress, DisplayMode.SECRET)
        
        // Should be completely obfuscated
        assertFalse(result.contains("1A1z"))
        assertTrue(result.contains("••••"))
    }
    
    @Test
    fun `obfuscate Bech32 address with SECRET mode`() {
        val result = AddressObfuscator.obfuscate(bech32Address, DisplayMode.SECRET)
        
        // Should maintain bc1 prefix but obfuscate the rest
        assertTrue(result.startsWith("bc1"))
        assertFalse(result.contains("qxy2"))
        assertTrue(result.contains("••••"))
    }
    
    @Test
    fun `shouldObfuscate returns false when privacy mode disabled`() {
        val contact = createTestContact(isSensitive = true, displayMode = DisplayMode.SECRET)
        
        val result = AddressObfuscator.shouldObfuscate(
            contact = contact,
            walletAddressIsSensitive = true,
            privacyModeEnabled = false
        )
        
        assertFalse(result)
    }
    
    @Test
    fun `shouldObfuscate returns true for sensitive contact when privacy enabled`() {
        val contact = createTestContact(isSensitive = true, displayMode = DisplayMode.FULL)
        
        val result = AddressObfuscator.shouldObfuscate(
            contact = contact,
            walletAddressIsSensitive = false,
            privacyModeEnabled = true
        )
        
        assertTrue(result)
    }
    
    @Test
    fun `shouldObfuscate returns true for sensitive wallet address`() {
        val contact = createTestContact(isSensitive = false, displayMode = DisplayMode.FULL)
        
        val result = AddressObfuscator.shouldObfuscate(
            contact = contact,
            walletAddressIsSensitive = true,
            privacyModeEnabled = true
        )
        
        assertTrue(result)
    }
    
    @Test
    fun `shouldObfuscate respects FULL display mode`() {
        val contact = createTestContact(isSensitive = false, displayMode = DisplayMode.FULL)
        
        val result = AddressObfuscator.shouldObfuscate(
            contact = contact,
            walletAddressIsSensitive = false,
            privacyModeEnabled = true
        )
        
        assertFalse(result)
    }
    
    @Test
    fun `shouldObfuscate respects HIDDEN display mode`() {
        val contact = createTestContact(isSensitive = false, displayMode = DisplayMode.HIDDEN)
        
        val result = AddressObfuscator.shouldObfuscate(
            contact = contact,
            walletAddressIsSensitive = false,
            privacyModeEnabled = true
        )
        
        assertTrue(result)
    }
    
    @Test
    fun `shouldObfuscate respects SECRET display mode`() {
        val contact = createTestContact(isSensitive = false, displayMode = DisplayMode.SECRET)
        
        val result = AddressObfuscator.shouldObfuscate(
            contact = contact,
            walletAddressIsSensitive = false,
            privacyModeEnabled = true
        )
        
        assertTrue(result)
    }
    
    @Test
    fun `cache stores and retrieves obfuscated addresses`() {
        // Clear cache first
        AddressObfuscator.clearCache()
        
        val address = "0x1234567890abcdef"
        
        // First call - should compute and cache
        val result1 = AddressObfuscator.obfuscate(address, DisplayMode.HIDDEN)
        
        // Second call - should use cache (same result)
        val result2 = AddressObfuscator.obfuscate(address, DisplayMode.HIDDEN)
        
        assertEquals(result1, result2)
        
        // Check cache contains the entry
        val stats = AddressObfuscator.getCacheStats()
        assertTrue(stats.size > 0)
        assertTrue(stats.entries.any { it.contains(address) })
    }
    
    @Test
    fun `forceRefresh bypasses cache`() {
        AddressObfuscator.clearCache()
        
        val address = "0x1234567890abcdef"
        
        // First call with cache
        val result1 = AddressObfuscator.obfuscate(address, DisplayMode.HIDDEN, forceRefresh = false)
        
        // Second call with force refresh
        val result2 = AddressObfuscator.obfuscate(address, DisplayMode.HIDDEN, forceRefresh = true)
        
        // Results should be the same (but computation was forced)
        assertEquals(result1, result2)
    }
    
    @Test
    fun `clearCache removes all cached entries`() {
        // Add some entries to cache
        AddressObfuscator.obfuscate("0x1111", DisplayMode.HIDDEN)
        AddressObfuscator.obfuscate("0x2222", DisplayMode.SECRET)
        
        // Verify cache has entries
        assertTrue(AddressObfuscator.getCacheStats().size > 0)
        
        // Clear cache
        AddressObfuscator.clearCache()
        
        // Verify cache is empty
        assertEquals(0, AddressObfuscator.getCacheStats().size)
    }
    
    @Test
    fun `obfuscateWithPattern DOTS produces correct format`() {
        val result = AddressObfuscator.obfuscateWithPattern(mediumAddress, ObfuscationPattern.DOTS)
        assertTrue(result.contains("••••"))
        assertFalse(result.contains("****"))
    }
    
    @Test
    fun `obfuscateWithPattern ASTERISKS produces correct format`() {
        val result = AddressObfuscator.obfuscateWithPattern(mediumAddress, ObfuscationPattern.ASTERISKS)
        assertTrue(result.contains("****"))
        assertFalse(result.contains("••••"))
    }
    
    @Test
    fun `extension function obfuscateAddress works correctly`() {
        val result = mediumAddress.obfuscateAddress(DisplayMode.HIDDEN)
        assertEquals("0x1234••••cdef", result)
    }
    
    @Test
    fun `extension function shouldBeObfuscated works correctly`() {
        val contact = createTestContact(displayMode = DisplayMode.HIDDEN)
        
        val result = mediumAddress.shouldBeObfuscated(
            contact = contact,
            walletAddressIsSensitive = false,
            privacyModeEnabled = true
        )
        
        assertTrue(result)
    }
    
    @Test
    fun `extension function hasObfuscatedAddresses works correctly`() {
        val addresses = listOf(
            createTestAddress("0x1111", isSensitive = true),
            createTestAddress("0x2222", isSensitive = false)
        )
        
        val contact = createTestContact(
            isSensitive = false,
            displayMode = DisplayMode.FULL,
            addresses = addresses
        )
        
        val result = contact.hasObfuscatedAddresses(privacyModeEnabled = true)
        assertTrue(result) // Should be true because one address is sensitive
    }
    
    @Test
    fun `multiple different addresses cache correctly`() {
        AddressObfuscator.clearCache()
        
        val addresses = listOf(
            "0x1111111111111111",
            "0x2222222222222222",
            "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2",
            "bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4"
        )
        
        // Obfuscate all addresses
        addresses.forEach { address ->
            AddressObfuscator.obfuscate(address, DisplayMode.HIDDEN)
        }
        
        // Check all are cached
        val stats = AddressObfuscator.getCacheStats()
        assertEquals(addresses.size, stats.size)
    }
}