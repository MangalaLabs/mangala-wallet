package com.mangala.wallet.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.ui.ButtonNormal

@Composable
fun CreateImportButton(text: String, isEnabled: Boolean = true, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ButtonNormal(
        onClick = onClick,
        text = text,
        buttonModifier = Modifier.padding(Dimensions.Padding.default),
        textColor = if (isEnabled) Colors.white else Colors.mistGray,
        disabledBackgroundColor = Colors.white,
        backgroundColor = if (isEnabled) Colors.main1Text else Colors.white,
        modifier = Modifier.fillMaxWidth().padding(Dimensions.Padding.small),
        enabled = isEnabled,
        buttonMinSizeDefault = 44.dp
    )
}