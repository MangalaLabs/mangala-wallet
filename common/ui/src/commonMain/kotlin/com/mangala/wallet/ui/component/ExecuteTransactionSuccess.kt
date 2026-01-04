package com.mangala.wallet.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TransactionSuccess
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun ExecuteTransactionSuccess(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    textTitle: String,
    bottomButton: @Composable () -> Unit,
) {
    OnboardingGradientBackground {
        MaxSizeColumn(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .fillMaxWidth()
                .padding(Dimensions.Padding.default)
        ) {
            MaxWidthColumn(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(144.dp))

                Image(
                    painter = rememberVectorPainter(MangalaWalletPack.TransactionSuccess),
                    contentDescription = "Transfer Success",
                    Modifier.size(Dimensions.Height.ultraLarge)
                )

                Spacer(modifier = Modifier.height(Dimensions.Height.xxLarge))

                TextNormal(
                    text = textTitle,
                    fontSize = FontType.LARGE,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.mangalaColors.textPrimary,
                )
            }

            MaxWidthColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                bottomButton()
            }
        }
    }
}
