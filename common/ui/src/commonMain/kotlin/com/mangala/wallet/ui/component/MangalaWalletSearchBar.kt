package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.icons.FontSizeNew
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Search
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun MangalaWalletSearchBar(
    searchText: State<String>,
    placeholder: String,
    onValueChange: (String) -> Unit,
) {
    MangalaWalletSearchBar(
        searchText = searchText.value,
        placeholder = placeholder,
        onValueChange = onValueChange,
    )
}

@Composable
fun MangalaWalletSearchBar(
    searchText: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                imageVector = MangalaWalletPack.Search,
                contentDescription = "Search Icon",
                tint = Colors.gray
            )
        },
        placeholder = {
            TextDescription2(
                text = placeholder,
                color = Colors.gray,
            )
        },
        textStyle = TextStyle.Default.copy(
            fontSize = FontType.SMALL,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = Color.Black,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(CornerRadius.Small))
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MangalaWalletSearchBarWithBorder(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    colors: TextFieldColors = MangalaWalletSearchBarDefaults.searchBarColors(),
    leadingIcon: (@Composable () -> Unit)? = {
        Icon(
            imageVector = MangalaWalletPack.Search,
            contentDescription = "Search Icon",
        )
    },
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    ),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = remember { RoundedCornerShape(CornerRadius.Small) }

    BasicTextField(
        value = query,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically, unbounded = true)
            .clip(shape)
            .then(modifier),
        onValueChange = onQueryChange,
        textStyle = TextStyle.Default.copy(
            fontSize = FontSizeNew.SMALL_BODY,
            color = colors.textColor(enabled).value
        ),
        cursorBrush = SolidColor(colors.cursorColor(enabled).value),
        keyboardOptions = keyboardOptions,
        singleLine = true,
        maxLines = 1,
        decorationBox = { innerTextField ->
            TextFieldDefaults.OutlinedTextFieldDecorationBox(
                value = query,
                visualTransformation = VisualTransformation.None,
                innerTextField = innerTextField,
                contentPadding = PaddingValues(
                    vertical = Dimensions.Padding.small,
                    horizontal = Dimensions.Padding.default
                ),
                placeholder = {
                    TextDescription2(
                        text = placeholder,
                        color = colors.placeholderColor(enabled).value,
                        fontSize = FontSizeNew.SMALL_BODY,
                        modifier = Modifier.wrapContentHeight(
                            align = Alignment.CenterVertically,
                            unbounded = true
                        ),
                        lineHeight = FontSizeNew.SMALL_BODY
                    )
                },
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                enabled = enabled,
                interactionSource = interactionSource,
                singleLine = true,
                shape = shape,
                colors = colors,
                border = {
                    TextFieldDefaults.BorderBox(
                        enabled = true,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = colors,
                        shape = shape,
                    )
                }
            )
        },
        enabled = enabled
    )
}

object MangalaWalletSearchBarDefaults {

    @Composable
    fun searchBarColors(
        focusedBorderColor: Color = MaterialTheme.mangalaColors.border,
        unfocusedBorderColor: Color = MaterialTheme.mangalaColors.border,
        backgroundColor: Color = MaterialTheme.mangalaColors.bgInnerCard,
        leadingIconColor: Color = MaterialTheme.mangalaColors.iconPrimary,
        textColor: Color = MaterialTheme.mangalaColors.textPrimary,
        placeholderColor: Color = MaterialTheme.mangalaColors.textSecondary,
    ): TextFieldColors {
        return TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            disabledBorderColor = unfocusedBorderColor,
            backgroundColor = backgroundColor,
            leadingIconColor = leadingIconColor,
            textColor = textColor,
            disabledTextColor = textColor,
            placeholderColor = placeholderColor,
            disabledPlaceholderColor = placeholderColor,
            cursorColor = textColor
        )
    }
}