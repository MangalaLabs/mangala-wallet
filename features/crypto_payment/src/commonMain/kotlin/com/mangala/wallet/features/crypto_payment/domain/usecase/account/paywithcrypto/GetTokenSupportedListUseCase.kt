package com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto

import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.abi.GetTokenSupportedListAbi
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.utils.toHexString
import com.mangala.wallet.utils.toInt

class GetTokenSupportedListUseCase(
    private val nodeRepository: NodeRepository,
    private val getCryptoPaymentContractAddressUseCase: GetCryptoPaymentContractAddressUseCase
) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        id: Int
    ): List<Address> {
        val url = blockchainType.getRpcUrl().first()
        val chain = Chain.fromBlockchainType(blockchainType)
        println("blockchainType: $blockchainType")
        val contractAddress = getCryptoPaymentContractAddressUseCase.invoke(chain)

        val listTokenSupportedByteArray = nodeRepository.call(
            url = url,
            id = id,
            contractAddress = contractAddress,
            data = GetTokenSupportedListAbi().encodedABI(),
            defaultBlockParameter = DefaultBlockParameter.Latest
        )
        val listTokenSupportedHex = listTokenSupportedByteArray.toHexString()
        println("listTokenSupportedHex: $listTokenSupportedHex")
        return decodeByteArrayToAddresses(listTokenSupportedHex)
    }

    private fun decodeByteArrayToAddresses(hexString: String): List<Address> {
        // Remove the '0x' prefix if it exists
        val hex = if (hexString.startsWith("0x")) hexString.substring(2) else hexString

        // Parse the number of addresses
        val numAddressesHex = hex.substring(64, 128)
        val numAddresses = numAddressesHex.toBigInteger(16).toInt()

        val addresses = mutableListOf<Address>()
        for (i in 0 until numAddresses) {
            val start = 128 + (i * 64)
            val end = start + 64
            val addressHex = "0x" + hex.substring(
                start + 24,
                end
            ) // Ethereum addresses are 20 bytes (40 hex chars) long
            addresses.add(Address(addressHex))
        }

        return addresses // Return as a list of lists to match your expected output
    }
}