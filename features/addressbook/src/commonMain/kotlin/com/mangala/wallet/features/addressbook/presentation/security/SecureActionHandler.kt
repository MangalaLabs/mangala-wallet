package com.mangala.wallet.features.addressbook.presentation.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SecureActionHandler(
    private val coordinator: SecureAuthFlowCoordinator,
    private val navigator: Navigator,
    private val policyProvider: SecureAuthPolicyProvider,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {

    /**
     * Runs a secure action with automatic policy retrieval.
     * This simplifies the calling code by handling policy retrieval internally.
     *
     * @param actionId The ID of the secure action to run
     * @param onSuccess Callback when authentication succeeds
     * @param onCancel Callback when authentication is canceled
     */
    fun runSecureActionForId(
        actionId: SecureActionId,
        onSuccess: () -> Unit,
        onCancel: () -> Unit = {}
    ) {
        val policy = policyProvider.getPolicyFor(actionId)
        runSecureAction(
            actionId = actionId,
            type = policy,
            onSuccess = onSuccess,
            onCancel = onCancel
        )
    }

    /**
     * Thực thi hành động có yêu cầu xác thực
     *
     * @param actionId ID của hành động cần xác thực
     * @param type Loại xác thực yêu cầu
     * @param onSuccess Callback khi xác thực thành công
     * @param onCancel Callback khi xác thực bị hủy
     */
    fun runSecureAction(
        actionId: SecureActionId,
        type: SecureActionType,
        onSuccess: () -> Unit,
        onCancel: () -> Unit = {}
    ) {
        println("SecureActionHandler: runSecureAction() called with actionId: $actionId, type: $type")
        println("SecureActionHandler: onSuccess callback: $onSuccess")

        coroutineScope.launch {
            when (type) {
                SecureActionType.None -> {
                    // Không cần xác thực
                    println("SecureActionHandler: No auth required, calling onSuccess directly")
                    onSuccess()
                }
                else -> {
                    // Check if authentication is already in progress
                    if (coordinator.currentState.value != AuthFlowState.Idle) {
                        println("SecureActionHandler: Authentication already in progress, ignoring")
                        return@launch
                    }
                    
                    println("SecureActionHandler: Navigating to secure auth screen")
                    // Điều hướng đến flow xác thực
                    navigator.navigateToSecureAuth()

                    // Bắt đầu flow xác thực
                    coordinator.startAuthFlow(
                        actionId = actionId,
                        type = type,
                        onSuccess = onSuccess,
                        onCancel = onCancel
                    )
                }
            }
        }
    }
}

/**
 * Composable Factory cho SecureActionHandler
 */
@Composable
fun rememberSecureActionHandler(
    coordinator: SecureAuthFlowCoordinator,
    navigator: Navigator,
    policyProvider: SecureAuthPolicyProvider
): SecureActionHandler {
    return remember(coordinator, navigator) {
        SecureActionHandler(
            coordinator = coordinator,
            navigator = navigator,
            policyProvider = policyProvider
        )
    }
}