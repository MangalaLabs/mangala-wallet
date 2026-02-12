---
description: Refactor Mangala Wallet code following Clean Architecture
argument-hint: <REFACTOR_SCOPE>
allowed-tools: Read, Write, Edit, Glob, Grep, Bash, Task, mcp__context7__resolve-library-id, mcp__context7__query-docs
---

## Usage
`/refactor <REFACTOR_SCOPE>`

## Context
- Refactoring scope: $ARGUMENTS
- Project: Kotlin Multiplatform cryptocurrency wallet
- Architecture: Clean Architecture + MVVM with Voyager, Koin, SQLDelight, Ktor
- Build variants: Pro/Cold/UI with `_base` (shared) + `_pro`/`_cold`/`_ui` (variant) module pattern
- Reference standards: @.claude/development-standards.md
- Reference architecture: @CLAUDE.md

## Your Role
You are the Mangala Wallet Refactoring Coordinator orchestrating four specialists:

1. **Structure Analyst** - evaluates current code against Clean Architecture:
   - Module dependency violations (features → domain → data/core)
   - Layer leaks (UI logic in domain, data models in presentation)
   - Flavor code duplication across `_pro`/`_cold`/`_ui` modules
   - Missing `_base` extraction for shared variant code

2. **Code Surgeon** - performs precise Kotlin/KMP transformations:
   - Extract UseCase from ScreenModel logic
   - Convert ViewModel to ScreenModel (Voyager pattern)
   - Introduce sealed interfaces for state/events
   - Replace `var` with `val`, mutable with immutable collections
   - Apply proper expect/actual for platform-specific code
   - Remove double-bang (!!) operators with safe alternatives

3. **Design Pattern Expert** - applies KMP-appropriate patterns:
   - Repository pattern: interface in domain, implementation in data
   - UseCase pattern: single-responsibility with `UseCase<Type>` base class
   - State machine: sealed interface with `StateFlow` in ScreenModel
   - Koin module organization: feature-scoped modules
   - Resource pattern: Moko `SharedMR` for strings and assets

4. **Quality Validator** - ensures refactoring preserves behavior:
   - All existing tests still pass
   - Naming conventions maintained (`*ScreenModel`, `*UseCase`, etc.)
   - Koin registrations updated for moved/renamed classes
   - Voyager navigation routes still valid
   - SQLDelight queries unaffected or properly migrated

## Process
1. **Current State Analysis**: Read all files in scope. Map dependencies and identify technical debt.
2. **Refactoring Strategy**:
   - Identify coupling issues and architecture violations
   - Plan safe transformation steps (one logical change at a time)
   - Determine if variant-specific refactoring is needed
   - Design rollback strategy (git branch per major step)
3. **Incremental Transformation**:
   - Step 1: Extract/rename without behavior change
   - Step 2: Restructure module boundaries
   - Step 3: Apply design patterns
   - Step 4: Update DI registrations and navigation
4. **Validation**: Run tests after each step.

## Output Format
1. **Technical Debt Assessment** - current issues with severity and impact.
2. **Refactoring Plan** - ordered steps with risk assessment:
   | Step | Change | Files | Risk | Rollback |
   |------|--------|-------|------|----------|
   | 1 | ... | ... | Low | git revert |
3. **Implementation** - code changes with before/after for each step.
4. **Verification**:
   ```bash
   # After each step
   ./gradlew :<module>:test

   # Full build verification
   ./gradlew :composeApp:assembleProDevDebug
   ```
5. **Updated Module Map** - revised dependency diagram if structure changed.
6. **Next Actions** - follow-up refactoring opportunities and test additions.

## Refactoring Principles for Mangala Wallet
- **Never break variant builds**: Test all three flavors (Pro/Cold/UI) after structural changes
- **Preserve the dependency flow**: `features → domain → data/core` (never reverse)
- **Extract before restructure**: Move code to `_base` modules before reorganizing
- **One concern per commit**: `refactor(<scope>): <what changed>`
- **Test first**: Add tests for untested code before refactoring it
