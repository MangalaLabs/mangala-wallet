package com.mangala.wallet.remote.portfolio

import com.mangala.wallet.model.portfolio.AddWalletToPortfolioResponseDto
import com.mangala.wallet.model.portfolio.CreatePortfolioRequestDto
import com.mangala.wallet.model.portfolio.CreatePortfolioResponseDto
import com.mangala.wallet.model.portfolio.PortfolioDetailResponseDto
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall

/**
 * Remote data source for Mangala Portfolio API operations
 * Handles all network calls to the portfolio API with proper error handling
 */
class MangalaPortfolioRemoteDataSource(private val api: MangalaPortfolioApi) {
    
    /**
     * Create a new portfolio
     * @param request Portfolio creation request
     * @return ApiResponse containing the created portfolio or error
     */
    suspend fun createPortfolio(request: CreatePortfolioRequestDto): ApiResponse<CreatePortfolioResponseDto, String> = 
        safeApiCall { 
            api.createPortfolio(request) 
        }

    suspend fun getPortfolioDetail(portfolioId: String): ApiResponse<PortfolioDetailResponseDto, String> =
        safeApiCall { 
            api.getPortfolioDetail(portfolioId) 
        }

    suspend fun addWalletToPortfolio(
        portfolioId: String,
        address: String,
        label: String
    ): ApiResponse<AddWalletToPortfolioResponseDto, String> =
        safeApiCall {
            api.addWalletToPortfolio(portfolioId, com.mangala.wallet.model.portfolio.AddWalletToPortfolioRequestDto(address, label))
        }

    suspend fun getPortfolioByNetwork(networkId: Int): ApiResponse<PortfolioDetailResponseDto, String> =
        safeApiCall {
            api.getPortfolioByNetwork(networkId)
        }

    suspend fun syncPortfolio(portfolioId: String): ApiResponse<PortfolioDetailResponseDto, String> =
        safeApiCall { 
            api.syncPortfolio(portfolioId) 
        }
}