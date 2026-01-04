package com.mangala.wallet.features.addressbook.presentation.privacy

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode

/**
 * A privacy-aware text component that automatically obfuscates cryptocurrency addresses
 * based on privacy settings and display mode.
 *
 * This component serves as a drop-in replacement for Text components that display cryptocurrency
 * addresses, automatically applying obfuscation based on privacy mode, display mode, and sensitivity flag.
 *
 * Features:
 * - Automatic obfuscation based on privacy settings
 * - Support for all standard Text component properties
 * - Centralized obfuscation logic for easy maintenance
 * - Performance optimized with caching through AddressObfuscator
 *
 * Behavior:
 * - If isSensitive = true: Always shows "••••••••" regardless of other settings
 * - If privacyModeEnabled = false: Shows full address regardless of displayMode
 * - If privacyModeEnabled = true: Applies obfuscation based on displayMode
 *   - FULL: EOS shows full address, others truncate to 6prefix••••4suffix
 *   - HIDDEN: EOS with subdomain shows ••••subdomain, others show ••••4suffix
 *   - SECRET: Always shows ••••••••
 *
 * @param address The cryptocurrency address to display
 * @param privacyModeEnabled Whether global privacy mode is currently active
 * @param modifier Modifier to be applied to the text
 * @param isSensitive Optional flag to mark this specific address as sensitive (defaults to false)
 * @param displayMode The display mode to use for obfuscation (defaults to HIDDEN)
 * @param style Text style configuration
 * @param color Text color (if not specified in style)
 * @param maxLines Maximum number of lines for the text (defaults to no limit)
 * @param overflow How to handle text overflow (defaults to Clip)
 * @param softWrap Whether text should wrap at soft line breaks (defaults to true)
 * @param minLines Minimum number of lines for the text (defaults to 1)
 *
 * Example usage:
 * ```
 * PrivacyAwareAddressText(
 *     address = "0x1234567890abcdef1234567890abcdef12345678",
 *     privacyModeEnabled = true,
 *     isSensitive = false,
 *     displayMode = DisplayMode.HIDDEN,
 *     style = MaterialTheme.typography.bodyMedium
 * )
 * ```
 */
@Composable
fun PrivacyAwareAddressText(
    address: String,
    privacyModeEnabled: Boolean,
    modifier: Modifier = Modifier,
    isSensitive: Boolean = false,
    privacyDisplayMode: DisplayMode = DisplayMode.FULL,
    style: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.MiddleEllipsis,
    softWrap: Boolean = true,
    minLines: Int = 1,
) {
    val displayAddress = AddressObfuscator.obfuscate(
        address = address,
        privacyDisplayMode = privacyDisplayMode,
        isSensitive = isSensitive,
        privacyModeEnabled = privacyModeEnabled
    )

    Text(
        text = displayAddress,
        modifier = modifier,
        style = style,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        softWrap = softWrap,
        minLines = minLines
    )
}