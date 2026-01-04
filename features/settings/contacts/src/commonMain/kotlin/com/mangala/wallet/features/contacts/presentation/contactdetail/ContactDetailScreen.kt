package com.mangala.wallet.features.contacts.presentation.contactdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextCanClick
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.DataInput
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

internal class ContactDetailScreen(private val contactId: Long) :
    BaseScreen<ContactDetailScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.CONTACT_DETAILS
    override val screenClassName: String = ContactDetailScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ContactDetailScreenModel {
        return getScreenModel(parameters = { parametersOf(contactId) })
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: ContactDetailScreenModel) {
        val parentNavigator = LocalNavigator.currentOrThrow

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet
            )
        ) {
            val navigator = LocalNavigator.currentOrThrow

            val uiState = screenModel.uiState.collectAsStateMultiplatform().value

            val contact = (uiState as? ContactDetailScreenUiState.Success)?.contact

            ContactDetailScreen(
                contact = contact,
                onEditClicked = {
                    val screen = ScreenRegistry.get(
                        SharedScreen.AddContactScreen(
                            id = contactId,
                            name = contact?.name ?: "",
                            address = contact?.address ?: "",
                            blockchainUid = contact?.blockchainUid ?: "",
                            isEdit = true
                        )
                    )
                    parentNavigator.push(screen)
                },
                onBackClicked = { parentNavigator.pop() }
            )
        }
    }

    @Composable
    private fun ContactDetailScreen(
        contact: ContactEntity?,
        onEditClicked: () -> Unit,
        onBackClicked: () -> Unit
    ) {
        Column(Modifier.fillMaxSize().background(Colors.cloudGray).windowInsetsPadding(WindowInsets.safeDrawing)) {
            MangalaWalletTopBar(
                text = MR.strings.all_contacts.desc().localized(),
                onBackClicked = onBackClicked,
                trailingButton = {
                    TextButton(
                        onClick = onEditClicked
                    ) {
                        TextCanClick(
                            text = MR.strings.all_edit.desc().localized(),
                            fontSize = FontType.SMALL,
                            enabled = contact != null
                        )
                    }
                }
            )
            Column(Modifier.padding(horizontal = Dimensions.Padding.default)) {
                val networkData = contact?.let { BlockchainType.fromUid(it.blockchainUid) }

                Spacer(Modifier.height(Spacing.SMALL))
                DataInput(
                    label = MR.strings.message_add_contact_name_label.desc().localized(),
                    inputField = {
                        Text(
                            text = contact?.name ?: "",
                            color = Colors.main1Text,
                            fontSize = FontType.REGULAR,
                            fontFamily = getSfProFamilyFont(FontWeight.Normal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(CornerRadius.Small))
                                .padding(horizontal = Dimensions.Padding.default, vertical = 10.dp)
                        )
                    }
                )

                Spacer(Modifier.height(Spacing.SMALL))
                networkData?.let {
                    DataInput(
                        label = MR.strings.all_address.desc().localized(),
                        inputField = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(CornerRadius.Small))
                                    .padding(
                                        horizontal = Dimensions.Padding.default,
                                        vertical = Dimensions.Padding.half
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                LocalImage(
                                    imageResource = networkData.localImage,
                                    modifier = Modifier.size(Dimensions.NetworkImageSizeEditContactScreen)
                                )

                                Spacer(Modifier.width(Spacing.TINY))
                                Column {
                                    TextNormal(
                                        text = networkData.name,
                                        color = Colors.main1Text,
                                    )

                                    Spacer(Modifier.height(Spacing.XTINY))
                                    val text = when (networkData.networkType) {
                                        NetworkType.EVM -> contact.formattedBip44Address()
                                        NetworkType.ANTELOPE -> contact.address
                                        else -> contact.formattedBip44Address()
                                    }

                                    TextDescription2(
                                        text = text,
                                        color = Colors.caption,
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }

}