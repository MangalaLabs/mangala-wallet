package com.mangala.wallet.features.chains.antelope.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.PullRefreshState
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTitle3_34
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.imageloader.DisplayImageForIcon
import com.mangala.wallet.ui.imageloader.ImageHolder
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ResourceScreen(
    title: StringResource,
    inputSectionTitle: StringResource,
    pricesLabel: @Composable () -> Unit,
    nativeCoinAvailableBalance: String?,
    buttonText: String,
    isRefreshing: Boolean,
    amountValue: String?,
    unit: String,
    error: String?,
    buttonEnabled: Boolean,
    onPullToRefresh: () -> Unit,
    onBackClicked: () -> Unit,
    onValueChange: (String) -> Unit,
    onClickButton: () -> Unit,
    suggestionInput: @Composable () -> Unit = {},
    inputSectionEnabled: Boolean = false
) {
    val pullRefreshState = PullRefreshState(
        isRefreshing = isRefreshing,
        onRefresh = {
            onPullToRefresh()
        }
    )
    val focusManager = LocalFocusManager.current

    MaxSizeBox(
        modifier = Modifier
            .background(MaterialTheme.mangalaColors.bg)
            .pullRefresh(pullRefreshState, enabled = !isRefreshing)
            .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        MaxSizeColumn {
            MangalaWalletTopBarCenteredTitle(
                title = title.desc().localized(),
                onBackClicked = onBackClicked
            )

            MaxWidthColumn(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.width(Spacing.XXXBASE))

                CurrentRamParameters(
                    nativeCoinBalance = nativeCoinAvailableBalance,
                    pricesLabel = pricesLabel
                )
                InputSection(
                    inputSectionTitle,
                    isLoading = amountValue == null,
                    onValueChange = onValueChange,
                    suggestionInput = suggestionInput,
//                    onClickSuggestionInput = screenModel::onSelectSuggestionInput,
                    value = amountValue.orEmpty(),
                    unit = unit,
                    error = error,
                    inputSectionEnabled = inputSectionEnabled
                )
                Spacer(modifier = Modifier.height(Spacing.TINY))
            }

            MangalaGradientButton(
                label = buttonText,
                onClick = {
                    focusManager.clearFocus()
                    onClickButton()
                },
                enabled = buttonEnabled,
                size = MangalaButtonSize.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimensions.Padding.default,
                        vertical = Dimensions.Padding.default
                    )
            )
        }

        PullRefreshIndicator(
            isRefreshing,
            pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = Dimensions.paddingRefreshingOffsetDefaultTop)
        )
    }
}

@Composable
fun CurrentRamParameters(
    pricesLabel: @Composable () -> Unit,
//    uiModel: RexScreenUiModel
    nativeCoinBalance: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = Dimensions.Padding.default,
                bottom = Dimensions.Padding.default,
                start = Dimensions.Padding.default,
                end = Dimensions.Padding.small
            )
            .background(
                color = MaterialTheme.mangalaColors.bgInnerCard,
                shape = RoundedCornerShape(CornerRadius.Medium)
            )
            .padding(Dimensions.Padding.default)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    pricesLabel()
                }

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    DisplayImageForIcon(
                        imageHolder = ImageHolder.Paint(MR.images.eos_new),
                        modifier = Modifier.size(Dimensions.IconButtonSize)
                    )
                    HorizontalSpacer(Spacing.TINY)
                    Column {
                        TextDescription2(
                            text = nativeCoinBalance ?: "0.0000 EOS",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.mangalaColors.textPrimary,
                            modifier = Modifier.mangalaWalletPlaceholder(nativeCoinBalance.isNullOrEmpty())
                        )
                        TextDescription2(
                            text = MR.strings.available_balance.desc().localized(),
                            fontSize = FontType.TINY,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.mangalaColors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InputSection(
    inputSectionTitle: StringResource,
    isLoading: Boolean,
    onValueChange: (String) -> Unit,
    suggestionInput: @Composable () -> Unit,
    value: String,
    unit: String,
    error: String?,
    inputSectionEnabled: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .padding(horizontal = Dimensions.Padding.default)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MaxWidthRow(
            horizontalArrangement = Arrangement.Center,
        ) {
            TextNormal(
                text = inputSectionTitle.desc().localized(),
                color = MaterialTheme.mangalaColors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        MaxWidthRow(
            modifier = Modifier
                .padding(horizontal = Dimensions.Padding.default)
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            BasicTextField(
                modifier = Modifier
                    .background(Color.Transparent)
                    .weight(5f, fill = false)
                    .width(IntrinsicSize.Min)
                    .defaultMinSize(minWidth = 0.dp)
                    .focusRequester(focusRequester)
                    .alignByBaseline(),
                value = value,
                onValueChange = { newText ->
                    if (inputSectionEnabled) {
                        onValueChange(newText)
                    }
                },
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
                    imeAction = ImeAction.Done
                ),
                decorationBox = { innerTextField ->
                    Box {
                        innerTextField()

                        if (value.isBlank()) {
                            TextTitle3_34(
                                text = "0",
                                color = MaterialTheme.mangalaColors.textSecondary
                            )
                        }
                    }
                }
            )
            HorizontalSpacer(Spacing.XTINY)
            TextDescription2(
                text = unit,
                fontSize = FontType.TINY,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier
                    .alignByBaseline()
                    .weight(1f, fill = false)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.TINY))

        suggestionInput()

        Spacer(modifier = Modifier.height(8.dp))

        error?.let {
            TextDescription2(
                text = it,
                fontSize = FontType.TINY,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.mangalaColors.buttonDestructiveContainer,
            )
        }
    }
}