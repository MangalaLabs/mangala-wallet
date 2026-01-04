package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.features.chains.erc20.contract.AllowanceMethod
import com.mangala.wallet.features.chains.evmcompatible.core.toRawHexString
import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter

class AllowanceUseCase(private val nodeRepository: NodeRepository) {

    suspend operator fun invoke(
        url: String,
        id: Int,
        contractAddress: Address,
        receiveAddress: Address,
        spenderAddress: Address,
        defaultBlockParameter: DefaultBlockParameter
    ): BigInteger? {
        val result = nodeRepository.call(
            url,
            id,
            contractAddress,
            AllowanceMethod(receiveAddress, spenderAddress).encodedABI(),
            defaultBlockParameter
        )
        return try {
            val allowance = result?.sliceArray(0..31).toRawHexString()
            allowance.toBigInteger(16)
        } catch (e: Exception) {
            null
        }
    }
}