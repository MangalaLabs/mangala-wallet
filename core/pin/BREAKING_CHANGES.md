# PIN Module - Breaking Changes

## 🚨 Breaking Changes Summary

**Date**: 2026-01-23
**Reason**: Remove app-specific coupling from `core:pin` module
**Impact**: V1 screens still work. New V2 screens use callback-based approach.
**Status**: ✅ BACKWARD COMPATIBLE - Old screens continue to work

---

## ✅ What Changed

### 1. **ScreenModels Now Support Both Patterns**
- ✅ Old `pinCase`-based approach still works (for V1 screens)
- ✅ New callback-based approach available (for V2 screens and new code)
- ✅ `ConfirmPinScreenModel` accepts EITHER `pinCase` OR `callbacks`
- ✅ `UnlockPinScreenModel` accepts EITHER `unlockPinCase` OR `callbacks`

### 2. **Navigation Methods**
- ✅ Internal PIN navigation preserved (`showConfirmPinScreen()`, `showSetupPinScreen()`)
- ✅ App-specific navigation moved to private methods in screen models
- ✅ `BasePinScreenModel` keeps only internal PIN flow navigation

### 3. **Backward Compatibility**
- ✅ All old V1 screens (`SetupPinScreen`, `ConfirmPinScreen`, `UnlockPinScreen`) continue to work
- ✅ Existing app code doesn't need immediate changes
- ✅ Can migrate gradually to callback-based approach

---

## ✅ What's Added

### 1. **New Callback Interfaces**
```kotlin
interface PinSetupCallbacks {
    fun onSuccess()
    fun onError(error: String)
    fun onCancel()
}

interface PinUnlockCallbacks {
    fun onSuccess()
    fun onError(error: String)
    fun onLocked(unlockTime: String, remainingTime: String)
    fun onRateLimited(retryAfterSeconds: Long)
    fun onCancel()
}
```

### 2. **Updated ScreenModel Signatures**
```kotlin
// Old
class ConfirmPinScreenModel(
    val pin: String,
    private val pinCase: SharedScreen.SetupPinScreen.SetupPinScreenCase
)

// New
class ConfirmPinScreenModel(
    val pin: String,
    private val callbacks: PinSetupCallbacks
)
```

---

## 🔄 Migration Guide

### Before (Old Code - WILL BREAK):
```kotlin
// In app layer
val setupPinScreen = ScreenRegistry.get(
    SharedScreen.SetupPinScreen(
        pinCase = SetupPinScreenCase.CREATE_NEW_WALLET,
        blockchainUid = uid,
        onPinSetupSuccess = { /* sometimes used */ }
    )
)
navigator.push(setupPinScreen)
```

### After (New Code - REQUIRED):
```kotlin
// In app layer
val setupPinScreen = SetupPinScreenV2(
    onSuccess = {
        // App decides what to do after PIN setup
        navigator.push(CreateWalletScreen())
    },
    onCancel = {
        navigator.pop()
    },
    onError = { error ->
        showError(error)
    }
)
navigator.push(setupPinScreen)
```

---

## 📝 Required Changes in App Layer

### 1. **SetupPinScreen Usage**
**Find all usages of**:
```kotlin
SharedScreen.SetupPinScreen.SetupPinScreenCase
```

**Replace with**:
```kotlin
SetupPinScreenV2(
    onSuccess = { /* navigate based on your use case */ },
    onCancel = { /* handle cancel */ },
    onError = { error -> /* handle error */ }
)
```

### 2. **UnlockPinScreen Usage**
**Find all usages of**:
```kotlin
SharedScreen.UnlockPinScreen.XXXX
```

**Replace with**:
```kotlin
UnlockPinScreenV2(
    onSuccess = { /* continue with action */ },
    onCancel = { /* handle cancel */ },
    onError = { error -> /* show error */ },
    onLocked = { unlockTime, remaining -> /* show lockout */ }
)
```

### 3. **ConfirmPinScreen Usage**
**Old code**:
```kotlin
ConfirmPinScreen(
    pin = userPin,
    pinCase = CREATE_NEW_WALLET
)
```

**New code**:
```kotlin
ConfirmPinScreen(
    pin = userPin,
    onSuccess = { navigator.push(CreateWalletScreen()) },
    onCancel = { navigator.pop() }
)
```

---

## 🎯 Example Migration

### Use Case: Create New Wallet with PIN

#### Before (Coupled):
```kotlin
// App calls
ScreenRegistry.get(
    SharedScreen.SetupPinScreen(
        pinCase = SetupPinScreenCase.CREATE_NEW_WALLET
    )
)

// PIN module decides navigation
when(pinCase) {
    CREATE_NEW_WALLET -> showCreateWalletScreen()  // ❌ Module knows app logic
}
```

#### After (Decoupled):
```kotlin
// App calls
SetupPinScreenV2(
    onSuccess = {
        // ✅ App decides navigation
        navigator.push(CreateWalletScreen())
    }
)

// PIN module just calls callback
callbacks.onSuccess()  // ✅ Module is dumb, app is smart
```

---

## 📍 How to Use (Choose Your Approach)

### Option A: Keep Using Old Screens (No Changes Required)
Your existing code continues to work:
```kotlin
// Old V1 screens - still works
ScreenRegistry.get(
    SharedScreen.SetupPinScreen(pinCase = CREATE_NEW_WALLET)
)
```

### Option B: Migrate to New V2 Screens (Recommended)
For new code or when refactoring, use V2 screens with callbacks:
```kotlin
// New V2 approach
SetupPinScreenV2(
    onSuccess = { navigator.push(CreateWalletScreen()) },
    onCancel = { navigator.pop() },
    onError = { error -> showError(error) }
)
```

### Migration Timeline
- ✅ **Now**: Both V1 and V2 screens work
- ⏳ **Later**: Team can gradually migrate to V2 screens
- 🎯 **Future**: Eventually deprecate V1 screens (not yet)

---

## ✅ Benefits After Migration

1. **No Coupling**: PIN module doesn't know about app screens
2. **Reusable**: Can use PIN module in any app
3. **Testable**: Easy to test with mock callbacks
4. **Flexible**: Different flows for different contexts
5. **Clear**: Explicit about what happens after PIN operations

---

## 🔧 Backwards Compatibility

**Full backward compatibility maintained!**

✅ Old V1 screens continue to work without changes
✅ No compilation errors for existing code
✅ Gradual migration path available
✅ Team can choose when to migrate

### Implementation Details

The ScreenModels now support BOTH approaches:

```kotlin
// ConfirmPinScreenModel constructor
class ConfirmPinScreenModel(
    val pin: String,
    private val pinCase: SharedScreen.SetupPinScreen.SetupPinScreenCase? = null,  // V1 approach
    private val callbacks: PinSetupCallbacks? = null,  // V2 approach
)
```

- If `pinCase` is provided → uses old navigation approach
- If `callbacks` is provided → uses new callback approach
- One of them must be provided (both cannot be null)

---

## 📞 Need Help?

If you encounter issues during migration:
1. Check this document's examples
2. Look at `PinCallbacks.kt` for available callback interfaces
3. Refer to V2 screens (SetupPinScreenV2, UnlockPinScreenV2) for usage examples

---

**Status**: ✅ Backward compatible - No breaking changes. Old screens work, new V2 screens available for gradual migration.
