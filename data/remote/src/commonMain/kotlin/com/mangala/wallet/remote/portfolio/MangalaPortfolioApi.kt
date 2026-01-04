package com.mangala.wallet.remote.portfolio

import com.mangala.wallet.model.portfolio.CreatePortfolioRequestDto
import com.mangala.wallet.model.portfolio.CreatePortfolioResponseDto
import com.mangala.wallet.model.portfolio.PortfolioDetailResponseDto
import com.mangala.wallet.model.portfolio.AddWalletToPortfolioRequestDto
import com.mangala.wallet.model.portfolio.AddWalletToPortfolioResponseDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

/**
 * Ktorfit API interface for Mangala Portfolio API
 * Base URL: https://staging-api.mangala.io
 */
interface MangalaPortfolioApi {
    
    @POST("portfolios")
    suspend fun createPortfolio(@Body request: CreatePortfolioRequestDto): CreatePortfolioResponseDto
    
    @GET("portfolios/{portfolioId}")
    suspend fun getPortfolioDetail(@Path("portfolioId") portfolioId: String): PortfolioDetailResponseDto

    @GET("portfolios")
    suspend fun getPortfolioByNetwork(@Query("networkId") networkId: Int): PortfolioDetailResponseDto
    
    @POST("portfolios/{portfolioId}/wallets")
    suspend fun addWalletToPortfolio(
        @Path("portfolioId") portfolioId: String,
        @Body request: AddWalletToPortfolioRequestDto
    ): AddWalletToPortfolioResponseDto
    
    @PUT("portfolios/{portfolioId}/sync")
    suspend fun syncPortfolio(@Path("portfolioId") portfolioId: String): PortfolioDetailResponseDto
}