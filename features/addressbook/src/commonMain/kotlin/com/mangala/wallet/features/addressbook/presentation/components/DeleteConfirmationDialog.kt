package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.compose.stringResource
import androidx.compose.foundation.Image
import dev.icerock.moko.resources.compose.painterResource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.draw.clip
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * A reusable delete confirmation dialog component matching Figma design
 * Figma: https://www.figma.com/design/5ALmNbdibAycbMy5m7F4N6/2025_Mangala-update?node-id=7806-22820
 *
 * @param title The title of the dialog (e.g., "Delete contact?", "Delete address?")
 * @param message The message to display (e.g., "Are you sure you want to delete this contact?")
 * @param confirmButtonText The text for the confirm button (default: "Delete")
 * @param cancelButtonText The text for the cancel button (default: "Cancel")
 * @param onConfirm Callback called when the user confirms the deletion
 * @param onDismiss Callback called when the user dismisses the dialog
 * @param showIcon Whether to show the delete icon (default: true)
 */
@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    confirmButtonText: String = stringResource(MR.strings.all_delete),
    cancelButtonText: String = stringResource(MR.strings.all_cancel),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    showIcon: Boolean = true,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Use 90% of screen width for better responsiveness
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.mangalaColors.bgInnerCard
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 32.dp,
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 24.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                if (showIcon) {
                    Box(
                        modifier = Modifier
                            .size(width = 140.dp, height = 104.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                if (isSystemInDarkTheme()) {
                                    MR.images.delete_confirmation_dark_mode
                                } else {
                                    MR.images.delete_confirmation
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Text Content Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.mangalaColors.textPrimary,
                            letterSpacing = (-0.17).sp, // -1% of 17sp
                            lineHeight = (17 * 1.4).sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Content/Description
                    Text(
                        text = message,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.mangalaColors.textPrimary,
                            letterSpacing = (-0.14).sp, // -1% of 14sp
                            lineHeight = (14 * 1.4).sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Button Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Delete Button (Destructive)
                    Button(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                            contentColor = MaterialTheme.mangalaColors.buttonDestructiveContent
                        ),
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(
                            horizontal = 24.dp,
                            vertical = 0.dp
                        )
                    ) {
                        Text(
                            text = confirmButtonText,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.mangalaColors.textPrimary,
                                letterSpacing = (-0.14).sp
                            )
                        )
                    }
                    
                    // Cancel Button (Outline)
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.mangalaColors.textPrimary
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.mangalaColors.border
                        ),
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(
                            horizontal = 24.dp,
                            vertical = 0.dp
                        )
                    ) {
                        Text(
                            text = cancelButtonText,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.mangalaColors.textPrimary,
                                letterSpacing = (-0.14).sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Convenience function for delete contact confirmation
 */
@Suppress("unused")
@Composable
fun DeleteContactConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    DeleteConfirmationDialog(
        title = stringResource(MR.strings.dialog_delete_contact_title),
        message = stringResource(MR.strings.dialog_delete_contact_message),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}