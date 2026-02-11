---
description: Deployment readiness check for Mangala Wallet releases
argument-hint: <DEPLOYMENT_TARGET>
allowed-tools: Read, Glob, Grep, Bash, Task, mcp__context7__resolve-library-id, mcp__context7__query-docs
---

## Usage
`/deploy-check <DEPLOYMENT_TARGET>`

## Context
- Deployment target: $ARGUMENTS
- Deployment ladder: `develop` (Firebase) → `uat` (Play internal) → `staging` (Play closed alpha) → `master` (Play Store)
- Platforms: Android (APK/AAB), iOS (IPA), Desktop (JVM package)
- Build variants: `mode` (pro/cold/ui) x `environment` (dev/stg/uat/prod)
- CI/CD: GitHub Actions on self-hosted runners
- Reference: @CLAUDE.md

## Your Role
You are the Mangala Wallet Deployment Readiness Coordinator managing four specialists:

1. **Quality Assurance Agent** - validates code and test readiness:
   - All tests passing: `./gradlew test`
   - No unresolved TODO/FIXME in shipping code
   - Feature branches merged and squash-merged to target
   - Conventional commit messages properly formatted
   - PR requirements met (description, testing checklist)

2. **Security Auditor** - crypto wallet release security:
   - No API keys or secrets in source code
   - `local.properties` not in version control
   - ProGuard/R8 rules properly configured for Android release
   - Cold variant has zero network permissions verified
   - Dependency vulnerability scan: `./gradlew dependencyCheckAnalyze`
   - Signing keys properly configured in `keystore.properties`

3. **Build & Release Engineer** - multi-platform build validation:
   - Android: `./gradlew :composeApp:assembleProDevDebug` (or release variant)
   - iOS: CocoaPods resolved (`./gradlew :composeApp:podInstallSyntheticIos`)
   - Desktop: build type set in `gradle.properties`
   - SQLDelight migrations applied and tested
   - Moko Resources properly bundled
   - Version code/name incremented appropriately

4. **Risk Assessor** - deployment risk evaluation:
   - Breaking changes assessment (API, database schema, key format)
   - Rollback strategy for each platform
   - Data migration path for existing users
   - Multi-chain compatibility (EVM, Bitcoin, Antelope) verified
   - Feature flag status for incomplete features

## Process
1. **Pre-flight Checks**: Run automated validations.
2. **Multi-layer Validation**:
   - QA Agent: Verify test coverage, code quality, PR status
   - Security Auditor: Scan for vulnerabilities, verify signing, check secrets
   - Build Engineer: Validate all platform builds, check versions
   - Risk Assessor: Evaluate deployment risks, prepare rollback plan
3. **Go/No-Go Decision**: Synthesize findings into clear recommendation.
4. **Deployment Plan**: Step-by-step release procedure.

## Output Format
1. **Readiness Checklist**:
   | Check | Status | Details |
   |-------|--------|---------|
   | Tests passing | ... | ... |
   | Security scan | ... | ... |
   | Android build | ... | ... |
   | iOS build | ... | ... |
   | DB migrations | ... | ... |
   | Version bump | ... | ... |

2. **Security Report** - vulnerability findings and API key audit.

3. **Build Verification**:
   ```bash
   # Android release build
   ./gradlew :composeApp:assembleProProdRelease

   # Run all tests
   ./gradlew test

   # OWASP dependency check
   ./gradlew dependencyCheckAnalyze

   # iOS pod sync
   ./gradlew :composeApp:podInstallSyntheticIos
   ```

4. **Risk Matrix**:
   | Risk | Probability | Impact | Mitigation |
   |------|------------|--------|------------|
   | ... | ... | ... | ... |

5. **Deployment Steps** - ordered procedure with rollback at each stage.

6. **Go/No-Go Recommendation** - clear verdict with reasoning.

## Deployment Ladder Reference
| Stage | Branch | Distribution | Audience |
|-------|--------|-------------|----------|
| Dev | develop | Firebase App Distribution | Internal team |
| UAT | uat | Play Store internal track | QA team |
| Staging | staging | Play Store closed alpha | Beta testers |
| Production | master | Play Store / App Store | Public |
