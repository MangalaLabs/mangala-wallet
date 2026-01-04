package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Dropdown
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.imageloader.RemoteImage
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun MangalaWalletDropDown(
    chosenOptionName: String,
    chosenOptionImageUrl: String,
    listOptionImagesUrl: List<String> = emptyList(),
    listOptionName: List<String> = emptyList(),
    optionImageModifier: Modifier = Modifier,
    dropdownMenuBoxModifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    textColor: Color = Colors.main1Text,
    onClickOption: (Int) -> Unit
) {
    BaseMangalaWalletDropdown(
        chosenOptionName = chosenOptionName,
        chosenOptionImageUrl = chosenOptionImageUrl,
        listOptionImageUrl = listOptionImagesUrl,
        listOptionName = listOptionName,
        dropdownMenuBoxModifier = dropdownMenuBoxModifier,
        fontWeight = fontWeight,
        textColor = textColor,
        onClickOption = onClickOption,
        imageContent = { url ->
            RemoteImage(
                url = url,
                modifier = optionImageModifier
            )
        }
    )
}

@Composable
fun MangalaWalletDropdownMenu(
    isOpenDropdownMenu: MutableState<Boolean>,
    listOptionName: List<String>,
    onClickOption: (Int) -> Unit,
    listOptionImageUrl: List<String>,
    optionImageModifier: Modifier = Modifier,
    textColor: Color,
    fontWeight: FontWeight
) {
    BaseMangalaWalletDropdownMenu(
        isOpenDropdownMenu = isOpenDropdownMenu,
        listOptionName = listOptionName,
        onClickOption = onClickOption,
        listOptionImageUrl = listOptionImageUrl,
        imageContent = { url ->
            RemoteImage(
                url = url,
                modifier = optionImageModifier
            )
        },
        textColor = textColor,
        fontWeight = fontWeight
    )
}

@Composable
fun MangalaWalletDropDownWithAccountAddressImage(
    chosenOptionName: String,
    chosenOptionAddress: String,
    listOptionAddress: List<String> = emptyList(),
    listOptionName: List<String> = emptyList(),
    optionImageSize: Dp = Dimensions.IconNormalSize,
    dropdownMenuBoxModifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    textColor: Color = Colors.main1Text,
    onClickOption: (Int) -> Unit
) {
    BaseMangalaWalletDropdown(
        chosenOptionName = chosenOptionName,
        chosenOptionImageUrl = chosenOptionAddress,
        listOptionImageUrl = listOptionAddress,
        listOptionName = listOptionName,
        dropdownMenuBoxModifier = dropdownMenuBoxModifier,
        fontWeight = fontWeight,
        textColor = textColor,
        onClickOption = onClickOption,
        imageContent = { url ->
            AccountAddressImage(
                address = url,
                size = optionImageSize
            )
        }
    )
}

@Composable
private fun BaseMangalaWalletDropdown(
    chosenOptionName: String,
    chosenOptionImageUrl: String,
    listOptionImageUrl: List<String> = emptyList(),
    listOptionName: List<String> = emptyList(),
    dropdownMenuBoxModifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    textColor: Color = Colors.main1Text,
    onClickOption: (Int) -> Unit,
    imageContent: @Composable (url: String) -> Unit
) {
    val isOpenDropdownMenu = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    Column {
        Row(
            modifier = dropdownMenuBoxModifier.clickable(interactionSource, null) { isOpenDropdownMenu.value = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (chosenOptionImageUrl.isNotBlank()) {
                imageContent(chosenOptionImageUrl)
            }

            Spacer(modifier = Modifier.width(Spacing.TINY))

            TextNormal(text = chosenOptionName, color = textColor, fontWeight = fontWeight, modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.width(Spacing.TINY))

            MangalaWalletIconButton(
                icon = MangalaWalletPack.Dropdown,
                onClick = { isOpenDropdownMenu.value = true }
            )
        }

        BaseMangalaWalletDropdownMenu(
            isOpenDropdownMenu = isOpenDropdownMenu,
            listOptionName = listOptionName,
            onClickOption = onClickOption,
            listOptionImageUrl = listOptionImageUrl,
            imageContent = imageContent,
            textColor = textColor,
            fontWeight = fontWeight
        )
    }
}

@Composable
private fun BaseMangalaWalletDropdownMenu(
    isOpenDropdownMenu: MutableState<Boolean>,
    listOptionName: List<String>,
    onClickOption: (Int) -> Unit,
    listOptionImageUrl: List<String>,
    imageContent: @Composable (url: String) -> Unit,
    textColor: Color,
    fontWeight: FontWeight
) {
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(CornerRadius.Medium))
    ) {
        DropdownMenu(
            expanded = isOpenDropdownMenu.value,
            onDismissRequest = { isOpenDropdownMenu.value = false },
            modifier = Modifier.background(androidx.compose.material3.MaterialTheme.mangalaColors.bgInnerCard),
        ) {
            listOptionName.forEachIndexed { index, optionName ->
                if (index != 0) Divider(color = Colors.cloudGray, thickness = 1.dp)
                DropdownMenuItem(
                    onClick = {
                        onClickOption(index)
                        isOpenDropdownMenu.value = false
                    },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (listOptionImageUrl.isNotEmpty()) {
                            imageContent(listOptionImageUrl[index])
                        }

                        Spacer(modifier = Modifier.width(Spacing.TINY))

                        TextNormal(
                            text = optionName,
                            color = textColor,
                            fontWeight = fontWeight
                        )
                    }
                }
            }
        }
    }
}

@Deprecated("Use new MangalaWalletDropdown with Material3 instead")
@Composable
fun MangalaWalletDropdown(
    value: String?,
    hint: String,
    hintColor: Color = Colors.gray,
    fontSize: TextUnit = 12.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    textColor: Color = Colors.darkGray,
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(CornerRadius.Small))
        .background(Color.White)
        .clickable { onClick() }
        .padding(horizontal = Spacing.SMALL, vertical = Spacing.XSMALL)
    ) {
        if (value.isNullOrBlank()) {
            Text(
                text = hint,
                style = TextStyle(color = hintColor),
                maxLines = 1,
                fontSize = fontSize,
                color = hintColor,
                fontFamily = getSfProFamilyFont(fontWeight),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value.orEmpty(),
                modifier = Modifier.fillMaxWidth().weight(1f),
                style = TextStyle(
                    fontSize = fontSize,
                    color = textColor,
                    fontFamily = getSfProFamilyFont(fontWeight)
                ),
            )
            MangalaWalletIconButton(
                icon = MangalaWalletPack.Dropdown,
                tint = Colors.caption,
                onClick = onClick
            )
        }
    }
}