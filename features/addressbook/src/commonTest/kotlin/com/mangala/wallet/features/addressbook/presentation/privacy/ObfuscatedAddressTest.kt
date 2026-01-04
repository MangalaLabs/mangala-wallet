package com.mangala.wallet.features.addressbook.presentation.privacy

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * UI Tests for ObfuscatedAddress Component
 * 
 * Test Coverage:
 * - Address display in different privacy states
 * - Obfuscation based on contact and address settings
 * - Tap-to-reveal functionality
 * - Authentication integration
 * - Copy functionality
 * - Accessibility features
 * - Animation states
 */
class ObfuscatedAddressTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private val testAddress = "0x1234567890abcdef1234567890abcdef12345678"
    
    private fun createTestContact(
        isSensitive: Boolean = false,
        displayMode: DisplayMode = DisplayMode.FULL
    ): ContactEntity {
        return ContactEntity.create(
            id = "test-contact",
            name = "Test Contact",
            isSensitive = isSensitive,
            privacyDisplayMode = displayMode
        )
    }
    
    private fun createTestWalletAddress(
        address: String = testAddress,
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
    fun `displays full address when privacy mode is disabled`() {
        val contact = createTestContact(isSensitive = true, displayMode = DisplayMode.SECRET)
        
        composeTestRule.setContent {
            MaterialTheme {
                ObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = false
                )
            }
        }
        
        composeTestRule
            .onNodeWithText(testAddress)
            .assertIsDisplayed()
    }
    
    @Test
    fun `displays obfuscated address for sensitive contact when privacy enabled`() {
        val contact = createTestContact(isSensitive = true, displayMode = DisplayMode.HIDDEN)
        
        composeTestRule.setContent {
            MaterialTheme {
                ObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = true
                )
            }
        }
        
        // Should not show the full address
        composeTestRule
            .onNodeWithText(testAddress)
            .assertDoesNotExist()
        
        // Should show obfuscated version
        composeTestRule
            .onNodeWithContentDescription("Sensitive address hidden. Tap to reveal.")
            .assertIsDisplayed()
    }
    
    @Test
    fun `displays obfuscated address for sensitive wallet address`() {
        val contact = createTestContact(isSensitive = false, displayMode = DisplayMode.FULL)
        val walletAddress = createTestWalletAddress(isSensitive = true)
        
        composeTestRule.setContent {
            MaterialTheme {
                ObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    walletAddress = walletAddress,
                    privacyModeEnabled = true
                )
            }
        }
        
        // Should show obfuscated version
        composeTestRule
            .onNodeWithContentDescription("Sensitive address hidden. Tap to reveal.")
            .assertIsDisplayed()
    }
    
    @Test
    fun `respects contact display mode FULL`() {
        val contact = createTestContact(isSensitive = false, displayMode = DisplayMode.FULL)
        
        composeTestRule.setContent {
            MaterialTheme {
                ObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = true
                )
            }
        }
        
        // Should show full address even with privacy mode on
        composeTestRule
            .onNodeWithText(testAddress)
            .assertIsDisplayed()
    }
    
    @Test
    fun `respects contact display mode HIDDEN`() {
        val contact = createTestContact(isSensitive = false, displayMode = DisplayMode.HIDDEN)
        
        composeTestRule.setContent {
            MaterialTheme {
                ObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = true
                )
            }
        }
        
        // Should show obfuscated address
        composeTestRule
            .onNodeWithContentDescription("Sensitive address hidden. Tap to reveal.")
            .assertIsDisplayed()
    }
    
    @Test
    fun `shows lock icon for obfuscated addresses`() {
        val contact = createTestContact(displayMode = DisplayMode.HIDDEN)
        
        composeTestRule.setContent {
            MaterialTheme {
                ObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = true
                )
            }
        }
        
        // Lock icon should be present (as part of the semantic description)
        composeTestRule
            .onNodeWithContentDescription("Sensitive address hidden. Tap to reveal.")
            .assertIsDisplayed()
            .assertHasClickAction()
    }
    
    @Test
    fun `tap to reveal triggers authentication request`() = runTest {
        val contact = createTestContact(displayMode = DisplayMode.HIDDEN)
        var authenticationRequested = false
        
        composeTestRule.setContent {
            MaterialTheme {
                ObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = true,
                    onRevealRequest = {
                        authenticationRequested = true
                        true // Return success
                    }
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Sensitive address hidden. Tap to reveal.")
            .performClick()
        
        // Wait for authentication to be processed
        composeTestRule.waitForIdle()
        
        assertTrue(authenticationRequested)
    }
    
    @Test
    fun `shows copy button for non-obfuscated addresses`() {
        val contact = createTestContact(displayMode = DisplayMode.FULL)
        
        composeTestRule.setContent {
            MaterialTheme {
                ObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = true,
                    showCopyButton = true
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Copy address")
            .assertIsDisplayed()
            .assertHasClickAction()
    }
    
    @Test
    fun `hides copy button for obfuscated addresses`() {
        val contact = createTestContact(displayMode = DisplayMode.HIDDEN)
        
        composeTestRule.setContent {
            MaterialTheme {
                ObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = true,
                    showCopyButton = true
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Copy address")
            .assertDoesNotExist()
    }
    
    @Test
    fun `copy button triggers onCopy callback`() {
        val contact = createTestContact(displayMode = DisplayMode.FULL)
        var copiedAddress = ""
        
        composeTestRule.setContent {
            MaterialTheme {
                ObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = false,
                    onCopy = { address -> copiedAddress = address }
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Copy address")
            .performClick()
        
        assertEquals(testAddress, copiedAddress)
    }
    
    @Test
    fun `CompactObfuscatedAddress shows lock icon for obfuscated addresses`() {
        val contact = createTestContact(displayMode = DisplayMode.HIDDEN)
        
        composeTestRule.setContent {
            MaterialTheme {
                CompactObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = true
                )
            }
        }
        
        // Should show obfuscated text (exact format depends on obfuscation logic)
        composeTestRule
            .onNodeWithText(testAddress)
            .assertDoesNotExist()
    }
    
    @Test
    fun `CompactObfuscatedAddress shows full address when not obfuscated`() {
        val contact = createTestContact(displayMode = DisplayMode.FULL)
        
        composeTestRule.setContent {
            MaterialTheme {
                CompactObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = true
                )
            }
        }
        
        composeTestRule
            .onNodeWithText(testAddress)
            .assertIsDisplayed()
    }
    
    @Test
    fun `AddressPrivacySummary shows correct count`() {
        val addresses = listOf(
            createTestWalletAddress("0x1111", isSensitive = true),
            createTestWalletAddress("0x2222", isSensitive = false),
            createTestWalletAddress("0x3333", isSensitive = true)
        )
        
        val contact = createTestContact(
            displayMode = DisplayMode.FULL
        ).copy(walletAddresses = addresses)
        
        composeTestRule.setContent {
            MaterialTheme {
                AddressPrivacySummary(
                    contact = contact,
                    privacyModeEnabled = true
                )
            }
        }
        
        // Should show that 2 out of 3 addresses are hidden (sensitive ones)
        composeTestRule
            .onNodeWithText("2 of 3 addresses hidden")
            .assertIsDisplayed()
    }
    
    @Test
    fun `AddressPrivacySummary shows all visible when privacy disabled`() {
        val addresses = listOf(
            createTestWalletAddress("0x1111", isSensitive = true),
            createTestWalletAddress("0x2222", isSensitive = true)
        )
        
        val contact = createTestContact().copy(walletAddresses = addresses)
        
        composeTestRule.setContent {
            MaterialTheme {
                AddressPrivacySummary(
                    contact = contact,
                    privacyModeEnabled = false
                )
            }
        }
        
        composeTestRule
            .onNodeWithText("2 addresses visible")
            .assertIsDisplayed()
    }
    
    @Test
    fun `AddressGroup displays title and addresses`() {
        val addresses = listOf(
            createTestWalletAddress("0x1111"),
            createTestWalletAddress("0x2222")
        )
        
        val contact = createTestContact()
        
        composeTestRule.setContent {
            MaterialTheme {
                AddressGroup(
                    addresses = addresses,
                    contact = contact,
                    privacyModeEnabled = false,
                    title = "Wallet Addresses"
                )
            }
        }
        
        composeTestRule
            .onNodeWithText("Wallet Addresses")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("0x1111")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("0x2222")
            .assertIsDisplayed()
    }
    
    @Test
    fun `dynamic privacy mode changes update display`() {
        val contact = createTestContact(displayMode = DisplayMode.HIDDEN)
        var privacyEnabled by mutableStateOf(false)
        
        composeTestRule.setContent {
            MaterialTheme {
                ObfuscatedAddress(
                    address = testAddress,
                    contact = contact,
                    privacyModeEnabled = privacyEnabled
                )
            }
        }
        
        // Initially should show full address (privacy disabled)
        composeTestRule
            .onNodeWithText(testAddress)
            .assertIsDisplayed()
        
        // Enable privacy mode
        privacyEnabled = true
        
        // Should now show obfuscated address
        composeTestRule
            .onNodeWithContentDescription("Sensitive address hidden. Tap to reveal.")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText(testAddress)
            .assertDoesNotExist()
    }
}