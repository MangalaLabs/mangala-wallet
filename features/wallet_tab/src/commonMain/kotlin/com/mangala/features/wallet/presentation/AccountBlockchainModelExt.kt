package com.mangala.features.wallet.presentation

import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.utils.formattedAddress

fun AccountBlockchainModel.formattedBip44Address() = Address(bip44Address).eip55.formattedAddress()
