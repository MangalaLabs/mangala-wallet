package com.mangala.wallet.features.addressbook.presentation.contact.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.ContactDetailModel
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithBlockchainModel
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.domain.usecase.contact.DeleteContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.favorite.AddFavoriteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactDetailByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.NotifyContactsChangedUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.RemoveFavoriteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdateLastViewedAtUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesWithBlockchainByContactIdUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.mangala.wallet.features.addressbook.presentation.security.SecureAuthProvider

// Navigation events for contact detail
sealed class ContactDetailNavigationEvent {
    data object NavigateBack : ContactDetailNavigationEvent()
    data class NavigateToSelectAddress(
        val contactId: String,
        val accountId: String, // Account ID is not used in the send flow, but kept for consistency
    ) : ContactDetailNavigationEvent()
    data class NavigateToStep3(
        val accountId: String, // Account ID is not used in the send flow, but kept for consistency
        val address: String,
        val blockchainUid: String,
    ) : ContactDetailNavigationEvent()
}

// Constants
private const val NAVIGATION_EVENT_REPEAT_COUNT = 3
private const val NAVIGATION_EVENT_DELAY_MS = 100L

class ContactDetailScreenModel(
    private val contactId: String,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val getContactDetailByIdUseCase: GetContactDetailByIdUseCase,
    private val updateLastViewedAtUseCase: UpdateLastViewedAtUseCase,
    private val getWalletAddressesWithBlockchainByContactIdUseCase: GetWalletAddressesWithBlockchainByContactIdUseCase,
    private val notifyContactsChangedUseCase: NotifyContactsChangedUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
) : BaseScreenModel() {
    private val _uiState = MutableStateFlow(ContactDetailUiState())
    val uiState: StateFlow<ContactDetailUiState> = _uiState.asStateFlow()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Navigation events flow
    private val _navigationEvents = MutableSharedFlow<ContactDetailNavigationEvent>()
    val navigationEvents: Flow<ContactDetailNavigationEvent> = _navigationEvents.asSharedFlow()

    private var currentContactId: String? = null
    private var isDeleting = false
    
    // Expose deletion state
    val isDeletingState: Boolean
        get() = isDeleting

    init {
        loadContactDetails(contactId)
    }
    
    /**
     * Public method to reload contact details
     * This can be called after editing the contact
     */
    fun loadContactDetail() {
        // Use the current contactId directly to ensure it's always up-to-date
        // This fixes any potential issues where currentContactId might not be set correctly
        loadContactDetails(contactId)
        
    }

    private fun loadContactDetails(contactId: String) {
        // Skip loading if we're in the middle of deleting
        if (isDeleting) {
            return
        }
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        currentContactId = contactId
        

        screenModelScope.launch {
            try {
                val contactDetail = getContactDetailByIdUseCase(contactId)

                if (contactDetail != null) {
                    
                    updateLastViewedAtUseCase(contactId)

                    val isFavorite = contactDetail.isFavorite

                    val contact = contactDetail.contact
                    val hasHighSecurity = contact.securityLevel == SecurityLevel.HIGH ||
                            contact.securityLevel == SecurityLevel.MAXIMUM

                    if (hasHighSecurity && !_authState.value.isAuthenticated) {
                        if (!_authState.value.requiresAuth) {
                            _authState.update {
                                it.copy(
                                    requiresAuth = true,
                                    authMethod = contact.authRequirement
                                )
                            }
                        }

                        val limitedInfo = showLimitedContactInfo(contactDetail)
                        _uiState.update {
                            it.copy(
                                contactDetail = limitedInfo,
                                isHighSecurity = true,
                                isFavorite = isFavorite,
                                isLoading = false,
                                requiresAuth = true,
                                isFullInfoVisible = false,
                                error = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                contactDetail = contactDetail,
                                isFavorite = isFavorite,
                                isHighSecurity = hasHighSecurity,
                                isAuthenticated = _authState.value.isAuthenticated,
                                requiresAuth = hasHighSecurity,
                                isFullInfoVisible = contact.privacyDisplayMode == DisplayMode.FULL || _authState.value.isAuthenticated,
                                error = null
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Contact not found"
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        val currentState = _uiState.value
        val isFavorite = currentState.isFavorite


        screenModelScope.launch {
            try {
                // Cập nhật UI state trước tiên để phản hồi nhanh hơn đối với người dùng
                _uiState.update { it.copy(isFavorite = !isFavorite) }
                
                // Thực hiện thao tác với repository
                val success = if (isFavorite) {
                    removeFavoriteUseCase(contactId)
                } else {
                    addFavoriteUseCase(contactId)
                }

                // Nếu thao tác thất bại, đặt lại trạng thái
                if (!success) {
                    _uiState.update { it.copy(isFavorite = isFavorite) }
                    _uiState.update { it.copy(error = "Failed to update favorite status") }
                } else {
                    // Thành công - thông báo thay đổi để các màn hình khác cập nhật
                    val notifySuccess = notifyContactsChangedUseCase()
                }
            } catch (e: Exception) {
                // Khôi phục trạng thái trước đó nếu có lỗi
                _uiState.update { it.copy(isFavorite = isFavorite, error = "Failed to update favorite status: ${e.message}") }
            }
        }
    }

    fun deleteContact(contactId: String) {
        screenModelScope.launch {
            try {
                
                // Set deletion flag to prevent reload
                isDeleting = true
                
                val result = deleteContactUseCase(contactId)

                if (result) {
                    notifyContactsChangedUseCase()
                    // Emit multiple times to ensure it's caught
                    repeat(NAVIGATION_EVENT_REPEAT_COUNT) {
                        _navigationEvents.emit(ContactDetailNavigationEvent.NavigateBack)
                        delay(NAVIGATION_EVENT_DELAY_MS)
                    }
                } else {
                    _uiState.update { it.copy(error = "Failed to delete contact") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete contact: ${e.message}") }
            }
        }
    }

    /**
     * Xác thực người dùng để xem thông tin đầy đủ
     * This will be called from UI through SecureAuthProvider
     */
    fun onAuthenticationSuccess() {
        screenModelScope.launch {
            try {
                _authState.update { it.copy(isAuthenticated = true) }
                _uiState.update {
                    it.copy(
                        error = null,
                        isAuthenticated = true,
                        isFullInfoVisible = true
                    ) 
                }
                currentContactId?.let { id ->
                    loadContactDetails(id)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error loading full contact details: ${e.message}") }
            }
        }
    }
    
    fun onAuthenticationCancelled() {
        _uiState.update { it.copy(error = "Authentication cancelled") }
    }


    private fun showLimitedContactInfo(contactDetail: ContactDetailModel): ContactDetailModel {
        val limitedContact = contactDetail.contact.copy(
            notes = null,
            encryptedData = null,
        )

        val limitedWalletAddresses = contactDetail.walletAddresses

        return ContactDetailModel(
            contact = limitedContact,
            phoneNumbers = contactDetail.phoneNumbers,
            emailAddresses = contactDetail.emailAddresses,
            physicalAddresses = contactDetail.physicalAddresses,
            walletAddresses = limitedWalletAddresses,
            tags = contactDetail.tags,
            groups = contactDetail.groups,
            isFavorite = contactDetail.isFavorite,
            relatedNames = contactDetail.relatedNames,
            socialProfiles = contactDetail.socialProfiles,
            importantDates = contactDetail.importantDates
        )
    }

    // Removed applyPartialPrivacy(), applyFullPrivacy(), processAddress(), maskAddress()
    // These functions caused double-masking issues.
    // Privacy is now handled entirely by PrivacyAwareAddressText + AddressObfuscator
    
    /**
     * Smart routing for contact send flow using existing ContactDetailModel data
     */
    fun onContactSendClick() {
        screenModelScope.launch {
            try {
                val contactDetail = _uiState.value.contactDetail
                if (contactDetail == null) {
                    _uiState.update { it.copy(error = "Contact details not loaded") }
                    return@launch
                }

                val walletAddresses = contactDetail.walletAddresses
                
                when (walletAddresses.size) {
                    0 -> {
                        _uiState.update { 
                            it.copy(error = "No addresses found for this contact")
                        }
                    }

                    1 -> {
                        // Single address: Validate network support before navigation
                        val walletAddressWithBlockchain = walletAddresses.first()
                        val address = walletAddressWithBlockchain.walletAddress
                        val blockchainType = walletAddressWithBlockchain.blockchainType

                        // Check if the blockchain network is supported for sending
                        if (isNetworkSupportedForSending(blockchainType.id)) {
                            _navigationEvents.emit(
                                ContactDetailNavigationEvent.NavigateToStep3(
                                    accountId = "", // Account ID is not used in the send flow
                                    address = address.address,
                                    blockchainUid = blockchainType.id,
                                )
                            )
                        } else {
                            _uiState.update { 
                                it.copy(
                                    error = "Does not support sending token on ${blockchainType.name}."
                                )
                            }
                        }
                    }

                    else -> {
                        // Multiple addresses: Navigate to address selection
                        _navigationEvents.emit(
                            ContactDetailNavigationEvent.NavigateToSelectAddress(
                                contactId = contactDetail.contact.id,
                                accountId = "" // Account ID is not used in the send flow
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to process send request: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Clear error state - called after showing toast
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Check if the network type is supported for sending transactions
     * Currently supports ANTELOPE networks
     */
    private fun isNetworkSupportedForSending(blockchainUid: String): Boolean {
        return when (BlockchainType.fromUid(blockchainUid).networkType) {
            NetworkType.ANTELOPE -> true
            else -> false
        }
    }
}