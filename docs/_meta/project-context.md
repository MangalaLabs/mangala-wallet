# Mangala Wallet - Documentation Project Context

> This file tracks the current state of documentation work. Update after each session to preserve context.

## Last Updated
- **Date**: 2025-02-01
- **Session**: Initial setup

## Project Overview

**Mangala Wallet** is a Kotlin Multiplatform cryptocurrency wallet supporting:
- **Platforms**: Android, iOS, Desktop
- **Chains**: Antelope (EOS, Telos, WAX), EVM (Ethereum, Polygon, BSC), Bitcoin
- **Build Variants**: Cold (air-gapped), UI (broadcast only), Pro (full-featured)

## Current Documentation Status

### Existing Docs (Before This Project)
| File | Status | Notes |
|------|--------|-------|
| README.md | ✅ Exists | Setup, build variants, common issues |
| GIT_WORKFLOW.md | ✅ Exists | Git branching and commit conventions |
| CONTRIBUTING.md | ✅ Exists | Contribution guidelines |
| .claude/development-standards.md | ✅ Exists | Code style, architecture |
| docs/ac/ | ✅ Exists | Acceptance criteria |

### New Documentation Plan
| Section | Status | Priority | Notes |
|---------|--------|----------|-------|
| docs/getting-started/introduction.md | ⏳ Pending | P0 | |
| docs/getting-started/quick-start.md | ⏳ Pending | P0 | |
| docs/architecture/overview.md | ⏳ Pending | P0 | |
| docs/architecture/module-structure.md | ⏳ Pending | P1 | |
| docs/architecture/build-variants.md | ⏳ Pending | P1 | |
| docs/features/wallet-management.md | ⏳ Pending | P1 | |
| docs/features/transaction-flow.md | ⏳ Pending | P1 | |
| docs/features/chain-integrations/ | ⏳ Pending | P2 | |

## Architecture Summary (Quick Reference)

```
mangala-wallet/
├── composeApp/           # Main Compose Multiplatform app
│   └── src/
│       ├── commonMain/   # Shared code
│       ├── androidMain/  # Android-specific
│       ├── iosMain/      # iOS-specific
│       └── jvmMain/      # Desktop code
├── core/                 # Core modules (wallet, auth, security, crypto)
├── features/             # Feature modules (organized by variant: *_cold, *_ui, *_pro)
├── data/                 # Data layer (local DB, remote API)
├── domain/               # Domain layer (use cases)
├── antelope/             # Antelope/EOS blockchain framework
├── common/               # Shared utilities and UI components
├── libraries/            # Custom libraries (chart, QR, WalletConnect)
└── iosApp/               # iOS app wrapper
```

## Key Architecture Decisions

1. **Clean Architecture**: Presentation → Domain → Data layers
2. **MVVM Pattern**: ScreenModels + Compose UI
3. **Dependency Injection**: Koin
4. **Navigation**: Voyager (type-safe)
5. **State Management**: Sealed interfaces for State/Events
6. **Database**: SQLDelight (multiplatform)
7. **Networking**: Ktor Client

## Tech Stack Quick Reference

| Category | Technology |
|----------|------------|
| Language | Kotlin 2.1.20 |
| UI | Compose Multiplatform 1.8.0 |
| DI | Koin 4.0.4 |
| Navigation | Voyager 1.0.0-rc10 |
| Database | SQLDelight 2.0.2 |
| Network | Ktor 3.1.2 |
| Crypto | Bitcoin-KMP, SECP256K1, Trust Wallet Core |

## Recent Code Changes (For Doc Sync)

Track recent significant code changes that may affect documentation:

| Date | Change | Affects Docs |
|------|--------|--------------|
| 2025-01 | Fixed seedphrase retrieval with multiple wallets (#11) | wallet-management.md |
| 2025-01 | Fixed EVM wallet creation infinite loop (#9) | getting-started, wallet-management |

## Open Questions

Questions to clarify with stakeholders:

1. [ ] Target audience priority: Internal team vs Open source contributors?
2. [ ] Should docs be bilingual (EN/VI)?
3. [ ] API reference: Auto-generate with Dokka or manual?

## Next Session TODOs

- [ ] Write `docs/getting-started/introduction.md`
- [ ] Write `docs/architecture/overview.md`
- [ ] Create architecture diagrams (Mermaid)
- [ ] Review and consolidate existing README content

## Session History

| Date | What Was Done |
|------|---------------|
| 2025-02-01 | Initial setup: Created docs structure, skill, templates |

---

**Instructions**: Update this file at the end of each documentation session to preserve context for the next session.
