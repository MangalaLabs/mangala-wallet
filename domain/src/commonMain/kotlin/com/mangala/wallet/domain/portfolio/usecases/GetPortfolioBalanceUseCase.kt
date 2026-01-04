package com.mangala.wallet.domain.portfolio.usecases

import com.mangala.wallet.domain.portfolio.model.PortfolioDetailResponse
import com.mangala.wallet.domain.portfolio.repository.PortfolioRepository
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.core.auth.SessionManager
import kotlinx.coroutines.flow.Flow

class GetPortfolioBalanceUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val sessionManager: SessionManager
) {
    
    suspend operator fun invoke(portfolioId: String): Result<PortfolioDetailResponse> {
        return try {
            portfolioRepository.getPortfolioDetail(portfolioId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun invokeFlow(forceRefresh: Boolean = false): Flow<Resource<PortfolioDetailResponse?>> {
        val userId = sessionManager.getCurrentUserId() ?: throw IllegalStateException("User not logged in")

        return portfolioRepository.getPortfolioByNetworkFlow(userId = userId, networkId = 1, forceRefresh)
    }
}