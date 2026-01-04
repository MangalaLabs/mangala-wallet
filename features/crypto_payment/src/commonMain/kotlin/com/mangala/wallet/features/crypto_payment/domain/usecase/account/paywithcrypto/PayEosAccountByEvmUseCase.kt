package com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto


import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.abi.PayForAccountAbi
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain

class PayEosAccountByEvmUseCase(
    private val signAndSendTransactionDataUseCase: SignAndSendTransactionDataUseCase,
    private val getCryptoPaymentContractAddressUseCase: GetCryptoPaymentContractAddressUseCase
) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        from: Address,
        gasPrice: GasPrice,
        gas: Long?,
        transactionData: TransactionData
    ): String? {
        println("before invoke signAndSendTransactionDataUseCase")
        println("blockchainType: $blockchainType")
        println("from: $from")
        println("gasPrice: $gasPrice")
        println("gas: $gas")
        println("transactionData: $transactionData")
        println("after invoke signAndSendTransactionDataUseCase")
        return signAndSendTransactionDataUseCase(blockchainType, from, transactionData, gasPrice, gas)
    }

    fun buildTransactionData(
        blockchainType: BlockchainType,
        newAccountName: String,
        publicActiveKey: String,
        publicOwnerKey: String,
        token: Address,
        amount: BigInteger,
        signatures: ByteArray,
        nonce: BigInteger,
        chainId: String
    ): TransactionData {
        println("==================== buildTransactionData =====================")
        println("blockchainType: $blockchainType")
        println("newAccountName: $newAccountName")
        println("publicActiveKey: $publicActiveKey")
        println("publicOwnerKey: $publicOwnerKey")
        println("token: $token")
        println("amount: $amount")
        println("signatures: $signatures")
        println("nonce: $nonce")
        println("==================== buildTransactionData =====================")
        val chain = Chain.fromBlockchainType(blockchainType)
        val contractAddress = getCryptoPaymentContractAddressUseCase.invoke(chain)
        return TransactionData(
            to = contractAddress,
            value = amount, // convert to wei
            input = PayForAccountAbi(
                accountName = newAccountName,
                ownerPublicKey = publicOwnerKey,
                activePublicKey = publicActiveKey,
                token = token,
                amount = amount,
                signature = signatures,
                nonce = nonce,
                chainId = chainId
            ).encodedABI()
        )
    }
}