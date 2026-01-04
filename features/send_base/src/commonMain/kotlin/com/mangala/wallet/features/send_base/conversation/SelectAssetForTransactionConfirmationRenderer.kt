package com.mangala.wallet.features.send_base.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.imageloader.RemoteImage
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRenderer
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.presentation.ConfirmationActions
import com.mangala.wallet.core.ai.presentation.StatusFeedbackText
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class TokenInfo(
    val symbol: String,
    val contractAddress: String? = null,
    val decimals: Int,
    val balance: Double,
    val balanceUSD: String? = null,
    val logo: String? = null
)

class SelectAssetForTransactionConfirmationRenderer : ConfirmationRenderer {
    override val functionName: String = "select_asset_for_transaction"

    @Composable
    override fun RenderConfirmation(
        message: FunctionCallConfirmationRequiredMessage,
        onConfirm: () -> Unit,
        onEdit: () -> Unit,
        onDeny: () -> Unit,
        isProcessing: Boolean
    ) {
        val tokens = remember { parseTokens(message.functionCall.parameters["tokens"]) }
        val title = message.functionCall.parameters["title"]?.toString() ?: "Select Token"
        val subtitle = message.functionCall.parameters["subtitle"]?.toString() ?: "Choose the token you want to send"
        
        var selectedToken by remember { mutableStateOf<TokenInfo?>(null) }
        
        val handleConfirm = {
            selectedToken?.let { token ->
                val mutableParams = message.functionCall.parameters as MutableMap<String, Any?>
                mutableParams["selectedAssetSymbol"] = token.symbol
                mutableParams["tokenLogoUrl"] = token.logo
                mutableParams["selectedAssetDecimals"] = token.decimals
                mutableParams["selectedAssetBalance"] = token.balance.toString()
                mutableParams["contract"] = token.contractAddress
                mutableParams["selectedAssetFromPicker"] = true
            }
            onConfirm()
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
            ) {
                Text(
                    text = title,
                    style = MangalaTypography.Size17SemiBold(),
                    color = MaterialTheme.mangalaColors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary
                )
            }

            if (message.executionStatus == ExecutionStatus.PENDING) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = Dimensions.Padding.default)
                ) {
                    items(tokens) { token ->
                        TokenCard(
                            token = token,
                            isSelected = selectedToken == token,
                            onClick = { selectedToken = token }
                        )
                    }
                }
            }

            ConfirmationActions(
                message = message,
                onConfirm = handleConfirm,
                onDeny = onDeny,
                isProcessing = isProcessing || selectedToken == null,
                confirmButtonLabel = "Confirm",
                statusTexts = StatusFeedbackText(
                    confirmed = "✓ Asset selected successfully",
                    executed = "✓ Asset selected successfully",
                    cancelled = "✗ Asset selection cancelled",
                    failed = "⚠️ Asset selection failed",
                    expired = "✗ Asset selection expired"
                ),
                modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
            )
        }
    }

    @Composable
    private fun TokenCard(
        token: TokenInfo,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        val borderBrush = if (isSelected) 
            MaterialTheme.mangalaColors.borderHighlight 
        else 
            null

        Box(
            modifier = Modifier
                .width(120.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .then(
                    if (isSelected && borderBrush != null) {
                        Modifier.border(2.dp, borderBrush, RoundedCornerShape(12.dp))
                    } else {
                        Modifier.border(1.dp, MaterialTheme.mangalaColors.border, RoundedCornerShape(12.dp))
                    }
                )
                .background(color = MaterialTheme.mangalaColors.bgInnerCard)
                .clickable { onClick() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (token.symbol == "EOS") {
                    LocalImage(Modifier.size(24.dp), imageResource = MR.images.eos_new)
                } else if (token.symbol == "A") {
                    LocalImage(Modifier.size(24.dp), imageResource = MR.images.vaulta)
                } else if (token.logo != null) {
                    RemoteImage(
                        url = token.logo,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3BA2F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = token.symbol.take(1),
                            style = MangalaTypography.Size12SemiBold(),
                            color = Color.White
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = token.symbol,
                        style = MangalaTypography.Size12SemiBold(),
                        color = MaterialTheme.mangalaColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Balance: ${token.balance}",
                        style = MangalaTypography.Size10Regular(),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }


    private fun parseTokens(tokensParam: Any?): List<TokenInfo> {
        return try {
            when (tokensParam) {
                is String -> {
                    Json.decodeFromString<List<TokenInfo>>(tokensParam)
                }
                else -> emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}