package com.mangala.wallet.domain.portfolio.repository.mappers

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.domain.portfolio.model.*
import com.mangala.wallet.local.portfolio.*
import com.mangala.wallet.model.portfolio.*
import kotlinx.datetime.Clock

/**
 * Extension functions to map between different portfolio data models
 */

// DTO to Domain Model mappings

fun PortfolioDetailResponseDto.toDomainModel(): PortfolioDetailResponse {
    return PortfolioDetailResponse(
        portfolio = this.data.portfolio.toDomainModel(),
        pricingContext = this.data.pricingContext.toDomainModel()
    )
}

fun PortfolioDto.toDomainModel(): Portfolio {
    return Portfolio(
        portfolioId = this.portfolioId,
        userId = this.userId,
        networkId = this.networkId,
        totals = this.totals.toDomainModel(),
        accounts = this.accounts.map { it.toDomainModel() }
    )
}

fun PortfolioTotalsDto.toDomainModel(): PortfolioTotals {
    return PortfolioTotals(
        balanceUsdt = this.balanceUsdt.toBigDecimal(),
        pnl24hUsdt = this.pnl24hUsdt.toBigDecimal(),
        pnl24hPercent = this.pnl24hPercent.toBigDecimal()
    )
}

fun PortfolioAccountDto.toDomainModel(): PortfolioAccount {
    return PortfolioAccount(
        accountId = this.accountId,
        address = this.address,
        label = this.label,
        createdAt = this.createdAt,
        totals = this.totals.toDomainModel(),
        tokens = this.tokens.map { it.toDomainModel() },
        resources = this.resources?.toDomainModel()
    )
}

fun PortfolioTokenDto.toDomainModel(): PortfolioToken {
    return PortfolioToken(
        tokenKey = this.tokenKey,
        symbol = this.symbol,
        name = this.name,
        quantity = this.quantity.toBigDecimal(),
        balanceUsdt = this.balanceUsdt.toBigDecimal(),
        priceUsdt = this.priceUsdt.toBigDecimal(),
        pnl24hUsdt = this.pnl24hUsdt.toBigDecimal(),
        pnl24hPercent = this.pnl24hPercent.toBigDecimal()
    )
}

fun PortfolioResourcesDto.toDomainModel(): PortfolioResources {
    return PortfolioResources(
        cpu = this.cpu?.toDomainModel(),
        net = this.net?.toDomainModel(),
        ram = this.ram?.toDomainModel()
    )
}

fun ResourceInfoDto.toDomainModel(): ResourceInfo {
    return ResourceInfo(
        used = this.used.toBigDecimal(),
        max = this.max.toBigDecimal(),
        available = this.available.toBigDecimal()
    )
}

fun PricingContextDto.toDomainModel(): PricingContext {
    return PricingContext(
        asOf = this.asOf,
        quoteCurrency = this.quoteCurrency,
        status = this.status,
        prices = this.prices.mapValues { it.value.toDomainModel() }
    )
}

fun TokenPriceDto.toDomainModel(): TokenPrice {
    return TokenPrice(
        spot = this.spot.toBigDecimal(),
        price24hAgo = this.price24hAgo.toBigDecimal(),
        lastUpdated = this.lastUpdated,
        source = this.source
    )
}

// Domain Model to DTO mappings

fun CreatePortfolioRequest.toDto(): CreatePortfolioRequestDto {
    return CreatePortfolioRequestDto(
        name = this.name,
        description = this.description,
        networkId = this.networkId,
        initialWallet = this.initialWallet.toDto()
    )
}

fun InitialWallet.toDto(): InitialWalletDto {
    return InitialWalletDto(
        address = this.address,
        label = this.label
    )
}

// DTO to Local Entity mappings

fun PortfolioDetailResponseDto.toCachedPortfolioDetail(): CachedPortfolioDetail {
    val currentTime = Clock.System.now().toEpochMilliseconds()
    val portfolio = this.data.portfolio
    
    return CachedPortfolioDetail(
        portfolio = PortfolioEntity(
            userId = portfolio.userId,
            networkId = portfolio.networkId,
            portfolioId = portfolio.portfolioId,
            balanceUsdt = portfolio.totals.balanceUsdt,
            pnl24hUsdt = portfolio.totals.pnl24hUsdt,
            pnl24hPercent = portfolio.totals.pnl24hPercent,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        accounts = portfolio.accounts.map { account ->
            PortfolioAccountEntity(
                accountId = account.accountId,
                userId = portfolio.userId,
                networkId = portfolio.networkId,
                address = account.address,
                label = account.label,
                createdAt = account.createdAt,
                balanceUsdt = account.totals.balanceUsdt,
                pnl24hUsdt = account.totals.pnl24hUsdt,
                pnl24hPercent = account.totals.pnl24hPercent
            )
        },
        tokens = portfolio.accounts.flatMap { account ->
            account.tokens.map { token ->
                PortfolioTokenEntity(
                    tokenKey = token.tokenKey,
                    accountId = account.accountId,
                    userId = portfolio.userId,
                    networkId = portfolio.networkId,
                    symbol = token.symbol,
                    name = token.name,
                    quantity = token.quantity,
                    balanceUsdt = token.balanceUsdt,
                    priceUsdt = token.priceUsdt,
                    pnl24hUsdt = token.pnl24hUsdt,
                    pnl24hPercent = token.pnl24hPercent
                )
            }
        },
        pricingContext = PortfolioPricingContextEntity(
            userId = portfolio.userId,
            networkId = portfolio.networkId,
            asOf = this.data.pricingContext.asOf,
            quoteCurrency = this.data.pricingContext.quoteCurrency,
            status = this.data.pricingContext.status,
            updatedAt = currentTime
        ),
        tokenPrices = this.data.pricingContext.prices.map { (tokenKey, price) ->
            PortfolioTokenPriceEntity(
                tokenKey = tokenKey,
                userId = portfolio.userId,
                networkId = portfolio.networkId,
                spot = price.spot,
                price24hAgo = price.price24hAgo,
                lastUpdated = price.lastUpdated,
                source = price.source
            )
        }
    )
}

// Local Entity to Domain Model mappings

fun CachedPortfolioDetail.toDomainModel(): PortfolioDetailResponse {
    return PortfolioDetailResponse(
        portfolio = Portfolio(
            portfolioId = this.portfolio.portfolioId,
            userId = this.portfolio.userId,
            networkId = this.portfolio.networkId,
            totals = PortfolioTotals(
                balanceUsdt = this.portfolio.balanceUsdt.toBigDecimal(),
                pnl24hUsdt = this.portfolio.pnl24hUsdt.toBigDecimal(),
                pnl24hPercent = this.portfolio.pnl24hPercent.toBigDecimal()
            ),
            accounts = this.accounts.map { account ->
                PortfolioAccount(
                    accountId = account.accountId,
                    address = account.address,
                    label = account.label,
                    createdAt = account.createdAt,
                    totals = PortfolioTotals(
                        balanceUsdt = account.balanceUsdt.toBigDecimal(),
                        pnl24hUsdt = account.pnl24hUsdt.toBigDecimal(),
                        pnl24hPercent = account.pnl24hPercent.toBigDecimal()
                    ),
                    tokens = this.tokens.filter { it.accountId == account.accountId }.map { token ->
                        PortfolioToken(
                            tokenKey = token.tokenKey,
                            symbol = token.symbol,
                            name = token.name,
                            quantity = token.quantity.toBigDecimal(),
                            balanceUsdt = token.balanceUsdt.toBigDecimal(),
                            priceUsdt = token.priceUsdt.toBigDecimal(),
                            pnl24hUsdt = token.pnl24hUsdt.toBigDecimal(),
                            pnl24hPercent = token.pnl24hPercent.toBigDecimal()
                        )
                    },
                    resources = null // TODO: Add resources mapping if needed
                )
            }
        ),
        pricingContext = this.pricingContext?.let { context ->
            PricingContext(
                asOf = context.asOf,
                quoteCurrency = context.quoteCurrency,
                status = context.status,
                prices = this.tokenPrices.associate { price ->
                    price.tokenKey to TokenPrice(
                        spot = price.spot.toBigDecimal(),
                        price24hAgo = price.price24hAgo.toBigDecimal(),
                        lastUpdated = price.lastUpdated,
                        source = price.source
                    )
                }
            )
        } ?: PricingContext(
            asOf = "",
            quoteCurrency = "USDT",
            status = "cached",
            prices = emptyMap()
        )
    )
}