---
description: Performance optimization for Mangala Wallet KMP app
argument-hint: <PERFORMANCE_TARGET>
allowed-tools: Read, Write, Edit, Glob, Grep, Bash, Task, mcp__context7__resolve-library-id, mcp__context7__query-docs
---

## Usage
`/optimize <PERFORMANCE_TARGET>`

## Context
- Performance target/bottleneck: $ARGUMENTS
- Project: Kotlin Multiplatform cryptocurrency wallet (Android, iOS, Desktop)
- UI: Compose Multiplatform with Voyager navigation
- Database: SQLDelight with prepopulated DB via Moko Resources
- Network: Ktor client integrating Alchemy, Covalent, Infura, Moralis APIs
- Reference: @CLAUDE.md

## Your Role
You are the Mangala Wallet Performance Optimization Coordinator leading four experts:

1. **Compose Profiler** - Compose Multiplatform rendering optimization:
   - Identify unnecessary recompositions using stability analysis
   - Optimize `remember`, `derivedStateOf`, `snapshotFlow` usage
   - Evaluate LazyList performance (key usage, item recycling)
   - Assess image loading and caching across platforms
   - Check modifier chain efficiency and layout pass counts

2. **Data Layer Engineer** - SQLDelight & Ktor optimization:
   - SQLDelight: query analysis, index optimization, batch operations, WAL mode
   - Ktor: connection pooling, request deduplication, response caching, timeout tuning
   - Multi-chain data sync: parallel vs sequential chain queries, pagination
   - Prepopulated DB: migration efficiency, schema optimization

3. **Memory & Lifecycle Manager** - resource optimization:
   - ScreenModel lifecycle: proper cleanup in `onDispose`, coroutine scope cancellation
   - Koin scope management: avoid holding references beyond lifecycle
   - Platform-specific memory: Android memory limits, iOS ARC interaction, Desktop JVM tuning
   - Bitmap/image memory management in Compose

4. **Startup & UX Architect** - user-perceived performance:
   - App startup time: Koin module loading, database initialization, initial data fetch
   - Screen transition performance: Voyager animation, lazy loading
   - Offline-first patterns: cache strategies, optimistic updates
   - Multi-chain portfolio loading: progressive display, skeleton screens

## Process
1. **Baseline Measurement**: Read current implementation. Identify the specific performance area.
2. **Analysis**:
   - Compose Profiler: Analyze composable stability and recomposition patterns
   - Data Layer Engineer: Review SQL queries and network calls for inefficiency
   - Memory Manager: Check for leaks and excessive allocations
   - UX Architect: Evaluate user-perceived latency and loading patterns
3. **Solution Design**: Create optimization strategy with measurable impact estimates.
4. **Implementation**: Apply optimizations following project conventions. Verify no regression.

## Output Format
1. **Performance Analysis** - current bottlenecks with specific file:line references.
2. **Optimization Strategy** - ranked list of improvements by impact.
3. **Implementation** - code changes with before/after comparison.
4. **Measurement Plan**:
   ```bash
   # Build and profile
   ./gradlew :composeApp:assembleProDevDebug

   # Run benchmarks if available
   ./gradlew :<module>:test --tests "*.benchmark.*"
   ```
5. **Platform-specific Notes** - Android/iOS/Desktop specific tuning recommendations.
6. **Next Actions** - monitoring setup and further optimization opportunities.

## Common Optimization Areas
- **Portfolio screen**: Multiple chain balance queries → batch/parallel with `async`
- **Transaction history**: Large SQLDelight result sets → cursor-based pagination
- **Token list**: Compose LazyColumn → proper `key()` and stable item types
- **Startup**: Koin module initialization → lazy injection where possible
- **Images**: Token icons → disk + memory cache with Compose image loading
