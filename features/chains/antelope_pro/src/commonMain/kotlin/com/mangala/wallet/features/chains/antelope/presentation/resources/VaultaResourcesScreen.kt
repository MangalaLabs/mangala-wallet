package com.mangala.wallet.features.chains.antelope.presentation.resources

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.features.chains.antelope.presentation.resources.components.*
import com.mangala.wallet.features.chains.antelope.presentation.resources.model.ResourceUiState

class VaultaResourcesScreen : Screen {
    
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<VaultaResourcesScreenModel>()
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        
        VaultaResourcesContent(
            uiState = uiState,
            onBoostClick = screenModel::onBoostClick,
            onTradeClick = screenModel::onTradeClick,
            onAutoOptimizeClick = screenModel::onAutoOptimizeClick,
            onCpuRechargeClick = screenModel::onCpuRechargeClick,
            onCpuUpgradeClick = screenModel::onCpuUpgradeClick,
            onNetRefreshClick = screenModel::onNetRefreshClick,
            onNetUpgradeClick = screenModel::onNetUpgradeClick,
            onRamBuyClick = screenModel::onRamBuyClick,
            onRamSellClick = screenModel::onRamSellClick,
            onRamMarketClick = screenModel::onRamMarketClick
        )
    }
}

@Composable
private fun VaultaResourcesContent(
    uiState: ResourceUiState,
    onBoostClick: () -> Unit,
    onTradeClick: () -> Unit,
    onAutoOptimizeClick: () -> Unit,
    onCpuRechargeClick: () -> Unit,
    onCpuUpgradeClick: () -> Unit,
    onNetRefreshClick: () -> Unit,
    onNetUpgradeClick: () -> Unit,
    onRamBuyClick: () -> Unit,
    onRamSellClick: () -> Unit,
    onRamMarketClick: () -> Unit
) {
    OnboardingGradientBackground {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 120.dp) // Space for skill bar
            ) {
                // Header
                ResourceHeader(
                    playerName = uiState.playerInfo.name,
                    walletAddress = uiState.playerInfo.walletAddress,
                    eosBalance = uiState.playerInfo.eosBalance
                )
                
                // Resources Panel
                ResourcesPanel(
                    powerLevel = uiState.powerLevel,
                    cpuResource = uiState.cpuResource,
                    netResource = uiState.netResource,
                    ramResource = uiState.ramResource,
                    onCpuRechargeClick = onCpuRechargeClick,
                    onCpuUpgradeClick = onCpuUpgradeClick,
                    onNetRefreshClick = onNetRefreshClick,
                    onNetUpgradeClick = onNetUpgradeClick,
                    onRamBuyClick = onRamBuyClick,
                    onRamSellClick = onRamSellClick,
                    onRamMarketClick = onRamMarketClick
                )
            }
            
            // Skill Bar (Fixed at bottom)
            ResourceSkillBar(
                onBoostClick = onBoostClick,
                onTradeClick = onTradeClick,
                onAutoOptimizeClick = onAutoOptimizeClick,
                modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
            )
        }
    }
}