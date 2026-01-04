package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.DocumentCopy
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.utils.ClipboardFactory
import org.koin.compose.koinInject

/**
 * Reusable DocumentCopy button component that handles clipboard operations internally.
 *
 * @param textToCopy The text to copy to clipboard
 * @param label Optional label for the clipboard content (default: "Text")
 * @param modifier Modifier for the IconButton
 * @param iconSize Size of the copy icon (default: Dimensions.IconSize)
 * @param iconTint Color of the copy icon (default: iconSecondary)
 * @param buttonSize Size of the clickable button area (default: 16.dp)
 * @param onCopyComplete Optional callback when copy operation completes
 * @param showFeedback Whether to show visual feedback (default: true)
 */
@Composable
fun DocumentCopyButton(
    textToCopy: String,
    label: String = "Text",
    modifier: Modifier = Modifier,
    iconSize: Dp = Dimensions.IconSize,
    iconTint: Color = MaterialTheme.mangalaColors.iconSecondary,
    buttonSize: Dp = Dimensions.IconButtonSize,
    onCopyComplete: (() -> Unit)? = null,
    clipboardFactory: ClipboardFactory = koinInject(),
) {
    IconButton(
        onClick = {
            // Copy to clipboard using ClipboardFactory
            clipboardFactory.copyText(label, textToCopy)
            // Call optional callback
            onCopyComplete?.invoke()
        },
        modifier = Modifier.size(buttonSize)
    ) {
        Icon(
            imageVector = ContactIcon.DocumentCopy,
            contentDescription = "Copy $label",
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}