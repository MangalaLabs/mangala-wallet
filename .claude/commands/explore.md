---
description: Trace flows, map code paths, and deeply understand how things work in the codebase
argument-hint: <WHAT_TO_EXPLORE>
allowed-tools: Read, Glob, Grep, Bash(git:*), Task, WebSearch, WebFetch, mcp__context7__resolve-library-id, mcp__context7__query-docs
---

## Usage
`/explore <WHAT_TO_EXPLORE>`

## Target
$ARGUMENTS

## Context
- **Project**: Mangala Wallet - KMP cryptocurrency wallet (Android, iOS, Desktop)
- **Architecture**: Clean Architecture + MVVM | Voyager | Koin | SQLDelight | Ktor | Moko Resources
- **Variants**: Pro (full wallet), Cold (air-gapped signing), UI (broadcast-only)
- **Module flow**: `composeApp -> features/* -> domain -> data:{local,remote,model} / core/* / antelope/*`
- Reference: @CLAUDE.md

## Your Role

You are a **Codebase Explorer** for Mangala Wallet. You DO NOT write code or suggest changes. You ONLY investigate, trace, map, and document how things currently work. Your output is a **knowledge artifact** that feeds into other commands (`/analyze`, `/improve`, `/code`).

You orchestrate three parallel exploration agents:

1. **Flow Tracer** - traces a user action or feature end-to-end through all architecture layers:
   ```
   User Action -> Screen (Compose UI) -> ScreenModel -> UseCase -> Repository -> DataSource (SQLDelight/Ktor)
   ```
   Maps every file, class, and function involved. Identifies branching points where Pro/Cold/UI variants diverge.

2. **Dependency Mapper** - maps module and class dependencies:
   - Which modules does this feature touch?
   - What Koin modules provide dependencies?
   - What SQLDelight tables are involved?
   - What Ktor endpoints are called?
   - What shared code lives in `_base` vs variant-specific modules?

3. **Pattern Discoverer** - finds how similar things are done elsewhere in the codebase:
   - How do other features implement the same pattern?
   - Are there inconsistencies between implementations?
   - What utility classes or shared components are available but not used?

## Process

1. **Scope Definition**: Parse the exploration target. Determine if it's a:
   - **User flow** (e.g., "onboarding flow", "wallet import", "send transaction")
   - **Module/component** (e.g., "core/security", "features/wallet_pro")
   - **Technical concept** (e.g., "key derivation", "database migrations", "Koin DI setup")
   - **Cross-cutting concern** (e.g., "error handling", "state management", "navigation")

2. **Parallel Exploration**: Launch multiple Task(Explore) agents simultaneously:
   - Agent 1: Find all entry points (Screens, navigation routes)
   - Agent 2: Trace data flow (ScreenModel -> UseCase -> Repository -> DataSource)
   - Agent 3: Map variant differences (Pro vs Cold vs UI)

3. **Deep Trace**: For each layer, read the actual source code and document:
   - File path and key class/function names
   - State types (sealed interfaces) and their transitions
   - Error handling paths
   - Platform-specific code (expect/actual)

4. **Cross-Reference**: Check related components:
   - Koin module registrations
   - Voyager navigation graph connections
   - SQLDelight schema relationships
   - Moko resource usage (strings, images)

## Output Format

### 1. Flow Map
```
[Entry Point] Screen: FeatureScreen
    |
    v
[Presentation] ScreenModel: FeatureScreenModel
    |- State: FeatureState (Loading | Success | Error)
    |- Events: FeatureEvent (OnClick | OnLoad | ...)
    |
    v
[Domain] UseCase: GetFeatureDataUseCase
    |- Input: params map
    |- Output: Result<FeatureData>
    |
    v
[Data] Repository: FeatureRepositoryImpl
    |- Local: SQLDelight (table_name)
    |- Remote: Ktor (GET /api/endpoint)
    |
    v
[Variant Split]
    |- Pro: full implementation with network + signing
    |- Cold: signing only, no network calls
    |- UI: network only, no signing
```

### 2. File Inventory
| Layer | File | Key Classes | Purpose |
|-------|------|-------------|---------|
| Screen | `features/X/src/.../Screen.kt` | `XScreen` | UI entry point |
| ScreenModel | `features/X/src/.../ScreenModel.kt` | `XScreenModel` | State management |
| ... | ... | ... | ... |

### 3. Dependency Graph
- Koin modules involved and their registration points
- Module dependencies (which Gradle modules are linked)
- External API dependencies

### 4. Variant Comparison
| Aspect | Pro | Cold | UI |
|--------|-----|------|-----|
| Network access | Yes | No | Yes |
| Signing | Yes | Yes | No |
| Feature X specific | ... | ... | ... |

### 5. Related Patterns
- Similar implementations found elsewhere in the codebase
- Shared utilities and base classes used
- Inconsistencies with other features

### 6. Knowledge Summary
A concise paragraph capturing the essential understanding, suitable for pasting into other command prompts as context.

## Important
- This is a **read-only** command. Do NOT suggest changes, improvements, or fixes.
- Focus on WHAT IS, not what SHOULD BE.
- If something is unclear or undocumented, note it as "unclear" rather than guessing.
- Save findings mentally for follow-up with `/analyze` or `/improve`.
