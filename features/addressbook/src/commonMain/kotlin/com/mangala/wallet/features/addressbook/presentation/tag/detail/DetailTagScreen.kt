package com.mangala.wallet.features.addressbook.presentation.tag.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Add
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Delete
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.EditNew
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.presentation.components.ContactColumnWithActionRowAndMultipleBlockchainsRow
import com.mangala.wallet.features.addressbook.presentation.components.DeleteConfirmationDialog
import com.mangala.wallet.features.addressbook.presentation.components.PrivacyToggleButton
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.features.addressbook.presentation.tag.TagIcon
import com.mangala.wallet.features.addressbook.presentation.tag.add.AddTagScreen
import com.mangala.wallet.features.addressbook.utils.stringToColor
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.ui.MangalaPullToRefreshBox
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.ToastFactory
import mangalawalletpack.linear.Export
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

class DetailTagScreen(
    private val tagId: String,
) : BaseScreen<DetailTagScreenModel>() {

    override val screenName: String = "TAG_DETAIL"
    override val screenClassName: String = DetailTagScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun createScreenModel(): DetailTagScreenModel = getScreenModel { parametersOf(tagId) }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun ScreenContent(screenModel: DetailTagScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val toastFactory = koinInject<ToastFactory>()

        val privacyModeEnabled = screenModel.privacyModeEnabled.collectAsStateMultiplatform()
        val tagDetail = screenModel.tagResource.collectAsStateMultiplatform()
        val deleteTagState = screenModel.deleteTagResource.collectAsStateMultiplatform()
        val searchQuery = screenModel.searchQuery.collectAsStateMultiplatform()
        val contactsPaging = screenModel.contactPagingFlow.collectAsLazyPagingItems()

        var showDeleteTagConfirmation by remember { mutableStateOf(false) }

        // Handle navigation events
        LaunchedEffect(Unit) {
            screenModel.navigationEvents.collect { event ->
                when (event) {
                    is DetailTagNavigationEvent.NavigateBack -> {
                        navigator.pop()
                    }
                }
            }
        }

        // Check if deletion is in progress and navigate back immediately
        LaunchedEffect(screenModel.isDeleting) {
            if (screenModel.isDeleting) {
                navigator.pop()
            }
        }

        // Create reference to AddressSelectionScreen to display when needed
        val addressSelectionScreen = rememberScreen(
            SharedScreen.AddressSelectionScreen(
                tagId = tagId,
                initialSelectedContactIds = emptyList(),
                onApplySelections = { selectedIds ->
                    screenModel.refreshData()
                    contactsPaging.refresh()
                }
            )
        )

        LaunchedEffect(tagDetail) {
            val tagDetailValue = tagDetail.value
            if (tagDetailValue is Resource.Error) {
                toastFactory.show(
                    text = tagDetailValue.exception.message ?: "Failed to load tag details",
                )
            }
        }

        LaunchedEffect(deleteTagState) {
            val deleteStateValue = deleteTagState.value
            when (deleteStateValue) {
                is Resource.Loading -> {
                    // No action needed - loading state is handled by UI
                }

                is Resource.Success -> {
                    // Only show toast - navigation is handled by navigation events
                    toastFactory.show(text = "Tag deleted successfully")
                }

                is Resource.Error -> {
                    toastFactory.show(
                        text = deleteStateValue.exception.message ?: "Failed to delete tag"
                    )
                }
            }
        }

        MangalaBottomSheetNavigator(
            hideOnBackPress = true,
            sheetShape = RoundedCornerShape(topStart = CornerRadius.BottomSheet, topEnd = CornerRadius.BottomSheet),
            skipHalfExpanded = true, // Skip half-expanded state to always show full height
        ) { bottomSheetNavigator ->
            MangalaPullToRefreshBox(
                isRefreshing = contactsPaging.loadState.refresh is LoadState.Loading || tagDetail.value.isLoading(),
                onRefresh = {
                    screenModel.refreshData()
                    contactsPaging.refresh()
                },
            ) {
                MaxSizeColumn(
                    modifier = Modifier
                        .background(MaterialTheme.mangalaColors.bg)
                        .safeDrawingPadding()
                ) {
                    TopBar(
                        onBackClick = navigator::pop,
                        onEditClick = {
                            navigator.push(AddTagScreen(tagId = tagId))
                        },
                        privacyModeEnabled = privacyModeEnabled.value,
                        onPrivacyToggle = screenModel::togglePrivacyMode,
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dimensions.Padding.default),
                        contentPadding = PaddingValues(vertical = Dimensions.Padding.default),
                    ) {
                        item(key = "tag_info_card") {
                            TagInfoCard(
                                tagName = tagDetail.value.data?.name ?: "",
                                contactCount = tagDetail.value.data?.contactCount ?: 0,
                                tagColor = tagDetail.value.data?.color,
                                tagTextColor = tagDetail.value.data?.textColor,
                                tagIcon = tagDetail.value.data?.icon,
                                isLoading = tagDetail.value is Resource.Loading,
                            )
                        }

                        item(key = "tag_action_row_margin_top") {
                            Spacer(modifier = Modifier.height(Spacing.SMALL))
                        }

                        item(key = "tag_action_row") {
                            ActionButtonsRow(
                                onDeleteClick = { showDeleteTagConfirmation = true },
                                onAddClick = {
                                    bottomSheetNavigator.show(addressSelectionScreen)
                                },
                                onExportClick = {
                                    // TODO: Implement logic to export tag contacts
                                }
                            )
                        }

                        item(key = "search_bar_margin_top") {
                            Spacer(modifier = Modifier.height(Spacing.BASE))
                        }

                        item(key = "search_bar") {
                            SearchBar(
                                query = searchQuery.value,
                                onQueryChange = screenModel::updateSearchQuery,
                                placeholder = "Search contact"
                            )
                        }

                        item(key = "contact_list_title_margin_top") {
                            Spacer(modifier = Modifier.height(Spacing.BASE))
                        }

                        item(key = "contact_list_title") {
                            Text(
                                text = "Contacts list",
                                style = MangalaTypography.Size14SemiBold(),
                                color = MaterialTheme.mangalaColors.textPrimary,
                            )
                        }

                        item(key = "contact_list_margin_top") {
                            Spacer(modifier = Modifier.height(Spacing.SMALL))
                        }

                        if (contactsPaging.loadState.refresh is LoadState.Loading && contactsPaging.itemCount == 0)
                            item(key = "contact_list_loading") {
                                MaxWidthBox(
                                    modifier = Modifier
                                        .mangalaWalletPlaceholder(
                                            visible = true,
                                            shape = RoundedCornerShape(CornerRadius.Small),
                                            color = MaterialTheme.mangalaColors.skeletonBase,
                                            highlightColor = MaterialTheme.mangalaColors.skeletonShimmer,
                                        )
                                        .height(300.dp),
                                ) {}
                            }
                        else
                            items(
                                count = contactsPaging.itemCount,
                                key = contactsPaging.itemKey { it.contactId },
                            ) { index ->
                                val isFirst = index == 0
                                val isLast = index == contactsPaging.itemCount - 1 && contactsPaging.loadState.append.endOfPaginationReached

                                val shape = when {
                                    isFirst && isLast -> RoundedCornerShape(CornerRadius.Small)

                                    isFirst -> RoundedCornerShape(
                                        topStart = CornerRadius.Small,
                                        topEnd = CornerRadius.Small
                                    )

                                    isLast -> RoundedCornerShape(
                                        bottomStart = CornerRadius.Small,
                                        bottomEnd = CornerRadius.Small
                                    )

                                    else -> RectangleShape
                                }

                                val outerContentPadding = PaddingValues(
                                    start = Dimensions.Padding.default,
                                    end = Dimensions.Padding.default,
                                    top = if (isFirst) Dimensions.Padding.small else 0.dp,
                                    bottom = if (isLast) Dimensions.Padding.small else 0.dp
                                )

                                contactsPaging[index]?.let { contact ->
                                    TagContactItem(
                                        contact = contact,
                                        privacyModeEnabled = privacyModeEnabled.value,
                                        onQrCodeClick = { selectedContact ->
                                            // Navigate to ReceiveTokenScreen for contact QR
                                            try {
                                                val mapper = com.mangala.wallet.features.addressbook.presentation.mapper.AddressBookToReceiveMapper
                                                val networkType = mapper.mapToNetworkType(selectedContact.blockchainName)
                                                val blockchainUid = mapper.mapToBlockchainUid(selectedContact.blockchainName)
                                                
                                                val receiveScreen = cafe.adriel.voyager.core.registry.ScreenRegistry.get(
                                                    com.mangala.wallet.ui.SharedScreen.ReceiveTokenScreen(
                                                        accountId = null,
                                                        address = selectedContact.walletAddress,
                                                        networkType = networkType,
                                                        initialBlockchainUid = blockchainUid
                                                    )
                                                )
                                                navigator.push(receiveScreen)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        },
                                        shouldShowDivider = isLast.not(),
                                        shape = shape,
                                        outerContentPadding = outerContentPadding,
                                        onDeleteConfirmed = screenModel::removeContactFromTagOptimistically,
                                    )
                                }
                            }

                        if (contactsPaging.loadState.append is LoadState.Loading) {
                            item(key = "contacts_load_more") {
                                MaxWidthBox(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.mangalaColors.bgInnerCard,
                                            shape = RoundedCornerShape(
                                                bottomStart = CornerRadius.Small,
                                                bottomEnd = CornerRadius.Small
                                            )
                                        )
                                        .padding(vertical = Dimensions.Padding.small)
                                ) {
                                    MangalaCircularProgressIndicator(
                                        color = MaterialTheme.mangalaColors.iconPrimary,
                                        size = 24.dp,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Delete tag confirmation dialog
            if (showDeleteTagConfirmation) {
                DeleteConfirmationDialog(
                    title = "Delete Tag?",
                    message = "Are you sure you want to delete this tag? This action cannot be undone.",
                    onConfirm = screenModel::deleteTag,
                    onDismiss = {
                        showDeleteTagConfirmation = false
                    }
                )
            }
        }
    }

    @Composable
    private fun TopBar(
        onBackClick: () -> Unit,
        onEditClick: () -> Unit,
        privacyModeEnabled: Boolean = false,
        onPrivacyToggle: () -> Unit = {},
    ) {
        MangalaWalletTopBarCenteredTitle(
            title = "Tag Detail",
            textColor = MaterialTheme.mangalaColors.textPrimary,
            backIconTint = MaterialTheme.mangalaColors.iconPrimary,
            onBackClicked = onBackClick,
            trailingButton = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PrivacyToggleButton(
                        isEnabled = privacyModeEnabled,
                        onToggle = onPrivacyToggle,
                    )

                    IconButton(
                        onClick = onEditClick,
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.EditNew,
                            contentDescription = "Menu",
                            tint = MaterialTheme.mangalaColors.iconPrimary,
                        )
                    }
                }
            }
        )
    }

    @Composable
    private fun TagInfoCard(
        tagName: String,
        contactCount: Int,
        tagColor: String?,
        tagTextColor: String?,
        tagIcon: String?,
        isLoading: Boolean = false,
        modifier: Modifier = Modifier,
    ) {
        // Read-only tag profile card
        val backgroundColor = remember(tagColor) {
            if (tagColor?.toIntOrNull() != null) {
                ColorsNew.indexToColor(tagColor.toInt())
            } else {
                stringToColor(tagColor, ColorsNew.tagTeal)
            }
        }

        val textColor = remember(tagTextColor) {
            if (tagTextColor?.toIntOrNull() != null) {
                ColorsNew.indexToColor(tagTextColor.toInt())
            } else {
                stringToColor(tagTextColor, Color.White)
            }
        }

        val contactCountString = remember(contactCount) {
            if (contactCount == 1) "1 Contact" else "$contactCount Contacts"
        }

        Card(
            modifier = modifier.mangalaWalletPlaceholder(
                visible = isLoading,
                shape = RoundedCornerShape(CornerRadius.Small),
                color = MaterialTheme.mangalaColors.skeletonBase,
                highlightColor = MaterialTheme.mangalaColors.skeletonShimmer,
            ),
            shape = RoundedCornerShape(CornerRadius.Small),
            elevation = 0.dp,
            backgroundColor = MaterialTheme.mangalaColors.bgInnerCard
        ) {
            MaxWidthColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL),
                modifier = Modifier.padding(
                    vertical = Dimensions.Padding.small,
                    horizontal = Dimensions.Padding.default
                )
            ) {
                TagIcon(
                    name = tagName,
                    icon = tagIcon,
                    backgroundColor = backgroundColor,
                    contentColor = textColor,
                    size = 96.dp,
                    useFullOpacityBackground = true
                )

                Text(
                    text = tagName,
                    style = MangalaTypography.Size17SemiBold(),
                    color = MaterialTheme.mangalaColors.textPrimary
                )

                Text(
                    text = contactCountString,
                    style = MangalaTypography.Size12Medium(),
                    color = MaterialTheme.mangalaColors.textOnBadge,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.mangalaColors.bgBadge,
                            shape = RoundedCornerShape(CornerRadius.Medium)
                        )
                        .padding(
                            vertical = Dimensions.Padding.quarter,
                            horizontal = Dimensions.Padding.half
                        )
                )
            }
        }
    }

    @Composable
    private fun ActionButtonsRow(
        onDeleteClick: () -> Unit,
        onAddClick: () -> Unit,
        onExportClick: () -> Unit,
    ) {
        Card(
            shape = RoundedCornerShape(CornerRadius.Small),
            elevation = 0.dp,
            backgroundColor = MaterialTheme.mangalaColors.bgInnerCard
        ) {
            MaxWidthRow(
                modifier = Modifier
                    .padding(
                        vertical = Dimensions.Padding.small,
                        horizontal = Dimensions.Padding.default
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    icon = MangalaWalletPack.Delete,
                    label = "Delete Tag",
                    iconTint = ColorsNew.error_600,
                    textColor = ColorsNew.error_600,
                    backgroundColor = ColorsNew.error_50,
                    onClick = onDeleteClick,
                )

                ActionButton(
                    icon = MangalaWalletPack.Add,
                    label = "Add Contact",
                    iconTint = ColorsNew.success_700,
                    textColor = ColorsNew.success_700,
                    backgroundColor = ColorsNew.success_50,
                    onClick = onAddClick,
                )

//                ActionButton(
//                    icon = MangalaWalletPack.Export,
//                    label = "Export List",
//                    iconTint = MaterialTheme.mangalaColors.iconPrimary,
//                    textColor = MaterialTheme.mangalaColors.textPrimary,
//                    backgroundColor = MaterialTheme.mangalaColors.bg,
//                    onClick = onExportClick,
//                )
            }
        }
    }

    @Composable
    private fun ActionButton(
        icon: ImageVector,
        label: String,
        iconTint: Color,
        backgroundColor: Color,
        textColor: Color,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .clickable(onClick = onClick)
        ) {
            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(backgroundColor),
                onClick = onClick,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(Dimensions.IconSize_20)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.XTINY))

            Text(
                text = label,
                style = MangalaTypography.Size12Medium(),
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    @Composable
    private fun TagContactItem(
        contact: ContactWithMultipleBlockchainsModel,
        shape: Shape,
        outerContentPadding: PaddingValues,
        shouldShowDivider: Boolean = true,
        privacyModeEnabled: Boolean,
        onQrCodeClick: (ContactModel) -> Unit,
        onDeleteConfirmed: (String) -> Unit,
    ) {
        var isOpenDeleteConfirmation by remember { mutableStateOf(false) }

        val dividerColor = MaterialTheme.mangalaColors.border

        val onDeleteConfirmedRef = rememberUpdatedState(onDeleteConfirmed)
        val onDeleteConfirmedRemembered: () -> Unit = remember(contact) {
            {
                onDeleteConfirmedRef.value(contact.contactId)
            }
        }

        if (isOpenDeleteConfirmation) {
            DeleteConfirmationDialog(
                title = "Remove contact from tag?",
                message = "Are you sure you want to remove ${contact.contactName} from this tag?",
                confirmButtonText = "Remove",
                onConfirm = onDeleteConfirmedRemembered,
                onDismiss = {
                    isOpenDeleteConfirmation = false
                }
            )
        }

        MaxWidthRow(
            modifier = Modifier
                .background(
                    color = MaterialTheme.mangalaColors.bgInnerCard,
                    shape = shape
                )
                .padding(outerContentPadding)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()

                        if (shouldShowDivider)
                            drawLine(
                                color = dividerColor,
                                start = Offset(0f, size.height - 1.dp.toPx()),
                                end = Offset(size.width, size.height - 1.dp.toPx()),
                                strokeWidth = 1.dp.toPx()
                            )
                    }
                }
                .padding(
                    top = Dimensions.Padding.small,
                    bottom = Dimensions.Padding.small,
                    start = Dimensions.Padding.default,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                ContactColumnWithActionRowAndMultipleBlockchainsRow(
                    contact = contact,
                    privacyModeEnabled = privacyModeEnabled,
                    isDisplayStar = true,
                    onQrCodeClick = onQrCodeClick,
                )
            }

            IconButton(
                onClick = {
                    isOpenDeleteConfirmation = true
                },
            ) {
                Icon(
                    imageVector = MangalaWalletPack.Delete,
                    contentDescription = "Delete Contact",
                    tint = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                    modifier = Modifier.size(Dimensions.IconSize_20)
                )
            }
        }
    }
}
