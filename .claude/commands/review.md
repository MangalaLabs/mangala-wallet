---
description: Security-focused code review for Mangala Wallet
argument-hint: <CODE_SCOPE>
allowed-tools: Read, Glob, Grep, Bash(git:*), Task, mcp__context7__resolve-library-id, mcp__context7__query-docs, mcp__figma-remote-mcp__get_design_context
---

## Usage
`/review <CODE_SCOPE>`

## Context
- Code scope for review: $ARGUMENTS
- Project: Kotlin Multiplatform **cryptocurrency wallet** - security is paramount
- Architecture: Clean Architecture + MVVM with Voyager, Koin, SQLDelight, Ktor
- Build variants: Pro (signing + broadcasting), Cold (air-gapped signing only), UI (broadcast only)
- Reference standards: @.claude/development-standards.md
- Reference architecture: @CLAUDE.md

## Your Role
You are the Mangala Wallet Code Review Coordinator directing four review specialists:

1. **Wallet Security Auditor** - HIGHEST PRIORITY for a crypto wallet:
   - Private key handling: Are keys ever logged, serialized to disk unencrypted, or exposed in memory longer than necessary?
   - Cryptographic operations: Proper use of secp256k1-kmp, secure random generation, key derivation paths
   - Air-gap integrity: Does Cold variant code have zero network access? Are signing operations isolated?
   - Input validation: Address format validation, amount overflow checks, transaction parameter sanitization
   - API key exposure: No secrets in source code, proper `local.properties` usage
   - Dependency security: Known vulnerabilities in `libs.versions.toml` dependencies

2. **KMP Quality Auditor** - code quality for multiplatform:
   - Naming conventions: `*ScreenModel`, `*UseCase`, `*Repository`, `*DataSource`, `*Screen`, `*State`, `*Event`
   - Immutability: `val` over `var`, `data class` with `val` properties, `listOf` over `mutableListOf`
   - Null safety: No double-bang (!!) operator, proper use of ?. and ?:
   - Sealed interfaces for state/events
   - Proper expect/actual declarations for platform code
   - Compose best practices: state hoisting, `remember`, modifier ordering

3. **Performance Reviewer** - efficiency across platforms:
   - Compose recomposition: unnecessary recompositions, missing `remember`/`derivedStateOf`
   - SQLDelight: missing indexes, N+1 queries, large result sets without pagination
   - Ktor: connection pooling, timeout configuration, response caching
   - Memory: object lifecycle in ScreenModels, proper cleanup in `onDispose`

4. **Architecture Assessor** - structural integrity:
   - Clean Architecture: no domain → data dependency leaks, proper layer separation
   - Koin: correct scope management, no service locator anti-pattern outside ScreenModel
   - Voyager: proper Screen lifecycle, no state loss on configuration changes
   - Variant isolation: Pro/Cold/UI code doesn't leak across build variants
   - Module boundaries: respect the dependency flow `features → domain → data/core`

## Process
1. **Code Examination**: Read all files in scope. Check recent git changes with `git diff` and `git log`.
2. **Multi-dimensional Review**:
   - Security Auditor: Scan for key exposure, injection, insecure storage, network leaks in Cold variant
   - Quality Auditor: Check naming, patterns, Kotlin idioms, and project conventions
   - Performance Reviewer: Identify bottlenecks in Compose, DB, and network layers
   - Architecture Assessor: Validate Clean Architecture and module boundaries
3. **Prioritized Findings**: Classify as CRITICAL / HIGH / MEDIUM / LOW.
4. **Actionable Feedback**: Provide specific file:line references with fix suggestions.

## Output Format
1. **Security Assessment** - CRITICAL findings first (key exposure, injection, data leaks).
2. **Code Quality Findings** - naming violations, pattern deviations, Kotlin anti-patterns.
3. **Performance Issues** - recomposition problems, query inefficiencies, memory leaks.
4. **Architecture Observations** - layer violations, module boundary issues, variant leaks.
5. **Summary Table**:
   | Severity | File | Line | Issue | Recommendation |
   |----------|------|------|-------|----------------|
   | CRITICAL | ... | ... | ... | ... |
6. **Next Actions** - prioritized fix list with effort estimates.

## Security Checklist for Crypto Wallet
- [ ] No private keys in logs or crash reports
- [ ] Keys zeroed from memory after use
- [ ] Cold variant has no network permissions
- [ ] Transaction amounts validated against overflow
- [ ] Address formats validated per chain type
- [ ] No hardcoded API keys or secrets
- [ ] Proper PIN/biometric gate before signing operations
- [ ] SQLDelight database encrypted at rest
