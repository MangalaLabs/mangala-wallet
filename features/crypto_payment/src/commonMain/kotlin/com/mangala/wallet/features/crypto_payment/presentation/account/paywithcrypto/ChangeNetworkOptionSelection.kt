package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Search
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer

@Composable
fun <T> ChangeNetworkOptionSelection(
    optionTitle: String,
    options: List<T>,
    selectedOption: T?,
    getOptionName: (T) -> String,
    searchPlaceHolder: String,
    onContinue: (T?) -> Unit,
    onDismiss: () -> Unit
) {

    var optionSelected by remember { mutableStateOf(selectedOption) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredOptions = options.filter {
        getOptionName(it).contains(searchQuery, ignoreCase = true)
    }

    MaxWidthColumn (
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {

        MaxWidthBox {
            TextNormal(
                text = optionTitle,
                color = Colors.slateGray,
                fontSize = FontType.REGULAR,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = { onDismiss() },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    tint = Colors.darkDarkGray,
                    contentDescription = null,
                )
            }
        }
        VerticalSpacer(Spacing.XSMALL)
        CustomSearchTextField(searchQuery, searchPlaceHolder) { query ->
            searchQuery = query
        }
        VerticalSpacer(Spacing.XSMALL)
        LazyColumn {
            items(filteredOptions) { option ->
                OptionCard(
                    option,
                    optionSelected,
                    onClick = {
                        optionSelected = it
                        onContinue(optionSelected)
                    },
                    getOptionName = getOptionName
                )
            }
        }
    }
}

@Composable
private fun <T> OptionCard(
    network: T,
    selectedNetwork: T?,
    onClick: (T) -> Unit,
    getOptionName: (T) -> String
) {

    val isSelected = network == selectedNetwork

    Column (
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = Dimensions.Padding.default)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    onClick(network)
                }
                .padding(vertical = Dimensions.Padding.small)
        ) {
            Text(
                text = getOptionName(network),
                color = Colors.darkDarkGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(
                            Colors.darkDarkGray,
                            shape = CircleShape
                        )
                        .padding(3.dp)
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Filled.Check,
                        contentDescription = if (isSelected) "Condition met" else "Condition not met",
                        tint = Colors.white
                    )
                }
            }
        }
        Divider(
            color = Color.Black.copy(alpha = 0.05f),
            thickness = 1.dp
        )
    }
}

@Composable
fun CustomSearchTextField(
    searchQuery: String,
    searchPlaceHolder: String,
    onSearchQueryChange: (String) -> Unit
) {

    BasicTextField(
        value = searchQuery,
        onValueChange = { onSearchQueryChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.Padding.default)
            .border(1.dp, Colors.stroke3, shape = RoundedCornerShape(CornerRadius.Tiny))
            .padding(Dimensions.Padding.small), // Inner padding for text alignment
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MangalaWalletPack.Search,
                    contentDescription = null,
                    tint = Colors.gray,
                    modifier = Modifier.padding(end = Dimensions.Padding.half)
                )
                if (searchQuery.isEmpty()) {
                    Text(
                        text = searchPlaceHolder,
                        color = Colors.gray
                    )
                }
                innerTextField() // The actual text field content
            }
        },
        textStyle = TextStyle(color = Color.Black) // Customize text style
    )
}