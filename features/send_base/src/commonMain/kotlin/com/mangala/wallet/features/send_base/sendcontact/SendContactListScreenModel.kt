package com.mangala.wallet.features.send_base.sendcontact

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import app.cash.paging.insertSeparators
import app.cash.paging.map
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetAllContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.CountWalletAddressesForContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesForContactUseCase
import com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class SendContactListScreenUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class SendContactNavigationEvent {
    data class NavigateToSelectAddress(
        val contactId: String,
        val accountId: String
    ) : SendContactNavigationEvent()

    data class NavigateToStep3(
        val accountId: String,
        val address: String,
        val blockchainUid: String,
    ) : SendContactNavigationEvent()
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SendContactListScreenModel(
    private val accountId: String,
    private val getAllContactsUseCase: GetAllContactsUseCase,
    private val countWalletAddressesForContactUseCase: CountWalletAddressesForContactUseCase,
    private val getWalletAddressesForContactUseCase: GetWalletAddressesForContactUseCase,
) : BaseScreenModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_TIME_MS = 300L
    }

    private val _uiState = MutableStateFlow(SendContactListScreenUiState())
    val uiState: StateFlow<SendContactListScreenUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<SendContactNavigationEvent>()
    val navigationEvents: Flow<SendContactNavigationEvent> = _navigationEvents.asSharedFlow()

    private val _contactsSearchQuery = MutableStateFlow<String?>(null)
    val contactsSearchQuery: StateFlow<String?> = _contactsSearchQuery.asStateFlow()

    private val _privacyModeEnabled = MutableStateFlow(false)
    val privacyModeEnabled: StateFlow<Boolean> = _privacyModeEnabled.asStateFlow()

    val contactsPagingFlow: Flow<PagingData<ContactGroupedByAlphabetUiModel>> =
        contactsSearchQuery
            .debounce(SEARCH_DEBOUNCE_TIME_MS)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                getAllContactsUseCase.getPaginatedContacts(searchQuery = query)
            }
            .cachedIn(screenModelScope)
            .map { pagingData ->
                pagingData
                    .map { contact ->
                        ContactGroupedByAlphabetUiModel.ContactItem(contact)
                    }
                    .insertSeparators { before, after ->
                        if (after == null) return@insertSeparators null

                        val afterChar = after.contact.contactName.first().uppercaseChar()

                        if (before == null) {
                            return@insertSeparators ContactGroupedByAlphabetUiModel.AlphabetHeader(
                                afterChar.toString()
                            )
                        }

                        val beforeChar = before.contact.contactName.first().uppercaseChar()

                        if (afterChar != beforeChar) {
                            ContactGroupedByAlphabetUiModel.AlphabetHeader(afterChar.toString())
                        } else {
                            null
                        }
                    }
            }

    fun updateContactsSearchQuery(query: String) {
        _contactsSearchQuery.value = query.takeIf { it.isNotBlank() }
    }

    fun togglePrivacyMode() {
        _privacyModeEnabled.value = !_privacyModeEnabled.value
    }

    fun onContactSelected(contact: ContactModel) {
        lifecycleScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Smart routing logic: Check number of addresses for the contact
                val count =
                    countWalletAddressesForContactUseCase(contact.contactId)

                when (count) {
                    0 -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "No addresses found for this contact"
                        )
                    }

                    1 -> {
                        // Single address: Validate network support before navigation
                        val address = getWalletAddressesForContactUseCase(
                            contact.contactId,
                            limit = 1
                        ).first()

                        // Check if the blockchain network is supported for sending
                        if (isNetworkSupportedForSending(address.blockchainNetworkId)) {
                            _navigationEvents.emit(
                                SendContactNavigationEvent.NavigateToStep3(
                                    accountId = accountId,
                                    address = address.address,
                                    blockchainUid = address.blockchainNetworkId,
                                )
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Does not support sending token on ${address.networkName}."
                            )
                        }
                    }

                    else -> {
                        // Multiple addresses: Navigate to address selection
                        _navigationEvents.emit(
                            SendContactNavigationEvent.NavigateToSelectAddress(
                                contactId = contact.contactId,
                                accountId = accountId
                            )
                        )
                    }
                }

                _uiState.value = _uiState.value.copy(isLoading = false)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to process contact selection: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Check if the network type is supported for sending transactions
     * Currently supports EVM, ANTELOPE, and BITCOIN networks
     */
    private fun isNetworkSupportedForSending(blockchainUid: String): Boolean {
        return when (BlockchainType.fromUid(blockchainUid).networkType) {
            NetworkType.ANTELOPE -> true
            else -> false
        }
    }
}