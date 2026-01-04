package com.mangala.eticket.presentation.onboard

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.eticket.data.local.securestorage.SecureStorageWrapperConstants.ACCESS_TOKEN
import com.mangala.eticket.data.local.securestorage.SecureStorageWrapperConstants.ACCESS_TOKEN_EXPIRATION
import com.mangala.eticket.data.local.securestorage.SecureStorageWrapperConstants.REFRESH_TOKEN
import com.mangala.eticket.data.local.securestorage.SecureStorageWrapperConstants.REFRESH_TOKEN_EXPIRATION
import com.mangala.eticket.data.local.securestorage.SecureStorageWrapperConstants.TOKEN_TYPE
import com.mangala.eticket.data.model.auth.AuthenticationResponse
import com.mangala.eticket.domain.usecases.auth.LoginUseCase
import com.mangala.eticket.domain.usecases.user.CheckUserHasAccountUseCase
import com.mangala.eticket.domain.usecases.user.RegisterUserUseCase
import com.mangala.wallet.core.address.domain.usecases.DeriveEthereumAddressUseCase
import com.mangala.wallet.core.hdwallet.domain.model.HDKey
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SignPersonalMessageUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.remote.di.ApiResponse
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent

/**
 * ViewModel of E-ticket onboard screen.
 * It has all function of E-ticket onboard screen
 */
class ETicketOnboardScreenModel(
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val generateHDKeyUseCase: GenerateHDKeyUseCase,
    private val deriveAddressUseCase: DeriveEthereumAddressUseCase,
    private val checkUserHasAccountUseCase: CheckUserHasAccountUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val signPersonalMessageUseCase: SignPersonalMessageUseCase,
    private val loginUseCase: LoginUseCase,
    private val secureStorage: SecureStorageWrapper
) : ScreenModel, KoinComponent {

    private var hdKey: HDKey? = null

    //state login --> ui to nav
    private val _hasAccount = MutableStateFlow(false)
    val hasAccount = _hasAccount.asStateFlow()

    private val _userFullName = MutableStateFlow("")
    val userFullName = _userFullName.asStateFlow()

    private val _unknownError = MutableStateFlow(false)
    val unknownError = _unknownError.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _messageToSign = MutableStateFlow<String?>("E_TICKET_SIGN_MESSAGE")
    val messageToSign = _messageToSign.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        screenModelScope.launch {
            _isLoading.value = true
            initializeHDKey()
            checkAccount()
            _isLoading.value = false
        }
    }

    private suspend fun checkAccount() {
        val publicKey: String? = getAddress()?.hex
        publicKey?.let {
            checkUserHasAccountUseCase.invoke(publicKey).let {
                if (it is ApiResponse.Success) {
                    _hasAccount.value = it.body.data == true
                } else {
                    _unknownError.value = true
                }
            }
        }
    }

    fun createUser() {
        screenModelScope.launch {
            // Retrieve wallet public key
            val publicKey: String? = getAddress()?.hex
            if (publicKey.isNullOrBlank()) {
                _unknownError.value = true
            } else {
                // register user
                registerUserUseCase.invoke(id = publicKey, fullName = userFullName.value).let {
                    if (it is ApiResponse.Success) {
                        _hasAccount.value = true
                    } else {
                        _unknownError.value = true
                    }
                }
            }
        }
    }

    fun onCreateUserFullNameChange(userFullName: String) {
        _userFullName.value = userFullName
    }

    fun login() {
        screenModelScope.launch {
            val signedSignatureAndPublicKey = signPersonalMessage()
            loginToETicket(signedSignatureAndPublicKey.first, signedSignatureAndPublicKey.second)?.let {
                secureStorage.saveValue(ACCESS_TOKEN, it.accessToken)
                secureStorage.saveValue(ACCESS_TOKEN_EXPIRATION, getTokenExpiration(it.tokenExpiredSeconds).toString())
                secureStorage.saveValue(REFRESH_TOKEN, it.refreshToken)
                secureStorage.saveValue(REFRESH_TOKEN_EXPIRATION, getTokenExpiration(it.refreshExpiredSeconds).toString())
                secureStorage.saveValue(TOKEN_TYPE, it.tokenType)
                _isLoggedIn.value = true
            } ?: run {
                _unknownError.value = true
            }
        }
    }

    private fun getAddress(): Address? {
        hdKey?.let {
            return Address(deriveAddressUseCase.invoke(it.publicKey))
        }
        return null
    }

    private suspend fun initializeHDKey() {
        val wallet = getSelectedWalletUseCase()
        val words = wallet?.words?.split(" ")
        val chainNetwork = getSelectedNetworkUseCase.invoke()
        val blockchainType = BlockchainType.fromUid(chainNetwork.blockChainUid)
        hdKey = generateHDKeyUseCase.invoke(
            words ?: listOf(),
            "",
            Blockchain(blockchainType, blockchainType.uid, ""),
            AddressType.Bip44
        )
    }

    private fun restrictHDKey() {
        screenModelScope.launch {
            val wallet = getSelectedWalletUseCase()
            val words = wallet?.words?.split(" ")
            val chainNetwork = getSelectedNetworkUseCase.invoke()
            val blockchainType = BlockchainType.fromUid(chainNetwork.blockChainUid)
            hdKey = generateHDKeyUseCase.invoke(
                words ?: listOf(),
                "",
                Blockchain(blockchainType, blockchainType.uid, ""),
                AddressType.Bip44
            )
        }
    }

    private suspend fun signPersonalMessage(): Pair<String, String?> {
        restrictHDKey()
        val chainNetwork = getSelectedNetworkUseCase.invoke()
        val blockchainType = BlockchainType.fromUid(chainNetwork.blockChainUid)
        val chain = Chain.fromBlockchainType(blockchainType)
        val data = signPersonalMessageUseCase.invoke(
            hdKey!!,
            chain,
            messageToSign.value?.toByteArray() ?: ByteArray(0)
        )
        return Pair(data.toHexString(), hdKey?.publicKey.toHexString())
    }

    private suspend fun loginToETicket(signature: String, hdPublicKey: String?): AuthenticationResponse? {
        val publicKey: String? = getAddress()?.hex
        loginUseCase.invoke(publicKey!!, hdPublicKey!!, messageToSign.value!!, signature).let {
            if (it is ApiResponse.Success) {
                return it.body.data
            } else {
                return null
            }
        }
    }

    private fun getTokenExpiration(expiration: Long): Long {
        return getCurrentTimeMillis() + expiration
    }

    private fun getCurrentTimeMillis(): Long = Clock.System.now().epochSeconds
}