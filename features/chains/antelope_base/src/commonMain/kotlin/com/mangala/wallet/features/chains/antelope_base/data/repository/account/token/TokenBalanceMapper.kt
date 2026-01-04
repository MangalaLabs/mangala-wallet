package com.mangala.wallet.features.chains.antelope_base.data.repository.account.token

import com.mangala.antelope.base.api.model.tokenbalance.BaseAntelopeTokenBalanceResponse
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.token.AntelopeTokenBalance
import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountTokenBalanceEntity
import kotlinx.datetime.Clock

fun BaseAntelopeTokenBalanceResponse.toAntelopeAccountTokenBalanceEntityList(
    accountName: String,
    blockchainUid: String
): List<AntelopeAccountTokenBalanceEntity> {
    val lastUpdatedTimestamp = Clock.System.now().toEpochMilliseconds()

    return this.tokens?.mapNotNull {
        it?.toAntelopeAccountTokenBalanceEntity(
            accountName = accountName,
            blockchainUid = blockchainUid,
            lastUpdatedTimestamp = lastUpdatedTimestamp
        )
    } ?: emptyList()
}

fun BaseAntelopeTokenBalanceResponse.Token.toAntelopeAccountTokenBalanceEntity(
    accountName: String,
    blockchainUid: String,
    lastUpdatedTimestamp: Long
): AntelopeAccountTokenBalanceEntity {
    return AntelopeAccountTokenBalanceEntity(
        blockchain_uid = blockchainUid,
        account_name = accountName,
        key = key.orEmpty(),
        currency = currency.orEmpty(),
        amount = amount ?: 0.0,
        contract = contract.orEmpty(),
        decimals = decimals?.toLongOrNull() ?: 0,
        name = name.orEmpty(),
        website = website.orEmpty(),
        logo = logo,
        token_created_at = createdAt.orEmpty(),
        exchange_name = exchangeName.orEmpty(),
        exchange_price = exchangePrice ?: 0.0,
        last_updated = lastUpdatedTimestamp,

    )
}

fun List<AntelopeAccountTokenBalanceEntity>.toAntelopeTokenBalanceList(): List<AntelopeTokenBalance> {
    return this.map {
        AntelopeTokenBalance(
            key = it.key,
            symbol = it.currency,
            amount = it.amount,
            contract = it.contract,
            decimals = it.decimals.toInt(),
            metadata = AntelopeTokenBalance.AntelopeTokenMetadata(
                name = it.name,
                logo = it.logo.orEmpty(),
                localImage = null,
                website = it.website.orEmpty(),
                createdAt = it.token_created_at
            ),
            exchanges = listOf(
                AntelopeTokenBalance.AntelopeTokenExchangeData(
                    name = it.exchange_name,
                    price = it.exchange_price
                )
            )
        )
    }
}