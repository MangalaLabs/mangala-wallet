package com.mangala.wallet.features.nft_base.domain.usecases

import com.mangala.wallet.domain.reset.usecases.ClearNFTDataUseCase
import com.mangala.wallet.features.nft_base.domain.repository.NftRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearNFTDataUseCaseImpl(
    private val nftRepository: NftRepository
) : ClearNFTDataUseCase {

    override suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            nftRepository.clearAllNftData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}