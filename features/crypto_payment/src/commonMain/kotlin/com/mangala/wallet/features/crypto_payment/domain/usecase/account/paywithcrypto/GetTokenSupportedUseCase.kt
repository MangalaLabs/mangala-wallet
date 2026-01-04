package com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.crypto_payment.SMART_CONTRACT_NATIVE_COIN_ADDRESS
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.TokenSupported
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.abi.GetTokenSupportedAbi
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.utils.toHexString
import com.mangala.wallet.utils.toInt

class GetTokenSupportedUseCase(
    private val nodeRepository: NodeRepository,
    private val getCryptoPaymentContractAddressUseCase: GetCryptoPaymentContractAddressUseCase
) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        id: Int,
        tokenSupportedAddress: Address
    ): TokenSupported {
        val url = blockchainType.getRpcUrl().first()
        val chain = Chain.fromBlockchainType(blockchainType)
        val contractAddress = getCryptoPaymentContractAddressUseCase.invoke(chain)

        val tokenSupported = nodeRepository.call(
            url = url,
            id = id,
            contractAddress = contractAddress,
            from = Address(SMART_CONTRACT_NATIVE_COIN_ADDRESS),
            data = GetTokenSupportedAbi(tokenSupportedAddress).encodedABI(),
            defaultBlockParameter = DefaultBlockParameter.Latest
        )
        val tokenSupportedHex = tokenSupported.toHexString()
        return decodeTokenSupportedResponse(tokenSupportedHex)
    }

    private fun decodeTokenSupportedResponse(hexString: String): TokenSupported {
        // Remove the '0x' prefix if it exists
        val hex = if (hexString.startsWith("0x")) hexString.substring(2) else hexString

        // Extract isSupported (first 32 bytes)
        val isSupportedHex = hex.substring(0, 64)
        val isSupported = isSupportedHex.toBigInteger(16) != BigInteger.ZERO

        // Extract index (second 32 bytes)
        val indexHex = hex.substring(64, 128)
        val index = indexHex.toBigInteger(16).toInt()

        return TokenSupported(
            isSupported = isSupported,
            index = index,
            name = ""
        )
    }
}