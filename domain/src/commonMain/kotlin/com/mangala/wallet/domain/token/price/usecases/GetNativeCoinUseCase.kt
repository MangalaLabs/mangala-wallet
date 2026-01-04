package com.mangala.wallet.domain.token.price.usecases

import com.mangala.wallet.domain.token.price.repository.TokenPriceRepository
import com.mangala.wallet.domain.token.repository.TokenRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.token.TokenEntity
import com.mangala.wallet.model.token.TokenPriceEntity

class GetNativeCoinUseCase(
    private val tokenRepository: TokenRepository
) {

    operator fun invoke(blockchainUid: String): TokenEntity {
        return tokenRepository.getNativeCoin(blockchainUid)
    }
}