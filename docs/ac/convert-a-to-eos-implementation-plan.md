# A to EOS Token Conversion - Implementation Plan

## Overview
This document outlines the step-by-step implementation plan for adding A to EOS token conversion functionality to the Mangala Wallet. The conversion rate is 1:1 between A (Vaulta native token) and EOS tokens.

## Implementation Phases

### Phase 1: Domain Layer - Conversion Use Case (1 day)

#### Step 1.1: Create Conversion Use Case
```kotlin
// Location: features/conversion/src/commonMain/kotlin/com/mangala/features/conversion/domain/
```
- [ ] Create `ConvertTokensUseCase`
  - Inject `AntelopeSendCryptoUseCase` 
  - Determine conversion contract address (A or EOS contract)
  - Format conversion memo appropriately
  - Call `sendToken()` with proper parameters:
    - For A → EOS: Send A tokens to EOS contract
    - For EOS → A: Send EOS tokens to A contract
  - Handle transaction result

#### Step 1.2: Create Conversion State Model
```kotlin
// Location: features/conversion/src/commonMain/kotlin/com/mangala/features/conversion/domain/model/
```
- [ ] Create `ConversionRequest` data class
  - fromToken: TokenType (A or EOS)
  - toToken: TokenType (A or EOS)  
  - amount: Balance
  - senderAccount: String
  - recipientAccount: String (same as sender for self-conversion)

#### Step 1.3: Token Contract Configuration
```kotlin
// Location: features/conversion/src/commonMain/kotlin/com/mangala/features/conversion/data/
```
- [ ] Create `ConversionContractConfig`
  - A_TOKEN_CONTRACT = "token.a"
  - EOS_TOKEN_CONTRACT = "eosio.token"
  - CONVERSION_MEMO_FORMAT = "convert"

### Phase 2: Dependency Injection Setup (0.5 day)

#### Step 2.1: Register Dependencies
```kotlin
// Location: core/di/src/commonMain/kotlin/com/mangala/core/di/
```
- [ ] Register `ConvertTokensUseCase` in Koin module
- [ ] Configure proper scoping for conversion feature

### Phase 3: UI Components Development (2-3 days)

#### Step 3.1: Create Reusable Components
```kotlin
// Location: core/designsystem/src/commonMain/kotlin/com/mangala/core/designsystem/components/
```
- [ ] `TokenBalanceCard`
  - Display token icon, name, and balance
  - Support for both A and EOS tokens
  - Animated balance updates

- [ ] `ConversionAmountInput`
  - Numeric keyboard integration
  - Decimal validation
  - "Max" button implementation
  - Error state display

- [ ] `TokenSwapToggle`
  - Animated swap button
  - Direction indicators (A → EOS or EOS → A)
  - Visual feedback on interaction

- [ ] `ConversionSummaryCard`
  - Display conversion details
  - Fee breakdown
  - Net amount calculation

#### Step 3.2: Create Conversion Screen
```kotlin
// Location: features/conversion/src/commonMain/kotlin/com/mangala/features/conversion/presentation/
```
- [ ] `ConversionScreen.kt`
  - Main conversion interface
  - Integration with navigation
  - Platform-specific layouts

- [ ] `ConversionViewModel.kt`
  - State management
  - Input validation
  - Conversion execution
  - Error handling

- [ ] `ConversionState.kt`
  - UI state representation
  - Loading states
  - Error states
  - Success states

#### Step 3.3: Navigation Integration
```kotlin
// Location: features/navigation/src/commonMain/kotlin/com/mangala/features/navigation/
```
- [ ] Add conversion route to navigation graph
- [ ] Create deep link support for conversion screen
- [ ] Add entry points from wallet screens

### Phase 4: Business Logic Implementation (2 days)

#### Step 4.1: Validation Logic
```kotlin
// Location: features/conversion/src/commonMain/kotlin/com/mangala/features/conversion/domain/
```
- [ ] Implement amount validation in ViewModel
  - Minimum amount: 0.0001 tokens
  - Maximum amount: Available balance - fees
  - Decimal precision handling (4 places)
  - Real-time balance verification

#### Step 4.2: Transaction Processing Integration
```kotlin
// Location: features/conversion/src/commonMain/kotlin/com/mangala/features/conversion/presentation/
```
- [ ] Wire up ConversionViewModel with ConvertTokensUseCase
  - Handle UI/Pro variant: Direct transaction execution
  - Handle Cold variant: Generate unsigned transaction for QR
  - Monitor transaction status updates
  - Handle success/error states

#### Step 4.3: Cold Wallet Support
```kotlin
// Location: features/conversion/src/commonMain/kotlin/com/mangala/features/conversion/cold/
```
- [ ] Leverage existing QR code infrastructure for unsigned transactions
- [ ] Use existing cold wallet signing flow
- [ ] Integrate with current transaction export/import patterns

### Phase 5: Integration & Platform-Specific Features (2-3 days)

#### Step 5.1: Android-Specific Implementation
```kotlin
// Location: features/conversion/src/androidMain/kotlin/
```
- [ ] Material Design 3 theming
- [ ] Biometric authentication integration
- [ ] Android-specific keyboard handling
- [ ] Landscape layout optimization

#### Step 5.2: iOS-Specific Implementation
```kotlin
// Location: features/conversion/src/iosMain/kotlin/
```
- [ ] iOS native UI elements
- [ ] Face ID/Touch ID integration
- [ ] iOS keyboard handling
- [ ] Safe area adjustments

#### Step 5.3: Desktop-Specific Implementation
```kotlin
// Location: features/conversion/src/desktopMain/kotlin/
```
- [ ] Desktop window sizing
- [ ] Keyboard shortcuts
- [ ] Mouse hover states
- [ ] Desktop-specific layouts

### Phase 7: Security & Performance (2 days)

#### Step 7.1: Security Implementation
- [ ] Input sanitization
- [ ] Rate limiting for conversion requests
- [ ] Transaction replay protection
- [ ] Secure storage of conversion history

#### Step 7.2: Performance Optimization
- [ ] Implement caching for balance queries
- [ ] Optimize database queries
- [ ] Lazy loading for conversion history
- [ ] Memory management for large transactions

### Phase 8: Documentation & Deployment (1-2 days)

#### Step 8.1: Documentation
- [ ] API documentation for conversion endpoints
- [ ] User guide for conversion feature
- [ ] Technical documentation for developers

#### Step 8.2: Deployment Preparation
- [ ] Feature flags for gradual rollout
- [ ] Migration scripts for existing users
- [ ] Monitoring and analytics setup
- [ ] A/B testing configuration

## Development Timeline

| Phase | Duration | Dependencies |
|-------|----------|--------------|
| Phase 1: Domain Layer - Use Case | 1 day | None |
| Phase 2: Dependency Injection | 0.5 day | Phase 1 |
| Phase 3: UI Components | 2-3 days | Phase 1, 2 |
| Phase 4: Business Logic | 2 days | Phase 1, 2, 3 |
| Phase 5: Platform Integration | 2-3 days | Phase 3, 4 |
| Phase 6: Testing | 2-3 days | Phase 1-5 |
| Phase 7: Security & Performance | 2 days | Phase 1-5 |
| Phase 8: Documentation | 1-2 days | Phase 1-7 |

**Total Estimated Duration: 10.5-14.5 days**

## Risk Mitigation

### Technical Risks
1. **Blockchain Integration Complexity**
   - Mitigation: Thorough testing with testnet first
   - Fallback: Manual conversion process via support

2. **Transaction Failures**
   - Mitigation: Comprehensive error handling
   - Fallback: Automatic retry mechanism

3. **Performance Issues**
   - Mitigation: Load testing and optimization
   - Fallback: Rate limiting and queuing system

### Business Risks
1. **User Adoption**
   - Mitigation: Clear UI/UX and user education
   - Fallback: In-app tutorials and tooltips

2. **Regulatory Compliance**
   - Mitigation: Legal review before launch
   - Fallback: Geographic restrictions if needed

## Success Criteria

- [ ] All acceptance criteria from requirements document met
- [ ] Unit test coverage > 80%
- [ ] Integration tests passing on all platforms
- [ ] Performance benchmarks met (< 2s load time)
- [ ] Security audit passed
- [ ] User acceptance testing completed
- [ ] Documentation complete and reviewed

## Next Steps

1. Review and approve implementation plan
2. Set up development branch `feature/token-conversion`
3. Create Jira tickets for each phase
4. Assign development team members
5. Begin Phase 1 implementation

## Notes

- The 1:1 conversion rate simplifies implementation significantly
- Consider adding analytics tracking for conversion metrics
- Plan for future extensibility to support other token pairs
- Ensure backward compatibility with existing wallet features