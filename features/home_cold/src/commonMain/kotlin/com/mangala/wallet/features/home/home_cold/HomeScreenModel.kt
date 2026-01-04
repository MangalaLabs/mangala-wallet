package com.mangala.wallet.features.home.home_cold

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.blockchain.usecases.CreateBlockchainUseCase
import com.mangala.wallet.domain.coin.usecases.CreateCoinUseCase
import com.mangala.wallet.domain.datastore.usecases.CheckInitialDatabaseUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveInitialDatabaseUseCase
import com.mangala.wallet.domain.token.usecases.CreateTokenUseCase
import com.mangala.wallet.model.blockchain.BlockchainEntity
import com.mangala.wallet.model.coin.Coin
import com.mangala.wallet.model.token.TokenEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeScreenModel: ScreenModel, KoinComponent {

    private val createBlockchainUseCase: CreateBlockchainUseCase by inject()
    private val createCoinUseCase: CreateCoinUseCase by inject()
    private val createTokenUseCase: CreateTokenUseCase by inject()

    private val checkInitialDatabaseUseCase: CheckInitialDatabaseUseCase by inject()
    private val saveInitialDatabaseUseCase: SaveInitialDatabaseUseCase by inject()

    fun createBlockchain(data: String) {
        val sqls = data.split(";")

        val blockchainEntitys = mutableListOf<BlockchainEntity>()
        sqls.forEach { sql ->
//            println("SQL: $sql")
            if (sql.isNotBlank()) {
                val values = sql.substringAfter("VALUES(").substringBeforeLast(")").split(",")
                val uid = values[0].trim().trim('\'')
                val name = values[1].trim().trim('\'')
                val eip3091url = values[2].trim().trim('\'').replace("NULL", "")
                val blockchainEntity = BlockchainEntity(uid, name, eip3091url)
                blockchainEntitys.add(blockchainEntity)
            }
        }

        screenModelScope.launch {
            val result = createBlockchainUseCase(blockchainEntitys)
        }
    }

    fun createCoinData(data: String) {
        val sqls = data.split(";")

        val coins = mutableListOf<Coin>()
        sqls.forEach { sql ->
//            println("SQLCOIN: $sql")
            if (sql.isNotBlank()) {
                val values = sql.substringAfter("VALUES(").substringBeforeLast(")").split(",")
                val uid = values[0].trim().trim('\'')
                val name = values[1].trim().trim('\'')
                val code = values[2].trim().trim('\'')
                val marketCapRank = values[3].trim().trim('\'').replace("NULL", "")
                val coinGeckoId = values[4].trim().trim('\'').replace("NULL", "")
                val rank = if (marketCapRank.isNotBlank()) {
                    marketCapRank.toLong()
                } else {
                    null
                }
                val coin = Coin(uid, name, code, rank, coinGeckoId)
                coins.add(coin)
            }
        }

        screenModelScope.launch {
            val result = createCoinUseCase(coins)
        }
    }

    fun createTokenData(data: String) {
        val sqls = data.split(";")

        val tokens = mutableListOf<TokenEntity>()
        sqls.forEach { sql ->
//            println("SQL TOKEN: $sql")
            if (sql.isNotBlank()) {
                val values = sql.substringAfter("VALUES(").substringBeforeLast(")").split(",")
                val coinUid = values[0].trim().trim('\'')
                val blockchainUid = values[1].trim().trim('\'')
                val type = values[2].trim().trim('\'')
                val decimalsText = values[3].trim().trim('\'').replace("NULL", "")
                val reference = values[4].trim().trim('\'').replace("NULL", "")
                val decimals = if (decimalsText.isNotBlank()) {
                    decimalsText.toLong()
                } else {
                    null
                }
                val token = TokenEntity(0L, coinUid, blockchainUid, type, decimals, reference)
                tokens.add(token)
            }
        }

        screenModelScope.launch {
            val result = createTokenUseCase(tokens)
        }
        saveInitialDatabase()
    }



    private val _checkInitialDatabase = MutableStateFlow<Boolean?>(null)
    val checkInitialDatabase = _checkInitialDatabase
    fun checkInitialDatabase() {
        screenModelScope.launch {
            val result: Flow<Boolean> = checkInitialDatabaseUseCase()
            _checkInitialDatabase.value = result.stateIn(screenModelScope).value
        }
    }

    private fun saveInitialDatabase() {
        screenModelScope.launch {
            saveInitialDatabaseUseCase(true)
        }
    }
}