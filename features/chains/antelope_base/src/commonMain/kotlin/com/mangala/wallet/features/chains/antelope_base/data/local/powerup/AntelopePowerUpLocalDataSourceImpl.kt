package com.mangala.wallet.features.chains.antelope_base.data.local.powerup

import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeTableRowsPowerUpEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class AntelopePowerUpLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AntelopePowerUpLocalDataSource {
    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun getTableRowsPowerUp(blockchainUid: String): AntelopeTableRowsPowerUpEntity? = withContext(ioDispatcher) {
        return@withContext dbQuery.selectTableRowsPowerUp(blockchainUid).executeAsOneOrNull()
    }

    override suspend fun insertTableRowsPowerUp(tableRowsPowerUpEntities: AntelopeTableRowsPowerUpEntity) = withContext(ioDispatcher) {
        dbQuery.insertTableRowsPowerUp(
            tableRowsPowerUpEntities.blockchain_uid,
            tableRowsPowerUpEntities.cpu_weight,
            tableRowsPowerUpEntities.net_weight,
            tableRowsPowerUpEntities.cpu_utilization,
            tableRowsPowerUpEntities.net_utilization,
            tableRowsPowerUpEntities.cpu_adjusted_utilization,
            tableRowsPowerUpEntities.net_adjusted_utilization,
            tableRowsPowerUpEntities.cpu_decay_secs,
            tableRowsPowerUpEntities.net_decay_secs,
            tableRowsPowerUpEntities.cpu_min_price,
            tableRowsPowerUpEntities.net_min_price,
            tableRowsPowerUpEntities.cpu_max_price,
            tableRowsPowerUpEntities.net_max_price,
            tableRowsPowerUpEntities.cpu_exponent,
            tableRowsPowerUpEntities.net_exponent,
            tableRowsPowerUpEntities.cpu_utilization_timestamp,
            tableRowsPowerUpEntities.net_utilization_timestamp,
            tableRowsPowerUpEntities.powerup_days,
            tableRowsPowerUpEntities.min_powerup_fee,
            tableRowsPowerUpEntities.last_updated
        )
    }

    override suspend fun deleteTableRowsPowerUp(blockchainUid: String) = withContext(ioDispatcher) {
        dbQuery.deleteTableRowsPowerUp(blockchainUid)
    }
    
    override suspend fun clearAllTableRowsPowerUp() = withContext(ioDispatcher) {
        dbQuery.clearAllAntelopeTableRowsPowerUp()
    }
}