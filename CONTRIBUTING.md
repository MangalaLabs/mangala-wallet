# Contributing to Mangala Wallet

Thank you for your interest in contributing to Mangala Wallet! This document provides guidelines for contributing to this open source project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Submitting Changes](#submitting-changes)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Features](#suggesting-features)

## Code of Conduct

This project follows standard open source community guidelines:

- Be respectful and inclusive
- Welcome newcomers and help them get started
- Focus on constructive feedback
- Respect different viewpoints and experiences
- Accept responsibility and apologize for mistakes

## Getting Started

### Prerequisites

Before contributing, ensure you have:

1. Read the [README.md](README.md) for build setup
2. Reviewed [GIT_WORKFLOW.md](GIT_WORKFLOW.md) for Git conventions
3. Familiarized yourself with [Development Standards](.claude/development-standards.md)

### Setting Up Your Development Environment

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/mangala-wallet.git
   cd mangala-wallet
   ```
3. Initialize submodules:
   ```bash
   git submodule update --init --recursive
   ```
4. Set up environment variables in `local.properties` (see README.md)
5. Configure Git commit template:
   ```bash
   git config commit.template .gitmessage
   ```
6. Add upstream remote:
   ```bash
   git remote add upstream https://github.com/MangalaLabs/mangala-wallet.git
   ```

## Development Workflow

### 1. Choose What to Work On

- Check open issues labeled `good first issue` or `help wanted`
- Comment on the issue to claim it
- Wait for maintainer approval before starting work
- For significant changes, open a discussion issue first

### 2. Create a Branch

Always branch from `develop`:

```bash
git checkout develop
git pull upstream develop
git checkout -b feature/your-feature-name
```

Follow branch naming conventions in [GIT_WORKFLOW.md](GIT_WORKFLOW.md):

- `feature/` - New features
- `bugfix/` - Bug fixes
- `refactor/` - Code refactoring
- `docs/` - Documentation updates
- `test/` - Test additions/updates
- `chore/` - Build/dependency updates

### 3. Make Your Changes

Follow our [Development Standards](.claude/development-standards.md):

- Write clean, readable code
- Follow Kotlin coding conventions
- Add comments for complex logic
- Include KDoc for public APIs
- Follow existing code patterns

### 4. Update Documentation

- Update relevant README files
- Add/update KDoc comments
- Update user-facing documentation
- Add inline comments for complex code

### 5. Commit Your Changes

Follow conventional commit format:

```bash
git add .
git commit -m "feat(module): brief description of changes"
```

See [GIT_WORKFLOW.md](GIT_WORKFLOW.md) for detailed commit message guidelines.

### 6. Keep Your Branch Updated

Regularly sync with upstream develop:

```bash
git checkout develop
git pull upstream develop
git checkout feature/your-feature-name
git rebase develop
```

### 7. Push Your Changes

```bash
git push origin feature/your-feature-name
```

## Submitting Changes

### Creating a Pull Request

1. Go to your fork on GitHub
2. Click "New Pull Request"
3. Select `develop` as the base branch
4. Fill out the PR template completely
5. Link related issues using keywords (e.g., "Closes #123")
6. Add screenshots for UI changes
7. Ensure all CI checks pass

### Pull Request Checklist

Before submitting, ensure:

- [ ] Code follows project style guidelines
- [ ] All tests pass locally
- [ ] New tests added for new features/fixes
- [ ] Documentation updated
- [ ] Commit messages follow conventions
- [ ] No merge conflicts with develop
- [ ] Self-reviewed the code
- [ ] No console warnings or errors
- [ ] Tested on target platforms (Android/iOS/Desktop)

### PR Review Process

1. Maintainers will review your PR
2. Address review feedback by pushing new commits
3. Once approved, maintainers will merge your PR
4. Your branch will be deleted after merge

## Coding Standards

### Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use 4 spaces for indentation
- Maximum line length: 120 characters
- See [Development Standards](.claude/development-standards.md) for details

### Architecture

- Follow Clean Architecture principles
- Use MVI pattern for UI layer
- Separate concerns properly (UI, domain, data)
- Keep composables stateless when possible

### Security

- Never commit API keys or secrets
- Use `local.properties` for sensitive config
- Validate all user inputs
- Follow security best practices in `core/security/`

### Performance

- Avoid unnecessary recomposition
- Use `remember` and `derivedStateOf` appropriately
- Optimize database queries
- Implement proper caching

## Reporting Bugs

### Before Reporting

- Check if the bug has already been reported
- Verify it's not a configuration issue
- Test on the latest develop branch

### Bug Report Template

```markdown
**Describe the bug**
A clear description of the bug

**To Reproduce**
Steps to reproduce:
1. Go to '...'
2. Click on '...'
3. See error

**Expected behavior**
What you expected to happen

**Screenshots**
Add screenshots if applicable

**Environment:**
- Platform: [Android/iOS/Desktop]
- OS Version: [e.g. Android 13, iOS 16]
- App Version: [e.g. 1.0.0]
- Device: [e.g. Pixel 7, iPhone 14]

**Additional context**
Any other relevant information
```

## Suggesting Features

### Before Suggesting

- Check if the feature has been suggested before
- Consider if it aligns with project goals
- Think about how it would benefit users

### Feature Request Template

```markdown
**Feature Description**
Clear description of the proposed feature

**Problem it Solves**
What problem does this feature solve?

**Proposed Solution**
How would this feature work?

**Alternatives Considered**
What alternatives have you considered?

**Additional Context**
Mockups, examples, or other context
```

## Questions?

- Open a [Discussion](https://github.com/MangalaLabs/mangala-wallet/discussions) for questions
- Check existing documentation in `/docs`
- Reach out in project communication channels

## Recognition

Contributors are recognized in:
- GitHub contributors page
- Release notes for significant contributions
- Project README (for major features)

Thank you for contributing to Mangala Wallet!
