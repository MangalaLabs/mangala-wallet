package com.mangala.wallet.features.send_base.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRenderer
import com.mangala.wallet.core.ai.presentation.ConfirmationActions
import com.mangala.wallet.core.ai.presentation.StatusFeedbackText
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.imageloader.RemoteImage
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.utils.truncateDecimal

class SendTransactionConfirmationRenderer : ConfirmationRenderer {
    override val functionName: String = SEND_TRANSACTION_FUNCTION_NAME

    @Composable
    override fun RenderConfirmation(
        message: FunctionCallConfirmationRequiredMessage,
        onConfirm: () -> Unit,
        onEdit: () -> Unit,
        onDeny: () -> Unit,
        isProcessing: Boolean
    ) {
        val amount = message.functionCall.parameters[SEND_TRANSACTION_PARAM_AMOUNT]?.toString()?.toDoubleOrNull() ?: 0.0
        val feeString = message.functionCall.parameters[SEND_TRANSACTION_PARAM_FEE]?.toString()
        val feeAmount = feeString?.split(" ")?.firstOrNull()?.toDoubleOrNull()
        val feeSymbol = feeString?.split(" ")?.lastOrNull()
        val total = if (feeAmount == null) amount else amount + feeAmount
        val memo = message.functionCall.parameters[SEND_TRANSACTION_PARAM_MEMO]?.toString()
        val assetSymbol = message.functionCall.parameters[SEND_TRANSACTION_PARAM_ASSET]?.toString().orEmpty()
        val selectedContactName = message.functionCall.parameters[SEND_TRANSACTION_RECIPIENT_CONTACT_NAME]?.toString()

        Column(
            modifier = Modifier.fillMaxWidth(0.8f).padding(horizontal = Dimensions.Padding.default),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Confirm send transaction",
                style = MangalaTypography.Size17SemiBold(),
                color = MaterialTheme.mangalaColors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.mangalaColors.bgInnerCard,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TransactionDetailRow(
                        label = "Token",
                        value = message.functionCall.parameters[SEND_TRANSACTION_PARAM_ASSET]?.toString().orEmpty(),
                        tokenSymbol = assetSymbol
                    )
                    
                    TransactionDetailRow(
                        label = "Amount",
                        value = "${message.functionCall.parameters[SEND_TRANSACTION_PARAM_AMOUNT]?.toString().orEmpty()} $assetSymbol"
                    )
                    
                    TransactionDetailRow(
                        label = "From",
                        value = message.functionCall.parameters[SEND_TRANSACTION_SENDER_ADDRESS]?.toString().orEmpty()
                    )
                    
                    if (!selectedContactName.isNullOrBlank()) {
                        TransactionDetailRow(
                            label = "Recipient",
                            value = selectedContactName,
                            hasAvatar = true
                        )
                        
                        TransactionDetailRow(
                            label = "Address",
                            value = message.functionCall.parameters[SEND_TRANSACTION_PARAM_RECIPIENT_ADDRESS]?.toString().orEmpty()
                        )
                    } else {
                        TransactionDetailRow(
                            label = "Recipient",
                            value = message.functionCall.parameters[SEND_TRANSACTION_PARAM_RECIPIENT_ADDRESS]?.toString().orEmpty(),
                            hasAvatar = true
                        )
                    }
                    
                    if (!memo.isNullOrBlank()) {
                        TransactionDetailRow(
                            label = "Memo",
                            value = memo
                        )
                    }
                    
                    message.functionCall.parameters[SEND_TRANSACTION_PARAM_FEE]?.toString()?.takeIf { it.isNotBlank() }?.let { fee ->
                        TransactionDetailRow(
                            label = "Network fee",
                            value = fee
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.mangalaColors.border)
                    )
                    
                    if (assetSymbol == feeSymbol || feeSymbol.isNullOrBlank()) {
                        TransactionDetailRow(
                            label = "Total",
                            value = "${total.truncateDecimal(4)} $assetSymbol",
                            isTotal = true
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TransactionDetailRow(
                                label = "Total",
                                value = "${message.functionCall.parameters[SEND_TRANSACTION_PARAM_AMOUNT]?.toString().orEmpty()} $assetSymbol",
                                isTotal = true
                            )
                            TransactionDetailRow(
                                label = "",
                                value = message.functionCall.parameters[SEND_TRANSACTION_PARAM_FEE]?.toString()?.let { "+ ${it} (network fee)" } ?: "Unable to calculate fees",
                                isTotal = false
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ConfirmationActions(
                message = message,
                onConfirm = onConfirm,
                onDeny = onDeny,
                confirmButtonLabel = "Confirm",
                isProcessing = isProcessing,
                statusTexts = StatusFeedbackText(
                    confirmed = "✓ Transaction sent successfully",
                    executed = "✓ Transaction sent successfully",
                    failed = "⚠️ Transaction failed",
                    cancelled = "✗ Transaction cancelled",
                    expired = "✗ Transaction expired"
                )
            )
        }
    }
    
    @Composable
    private fun TransactionDetailRow(
        label: String,
        value: String,
        tokenSymbol: String? = null,
        tokenLogo: String? = null,
        hasAvatar: Boolean = false,
        isTotal: Boolean = false
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MangalaTypography.Size14Regular(),
                color = MaterialTheme.mangalaColors.textSecondary
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (tokenSymbol != null) {
                    if (tokenSymbol == "EOS") {
                        LocalImage(Modifier.size(24.dp), imageResource = MR.images.eos_new)
                    } else if (tokenSymbol == "A") {
                        LocalImage(Modifier.size(24.dp), imageResource = MR.images.vaulta)
                    } else if (tokenLogo != null) {
                        RemoteImage(
                            url = tokenLogo,
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
                                text = tokenSymbol.take(1),
                                style = MangalaTypography.Size12SemiBold(),
                                color = Color.White
                            )
                        }
                    }
                }
                
                if (hasAvatar) {
                    // User avatar
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = Color(0xFFE6E6FA),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 10.sp
                        )
                    }
                }
                
                Text(
                    text = value,
                    style = if (isTotal) MangalaTypography.Size14SemiBold() else MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textPrimary
                )
            }
        }
    }
}