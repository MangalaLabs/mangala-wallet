# Privacy Mode - Action Plan (2 Tuần)

## 🎯 **MỤC TIÊU CỤ THỂ**

Tận dụng hệ thống privacy hoàn chỉnh đã có để implement **Privacy Mode UI enhancements** với focus vào UX và user-facing features.

## 📋 **PHÂN TÍCH HIỆN TRẠNG**

### ✅ **Infrastructure đã có (100%)**
```kotlin
// Database Schema - HOÀN CHỈNH
contacts {
    is_sensitive: Boolean          // ✅ Đánh dấu contact nhạy cảm  
    privacy_display_mode: String   // ✅ FULL/HIDDEN/SECRET
    security_level: String         // ✅ NORMAL/HIGH/MAXIMUM
    auth_requirement: String       // ✅ NONE/BIOMETRIC/BIOMETRIC_PIN
    encrypted_data: String         // ✅ Mã hóa dữ liệu nhạy cảm
}

wallet_addresses {
    is_sensitive: Boolean          // ✅ Địa chỉ ví nhạy cảm
}

user_settings {
    privacy_mode_enabled: Boolean  // ✅ Toggle privacy mode
    default_privacy_display: String // ✅ FULL/PARTIAL/HIDDEN
    biometric_auth_enabled: Boolean // ✅ Biometric auth
    safe_zones: String            // ✅ JSON safe zones
}

security_audit_logs {            // ✅ Audit trail
    // Complete logging system
}
```

### ✅ **Business Logic đã có (90%)**
- SecureActionHandler - Authentication flows
- SecureAuthProvider - Biometric/PIN auth  
- ContactRepository - CRUD với privacy
- Security audit logging
- Encrypted data handling

### 🔧 **Cần implement (10%)**
1. **Privacy Mode Toggle UI**
2. **Address Obfuscation Display** 
3. **Quick Actions (gestures)**
4. **Visual Feedback & Animations**

## 🗓️ **TIMELINE 2 TUẦN**

### **Tuần 1: Core UI Implementation (5 ngày)**

#### **Ngày 1: Setup & Research**
- [ ] Nghiên cứu ContactListScreen hiện tại
- [ ] Phân tích SecureActionHandler usage
- [ ] Map privacy_display_mode với requirements
- [ ] Setup development environment

#### **Ngày 2-3: Privacy Toggle Implementation**
- [ ] Create PrivacyModeToggle component
- [ ] Integrate với user_settings.privacy_mode_enabled
- [ ] Add visual states (enabled/disabled)
- [ ] Implement toggle persistence

#### **Ngày 4-5: Address Obfuscation**
- [ ] Create ObfuscatedAddress component
- [ ] Implement obfuscation logic based on privacy_display_mode
- [ ] Add reveal authentication flow
- [ ] Test với existing SecureActionHandler

### **Tuần 2: Enhanced UX & Polish (5 ngày)**

#### **Ngày 6-7: Gesture Controls**
- [ ] Double-tap gesture for quick toggle
- [ ] Swipe gestures for bulk actions
- [ ] Haptic feedback integration
- [ ] Visual animations (eye icon, transitions)

#### **Ngày 8: Platform Security**
- [ ] Screenshot protection when privacy mode ON
- [ ] Clipboard protection for sensitive addresses
- [ ] Platform-specific implementations

#### **Ngày 9-10: Testing & Polish**
- [ ] End-to-end testing
- [ ] Performance optimization
- [ ] UI/UX polish
- [ ] Documentation & demo

## 💻 **TECHNICAL IMPLEMENTATION**

### **1. Privacy Mode Toggle Component**

```kotlin
@Composable
fun PrivacyModeToggle(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    var isAnimating by remember { mutableStateOf(false) }
    
    IconButton(
        onClick = {
            isAnimating = true
            onToggle(!isEnabled)
        },
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = isEnabled,
            transitionSpec = {
                fadeIn() + scaleIn() with fadeOut() + scaleOut()
            }
        ) { enabled ->
            Icon(
                imageVector = if (enabled) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                contentDescription = if (enabled) "Disable Privacy Mode" else "Enable Privacy Mode",
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
    
    // Haptic feedback
    LaunchedEffect(isEnabled) {
        if (isAnimating) {
            // Platform-specific haptic feedback
            isAnimating = false
        }
    }
}
```

### **2. Obfuscated Address Component**

```kotlin
@Composable
fun ObfuscatedAddress(
    address: String,
    contact: ContactEntity,
    privacyModeEnabled: Boolean,
    onRevealRequest: suspend () -> Boolean,
    modifier: Modifier = Modifier
) {
    var isRevealed by remember { mutableStateOf(false) }
    var isAuthenticating by remember { mutableStateOf(false) }
    
    val displayAddress = remember(address, contact, privacyModeEnabled, isRevealed) {
        when {
            !privacyModeEnabled || !contact.isSensitive || isRevealed -> address
            else -> obfuscateAddress(address, contact.privacyDisplayMode)
        }
    }
    
    Row(
        modifier = modifier
            .clickable(
                enabled = privacyModeEnabled && contact.isSensitive && !isRevealed,
                onClick = {
                    isAuthenticating = true
                    // Trigger authentication
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayAddress,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = if (address != displayAddress) FontFamily.Monospace else FontFamily.Default
        )
        
        if (privacyModeEnabled && contact.isSensitive && !isRevealed) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Outlined.Lock,
                contentDescription = "Tap to reveal",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
    
    // Authentication handling
    LaunchedEffect(isAuthenticating) {
        if (isAuthenticating) {
            isRevealed = onRevealRequest()
            isAuthenticating = false
        }
    }
}

// Obfuscation logic using existing privacy_display_mode
fun obfuscateAddress(address: String, mode: String): String {
    return when (mode.uppercase()) {
        "FULL" -> address
        "HIDDEN" -> "****"
        "SECRET" -> "••••••••"
        else -> { // PARTIAL - default obfuscation
            when {
                address.length <= 8 -> "••••"
                address.length <= 16 -> "${address.take(4)}••••${address.takeLast(2)}"
                else -> "${address.take(6)}••••${address.takeLast(4)}"
            }
        }
    }
}
```

### **3. Contact List Screen Integration**

```kotlin
// Update ContactListScreen
@Composable
fun ContactListScreen(
    navigator: Navigator,
    screenModel: ContactListScreenModel = rememberScreenModel()
) {
    val uiState by screenModel.uiState.collectAsState()
    val privacyModeEnabled by screenModel.privacyModeEnabled.collectAsState()
    
    Scaffold(
        topBar = {
            ContactListTopBar(
                onSearchClick = { /* existing */ },
                onPrivacyToggle = { screenModel.togglePrivacyMode() },
                privacyModeEnabled = privacyModeEnabled,
                // ... existing parameters
            )
        }
    ) { paddingValues ->
        // Existing contact list với privacy mode applied
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(uiState.contacts) { contact ->
                ContactListItem(
                    contact = contact,
                    privacyModeEnabled = privacyModeEnabled,
                    onContactClick = { navigator.push(ContactDetailScreen(contact.id)) },
                    onRevealAddress = { 
                        screenModel.authenticateAndReveal(contact.id)
                    }
                )
            }
        }
    }
}
```

### **4. ScreenModel Updates**

```kotlin
class ContactListScreenModel(
    private val contactRepository: ContactRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val secureActionHandler: SecureActionHandler
) : BaseScreenModel() {
    
    private val _privacyModeEnabled = MutableStateFlow(false)
    val privacyModeEnabled = _privacyModeEnabled.asStateFlow()
    
    init {
        // Load initial privacy mode state
        viewModelScope.launch {
            userSettingsRepository.getUserSettings()
                .collect { settings ->
                    _privacyModeEnabled.value = settings.privacyModeEnabled
                }
        }
    }
    
    fun togglePrivacyMode() {
        viewModelScope.launch {
            val newState = !_privacyModeEnabled.value
            _privacyModeEnabled.value = newState
            
            // Persist to database
            userSettingsRepository.updatePrivacyMode(newState)
            
            // Add to audit log
            securityAuditRepository.logPrivacyAction(
                action = if (newState) "PRIVACY_ENABLED" else "PRIVACY_DISABLED",
                timestamp = Clock.System.now(),
                triggerType = "MANUAL"
            )
            
            // Show feedback
            showMessage(
                if (newState) "Privacy mode enabled" else "Privacy mode disabled"
            )
        }
    }
    
    suspend fun authenticateAndReveal(contactId: String): Boolean {
        return secureActionHandler.executeSecureAction(
            action = SecureAction.ViewSensitiveData(contactId),
            authPolicy = AuthPolicy.fromContact(contactId),
            onSuccess = {
                // Log reveal action
                securityAuditRepository.logPrivacyAction(
                    action = "ADDRESS_REVEALED",
                    contactId = contactId,
                    timestamp = Clock.System.now()
                )
                true
            },
            onFailure = { false }
        )
    }
}
```

## 🎮 **GESTURE CONTROLS**

### **Double-tap Quick Toggle**
```kotlin
@Composable
fun PrivacyModeGestureDetector(
    onQuickToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { offset ->
                        // Quick privacy toggle
                        onQuickToggle()
                    }
                )
            }
    ) {
        content()
    }
}
```

### **Swipe Actions**
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableContactItem(
    contact: ContactEntity,
    onToggleSensitive: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.DismissedToEnd -> {
                    onToggleSensitive()
                    false // Don't actually dismiss
                }
                else -> false
            }
        }
    )
    
    SwipeToDismiss(
        state = dismissState,
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    if (contact.isSensitive) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = "Toggle sensitivity",
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        dismissContent = { content() }
    )
}
```

## 🔒 **PLATFORM SECURITY**

### **Screenshot Protection**
```kotlin
// Android
class ScreenshotProtectionAndroid(private val activity: Activity) {
    fun applyProtection(enabled: Boolean) {
        if (enabled) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}

// iOS  
class ScreenshotProtectionIOS {
    fun applyProtection(enabled: Boolean) {
        if (enabled) {
            // Add privacy overlay or disable screenshot
            NotificationCenter.default.addObserver(
                forName: UIApplication.userDidTakeScreenshotNotification
            ) { _ ->
                showPrivacyWarning()
            }
        }
    }
}
```

## 📊 **SUCCESS METRICS**

### **Technical KPIs**
- [ ] Privacy toggle response < 50ms
- [ ] Address obfuscation < 10ms  
- [ ] Zero crashes in privacy flows
- [ ] 100% audit log coverage

### **UX KPIs**  
- [ ] < 2 taps to toggle privacy mode
- [ ] < 3 seconds for address reveal
- [ ] Smooth animations (60fps)
- [ ] Intuitive gesture recognition

### **Security KPIs**
- [ ] 100% sensitive data protection
- [ ] Zero data leaks in privacy mode
- [ ] Complete audit trail
- [ ] Strong authentication enforcement

## 🚀 **DELIVERY PLAN**

### **Week 1 Deliverables**
- [ ] Working privacy toggle in ContactListScreen
- [ ] Address obfuscation with reveal authentication
- [ ] Basic visual feedback and states
- [ ] Integration với existing security system

### **Week 2 Deliverables**  
- [ ] Complete gesture controls (double-tap, swipe)
- [ ] Platform security features
- [ ] Polished animations and UX
- [ ] Full test coverage and documentation

### **Definition of Done**
- [ ] All UI components functional
- [ ] Security integration verified
- [ ] Cross-platform testing passed
- [ ] Performance benchmarks met
- [ ] Code review approved
- [ ] Documentation complete

## 💡 **TECHNICAL NOTES**

1. **Zero Database Changes** - Tận dụng 100% schema hiện có
2. **Minimal Business Logic** - Chỉ UI wrapper cho existing systems  
3. **Platform Consistency** - Sử dụng shared UI components
4. **Performance First** - Lazy loading và caching cho obfuscation
5. **Security by Design** - Tận dụng SecureActionHandler đã test kỹ

Privacy Mode thực chất là **UX enhancement** cho powerful privacy system đã có sẵn!