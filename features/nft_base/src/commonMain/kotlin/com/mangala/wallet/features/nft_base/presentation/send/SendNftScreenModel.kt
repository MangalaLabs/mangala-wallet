package com.mangala.wallet.features.nft_base.presentation.send

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.evmcompatible.core.AddressValidator
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.contacts.domain.usecases.CreateContactUseCase
import com.mangala.wallet.features.contacts.domain.usecases.GetContactsByBlockchainUidUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.GetNftByTokenIdUseCase
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SendNftScreenModel(
    accountId: String,
    private val collectionContractAddress: String,
    private val tokenId: String,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getNftByTokenIdUseCase: GetNftByTokenIdUseCase,
    private val getContactsUseCase: GetContactsByBlockchainUidUseCase,
    private val createContactUseCase: CreateContactUseCase
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow<SendNftScreenUiState>(SendNftScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    lateinit var blockchainUid: String
        private set

    init {
        screenModelScope.launch {
            val nft = getNftByTokenIdUseCase(
                accountId,
                collectionContractAddress,
                tokenId
            )
            val selectedNetwork = getSelectedNetworkUseCase()
            blockchainUid = selectedNetwork.blockChainUid
            val contacts = getContactsUseCase(selectedNetwork.blockChainUid)
            _uiState.update {
                nft?.let {
                    SendNftScreenUiState.Success(
                        SendNftScreenUiModel(
                            it,
                            contacts,
                            contactsFilter = "",
                            address = "",
                            isValidAddress = false,
                            contactId = null,
                            isSaveRecipientEnabled = false,
                            newRecipientName = null,
                            isDoneSelectAddress = false,
                            isDoneEnterRecipientName = false,
                            isDoneEnterInfo = false,
                            blockchainType = selectedNetwork.blockchainType
                        )
                    )
                } ?: kotlin.run {
                    SendNftScreenUiState.Error("Error")
                }
            }
        }
    }

    fun onSelectContact(id: Long) {
        _uiState.update {
            (it as? SendNftScreenUiState.Success)?.let {
                val contacts = it.uiModel.contacts
                contacts.find { it.id == id }?.let { contact ->
                    it.copy(it.uiModel.copy(address = contact.address, isValidAddress = true, isDoneSelectAddress = true))
                } ?: it
            } ?: it
        }
    }

    fun onContactFilterChange(query: String) {
        _uiState.update {
            (it as? SendNftScreenUiState.Success)?.let {
                it.copy(it.uiModel.copy(contactsFilter = query))
            } ?: it
        }
    }

    fun onAddressChange(newAddress: String) {
        val isValidAddress = isValidAddress(newAddress)
        _uiState.update {
            (it as? SendNftScreenUiState.Success)?.let {
                it.copy(it.uiModel.copy(
                    address = try { Address(newAddress).eip55 } catch (e: Exception) { newAddress },
                    isValidAddress = isValidAddress,
                    isDoneSelectAddress = isValidAddress
                ))
            } ?: it
        }
    }

    fun setDoneSelectAddress(isDone: Boolean) {
        _uiState.update {
            (it as? SendNftScreenUiState.Success)?.let {
                it.copy(it.uiModel.copy(isDoneSelectAddress = isDone))
            } ?: it
        }
    }

    fun onToggleSaveRecipient(newValue: Boolean) {
        _uiState.update {
            (it as? SendNftScreenUiState.Success)?.let {
                it.copy(it.uiModel.copy(isSaveRecipientEnabled = newValue))
            } ?: it
        }
    }

    fun onNewRecipientNameChange(name: String) {
        _uiState.update {
            (it as? SendNftScreenUiState.Success)?.let {
                it.copy(it.uiModel.copy(newRecipientName = name))
            } ?: it
        }
    }

    fun onDoneSelectRecipientName(isDone: Boolean) {
        _uiState.update {
            (it as? SendNftScreenUiState.Success)?.let {
                it.copy(it.uiModel.copy(isDoneEnterRecipientName = isDone))
            } ?: it
        }
    }

    fun onClickContinue() {
        screenModelScope.launch {
            val uiState = _uiState.value as? SendNftScreenUiState.Success ?: return@launch
            val newRecipientName = uiState.uiModel.newRecipientName

            val id = if(newRecipientName.isNullOrEmpty().not()){
                val contact = ContactEntity(
                    id = 0,
                    name = newRecipientName.orEmpty(),
                    address = uiState.uiModel.address,
                    blockchainUid = blockchainUid
                )
                createContactUseCase.invoke(contact)
            } else null

            _uiState.update {
                (it as? SendNftScreenUiState.Success)?.let {
                    it.copy(it.uiModel.copy(isDoneEnterInfo = true, contactId = id))
                } ?: it
            }
        }
    }

    fun onConsumeIsDoneEnterInfo() {
        _uiState.update {
            (it as? SendNftScreenUiState.Success)?.let {
                it.copy(it.uiModel.copy(isDoneEnterInfo = false))
            } ?: it
        }
    }

    private fun isValidAddress(address: String?): Boolean {
        return try {
            address?.let { AddressValidator.isAddressValid(it) } ?: false
        } catch (e: Exception) {
            false
        }
    }
}