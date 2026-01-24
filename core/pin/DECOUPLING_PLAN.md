# PIN Module Decoupling Plan

## 🎯 Goal
Remove app-specific coupling from `core:pin` module while keeping screens reusable.

## 📊 Current Architecture

### Problems:
1. ❌ `BasePinScreenModel` has 15+ navigation methods (`showHomeScreen()`, `showCreateWalletScreen()`, etc.)
2. ❌ `ConfirmPinScreenModel` depends on `SharedScreen.SetupPinScreen.SetupPinScreenCase` enum
3. ❌ Navigation decisions based on `pinCase` - app business logic
4. ❌ Direct navigation calls instead of callbacks

### Current Flow:
```
ConfirmPinScreen
  → ConfirmPinScreenModel
    → savePin()
      → navigateScreen()
        → switch(pinCase)
          → showHomeScreen() / showCreateWalletScreen() / etc.
```

## ✅ Target Architecture

### Solution: **Keep PinScreenFlow but make it generic**

**Key Insight**: `PinScreenFlow` is actually fine! It's a state machine that screens emit. The problem is that ScreenModels **decide** which flow to emit based on `pinCase`.

### New Flow:
```
ConfirmPinScreen(onSuccess: () -> Unit, onError: (String) -> Unit)
  → ConfirmPinScreenModel
    → savePin()
      → emit success/error state
      → callback to app layer

App Layer:
  → Observes callbacks
  → Decides navigation based on context
```

## 🔧 Refactoring Strategy

### Phase 1: Remove `pinCase` Parameter
**Before:**
```kotlin
class ConfirmPinScreenModel(
    val pin: String,
    private val pinCase: SharedScreen.SetupPinScreen.SetupPinScreenCase
)
```

**After:**
```kotlin
class ConfirmPinScreenModel(
    val pin: String,
    private val onSuccess: () -> Unit,
    private val onError: (String) -> Unit
)
```

### Phase 2: Replace Navigation Logic with Callbacks
**Before:**
```kotlin
private fun navigateScreen() {
    when(pinCase) {
        CREATE_NEW_WALLET -> showCreateWalletScreen()
        CHANGE_PIN -> showHomeScreen()
        ...
    }
}
```

**After:**
```kotlin
private fun handleSuccess() {
    onSuccess() // App decides what to do
}
```

### Phase 3: Keep PinScreenFlow for Internal Navigation
Some navigation is **internal** to PIN module (Setup → Confirm → Done):
```kotlin
// Internal PIN flow - stays in module
showConfirmPinScreen()  // ✅ OK - internal to PIN module
showSetupPinScreen()    // ✅ OK - internal to PIN module

// External app navigation - use callbacks
onSuccess()             // ✅ OK - app decides
onError()               // ✅ OK - app decides
```

## 📋 Implementation Checklist

### ConfirmPinScreenModel
- [ ] Remove `pinCase` parameter
- [ ] Add `onSuccess: () -> Unit` callback
- [ ] Add `onError: (String) -> Unit` callback
- [ ] Replace `navigateScreen()` with `onSuccess()`
- [ ] Keep biometry check (internal feature)

### UnlockPinScreenModel
- [ ] Remove `unlockPinCase` parameter
- [ ] Add `onUnlockSuccess: () -> Unit` callback
- [ ] Add `onUnlockError: (String) -> Unit` callback
- [ ] Remove navigation switch statement
- [ ] Keep lockout/rate-limit UI (internal feature)

### SetupPinScreenModel
- [ ] Already clean - just collects PIN
- [ ] Navigates to Confirm (internal flow) ✅

### BasePinScreenModel
- [ ] Keep `PinScreenFlow` for internal navigation
- [ ] Remove app-specific navigation methods
- [ ] Add generic `onComplete: () -> Unit` callback support

## 🎨 Usage Example (After Refactor)

### In App Layer:
```kotlin
// Create PIN flow
navigator.push(
    SetupPinScreen(
        onSuccess = {
            // App decides: go to home, create wallet, etc.
            navigator.push(HomeScreen)
        },
        onCancel = {
            navigator.pop()
        }
    )
)

// Unlock PIN flow
navigator.push(
    UnlockPinScreen(
        onSuccess = {
            // App decides: continue with transaction, show data, etc.
            viewModel.proceedWithTransaction()
        },
        onError = { error ->
            showError(error)
        }
    )
)
```

### In PIN Module:
```kotlin
class ConfirmPinScreenModel(
    val pin: String,
    private val onSuccess: () -> Unit,
    private val onError: (String) -> Unit
) {
    private fun savePin(pin: String) {
        val result = pinManager.setupPIN(pin)

        result.fold(
            onSuccess = {
                checkBiometry() // Internal feature
                onSuccess()     // Callback to app
            },
            onFailure = { error ->
                onError(error.message ?: "Failed to save PIN")
            }
        )
    }
}
```

## ✅ Benefits

1. **No app coupling**: Module doesn't know about HomeScreen, CreateWallet, etc.
2. **Reusable**: Any app can use PIN module with their own navigation
3. **Testable**: Easy to test with mock callbacks
4. **Clear separation**: PIN module = PIN logic + UI, App = business logic + navigation
5. **Flexible**: App can decide different flows for different contexts

## 🚧 Migration Path

1. ✅ Implement new PINManager (domain layer) - DONE
2. 🔄 Refactor ScreenModels to use callbacks - IN PROGRESS
3. ⏳ Update app layer to provide callbacks
4. ⏳ Remove old `pinCase` enum from SharedScreen
5. ⏳ Test all PIN flows

---

**Status**: Ready to implement Phase 1-2
**Next**: Refactor ConfirmPinScreenModel
