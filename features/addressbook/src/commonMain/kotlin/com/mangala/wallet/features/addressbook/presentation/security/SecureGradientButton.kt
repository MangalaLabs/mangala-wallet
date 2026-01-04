package com.mangala.wallet.features.addressbook.presentation.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.theme.MangalaTypography

/**
 * A gradient button that triggers secure authentication when clicked.
 * This combines MangalaGradientButton with secure authentication flow.
 *
 * @param actionId The ID of the secure action to execute
 * @param label The text to display on the button
 * @param onClick The action to perform when authentication succeeds
 * @param onCancel The action to perform when authentication is canceled
 * @param modifier The modifier to apply to this button
 * @param enabled Controls the enabled state of the button
 * @param size The size of the button
 * @param buttonStyle The style of the button
 * @param style The text style for the button label
 */
@Composable
fun SecureGradientButton(
    actionId: SecureActionId,
    label: String,
    onClick: () -> Unit,
    onCancel: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: MangalaButtonSize = MangalaButtonSize.Big,
    buttonStyle: MangalaButtonStyle = MangalaButtonStyle.GRADIENT,
    style: TextStyle = MangalaTypography.Size17SemiBold()
) {
    val secureActionHandler = SecureAuthProvider.current

    MangalaGradientButton(
        label = label,
        onClick = {
            secureActionHandler.runSecureActionForId(
                actionId = actionId,
                onSuccess = {
                    onClick()
                },
                onCancel = {
                    onCancel()
                }
            )
        },
        enabled = enabled,
        size = size,
        modifier = modifier,
        buttonStyle = buttonStyle,
        style = style
    )
}