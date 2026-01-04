# Privacy Mode Feature - Address Book Module

## 1. Tổng quan hệ thống Address Book hiện tại

### 1.1 Kiến trúc hiện tại

#### Domain Models
- **ContactEntity**: Model contact chính với các tính năng bảo mật
  - Security levels: NORMAL, HIGH, MAXIMUM
  - Privacy display modes: FULL, HIDDEN, SECRET
  - Auth requirements: NONE, BIOMETRIC, BIOMETRIC_PIN
  - Hỗ trợ mã hóa dữ liệu nhạy cảm

- **WalletAddressEntity**: Địa chỉ ví với thuộc tính `isSensitive`
- **GroupEntity**: Nhóm contact với privacy levels
- **TagEntity**: Tag để tổ chức contacts

#### Database Schema (SQLDelight)
- 25+ tables bao gồm contacts, wallet_addresses, groups, tags
- Hỗ trợ security audit logs
- User settings table cho preferences

#### Security & Privacy Infrastructure sẵn có
- **SecureAuthProvider**: Authentication provider
- **SecureActionHandler**: Xử lý các action bảo mật
- **SecureAuthPolicyProvider**: Policy cho authentication
- **SecureAuthFlowCoordinator**: Điều phối authentication flows

### 1.2 Tính năng Privacy hiện có (ĐÃ IMPLEMENT)
- ✅ **Contact privacy modes**: FULL, HIDDEN, SECRET (privacy_display_mode column)
- ✅ **Security levels**: NORMAL, HIGH, MAXIMUM (security_level column) 
- ✅ **Sensitive flag**: is_sensitive cho contacts và addresses
- ✅ **Authentication requirements**: NONE, BIOMETRIC, BIOMETRIC_PIN (auth_requirement column)
- ✅ **Encrypted data storage**: encrypted_data column
- ✅ **Audit logging**: security_audit_logs table
- ✅ **User privacy settings**: privacy_mode_enabled, default_privacy_display
- ✅ **Safe zones storage**: safe_zones TEXT column (JSON)

## 2. Privacy Mode - Thiết kế chi tiết

### 2.1 Mục tiêu
- Ẩn địa chỉ ví nhạy cảm khi sử dụng nơi công cộng
- Tích hợp mượt mà với hệ thống hiện có
- Dễ sử dụng với toggle nhanh
- Bảo mật cao với authentication

### 2.2 Kiến trúc đề xuất

#### 2.2.1 Domain Layer mới

```kotlin
// Domain models - tận dụng user_settings table hiện có
data class PrivacyModeState(
    val isEnabled: Boolean = false, // maps to privacy_mode_enabled
    val defaultPrivacyDisplay: PrivacyDisplayMode = PrivacyDisplayMode.PARTIAL, // existing column
    val hideSensitiveOnly: Boolean = true, // new column needed
    val autoActivationEnabled: Boolean = false, // new column needed
    val quickHideEnabled: Boolean = true, // new column needed
    val screenshotProtection: Boolean = true, // new column needed
    val clipboardProtection: Boolean = true, // new column needed
    val safeZones: List<SafeZone> = emptyList(), // stored as JSON in safe_zones column
    val lastToggleTime: Long = 0L
)

enum class PrivacyDisplayMode {
    FULL,    // Hiển thị đầy đủ địa chỉ
    PARTIAL, // Hiển thị một phần (0x1234...5678)
    HIDDEN   // Ẩn hoàn toàn (****)
}

data class SafeZone(
    val id: String,
    val name: String,
    val type: SafeZoneType,
    val coordinates: Coordinates? = null,
    val wifiSSID: String? = null
)

enum class SafeZoneType {
    HOME, OFFICE, CUSTOM
}

// Use cases
interface PrivacyModeUseCase {
    suspend fun togglePrivacyMode()
    suspend fun setPrivacyModeState(enabled: Boolean)
    suspend fun getPrivacyModeState(): Flow<PrivacyModeState>
    suspend fun configureSafeZones(zones: List<SafeZone>)
    suspend fun checkAutoActivation(location: Location?, wifiInfo: WifiInfo?)
}

interface ContactPrivacyUseCase {
    suspend fun markContactAsSensitive(contactId: String, isSensitive: Boolean)
    suspend fun getObfuscatedAddress(address: String, privacyMode: Boolean): String
    suspend fun canViewFullAddress(contactId: String): Boolean
}
```

#### 2.2.2 Repository Layer

```kotlin
interface PrivacyModeRepository {
    suspend fun getPrivacyModeState(): PrivacyModeState
    suspend fun updatePrivacyModeState(state: PrivacyModeState)
    suspend fun addPrivacyToggleLog(timestamp: Long, enabled: Boolean)
    suspend fun getPrivacyLogs(): List<PrivacyLog>
}

// Extension cho ContactRepository hiện có
interface ContactRepositoryExt : ContactRepository {
    suspend fun getContactsWithPrivacy(privacyModeEnabled: Boolean): Flow<List<ContactEntity>>
    suspend fun updateContactSensitivity(contactId: String, isSensitive: Boolean)
}
```

#### 2.2.3 Database Schema mở rộng

```sql
-- user_settings table đã có sẵn các columns:
-- privacy_mode_enabled INTEGER AS Boolean DEFAULT 0
-- default_privacy_display TEXT DEFAULT 'PARTIAL' -- FULL, PARTIAL, HIDDEN

-- KHÔNG CẦN THÊM GÌ VÀO DATABASE!
-- Hệ thống privacy đã có sẵn hoàn chỉnh:

-- contacts table đã có:
-- is_sensitive INTEGER AS Boolean DEFAULT 0
-- security_level TEXT DEFAULT 'NORMAL' -- NORMAL, HIGH, MAXIMUM  
-- privacy_display_mode TEXT DEFAULT 'FULL' -- FULL, HIDDEN, SECRET
-- auth_requirement TEXT DEFAULT 'NONE' -- NONE, BIOMETRIC, BIOMETRIC_PIN
-- encrypted_data TEXT

-- user_settings table đã có:
-- privacy_mode_enabled INTEGER AS Boolean DEFAULT 0
-- default_privacy_display TEXT DEFAULT 'PARTIAL' -- FULL, PARTIAL, HIDDEN
-- biometric_auth_enabled INTEGER AS Boolean DEFAULT 0
-- two_factor_auth_enabled INTEGER AS Boolean DEFAULT 0
-- safe_zones TEXT (JSON storage)

-- security_audit_logs table đã có sẵn cho audit trail
-- wallet_addresses table cũng đã có is_sensitive column

-- CHỈ CẦN THÊM VÀI COLUMNS TÙY CHỌN:
ALTER TABLE user_settings ADD COLUMN privacy_quick_hide_enabled INTEGER DEFAULT 1;
ALTER TABLE user_settings ADD COLUMN privacy_auto_activation_enabled INTEGER DEFAULT 0;
```

### 2.3 UI/UX Implementation

#### 2.3.1 UI Components

```kotlin
// Privacy Mode Toggle Button
@Composable
fun PrivacyModeToggle(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isEnabled) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
            contentDescription = "Privacy Mode",
            tint = if (isEnabled) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
        )
    }
}

// Obfuscated Address Display - sử dụng default_privacy_display
@Composable
fun ObfuscatedAddress(
    address: String,
    isPrivacyMode: Boolean,
    isSensitive: Boolean,
    privacyDisplayMode: PrivacyDisplayMode,
    onRevealRequest: () -> Unit
) {
    val displayAddress = remember(address, isPrivacyMode, isSensitive, privacyDisplayMode) {
        when {
            !isPrivacyMode || !isSensitive -> address
            else -> obfuscateAddress(address, privacyDisplayMode)
        }
    }
    
    Row(
        modifier = Modifier.clickable(
            enabled = isPrivacyMode && isSensitive,
            onClick = onRevealRequest
        )
    ) {
        Text(displayAddress)
        if (isPrivacyMode && isSensitive) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = "Tap to reveal",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Helper function - obfuscate based on privacy display mode
fun obfuscateAddress(address: String, mode: PrivacyDisplayMode): String {
    return when (mode) {
        PrivacyDisplayMode.FULL -> address // Không ẩn
        PrivacyDisplayMode.PARTIAL -> when {
            address.length <= 10 -> "${address.take(2)}...${address.takeLast(2)}"
            else -> "${address.take(4)}...${address.takeLast(4)}"
        }
        PrivacyDisplayMode.HIDDEN -> "****" // Ẩn hoàn toàn
    }
}
```

#### 2.3.2 Screen Integration

```kotlin
// ContactListScreen modification
@Composable
fun ContactListScreenWithPrivacy(
    navigator: Navigator,
    screenModel: ContactListScreenModel = rememberScreenModel()
) {
    val privacyModeState by screenModel.privacyModeState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacts") },
                actions = {
                    PrivacyModeToggle(
                        isEnabled = privacyModeState.isEnabled,
                        onToggle = { screenModel.togglePrivacyMode() }
                    )
                }
            )
        }
    ) { paddingValues ->
        // Contact list with privacy mode applied
    }
}

// ContactDetailScreen modification
@Composable
fun ContactDetailWithPrivacy(
    contact: ContactEntity,
    privacyModeEnabled: Boolean,
    onAuthRequired: () -> Unit
) {
    // Display logic with privacy mode
    contact.walletAddresses.forEach { address ->
        ObfuscatedAddress(
            address = address.address,
            isPrivacyMode = privacyModeEnabled,
            isSensitive = contact.isSensitive || address.isSensitive,
            onRevealRequest = {
                // Trigger biometric/PIN authentication
                onAuthRequired()
            }
        )
    }
}
```

### 2.4 Tính năng nâng cao

#### 2.4.1 Auto-activation based on location

```kotlin
class LocationBasedPrivacyService(
    private val privacyModeUseCase: PrivacyModeUseCase,
    private val locationProvider: LocationProvider,
    private val wifiManager: WifiManager
) {
    fun startMonitoring() {
        // Monitor location changes
        locationProvider.locationUpdates.collect { location ->
            checkAndUpdatePrivacyMode(location)
        }
        
        // Monitor WiFi changes
        wifiManager.wifiStateChanges.collect { wifiInfo ->
            checkWifiAndUpdatePrivacyMode(wifiInfo)
        }
    }
    
    private suspend fun checkAndUpdatePrivacyMode(location: Location) {
        val state = privacyModeUseCase.getPrivacyModeState().first()
        if (!state.autoActivationEnabled) return
        
        val isInSafeZone = state.safeZones.any { zone ->
            zone.coordinates?.let { 
                location.distanceTo(it) < SAFE_ZONE_RADIUS 
            } ?: false
        }
        
        privacyModeUseCase.setPrivacyModeState(!isInSafeZone)
    }
}
```

#### 2.4.2 Quick Hide Double Tap

```kotlin
@Composable
fun PrivacyModeGestureDetector(
    content: @Composable () -> Unit
) {
    var lastTapTime by remember { mutableStateOf(0L) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        // Toggle privacy mode
                        screenModel.togglePrivacyMode()
                    }
                )
            }
    ) {
        content()
    }
}
```

#### 2.4.3 Security Integration

```kotlin
// Integration with existing SecureActionHandler
class PrivacyModeSecureActionHandler(
    private val secureActionHandler: SecureActionHandler,
    private val privacyModeRepository: PrivacyModeRepository
) {
    suspend fun revealSensitiveAddress(
        contactId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        secureActionHandler.executeSecureAction(
            action = SecureAction.ViewSensitiveData(contactId),
            authPolicy = AuthPolicy.BiometricOrPin,
            onSuccess = {
                // Log access
                privacyModeRepository.addPrivacyToggleLog(
                    timestamp = System.currentTimeMillis(),
                    enabled = false
                )
                onSuccess()
            },
            onFailure = onFailure
        )
    }
}
```

### 2.5 Migration Strategy

#### Phase 1: Core Privacy Mode (Sprint 1)
1. Implement PrivacyModeState và repository
2. Add database schema changes
3. Basic UI toggle trong ContactListScreen
4. Address obfuscation logic

#### Phase 2: Enhanced Features (Sprint 2)
1. Selective sensitivity (per contact/address)
2. Authentication for reveal
3. Settings integration
4. Privacy logs

#### Phase 3: Premium Features (Sprint 3)
1. Auto-activation based on location/WiFi
2. Safe zones configuration
3. Quick hide gestures
4. Advanced analytics

### 2.6 Testing Strategy

```kotlin
// Unit Tests
class PrivacyModeUseCaseTest {
    @Test
    fun `toggle privacy mode updates state correctly`() {
        // Test implementation
    }
    
    @Test
    fun `obfuscate address returns correct format`() {
        val address = "0x1234567890abcdef"
        val obfuscated = obfuscateAddress(address)
        assertEquals("0x12****ef", obfuscated)
    }
}

// Integration Tests
class PrivacyModeIntegrationTest {
    @Test
    fun `privacy mode hides sensitive addresses in contact list`() {
        // Test with mock data
    }
}

// UI Tests
class PrivacyModeUITest {
    @Test
    fun `privacy toggle button changes icon state`() {
        // Compose UI test
    }
}
```

### 2.7 Performance Considerations

1. **Caching**: Cache obfuscated addresses để tránh recalculation
2. **Lazy Loading**: Chỉ load privacy state khi cần
3. **Batch Operations**: Update nhiều contacts cùng lúc
4. **Background Processing**: Auto-activation chạy background

### 2.8 Security Best Practices

1. **No Screenshots**: Disable screenshot khi privacy mode ON
2. **Clipboard Protection**: Prevent copy sensitive addresses
3. **Session Timeout**: Auto-enable privacy mode sau inactivity
4. **Audit Trail**: Log tất cả privacy-related actions

## 3. API Integration

### 3.1 Function Calling Integration

```kotlin
// Add to AddressBookFunctions
object PrivacyModeFunctions {
    val togglePrivacyMode = FunctionDefinition(
        name = "toggle_privacy_mode",
        description = "Toggle privacy mode for address book",
        parameters = emptyList()
    )
    
    val setSensitiveContact = FunctionDefinition(
        name = "mark_contact_sensitive",
        description = "Mark a contact as sensitive",
        parameters = listOf(
            Parameter("contact_id", "string", "Contact ID"),
            Parameter("is_sensitive", "boolean", "Sensitive flag")
        )
    )
}
```

## 4. Kết luận

Privacy Mode feature sẽ tích hợp hoàn hảo với Address Book hiện có, tận dụng infrastructure security đã có và mở rộng với các tính năng privacy mới. Implementation theo phases cho phép release incremental và thu thập feedback sớm.