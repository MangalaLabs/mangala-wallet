package com.mangala.wallet.domain.portfolio.repository

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.domain.portfolio.model.CreatePortfolioRequest
import com.mangala.wallet.domain.portfolio.model.Portfolio
import com.mangala.wallet.domain.portfolio.model.PortfolioDetailResponse
import com.mangala.wallet.local.portfolio.PortfolioDetailLocalDataSource
import com.mangala.wallet.domain.portfolio.repository.mappers.toCachedPortfolioDetail
import com.mangala.wallet.domain.portfolio.repository.mappers.toDomainModel
import com.mangala.wallet.domain.portfolio.repository.mappers.toDto
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.portfolio.MangalaPortfolioRemoteDataSource
import com.mangala.wallet.remote.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PortfolioRepositoryImpl(
    private val remoteDataSource: MangalaPortfolioRemoteDataSource,
    private val localDataSource: PortfolioDetailLocalDataSource
) : PortfolioRepository {
    
    override suspend fun createPortfolio(request: CreatePortfolioRequest): Result<Portfolio> {
        return try {
            when (val response = remoteDataSource.createPortfolio(request.toDto())) {
                is ApiResponse.Success -> {
                    // Convert response to Portfolio domain model
                    val portfolio = Portfolio(
                        portfolioId = response.body.id,
                        userId = response.body.id, // Note: API returns 'id' as userId
                        networkId = response.body.networkId,
                        totals = com.mangala.wallet.domain.portfolio.model.PortfolioTotals(
                            balanceUsdt = BigDecimal.ZERO,
                            pnl24hUsdt = BigDecimal.ZERO,
                            pnl24hPercent = BigDecimal.ZERO
                        ),
                        accounts = emptyList() // New portfolio starts empty
                    )
                    Result.success(portfolio)
                }
                is ApiResponse.Error -> {
                    val errorMessage = when (response) {
                        is ApiResponse.Error.HttpError -> "HTTP Error ${response.code}: ${response.errorBody}"
                        is ApiResponse.Error.NetworkError -> "Network Error: ${response.exception.message}"
                        is ApiResponse.Error.UnknownError -> "Unknown Error: ${response.message}"
                        is ApiResponse.Error.CustomError -> "Custom Error ${response.code}: ${response.errorBody}"
                        ApiResponse.Error.SerializationError -> "Serialization Error"
                        ApiResponse.Error.CancellationError -> "Request Cancelled"
                    }
                    Result.failure(Exception("Portfolio creation failed: $errorMessage"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addWalletToPortfolio(
        portfolioId: String,
        address: String,
        label: String
    ): Result<Unit> {
        return try {
            when (val response = remoteDataSource.addWalletToPortfolio(portfolioId, address, label)) {
                is ApiResponse.Success -> {
                    Result.success(Unit)
                }
                is ApiResponse.Error -> {
                    val errorMessage = when (response) {
                        is ApiResponse.Error.HttpError -> "HTTP Error ${response.code}: ${response.errorBody}"
                        is ApiResponse.Error.NetworkError -> "Network Error: ${response.exception.message}"
                        is ApiResponse.Error.UnknownError -> "Unknown Error: ${response.message}"
                        is ApiResponse.Error.CustomError -> "Custom Error ${response.code}: ${response.errorBody}"
                        ApiResponse.Error.SerializationError -> "Serialization Error"
                        ApiResponse.Error.CancellationError -> "Request Cancelled"
                    }
                    Result.failure(Exception("Failed to add wallet to portfolio: $errorMessage"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPortfolioDetail(portfolioId: String): Result<PortfolioDetailResponse> {
        return try {
            when (val response = remoteDataSource.getPortfolioDetail(portfolioId)) {
                is ApiResponse.Success -> {
                    val domainModel = response.body.toDomainModel()
                    
                    // Cache the response
                    localDataSource.savePortfolioDetail(response.body.toCachedPortfolioDetail())
                    
                    Result.success(domainModel)
                }
                is ApiResponse.Error -> {
                    val errorMessage = when (response) {
                        is ApiResponse.Error.HttpError -> "HTTP Error ${response.code}: ${response.errorBody}"
                        is ApiResponse.Error.NetworkError -> "Network Error: ${response.exception.message}"
                        is ApiResponse.Error.UnknownError -> "Unknown Error: ${response.message}"
                        is ApiResponse.Error.CustomError -> "Custom Error ${response.code}: ${response.errorBody}"
                        ApiResponse.Error.SerializationError -> "Serialization Error"
                        ApiResponse.Error.CancellationError -> "Request Cancelled"
                    }
                    Result.failure(Exception("Failed to get portfolio detail: $errorMessage"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getPortfolioDetailFlow(portfolioId: String, forceRefresh: Boolean): Flow<Resource<PortfolioDetailResponse?>> {
        // Extract userId and networkId from portfolioId
        // Note: This assumes portfolioId format like "userId_networkId" or similar
        // You may need to adjust this based on your actual portfolioId format
        val (userId, networkId) = parsePortfolioId(portfolioId)
        
        return networkBoundResource(
            query = { 
                localDataSource.getPortfolioDetailFlow(userId, networkId)
            },
            fetch = { 
                remoteDataSource.getPortfolioDetail(portfolioId)
            },
            saveFetchResult = { dto ->
                localDataSource.savePortfolioDetail(dto.toCachedPortfolioDetail())
            },
            shouldFetch = { cachedData ->
                forceRefresh || cachedData == null || !localDataSource.isDataFresh(userId, networkId, 5)
            },
            entityToDomain = { cachedData ->
                cachedData?.toDomainModel()
            }
        ).map { resource ->
            when (resource) {
                is Resource.Success -> resource
                is Resource.Loading -> resource
                is Resource.Error -> {
                    // Try to return cached data on error if available
                    val cachedData = localDataSource.getPortfolioDetail(userId, networkId)
                    if (cachedData != null) {
                        Resource.Success(cachedData.toDomainModel())
                    } else {
                        resource
                    }
                }
            }
        }
    }

    override fun getPortfolioByNetworkFlow(userId: String, networkId: Int, forceRefresh: Boolean): Flow<Resource<PortfolioDetailResponse?>> {
        return networkBoundResource(
            query = {
                localDataSource.getPortfolioDetailFlow(userId, networkId)
            },
            fetch = {
                remoteDataSource.getPortfolioByNetwork(networkId)
            },
            saveFetchResult = { dto ->
                localDataSource.savePortfolioDetail(dto.toCachedPortfolioDetail())
            },
            shouldFetch = { cachedData ->
                forceRefresh || cachedData == null || !localDataSource.isDataFresh(userId, networkId, 5)
            },
            entityToDomain = { cachedData ->
                cachedData?.toDomainModel()
            }
        ).map { resource ->
            when (resource) {
                is Resource.Success -> resource
                is Resource.Loading -> resource
                is Resource.Error -> {
                    // Try to return cached data on error if available
                    val cachedData = localDataSource.getPortfolioDetail(userId, networkId)
                    if (cachedData != null) {
                        Resource.Success(cachedData.toDomainModel())
                    } else {
                        resource
                    }
                }
            }
        }
    }

    override suspend fun getPortfolioByNetwork(
        userId: String,
        networkId: Int
    ): Result<PortfolioDetailResponse> {
        return try {
            // First try to get from local cache
            val cachedData = localDataSource.getPortfolioDetail(userId, networkId)
            if (cachedData != null && localDataSource.isDataFresh(userId, networkId, 5)) {
                Result.success(cachedData.toDomainModel())
            } else {
                // Cache miss or stale data, fetch from remote
                when (val response = remoteDataSource.getPortfolioByNetwork(networkId)) {
                    is ApiResponse.Success -> {
                        val domainModel = response.body.toDomainModel()
                        
                        // Cache the response
                        localDataSource.savePortfolioDetail(response.body.toCachedPortfolioDetail())
                        
                        Result.success(domainModel)
                    }
                    is ApiResponse.Error -> {
                        // If remote fails but we have cached data, return cached
                        if (cachedData != null) {
                            Result.success(cachedData.toDomainModel())
                        } else {
                            val errorMessage = when (response) {
                                is ApiResponse.Error.HttpError -> "HTTP Error ${response.code}: ${response.errorBody}"
                                is ApiResponse.Error.NetworkError -> "Network Error: ${response.exception.message}"
                                is ApiResponse.Error.UnknownError -> "Unknown Error: ${response.message}"
                                is ApiResponse.Error.CustomError -> "Custom Error ${response.code}: ${response.errorBody}"
                                ApiResponse.Error.SerializationError -> "Serialization Error"
                                ApiResponse.Error.CancellationError -> "Request Cancelled"
                            }
                            Result.failure(Exception("Failed to get portfolio by network: $errorMessage"))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Try to return cached data on exception
            try {
                val cachedData = localDataSource.getPortfolioDetail(userId, networkId)
                if (cachedData != null) {
                    Result.success(cachedData.toDomainModel())
                } else {
                    Result.failure(e)
                }
            } catch (_: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun syncPortfolioData(portfolioId: String): Result<PortfolioDetailResponse> {
        return try {
            when (val response = remoteDataSource.syncPortfolio(portfolioId)) {
                is ApiResponse.Success -> {
                    val domainModel = response.body.toDomainModel()
                    
                    // Cache the synced response
                    localDataSource.savePortfolioDetail(response.body.toCachedPortfolioDetail())
                    
                    Result.success(domainModel)
                }
                is ApiResponse.Error -> {
                    val errorMessage = when (response) {
                        is ApiResponse.Error.HttpError -> "HTTP Error ${response.code}: ${response.errorBody}"
                        is ApiResponse.Error.NetworkError -> "Network Error: ${response.exception.message}"
                        is ApiResponse.Error.UnknownError -> "Unknown Error: ${response.message}"
                        is ApiResponse.Error.CustomError -> "Custom Error ${response.code}: ${response.errorBody}"
                        ApiResponse.Error.SerializationError -> "Serialization Error"
                        ApiResponse.Error.CancellationError -> "Request Cancelled"
                    }
                    Result.failure(Exception("Failed to sync portfolio: $errorMessage"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPortfolioByAccountAddress(address: String): Portfolio? {
        return try {
            val cachedDetail = localDataSource.getPortfolioByAccountAddress(address)
            cachedDetail?.toDomainModel()?.portfolio
        } catch (_: Exception) {
            null
        }
    }
    
    override suspend fun getCachedPortfolios(): List<Portfolio> {
        return try {
            // This would need to be implemented to get all cached portfolios
            // For now, return empty list as it would require additional database queries
            emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
    
    /**
     * Parse portfolio ID to extract userId and networkId
     * This is a placeholder implementation - adjust based on your actual portfolioId format
     */
    private fun parsePortfolioId(portfolioId: String): Pair<String, Int> {
        // Example implementation assuming portfolioId format: "userId_networkId"
        val parts = portfolioId.split("_")
        return if (parts.size >= 2) {
            parts[0] to (parts[1].toIntOrNull() ?: 1)
        } else {
            portfolioId to 1 // Default to network ID 1 (Antelope)
        }
    }
}