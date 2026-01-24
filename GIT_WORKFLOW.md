# Git Workflow Standards

This document defines the branching strategy and commit message conventions for the Mangala Wallet project. All contributors must follow these rules to maintain consistency and code quality.

## Branch Structure

### Main Branches

- `master` - Production release branch (Google Play Store)
- `staging` - Closed alpha release branch (Google Play closed alpha)
- `uat` - Internal release branch (Google Play internal testing)
- `develop` - Development integration branch (Firebase App Distribution builds)

### Branch Naming Conventions

All feature branches should follow this naming pattern:

```
<type>/<short-description>
```

#### Branch Types

- `feature/` - New features or enhancements
  - Example: `feature/add-biometric-auth`
  - Example: `feature/transaction-history-filter`

- `bugfix/` - Bug fixes for develop or release branches
  - Example: `bugfix/fix-wallet-balance-calculation`
  - Example: `bugfix/resolve-crash-on-startup`

- `hotfix/` - Critical fixes for production
  - Example: `hotfix/security-patch-xss`
  - Example: `hotfix/fix-transaction-signing-error`

- `refactor/` - Code refactoring without functional changes
  - Example: `refactor/simplify-network-layer`
  - Example: `refactor/extract-common-ui-components`

- `docs/` - Documentation updates
  - Example: `docs/update-setup-guide`
  - Example: `docs/add-api-documentation`

- `test/` - Adding or updating tests
  - Example: `test/add-unit-tests-for-crypto-module`
  - Example: `test/e2e-transaction-flow`

- `chore/` - Build process, dependencies, or tooling updates
  - Example: `chore/upgrade-kotlin-1.9.22`
  - Example: `chore/update-gradle-dependencies`

- `perf/` - Performance improvements
  - Example: `perf/optimize-database-queries`
  - Example: `perf/reduce-app-startup-time`

#### Branch Naming Rules

1. Use lowercase letters
2. Use hyphens to separate words (kebab-case)
3. Keep it short but descriptive (max 50 characters)
4. No special characters except hyphens
5. Start with the appropriate type prefix

**Good Examples:**
- `feature/add-passkey-support`
- `bugfix/fix-ram-calculation`
- `hotfix/patch-security-vulnerability`

**Bad Examples:**
- `feature/Add_Passkey_Support` (wrong case, underscores)
- `myFeature` (no type prefix)
- `fix-bug` (too vague)
- `feature/add-a-new-feature-that-allows-users-to-do-something-really-cool` (too long)

## Commit Message Conventions

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification.

### Commit Message Format

```
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

#### Types

- `feat` - A new feature
- `fix` - A bug fix
- `docs` - Documentation only changes
- `style` - Code style changes (formatting, missing semicolons, etc.)
- `refactor` - Code refactoring without adding features or fixing bugs
- `perf` - Performance improvements
- `test` - Adding or updating tests
- `chore` - Build process, dependencies, or tooling updates
- `ci` - CI/CD configuration changes
- `build` - Build system or external dependencies changes
- `revert` - Revert a previous commit

#### Scope (Optional but Recommended)

The scope should be the name of the module, feature, or component affected:

Examples:
- `auth` - Authentication module
- `wallet` - Wallet functionality
- `ui` - User interface
- `core` - Core functionality
- `antelope` - Antelope chain integration
- `passkey` - Passkey feature
- `portfolio` - Portfolio screen
- `transaction` - Transaction handling
- `network` - Network layer
- `database` - Database operations

#### Subject

- Use imperative mood: "add" not "added" or "adds"
- Don't capitalize first letter
- No period at the end
- Maximum 72 characters
- Be concise but descriptive

#### Body (Optional)

- Use imperative mood
- Explain what and why, not how
- Wrap at 72 characters
- Separate from subject with a blank line

#### Footer (Optional)

- Reference issues: `Closes #123` or `Fixes #456`
- Breaking changes: `BREAKING CHANGE: description`

### Commit Message Examples

**Simple feature:**
```
feat(wallet): add balance refresh button

Users can now manually refresh their wallet balance by tapping the refresh icon.
```

**Bug fix:**
```
fix(transaction): resolve crash when parsing invalid transaction data

Added null safety checks and error handling for malformed transaction responses.

Closes #234
```

**Breaking change:**
```
feat(auth)!: migrate to passkey-only authentication

BREAKING CHANGE: PIN authentication has been removed. Users must re-authenticate using passkeys.

Migration guide: docs/auth/passkey-migration.md
```

**Refactoring:**
```
refactor(network): extract HTTP client configuration to separate module

Improves code reusability and makes network layer easier to test.
```

**Documentation:**
```
docs(readme): update build instructions for iOS

Added steps for configuring Xcode schemes and resolving common build errors.
```

**Chore:**
```
chore(deps): upgrade Kotlin to 1.9.22

Updated all Kotlin-related dependencies to support latest language features.
```

**Multiple changes (discouraged - prefer atomic commits):**
```
feat(portfolio): add transaction filtering and export

- Implemented date range filter for transaction history
- Added CSV export functionality
- Updated UI with filter controls

Closes #123, #124
```

## Workflow Process

### 1. Creating a New Branch

Always branch from `develop` (unless it's a hotfix):

```bash
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name
```

For hotfixes, branch from `master`:

```bash
git checkout master
git pull origin master
git checkout -b hotfix/critical-fix
```

### 2. Making Commits

- Make small, atomic commits
- Each commit should represent a single logical change
- Write clear, descriptive commit messages following the conventions above

```bash
git add .
git commit -m "feat(wallet): add multi-currency support"
```

### 3. Keeping Your Branch Updated

Regularly sync with develop:

```bash
git checkout develop
git pull origin develop
git checkout feature/your-feature-name
git rebase develop
```

### 4. Creating a Pull Request

1. Push your branch to remote:
   ```bash
   git push origin feature/your-feature-name
   ```

2. Create a Pull Request targeting `develop`

3. PR title should follow commit message format:
   ```
   feat(module): brief description of changes
   ```

4. PR description should include:
   - What changes were made
   - Why the changes were necessary
   - Any breaking changes
   - Testing performed
   - Screenshots (for UI changes)
   - Related issues

### 5. Merging Strategy

- Feature branches → `develop`: Squash and merge (preferred) or Merge commit
- `develop` → `uat`: Merge commit (preserve history)
- `uat` → `staging`: Merge commit (preserve history)
- `staging` → `master`: Merge commit (preserve history)
- Hotfix branches → `master`: Merge commit, then cherry-pick to `develop`

## Best Practices

1. **Never commit directly to main branches** (`master`, `staging`, `uat`, `develop`)
2. **Keep branches short-lived** - merge within a week if possible
3. **Delete merged branches** to keep the repository clean
4. **Review your own PR first** before requesting reviews
5. **Write tests** for new features and bug fixes
6. **Update documentation** when changing behavior
7. **One feature per branch** - don't mix unrelated changes
8. **Rebase instead of merge** when updating feature branches
9. **Use meaningful branch and commit names** for better searchability
10. **Link commits to issues** when applicable

## Git Commit Template

You can set up a commit template to help follow these conventions:

```bash
git config commit.template .gitmessage
```

Create `.gitmessage` file in project root:

```
# <type>(<scope>): <subject>
# |<----  Using a Maximum Of 72 Characters  ---->|

# [optional body]
# Explain what and why (not how). Wrap at 72 characters.

# [optional footer]
# Reference issues (e.g., "Closes #123" or "Fixes #456")
# Note breaking changes (e.g., "BREAKING CHANGE: description")

# Types: feat, fix, docs, style, refactor, perf, test, chore, ci, build, revert
# Scopes: auth, wallet, ui, core, antelope, passkey, portfolio, transaction, network, database
```

## Enforcement

- Pre-commit hooks validate commit message format
- CI/CD pipelines check branch naming conventions
- Pull requests must pass all checks before merging
- Reviewers should enforce these standards during code review

## Questions?

If you have questions about these conventions, please:
1. Check the examples in this document
2. Review recent commits for reference
3. Ask in the team chat
4. Open an issue for clarification

---

**Remember:** Consistent Git practices make collaboration easier, history more readable, and debugging more efficient. Thank you for following these standards!
