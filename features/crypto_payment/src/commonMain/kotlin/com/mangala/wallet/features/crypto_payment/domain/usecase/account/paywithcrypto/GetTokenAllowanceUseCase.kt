package com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.abi.GetAddressAllowanceAbi
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.toHexString

class GetTokenAllowanceUseCase(
    private val nodeRepository: NodeRepository
) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        id: Int,
        owner: Address,
        spender: Address,
        tokenAddress: Address
    ): BigDecimal {
        val url = blockchainType.getRpcUrl().first()
        val allowanceByteArray = nodeRepository.call(
            url = url,
            id = id,
            contractAddress = tokenAddress,
            data = GetAddressAllowanceAbi(owner, spender).encodedABI(),
            defaultBlockParameter = DefaultBlockParameter.Latest
        )
        val allowanceHexString = allowanceByteArray.toHexString()
        return hexToWei(allowanceHexString)
    }

    private fun hexToWei(hexString: String): BigDecimal {
        // Remove "0x" prefix if present
        val cleanHexString = if (hexString.startsWith("0x")) hexString.substring(2) else hexString

        // Convert hex string to BigInteger
        val weiValueBigInt = BigInteger.parseString(cleanHexString, 16)

        // Convert BigInteger to BigDecimal
        return BigDecimal.fromBigInteger(weiValueBigInt)
    }
}