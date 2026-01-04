package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.ClearText
import com.mangala.wallet.features.addressbook.icon.contacticon.DeleteButton
import com.mangala.wallet.features.addressbook.icon.contacticon.DropDown
import com.mangala.wallet.features.addressbook.utils.DateUtils
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime

/**
 * Reusable text field component with optional label dropdown and trailing clear/delete icons
 * Updated to match the new Figma design in the image with Phone number field
 * Added support for date picker when field is of type important date
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContactTextField(
    value: String,
    onValueChange: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    labelOptions: List<String>? = null,
    selectedLabel: String? = null,
    onLabelChange: ((String) -> Unit)? = null,
    onClearText: (() -> Unit)? = null,
    onDeleteField: (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    fieldTitle: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    icon: ImageVector? = null,
    isDateField: Boolean = false,
    onDateSelected: ((Instant) -> Unit)? = null,
    // ✅ ADD: Error display support
    isError: Boolean = false,
    errorMessage: String? = null,
    // Add total field count to determine if delete icon should be shown
    totalFieldCount: Int = 1,
    // ✅ ADD: Support for ImportantDate display
    importantDate: com.mangala.wallet.features.addressbook.domain.model.ImportantDate? = null,
    // Add keyboard type parameter
    keyboardType: KeyboardType = KeyboardType.Text,
    // Focus state management for AC-12.3, AC-12.5
    isFocused: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    var localIsFocused by remember { mutableStateOf(false) }
    var isLongPressed by remember { mutableStateOf(false) }
    var isLabelDropdownExpanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Date picker is now handled by CalendarBottomSheet

    // Reset long press state when focus changes
    LaunchedEffect(localIsFocused) {
        if (!localIsFocused) {
            isLongPressed = false
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Field title row with icon
        if (fieldTitle != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.mangalaColors.iconPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = fieldTitle,
                    style = MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                )
            }
        }

        // Layout for label and text field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top // Align tops of both columns
        ) {
            // Label dropdown on the left with fixed height
            if (labelOptions != null && selectedLabel != null) {
                Box {
                    Row(
                        modifier = Modifier
                            .height(20.dp)
                            .clickable { 
                                // For date fields with Solar/Lunar labels, trigger date selection
                                if (isDateField && (selectedLabel == "Solar" || selectedLabel == "Lunar")) {
                                    onDateSelected?.invoke(kotlinx.datetime.Clock.System.now())
                                } else {
                                    isLabelDropdownExpanded = true
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedLabel,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = MaterialTheme.mangalaColors.textSecondary,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Icon(
                            imageVector = ContactIcon.DropDown,
                            contentDescription = "Select label",
                            tint = MaterialTheme.mangalaColors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        DropdownMenu(
                            expanded = isLabelDropdownExpanded,
                            onDismissRequest = { isLabelDropdownExpanded = false }
                        ) {
                            labelOptions.forEach { labelOption ->
                                DropdownMenuItem(
                                    text = { Text(labelOption) },
                                    onClick = {
                                        onLabelChange?.invoke(labelOption)
                                        isLabelDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
            }

            // Text field section
            Column(modifier = Modifier.weight(1f)) {
                // Input row with fixed height for vertical alignment
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                ) {
                    if (isDateField) {
                        // Date field content
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    // Call the date selected callback directly for calendar bottom sheet
                                    onDateSelected?.invoke(kotlinx.datetime.Clock.System.now())
                                },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            // ✅ SIMPLE: Display date in simple format based on calendar type
                            val displayValue = when {
                                importantDate != null -> {
                                    // Display date based on calendar type

                                    when (importantDate.calendarType) {
                                        com.mangala.wallet.features.addressbook.domain.model.CalendarType.SOLAR -> {
                                            // Simple solar date: "15/4/2025"
                                            val date = importantDate.date
                                            "${date.dayOfMonth}/${date.monthNumber}/${date.year}"
                                        }

                                        com.mangala.wallet.features.addressbook.domain.model.CalendarType.LUNAR -> {
                                            // Simple lunar date: "15/4/2025" (lunar format)
                                            val lunarDate = importantDate.lunarDate
                                            if (lunarDate != null) {
                                                "${lunarDate.day}/${lunarDate.month}/${lunarDate.year}"
                                            } else {
                                                // Fallback to solar if lunar not available
                                                val date = importantDate.date
                                                "${date.dayOfMonth}/${date.monthNumber}/${date.year}"
                                            }
                                        }
                                    }
                                }

                                value.isNotEmpty() && DateUtils.stringToInstant(value) != null -> {
                                    // Simple format for existing dates
                                    val instant = DateUtils.stringToInstant(value)!!
                                    val date = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
                                    "${date.dayOfMonth}/${date.monthNumber}/${date.year}"
                                }

                                else -> value
                            }

                            Text(
                                text = displayValue.ifEmpty { placeholder },
                                style = TextStyle(
                                    color = MaterialTheme.mangalaColors.textSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 20.sp
                                ),
                                maxLines = 1
                            )
                        }

                        // AC-12.5, AC-12.6: Delete icon for date field (always show when not focused since date fields aren't editable)
                        if (onDeleteField != null && totalFieldCount > 1) {
                            IconButton(
                                onClick = {
                                    onDeleteField()
                                    keyboardController?.hide()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = ContactIcon.DeleteButton,
                                    contentDescription = "Remove field",
                                    tint = Color(0xFFFF3B30),
                                    modifier = Modifier.size(Spacing.SMALL)
                                )
                            }
                        }
                    } else {
                        if (onValueChange != null) {
                            // Text input field
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (value.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        style = TextStyle(
                                            color = MaterialTheme.mangalaColors.textSecondary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            lineHeight = 20.sp
                                        ),
                                        maxLines = 1
                                    )
                                }

                                BasicTextField(
                                    value = value,
                                    onValueChange = onValueChange,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .onFocusChanged { 
                                            localIsFocused = it.isFocused
                                            onFocusChanged(it.isFocused)
                                        },
                                    textStyle = TextStyle(
                                        color = MaterialTheme.mangalaColors.textSecondary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        lineHeight = 20.sp
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                                    singleLine = true,
                                    cursorBrush = SolidColor(ColorsNew.primary_500),
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier.fillMaxHeight(),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            innerTextField()
                                        }
                                    }
                                )
                            }

                            // Trailing icons
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // AC-12.3, AC-12.5: Show X icon when focused, trash icon when not focused
                                if (localIsFocused && onClearText != null && value.isNotEmpty()) {
                                    // Clear icon when focused and has content
                                    IconButton(
                                        onClick = {
                                            onClearText()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = ContactIcon.ClearText,
                                            contentDescription = "Clear text",
                                            tint = ColorsNew.white,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(RoundedCornerShape(9.dp))
                                                .background(color = ColorsNew.primary_200) // Reduced icon size
                                                .padding(2.dp)// Clip the background
                                        )
                                    }
                                } else if (!localIsFocused && onDeleteField != null && totalFieldCount > 1) {
                                    // AC-12.5, AC-12.6: Delete icon when not focused and multiple fields exist
                                    IconButton(
                                        onClick = {
                                            onDeleteField()
                                            keyboardController?.hide()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = ContactIcon.DeleteButton,
                                            contentDescription = "Remove field",
                                            tint = Color(0xFFFF3B30),
                                            modifier = Modifier.size(Spacing.MEDIUM)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Divider only under the input field
                Spacer(modifier = Modifier.height(12.dp)) // Space before divider

                Divider(
                    color = if (isError) Color(0xFFFF3B30) else MaterialTheme.mangalaColors.bgButton,
                    thickness = if (isError) 1.dp else 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                // ✅ Error message display
                if (isError && !errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        style = MangalaTypography.Size12Regular(),
                        color = Color(0xFFFF3B30),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }

    // Date picker dialog is now replaced by CalendarBottomSheet
    // The onDateSelected callback will trigger the calendar bottom sheet instead
}