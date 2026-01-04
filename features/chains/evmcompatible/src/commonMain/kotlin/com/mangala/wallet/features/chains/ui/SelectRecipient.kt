package com.mangala.wallet.features.chains.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Search
import com.mangala.wallet.ui.TextTitle4
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun SelectRecipientType(
    contactFilter: String,
    onContactFilterChange: (String) -> Unit,
    onClearContactFilter: () -> Unit,
    onAddNewRecipient: () -> Unit
) {
    NewRecipientButton(onAddNewRecipient)

    Spacer(modifier = Modifier.height(Spacing.SMALL))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextTitle4(
            text = MR.strings.label_select_recipient_or.desc().localized(),
        )
        MaterialTheme(
            colors = MaterialTheme.colors.copy(primary = MaterialTheme.colors.onSecondary)
        ) {
            TextField(contactFilter,
                onValueChange = {
                    onContactFilterChange(it)
                },
                placeholder = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = MR.strings.label_select_recipient_saved_recipient.desc().localized(),
                            color = MaterialTheme.colors.secondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = FontType.TITLE_3,
                            fontFamily = fontFamilyResource(MR.fonts.sfpro)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(20.dp)
                        ) {
                            val color =
                                MaterialTheme.colors.secondary.copy(alpha = 0.9f)
//                                        color.alpha = 0.5f
                            Icon(
                                MangalaWalletPack.Search,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                textStyle = TextStyle(
                    color = MaterialTheme.colors.onSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = FontType.TITLE_3,
                    fontFamily = fontFamilyResource(MR.fonts.sfpro)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
//                                    keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .onFocusChanged { focusState ->
//                                    if (focusState.isFocused) {
//                                        if (doneSelectNetwork) {
//                                            viewModel.doneSelectNetwork.value = false
//                                            viewModel.showNetworkList.value = true
//                                        }
//                                    }
//                                    isNetworkFocus.value = focusState.isFocused
                    }
                    .fillMaxWidth()
//                                .focusRequester(networkFocusRequester)
                ,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    if (contactFilter.isNotEmpty()) {
//                                    if (isNetworkFocus.value) {
                        IconButton(
                            onClick = {
                                onClearContactFilter()
                            },
                            modifier = Modifier.size(Dimensions.IconButtonSize)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null,
                                tint = MaterialTheme.colors.onSecondary,
                                modifier = Modifier.size(Dimensions.IconSize)
                            )
                        }
                    }
//                                }
                }
            )
        }
    }
}

@Composable
private fun NewRecipientButton(onAddNewRecipient: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable {
                onAddNewRecipient()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextTitle4(
            text = MR.strings.all_send_new_recipient.desc().localized(),
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
            onClick = { onAddNewRecipient() },
            modifier = Modifier.size(20.dp)
        ) {
            val color =
                MaterialTheme.colors.secondary.copy(alpha = 0.9f)
            Icon(
                Icons.Default.Create,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}