# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Mangala Wallet is a Kotlin Multiplatform (KMP) cryptocurrency wallet targeting Android, iOS, and Desktop. It supports multiple blockchain networks (EVM, Bitcoin, Antelope/EOS) with three build variants:

- **Pro**: Full-featured wallet (signing + broadcasting) — default
- **Cold**: Air-gapped signing-only wallet (no internet)
- **UI**: Broadcast-only wallet (no signing, pairs with Cold)

The active variant is controlled by `currentFlavor` in `gradle.properties`. Many modules have flavor-specific implementations (e.g., `features/wallet_pro`, `features/home_pro`) that are selected at build time via `project(":features:wallet_${currentFlavor}")`.

## Build & Development Commands

```bash
# Build Android APK (Pro Debug)
./gradlew :composeApp:assembleProDevDebug

# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :core:wallet:test
./gradlew :domain:test

# Generate SQLDelight code after editing .sq files
./gradlew generateCommonMainMangalaWalletDatabaseInterface

# Generate Antelope DB code after editing .sq files
./gradlew generateCommonMainAntelopeDatabaseInterface

# iOS pod install (if cocoapods error)
./gradlew :composeApp:podInstallSyntheticIos

# kmpnotifier pod fix
./gradlew :libraries:kmpnotifier:podInstallSyntheticIos

# OWASP dependency check
./gradlew dependencyCheckAnalyze
```

Android build uses two flavor dimensions: `mode` (pro/cold/ui) and `environment` (dev/stg/uat/prod). Desktop build type is set via `desktopBuildType` in `gradle.properties`.

## Architecture

**Clean Architecture + MVVM** with Voyager navigation and Koin DI.

```
Compose UI (Screen) → ScreenModel → UseCase → Repository → DataSource
```

### Key Patterns

- **ScreenModel** (not ViewModel): Uses Voyager's `ScreenModel` with `KoinComponent` for DI injection via `by inject()`. State exposed as `StateFlow`.
- **UseCase base class**: `abstract class UseCase<out Type>` with `suspend fun run(params: Map<String, Any?>)` and `operator fun invoke()`.
- **Navigation**: Voyager `Navigator` with `Screen` interface. Root entry point is `RootScreen` → `RootScreenModel` which determines initial destination (OnboardingScreen, UnlockPinScreen, or HomeScreen).
- **Flavor-aware dependencies**: `composeApp` uses string interpolation for variant modules: `implementation(project(":features:home_${currentFlavor}"))`. Feature modules with `_base` suffix contain shared code; `_pro`/`_cold`/`_ui` suffixes contain variant-specific implementations.
- **Resources**: Moko Resources (`SharedMR` in package `com.mangala.wallet`). Strings, images, and prepopulated SQLite database in `common/mokoresources/src/commonMain/moko-resources/`.

### Module Dependency Flow

```
composeApp (app entry point)
  ├── features/* (UI + presentation)
  │     └── domain (use cases + repositories)
  │           ├── data:local (SQLDelight DB)
  │           ├── data:remote (Ktor HTTP + WebSocket)
  │           └── data:model (shared DTOs)
  ├── core/* (cross-cutting: auth, crypto, security, pin, biometry, hdwallet)
  ├── antelope/* (EOSIO chain-specific: rpc, actions, key manager)
  └── common/* (ui components, utils, moko resources, test helpers)
```

### Data Layer

- **Local**: SQLDelight (`data/local/`), database file at `MangalaWalletDatabase.sq`, migrations in `data/local/src/commonMain/sqldelight/migrations/`
- **Remote**: Ktor client (`data/remote/`), integrates with Alchemy, Covalent, Infura, Moralis APIs
- **Prepopulated DB**: SQLite file in `common/mokoresources/src/commonMain/moko-resources/files/`. Edit SQL files there, then run `sqlite3 mangalawallet.db < file.sql` and add a migration.

### Convention Plugins

`build-logic/convention/` provides `mangala.kotlin.multiplatform.feature` which applies: kotlin-multiplatform, android-library, kotlinx-serialization, compose, compose-compiler, kotlin-parcelize.

## Git Workflow

Branch from `develop` (or `master` for hotfixes). Branch naming: `<type>/<short-description>` where type is `feature/`, `bugfix/`, `hotfix/`, `refactor/`, `docs/`, `test/`, `chore/`, `perf/`.

Commit messages follow Conventional Commits: `<type>(<scope>): <subject>`. Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`, `ci`, `build`, `revert`. Scopes: `auth`, `wallet`, `ui`, `core`, `antelope`, `passkey`, `portfolio`, `transaction`, `network`, `database`.

PRs target `develop`. Squash merge for feature branches. Merge commit for promotion: develop → uat → staging → master.

## Key Dependencies & Versions

| Library | Version | Notes |
|---------|---------|-------|
| Kotlin | 2.1.20 | Pinned — higher versions break moko compatibility |
| Compose Multiplatform | 1.8.0 | |
| AGP | 8.9.3 | |
| Ktor | 3.1.2 | HTTP client + WebSocket |
| SQLDelight | 2.0.2 | Local database |
| Koin | 4.0.4 | Dependency injection |
| Voyager | 1.0.0-rc10 | Navigation + ScreenModel |
| Moko Resources | 0.24.5 | Shared resources (strings, files) |
| secp256k1-kmp | 0.19.0 | ECDSA cryptography |
| Bitcoin KMP | 0.22.1 | Bitcoin protocol |
| Lightning KMP | 1.9.1 | Lightning network |

Version catalog at `gradle/libs.versions.toml`. JVM target is 21. Android minSdk 26, targetSdk 35.

## Setup Requirements

Requires `local.properties` with: `GITHUB_ACTOR`, `GITHUB_TOKEN` (read:packages), `ALCHEMY_API_KEY`, `COVALENTHQ_API_KEY`, `INFURA_API_KEY`, `INFURA_SECRET_KEY`, `REVENUECAT_ANDROID_API_KEY`, `REVENUECAT_IOS_API_KEY`. Also needs `keystore.properties` for signing. Initialize submodules: `git submodule update --init --recursive`.

## CI/CD

GitHub Actions on self-hosted runners:
- **develop push**: Build APK → Firebase App Distribution
- **develop PR**: Build validation
- **staging push**: Production builds (Android + iOS)

Deployment ladder: develop (Firebase) → uat (Play internal) → staging (Play closed alpha) → master (Play Store).

## Detailed Standards

See `.claude/development-standards.md` for complete coding conventions, naming patterns, and PR templates.
