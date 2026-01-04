package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTitle3_34
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun InputSectionPercent(
    isLoading: Boolean,
    onValueChange: (String) -> Unit,
    onClickSuggestionInput: (RamSuggestionInputUiModel) -> Unit,
    amountValue: String,
    textTitleInput: String,
    listSuggestionInputUiModels: List<RamSuggestionInputUiModel>,
    textNativeToken: String,
    iconButtonSwap: @Composable () -> Unit,
    textNotify: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextNormal(
                text = textTitleInput,
                color = MaterialTheme.mangalaColors.textSecondary
            )

            iconButtonSwap()
        }

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        MaxWidthRow(
            modifier = Modifier
                .padding(horizontal = Dimensions.Padding.default)
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.Center
        ) {
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(primary = MaterialTheme.mangalaColors.iconPrimary)
            ) {
                BasicTextField(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .weight(5f, fill = false)
                        .width(IntrinsicSize.Min)
                        .defaultMinSize(minWidth = 0.dp)
                        .alignByBaseline(),
                    value = amountValue,
                    onValueChange = onValueChange,
                    textStyle = TextStyle.Default.copy(
                        color = MaterialTheme.mangalaColors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = FontType.TITLE_3_34,
                        fontFamily = getSfProFamilyFont(FontWeight.SemiBold)
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.mangalaColors.textPrimary),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = imeAction
                    ),
                    keyboardActions = keyboardActions,
                    decorationBox = { innerTextField ->
                        Box {
                            innerTextField()

                            if (amountValue.isBlank()) {
                                TextTitle3_34(
                                    text = "0",
                                    color = MaterialTheme.mangalaColors.textSecondary,
                                )
                            }
                        }
                    }
                )

                TextDescription2(
                    text = textNativeToken,
                    fontSize = FontType.TINY,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    modifier = Modifier
                        .alignByBaseline()
                        .weight(1f, fill = false)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.TINY))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            items(listSuggestionInputUiModels) {
                SuggestionChip(
                    value = it.amount.toString(),
                    isSelected = it.isSelected,
                    isLoading = isLoading,
                    onClick = {
                        onClickSuggestionInput(it)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        textNotify()
    }
}

data class RamSuggestionInputUiModel(
    val amount: Int,
    val quantity: Quantity,
    val isSelected: Boolean = false
) {
    enum class Quantity {
        Percent, Eos, Kb
    }
}
