---
name: coding-conventions
description: Kotlin Multiplatform coding conventions for Mangala Wallet - naming patterns, Clean Architecture rules, ScreenModel/UseCase/Repository patterns, Compose best practices. Auto-applies when writing or editing Kotlin code.
user-invocable: false
---

# Coding Conventions

Follow these conventions when writing or editing Kotlin code in Mangala Wallet.

## Naming Patterns

| Component | Pattern | Example |
|-----------|---------|---------|
| Screen | `*Screen` | `WalletMainScreen` |
| ScreenModel | `*ScreenModel` | `WalletScreenModel` |
| UseCase | `*UseCase` | `GetBalanceUseCase` |
| Repository (interface) | `*Repository` | `WalletRepository` |
| Repository (impl) | `*RepositoryImpl` | `WalletRepositoryImpl` |
| DataSource | `*DataSource` | `LocalWalletDataSource` |
| State | `*State` | `WalletState` |
| Event | `*Event` | `WalletEvent` |
| Koin Module | `*Module` | `walletModule` (val, camelCase) |

## Architecture Layer Rules

```
Presentation (Screen + ScreenModel)
    ↓ depends on
Domain (UseCase + Repository interface + Entity)
    ↓ depends on
Data (RepositoryImpl + DataSource + DTO)
```

**NEVER**: Domain depends on Data. Presentation depends on Data directly. Data depends on Presentation.

## Key Patterns

### ScreenModel
```kotlin
class FeatureScreenModel(
    private val useCase: FeatureUseCase
) : ScreenModel(), KoinComponent {
    private val _state = MutableStateFlow<FeatureState>(FeatureState.Loading)
    val state: StateFlow<FeatureState> = _state.asStateFlow()
}
```

### UseCase
```kotlin
class GetFeatureDataUseCase(
    private val repository: FeatureRepository
) : UseCase<FeatureData>() {
    override suspend fun run(params: Map<String, Any?>): FeatureData {
        // Business logic here
    }
}
```

### State / Event (sealed interface)
```kotlin
sealed interface FeatureState {
    data object Loading : FeatureState
    data class Success(val data: FeatureData) : FeatureState
    data class Error(val message: String) : FeatureState
}

sealed interface FeatureEvent {
    data class OnItemClick(val id: String) : FeatureEvent
    data object OnRefresh : FeatureEvent
}
```

## Kotlin Rules

1. **Immutability**: `val` over `var`, `listOf` over `mutableListOf`, `data class` with `val` properties
2. **Null safety**: No double-bang operator. Use `?.`, `?:`, `let`, `require()`, `check()`
3. **Coroutines**: Use `viewModelScope` (via ScreenModel), never `GlobalScope`
4. **Error handling**: `Result<T>` or `Resource<T>` sealed class, never raw try-catch in ScreenModel

## Compose Rules

1. Composable functions: PascalCase, noun-based (`WalletCard`, not `showWallet`)
2. Pass `Modifier` as first parameter
3. State hoisting: stateless composables, state in ScreenModel
4. Use `remember` for expensive calculations
5. Use `derivedStateOf` for computed state
6. Use `key()` in `LazyColumn`/`LazyRow` items

## DI (Koin)

```kotlin
val featureModule = module {
    factory { FeatureUseCase(get()) }
    factory { FeatureRepositoryImpl(get()) as FeatureRepository }
    factory { FeatureScreenModel(get()) }
}
```

Register in the appropriate feature module's DI package. ScreenModel uses `by inject()` from `KoinComponent`.
