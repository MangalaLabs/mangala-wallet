package com.mangala.wallet.features.conversationui.presentation.components.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TransactionReviewData(
    val summary: TransactionSummary,
    val fees: TransactionFees,
    val total: TransactionTotal
)

data class TransactionSummary(
    val fromAddress: String,
    val toAddress: String,
    val amount: String,
    val tokenSymbol: String,
    val network: String,
    val memo: String? = null
)

data class TransactionFees(
    val networkFee: String,
    val networkFeeUSD: String,
    val cpuUsage: String? = null,
    val netUsage: String? = null,
    val ramUsage: String? = null,
    val gasLimit: String? = null,
    val gasPrice: String? = null
)

data class TransactionTotal(
    val totalAmount: String,
    val totalAmountUSD: String,
    val includingFees: String,
    val includingFeesUSD: String
)

@Composable
fun TransactionReviewComponent(
    reviewData: TransactionReviewData,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Review Transaction",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            item {
                TransactionSummarySection(reviewData.summary)
            }
            
            item {
                Divider()
            }
            
            item {
                TransactionFeesSection(reviewData.fees)
            }
            
            item {
                Divider()
            }
            
            item {
                TransactionTotalSection(reviewData.total)
            }
            
            item {
                TransactionActionButtons(
                    onConfirm = onConfirm,
                    onCancel = onCancel
                )
            }
        }
    }
}

@Composable
private fun TransactionSummarySection(summary: TransactionSummary) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Transaction Details",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        
        TransactionDetailRow("From", summary.fromAddress)
        TransactionDetailRow("To", summary.toAddress)
        TransactionDetailRow("Amount", "${summary.amount} ${summary.tokenSymbol}")
        TransactionDetailRow("Network", summary.network)
        
        summary.memo?.let { memo ->
            if (memo.isNotBlank()) {
                TransactionDetailRow("Memo", memo)
            }
        }
    }
}

@Composable
private fun TransactionFeesSection(fees: TransactionFees) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Network Fees",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        
        TransactionDetailRow("Network Fee", fees.networkFee)
        TransactionDetailRow("USD Value", fees.networkFeeUSD)
        
        fees.cpuUsage?.let { cpu ->
            TransactionDetailRow("CPU Usage", cpu)
        }
        
        fees.netUsage?.let { net ->
            TransactionDetailRow("NET Usage", net)
        }
        
        fees.ramUsage?.let { ram ->
            TransactionDetailRow("RAM Usage", ram)
        }
        
        fees.gasLimit?.let { gasLimit ->
            TransactionDetailRow("Gas Limit", gasLimit)
        }
        
        fees.gasPrice?.let { gasPrice ->
            TransactionDetailRow("Gas Price", gasPrice)
        }
    }
}

@Composable
private fun TransactionTotalSection(total: TransactionTotal) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Total",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        
        TransactionDetailRow("Amount", total.totalAmount)
        TransactionDetailRow("USD Value", total.totalAmountUSD)
        
        Spacer(modifier = Modifier.height(4.dp))
        
        TransactionDetailRow(
            label = "Including Fees",
            value = total.includingFees,
            isTotal = true
        )
        TransactionDetailRow(
            label = "Total USD",
            value = total.includingFeesUSD,
            isTotal = true
        )
    }
}

@Composable
private fun TransactionDetailRow(
    label: String,
    value: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) {
                MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = if (isTotal) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        
        Text(
            text = value,
            style = if (isTotal) {
                MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = if (isTotal) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun TransactionActionButtons(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Cancel")
        }
        
        Button(
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Confirm Transaction")
        }
    }
}