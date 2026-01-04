package com.mangala.wallet.features.chains.erc20.events

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.contract.ContractEventInstance
import com.mangala.wallet.features.chains.evmcompatible.model.Address

class ApproveEventInstance(
    contractAddress: Address,
    val owner: Address,
    val spender: Address,
    val value: BigInteger
) : ContractEventInstance(contractAddress)
