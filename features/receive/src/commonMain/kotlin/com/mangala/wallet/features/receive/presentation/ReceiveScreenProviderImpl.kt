package com.mangala.wallet.features.receive.presentation

import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.scanqr.ReceiveScreenProvider

class ReceiveScreenProviderImpl: ReceiveScreenProvider {

    override fun provideReceiveScreen(
        accountId: String,
        onBackPressed: () -> Unit,
        networkType: NetworkType,
        initialBlockchainUid: String?
    ): Screen {
        return ReceiveTokenScreen(
            accountId = accountId,
            address = null,
            networkType = networkType,
            onBackPressedButton = onBackPressed,
            initialBlockchainUid = initialBlockchainUid
        )
    }
}