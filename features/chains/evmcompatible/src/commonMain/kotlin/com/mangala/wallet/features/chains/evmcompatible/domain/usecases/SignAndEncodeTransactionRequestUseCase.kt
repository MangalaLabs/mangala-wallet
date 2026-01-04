package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.core.hdwallet.domain.model.HDKey
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletByIdUseCase
import com.mangala.wallet.features.chains.evmcompatible.core.signer.Signer
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.RawTransaction
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.chains.evmcompatible.model.Signature
import com.mangala.wallet.features.chains.evmcompatible.model.SignedTransactionResponse
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.Chain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SignAndEncodeTransactionRequestUseCase(
    private val getWalletByIdUseCase: GetWalletByIdUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val generateHDKeyUseCase: GenerateHDKeyUseCase,
    private val json: Json
) {

    suspend operator fun invoke(
        signTransactionRequest: SignTransactionRequest
    ): String {
        val signature = signTransaction(signTransactionRequest)

        val response = SignedTransactionResponse(
            signature = signature,
            signTransactionRequest = signTransactionRequest
        )

        return json.encodeToString(response)
    }

    private suspend fun signTransaction(
        signTransactionRequest: SignTransactionRequest
    ): Signature {
        with(signTransactionRequest) {
            val selectedWallet = getWalletByIdUseCase(signTransactionRequest.walletId)
                ?: throw IllegalStateException("Wallet not found")
            val account = getAccountByIdUseCase(signTransactionRequest.accountId)
            val hdKey = getHDKey(
                Blockchain(blockchainType, "", null),
                AddressType.Bip44, // TODO: Handle different address types
                selectedWallet.words,
                account.derivationPathIndex
            )
            val wrappedAddress =
                Address(account.bip44Address) // chains not EVM-compatible may need different address type

            val chain = Chain.fromBlockchainType(blockchainType)
            val signer = Signer.getInstance(hdKey.privateKey, wrappedAddress, chain)
            val rawTransaction = rawTransaction(transactionData, gasPrice, gasLimit, nonce)

            return signer.signature(rawTransaction)
        }
    }

    private fun getHDKey(
        blockchain: Blockchain,
        addressType: AddressType,
        defaultsWords: String,
        derivationPathIndex: Int
    ): HDKey {
        val words = defaultsWords.split(" ")
        return generateHDKeyUseCase.invoke(
            seedPhrase = words,
            blockchain = blockchain,
            addressType = addressType,
            derivationPathIndex = derivationPathIndex
        )
    }

    private fun rawTransaction(
        transactionData: TransactionData,
        gasPrice: GasPrice,
        gasLimit: Long,
        nonce: Long
    ): RawTransaction {
        return rawTransaction(
            address = transactionData.to ?: Address(""),
            value = transactionData.value,
            transactionInput = transactionData.input,
            gasPrice = gasPrice,
            gasLimit = gasLimit,
            nonce = nonce
        )
    }

    private fun rawTransaction(
        address: Address,
        value: BigInteger,
        transactionInput: ByteArray = byteArrayOf(),
        gasPrice: GasPrice,
        gasLimit: Long,
        nonce: Long
    ): RawTransaction {
        return RawTransaction(
            gasPrice = gasPrice,
            gasLimit = gasLimit,
            to = address,
            value = value,
            nonce = nonce,
            data = transactionInput
        )
    }

}