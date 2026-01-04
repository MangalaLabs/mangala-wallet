package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.features.addressbook.presentation.contact.model.ImportantDateUiState

/**
 * Section for contact information including name, tags, emails, phones, etc.
 * Updated to match Figma design with collapsible sections
 */
import androidx.compose.material3.Surface
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRightNew
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.Email
import com.mangala.wallet.features.addressbook.icon.contacticon.ImpotantDate
import com.mangala.wallet.features.addressbook.icon.contacticon.Location
import com.mangala.wallet.features.addressbook.icon.contacticon.Phone
import com.mangala.wallet.features.addressbook.icon.contacticon.Social
import com.mangala.wallet.features.addressbook.icon.contacticon.TagUser
import com.mangala.wallet.features.addressbook.utils.DateUtils
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.common.mokoresources.Spacing
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun ContactInfoSection(
    // Email fields
    emailFields: List<Pair<String, String>>, // (label, value) pairs
    onEmailAdd: () -> Unit,
    onEmailChange: (Int, String, String) -> Unit, // (index, label, value)
    onEmailRemove: (Int) -> Unit,

    // Phone fields
    phoneFields: List<Pair<String, String>>, // (label, value) pairs
    onPhoneAdd: () -> Unit,
    onPhoneChange: (Int, String, String) -> Unit, // (index, label, value)
    onPhoneRemove: (Int) -> Unit,

    addressFields: List<Pair<String, String>>,
    onAddressChange: (Int, String, String) -> Unit,
    onAddressAdd: () -> Unit,
    onAddressRemove: (Int) -> Unit,

    // Nickname fields - Figma sync: Add nickname support
    nicknameFields: List<Pair<String, String>> = emptyList(), // (label, value) pairs
    onNicknameAdd: () -> Unit = {},
    onNicknameChange: (Int, String, String) -> Unit = { _, _, _ -> },
    onNicknameRemove: (Int) -> Unit = {},

    importantDateFields: List<ImportantDateUiState>,
    onImportantDateChange: (Int, Instant, String) -> Unit,
    onImportantDateAdd: () -> Unit,
    onImportantDateRemove: (Int) -> Unit,
    onImportantDateClick: ((Int) -> Unit)? = null,
    // ✅ ADD: Pass ImportantDate objects for proper calendar type display
    importantDates: List<com.mangala.wallet.features.addressbook.domain.model.ImportantDate> = emptyList(),

    socialField: List<Pair<String, String>>,
    onSocialChange: (Int, String, String) -> Unit,
    onSocialAdd: (() -> Unit)? = null,
    onSocialRemove: (Int) -> Unit,

    // Theo thiết kế Figma, phần contact info có thể mở rộng hoặc thu gọn
    expanded: Boolean = false,
    onToggleExpand: () -> Unit = {},

    // Flexible divider control - set to true for the last section to hide divider
    isLastSection: Boolean = false,

    // ✅ ADD: Validation errors for error display
    validationErrors: Map<String, String> = emptyMap(),

    // ✅ ADD: Modified fields for divider color detection
    modifiedFields: Set<String> = emptySet(),

    // Add flag to determine if this is a new contact or edit
    isNewContact: Boolean = true,

    // Focus state management for AC-12.3, AC-12.5, AC-12.6
    focusedEmailIndex: Int? = null,
    focusedPhoneIndex: Int? = null,
    focusedAddressIndex: Int? = null,
    onEmailFocusChanged: (Int?) -> Unit = {},
    onPhoneFocusChanged: (Int?) -> Unit = {},
    onAddressFocusChanged: (Int?) -> Unit = {}
) {
    val emailLabels = listOf("Home", "Work", "Other")
    val phoneLabels = listOf("Mobile", "Home", "Work", "Other")
    val addressLabels = listOf("Home", "Company", "Other")
    val socialLabels = listOf("Facebook", "Twitter", "Instagram", "LinkedIn", "Other")
    val calendarLabels = listOf("Solar", "Lunar")
    val nicknameLabels = listOf("Nickname") // Single option as per design

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.SMALL, vertical = Spacing.XSMALL),
        color = MaterialTheme.mangalaColors.bgInnerCard,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 0.dp // Remove bottom padding, handle it per section
                )
            // Removed spacedBy to control spacing manually
        ) {
            // Tiêu đề có thể mở rộng/thu gọn - theo thiết kế Figma
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Contact information",
                    style = MangalaTypography.Size14Medium(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                )

                Icon(
                    imageVector = MangalaWalletPack.ArrowRightNew,
                    contentDescription = null,
                    tint = MaterialTheme.mangalaColors.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(12.dp)) // Space after header
            // Phần nội dung chỉ hiển thị khi đã mở rộng
            AnimatedVisibility(visible = expanded) {
                Spacer(modifier = Modifier.height(4.dp)) // Space after header

                Column {
                    // Phone section
                    Column {
                        if (phoneFields.isEmpty() && isNewContact) {
                            // Show empty field only in create mode
                            ContactTextField(
                                value = "",
                                onValueChange = { }, // No-op for read-only placeholder
                                placeholder = "Enter phone number",
                                labelOptions = phoneLabels,
                                selectedLabel = "Mobile",
                                onLabelChange = { }, // No-op for read-only placeholder
                                onClearText = { }, // No-op for read-only placeholder
                                onDeleteField = null,
                                fieldTitle = "Phone number",
                                icon = ContactIcon.Phone,
                                isError = false,
                                errorMessage = null,
                                totalFieldCount = 0,
                                keyboardType = KeyboardType.Phone,
                                isFocused = false,
                                onFocusChanged = { focused ->
                                    if (focused) {
                                        onPhoneAdd()
                                    }
                                }
                            )
                        } else {
                            // Show existing phones
                            phoneFields.forEachIndexed { index, (label, phone) ->
                                if (index > 0) {
                                    Spacer(modifier = Modifier.height(12.dp)) // 12dp from previous field's divider
                                }
                                ContactTextField(
                                    value = phone,
                                    onValueChange = { newPhone ->
                                        onPhoneChange(
                                            index,
                                            label,
                                            newPhone
                                        )
                                    },
                                    placeholder = "Enter phone number",
                                    labelOptions = phoneLabels,
                                    selectedLabel = label,
                                    onLabelChange = { newLabel ->
                                        onPhoneChange(
                                            index,
                                            newLabel,
                                            phone
                                        )
                                    },
                                    onClearText = { onPhoneChange(index, label, "") },
                                    onDeleteField = { onPhoneRemove(index) },
                                    fieldTitle = if (index == 0) "Phone number" else null,
                                    icon = ContactIcon.Phone,
                                    isError = validationErrors.containsKey("phone_$index"),
                                    errorMessage = validationErrors["phone_$index"],
                                    totalFieldCount = phoneFields.size,
                                    keyboardType = KeyboardType.Phone, // AC-11.1: Phone Number Keyboard
                                    isFocused = focusedPhoneIndex == index,
                                    onFocusChanged = { focused ->
                                        onPhoneFocusChanged(if (focused) index else null)
                                    }
                                )
                            }
                        }

                        AddAnotherButton(
                            text = if (phoneFields.isEmpty() && isNewContact) "Add phone number" else "Add other phone number",
                            onClick = onPhoneAdd
                        )

                        Divider(
                            color = MaterialTheme.mangalaColors.bgButton,
                            thickness = 0.5.dp
                        )

                        Spacer(modifier = Modifier.height(Spacing.SMALL)) // 16dp from divider to next section

                        // Email section
                        Column {
                            if (emailFields.isEmpty() && isNewContact) {
                                // Show empty field only in create mode
                                ContactTextField(
                                    value = "",
                                    onValueChange = { }, // No-op for read-only placeholder
                                    placeholder = "Enter email address",
                                    labelOptions = emailLabels,
                                    selectedLabel = "Home",
                                    onLabelChange = { }, // No-op for read-only placeholder
                                    onClearText = { }, // No-op for read-only placeholder
                                    onDeleteField = null,
                                    fieldTitle = "Email Address",
                                    icon = ContactIcon.Email,
                                    isError = false,
                                    errorMessage = null,
                                    totalFieldCount = 0,
                                    keyboardType = KeyboardType.Email,
                                    isFocused = false,
                                    onFocusChanged = { focused ->
                                        if (focused) {
                                            onEmailAdd()
                                        }
                                    }
                                )
                            } else {
                                // Show existing emails
                                emailFields.forEachIndexed { index, (label, email) ->
                                    if (index > 0) {
                                        Spacer(modifier = Modifier.height(12.dp)) // 12dp from previous field's divider
                                    }
                                    ContactTextField(
                                        value = email,
                                        onValueChange = { newEmail ->
                                            onEmailChange(
                                                index,
                                                label,
                                                newEmail
                                            )
                                        },
                                        placeholder = "Enter email address",
                                        labelOptions = emailLabels,
                                        selectedLabel = label,
                                        onLabelChange = { newLabel ->
                                            onEmailChange(
                                                index,
                                                newLabel,
                                                email
                                            )
                                        },
                                        onClearText = { onEmailChange(index, label, "") },
                                        onDeleteField = { onEmailRemove(index) },
                                        fieldTitle = if (index == 0) "Email Address" else null,
                                        icon = ContactIcon.Email,
                                        isError = validationErrors.containsKey("email_$index"),
                                        errorMessage = validationErrors["email_$index"],
                                        totalFieldCount = emailFields.size,
                                        keyboardType = KeyboardType.Email, // AC-11.2: Email Keyboard
                                        isFocused = focusedEmailIndex == index,
                                        onFocusChanged = { focused ->
                                            onEmailFocusChanged(if (focused) index else null)
                                        }
                                    )
                                }
                            }

                            AddAnotherButton(
                                text = if (emailFields.isEmpty() && isNewContact) "Add email" else "Add other email",
                                onClick = onEmailAdd
                            )

                            Divider(
                                color = MaterialTheme.mangalaColors.bgButton,
                                thickness = 0.5.dp
                            )

                            Spacer(modifier = Modifier.height(Spacing.SMALL)) // 16dp from divider to next section

                            // Address field
                            Column {
                                if (addressFields.isEmpty() && isNewContact) {
                                    // Show empty field only in create mode
                                    ContactTextField(
                                        value = "",
                                        onValueChange = { }, // No-op for read-only placeholder
                                        placeholder = "Enter physical address",
                                        labelOptions = addressLabels,
                                        selectedLabel = "Home",
                                        onLabelChange = { }, // No-op for read-only placeholder
                                        onClearText = { }, // No-op for read-only placeholder
                                        onDeleteField = null,
                                        fieldTitle = "Physical Address",
                                        icon = ContactIcon.Location,
                                        isError = false,
                                        errorMessage = null,
                                        totalFieldCount = 0,
                                        isFocused = false,
                                        onFocusChanged = { focused ->
                                            if (focused) {
                                                onAddressAdd()
                                            }
                                        }
                                    )
                                } else {
                                    // Show existing addresses
                                    addressFields.forEachIndexed { index, (label, address) ->
                                        if (index > 0) {
                                            Spacer(modifier = Modifier.height(12.dp)) // 12dp from previous field's divider
                                        }
                                        ContactTextField(
                                            value = address,
                                            onValueChange = { newAddress ->
                                                onAddressChange(
                                                    index,
                                                    label,
                                                    newAddress
                                                )
                                            },
                                            placeholder = "Enter physical address",
                                            labelOptions = addressLabels,
                                            selectedLabel = label,
                                            onLabelChange = { newLabel ->
                                                onAddressChange(
                                                    index,
                                                    newLabel,
                                                    address
                                                )
                                            },
                                            onClearText = { onAddressChange(index, label, "") },
                                            onDeleteField = { onAddressRemove(index) },
                                            fieldTitle = if (index == 0) "Physical Address" else null,
                                            icon = ContactIcon.Location,
                                            isError = validationErrors.containsKey("address_$index"),
                                            errorMessage = validationErrors["address_$index"],
                                            totalFieldCount = addressFields.size,
                                            isFocused = focusedAddressIndex == index,
                                            onFocusChanged = { focused ->
                                                onAddressFocusChanged(if (focused) index else null)
                                            }
                                        )
                                    }
                                }

                                AddAnotherButton(
                                    text = if (addressFields.isEmpty() && isNewContact) "Add address" else "Add other address",
                                    onClick = onAddressAdd
                                )

                                Divider(
                                    color = MaterialTheme.mangalaColors.bgButton,
                                    thickness = 0.5.dp
                                )

                                Spacer(modifier = Modifier.height(Spacing.SMALL)) // 16dp from divider to next section

                                // Nickname field - Figma sync: Add between Address and Important date
                                Column {
                                    if (nicknameFields.isEmpty() && isNewContact) {
                                        // Show empty field only in create mode
                                        ContactTextField(
                                            value = "",
                                            onValueChange = { }, // No-op for read-only placeholder
                                            placeholder = "Enter nickname",
                                            labelOptions = nicknameLabels,
                                            selectedLabel = "Nickname",
                                            onLabelChange = { }, // No-op for read-only placeholder
                                            onClearText = { }, // No-op for read-only placeholder
                                            onDeleteField = null,
                                            fieldTitle = "Nickname",
                                            icon = ContactIcon.TagUser,
                                            isError = false,
                                            errorMessage = null,
                                            totalFieldCount = 0,
                                            isFocused = false,
                                            onFocusChanged = { focused ->
                                                if (focused) {
                                                    onNicknameAdd()
                                                }
                                            }
                                        )
                                    } else {
                                        // Show existing nicknames
                                        nicknameFields.forEachIndexed { index, (_, nickname) ->
                                            if (index > 0) {
                                                Spacer(modifier = Modifier.height(12.dp)) // 12dp from previous field's divider
                                            }
                                            ContactTextField(
                                                value = nickname,
                                                onValueChange = { newNickname ->
                                                    onNicknameChange(
                                                        index,
                                                        "Nickname",
                                                        newNickname
                                                    )
                                                },
                                                placeholder = "Enter nickname",
                                                labelOptions = nicknameLabels,
                                                selectedLabel = "Nickname",
                                                onLabelChange = { /* No-op for single option */ },
                                                onClearText = {
                                                    onNicknameChange(
                                                        index,
                                                        "Nickname",
                                                        ""
                                                    )
                                                },
                                                onDeleteField = { onNicknameRemove(index) },
                                                fieldTitle = if (index == 0) "Nickname" else null,
                                                icon = ContactIcon.TagUser,
                                                isError = validationErrors.containsKey("nickname_$index"),
                                                errorMessage = validationErrors["nickname_$index"],
                                                totalFieldCount = nicknameFields.size
                                            )
                                        }
                                    }

                                    AddAnotherButton(
                                        text = if (nicknameFields.isEmpty() && isNewContact) "Add nickname" else "Add other name",
                                        onClick = onNicknameAdd
                                    )

                                    Divider(
                                        color = MaterialTheme.mangalaColors.bgButton,
                                        thickness = 0.5.dp
                                    )

                                    Spacer(modifier = Modifier.height(Spacing.SMALL)) // 16dp from divider to next section

                                    // Important date field
                                    Column {
                                        if (importantDateFields.isEmpty()) {
                                            // Show empty field when no dates exist (both create and edit mode)
                                            ContactTextField(
                                                value = "",
                                                onValueChange = null, // Date field is read-only
                                                placeholder = "Select a date",
                                                labelOptions = calendarLabels,
                                                selectedLabel = "Solar",
                                                onLabelChange = { }, // No-op for read-only placeholder
                                                onClearText = { }, // No-op for read-only placeholder
                                                onDeleteField = null,
                                                fieldTitle = "Important date",
                                                icon = ContactIcon.ImpotantDate,
                                                isDateField = true,
                                                onDateSelected = { 
                                                    onImportantDateAdd() // Trigger add date when clicked
                                                },
                                                totalFieldCount = 0,
                                                importantDate = null,
                                                isFocused = false,
                                                onFocusChanged = { } // Date field doesn't have focus events, using onDateSelected instead
                                            )
                                        } else {
                                            // Show existing important dates
                                            importantDateFields.forEachIndexed { index, date ->
                                                if (index > 0) {
                                                    Spacer(modifier = Modifier.height(12.dp)) // 12dp from previous field's divider
                                                }

                                                // ✅ ENHANCED: Get corresponding ImportantDate for proper calendar type display
                                                val correspondingImportantDate =
                                                    importantDates.getOrNull(index)

                                                // Map date field to corresponding ImportantDate

                                                ContactTextField(
                                                    value = date.date?.let {
                                                        DateUtils.instantToString(
                                                            it
                                                        )
                                                    } ?: "",
                                                    onValueChange = null, // Date field is read-only, use date picker
                                                    placeholder = "Select a date",
                                                    labelOptions = calendarLabels,
                                                    selectedLabel = date.description,
                                                    onLabelChange = { newLabel ->
                                                        // Allow label change even if date is not set yet
                                                        onImportantDateChange(
                                                            index,
                                                            date.date
                                                                ?: Clock.System.now(), // Use current date if no date is set
                                                            newLabel
                                                        )
                                                    },
                                                    onClearText = {
                                                        // Clear the date by setting it to current time with empty value
                                                        onImportantDateChange(
                                                            index,
                                                            Clock.System.now(),
                                                            date.description ?: ""
                                                        )
                                                        // Then remove it
                                                        onImportantDateRemove(index)
                                                    },
                                                    onDeleteField = { onImportantDateRemove(index) },
                                                    fieldTitle = if (index == 0) "Important date" else null,
                                                    icon = ContactIcon.ImpotantDate,
                                                    isDateField = true,
                                                    onDateSelected = { instant ->
                                                        // If we have a click handler, use that for calendar bottom sheet
                                                        if (onImportantDateClick != null) {
                                                            onImportantDateClick(index)
                                                        } else {
                                                            // Fall back to the original behavior
                                                            onImportantDateChange(
                                                                index,
                                                                instant,
                                                                date.description ?: ""
                                                            )
                                                        }
                                                    },
                                                    totalFieldCount = importantDateFields.size,
                                                    // ✅ ENHANCED: Pass ImportantDate for proper calendar type display
                                                    importantDate = correspondingImportantDate
                                                )
                                            }
                                        }

                                        // ✅ Allow adding dates even if there are empty fields (user may want multiple dates)
                                        AddAnotherButton(
                                            text = if (importantDateFields.isEmpty()) "Add date" else "Add other date",
                                            onClick = onImportantDateAdd // ✅ Always enable add button
                                        )

                                        Divider(
                                            color = MaterialTheme.mangalaColors.bgButton,
                                            thickness = 0.5.dp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(Spacing.SMALL)) // 16dp from divider to next section

                                    // Social profile field
                                    Column {
                                        if (socialField.isEmpty() && isNewContact) {
                                            // Show empty field only in create mode
                                            ContactTextField(
                                                value = "",
                                                onValueChange = { }, // No-op for read-only placeholder
                                                labelOptions = socialLabels,
                                                selectedLabel = "Facebook",
                                                onLabelChange = { }, // No-op for read-only placeholder
                                                placeholder = "https://tiktok.com/anhEm",
                                                onClearText = { }, // No-op for read-only placeholder
                                                onDeleteField = null,
                                                fieldTitle = "Social profile",
                                                icon = ContactIcon.Social,
                                                isError = false,
                                                errorMessage = null,
                                                totalFieldCount = 0,
                                                isFocused = false,
                                                onFocusChanged = { focused ->
                                                    if (focused && onSocialAdd != null) {
                                                        onSocialAdd()
                                                    }
                                                }
                                            )
                                        } else {
                                            // Show existing social profiles
                                            socialField.forEachIndexed { index, (label, url) ->
                                                if (index > 0) {
                                                    Spacer(modifier = Modifier.height(12.dp)) // 12dp from previous field's divider
                                                }
                                                ContactTextField(
                                                    value = url,
                                                    onValueChange = { newUrl ->
                                                        onSocialChange(
                                                            index,
                                                            label,
                                                            newUrl
                                                        )
                                                    },
                                                    labelOptions = socialLabels,
                                                    selectedLabel = label,
                                                    onLabelChange = { newLabel ->
                                                        onSocialChange(
                                                            index,
                                                            newLabel,
                                                            url
                                                        )
                                                    },
                                                    placeholder = "https://tiktok.com/anhEm",
                                                    onClearText = {
                                                        onSocialChange(
                                                            index,
                                                            label,
                                                            ""
                                                        )
                                                    },
                                                    onDeleteField = { onSocialRemove(index) },
                                                    fieldTitle = if (index == 0) "Social profile" else null,
                                                    icon = ContactIcon.Social,
                                                    isError = validationErrors.containsKey("social_$index"),
                                                    errorMessage = validationErrors["social_$index"],
                                                    totalFieldCount = socialField.size
                                                )
                                            }
                                        }
                                        if (onSocialAdd != null) {
                                            AddAnotherButton(
                                                text = if (socialField.isEmpty() && isNewContact) "Add social profile" else "Add other link",
                                                onClick = onSocialAdd
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}