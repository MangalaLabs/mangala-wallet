package com.mangala.wallet.features.addressbook.presentation.blockchain

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.TokenInformationEntity
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen

class BlockchainScreen : BaseScreen<BlockchainScreenModel>() {
    @Composable
    override fun createScreenModel(): BlockchainScreenModel = getScreenModel()

    override val screenName: String
        get() = "Blockchain Types"
    override val screenClassName: String
        get() = "BlockchainScreen"
    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun ScreenContent(screenModel: BlockchainScreenModel) {
        val uiState by screenModel.uiState.collectAsState()

        Surface(
            modifier = Modifier
                .safeDrawingPadding()
                .fillMaxSize(),
            color = ColorsNew.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Blockchain Network",
                    style = MangalaTypography.Size14Medium(),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.error != null) {
                    ErrorMessage(
                        error = uiState.error!!,
                        onRetry = { screenModel.resetState() }
                    )
                } else if (uiState.blockchainTypes.isEmpty()) {
                    // Show empty state with button to add sample blockchain networks
                    EmptyBlockchainNetworksView(
                        onAddSampleNetworks = { screenModel.addSampleBlockchains() }
                    )
                } else {
                    BlockchainSelection(
                        blockchainTypes = uiState.blockchainTypes,
                        selectedBlockchainType = uiState.selectedBlockchainType,
                        onSelectBlockchain = { screenModel.selectBlockchainType(it.id) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    WalletAddressInput(
                        walletAddress = uiState.walletAddress.address,
                        onAddressChange = { screenModel.updateWalletAddress(it) },
                        isValid = uiState.isAddressValid,
                        validationError = uiState.validationError,
                        onValidate = { screenModel.validateWalletAddress() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AliasInput(
                        alias = uiState.walletAddress.alias,
                        onAliasChange = { screenModel.updateWalletAlias(it) }
                    )

                    if (uiState.selectedBlockchainType != null && uiState.tokensForSelectedBlockchain.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))

                        TokensForBlockchain(
                            tokens = uiState.tokensForSelectedBlockchain
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ErrorMessage(error: String, onRetry: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Icon(
//                imageVector = Icons.Default.Error,
//                contentDescription = "Error",
//                tint = MaterialTheme.colorScheme.error,
//                modifier = Modifier.size(48.dp)
//            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = error,
                color = ColorsNew.error_50,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }

    @Composable
    private fun BlockchainSelection(
        blockchainTypes: List<BlockchainTypeEntity>,
        selectedBlockchainType: BlockchainTypeEntity?,
        onSelectBlockchain: (BlockchainTypeEntity) -> Unit
    ) {
        Column {
            Text(
                text = "Blockchain Networks",
                style = MangalaTypography.Size14Medium(),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(blockchainTypes) { blockchain ->
                    BlockchainItem(
                        blockchain = blockchain,
                        isSelected = blockchain.id == selectedBlockchainType?.id,
                        onClick = { onSelectBlockchain(blockchain) }
                    )
                }
            }
        }
    }

    @Composable
    private fun BlockchainItem(
        blockchain: BlockchainTypeEntity,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = Color(
                                blockchain.color?.toLongOrNull(16)?.toInt()
                                    ?: 0xFF4285F4.toInt()
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = blockchain.name,
                        style = MangalaTypography.Size14Medium(),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (blockchain.symbol.isNotEmpty()) {
                        Text(
                            text = blockchain.symbol,
                            style = MangalaTypography.Size14Medium(),
                            color = ColorsNew.black
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = ColorsNew.primary_50
                    )
                }
            }
        }
    }

    @Composable
    private fun WalletAddressInput(
        walletAddress: String,
        onAddressChange: (String) -> Unit,
        isValid: Boolean?,
        validationError: String?,
        onValidate: () -> Unit
    ) {
        Column {
            Text(
                text = "Wallet Address",
                style = MangalaTypography.Size14Medium(),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = walletAddress,
                    onValueChange = onAddressChange,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("Enter wallet address")
                    },
                    isError = isValid == false,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Scan QR Code",
                            modifier = Modifier.clickable { /* Open QR scanner */ }
                        )
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onValidate) {
                    Text("Validate")
                }
            }

            if (isValid == true) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Valid",
                        tint = Color.Green
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Address is valid",
                        color = Color.Green
                    )
                }
            }
        }
    }

    @Composable
    private fun AliasInput(
        alias: String,
        onAliasChange: (String) -> Unit
    ) {
        Column {
            Text(
                text = "Address Alias (Optional)",
                style = MangalaTypography.Size14Medium(),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = alias,
                onValueChange = onAliasChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter a friendly name for this address") },
                placeholder = { Text("e.g. My Bitcoin Wallet, Trading Account") }
            )
        }
    }

    @Composable
    private fun TokensForBlockchain(
        tokens: List<TokenInformationEntity>
    ) {
        Column {
            Text(
                text = "Available Tokens",
                style = MangalaTypography.Size14Medium(),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(tokens) { token ->
                    TokenItem(token = token)
                }
            }
        }
    }

    @Composable
    private fun TokenItem(token: TokenInformationEntity) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Token symbol in a colored circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = ColorsNew.background,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = ColorsNew.primary_50,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = token.tokenSymbol.take(3),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = token.tokenName,
                    style = MangalaTypography.Size17Medium()
                )
                Text(
                    text = token.tokenSymbol,
                    style = MangalaTypography.Size14Medium(),
                    color = ColorsNew.black
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (token.isNative) {
                Text(
                    text = "Native",
                    style = MangalaTypography.Size17Medium(),
                    color = ColorsNew.primary_50,
                    modifier = Modifier
                        .background(
                            color = ColorsNew.background,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }

    @Composable
    private fun EmptyBlockchainNetworksView(onAddSampleNetworks: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Networks",
                tint = ColorsNew.primary_50,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = ColorsNew.primary_50.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Blockchain Networks Found",
                style = MangalaTypography.Size17Medium(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add sample blockchain networks to get started",
                style = MangalaTypography.Size14Medium(),
                color = ColorsNew.black.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onAddSampleNetworks) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Sample Networks")
            }
        }
    }


}
