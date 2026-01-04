package com.mangala.wallet.features.send_base.step2

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckAccountNotExistsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.address.IsValidBitcoinAddressUseCase
import com.mangala.wallet.features.chains.evmcompatible.core.AddressValidator
import com.mangala.wallet.features.contacts.domain.usecases.CreateContactUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.ui.RecipientValidationStatus
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Step2SelectNetworkScreenModel(
    address: String?, // Address passed from QR code flow
    val networkType: NetworkType,
    private val createContactUseCase: CreateContactUseCase,
    private val antelopeValidateAccountUseCase: ValidateAccountUseCase,
    private val checkAccountNotExistsUseCase: CheckAccountNotExistsUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider,
    private val isValidBitcoinAddressUseCase: IsValidBitcoinAddressUseCase,
    private val parseQRCodeResultUseCase: ParseQRCodeResultUseCase
): BaseScreenModel() {

    private val allNetworks = BlockchainNetworkData.getAllBlockchainNetworkSupported(buildEnvironmentProvider.isDevelopmentEnvironment()).filter { it.blockchainType.networkType == networkType }
    val networks = mutableStateOf(allNetworks)
    val selectedNetwork = mutableStateOf<BlockchainNetworkData?>(null)
    val showNetworkList = mutableStateOf(true)
    val doneSelectNetwork = mutableStateOf(false)
    val selectedAddress = mutableStateOf<String?>(null)
    val doneSelectAddress = mutableStateOf(false)

    val selectedName = mutableStateOf<String?>(null)
    val doneSelectName = mutableStateOf(false)

    private val _sendNetworkAddressState: MutableStateFlow<SendNetworkAddressState?> = MutableStateFlow(null)
    val sendNetworkAddressState: StateFlow<SendNetworkAddressState?> = _sendNetworkAddressState.asStateFlow()

    private val _uiState: MutableStateFlow<Step2SelectNetworkScreenUiState> = MutableStateFlow(
        Step2SelectNetworkScreenUiState(
            networks = allNetworks,
            selectedNetwork = null,
            recipientValidationStatus = RecipientValidationStatus.NotValidated
        )
    )
    val uiState: StateFlow<Step2SelectNetworkScreenUiState> = _uiState.asStateFlow()

    init {
        selectedAddress.value = address
    }

    fun filterNetworks(query: String) {
        networks.value = allNetworks.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun onPasteAddress(value: String) {
        val qrCodeResult = parseQRCodeResultUseCase(value)

        val address = when (qrCodeResult) {
            is QrCodeData.Payment -> qrCodeResult.address
            else -> value
        }

        selectedAddress.value = address
        onDoneAddress()
    }

    fun onAddressChange(value: String) {
        selectedAddress.value = value
        checkValidAddress()
    }

    fun onDoneAddress() {
        if (!doneSelectAddress.value) {
            doneSelectAddress.value = true
            checkValidAddress(validateExistence = true)
        }
    }

    private fun checkValidAddress(validateExistence: Boolean = false) {
        val accountId = selectedAddress.value.toString()

        when (networkType) {
            NetworkType.EVM -> {
                val isValidAddress = AddressValidator.isAddressValid(accountId)

                updateAddressValidationResult(isValidAddress)
            }

            NetworkType.ANTELOPE -> {
                val selectedNetwork = selectedNetwork.value ?: return

                screenModelScope.launch {
                    val isValidAccount =
                        antelopeValidateAccountUseCase.validateAccountName(accountId)

                    _uiState.update {
                        it.copy(recipientValidationStatus = RecipientValidationStatus.Validating)
                    }

                    if (validateExistence) {
                        val isValidRecipient = if (isValidAccount) checkAccountNotExistsUseCase(
                            selectedNetwork.blockchainType,
                            accountId
                        ).not() else false
                        updateAddressValidationResult(isValidRecipient)
                    } else {
                        updateAddressValidationResult(isValidAccount)
                    }
                }
            }

            NetworkType.BITCOIN -> {
                val selectedNetwork = selectedNetwork.value ?: return

                val isValidAddress = isValidBitcoinAddressUseCase(selectedNetwork.blockchainType, accountId)

                updateAddressValidationResult(isValidAddress)
            }

            else -> throw UnsupportedOperationException("Unsupported network type: $networkType")
        }
    }

    private fun updateAddressValidationResult(isValidAddress: Boolean) {
        if (!isValidAddress) {
            _uiState.update {
                it.copy(recipientValidationStatus = RecipientValidationStatus.Invalid)
            }
        } else {
            _uiState.update {
                it.copy(recipientValidationStatus = RecipientValidationStatus.Valid)
            }
        }
    }

    fun clickContinue() {
        screenModelScope.launch {
            var id: Long? = null
            if(!selectedName.value.isNullOrEmpty()){
                val contact = ContactEntity(
                    id = 0,
                    name = selectedName.value.toString(),
                    address = selectedAddress.value.toString(),
                    blockchainUid = selectedNetwork.value?.blockChainUid ?: ""
                )
                id = createContactUseCase.invoke(contact)
            }

            _sendNetworkAddressState.value = SendNetworkAddressState(
                contactId = id,
                blockchainUid = selectedNetwork.value?.blockChainUid,
                address = selectedAddress.value.toString()
            )
        }
    }

    fun clearState() {
        _sendNetworkAddressState.value = null
    }
}

data class SendNetworkAddressState(
    val contactId: Long?,
    val blockchainUid: String?,
    val address: String?,
)