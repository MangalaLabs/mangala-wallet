package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.theme.MangalaTypography
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProposalAutoCompleteTextInputField(
    label: String,
    query: String,
    onQueryChange: (value: String, isChoosingFromSuggestion: Boolean) -> Unit,
    placeholder: String,
    requiredInput: Boolean = true,
    enabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    suggestions: List<String>,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    ),
    isError: Boolean = false,
    errorText: String? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isDropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = isDropdownExpanded,
        onExpandedChange = { expanded ->
            isDropdownExpanded = expanded
        }
    ) {
        ProposalTextInputField(
            label = label,
            query = query,
            onQueryChange = {
                onQueryChange(it, false)
                isDropdownExpanded = it.isNotEmpty()
            },
            placeholder = placeholder,
            requiredInput = requiredInput,
            enabled = enabled,
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth().onFocusChanged {
                if (!it.isFocused) {
                    isDropdownExpanded = false
                }
            },
            keyboardOptions = keyboardOptions,
            isError = isError,
            errorText = errorText
        )

        if (suggestions.isNotEmpty()) {
            DropdownMenu(
                modifier = Modifier
                    .exposedDropdownSize()
                    .background(MaterialTheme.mangalaColors.bgInnerCard),
                expanded = isDropdownExpanded,
                onDismissRequest = {
                    isDropdownExpanded = false
                },
                properties = PopupProperties(focusable = false)
            ) {
                suggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { 
                            Text(
                                text = suggestion,
                                color = MaterialTheme.mangalaColors.textPrimary,
                                style = MangalaTypography.Size13Medium()
                            ) 
                        },
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onQueryChange(suggestion, true)
                            isDropdownExpanded = false
                        },
                        modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                    )
                }
            }
        }
    }
}