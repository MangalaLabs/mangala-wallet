package com.mangala.wallet.features.addressbook.presentation.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

/**
 * Screen for contact selection that replaces the previous ModalBottomSheet implementation
 * This is a standalone screen that can be navigated to using the Voyager BottomSheetNavigator
 */
class AddressSelectionScreen(
    private val tagId: String? = null,
    private val initialSelectedContactIds: List<String>,
    private val onApplySelections: (List<String>) -> Unit,
) : BaseScreen<AddressSelectionViewModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ADDRESS_SELECTION
    override val screenClassName: String = AddressSelectionScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): AddressSelectionViewModel {
        return getScreenModel(parameters = {
            parametersOf(
                tagId,
                initialSelectedContactIds,
            )
        })
    }

    @Composable
    override fun ScreenContent(screenModel: AddressSelectionViewModel) {
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val scope = rememberCoroutineScope()

        val searchQuery = screenModel.searchQuery.collectAsStateMultiplatform()
        val contactPaginated = screenModel.contactPagingFlow.collectAsLazyPagingItems()

        // Set up back press handling - important to only hide the bottom sheet, not pop the whole screen
        onBackPressedCallback = {
            bottomSheetNavigator.hide()
            false // Don't pop the screen, the navigator will handle it
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.mangalaColors.bg)
        ) {
            AddressSelectionSheetContent(
                contactsPaging = contactPaginated,
                onToggleSelectContact = { contact ->
                    screenModel.toggleContactSelectionWithState(
                        contactId = contact.contactWithMultipleBlockchainsModel.contactId,
                        isSelected = contact.isSelected
                    )
                },
                onApplySelections = {
                    scope.launch {
                        val finalSelectedIds = tagId?.let {
                            // Apply changes to database
                            screenModel.applyChanges()
                            // Get all selected contact IDs after changes
                            screenModel.getAllSelectedContactIds()
                        } ?: screenModel.getFinalSelectedContactIds()

                        // Call the lambda with selected IDs
                        onApplySelections(finalSelectedIds)
                        bottomSheetNavigator.hide()
                    }
                },
                onSearchQueryChanged = screenModel::updateSearchQuery,
                searchQuery = searchQuery.value,
                onQrCodeClick = { contactId ->
                    // Find the contact in the paging data to get address information
                    val contact = contactPaginated.itemSnapshotList.items
                        .find { it.contactWithMultipleBlockchainsModel.contactId == contactId }
                        ?.contactWithMultipleBlockchainsModel
                    
                    if (contact != null) {
                        try {
                            val mapper = com.mangala.wallet.features.addressbook.presentation.mapper.AddressBookToReceiveMapper
                            val networkType = mapper.mapToNetworkType(contact.primaryBlockchainName)
                            val blockchainUid = mapper.mapToBlockchainUid(contact.primaryBlockchainName)
                            
                            val receiveScreen = ScreenRegistry.get(
                                SharedScreen.ReceiveTokenScreen(
                                    accountId = null,
                                    address = contact.primaryWalletAddress,
                                    networkType = networkType,
                                    initialBlockchainUid = blockchainUid
                                )
                            )
                            navigator.push(receiveScreen)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )
        }
    }
} 