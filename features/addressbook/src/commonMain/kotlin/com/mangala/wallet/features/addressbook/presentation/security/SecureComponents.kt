package com.mangala.wallet.features.addressbook.presentation.security

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

/**
 * A composable that wraps any content with secure authentication.
 * When clicked, it will trigger the secure authentication flow.
 *
 * @param actionId The ID of the secure action to execute
 * @param onClick The action to perform when authentication succeeds
 * @param onCancel The action to perform when authentication is canceled
 * @param modifier The modifier to apply to this layout
 * @param content The content to display
 */
@Composable
fun SecureAction(
    actionId: SecureActionId,
    onClick: () -> Unit,
    onCancel: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val secureActionHandler = SecureAuthProvider.current

    Box(
        modifier = modifier.clickable {
            secureActionHandler.runSecureActionForId(
                actionId = actionId,
                onSuccess = onClick,
                onCancel = onCancel
            )
        }
    ) {
        content()
    }
}

/**
 * A button that triggers secure authentication when clicked.
 * This combines a standard Button with secure authentication flow.
 *
 * @param actionId The ID of the secure action to execute
 * @param onClick The action to perform when authentication succeeds
 * @param onCancel The action to perform when authentication is canceled
 * @param modifier The modifier to apply to this layout
 * @param enabled Controls the enabled state of the button
 * @param shape The shape of the button
 * @param colors The colors to use for the button
 * @param contentPadding The padding to apply to the button content
 * @param content The content of the button
 */
@Composable
fun SecureButton(
    actionId: SecureActionId,
    onClick: () -> Unit,
    onCancel: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val secureActionHandler = SecureAuthProvider.current

    Button(
        onClick = {
            secureActionHandler.runSecureActionForId(
                actionId = actionId,
                onSuccess = onClick,
                onCancel = onCancel
            )
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        content = content
    )
}