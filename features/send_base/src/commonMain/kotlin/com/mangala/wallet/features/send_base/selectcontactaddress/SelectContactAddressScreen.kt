package com.mangala.wallet.features.send_base.selectcontactaddress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel
import com.mangala.wallet.features.addressbook.presentation.components.ContactAvatar
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import org.koin.core.parameter.parametersOf

class SelectContactAddressScreen(
    private val contactId: String,
    private val accountId: String
) : BaseScreen<SelectContactAddressScreenModel>() {

    override val key: ScreenKey
        get() = "SelectContactAddressScreen"

    override val screenName: String = "SendSelectContactAddressScreen"
    override val screenClassName: String = SelectContactAddressScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): SelectContactAddressScreenModel =
        getScreenModel(
            parameters = {
                parametersOf(contactId, accountId)
            }
        )

    @Composable
    override fun ScreenContent(screenModel: SelectContactAddressScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val selectedAddress by screenModel.selectedAddress.collectAsStateMultiplatform()

        OnboardingGradientBackground {
            MaxSizeColumn(
                modifier = Modifier
                    .safeDrawingPadding()
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = "Select Address",
                    onBackClicked = navigator::pop
                )

                when (uiState) {
                    is SelectContactAddressScreenUiState.Loading -> {
                        MaxWidthBox(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            MangalaCircularProgressIndicator(
                                color = MaterialTheme.mangalaColors.iconPrimary,
                                size = 32.dp
                            )
                        }
                    }

                    is SelectContactAddressScreenUiState.Error -> {
                        val errorState = uiState as SelectContactAddressScreenUiState.Error
                        MaxWidthBox(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorState.message,
                                style = MangalaTypography.Size14Regular(),
                                color = MaterialTheme.mangalaColors.buttonDestructiveContainer
                            )
                        }
                    }

                    is SelectContactAddressScreenUiState.Data -> {
                        val dataState = uiState as SelectContactAddressScreenUiState.Data
                        AddressSelectionContent(
                            contact = dataState.contact,
                            addressGroups = dataState.addressGroups,
                            selectedAddress = selectedAddress,
                            onAddressSelected = screenModel::selectAddress,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Continue button
                if (uiState is SelectContactAddressScreenUiState.Data && selectedAddress != null) {
                    Column(
                        modifier = Modifier.padding(Dimensions.Padding.default)
                    ) {
                        MangalaGradientButton(
                            label = "Continue",
                            onClick = {
                                selectedAddress?.let { address ->
                                    val step3Screen = ScreenRegistry.get(
                                        SharedScreen.Step3SelectAmountScreen(
                                            accountId = accountId,
                                            contactId = null,
                                            address = address.address,
                                            blockchainUid = address.blockchainNetworkId,
                                            amount = null
                                        )
                                    )
                                    navigator.push(step3Screen)
                                }
                            },
                            enabled = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AddressSelectionContent(
        contact: ContactModel,
        addressGroups: List<BlockchainAddressGroup>,
        selectedAddress: WalletAddressWithNetworkModel?,
        onAddressSelected: (WalletAddressWithNetworkModel) -> Unit,
        modifier: Modifier = Modifier
    ) {
        LazyColumn(
            modifier = modifier.padding(horizontal = Dimensions.Padding.default),
            verticalArrangement = Arrangement.spacedBy(Spacing.SMALL)
        ) {
            // Contact header
            item {
                ContactHeader(contact = contact)
                VerticalSpacer(Spacing.MEDIUM)
            }

            // Address groups
            items(addressGroups) { group ->
                AddressGroupSection(
                    group = group,
                    selectedAddress = selectedAddress,
                    onAddressSelected = onAddressSelected
                )
                VerticalSpacer(Spacing.SMALL)
            }
        }
    }

    @Composable
    private fun ContactHeader(contact: ContactModel) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(
                contactId = contact.contactId,
                isFavorite = contact.isFavorite,
                iconString = contact.avatar
            )

            Spacer(modifier = Modifier.width(Spacing.SMALL))

            Column {
                Text(
                    text = contact.contactName,
                    style = MangalaTypography.Size17SemiBold(),
                    color = MaterialTheme.mangalaColors.textPrimary
                )
                Text(
                    text = "Select address to send to",
                    style = MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary
                )
            }
        }
    }

    @Composable
    private fun AddressGroupSection(
        group: BlockchainAddressGroup,
        selectedAddress: WalletAddressWithNetworkModel?,
        onAddressSelected: (WalletAddressWithNetworkModel) -> Unit
    ) {
        Column {
            // Blockchain header
            Text(
                text = "${group.blockchainName} (${group.blockchainSymbol})",
                style = MangalaTypography.Size14SemiBold(),
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier.padding(bottom = Spacing.TINY)
            )

            // Addresses
            group.addresses.forEach { address ->
                AddressItem(
                    address = address,
                    isSelected = selectedAddress?.id == address.id,
                    onSelected = { onAddressSelected(address) }
                )
                Spacer(modifier = Modifier.height(Spacing.TINY))
            }
        }
    }

    @Composable
    private fun AddressItem(
        address: WalletAddressWithNetworkModel,
        isSelected: Boolean,
        onSelected: () -> Unit
    ) {
        val borderColor = if (isSelected) {
            MaterialTheme.mangalaColors.borderHighlight
        } else {
            SolidColor(MaterialTheme.mangalaColors.border)
        }

        val shape = RoundedCornerShape(CornerRadius.Medium)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, brush = borderColor, shape = shape)
                .clip(shape)
                .clickable { onSelected() }
                .padding(Dimensions.Padding.default),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelected
            )

            Spacer(modifier = Modifier.width(Spacing.SMALL))

            Column(modifier = Modifier.weight(1f)) {
                // Address alias or "Main Address"
                Text(
                    text = if (address.alias.isNullOrBlank()) {
                        if (address.isDefault) "Main Address" else "Address"
                    } else {
                        address.alias ?: "Address"
                    },
                    style = MangalaTypography.Size14SemiBold(),
                    color = MaterialTheme.mangalaColors.textPrimary
                )

                // Address
                Text(
                    text = address.address,
                    style = MangalaTypography.Size12Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Status indicators
                if (address.isDefault || address.isVerified == true) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.XTINY)
                    ) {
                        if (address.isDefault) {
                            Text(
                                text = "Primary",
                                style = MangalaTypography.Size10Medium(),
                                color = MaterialTheme.mangalaColors.textLink,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.mangalaColors.bgInnerCard,
                                        shape = RoundedCornerShape(CornerRadius.XTiny)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        if (address.isVerified == true) {
                            Text(
                                text = "Verified",
                                style = MangalaTypography.Size10Medium(),
                                color = MaterialTheme.mangalaColors.textLink,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.mangalaColors.bgInnerCard,
                                        shape = RoundedCornerShape(CornerRadius.XTiny)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}