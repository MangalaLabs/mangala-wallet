package com.mangala.wallet.features.chains.erc20.decorations

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.erc20.model.TokenInfo
import com.mangala.wallet.features.chains.evmcompatible.decorations.TransactionDecoration
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionTag

class OutgoingEip20Decoration(
    val contractAddress: Address,
    val to: Address,
    val value: BigInteger,
    val sentToSelf: Boolean,
    val tokenInfo: TokenInfo?
) : TransactionDecoration() {

    override fun tags(): List<String> =
        listOf(contractAddress.hex, TransactionTag.EIP20_TRANSFER, TransactionTag.tokenOutgoing(contractAddress.hex), TransactionTag.OUTGOING)

}
