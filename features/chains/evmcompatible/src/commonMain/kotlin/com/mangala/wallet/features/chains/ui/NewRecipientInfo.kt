package com.mangala.wallet.features.chains.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.BasicTextFieldWithHintAndTrailingIcons
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun NewRecipientInfo(
    selectedName: String?,
    onNewRecipientNameChange: (String) -> Unit,
    onDoneSelectName: (Boolean) -> Unit,
    focusManager: FocusManager,
    doneSelectName: Boolean,
    nameFocusRequester: FocusRequester,
) {
    val isClearButtonVisible = remember(selectedName) {
        derivedStateOf { selectedName?.isNotEmpty() == true }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)
    ) {

        TextNormal(
            MR.strings.label_select_recipient_contact_name.desc().localized(),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.mangalaColors.textPrimary
        )
        HorizontalSpacer(Spacing.TINY)
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(primary = MaterialTheme.mangalaColors.iconPrimary)
        ) {
            BasicTextFieldWithHintAndTrailingIcons(
                selectedName ?: "",
                onValueChange = (onNewRecipientNameChange),
                hint = MR.strings.label_select_recipient_contact_name_placeholder.desc()
                    .localized(),
                hintColor = MaterialTheme.mangalaColors.textSecondary,
                textColor = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = FontType.REGULAR,
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
//                        keyboardController?.hide()
                        onDoneSelectName(true)
                        focusManager.clearFocus()
                    }
                ),
                textFieldModifier = Modifier
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            if (doneSelectName) {
                                onDoneSelectName(false)
                            }
                        }
                    }
                    .fillMaxWidth()
                    .focusRequester(nameFocusRequester),
                boxModifier = Modifier.padding(vertical = Spacing.SMALL),
                trailingIcon = {
                    if (isClearButtonVisible.value) {
                        IconButton(
                            onClick = { onNewRecipientNameChange("") },
                            modifier = Modifier.size(Dimensions.IconButtonSize)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null,
                                tint = MaterialTheme.mangalaColors.iconPrimary,
                                modifier = Modifier.size(Dimensions.IconSize)
                            )
                        }
                    }
                }
            )
        }
    }
}