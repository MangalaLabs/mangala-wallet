package com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.abi

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import io.ktor.utils.io.core.toByteArray

class PayForAccountAbi(
    val accountName: String,
    val ownerPublicKey: String,
    val activePublicKey: String,
    val token: Address,
    val amount: BigInteger,
    val signature: ByteArray,
    val nonce: BigInteger,
    val chainId: String
): ContractMethod() {
    override val methodSignature: String = Companion.methodSignature

    override fun getArguments(): List<Any> {
        return listOf(accountName.toByteArray(), ownerPublicKey.toByteArray(), activePublicKey.toByteArray(), chainId.toByteArray(), token, amount, signature, nonce)
    }

    companion object {
        const val methodSignature = "payForAccount(string,string,string,string,address,uint256,bytes,uint256)"
    }
}