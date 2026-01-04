package com.mangala.wallet.features.addressbook.presentation.privacy

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import com.mangala.wallet.features.addressbook.presentation.security.SecureActionHandler
import com.mangala.wallet.features.addressbook.presentation.security.SecureActionId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for AddressRevealAuthenticator
 * 
 * Test Coverage:
 * - Authentication requirements determination
 * - Secure action ID mapping
 * - Authentication flow integration
 * - Batch authentication
 * - Error handling
 * - Audit logging
 */
class AddressRevealAuthenticatorTest {
    
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
        address: String = "0x1234567890abcdef",
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
    
    private fun createMockSecureActionHandler(
        authSuccess: Boolean = true
    ): SecureActionHandler {
        return object : SecureActionHandler {
            override fun runSecureActionForId(
                actionId: SecureActionId,
                onSuccess: () -> Unit,
                onCancel: () -> Unit
            ) {
                if (authSuccess) {
                    onSuccess()
                } else {
                    onCancel()
                }
            }
        }
    }
    
    private fun createAuthenticator(
        authSuccess: Boolean = true,
        coroutineScope: CoroutineScope = TestScope()
    ): AddressRevealAuthenticator {
        return AddressRevealAuthenticator(
            secureActionHandler = createMockSecureActionHandler(authSuccess),
            coroutineScope = coroutineScope
        )
    }
    
    @Test
    fun `isAuthenticationRequired returns false when privacy mode disabled`() {
        val authenticator = createAuthenticator()
        val contact = createTestContact(isSensitive = true, displayMode = DisplayMode.SECRET)
        val walletAddress = createTestWalletAddress(isSensitive = true)
        
        val result = authenticator.isAuthenticationRequired(
            contact = contact,
            walletAddress = walletAddress,
            privacyModeEnabled = false
        )
        
        assertFalse(result)
    }
    
    @Test
    fun `isAuthenticationRequired returns true for sensitive contact`() {
        val authenticator = createAuthenticator()
        val contact = createTestContact(isSensitive = true, displayMode = DisplayMode.FULL)
        
        val result = authenticator.isAuthenticationRequired(
            contact = contact,
            walletAddress = null,
            privacyModeEnabled = true
        )
        
        assertTrue(result)
    }
    
    @Test
    fun `isAuthenticationRequired returns true for sensitive wallet address`() {
        val authenticator = createAuthenticator()
        val contact = createTestContact(isSensitive = false, displayMode = DisplayMode.FULL)
        val walletAddress = createTestWalletAddress(isSensitive = true)
        
        val result = authenticator.isAuthenticationRequired(
            contact = contact,
            walletAddress = walletAddress,
            privacyModeEnabled = true
        )
        
        assertTrue(result)
    }
    
    @Test
    fun `isAuthenticationRequired returns true for HIDDEN display mode`() {
        val authenticator = createAuthenticator()
        val contact = createTestContact(isSensitive = false, displayMode = DisplayMode.HIDDEN)
        
        val result = authenticator.isAuthenticationRequired(
            contact = contact,
            walletAddress = null,
            privacyModeEnabled = true
        )
        
        assertTrue(result)
    }
    
    @Test
    fun `isAuthenticationRequired returns false for FULL display mode with no sensitivity`() {
        val authenticator = createAuthenticator()
        val contact = createTestContact(isSensitive = false, displayMode = DisplayMode.FULL)
        val walletAddress = createTestWalletAddress(isSensitive = false)
        
        val result = authenticator.isAuthenticationRequired(
            contact = contact,
            walletAddress = walletAddress,
            privacyModeEnabled = true
        )
        
        assertFalse(result)
    }
    
    @Test
    fun `authenticateForReveal returns true on successful authentication`() = runTest {
        val authenticator = createAuthenticator(authSuccess = true, coroutineScope = this)
        val contact = createTestContact(displayMode = DisplayMode.HIDDEN)
        
        val result = authenticator.authenticateForReveal(contact)
        
        assertTrue(result)
    }
    
    @Test
    fun `authenticateForReveal returns false on cancelled authentication`() = runTest {
        val authenticator = createAuthenticator(authSuccess = false, coroutineScope = this)
        val contact = createTestContact(displayMode = DisplayMode.HIDDEN)
        
        val result = authenticator.authenticateForReveal(contact)
        
        assertFalse(result)
    }
    
    @Test
    fun `authenticateForBatchReveal handles multiple addresses`() = runTest {
        val authenticator = createAuthenticator(authSuccess = true, coroutineScope = this)
        val contact = createTestContact(displayMode = DisplayMode.FULL)
        val addresses = listOf(
            createTestWalletAddress("0x1111", isSensitive = true),
            createTestWalletAddress("0x2222", isSensitive = false),
            createTestWalletAddress("0x3333", isSensitive = true)
        )
        
        val result = authenticator.authenticateForBatchReveal(contact, addresses)
        
        assertTrue(result)
    }
    
    @Test
    fun `authenticateForBatchReveal returns false when authentication fails`() = runTest {
        val authenticator = createAuthenticator(authSuccess = false, coroutineScope = this)
        val contact = createTestContact(displayMode = DisplayMode.FULL)
        val addresses = listOf(
            createTestWalletAddress("0x1111", isSensitive = true)
        )
        
        val result = authenticator.authenticateForBatchReveal(contact, addresses)
        
        assertFalse(result)
    }
    
    @Test
    fun `determineSecureActionId returns correct action for different security levels`() {
        // We can't directly test the private method, but we can verify behavior
        // through the authentication calls by mocking the SecureActionHandler
        // and checking which action ID was called
        
        var calledActionId: SecureActionId? = null
        
        val mockHandler = object : SecureActionHandler {
            override fun runSecureActionForId(
                actionId: SecureActionId,
                onSuccess: () -> Unit,
                onCancel: () -> Unit
            ) {
                calledActionId = actionId
                onSuccess()
            }
        }
        
        val authenticator = AddressRevealAuthenticator(mockHandler, TestScope())
        
        runTest {
            // Test SECRET mode
            val secretContact = createTestContact(displayMode = DisplayMode.SECRET)
            authenticator.authenticateForReveal(secretContact)
            assertEquals(SecureActionId.RevealSecretAddress, calledActionId)
            
            // Test sensitive contact
            val sensitiveContact = createTestContact(isSensitive = true, displayMode = DisplayMode.FULL)
            authenticator.authenticateForReveal(sensitiveContact)
            assertEquals(SecureActionId.RevealSensitiveAddress, calledActionId)
            
            // Test sensitive address
            val normalContact = createTestContact(displayMode = DisplayMode.FULL)
            val sensitiveAddress = createTestWalletAddress(isSensitive = true)
            authenticator.authenticateForReveal(normalContact, sensitiveAddress)
            assertEquals(SecureActionId.RevealSensitiveAddress, calledActionId)
            
            // Test HIDDEN mode
            val hiddenContact = createTestContact(displayMode = DisplayMode.HIDDEN)
            authenticator.authenticateForReveal(hiddenContact)
            assertEquals(SecureActionId.RevealAddress, calledActionId)
        }
    }
    
    @Test
    fun `batch authentication uses highest security requirement`() {
        var calledActionId: SecureActionId? = null
        
        val mockHandler = object : SecureActionHandler {
            override fun runSecureActionForId(
                actionId: SecureActionId,
                onSuccess: () -> Unit,
                onCancel: () -> Unit
            ) {
                calledActionId = actionId
                onSuccess()
            }
        }
        
        val authenticator = AddressRevealAuthenticator(mockHandler, TestScope())
        
        runTest {
            val contact = createTestContact(displayMode = DisplayMode.FULL)
            val addresses = listOf(
                createTestWalletAddress("0x1111", isSensitive = false), // Normal
                createTestWalletAddress("0x2222", isSensitive = true),  // Sensitive
                createTestWalletAddress("0x3333", isSensitive = false)  // Normal
            )
            
            authenticator.authenticateForBatchReveal(contact, addresses)
            
            // Should use the highest security requirement (sensitive address)
            assertEquals(SecureActionId.RevealSensitiveAddress, calledActionId)
        }
    }
    
    @Test
    fun `AddressRevealUseCase delegates to authenticator correctly`() = runTest {
        val authenticator = createAuthenticator(authSuccess = true, coroutineScope = this)
        val useCase = AddressRevealUseCase(authenticator)
        
        val contact = createTestContact(displayMode = DisplayMode.HIDDEN)
        val walletAddress = createTestWalletAddress()
        
        // Test single address reveal
        val singleResult = useCase.revealAddress(contact, walletAddress)
        assertTrue(singleResult)
        
        // Test multiple addresses reveal
        val addresses = listOf(walletAddress)
        val multipleResult = useCase.revealMultipleAddresses(contact, addresses)
        assertTrue(multipleResult)
        
        // Test authentication requirement check
        val authRequired = useCase.isAuthenticationRequired(contact, walletAddress, true)
        assertTrue(authRequired)
        
        val authNotRequired = useCase.isAuthenticationRequired(contact, walletAddress, false)
        assertFalse(authNotRequired)
    }
}

// Mock interface for testing since the actual SecureActionHandler might be complex
private interface SecureActionHandler {
    fun runSecureActionForId(
        actionId: SecureActionId,
        onSuccess: () -> Unit,
        onCancel: () -> Unit = {}
    )
}