package com.mangala.wallet.domain.wallet.repository

import com.mangala.wallet.model.wallet.domain.WalletModel
import kotlinx.coroutines.flow.Flow

interface WalletRepository {

    suspend fun setSelectedWallet(walletId: String)
    fun getSelectedWallet(): WalletModel?
    fun getSelectedWalletFlow(): Flow<WalletModel?>
    suspend fun getAllWallets(): List<WalletModel>
    suspend fun saveWallet(wallet: WalletModel)
    suspend fun deletedWallet(walletId: String)
    suspend fun deleteAllWallets()
    suspend fun saveWalletName(walletName:String, walletId: String)
    suspend fun markWalletAsBackedUp(walletId: String)
    suspend fun getWalletById(walletId: String): WalletModel?
    fun getWalletByIdFlow(walletId: String): Flow<WalletModel?>
}