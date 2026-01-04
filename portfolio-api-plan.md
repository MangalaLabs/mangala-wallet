# Portfolio API Integration Plan

## Overview

This document outlines the plan to integrate the new Mangala Portfolio API (`https://staging-api.mangala.io`) into the Mangala Wallet application following Clean Architecture principles. The integration will enable automatic portfolio registration after account creation/import and provide enhanced balance and pricing data.

## API Endpoint Details

- **Base URL**: `https://staging-api.mangala.io`
- **Portfolio Registration**: `POST /portfolios`
- **Request Body**:
  ```json
  {
    "name": "Vaulta portfolio 2",
    "description": "Main cryptocurrency holdings1", 
    "networkId": 1,
    "initialWallet": {
      "address": "leonard.man",
      "label": "Main Wallet"
    }
  }
  ```

## Architecture Overview

The integration follows the existing Clean Architecture pattern with three main layers:
- **Domain Layer**: Business logic, models, use cases, and repository interfaces
- **Data Layer**: API clients, local storage, and repository implementations  
- **Presentation Layer**: ViewModels and UI components

## Phase 1: Domain Layer - Models and Use Cases

### 1.1 Domain Models

**Portfolio Model**
```kotlin
data class Portfolio(
    val userId: String,
    val networkId: Int,
    val totals: PortfolioTotals,
    val accounts: List<PortfolioAccount>
)
```

**PortfolioTotals Model**
```kotlin
data class PortfolioTotals(
    val balanceUsdt: BigDecimal,
    val pnl24hUsdt: BigDecimal,
    val pnl24hPercent: BigDecimal
)
```

**PortfolioAccount Model**
```kotlin
data class PortfolioAccount(
    val accountId: String,
    val address: String,
    val label: String,
    val createdAt: String,
    val totals: PortfolioTotals,
    val tokens: List<PortfolioToken>,
    val resources: PortfolioResources?
)
```

**PortfolioToken Model**
```kotlin
data class PortfolioToken(
    val tokenKey: String,
    val symbol: String,
    val name: String,
    val quantity: BigDecimal,
    val balanceUsdt: BigDecimal,
    val priceUsdt: BigDecimal,
    val pnl24hUsdt: BigDecimal,
    val pnl24hPercent: BigDecimal
)
```

**PortfolioResources Model**
```kotlin
data class PortfolioResources(
    val cpu: ResourceInfo?,
    val net: ResourceInfo?,
    val ram: ResourceInfo?
)

data class ResourceInfo(
    val used: BigDecimal,
    val max: BigDecimal,
    val available: BigDecimal
)
```

**PricingContext Model**
```kotlin
data class PricingContext(
    val asOf: String,
    val quoteCurrency: String,
    val status: String,
    val prices: Map<String, TokenPrice>
)

data class TokenPrice(
    val spot: BigDecimal,
    val price24hAgo: BigDecimal,
    val lastUpdated: String,
    val source: String
)
```

**PortfolioDetailResponse Model**
```kotlin
data class PortfolioDetailResponse(
    val portfolio: Portfolio,
    val pricingContext: PricingContext
)
```

### 1.3 Use Cases

**CreatePortfolioUseCase**
- **Location**: `domain/src/commonMain/kotlin/com/mangala/wallet/domain/portfolio/usecases/CreatePortfolioUseCase.kt`
- **Purpose**: Register portfolio after account creation/import
- **Triggers**: Called automatically when accounts are created or imported

**GetPortfolioBalanceUseCase**
- **Location**: `domain/src/commonMain/kotlin/com/mangala/wallet/domain/portfolio/usecases/GetPortfolioBalanceUseCase.kt`
- **Purpose**: Load balance and pricing data from portfolio API
- **Integration**: Replaces current balance loading mechanisms

**SyncPortfolioDataUseCase**
- **Location**: `domain/src/commonMain/kotlin/com/mangala/wallet/domain/portfolio/usecases/SyncPortfolioDataUseCase.kt`
- **Purpose**: Background synchronization of portfolio data
- **Features**: Retry logic, error handling, periodic sync

**GetPortfolioByAccountUseCase**
- **Location**: `domain/src/commonMain/kotlin/com/mangala/wallet/domain/portfolio/usecases/GetPortfolioByAccountUseCase.kt`
- **Purpose**: Retrieve portfolio information for specific account

## Phase 2: Data Layer - API Client and Repository Implementation

### 2.1 API Models (DTOs)

**Location**: `data/model/src/commonMain/kotlin/com/mangala/wallet/model/portfolio/`

**CreatePortfolioRequestDto**
```kotlin
@Serializable
data class CreatePortfolioRequestDto(
    val name: String,
    val description: String,
    val networkId: Int,
    val initialWallet: InitialWalletDto
)

@Serializable
data class InitialWalletDto(
    val address: String,
    val label: String
)
```

**CreatePortfolioResponseDto**
```kotlin
@Serializable
data class CreatePortfolioResponseDto(
    val id: String,
    val name: String,
    val description: String,
    val networkId: Int,
    val createdAt: String
)
```

**PortfolioDetailResponseDto**
```kotlin
@Serializable
data class PortfolioDetailResponseDto(
    val data: PortfolioDataDto
)

@Serializable
data class PortfolioDataDto(
    val portfolio: PortfolioDto,
    val pricingContext: PricingContextDto
)

@Serializable
data class PortfolioDto(
    val userId: String,
    val networkId: Int,
    val totals: PortfolioTotalsDto,
    val accounts: List<PortfolioAccountDto>
)

@Serializable
data class PortfolioTotalsDto(
    val balanceUsdt: String,
    val pnl24hUsdt: String,
    val pnl24hPercent: String
)

@Serializable
data class PortfolioAccountDto(
    val accountId: String,
    val address: String,
    val label: String,
    val createdAt: String,
    val totals: PortfolioTotalsDto,
    val tokens: List<PortfolioTokenDto>,
    val resources: PortfolioResourcesDto?
)

@Serializable
data class PortfolioTokenDto(
    val tokenKey: String,
    val symbol: String,
    val name: String,
    val quantity: String,
    val balanceUsdt: String,
    val priceUsdt: String,
    val pnl24hUsdt: String,
    val pnl24hPercent: String
)

@Serializable
data class PortfolioResourcesDto(
    val cpu: ResourceInfoDto?,
    val net: ResourceInfoDto?,
    val ram: ResourceInfoDto?
)

@Serializable
data class ResourceInfoDto(
    val used: String,
    val max: String,
    val available: String
)

@Serializable
data class PricingContextDto(
    val asOf: String,
    val quoteCurrency: String,
    val status: String,
    val prices: Map<String, TokenPriceDto>
)

@Serializable
data class TokenPriceDto(
    val spot: String,
    val price24hAgo: String,
    val lastUpdated: String,
    val source: String
)
```

### 2.2 API Interface

**Location**: `data/remote/src/commonMain/kotlin/com/mangala/wallet/remote/portfolio/MangalaPortfolioApi.kt`

```kotlin
@KtorfitApi
interface MangalaPortfolioApi {
    @POST("portfolios")
    suspend fun createPortfolio(@Body request: CreatePortfolioRequestDto): CreatePortfolioResponseDto
    
    @GET("portfolios/{portfolioId}")
    suspend fun getPortfolioDetail(@Path("portfolioId") portfolioId: String): PortfolioDetailResponseDto
    
    @PUT("portfolios/{portfolioId}/sync")
    suspend fun syncPortfolio(@Path("portfolioId") portfolioId: String): PortfolioDetailResponseDto
}
```

### 2.3 Remote Data Source

**Location**: `data/remote/src/commonMain/kotlin/com/mangala/wallet/remote/portfolio/MangalaPortfolioRemoteDataSource.kt`

```kotlin
class MangalaPortfolioRemoteDataSource(private val api: MangalaPortfolioApi) {
    suspend fun createPortfolio(request: CreatePortfolioRequestDto): ApiResponse<CreatePortfolioResponseDto, String> = 
        safeApiCall { api.createPortfolio(request) }
    
    suspend fun getPortfolioDetail(portfolioId: String): ApiResponse<PortfolioDetailResponseDto, String> =
        safeApiCall { api.getPortfolioDetail(portfolioId) }
        
    suspend fun syncPortfolio(portfolioId: String): ApiResponse<PortfolioDetailResponseDto, String> =
        safeApiCall { api.syncPortfolio(portfolioId) }
}
```

### 2.4 Local Data Source

**Location**: `data/local/src/commonMain/kotlin/com/mangala/wallet/local/portfolio/`

**SQLDelight Entities**
```sql
-- portfolio.sq
CREATE TABLE PortfolioEntity (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    network_id INTEGER NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

CREATE TABLE PortfolioWalletEntity (
    portfolio_id TEXT NOT NULL,
    address TEXT NOT NULL,
    label TEXT NOT NULL,
    network_type TEXT NOT NULL,
    PRIMARY KEY (portfolio_id, address),
    FOREIGN KEY (portfolio_id) REFERENCES PortfolioEntity(id)
);

CREATE TABLE PortfolioBalanceEntity (
    portfolio_id TEXT PRIMARY KEY NOT NULL,
    total_balance TEXT NOT NULL,
    total_usd_value TEXT NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY (portfolio_id) REFERENCES PortfolioEntity(id)
);
```

### 2.5 Repository Implementation

**Location**: `domain/src/commonMain/kotlin/com/mangala/wallet/domain/portfolio/repository/PortfolioRepositoryImpl.kt`

```kotlin
class PortfolioRepositoryImpl(
    private val remoteDataSource: MangalaPortfolioRemoteDataSource,
    private val localDataSource: PortfolioLocalDataSource,
    private val balanceLocalDataSource: PortfolioBalanceLocalDataSource
) : PortfolioRepository {
    
    override suspend fun getPortfolioBalanceFlow(portfolioId: String): Flow<Resource<PortfolioBalance>> = 
        networkBoundResource(
            query = { balanceLocalDataSource.getPortfolioBalanceFlow(portfolioId) },
            fetch = { remoteDataSource.getPortfolioBalance(portfolioId) },
            saveFetchResult = { balanceLocalDataSource.savePortfolioBalance(it.toDomainModel()) },
            shouldFetch = { checkShouldFetchBalance(portfolioId) },
            entityToDomain = { it.toDomainModel() }
        )
}
```

## Phase 3: Multi-Network Support Architecture

### 3.1 Network Abstraction

**NetworkType to NetworkId Mapping**
```kotlin
object PortfolioNetworkMapper {
    fun mapNetworkTypeToNetworkId(networkType: NetworkType): Int = when (networkType) {
        NetworkType.ANTELOPE -> 1
        NetworkType.EVM -> 2
        NetworkType.BITCOIN -> 3
        NetworkType.BINANCE -> 4
        // Add more as needed
    }
    
    fun mapNetworkIdToNetworkType(networkId: Int): NetworkType = when (networkId) {
        1 -> NetworkType.ANTELOPE
        2 -> NetworkType.EVM
        3 -> NetworkType.BITCOIN
        4 -> NetworkType.BINANCE
        else -> throw IllegalArgumentException("Unknown networkId: $networkId")
    }
}
```

### 3.2 Account Creation Integration

**PortfolioAccountCreator**

**Location**: `domain/src/commonMain/kotlin/com/mangala/wallet/domain/portfolio/usecases/PortfolioAccountCreator.kt`

```kotlin
class PortfolioAccountCreator(
    private val createPortfolioUseCase: CreatePortfolioUseCase,
    private val portfolioNetworkMapper: PortfolioNetworkMapper
) : AccountCreator {
    
    override suspend fun createAccount(
        accountId: String,
        derivationPathIndex: Int,
        wallet: WalletModel
    ) {
        // Determine network type based on wallet/account context
        val networkType = determineNetworkType(wallet)
        val networkId = portfolioNetworkMapper.mapNetworkTypeToNetworkId(networkType)
        
        val request = CreatePortfolioRequest(
            name = wallet.name,
            description = "",
            networkId = networkId,
            initialWallet = PortfolioWallet(
                address = accountId,
                label = wallet.name
            )
        )
        
        createPortfolioUseCase(request)
    }
}
```

## Phase 4: Presentation Layer Updates

### 4.1 ViewModel Updates

**AntelopeWalletViewModel Changes**

**Location**: `features/wallet_pro/src/commonMain/kotlin/com/mangala/features/wallet/presentationv2/antelope/AntelopeWalletViewModel.kt`

**Constructor Updates**
```kotlin
class AntelopeWalletViewModel(
    // Existing dependencies...
    private val getPortfolioBalanceUseCase: GetPortfolioBalanceUseCase,
    private val getPortfolioByAccountUseCase: GetPortfolioByAccountUseCase,
    private val syncPortfolioDataUseCase: SyncPortfolioDataUseCase
) : BaseWalletViewModel() {
    // Implementation...
}
```

**Load Wallet Data Updates**
```kotlin
private fun loadWalletData(blockchainNetworkData: BlockchainNetworkData) {
    screenModelScope.launch {
        val accounts = getAntelopeAccountsUseCase(blockchainType = blockchainNetworkData.blockchainType)
        
        // Load portfolio data instead of individual balances
        val portfolioData = accounts.mapNotNull { account ->
            getPortfolioByAccountUseCase(account.accountName)
        }
        
        // Collect portfolio balance flows
        portfolioData.forEach { portfolio ->
            getPortfolioBalanceUseCase.invoke(portfolio.id).collect { balanceResource ->
                when (balanceResource) {
                    is Resource.Success -> updateUIStateWithPortfolioBalance(portfolio, balanceResource.data)
                    is Resource.Loading -> updateUIStateWithLoading()
                    is Resource.Error -> handlePortfolioError(balanceResource.exception)
                }
            }
        }
    }
}
```

### 4.2 UI State Updates

**AntelopeWalletUiState Extensions**
```kotlin
data class AntelopeWalletUiState(
    // Existing fields...
    val portfolioSyncStatus: PortfolioSyncStatus = PortfolioSyncStatus.Idle,
    val portfolioError: String? = null,
    val isPortfolioDataAvailable: Boolean = false
)

enum class PortfolioSyncStatus {
    Idle, Syncing, Success, Error
}
```

## Phase 5: Integration Points

### 5.1 Account Creation Flow Integration

**Trigger Points:**
1. **New Account Creation**: `CreateWalletAccountUseCase` completion
2. **Account Import**: `RestoreWalletUseCase` completion  
3. **Private Key Import**: Import flow completion

**Implementation:**
- `PortfolioAccountCreator` registered in `AccountCreator` list
- Automatic portfolio registration on successful account creation
- Error handling for portfolio creation failures

### 5.2 Balance Loading Flow Integration

**Current Flow Replacement:**
- Replace calls to existing balance use cases in `AntelopeWalletViewModel`
- Use `GetPortfolioBalanceUseCase` as primary data source
- Maintain fallback to existing balance loading for error scenarios

**Migration Strategy:**
- Feature flag to toggle between old and new balance loading
- Gradual rollout with monitoring
- Fallback mechanisms for API failures

### 5.3 Network Selection Updates

**Network Switching:**
- Sync portfolio data when user switches networks
- Handle multi-network portfolio scenarios
- Cache management for different network data

## Phase 6: Dependency Injection Setup

### 6.1 Domain Module Updates

**Location**: `domain/src/commonMain/kotlin/com/mangala/wallet/domain/di/DomainModule.kt`

```kotlin
fun domainModule() = module {
    // Existing dependencies...
    
    // Portfolio Repository
    singleOf(::PortfolioRepositoryImpl) bind PortfolioRepository::class
    
    // Portfolio Use Cases
    factoryOf(::CreatePortfolioUseCase)
    factoryOf(::GetPortfolioBalanceUseCase)
    factoryOf(::SyncPortfolioDataUseCase)
    factoryOf(::GetPortfolioByAccountUseCase)
    
    // Portfolio Account Creator
    factory<AccountCreator> { PortfolioAccountCreator(get(), get()) }
    
    // Network Mapper
    singleOf(::PortfolioNetworkMapper)
}
```

### 6.2 Remote Module Updates

**Location**: `data/remote/src/commonMain/kotlin/com/mangala/wallet/remote/di/RemoteModule.kt`

```kotlin
fun remoteModule() = module {
    // Existing dependencies...
    
    // Portfolio API
    single<MangalaPortfolioApi> { 
        provideKtorfit(get<HttpClient>(), "https://staging-api.mangala.io").create()
    }
    
    // Portfolio Remote Data Source
    factoryOf(::MangalaPortfolioRemoteDataSource)
}
```

### 6.3 Local Module Updates

**Location**: `data/local/src/commonMain/kotlin/com/mangala/wallet/local/di/LocalModule.kt`

```kotlin
fun localModule() = module {
    // Existing dependencies...
    
    // Portfolio Local Data Sources
    factoryOf(::PortfolioLocalDataSource)
    factoryOf(::PortfolioBalanceLocalDataSource)
}
```

## Phase 7: Error Handling and Fallbacks

### 7.1 Graceful Degradation

**Fallback Strategy:**
- Portfolio API failure → Use existing balance loading mechanisms
- Network connectivity issues → Display cached data with sync status
- Authentication errors → Prompt for re-authentication

**Error Types:**
- Network connectivity errors
- API authentication failures
- Portfolio not found errors
- Balance calculation errors

### 7.2 User Experience

**Loading States:**
- Portfolio sync in progress indicators
- Balance data freshness indicators
- Error messages with retry options

**Retry Mechanisms:**
- Automatic retry with exponential backoff
- Manual retry options in UI
- Background sync on app resume

## Implementation Timeline

### Week 1-2: Foundation
- [ ] Domain models and repository interfaces
- [ ] API DTOs and interface definitions
- [ ] SQLDelight schema updates

### Week 3-4: Data Layer
- [ ] API client implementation
- [ ] Remote and local data sources
- [ ] Repository implementation with caching

### Week 5: Integration
- [ ] Account creation integration
- [ ] Use case implementations
- [ ] Network mapper implementation

### Week 6: Presentation Updates
- [ ] ViewModel updates
- [ ] UI state modifications
- [ ] Error handling implementation

### Week 7: Testing & Polish
- [ ] Unit tests for all components
- [ ] Integration testing
- [ ] Error scenario testing
- [ ] Performance optimization

### Week 8: Deployment
- [ ] Feature flag implementation
- [ ] Gradual rollout preparation
- [ ] Monitoring and analytics setup

## Testing Strategy

### Unit Tests
- All use cases and repository methods
- Network mapping logic
- Error handling scenarios
- Data transformation methods

### Integration Tests
- API client integration
- Database operations
- End-to-end account creation flow
- Balance loading scenarios

### Performance Tests
- API response times
- Caching effectiveness
- Memory usage optimization
- Background sync performance

## Monitoring and Analytics

### Key Metrics
- Portfolio creation success rate
- API response times
- Cache hit/miss ratios
- Error rates by type
- User engagement with portfolio features

### Logging
- Portfolio API calls and responses
- Account creation events
- Balance sync operations
- Error occurrences with context

## Security Considerations

### API Security
- Authentication token management
- Request/response encryption
- API key rotation support
- Rate limiting compliance

### Data Protection
- Sensitive data encryption at rest
- Secure transmission protocols
- User data privacy compliance
- Audit logging for sensitive operations

## Migration and Rollback Plan

### Feature Flag Strategy
- `ENABLE_PORTFOLIO_API`: Master switch
- `PORTFOLIO_BALANCE_LOADING`: Balance loading specific toggle
- `PORTFOLIO_AUTO_REGISTRATION`: Account creation integration toggle

### Rollback Procedures
- Disable feature flags to revert to old behavior
- Database rollback procedures for schema changes
- API client fallback mechanisms
- User communication for service interruptions

## Success Criteria

### Functional Requirements
- ✅ Automatic portfolio registration on account creation
- ✅ Balance and pricing data loaded from portfolio API
- ✅ Multi-network support (Antelope, EVM, etc.)
- ✅ Graceful fallback to existing balance loading
- ✅ Real-time balance updates and synchronization

### Performance Requirements
- Portfolio API response time < 2 seconds
- Balance data cache hit rate > 80%
- UI responsiveness maintained during sync operations
- Background sync completion within 30 seconds

### Quality Requirements
- Zero critical bugs in production
- 99.5% API success rate
- Comprehensive error handling coverage
- Backward compatibility maintained

---

**Document Version**: 1.0  
**Last Updated**: 2025-08-20  
**Review Status**: Pending Approval