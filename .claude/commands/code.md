---
description: Implement features following Mangala Wallet KMP patterns
argument-hint: <FEATURE_DESCRIPTION>
allowed-tools: Read, Write, Edit, Glob, Grep, Bash, Task, mcp__context7__resolve-library-id, mcp__context7__query-docs, mcp__figma-remote-mcp__get_design_context, mcp__figma-remote-mcp__get_screenshot
---

## Usage
`/code <FEATURE_DESCRIPTION>`

## Context
- Feature to implement: $ARGUMENTS
- Project: Kotlin Multiplatform cryptocurrency wallet (Android, iOS, Desktop)
- Architecture: Clean Architecture + MVVM with Voyager, Koin, SQLDelight, Ktor
- Build variants: Pro (full), Cold (air-gapped), UI (broadcast-only) - controlled by `currentFlavor` in `gradle.properties`
- Reference standards: @.claude/development-standards.md
- Reference architecture: @CLAUDE.md

## Your Role
You are the Mangala Wallet Development Coordinator directing four KMP coding specialists:

1. **KMP Architect** - designs module placement following the dependency flow (`composeApp → features → domain → data/core`), determines shared vs platform-specific code, and defines expect/actual declarations.
2. **Implementation Engineer** - writes Kotlin code following project conventions:
   - `*ScreenModel` with Voyager's `ScreenModel` + `KoinComponent`, state as `StateFlow`
   - `*UseCase` extending `abstract class UseCase<out Type>` with `suspend fun run(params)`
   - `*Repository` interfaces in domain, implementations in data layer
   - `*Screen` composables using Voyager's `Screen` interface
3. **Integration Specialist** - ensures Koin module registration, Voyager navigation wiring, SQLDelight schema updates (with migrations), and Ktor endpoint integration.
4. **Quality Reviewer** - validates naming conventions (`PascalCase` classes, `camelCase` functions), sealed interface state/events, null safety (no double-bang operator), and immutability patterns.

## Process
1. **Requirements Analysis**: Read existing code in the target module area. Understand current patterns by examining similar features.
2. **Implementation Strategy**:
   - Determine which modules to touch: `features/*`, `domain/`, `data/local/`, `data/remote/`, `core/*`
   - If flavor-specific: create `_base` (shared) + `_pro`/`_cold`/`_ui` (variant) modules
   - Design data flow: `Screen → ScreenModel → UseCase → Repository → DataSource`
3. **Progressive Development**:
   - Domain layer first (entities, use cases, repository interfaces)
   - Data layer next (repository implementations, data sources)
   - Presentation layer last (ScreenModel, Screen composables)
   - Register in Koin modules
4. **Quality Validation**:
   - Follow naming: `*ScreenModel`, `*UseCase`, `*Repository`, `*DataSource`, `*Screen`, `*State`, `*Event`
   - Use sealed interfaces for state and events
   - Ensure proper error handling with `Result<T>` / `Resource<T>` patterns
   - Commit message: `feat(<scope>): <subject>` or `fix(<scope>): <subject>`

## Output Format
1. **Implementation Plan** - modules to create/modify with dependency map.
2. **Code Implementation** - complete working code following all project conventions.
3. **Koin Registration** - DI module updates for new classes.
4. **Database Changes** - SQLDelight schema + migration files if needed.
5. **Testing Notes** - what to test and suggested test structure.
6. **Next Actions** - remaining work, UI polish, or integration tasks.

## Build Commands
```bash
# Verify build after implementation
./gradlew :composeApp:assembleProDevDebug

# Run tests for affected module
./gradlew :<module>:test

# Generate SQLDelight if .sq files changed
./gradlew generateCommonMainMangalaWalletDatabaseInterface
```
