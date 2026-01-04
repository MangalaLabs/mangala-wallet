# Privacy Mode Implementation Plan

## 1. Executive Summary - REVISED

### Objective  
**Tận dụng hệ thống privacy hoàn chỉnh đã có** để implement Privacy Mode toggle và các enhancement cho Address Book.

### Phát hiện quan trọng
✅ **Database schema đã hoàn chỉnh** - contacts, wallet_addresses, user_settings, security_audit_logs
✅ **Privacy system đã implement** - security levels, auth requirements, encrypted data
✅ **Infrastructure đã sẵn sàng** - SecureActionHandler, authentication flows

### Timeline Giảm
- **Total Duration**: 2 tuần (1 sprint)
- **Scope**: UI enhancements + gesture controls + location features

### Resources
- 1 Senior KMP Developer (2 tuần)
- 1 UI/UX Designer (1 tuần)

## 2. Technical Architecture

### 2.1 Module Structure
```
features/addressbook/
├── src/
│   ├── commonMain/
│   │   ├── kotlin/com/mangala/wallet/addressbook/
│   │   │   ├── domain/
│   │   │   │   ├── models/
│   │   │   │   │   ├── PrivacyModeState.kt
│   │   │   │   │   ├── SafeZone.kt
│   │   │   │   │   └── PrivacyLog.kt
│   │   │   │   ├── usecases/
│   │   │   │   │   ├── PrivacyModeUseCase.kt
│   │   │   │   │   ├── ContactPrivacyUseCase.kt
│   │   │   │   │   └── SafeZoneUseCase.kt
│   │   │   │   └── repository/
│   │   │   │       └── PrivacyModeRepository.kt
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── PrivacyModeLocalDataSource.kt
│   │   │   │   │   └── dao/
│   │   │   │   │       └── PrivacyModeDao.kt
│   │   │   │   └── repository/
│   │   │   │       └── PrivacyModeRepositoryImpl.kt
│   │   │   └── presentation/
│   │   │       ├── privacy/
│   │   │       │   ├── PrivacyModeScreenModel.kt
│   │   │       │   ├── PrivacyModeSettingsScreen.kt
│   │   │       │   └── components/
│   │   │       │       ├── PrivacyModeToggle.kt
│   │   │       │       ├── ObfuscatedAddress.kt
│   │   │       │       └── SafeZonesList.kt
│   │   │       └── shared/
│   │   │           └── PrivacyModeProvider.kt
│   │   └── sqldelight/
│   │       └── migration/
│   │           └── 4.sqm (privacy mode tables)
│   ├── androidMain/
│   │   └── kotlin/com/mangala/wallet/addressbook/
│   │       ├── privacy/
│   │       │   ├── LocationService.kt
│   │       │   ├── WifiMonitorService.kt
│   │       │   └── ScreenshotBlocker.kt
│   │       └── di/
│   │           └── PrivacyModulePlatform.android.kt
│   └── iosMain/
│       └── kotlin/com/mangala/wallet/addressbook/
│           ├── privacy/
│           │   ├── LocationService.kt
│           │   ├── WifiMonitorService.kt
│           │   └── ScreenshotBlocker.kt
│           └── di/
│               └── PrivacyModulePlatform.ios.kt
```

### 2.2 Dependencies Update

```kotlin
// features/addressbook/build.gradle.kts
dependencies {
    // Existing dependencies...
    
    // New dependencies for Privacy Mode
    commonMainImplementation(libs.kotlinx.datetime)
    androidMainImplementation(libs.play.services.location)
    androidMainImplementation(libs.accompanist.permissions)
    iosMainImplementation(libs.ktor.client.darwin)
}
```

## 3. Sprint Planning

### Sprint 1: Privacy Mode UI Enhancement (Tuần 1-2)

#### Week 1: Tận dụng hệ thống hiện có
**Day 1-2: Research & Integration**
- [ ] ✅ **KHÔNG CẦN** Database migration - đã có sẵn!
- [ ] ✅ **KHÔNG CẦN** Domain models - sử dụng hiện có!
- [ ] Research existing SecureActionHandler usage
- [ ] Map existing privacy_display_mode với requirements
- [ ] Understand current authentication flows

**Day 3-4: UI Components**
- [ ] Create PrivacyModeToggle using existing privacy_mode_enabled
- [ ] Enhance ObfuscatedAddress using privacy_display_mode
- [ ] Integrate với existing auth_requirement system
- [ ] Update ContactListScreen với privacy toggle

**Day 5: Testing**
- [ ] Test integration với existing privacy system
- [ ] Verify obfuscation logic
- [ ] Test authentication flows

#### Week 2: Enhanced Features
**Day 6-7: Gesture Controls**
- [ ] Implement double-tap quick toggle
- [ ] Add haptic feedback và animations
- [ ] Swipe gestures cho bulk privacy actions
- [ ] Visual feedback (eye icon animations)

**Day 8-9: Advanced Features** 
- [ ] Location-based auto-activation (optional)
- [ ] Screenshot protection integration
- [ ] Clipboard protection cho sensitive addresses
- [ ] Settings screen integration

**Day 10: Polish & Release**
- [ ] End-to-end testing
- [ ] Performance optimization
- [ ] Documentation updates
- [ ] Release preparation

### Optional Future Enhancements (Nếu cần)

#### Advanced Location Features
- [ ] Location-based auto-activation (sử dụng safe_zones JSON)
- [ ] WiFi-based safe zone detection
- [ ] Background monitoring services

#### Premium Security Features  
- [ ] Advanced screenshot protection
- [ ] Clipboard protection enhancements
- [ ] Session timeout controls

#### Analytics & Monitoring
- [ ] Privacy usage analytics (sử dụng security_audit_logs)
- [ ] Performance monitoring
- [ ] User behavior insights

## 4. Technical Implementation Details

### 4.1 Database Schema

```sql
-- DATABASE SCHEMA ĐÃ CÓ SẴN HOÀN CHỈNH!

-- contacts table đã có:
-- is_sensitive INTEGER AS Boolean DEFAULT 0
-- security_level TEXT DEFAULT 'NORMAL' -- NORMAL, HIGH, MAXIMUM
-- privacy_display_mode TEXT DEFAULT 'FULL' -- FULL, HIDDEN, SECRET  
-- auth_requirement TEXT DEFAULT 'NONE' -- NONE, BIOMETRIC, BIOMETRIC_PIN
-- encrypted_data TEXT

-- wallet_addresses table đã có:
-- is_sensitive INTEGER AS Boolean DEFAULT 0

-- user_settings table đã có:
-- privacy_mode_enabled INTEGER AS Boolean DEFAULT 0
-- default_privacy_display TEXT DEFAULT 'PARTIAL' -- FULL, PARTIAL, HIDDEN
-- biometric_auth_enabled INTEGER AS Boolean DEFAULT 0
-- two_factor_auth_enabled INTEGER AS Boolean DEFAULT 0
-- safe_zones TEXT (JSON storage cho safe zones)

-- security_audit_logs table đã có sẵn cho audit trail

-- KHÔNG CẦN MIGRATION - CHỈ CẦN SỬ DỤNG HỆ THỐNG HIỆN CÓ!

-- TẤT CẢ ĐÃ CÓ SẴN!
-- Safe zones: Sử dụng user_settings.safe_zones (TEXT JSON)
-- Privacy logs: Sử dụng security_audit_logs table hiện có
-- Contact sensitivity: contacts.is_sensitive đã có
-- Address sensitivity: wallet_addresses.is_sensitive đã có
-- Privacy modes: contacts.privacy_display_mode đã có
-- Security levels: contacts.security_level đã có
-- Auth requirements: contacts.auth_requirement đã có
```

### 4.2 State Management

```kotlin
// Global Privacy State Management
class PrivacyModeStateManager(
    private val privacyModeRepository: PrivacyModeRepository,
    private val secureStorage: SecureStorage,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(PrivacyModeState())
    val state: StateFlow<PrivacyModeState> = _state.asStateFlow()
    
    init {
        scope.launch {
            loadState()
            observeChanges()
        }
    }
    
    private suspend fun loadState() {
        _state.value = privacyModeRepository.getPrivacyModeState()
    }
    
    fun togglePrivacyMode() {
        scope.launch {
            val newState = _state.value.copy(
                isEnabled = !_state.value.isEnabled,
                lastToggleTime = Clock.System.now().toEpochMilliseconds()
            )
            _state.value = newState
            privacyModeRepository.updatePrivacyModeState(newState)
            
            // Log action
            privacyModeRepository.addPrivacyLog(
                PrivacyLog(
                    timestamp = newState.lastToggleTime,
                    actionType = ActionType.TOGGLE,
                    triggerType = TriggerType.MANUAL,
                    success = true
                )
            )
        }
    }
}
```

### 4.3 Security Implementation

```kotlin
// Biometric Authentication for Reveal
class RevealAuthenticationHandler(
    private val biometricAuth: BiometricAuthenticator,
    private val pinAuth: PinAuthenticator
) {
    suspend fun authenticateForReveal(
        contactId: String,
        onSuccess: () -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        // Try biometric first
        biometricAuth.authenticate(
            reason = "Authenticate to view sensitive address",
            onSuccess = {
                logRevealAccess(contactId, AuthMethod.BIOMETRIC)
                onSuccess()
            },
            onFailure = { biometricError ->
                // Fallback to PIN
                pinAuth.authenticate(
                    onSuccess = {
                        logRevealAccess(contactId, AuthMethod.PIN)
                        onSuccess()
                    },
                    onFailure = onFailure
                )
            }
        )
    }
}

// Screenshot Protection
// Android Implementation
class ScreenshotBlockerAndroid(private val activity: Activity) {
    fun enableProtection() {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
    
    fun disableProtection() {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}

// iOS Implementation
class ScreenshotBlockerIOS {
    fun enableProtection() {
        // Use UITextField hack or notification observer
        NotificationCenter.default.addObserver(
            forName: UIApplication.userDidTakeScreenshotNotification,
            object: nil,
            queue: .main
        ) { _ in
            // Handle screenshot taken
            showPrivacyWarning()
        }
    }
}
```

### 4.4 Performance Optimizations

```kotlin
// Cached Obfuscation
class ObfuscationCache {
    private val cache = LruCache<String, String>(100)
    
    fun getObfuscated(address: String, pattern: ObfuscationPattern): String {
        val key = "$address:${pattern.name}"
        return cache.get(key) ?: run {
            val obfuscated = ObfuscationEngine.obfuscate(address, pattern)
            cache.put(key, obfuscated)
            obfuscated
        }
    }
}

// Batch Privacy Updates
class BatchPrivacyUpdater(
    private val contactRepository: ContactRepository,
    private val scope: CoroutineScope
) {
    private val updateQueue = Channel<PrivacyUpdate>(Channel.UNLIMITED)
    
    init {
        scope.launch {
            updateQueue.consumeAsFlow()
                .chunked(50) // Batch size
                .collect { batch ->
                    contactRepository.batchUpdatePrivacy(batch)
                }
        }
    }
    
    fun queueUpdate(update: PrivacyUpdate) {
        updateQueue.trySend(update)
    }
}
```

## 5. Testing Strategy

### 5.1 Unit Tests
- Domain models và business logic
- Use cases với mock repositories
- Obfuscation algorithms
- State management

### 5.2 Integration Tests
- Database operations
- Repository implementations
- Authentication flows
- Platform-specific services

### 5.3 UI Tests
- Component rendering
- User interactions
- Gesture detection
- Animation smoothness

### 5.4 E2E Tests
- Complete user flows
- Cross-platform scenarios
- Performance under load
- Security vulnerabilities

## 6. Risk Mitigation

### 6.1 Technical Risks
- **Performance degradation**: Mitigate với caching và lazy loading
- **Battery drain (location)**: Implement smart polling và geofencing
- **Platform differences**: Abstract platform-specific code properly

### 6.2 Security Risks
- **Screenshot bypass**: Multiple protection layers
- **Authentication bypass**: Strong auth policies và audit logs
- **Data leaks**: Encryption và secure storage

### 6.3 UX Risks
- **Complexity**: Progressive disclosure và good defaults
- **Accidental activation**: Confirmation dialogs và undo options
- **Performance perception**: Loading states và animations

## 7. Success Metrics

### 7.1 Technical Metrics
- Privacy mode toggle < 100ms
- Address obfuscation < 10ms
- No memory leaks
- < 0.1% crash rate

### 7.2 User Metrics
- 80% feature adoption
- < 3 taps to toggle
- 95% successful auth rate
- Positive user feedback

### 7.3 Security Metrics
- Zero data breaches
- 100% audit log coverage
- No screenshot leaks
- Strong auth compliance

## 8. Post-Release Plan

### 8.1 Monitoring
- Crashlytics integration
- Performance monitoring
- Usage analytics
- User feedback collection

### 8.2 Iterations
- A/B testing variations
- Feature refinements
- Performance optimizations
- New privacy features

### 8.3 Documentation
- User guides
- Video tutorials
- FAQ section
- Developer docs

## 9. Conclusion - MAJOR DISCOVERY!

### 🔥 **QUAN TRọNG**: Hệ thống Privacy đã có sẵn 90%!

Sau khi phân tích kỹ lưỡng, phát hiện Address Book đã có **hệ thống privacy hoàn chỉnh**:

#### ✅ **Đã có sẵn**:
- Database schema đầy đủ: contacts.is_sensitive, privacy_display_mode, security_level
- Authentication system: auth_requirement (BIOMETRIC, PIN)
- Encrypted data storage: encrypted_data column
- Audit logging: security_audit_logs table
- User settings: privacy_mode_enabled, default_privacy_display
- Safe zones: safe_zones TEXT (JSON storage)

#### 🔧 **Chỉ cần implement**:
1. **UI Toggle**: Privacy mode button trong ContactListScreen
2. **Obfuscation Logic**: Sử dụng privacy_display_mode hiện có
3. **Gesture Controls**: Double-tap, swipe cho quick actions
4. **Platform Integration**: Screenshot/clipboard protection

#### 🚀 **Kết quả**:
- **Timeline giảm**: 6 tuần → 2 tuần
- **Complexity giảm**: Major refactor → UI enhancements
- **Risk giảm**: Database changes → Zero schema changes
- **Resources giảm**: 3 sprints → 1 sprint

Privacy Mode chỉ là **một UI wrapper** cho hệ thống privacy sâu sắc đã có sẵn!