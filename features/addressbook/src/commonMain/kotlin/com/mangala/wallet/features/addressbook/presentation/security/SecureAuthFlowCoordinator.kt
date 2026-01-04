package com.mangala.wallet.features.addressbook.presentation.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.mangala.wallet.biometry.presentation.IBiometryScreenModel
import com.mangala.wallet.features.addressbook.domain.usecase.setting.GetCurrentUserSettingUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.setting.UpdateCurrentSettingUseCase
import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import io.github.aakira.napier.Napier

// Constants
private const val RESET_STATE_UI_UPDATE_DELAY_MS = 50L

/**
 * Kết quả của quá trình xác thực
 */
sealed class AuthResult {
    object Success : AuthResult()
    object Cancel : AuthResult()
    object Failure : AuthResult()
}

/**
 * Định nghĩa trạng thái hiện tại của flow xác thực
 */
sealed class AuthFlowState {
    data object Idle : AuthFlowState()
    data object BiometryCheck : AuthFlowState()
    data object PinCheck : AuthFlowState()
    data object PinSetupRequired : AuthFlowState()
    data object TwoFactorCheck : AuthFlowState()
    data object TwoFactorSetupRequired : AuthFlowState()
    data object Completed : AuthFlowState()
}

/**
 * Điều phối luồng xác thực an toàn dựa trên các policy
 */
class SecureAuthFlowCoordinator(
    private val getCurrentUserSettingUseCase: GetCurrentUserSettingUseCase,
    private val sessionManager: SecureAuthSessionManager,
    private val analytics: SecureAuthAnalytics,
    private val biometryScreenModel: IBiometryScreenModel,
    private val updateCurrentSettingUseCase: UpdateCurrentSettingUseCase,
    private val getIsPinSetupUseCase: GetIsPinSetupUseCase
) {
    // Use SupervisorJob for better error handling and cancellation
    internal val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Lưu trữ hành động đang được thực thi
    private var currentActionId: SecureActionId? = null
    private var currentActionType: SecureActionType? = null
    private val _currentState = MutableStateFlow<AuthFlowState>(AuthFlowState.Idle)
    val currentState: StateFlow<AuthFlowState> = _currentState.asStateFlow()

    // Dùng để lưu callback khi xác thực thành công hoặc thất bại
    private var onSuccessCallback: (() -> Unit)? = null
    private var onCancelCallback: (() -> Unit)? = null
    private var onNavigationCallback: (() -> Unit)? = null

    /**
     * Bắt đầu flow xác thực với loại xác thực yêu cầu
     */
    fun startAuthFlow(
        actionId: SecureActionId,
        type: SecureActionType,
        onSuccess: () -> Unit,
        onCancel: () -> Unit
    ) {
        currentActionId = actionId
        currentActionType = type
        onSuccessCallback = onSuccess
        onCancelCallback = onCancel

        scope.launch {
            // Kiểm tra xem đã có session hợp lệ nào chưa (session manager sẽ xử lý ViewHighSecurityContact)
            if (sessionManager.hasValidSession(type, actionId)) {
                // Nếu đã có session hợp lệ, không cần xác thực lại
                val sessionAge = sessionManager.getSessionAge(type)
                currentActionId?.let {
                    analytics.trackSessionReuse(it, sessionAge)
                }
                _currentState.value = AuthFlowState.Completed
                return@launch
            }

            // Ghi nhận sự kiện bắt đầu xác thực
            currentActionId?.let {
                analytics.trackAuthenticationStarted(it, type)
            }

            // Lấy setting hiện tại và kiểm tra PIN setup
            val currentUserSetting = getCurrentUserSettingUseCase()
            val isPinSetup = getIsPinSetupUseCase()

            when (type) {
                is SecureActionType.None -> {
                    // Không cần xác thực
                    onSuccessCallback?.invoke()
                    resetState()
                }

                is SecureActionType.RequirePin -> {
                    if (isPinSetup) {
                        _currentState.value = AuthFlowState.PinCheck
                    } else {
                        _currentState.value = AuthFlowState.PinSetupRequired
                    }
                }

                is SecureActionType.RequireBiometryOrPin -> {
                    val isBiometricEnabled = currentUserSetting?.biometricAuthEnabled == true
                    val isDeviceSupported = biometryScreenModel.isBiometricAvailable()
                    if (isBiometricEnabled && isDeviceSupported) {
                        _currentState.value = AuthFlowState.BiometryCheck
                    } else {
                        if (!isPinSetup) {
                            _currentState.value = AuthFlowState.PinSetupRequired
                        } else {
                            _currentState.value = AuthFlowState.PinCheck
                        }
                    }
                }

                is SecureActionType.Require2FA -> {
                    val isSetting2FAEnabled = currentUserSetting?.twoFactorAuthEnabled == true
                    if (isSetting2FAEnabled) {
                        _currentState.value = AuthFlowState.TwoFactorCheck
                    } else {
                        _currentState.value = AuthFlowState.TwoFactorSetupRequired
                    }
                }
            }
        }
    }

    /**
     * Xử lý kết quả từ BiometryScreen
     */
    fun handleBiometryResult(result: AuthResult) {
        val actionType = SecureActionType.RequireBiometryOrPin

        when (result) {
            is AuthResult.Success -> {
                currentActionId?.let {
                    analytics.trackAuthenticationSuccess(it, actionType, 0)
                }
                completeAuthFlow(true)
            }

            is AuthResult.Cancel -> {
                currentActionId?.let {
                    analytics.trackAuthenticationCancelled(it, actionType)
                }
                onCancelCallback?.invoke()
                resetState()
            }

            is AuthResult.Failure -> {
                // Ghi nhận thất bại biometry
                currentActionId?.let {
                    analytics.trackAuthenticationFailure(
                        it,
                        actionType,
                        AuthenticationFailureReason.BiometricHardwareFailed.toString(),
                        1
                    )
                }
                // Fallback to PIN
                _currentState.value = AuthFlowState.PinCheck
            }
        }
    }

    /**
     * Xử lý kết quả từ UnlockPinScreen
     */
    fun handlePinResult(result: AuthResult) {

        val actionType = SecureActionType.RequirePin

        when (result) {
            is AuthResult.Success -> {
                currentActionId?.let {
                    analytics.trackAuthenticationSuccess(it, actionType, 0)
                }
                completeAuthFlow(true)
            }

            is AuthResult.Cancel -> {
                currentActionId?.let {
                    analytics.trackAuthenticationCancelled(it, actionType)
                }
                onCancelCallback?.invoke()
                resetState()
            }

            is AuthResult.Failure -> {
                currentActionId?.let {
                    analytics.trackAuthenticationFailure(
                        it,
                        actionType,
                        AuthenticationFailureReason.InvalidCredentials.toString(),
                        1
                    )
                }
                onCancelCallback?.invoke()
                resetState()
            }
        }
    }

    /**
     * Xử lý kết quả từ PIN Setup
     */
    fun handlePinSetupResult(result: AuthResult) {
        when (result) {
            is AuthResult.Success -> {
                // PIN đã được setup thành công - người dùng đã xác thực bằng việc setup PIN
                // Không cần yêu cầu unlock PIN nữa, complete auth flow ngay lập tức
                currentActionId?.let {
                    analytics.trackAuthenticationSuccess(
                        it,
                        currentActionType ?: SecureActionType.RequirePin,
                        0
                    )
                }
                completeAuthFlow(true)
            }

            is AuthResult.Cancel -> {
                currentActionId?.let {
                    analytics.trackAuthenticationCancelled(it, SecureActionType.RequirePin)
                }
                completeAuthFlow(false)
            }

            is AuthResult.Failure -> {
                currentActionId?.let {
                    analytics.trackAuthenticationFailure(
                        it,
                        SecureActionType.RequirePin,
                        AuthenticationFailureReason.InvalidCredentials.toString(),
                        1
                    )
                }
                completeAuthFlow(false)
            }
        }
    }

    /**
     * Xử lý kết quả từ 2FA
     */
    fun handle2FAResult(result: AuthResult) {
        val actionType = SecureActionType.Require2FA

        when (result) {
            is AuthResult.Success -> {
                currentActionId?.let {
                    analytics.trackAuthenticationSuccess(it, actionType, 0)
                }
                completeAuthFlow(true)
            }

            is AuthResult.Cancel -> {
                currentActionId?.let {
                    analytics.trackAuthenticationCancelled(it, actionType)
                }
                onCancelCallback?.invoke()
                resetState()
            }

            is AuthResult.Failure -> {
                currentActionId?.let {
                    analytics.trackAuthenticationFailure(
                        it,
                        actionType,
                        AuthenticationFailureReason.InvalidCredentials.toString(),
                        1
                    )
                }
                onCancelCallback?.invoke()
                resetState()
            }
        }
    }

    fun enable2FAonSetting() {
        scope.launch {
            val currentUserSetting = getCurrentUserSettingUseCase()
            if (currentUserSetting != null) {
                val updatedSetting = currentUserSetting.copy(twoFactorAuthEnabled = true)
                updateCurrentSettingUseCase(updatedSetting)
            }
        }
    }

    /**
     * Hoàn thành luồng xác thực
     */
    fun completeAuthFlow(success: Boolean) {
        if (success) {
            // Lấy actionType hiện tại từ flow state
            val currentActionType = getCurrentActionTypeFromState()
            if (currentActionType != null) {
                // Tạo session xác thực cho action type này
                sessionManager.createSession(currentActionType)
            }

            // Call success callback before navigation
            onSuccessCallback?.invoke()

            // Navigate back to original screen (SecureAuthScreen will be popped)
            onNavigationCallback?.invoke()
        } else {
            onCancelCallback?.invoke()

            // Navigate back immediately on cancel
            onNavigationCallback?.invoke()
        }

        // Reset state after everything is done
        resetState()
    }

    /**
     * Lấy ActionType tương ứng với trạng thái hiện tại
     */
    private fun getCurrentActionTypeFromState(): SecureActionType? {
        return when (_currentState.value) {
            AuthFlowState.PinCheck -> SecureActionType.RequirePin
            AuthFlowState.PinSetupRequired -> currentActionType // Return the original action type
            AuthFlowState.BiometryCheck -> SecureActionType.RequireBiometryOrPin
            AuthFlowState.TwoFactorCheck -> SecureActionType.Require2FA
            else -> null
        }
    }

    /**
     * Set navigation callback to be called after authentication completes
     */
    fun setNavigationCallback(callback: () -> Unit) {
        onNavigationCallback = callback
    }

    /**
     * Reset trạng thái về ban đầu
     * Uses a coroutine to ensure proper timing and avoid race conditions
     */
    fun resetState() {
        // Clear callbacks immediately
        onSuccessCallback = null
        onCancelCallback = null
        onNavigationCallback = null
        currentActionId = null
        currentActionType = null

        // Reset state with a small delay to ensure UI has updated
        // Only set state once in the coroutine to avoid race conditions
        scope.launch {
            delay(RESET_STATE_UI_UPDATE_DELAY_MS)
            _currentState.value = AuthFlowState.Idle
        }
    }

    /**
     * Clean up resources when coordinator is no longer needed
     */
    fun dispose() {
        // Clear state first before canceling scope
        // This ensures resetState() can complete its coroutine
        onSuccessCallback = null
        onCancelCallback = null
        onNavigationCallback = null
        currentActionId = null
        currentActionType = null
        _currentState.value = AuthFlowState.Idle

        // Then cancel the scope
        scope.cancel()
    }
}

/**
 * Interface kiểm tra khả năng sử dụng sinh trắc học
 */
interface BiometryAvailabilityChecker {
    fun isBiometricAuthAvailable(): Boolean
}

/**
 * Interface phục vụ xác thực 2FA
 */
interface TwoFactorAuthService {
    fun is2FAEnabled(): Boolean
    fun sendOTP(): Boolean
    fun verify(code: String): Boolean
}

/**
 * Interface xử lý các yêu cầu đặc biệt trong flow xác thực
 */
interface SecureAuthResultHandler {
    fun prompt2FASetup()
}

/**
 * Composable chính điều phối hiển thị các màn hình xác thực
 */
@Composable
fun SecureAuthFlow(
    coordinator: SecureAuthFlowCoordinator,
    onNavigateBack: () -> Unit,
    localNavigator: cafe.adriel.voyager.navigator.Navigator
) {
    val state by coordinator.currentState.collectAsStateMultiplatform()
    var showContent by remember { mutableStateOf(false) }

    val localGlobalNavigator = LocalGlobalNavigator.current

    // Set navigation callback for coordinator
    LaunchedEffect(Unit) {
        coordinator.setNavigationCallback(onNavigateBack)
    }

    LaunchedEffect(state) {
        val a = state != AuthFlowState.Idle
        showContent = a
    }

    if (showContent) {
        when (state) {
            AuthFlowState.BiometryCheck -> {
                localNavigator.push(
                    ScreenRegistry.get(
                        SharedScreen.BiometryScreen(
                            onBiometryCallback = { success ->
                                coordinator.handleBiometryResult(
                                    if (success) AuthResult.Success else AuthResult.Failure
                                )
                                if (success) {
                                    // Pop Biometry screen and navigate back from SecureAuthScreen
                                    localNavigator.pop()
                                    onNavigateBack()
                                } else {
                                    // Only pop Biometry screen when failed
                                    localNavigator.pop()
                                }
                            },
                            onCancel = {
                                coordinator.handleBiometryResult(AuthResult.Cancel)
                                localNavigator.pop()
                                onNavigateBack()
                            }
                        )
                    )
                )
            }

            AuthFlowState.PinCheck -> {
                localNavigator.push(
                    ScreenRegistry.get(
                        SharedScreen.UnlockPinScreen(
                            unlockPinCase = SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                            antelopeAccountName = null,
                            unlockPinCallback = { success ->
                                if (!success) {
                                    // User cancelled - pop PIN screen and handle cancel
                                    localNavigator.pop()
                                    coordinator.handlePinResult(AuthResult.Cancel)
                                }
                                // If success, wait for onUnlockSuccess to be called
                            },
                            onUnlockSuccess = {
                                // Pop PIN screen first
                                localNavigator.pop()
                                // Handle the PIN result
                                coordinator.handlePinResult(AuthResult.Success)
                            }
                        )
                    )
                )
            }

            AuthFlowState.TwoFactorCheck -> {
                localNavigator.push(
                    ScreenRegistry.get(
                        SharedScreen.Unlock2FaScreen(
                            onUnlockSuccess = {
                                coordinator.handle2FAResult(AuthResult.Success)
                                // Pop 2FA screen and navigate back from SecureAuthScreen
                                localNavigator.pop()
                                onNavigateBack()
                            },
                            onUnlockCancelled = {
                                coordinator.handle2FAResult(AuthResult.Cancel)
                                localNavigator.pop()
                            }
                        )
                    )
                )
            }

            AuthFlowState.PinSetupRequired -> {
                localNavigator.push(
                    ScreenRegistry.get(
                        SharedScreen.SetupPinScreen(
                            pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_CONTINUE.toString(),
                            onPinSetupSuccess = {
                                localNavigator.popUntil { it is SecureAuthScreen }
                                coordinator.handlePinSetupResult(AuthResult.Success)
                            },
                            onPinSetupCancel = {
                                localNavigator.popUntil { it is SecureAuthScreen }
                                coordinator.handlePinSetupResult(AuthResult.Cancel)
                            }
                        )
                    )
                )
            }

            AuthFlowState.TwoFactorSetupRequired -> {
                val setUp2FAScreen = ScreenRegistry.get(
                    SharedScreen.TwoFactorAuthenticationSetupScreen(
                        onSuccess = {
                            coordinator.handle2FAResult(AuthResult.Success)
                            coordinator.enable2FAonSetting()
                            localGlobalNavigator.pop()
                        }
                    )
                )
                localGlobalNavigator.push(
                    ScreenRegistry.get(
                        SharedScreen.TwoFactorSetupRequiredScreen(
                            onCancel = {
                                coordinator.handle2FAResult(AuthResult.Cancel)
                                localNavigator.pop()
                            },
                            onSetup2Fa = {
                                localGlobalNavigator.push(setUp2FAScreen)
                            },
                            onFallbackToPin = {
                                // For 2FA fallback, use the standard PIN screen
                                localGlobalNavigator.push(
                                    ScreenRegistry.get(
                                        SharedScreen.UnlockPinScreen(
                                            unlockPinCase = SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                                            antelopeAccountName = null,
                                            onUnlockSuccess = {
                                                coordinator.handlePinResult(AuthResult.Success)
                                            }
                                        )
                                    )
                                )
                            }
                        )
                    )
                )
            }

            AuthFlowState.Completed -> {
                localNavigator.popUntil { it is SecureAuthScreen }
                coordinator.completeAuthFlow(true)
            }

            else -> {
                // Fail-secure: undefined states should deny access
                Napier.w("SecureAuthFlow: Unexpected state: $state - denying access")
                localNavigator.popUntil { it is SecureAuthScreen }
                coordinator.completeAuthFlow(false) // Changed from true to false
            }
        }
    }
}