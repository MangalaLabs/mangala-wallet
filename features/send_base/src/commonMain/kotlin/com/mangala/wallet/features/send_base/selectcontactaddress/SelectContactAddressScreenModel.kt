package com.mangala.wallet.features.send_base.selectcontactaddress

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SelectContactAddressScreenModel(
    private val contactId: String,
    private val accountId: String,
    private val contactRepository: ContactRepository,
    private val walletAddressRepository: WalletAddressRepository,
    private val blockchainRepository: BlockchainRepository
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow<SelectContactAddressScreenUiState>(
        SelectContactAddressScreenUiState.Loading
    )
    val uiState: StateFlow<SelectContactAddressScreenUiState> = _uiState.asStateFlow()

    private val _selectedAddress = MutableStateFlow<WalletAddressWithNetworkModel?>(null)
    val selectedAddress: StateFlow<WalletAddressWithNetworkModel?> = _selectedAddress.asStateFlow()

    init {
        loadContactAndAddresses()
    }

    fun selectAddress(address: WalletAddressWithNetworkModel) {
        _selectedAddress.value = address
        
        // Update UI state with selected address
        val currentState = _uiState.value
        if (currentState is SelectContactAddressScreenUiState.Data) {
            _uiState.value = currentState.copy(selectedAddress = address)
        }
    }

    fun getSelectedAddressForNavigation(): WalletAddressWithNetworkModel? {
        return _selectedAddress.value
    }

    private fun loadContactAndAddresses() {
        screenModelScope.launch {
            try {
                _uiState.value = SelectContactAddressScreenUiState.Loading

                // Load contact details
                val contact = contactRepository.getContactById(contactId)
                if (contact == null) {
                    _uiState.value = SelectContactAddressScreenUiState.Error("Contact not found")
                    return@launch
                }

                // Load all addresses for the contact
                val addresses = walletAddressRepository.getWalletAddressesForContact(contactId)
                if (addresses.isEmpty()) {
                    _uiState.value = SelectContactAddressScreenUiState.Error("No addresses found for this contact")
                    return@launch
                }

                // Group addresses by blockchain
                val addressGroups = addresses.groupBy { it.blockchainNetworkId }
                    .map { (blockchainId, addressList) ->
                        // Get blockchain details
                        val blockchain = blockchainRepository.getBlockchainTypeById(blockchainId)
                        BlockchainAddressGroup(
                            blockchainId = blockchainId,
                            blockchainName = blockchain?.name ?: "Unknown",
                            blockchainSymbol = blockchain?.symbol ?: "",
                            blockchainIcon = blockchain?.icon,
                            blockchainColor = blockchain?.color,
                            addresses = addressList.sortedBy { !it.isDefault } // Primary addresses first
                        )
                    }
                    .sortedBy { it.blockchainName }

                // Convert contact data to ContactModel
                val contactModel = ContactModel(
                    contactId = contact.id,
                    contactName = contact.name,
                    walletAddress = addresses.firstOrNull { it.isDefault }?.address ?: addresses.first().address,
                    walletAddressId = addresses.firstOrNull { it.isDefault }?.id ?: addresses.first().id,
                    walletAlias = addresses.firstOrNull { it.isDefault }?.alias ?: "",
                    walletSensitive = addresses.firstOrNull { it.isDefault }?.isSensitive ?: false,
                    blockchainName = addressGroups.firstOrNull()?.blockchainName ?: "",
                    blockchainSymbol = addressGroups.firstOrNull()?.blockchainSymbol ?: "",
                    blockchainIcon = addressGroups.firstOrNull()?.blockchainIcon ?: "",
                    blockChainColor = addressGroups.firstOrNull()?.blockchainColor ?: "",
                    isFavorite = false, // We'll get this from favorites if needed
                    isSensitive = contact.isSensitive ?: false,
                    addedTime = null // Will handle this properly later
                )

                _uiState.value = SelectContactAddressScreenUiState.Data(
                    contact = contactModel,
                    addressGroups = addressGroups
                )

            } catch (e: Exception) {
                _uiState.value = SelectContactAddressScreenUiState.Error(
                    "Failed to load contact: ${e.message}"
                )
            }
        }
    }
}