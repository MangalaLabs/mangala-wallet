package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.createimport

import com.mangala.antelope.base.api.model.GetAccountsByAuthorizersRequest
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.KeyType

class GetAccountsByAuthorizersUseCase(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(privateKey: String, blockchainType: BlockchainType): Result<List<AntelopeAccountByAuthorizer>> {
        val privateKeyObj = EosPrivateKey.fromString(privateKey)
        val publicKey = privateKeyObj.publicKey.toString()

        return repository.getAccountsByAuthorizers(
            publicKey,
            blockchainType
        )
    }
}