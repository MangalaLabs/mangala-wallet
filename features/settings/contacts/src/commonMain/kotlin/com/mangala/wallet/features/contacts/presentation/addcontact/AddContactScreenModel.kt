package com.mangala.wallet.features.contacts.presentation.addcontact

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckAccountNotExistsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.features.chains.evmcompatible.core.AddressValidator
import com.mangala.wallet.features.contacts.domain.usecases.CreateContactUseCase
import com.mangala.wallet.features.contacts.domain.usecases.UpdateContactUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.ui.RecipientValidationStatus
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddContactScreenModel(
    contactName: String,
    address: String,
    blockchainUid: String,
    private val antelopeValidateAccountUseCase: ValidateAccountUseCase,
    private val createContactUseCase: CreateContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase,
    private val checkAccountNotExistsUseCase: CheckAccountNotExistsUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
) : BaseScreenModel() {

    private val _uiModel = MutableStateFlow(
        AddContactScreenUiModel(
            name = contactName,
            address = address,
            network = BlockchainNetworkData.getBlockchainByUid(blockchainUid, buildEnvironmentProvider.isDevelopmentEnvironment())
        )
    )
    val uiModel: StateFlow<AddContactScreenUiModel> get() = _uiModel
    private var inputJob: Job? = null

    fun onNameChanged(name: String) {
        _uiModel.update { it.copy(name = name) }
    }

    fun onNetworkSelected(selectedNetwork: BlockchainNetworkData) {
        _uiModel.update {
            it.copy(
                network = selectedNetwork,
                address = ""
            )
        }

        onDoneAddress()
    }

    fun onDoneAddress() {
        val address = _uiModel.value.address
        if (address.isBlank()) return

        _uiModel.update { it.copy(addressDone = true) }

        checkValidAddress(address, validateExistence = true)
    }

    fun onAddressChanged(address: String) {

        inputJob?.cancel()
        inputJob = screenModelScope.launch {
            _uiModel.update {
                it.copy(
                    address = address,
                )
            }
            delay(debouncePeriod)

            withContext(Dispatchers.IO){
                checkValidAddress(address, validateExistence = true)
            }
        }
    }

    fun onSaveContact() {
        val uiModel = _uiModel.value
        if (uiModel.network == null || uiModel.name.isBlank() || uiModel.address.isBlank()) return

        createContactUseCase(
            ContactEntity(
                id = 0,
                name = uiModel.name,
                blockchainUid = uiModel.network.blockChainUid,
                address = uiModel.address
            )
        )
    }

    fun onUpdateContact(contactId: Long) {
        val uiModel = _uiModel.value
        if (uiModel.network == null || uiModel.name.isBlank() || uiModel.address.isBlank()) return

        updateContactUseCase(
            ContactEntity(
                id = contactId,
                name = uiModel.name,
                blockchainUid = uiModel.network.blockChainUid,
                address = uiModel.address
            )
        )
    }

    private fun checkValidAddress(address: String, validateExistence: Boolean = false) {
        val blockchainType = _uiModel.value.network?.blockchainType
        val networkType = blockchainType?.networkType

        when (networkType) {
            NetworkType.EVM -> {
                val result = AddressValidator.isAddressValid(address)

                _uiModel.update {
                    it.copy(
                        recipientValidationStatus = if (result) RecipientValidationStatus.Valid else RecipientValidationStatus.Invalid,
                        addressDone = result
                    )
                }
            }

            NetworkType.ANTELOPE -> {
                screenModelScope.launch {
                    val isValidAccount =
                        antelopeValidateAccountUseCase.validateAccountName(address)

                    _uiModel.update {
                        it.copy(
                            recipientValidationStatus = RecipientValidationStatus.Validating,
                            addressDone = true
                        )
                    }

                    if (validateExistence) {
                        val isValidRecipient = if (isValidAccount) checkAccountNotExistsUseCase(
                            blockchainType,
                            address
                        ).not() else false
                        updateAddressValidationResult(isValidRecipient)
                    } else {
                        updateAddressValidationResult(isValidAccount)
                    }
                }
            }

            else -> AddressValidator.isAddressValid(address)
        }
    }

    private fun updateAddressValidationResult(isValidAddress: Boolean) {
        if (!isValidAddress) {
            _uiModel.update {
                it.copy(recipientValidationStatus = RecipientValidationStatus.Invalid)
            }
        } else {
            _uiModel.update {
                it.copy(recipientValidationStatus = RecipientValidationStatus.Valid)
            }
        }
    }

    companion object {
        private const val debouncePeriod = 300L
    }
}