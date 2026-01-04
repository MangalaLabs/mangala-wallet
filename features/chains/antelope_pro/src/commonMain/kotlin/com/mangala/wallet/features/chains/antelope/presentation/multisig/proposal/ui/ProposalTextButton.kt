package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.LocalMinimumTouchTargetEnforcement
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProposalTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.mangalaColors.textPrimary,
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp, vertical = Dimensions.Padding.default),
    textStyle: TextStyle = MangalaTypography.Size14Medium()
) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false,
        LocalMinimumInteractiveComponentSize provides 0.dp,
    ) {
        TextButton(
            modifier = modifier.then(Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)),
            onClick = onClick,
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                text,
                style = textStyle,
                color = color,
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}