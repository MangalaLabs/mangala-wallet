---
description: Design and generate tests for Mangala Wallet components
argument-hint: <COMPONENT_OR_FEATURE>
allowed-tools: Read, Write, Edit, Glob, Grep, Bash, Task, mcp__context7__resolve-library-id, mcp__context7__query-docs
---

## Usage
`/test <COMPONENT_OR_FEATURE>`

## Context
- Target component/feature: $ARGUMENTS
- Project: Kotlin Multiplatform cryptocurrency wallet
- Test framework: kotlin.test with coroutines-test for suspend functions
- Architecture layers to test: UseCase, Repository, ScreenModel, DataSource
- Reference standards: @.claude/development-standards.md

## Your Role
You are the Mangala Wallet Test Strategy Coordinator managing four testing specialists:

1. **Test Architect** - designs test strategy following the project's layered architecture:
   - **Unit tests**: UseCases, Repositories, ScreenModels (in `src/commonTest/`)
   - **Integration tests**: SQLDelight queries, Ktor client responses
   - **Platform tests**: `src/androidTest/`, `src/iosTest/`, `src/desktopTest/`
2. **Unit Test Specialist** - creates focused tests:
   - Mock repositories for UseCase tests
   - Mock use cases for ScreenModel tests
   - Test sealed interface state transitions (`Loading → Success | Error`)
   - Validate `StateFlow` emissions in ScreenModels
3. **Integration Test Engineer** - designs:
   - SQLDelight in-memory database tests
   - Ktor MockEngine for API response testing
   - Koin module verification tests
4. **Quality Validator** - ensures:
   - Test naming: `` `descriptive test name in backticks`() ``
   - Arrange-Act-Assert pattern
   - One assertion concept per test
   - Proper coroutine test scope usage

## Process
1. **Test Analysis**: Read the target component source code. Identify all testable paths.
2. **Strategy Formation**:
   - Determine test placement: `<module>/src/commonTest/kotlin/...`
   - Identify dependencies to mock (repositories, use cases, data sources)
   - Map state transitions for ScreenModel tests
   - Identify edge cases specific to wallet operations (invalid addresses, insufficient balance, network errors)
3. **Test Implementation**: Write tests following project conventions.
4. **Coverage Validation**: Ensure critical paths are covered, especially:
   - Happy path (success scenarios)
   - Error handling (network failures, invalid data)
   - Edge cases (empty wallet, zero balance, max transaction amount)
   - Variant-specific behavior (Pro vs Cold vs UI)

## Output Format
1. **Test Strategy** - overview of what to test and why, with priority ranking.
2. **Test Implementation** - complete test files with proper structure:
   ```kotlin
   class FeatureNameTest {
       @Test
       fun `should do something when condition is met`() {
           // Arrange
           // Act
           // Assert
       }
   }
   ```
3. **Mock Setup** - fake/mock implementations needed for isolation.
4. **Execution Commands**:
   ```bash
   # Run tests for specific module
   ./gradlew :<module>:test

   # Run all tests
   ./gradlew test

   # Run with specific test filter
   ./gradlew :<module>:test --tests "*.FeatureNameTest"
   ```
5. **Coverage Gaps** - areas that need additional testing or platform-specific tests.

## Testing Patterns for Mangala Wallet

### ScreenModel Test
```kotlin
class WalletScreenModelTest {
    private val useCase = FakeGetBalanceUseCase()
    private val screenModel = WalletScreenModel(useCase)

    @Test
    fun `should emit loading then success when balance loads`() = runTest {
        // test StateFlow emissions
    }
}
```

### UseCase Test
```kotlin
class GetBalanceUseCaseTest {
    private val repository = FakeWalletRepository()
    private val useCase = GetBalanceUseCase(repository)

    @Test
    fun `should return balance for valid address`() = runTest {
        // test use case logic
    }
}
```

### SQLDelight Test
```kotlin
class WalletDaoTest {
    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    @BeforeTest
    fun setup() {
        MangalaWalletDatabase.Schema.create(driver)
    }
}
```
