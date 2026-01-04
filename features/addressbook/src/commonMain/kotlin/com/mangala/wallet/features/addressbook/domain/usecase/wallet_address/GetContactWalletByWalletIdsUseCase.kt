package com.mangala.wallet.features.addressbook.domain.usecase.wallet_address


import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class GetContactWalletByWalletIdsUseCase(
    private val walletAddressRepository: WalletAddressRepository
) {

    suspend fun getGroupWallets(walletIds: List<String>): List<GroupWallet> = withContext(
        Dispatchers.IO
    ) {
        walletAddressRepository.getGroupWalletsByWalletIds(walletIds)
    }
}