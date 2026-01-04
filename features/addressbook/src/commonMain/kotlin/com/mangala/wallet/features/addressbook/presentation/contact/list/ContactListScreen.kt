package com.mangala.wallet.features.addressbook.presentation.contact.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactRecentTransactionModel
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.presentation.components.SendTokenHeaderWithPrivacy
import com.mangala.wallet.features.addressbook.presentation.components.TabNavigation
import com.mangala.wallet.features.addressbook.presentation.contact.detail.ContactDetailScreen
import com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel
import com.mangala.wallet.features.addressbook.presentation.mapper.AddressBookToReceiveMapper
import com.mangala.wallet.features.addressbook.presentation.contact.qr.ShowGroupQrScreen
import com.mangala.wallet.features.addressbook.presentation.contact.recent.ContactsContent
import com.mangala.wallet.features.addressbook.presentation.contact.recent.TransactionDetailScreen
import com.mangala.wallet.features.addressbook.presentation.contact.recent.recentTransactionContent
import com.mangala.wallet.features.addressbook.presentation.contact.scan.ScanToAddContactScreen
import com.mangala.wallet.features.addressbook.presentation.group.GroupsContent
import com.mangala.wallet.features.addressbook.presentation.group.create.CreateGroupScreenNew
import com.mangala.wallet.features.addressbook.presentation.group.detail.GroupDetailScreenNew
import com.mangala.wallet.features.addressbook.presentation.group.model.GroupGroupedByAlphabetUiModel
import com.mangala.wallet.features.addressbook.presentation.tag.TagTabContent
import com.mangala.wallet.features.addressbook.presentation.tag.add.AddTagScreen
import com.mangala.wallet.features.addressbook.presentation.tag.detail.DetailTagScreen
import com.mangala.wallet.features.addressbook.presentation.tag.model.TagGroupedByAlphabetUiModel
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.utils.ToastFactory
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.flow.collectLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.jvm.Transient

class ContactListScreen(
    @Transient private val navigateBack: () -> Unit = {},
    private val chainId: String? = null,
) : BaseScreen<ContactListScreenModel>(), KoinComponent {

    override val key: ScreenKey
        get() = "ContactListScreen"

    override val screenName: String = "CONTACT_LIST"
    override val screenClassName: String = ContactListScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ContactListScreenModel =
        getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: ContactListScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val toastFactory = get<ToastFactory>()
        val globalNavigator = LocalGlobalNavigator.current

        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val recentTransactionsPaging =
            screenModel.recentTransactionsPagingFlow.collectAsLazyPagingItems()
        val recentTransactionsSearchQuery by screenModel.recentTransactionsSearchQuery.collectAsStateMultiplatform()
        val favoritesPaging = screenModel.favoriteContactsPagingFlow.collectAsLazyPagingItems()
        val favoritesSearchQuery by screenModel.favoriteContactsSearchQuery.collectAsStateMultiplatform()
        val contactsPaging = screenModel.contactsPagingFlow.collectAsLazyPagingItems()
        val contactsSearchQuery by screenModel.contactsSearchQuery.collectAsStateMultiplatform()
        val groupsPaging = screenModel.groupsPagingFlow.collectAsLazyPagingItems()
        val groupsSearchQuery by screenModel.groupsSearchQuery.collectAsStateMultiplatform()
        val tagsPaging = screenModel.tagsPagingFlow.collectAsLazyPagingItems()
        val tagsSearchQuery by screenModel.tagsSearchQuery.collectAsStateMultiplatform()

        // Handle error states with toast messages
        LaunchedEffect(uiState.error) {
            uiState.error?.let { errorMessage ->
                toastFactory.show(errorMessage)
                screenModel.clearError()
            }
        }

        // Handle navigation events from ScreenModel using MVI pattern
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

        // Reusable navigation lambdas for better state hoisting
        val navigateToContactDetail = remember {
            { contact: ContactModel ->
                navigator.push(
                    ContactDetailScreen(
                        contactId = contact.contactId,
                        onBackClick = {
                            navigator.pop()
                        },
                        onSaveSuccess = { _ ->
                            // Paging automatically handles data refresh
                        },
                        privacyModeEnabled = screenModel.privacyModeEnabled.value
                    )
                )
            }
        }

        // Create lambda for transaction QR click with toastFactory closure
        val onTransactionQrClick = remember(toastFactory) {
            { transaction: ContactRecentTransactionModel ->
                try {
                    val mapper = AddressBookToReceiveMapper
                    val networkType = mapper.mapToNetworkType(transaction.blockchainName)
                    val blockchainUid = mapper.mapToBlockchainUid(transaction.blockchainName)
                    
                    val receiveScreen = ScreenRegistry.get(
                        SharedScreen.ReceiveTokenScreen(
                            accountId = null,
                            address = transaction.walletAddress,
                            networkType = networkType,
                            initialBlockchainUid = blockchainUid
                        )
                    )
                    navigator.push(receiveScreen)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle unsupported blockchain type
                    val message = when {
                        e.message?.contains("not supported for QR display") == true -> 
                            "QR code display is only supported for EVM and Antelope blockchains"
                        else -> 
                            "Unable to display QR code for ${transaction.blockchainName}"
                    }
                    toastFactory.show(message)
                }
            }
        }

        com.mangala.wallet.features.addressbook.presentation.security.SecureAuthProvider.Provider(
            rootNavigator = navigator
        ) {
            ContactListScreenContent(
                recentTransactionsPaging = recentTransactionsPaging,
                favoritesPaging = favoritesPaging,
                favoritesSearchQuery = favoritesSearchQuery,
                onFavoritesSearchQueryChange = screenModel::updateFavoriteContactsSearchQuery,
                contactsPaging = contactsPaging,
                contactsSearchQuery = contactsSearchQuery,
                onContactsSearchQueryChange = screenModel::updateContactsSearchQuery,
                groupsPaging = groupsPaging,
                groupsSearchQuery = groupsSearchQuery,
                onGroupsSearchQueryChange = screenModel::updateGroupsSearchQuery,
                tagsPaging = tagsPaging,
                tagsSearchQuery = tagsSearchQuery,
                onTagsSearchQueryChange = screenModel::updateTagsSearchQuery,
                onBackClicked = if (navigator.canPop) {
                    {
                        navigator.pop()
                    }
                } else null,
                onNavigateToContactDetail = navigateToContactDetail,
                onToggleFavorite = screenModel::toggleContactFavoriteOptimistically,
                onAddContact = {
                    navigator.push(
                        com.mangala.wallet.features.addressbook.presentation.contact.ContactScreen(
                            contactId = null, // Create mode
                            onBackClick = navigator::pop,
                            onSaveSuccess = {
                                navigator.pop()
                            }
                        )
                    )
                },
                onScanQrToAddContact = {
                    navigator.push(
                        ScanToAddContactScreen(
                            onBackClick = navigator::pop
                        )
                    )
                },
                onAddTransaction = {
                    navigator.push(
                        ScreenRegistry.get(
                            SharedScreen.Step2SelectNetwork(
                                accountId = "",
                                networkType = NetworkType.ANTELOPE.name,
                                address = null
                            )
                        )
                    )
                },
                onAddTag = {
                    navigator.push(
                        AddTagScreen(
                        )
                    )
                },
                onDeleteContact = screenModel::deleteContactOptimistically,
                navigator = navigator,
                screenModel = screenModel,
                onAddGroup = {
                    navigator.push(
                        CreateGroupScreenNew(
                            onBackClick = {
                                navigator.pop()
                                // Group list automatically refreshes due to reactive paging
                            }
                        )
                    )
                },
                navigateToGroupDetail = { group ->
                    navigator.push(
                        GroupDetailScreenNew(
                            groupId = group.id,
                            onEditClick = {
                                navigator.push(
                                    CreateGroupScreenNew(
                                        groupId = group.id,
                                        onBackClick = navigator::pop
                                    )
                                )
                            },
                            onBackClick = navigator::pop,
                            onQrCodeClick = {
                                navigator.push(
                                    ShowGroupQrScreen(
                                        groupId = group.id,
                                        onBackClick = navigator::pop
                                    )
                                )
                            }
                        )
                    )
                },
                onQrCodeClick = { contact ->
                    try {
                        val mapper = AddressBookToReceiveMapper
                        val networkType = mapper.mapToNetworkType(contact.blockchainName)
                        val blockchainUid = mapper.mapToBlockchainUid(contact.blockchainName)
                        
                        val receiveScreen = ScreenRegistry.get(
                            SharedScreen.ReceiveTokenScreen(
                                accountId = null,
                                address = contact.walletAddress,
                                networkType = networkType,
                                initialBlockchainUid = blockchainUid
                            )
                        )
                        navigator.push(receiveScreen)
                    } catch (e: Exception) {
                        // Handle unsupported blockchain type
                        val message = when {
                            e.message?.contains("not supported for QR display") == true -> 
                                "QR code display is only supported for EVM and Antelope blockchains"
                            else -> 
                                "Unable to display QR code for ${contact.blockchainName}"
                        }
                        toastFactory.show(message)
                    }
                },
                onClickTransactionDetail = { transaction ->
                    navigator.push(
                        TransactionDetailScreen(
                            transactionId = transaction.transactionId,
                        )
                    )
                },
                recentTransactionsSearchQuery = recentTransactionsSearchQuery,
                onRecentTransactionsSearchQueryChange = screenModel::updateRecentTransactionsSearchQuery,
                onClickTransaction = {
                    val step3Screen = ScreenRegistry.get(
                        SharedScreen.Step3SelectAmountScreen(
                            accountId = "",
                            contactId = null,
                            address = it.walletAddress,
                            blockchainUid = it.blockchainUid,
                            amount = it.lastTransactionAmount
                        )
                    )
                    navigator.push(step3Screen)
                },
                onTransactionQrClick = onTransactionQrClick,
                navigateToCreateAccount = {
                    val antelopeCreateAccountScreen =
                        ScreenRegistry.get(SharedScreen.AntelopeCreateAccountV2Screen)
                    navigator.push(antelopeCreateAccountScreen)
                },
                navigateToImportAccount = {
                    val antelopeImportAccountScreen =
                        ScreenRegistry.get(SharedScreen.ImportPrivateKeyScreen)
                    navigator.push(antelopeImportAccountScreen)
                },
                hasAnyImportedAccount = uiState.hasAnyImportedAccount,
            )
        }
    }

    @Composable
    private fun ContactListScreenContent(
        recentTransactionsPaging: LazyPagingItems<ContactRecentTransactionModel>,
        hasAnyImportedAccount: Boolean,
        favoritesPaging: LazyPagingItems<ContactWithMultipleBlockchainsModel>,
        favoritesSearchQuery: String?,
        onFavoritesSearchQueryChange: (String) -> Unit,
        contactsPaging: LazyPagingItems<ContactGroupedByAlphabetUiModel>,
        contactsSearchQuery: String?,
        onContactsSearchQueryChange: (String) -> Unit,
        groupsPaging: LazyPagingItems<GroupGroupedByAlphabetUiModel>,
        groupsSearchQuery: String?,
        onGroupsSearchQueryChange: (String) -> Unit,
        tagsPaging: LazyPagingItems<TagGroupedByAlphabetUiModel>,
        tagsSearchQuery: String?,
        onTagsSearchQueryChange: (String) -> Unit,
        onBackClicked: (() -> Unit)?,
        onNavigateToContactDetail: (ContactModel) -> Unit,
        onToggleFavorite: (ContactModel) -> Unit,
        onAddContact: () -> Unit,
        onScanQrToAddContact: () -> Unit = {},
        onAddTag: () -> Unit,
        onDeleteContact: (String) -> Unit,
        onClickTransactionDetail: (ContactRecentTransactionModel) -> Unit,
        onClickTransaction: (ContactRecentTransactionModel) -> Unit,
        onAddTransaction: () -> Unit,
        navigateToGroupDetail: (GroupModel) -> Unit,
        onAddGroup: () -> Unit = {},
        onQrCodeClick: (ContactModel) -> Unit = {}, // Thêm callback cho QR code
        navigateToImportAccount: () -> Unit,
        navigateToCreateAccount: () -> Unit,
        navigator: Navigator,
        screenModel: ContactListScreenModel,
        recentTransactionsSearchQuery: String?,
        onRecentTransactionsSearchQueryChange: (String) -> Unit,
        onTransactionQrClick: (ContactRecentTransactionModel) -> Unit = {},
    ) {
        // Sử dụng currentTabIndex từ ScreenModel
        val currentTabIndex by screenModel.currentTabIndex.collectAsStateMultiplatform()
        val privacyModeEnabled by screenModel.privacyModeEnabled.collectAsStateMultiplatform()

        val fabAction by derivedStateOf {
            when (currentTabIndex) {
                0 -> { { screenModel.setCurrentTab(2) } } // Favorites tab -> Navigate to Contacts tab
                2 -> onAddContact
                1 -> { if (hasAnyImportedAccount) onAddTransaction else null }
//                3 -> onAddGroup
                3 -> onAddTag
                else -> {
                    if (hasAnyImportedAccount) onAddTransaction else null
                }
            }
        }
        MaxSizeColumn(
            modifier = Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .safeDrawingPadding()
        ) {
            SendTokenHeaderWithPrivacy(
                onBackClick = onBackClicked,
                addClick = fabAction,
                privacyModeEnabled = privacyModeEnabled,
                onPrivacyToggle = screenModel::togglePrivacyMode
            )

            TabNavigation(
                selectedTabIndex = currentTabIndex,
                onTabSelected = screenModel::setCurrentTab
            )

            Spacer(modifier = Modifier.height(Dimensions.Padding.default))

            when (currentTabIndex) {
                0 -> FavoritesContent(
                    favoritesPaging = favoritesPaging,
                    searchQuery = favoritesSearchQuery,
                    onSearchQueryChange = onFavoritesSearchQueryChange,
                    onContactClick = screenModel::onContactSelected,
                    onQrCodeClick = onQrCodeClick,
                    onClickContactDetail = onNavigateToContactDetail,
                    onConfirmRemoveFromFavorites = onToggleFavorite,
                    onClickAddFavorite = {
                        screenModel.setCurrentTab(2)
                    },
                    privacyModeEnabled = privacyModeEnabled
                )

                1 -> {
                    val topFavoriteContacts by screenModel.topFavoriteContacts.collectAsStateMultiplatform()
                    recentTransactionContent(
                        favoriteContacts = topFavoriteContacts,
                        recentTransactionsPaging = recentTransactionsPaging,
                        searchQuery = recentTransactionsSearchQuery,
                        onSearchQueryChange = onRecentTransactionsSearchQueryChange,
                        onClickSendToken = onAddTransaction,
                        onContactClick = screenModel::onContactSelected,
                        onQrCodeClick = onTransactionQrClick,
                        onTransactionClick = onClickTransaction,
                        navigateToTransactionDetail = onClickTransactionDetail,
                        navigateToFavoriteContacts = {
                            screenModel.setCurrentTab(0)
                        },
                        privacyModeEnabled = privacyModeEnabled,
                        navigateToCreateAccount = navigateToCreateAccount,
                        navigateToImportAccount = navigateToImportAccount,
                        hasAnyImportedAccount = hasAnyImportedAccount
                    )
                }

                2 -> ContactsContent(
                    contactsPaging = contactsPaging,
                    searchQuery = contactsSearchQuery,
                    onSearchQueryChange = onContactsSearchQueryChange,
                    onAddContact = onAddContact,
                    onContactClick = onNavigateToContactDetail,
                    toggleFavorite = onToggleFavorite,
                    onConfirmDeleteContact = onDeleteContact,
                    clearLocalChanges = screenModel::clearLocalChanges,
                    onQrCodeClick = onQrCodeClick,
                    onClickSend = screenModel::onContactSelected,
                    privacyModeEnabled = privacyModeEnabled
                )

//                3 -> {
//                    GroupsContent(
//                        groupsPaging = groupsPaging,
//                        searchQuery = groupsSearchQuery,
//                        onSearchQueryChange = onGroupsSearchQueryChange,
//                        onAddGroup = onAddGroup,
//                        onGroupClick = navigateToGroupDetail,
//                        onConfirmDeleteGroup = screenModel::deleteGroupOptimistically,
//                        onGroupSend = { group ->
//                            // TODO: Navigate to group send screen
//                            // This is different from contact send - it's for sending to multiple recipients in a group
//                            onAddTransaction()
//                        },
//                        clearGroupLocalChanges = screenModel::clearGroupLocalChanges
//                    )
//                }

                3 ->
                    TagTabContent(
                        tagsPaging = tagsPaging,
                        searchQuery = tagsSearchQuery,
                        onSearchQueryChange = onTagsSearchQueryChange,
                        onAddTag = onAddTag,
                        onTagClick = { tag ->
                            navigator.push(DetailTagScreen(tag.id))
                        },
                        onConfirmDeleteTag = screenModel::deleteTagOptimistically,
                        clearTagLocalChanges = screenModel::clearTagLocalChanges
                    )
            }
        }
    }
}