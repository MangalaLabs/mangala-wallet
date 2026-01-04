package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AntelopeAccountBalanceUnit
import com.mangala.wallet.utils.ext.formatFiat
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.ui.WalletThemeV2
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSwitchBottomSheet(
    accounts: List<AccountInfo>,
    activeAccountName: String,
    selectedCurrency: AntelopeAccountBalanceUnit,
    exchangeRateData: Map<String, BigDecimal>?,
    onAccountSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = Color(0xFF1D263E),
        contentColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        Color.White.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            
            // Title
            Text(
                text = "Switch Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontFamily = getInterFontFamily(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            
            // Account list
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(accounts) { account ->
                    AccountSwitchItem(
                        account = account,
                        isActive = account.accountName == activeAccountName,
                        selectedCurrency = selectedCurrency,
                        exchangeRateData = exchangeRateData,
                        onClick = {
                            scope.launch {
                                bottomSheetState.hide()
                                onAccountSelected(account.accountName)
                                onDismiss()
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AccountSwitchItem(
    account: AccountInfo,
    isActive: Boolean,
    selectedCurrency: AntelopeAccountBalanceUnit,
    exchangeRateData: Map<String, BigDecimal>?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .border(
                width = if (isActive) 2.dp else 1.dp,
                brush = if (isActive) {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3B90FF),
                            Color(0xFFC27DFF)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2A3E6C),
                            Color(0xFF2A3E6C)
                        )
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Account info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = account.accountName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = WalletThemeV2.Colors.primaryText.copy(alpha = if (isActive) 0.95f else 0.8f),
                        fontFamily = getInterFontFamily()
                    )
                    
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(WalletThemeV2.Colors.accentBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${account.getBalanceInSelectedUnit(selectedCurrency, exchangeRateData)?.formatFiat("")} ${selectedCurrency.symbol}",
                    fontSize = 13.sp,
                    color = WalletThemeV2.Colors.secondaryText,
                    fontFamily = getInterFontFamily()
                )
            }
            
            if (account.calculatedPnlAmount != null && account.calculatedPnlPercentage != null) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = account.getPnlAmountFormatted(selectedCurrency, exchangeRateData).orEmpty(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = account.pnlColor,
                        fontFamily = getInterFontFamily()
                    )

                    Text(
                        text = "(${account.pnlPercentageFormatted}%)",
                        fontSize = 12.sp,
                        color = account.pnlColor,
                        fontFamily = getInterFontFamily()
                    )
                }
            }
        }
    }
}