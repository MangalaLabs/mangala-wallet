# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Mangala Wallet is a Kotlin Multiplatform cryptocurrency wallet supporting Android, iOS, and Desktop platforms. It supports multiple blockchain networks including Antelope (EOS/WAX/Telos), EVM-compatible chains, and Bitcoin.

The project uses three build variants:
- **Pro**: Full-featured wallet (combined functionality)
- **Cold**: Air-gapped signing device for secure transaction signing
- **UI**: Transaction broadcast only (works with Cold variant)

## Essential Commands

### Initial Setup

```bash
# Initialize submodules
git submodule update --init --recursive

# Create local.properties with required API keys (see README.md)
# Required keys: GITHUB_ACTOR, GITHUB_TOKEN, ALCHEMY_API_KEY, COVALENTHQ_API_KEY, INFURA_API_KEY, INFURA_SECRET_KEY
```

### Build Commands

```bash
# Build all modules
./gradlew build

# Run tests
./gradlew test

# Check code style
./gradlew ktlintCheck

# Generate database interfaces after editing .sq files
./gradlew generateCommonMainAntelopeDatabaseInterface
```

### Platform-Specific Builds

**Android**: Use Android Studio build variants

**iOS**:
- Select scheme in Xcode
- If package issues occur: File → Packages → Reset Package Caches
- Pod install: `./gradlew :composeApp:podInstallSyntheticIos`

**Desktop**:
1. Set `desktopBuildType` in `gradle.properties` (debug or release)
2. Run `./gradlew :common:utils:generateBuildKonfig` to update values

### Build Variant Configuration

Set `currentFlavor` in `gradle.properties`:
- `currentFlavor=pro` (default)
- `currentFlavor=cold`
- `currentFlavor=ui`

**IMPORTANT**: Do not commit changes to `currentFlavor` in `gradle.properties`

### Database Modifications

When editing prepopulated database in `./common/mokoresources/src/commonMain/moko-resources/files/`:

```bash
cd ./common/mokoresources/src/commonMain/moko-resources/files/
for file in *.sql; do
    sqlite3 mangalawallet.db < "$file"
done
```

Then create migration in `data/local/src/commonMain/sqldelight/migrations`

## Architecture

### Clean Architecture with MVVM

```
Presentation Layer (Compose UI + ScreenModels)
    ↓
Domain Layer (Use Cases + Domain Models)
    ↓
Data Layer (Repositories + Data Sources)
```

### Module Structure

- `composeApp/` - Main Compose Multiplatform application entry point
- `core/` - Core functionality modules:
  - `core/security/` - Security and cryptography
  - `core/auth/` - Authentication logic
  - `core/hdwallet/` - HD wallet implementation
  - `core/pin/` - PIN management
  - `core/biometry/` - Biometric authentication
  - `core/websocket-chat/` - WebSocket chat functionality
  - `core/ai/` - AI conversation features
- `features/` - Feature modules organized by functionality:
  - `features/wallet_*/` - Wallet management (pro/cold/ui variants)
  - `features/portfolio/` - Portfolio aggregation and display
  - `features/send_*/` - Send transaction flows
  - `features/chains/` - Blockchain-specific implementations
    - `features/chains/evmcompatible/` - EVM chains
    - `features/chains/antelope_*/` - Antelope chains
    - `features/chains/bitcoin/` - Bitcoin support
  - `features/passkey/` - Passkey authentication
  - `features/walletconnect/` - WalletConnect integration
  - `features/dex/` - DEX integrations (Uniswap, etc.)
- `data/` - Data layer:
  - `data/local/` - SQLDelight local database
  - `data/remote/` - Network data sources
  - `data/model/` - Data models
- `domain/` - Domain layer with use cases and domain models
- `libraries/` - Shared libraries (chart, scanqr, walletconnect, etc.)
- `antelope/` - Antelope blockchain SDK modules
- `common/` - Common utilities:
  - `common/ui/` - Shared UI components
  - `common/utils/` - Utility functions
  - `common/mokoresources/` - Resources and assets

### Key Technologies

- **UI**: Jetpack Compose Multiplatform
- **Navigation**: Type-safe navigation with sealed classes
- **DI**: Koin dependency injection
- **Database**: SQLDelight for local storage
- **Serialization**: kotlinx-serialization
- **Networking**: Ktor client
- **State Management**: MVVM with ScreenModels (Cafe Bazaar Voyager)
- **Async**: Kotlin Coroutines and Flow
- **Resources**: MOKO Resources for multiplatform assets

## Git Workflow

**CRITICAL**: Always follow the Git workflow defined in `GIT_WORKFLOW.md` and development standards in `.claude/development-standards.md`.

### Branch Naming

```
<type>/<short-description>
```

Types: `feature/`, `bugfix/`, `hotfix/`, `refactor/`, `docs/`, `test/`, `chore/`, `perf/`

Examples:
- `feature/add-solana-support`
- `bugfix/fix-balance-calculation`
- `hotfix/security-patch`

### Commit Messages

```
<type>(<scope>): <subject>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`, `ci`, `build`, `revert`

Scopes: `auth`, `wallet`, `portfolio`, `transaction`, `network`, `database`, `ui`, `core`, etc.

Examples:
- `feat(portfolio): add multi-wallet aggregation for EVM`
- `fix(transaction): resolve crash when parsing invalid data`
- `refactor(network): extract HTTP client configuration`

### Branching Strategy

- Branch from `develop` for features/bugfixes
- Branch from `master` for hotfixes
- Main branches: `master` (prod) → `staging` (alpha) → `uat` (internal) → `develop` (dev)

## Naming Conventions

### Kotlin Code

- **ScreenModels**: `*ScreenModel` (e.g., `WalletScreenModel`)
- **Use Cases**: `*UseCase` (e.g., `GetBalanceUseCase`)
- **Repositories**: `*Repository` (e.g., `WalletRepository`)
- **Data Sources**: `*DataSource` (e.g., `LocalWalletDataSource`)
- **Screens**: `*Screen` (e.g., `PortfolioScreen`)
- **UI State**: `*State` (e.g., `WalletState`)
- **Events**: `*Event` (e.g., `WalletEvent`)

### Compose UI

- Use PascalCase for composable functions
- Start with noun describing UI element: `WalletCard`, `TransactionList`, `BalanceHeader`
- Always pass `modifier` as first parameter
- Provide `@Preview` for UI components

## Code Patterns

### State Management

Use sealed interfaces for state and events:

```kotlin
sealed interface WalletState {
    data object Loading : WalletState
    data class Success(val balance: BigDecimal) : WalletState
    data class Error(val message: String) : WalletState
}
```

### Resource Loading

```kotlin
sealed interface Resource<out T> {
    data object Loading : Resource<Nothing>
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val message: String) : Resource<Nothing>
}
```

### Error Handling

```kotlin
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
}
```

## Testing

### Test Structure

- Use Arrange-Act-Assert pattern
- Name tests with backticks: `` `test description in backticks` ``
- One assertion concept per test
- Write tests for: use cases, ScreenModel logic, repositories, complex UI

### Running Tests

```bash
# All tests
./gradlew test

# Specific module
./gradlew :features:portfolio:test
```

## Security Requirements

1. **Never commit sensitive data**: API keys go in `local.properties`, use `.gitignore` properly
2. **Cryptography**: Use established libraries from `core/security/`, never roll your own crypto
3. **Input validation**: Validate all user inputs and sanitize external data
4. **Secure randomness**: Use proper secure random number generation from security module

## Common Patterns in This Codebase

### Feature Module Structure

Each feature module typically contains:
- `presentation/` - ScreenModels and Compose screens
- `domain/` - Use cases (if feature-specific)
- `di/` - Koin dependency injection modules
- `navigation/` - Navigation definitions

### Variant-Specific Modules

Many features have three variants (e.g., `wallet_pro`, `wallet_cold`, `wallet_ui`). Only `pro` variant is typically active. Check `settings.gradle.kts` for commented-out modules.

### Dependency Injection

Use Koin modules. Register dependencies in `di/` folders within each module. Modules are composed in `composeApp`.

### Navigation

Type-safe navigation using sealed classes for routes. Pass only necessary data between screens.

## Important Files

- `gradle.properties` - Build configuration (do not commit `currentFlavor` changes)
- `local.properties` - API keys and secrets (never commit)
- `settings.gradle.kts` - Module inclusion configuration
- `.gitmessage` - Commit message template
- `GIT_WORKFLOW.md` - Detailed Git workflow rules
- `.claude/development-standards.md` - Comprehensive development standards

## Common Issues and Solutions

### Build Issues

**Pod install failure**:
```bash
./gradlew :composeApp:podInstallSyntheticIos
./gradlew :libraries:kmpnotifier:podInstallSyntheticIos
```

**Missing Swift packages in Xcode**:
- File → Packages → Reset Package Caches

**Cannot find `strcmp` in scope**:
- Modify StatementAuthorizer file per GRDB.swift commit fcfdab2f

### Database Changes

After modifying `.sq` files in `data/local/src/commonMain/sqldelight/`:
1. Generate interfaces: `./gradlew generateCommonMainAntelopeDatabaseInterface`
2. Create migration in `data/local/src/commonMain/sqldelight/migrations/`

## Claude Code Specific Instructions

When working in this repository:

1. **Always read** `GIT_WORKFLOW.md` and `.claude/development-standards.md` before starting work
2. **Use TodoWrite tool** to track multi-step tasks and demonstrate progress
3. **Follow naming conventions** exactly as specified above
4. **Create branches** using proper naming format before making changes
5. **Write conventional commits** with appropriate type and scope
6. **Add tests** for new functionality (use cases, repositories, complex logic)
7. **Never expose secrets** - check that API keys stay in `local.properties`
8. **Prefer editing** existing files over creating new ones
9. **Reference file locations** using `file_path:line_number` format when discussing code
10. **Check build variant context** - some features only exist in specific variants

### Multi-Wallet Architecture Context

The codebase supports multiple wallets and blockchain networks. When working on features:
- Portfolio features aggregate across multiple wallets and networks
- Network-specific logic goes in `features/chains/[network]/`
- Shared blockchain logic goes in `core/` modules
- Use cases in `domain/` orchestrate cross-module operations

### Important Caveats

- Not all feature modules are active - check `settings.gradle.kts` for included modules
- Build variant affects which modules are compiled - `pro` is the full-featured variant
- Some modules have platform-specific implementations (`iosMain`, `androidMain`, `jvmMain`)
