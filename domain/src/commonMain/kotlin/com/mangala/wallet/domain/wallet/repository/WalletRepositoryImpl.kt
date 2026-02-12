package com.mangala.wallet.domain.wallet.repository

import com.mangala.wallet.local.wallet.WalletLocalDataSource
import com.mangala.wallet.model.wallet.domain.WalletModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class WalletRepositoryImpl(
    private val walletLocalDataSource: WalletLocalDataSource
) :
    WalletRepository {

    override suspend fun setSelectedWallet(walletId: String) {
        return walletLocalDataSource.setSelectedWallet(walletId);
    }

    override fun getSelectedWallet(): WalletModel? {
        return walletLocalDataSource.getSelectedWallet()?.mapToDomainModel()
    }

    override fun getSelectedWalletFlow(): Flow<WalletModel?> {
        return walletLocalDataSource.getSelectedWalletFlow().map { it?.mapToDomainModel() }
    }

    override suspend fun getAllWallets(): List<WalletModel> {
        return walletLocalDataSource.getAllWallets().map { it.mapToDomainModel() }
    }

    override suspend fun saveWallet(wallet: WalletModel) {
        walletLocalDataSource.insertWallet(wallet.toLocalDto())
    }

    override suspend fun deletedWallet(walletId: String) {
        walletLocalDataSource.deleteWallet(walletId)
    }

    override suspend fun deleteAllWallets() {
        walletLocalDataSource.deleteAllWallets()
    }

    override suspend fun saveWalletName(walletName: String, walletId: String) {
        walletLocalDataSource.saveWalletName(walletId, walletName)
    }

    override suspend fun markWalletAsBackedUp(walletId: String) {
        walletLocalDataSource.updateWalletBackupStatus(walletId, isBackedUp = true)
    }

    override suspend fun getWalletById(walletId: String): WalletModel? {
        return walletLocalDataSource.getWalletById(walletId)
    }

    override fun getWalletByIdFlow(walletId: String): Flow<WalletModel?> {
        return walletLocalDataSource.getWalletByIdFlow(walletId)
    }
}