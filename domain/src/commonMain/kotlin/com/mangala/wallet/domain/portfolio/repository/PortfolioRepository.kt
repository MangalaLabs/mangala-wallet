package com.mangala.wallet.domain.portfolio.repository

import com.mangala.wallet.domain.portfolio.model.CreatePortfolioRequest
import com.mangala.wallet.domain.portfolio.model.Portfolio
import com.mangala.wallet.domain.portfolio.model.PortfolioDetailResponse
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    
    suspend fun createPortfolio(request: CreatePortfolioRequest): Result<Portfolio>

    suspend fun addWalletToPortfolio(portfolioId: String, address: String, label: String): Result<Unit>
    
    suspend fun getPortfolioDetail(portfolioId: String): Result<PortfolioDetailResponse>
    
    fun getPortfolioDetailFlow(portfolioId: String, forceRefresh: Boolean = false): Flow<Resource<PortfolioDetailResponse?>>

    fun getPortfolioByNetworkFlow(userId: String, networkId: Int, forceRefresh: Boolean): Flow<Resource<PortfolioDetailResponse?>>

    suspend fun getPortfolioByNetwork(userId: String, networkId: Int): Result<PortfolioDetailResponse>

    suspend fun syncPortfolioData(portfolioId: String): Result<PortfolioDetailResponse>

    suspend fun getPortfolioByAccountAddress(address: String): Portfolio?

    suspend fun getCachedPortfolios(): List<Portfolio>
}