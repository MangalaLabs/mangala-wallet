package com.mangala.wallet.features.send_base.sendcontact

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.presentation.components.PrivacyToggleButton
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.ToastFactory
import kotlinx.coroutines.flow.collectLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class SendContactListScreen(
    private val accountId: String,
    @Transient private val onContactSelected: ((ContactModel) -> Unit)? = null
) : BaseScreen<SendContactListScreenModel>(), KoinComponent {

    override val key: ScreenKey
        get() = "SendContactListScreen"

    override val screenName: String = "SEND_CONTACT_LIST"
    override val screenClassName: String = SendContactListScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun createScreenModel(): SendContactListScreenModel =
        getScreenModel(
            parameters = {
                parametersOf(accountId)
            }
        )

    @Composable
    override fun ScreenContent(screenModel: SendContactListScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val toastFactory = get<ToastFactory>()
        
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val contactsPaging = screenModel.contactsPagingFlow.collectAsLazyPagingItems()
        val contactsSearchQuery by screenModel.contactsSearchQuery.collectAsStateMultiplatform()
        val privacyModeEnabled by screenModel.privacyModeEnabled.collectAsStateMultiplatform()

        // Handle error states with toast messages
        LaunchedEffect(uiState.error) {
            uiState.error?.let { errorMessage ->
                toastFactory.show(errorMessage)
                screenModel.clearError()
            }
        }

        // Observe navigation events using MVI pattern
        LaunchedEffect(screenModel) {
            screenModel.navigationEvents.collectLatest { event ->
                when (event) {
                    is SendContactNavigationEvent.NavigateToSelectAddress -> {
                        val selectAddressScreen = ScreenRegistry.get(
                            SharedScreen.SelectContactAddressScreen(
                                contactId = event.contactId,
                                accountId = event.accountId
                            )
                        )
                        navigator.push(selectAddressScreen)
                    }
                    
                    is SendContactNavigationEvent.NavigateToStep3 -> {
                        val step3Screen = ScreenRegistry.get(
                            SharedScreen.Step3SelectAmountScreen(
                                accountId = event.accountId,
                                contactId = null,
                                address = event.address,
                                blockchainUid = event.blockchainUid,
                                amount = null
                            )
                        )
                        navigator.push(step3Screen)
                    }
                }
            }
        }

        OnboardingGradientBackground {
            MaxSizeColumn(
                modifier = Modifier
                    .safeDrawingPadding()
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = "Select Contact",
                    onBackClicked = navigator::pop,
                    trailingButton = {
                        PrivacyToggleButton(
                            isEnabled = privacyModeEnabled,
                            onToggle = screenModel::togglePrivacyMode,
                        )
                    }
                )

                SendContactsContent(
                    contactsPaging = contactsPaging,
                    searchQuery = contactsSearchQuery,
                    onSearchQueryChange = screenModel::updateContactsSearchQuery,
                    onContactClick = screenModel::onContactSelected,
                    privacyModeEnabled = privacyModeEnabled
                )
            }
        }
    }
}