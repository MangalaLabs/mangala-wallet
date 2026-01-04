package com.mangala.wallet.features.chains.bitcoin.data.repository.account

import com.mangala.wallet.features.chains.bitcoin.BitcoinAccountEntity
import com.mangala.wallet.features.chains.bitcoin.data.local.account.BitcoinAccountLocalDataSource
import com.mangala.wallet.features.chains.bitcoin.domain.model.account.BitcoinAccount
import com.mangala.wallet.features.chains.bitcoin.domain.repository.account.BitcoinAccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BitcoinAccountRepositoryImpl(
    private val bitcoinAccountLocalDataSource: BitcoinAccountLocalDataSource
): BitcoinAccountRepository {
    override suspend fun saveAccount(
        blockchainType: BlockchainType,
        accountId: String,
        bip44Address: String,
        bip49Address: String,
        bip84Address: String
    ) {
        try {
            bitcoinAccountLocalDataSource.insertBitcoinAccount(BitcoinAccountEntity(
                account_id = accountId,
                blockchain_uid = blockchainType.uid,
                bip_44_address = bip44Address,
                bip_49_address = bip49Address,
                bip_84_address = bip84Address
            ))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAccounts(blockchainType: BlockchainType, accountIds: List<String>): Flow<List<BitcoinAccount>> {
        return bitcoinAccountLocalDataSource.getBitcoinAccounts(blockchainType, accountIds)
            .map { accountDataList ->
                accountDataList.map { accountData ->
                    BitcoinAccount(
                        accountId = accountData.account_id,
                        name = null,
                        bip44Address = accountData.bip_44_address,
                        bip49Address = accountData.bip_49_address,
                        bip84Address = accountData.bip_84_address,
                        sortingOrder = null
                    )
                }
            }
    }

    override suspend fun getAccount(blockchainType: BlockchainType, accountId: String): BitcoinAccount? {
        val accountData = bitcoinAccountLocalDataSource.getBitcoinAccount(blockchainType, accountId) ?: return null

        return BitcoinAccount(
            accountId = accountData.account_id,
            name = null,
            bip44Address = accountData.bip_44_address,
            bip49Address = accountData.bip_49_address,
            bip84Address = accountData.bip_84_address,
            sortingOrder = null
        )
    }
    
    override suspend fun getActiveAccount(blockchainType: BlockchainType): BitcoinAccount? {
        val activeAccountId = bitcoinAccountLocalDataSource.getActiveAccountId() ?: return null
        
        return getAccount(blockchainType, activeAccountId)
    }
}