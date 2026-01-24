# Development Standards for Mangala Wallet

This document provides development standards and guidelines for all contributors working on Mangala Wallet, whether coding manually or using AI assistants like Claude Code.

## Git Workflow

**IMPORTANT:** All developers and AI assistants MUST follow the Git workflow rules defined in `/GIT_WORKFLOW.md`.

### Quick Reference

#### Branch Naming
```
<type>/<short-description>
```

Types: `feature/`, `bugfix/`, `hotfix/`, `refactor/`, `docs/`, `test/`, `chore/`, `perf/`

Examples:
- `feature/add-biometric-auth`
- `bugfix/fix-wallet-balance`
- `hotfix/security-patch`

#### Commit Messages
```
<type>(<scope>): <subject>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`, `ci`, `build`, `revert`

Examples:
- `feat(wallet): add multi-currency support`
- `fix(transaction): resolve parsing error`
- `docs(readme): update setup instructions`

**For complete guidelines, see [GIT_WORKFLOW.md](/GIT_WORKFLOW.md)**

## Project Structure

This is a Kotlin Multiplatform project targeting Android, iOS, and Desktop platforms.

### Module Organization

- `composeApp/` - Main Compose Multiplatform app
- `core/` - Core functionality modules
  - `core/security/` - Security and cryptography
  - `core/websocket-chat/` - WebSocket chat functionality
- `features/` - Feature modules
  - `features/auth/` - Authentication (PIN, passkey)
  - `features/wallet/` - Wallet management
  - `features/addressbook/` - Address book
  - `features/chains/` - Blockchain integrations
  - `features/portfolio/` - Portfolio management
  - `features/conversationui/` - AI conversation UI
- `data/` - Data layer (local, remote)
- `libraries/` - Shared libraries
- `iosApp/` - iOS-specific code

### Architecture Pattern

We follow **Clean Architecture** principles with **MVI (Model-View-Intent)** pattern for UI:

```
Presentation Layer (Compose UI + ViewModels)
    ↓
Domain Layer (Use Cases + Domain Models)
    ↓
Data Layer (Repositories + Data Sources)
```

## Code Style Guidelines

### Kotlin

1. **Follow official Kotlin coding conventions**
   - Use 4 spaces for indentation
   - Maximum line length: 120 characters
   - Use camelCase for functions and variables
   - Use PascalCase for classes

2. **Naming Conventions**
   - ViewModels: `*ViewModel` (e.g., `WalletViewModel`)
   - Use Cases: `*UseCase` (e.g., `GetBalanceUseCase`)
   - Repositories: `*Repository` (e.g., `WalletRepository`)
   - Data Sources: `*DataSource` (e.g., `LocalWalletDataSource`)
   - Screens: `*Screen` (e.g., `WalletMainScreen`)
   - UI State: `*State` (e.g., `WalletState`)
   - Events: `*Event` (e.g., `WalletEvent`)

3. **Prefer immutability**
   ```kotlin
   // Good
   val items = listOf(...)
   data class User(val name: String)

   // Avoid
   var items = mutableListOf(...)
   data class User(var name: String)
   ```

4. **Use sealed classes for state and events**
   ```kotlin
   sealed interface WalletState {
       data object Loading : WalletState
       data class Success(val balance: BigDecimal) : WalletState
       data class Error(val message: String) : WalletState
   }
   ```

5. **Null safety**
   - Avoid `!!` operator unless absolutely necessary
   - Prefer safe calls `?.` and Elvis operator `?:`
   - Use `require()` and `check()` for validation

### Compose UI

1. **Composable naming**
   - PascalCase for composable functions
   - Start with a noun describing the UI element
   - Examples: `WalletCard`, `TransactionList`, `BalanceHeader`

2. **State hoisting**
   - Keep composables stateless when possible
   - Hoist state to the appropriate level
   - Pass callbacks for events

3. **Preview composables**
   - Always provide `@Preview` for UI components
   - Create preview data in separate preview functions

4. **Modifiers**
   - Always pass modifier as first parameter
   - Apply modifiers in consistent order: size → padding → background → border

## Security Best Practices

1. **Never commit sensitive data**
   - API keys go in `local.properties`
   - Use `.gitignore` for sensitive files
   - Review diffs before committing

2. **Cryptography**
   - Use established libraries (don't roll your own crypto)
   - Follow security module patterns in `core/security/`
   - Always use secure random number generation

3. **Input validation**
   - Validate all user inputs
   - Sanitize data from external sources
   - Use proper error handling

4. **Dependencies**
   - Keep dependencies updated
   - Review security advisories
   - Avoid deprecated libraries

## Testing Standards

1. **Write tests for**
   - All use cases
   - Business logic in ViewModels
   - Repository implementations
   - Complex UI interactions

2. **Test naming**
   ```kotlin
   @Test
   fun `test description in backticks describing what is being tested`() {
       // Arrange
       // Act
       // Assert
   }
   ```

3. **Test structure**
   - Use Arrange-Act-Assert pattern
   - One assertion concept per test
   - Use descriptive test names

## Documentation Standards

1. **Code documentation**
   - Document public APIs with KDoc
   - Explain complex algorithms
   - Add TODO comments with issue references

2. **README files**
   - Each major module should have a README
   - Include setup instructions
   - Document module responsibilities

3. **Acceptance Criteria**
   - Store in `docs/ac/` directory
   - Follow template format
   - Link to implementation tasks

## Pull Request Guidelines

### PR Title
Follow commit message format:
```
feat(module): brief description
```

### PR Description Template
```markdown
## Description
Brief description of what this PR does

## Changes
- List of main changes
- Another change
- etc.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Manual testing performed
- [ ] Tested on Android
- [ ] Tested on iOS

## Screenshots (if applicable)
[Add screenshots for UI changes]

## Related Issues
Closes #123
```

### PR Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No console warnings or errors
- [ ] Tests pass locally
- [ ] No merge conflicts

## CI/CD and Build

1. **Before committing**
   ```bash
   # Run tests
   ./gradlew test

   # Check code style
   ./gradlew ktlintCheck

   # Build all variants
   ./gradlew build
   ```

2. **Build variants**
   - Cold: Air-gapped signing device
   - Pro: Full-featured wallet
   - UI: Transaction broadcast only

   Configure in `gradle.properties` → `currentFlavor`

3. **Platform-specific builds**
   - Android: Use Android Studio build variants
   - iOS: Select scheme in Xcode
   - Desktop: Set `desktopBuildType` in `gradle.properties`

## Common Patterns

### Error Handling
```kotlin
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
}

// Usage
when (val result = repository.getData()) {
    is Result.Success -> handleSuccess(result.data)
    is Result.Error -> handleError(result.exception)
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

### Navigation
- Use type-safe navigation
- Define routes in a sealed class
- Pass only necessary data

## Performance Guidelines

1. **Compose**
   - Use `remember` for expensive calculations
   - Use `derivedStateOf` for computed state
   - Avoid unnecessary recomposition
   - Use `key()` in lists

2. **Database**
   - Use indexes for frequently queried columns
   - Batch operations when possible
   - Use transactions for multiple writes

3. **Network**
   - Implement caching
   - Use proper timeout configurations
   - Handle offline scenarios

## AI Assistant Guidelines (Claude Code)

When working with AI assistants like Claude Code:

1. **Always reference this document** before starting work
2. **Follow all naming conventions** exactly as specified
3. **Create branches** using the proper naming format
4. **Write commits** following conventional commits
5. **Add tests** for new functionality
6. **Update documentation** when behavior changes
7. **Follow security best practices** - never expose sensitive data
8. **Use TodoWrite tool** to track multi-step tasks
9. **Provide context** from relevant docs and code before making changes

## Questions and Support

- Check project documentation in `/docs`
- Review acceptance criteria in `/docs/ac`
- Check feature-specific docs in `features/*/docs`
- Ask in team communication channels
- Open an issue for clarification

## Continuous Improvement

This document is living and should be updated as the project evolves. If you identify areas for improvement:

1. Create a branch: `docs/update-development-standards`
2. Make your changes
3. Submit a PR with clear justification
4. Get team approval before merging

---

**Remember:** These standards exist to ensure code quality, maintainability, and consistency across the project. Following them benefits everyone!
