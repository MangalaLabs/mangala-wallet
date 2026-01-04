package com.mangala.wallet.features.addressbook.presentation.group.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.EditNew
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.MoreVertical
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Trash
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.EditButton
import com.mangala.wallet.features.addressbook.icon.contacticon.Qrcode
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SendButton
import com.mangala.wallet.features.addressbook.presentation.components.DeleteConfirmationDialog
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.features.addressbook.presentation.components.PrivacyToggleButton
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.features.addressbook.presentation.group.GroupIcon
import com.mangala.wallet.features.addressbook.presentation.group.create.CreateGroupScreenNew
import com.mangala.wallet.features.addressbook.presentation.group.getColorFromGroupColor
import com.mangala.wallet.features.addressbook.utils.WalletAddressFormatter
import com.mangala.wallet.features.addressbook.presentation.components.UnifiedAddressItem
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.ui.rememberSystemUiController
import kotlinx.coroutines.launch
import mangalawalletpack.linear.Export
import org.koin.core.parameter.parametersOf
import androidx.compose.runtime.SideEffect
import kotlin.jvm.Transient
import com.mangala.wallet.ui.component.KeyboardDismissBox

class GroupDetailScreenNew(
    @Transient private val groupId: String,
    @Transient private val onBackClick: () -> Unit = {},
    @Transient private val onEditClick: () -> Unit = {},
    @Transient private val onAddAddressClick: () -> Unit = {},
    @Transient private val onSendClick: () -> Unit = {},
    @Transient private val onExportClick: () -> Unit = {},
    @Transient private val onQrCodeClick: () -> Unit = {},
) : BaseScreen<GroupDetailScreenModelNew>() {

    @Composable
    override fun createScreenModel(): GroupDetailScreenModelNew = getScreenModel { parametersOf(groupId) }

    override val screenName: String = "GROUP_DETAIL_NEW"
    override val screenClassName: String = GroupDetailScreenNew::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun ScreenContent(screenModel: GroupDetailScreenModelNew) {
        val state by screenModel.state.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        var showDeleteDialog by remember { mutableStateOf(false) }

        // Handle navigation events
        LaunchedEffect(Unit) {
            screenModel.navigationEvents.collect { event ->
                when (event) {
                    is GroupDetailNavigationEvent.NavigateBack -> {
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

        LaunchedEffect(groupId) {
            if (state.group?.id != groupId) {
                screenModel.fetchGroupDetails(groupId)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.mangalaColors.bg)
                .safeDrawingPadding()
        ) {
            when {
                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error loading group",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.mangalaColors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.error ?: "Unknown error",
                                fontSize = 14.sp,
                                color = MaterialTheme.mangalaColors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { screenModel.fetchGroupDetails(groupId) }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }

                // Show content if group is available
                state.group != null -> {
                    GroupDetailScreenContent(
                        state = state,
                        group = state.group!!, // Pass non-null group
                        privacyModeEnabled = screenModel.privacyModeEnabled.collectAsState().value,
                        onPrivacyToggle = { screenModel.togglePrivacyMode() },
                        onBackClick = onBackClick,
                        onEditClick = {
                            navigator.push(
                                CreateGroupScreenNew(
                                    groupId = groupId,
                                    onBackClick = {
                                        // Refresh group details after edit
                                        screenModel.fetchGroupDetails(groupId)
                                    }
                                )
                            )
                        },
                        onDeleteClick = { showDeleteDialog = true },
                        onAddAddressClick = onAddAddressClick,
                        onSendClick = onSendClick,
                        onExportClick = onExportClick,
                        onQrCodeClick = onQrCodeClick,
                        onQrCodeAddressClick = { walletAddress ->
                            // Navigate to ReceiveTokenScreen for address QR display
                            try {
                                val wallet = state.wallets.find { it.walletAddress == walletAddress }
                                val mapper = com.mangala.wallet.features.addressbook.presentation.mapper.AddressBookToReceiveMapper
                                val networkType = mapper.mapToNetworkType(wallet?.blockchainTypeSymbol)
                                val blockchainUid = mapper.mapToBlockchainUid(wallet?.blockchainTypeSymbol)
                                
                                val receiveScreen = cafe.adriel.voyager.core.registry.ScreenRegistry.get(
                                    com.mangala.wallet.ui.SharedScreen.ReceiveTokenScreen(
                                        accountId = null,
                                        address = walletAddress,
                                        networkType = networkType,
                                        initialBlockchainUid = blockchainUid
                                    )
                                )
                                navigator.push(receiveScreen)
                            } catch (e: Exception) {
                                // Handle error - blockchain type not supported for QR
                                e.printStackTrace()
                            }
                        },
                        onSearchQueryChange = { query ->
                            screenModel.searchWallets(query)
                        },
                        onLoadMore = {
                            coroutineScope.launch {
                                screenModel.loadMore(groupId)
                            }
                        }
                    )
                }
                // Default: Show loading (when group is null AND no error)
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        MangalaCircularProgressIndicator( )
                    }
                }
            }
            
            // Delete confirmation dialog
            if (showDeleteDialog && state.group != null) {
                DeleteConfirmationDialog(
                    title = "Delete group?",
                    message = "Are you sure you want to delete ${state.group!!.name}? This action cannot be undone.",
                    onConfirm = {
                        showDeleteDialog = false
                        // Delete group - navigation will be handled by screenModel
                        screenModel.deleteGroup(state.group!!.id)
                    },
                    onDismiss = {
                        showDeleteDialog = false
                    }
                )
            }
        }
        // Show loading indicator while data is being loaded
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MangalaCircularProgressIndicator()
            }
            return
        }

        // Show error state if group loading failed
        if (state.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error loading group",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.mangalaColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error ?: "Unknown error",
                        fontSize = 14.sp,
                        color = MaterialTheme.mangalaColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { screenModel.fetchGroupDetails(groupId) }
                    ) {
                        Text("Retry")
                    }
                }
            }
            return
        }

        // Check if group data is available
        val group = state.group
        if (group == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Group not found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.mangalaColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onBackClick
                    ) {
                        Text("Go Back")
                    }
                }
            }
            return
        }

        // Once data is loaded and valid, show the screen content
        GroupDetailScreenContent(
            state = state,
            group = group, // Pass non-null group
            privacyModeEnabled = screenModel.privacyModeEnabled.collectAsState().value,
            onPrivacyToggle = { screenModel.togglePrivacyMode() },
            onBackClick = onBackClick,
            onEditClick = {
                navigator.push(
                    CreateGroupScreenNew(
                        groupId = groupId,
                        onBackClick = {
                            // Refresh group details after edit
                            screenModel.fetchGroupDetails(groupId)
                        }
                    )
                )
            },
            onDeleteClick = { showDeleteDialog = true },
            onAddAddressClick = onAddAddressClick,
            onSendClick = onSendClick,
            onExportClick = onExportClick,
            onQrCodeClick = onQrCodeClick,
            onQrCodeAddressClick = { walletAddress ->
                // Navigate to ReceiveTokenScreen for address QR display
                try {
                    val wallet = state.wallets.find { it.walletAddress == walletAddress }
                    val mapper = com.mangala.wallet.features.addressbook.presentation.mapper.AddressBookToReceiveMapper
                    val networkType = mapper.mapToNetworkType(wallet?.blockchainTypeSymbol)
                    val blockchainUid = mapper.mapToBlockchainUid(wallet?.blockchainTypeSymbol)
                    
                    val receiveScreen = cafe.adriel.voyager.core.registry.ScreenRegistry.get(
                        com.mangala.wallet.ui.SharedScreen.ReceiveTokenScreen(
                            accountId = null,
                            address = walletAddress,
                            networkType = networkType,
                            initialBlockchainUid = blockchainUid
                        )
                    )
                    navigator.push(receiveScreen)
                } catch (e: Exception) {
                    // Handle error - blockchain type not supported for QR
                    e.printStackTrace()
                }
            },
            onSearchQueryChange = { query ->
                screenModel.searchWallets(query)
            },
            onLoadMore = {
                coroutineScope.launch {
                    screenModel.loadMore(groupId)
                }
            }
        )
    }

    @Composable
    fun GroupDetailScreenContent(
        state: GroupDetailScreenModelNew.GroupDetailState,
        group: GroupModel, // Accept non-null group parameter
        privacyModeEnabled: Boolean = false,
        onPrivacyToggle: () -> Unit = {},
        onBackClick: () -> Unit = {},
        onEditClick: () -> Unit = {},
        onDeleteClick: () -> Unit = {},
        onAddAddressClick: () -> Unit = {},
        onSendClick: () -> Unit = {},
        onExportClick: () -> Unit = {},
        onQrCodeClick: () -> Unit = {},
        onQrCodeAddressClick: (String) -> Unit = {},
        onSearchQueryChange: (String) -> Unit = {},
        onLoadMore: () -> Unit = {}
    ) {

        KeyboardDismissBox(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.mangalaColors.bg)
                .safeDrawingPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            ) {
                // Top Navigation Bar
                GroupDetailNavBar(
                    title = group.name,
                    onBackClick = onBackClick,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    privacyModeEnabled = privacyModeEnabled,
                    onPrivacyToggle = onPrivacyToggle
                )

                // Main content with optimized spacing strategy
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(Dimensions.Padding.default),
                    verticalArrangement = Arrangement.spacedBy(Spacing.TINY) // Unified spacing management
                ) {
                    item {
                        // Group Info Card
                        GroupInfoCard(group)
                    }

                    item {
                        VerticalSpacer(Spacing.TINY)
                        // Quick Actions
                        GroupActionsCard(
                            onSendClick = onSendClick,
                            onExportClick = onExportClick,
                            onQrCodeClick = onQrCodeClick
                        )
                    }

                    item {
                        VerticalSpacer(Spacing.TINY)
                        // Search and Filter Row
                        SearchBar(
                            query = state.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            placeholder = "Search member",
                        )
                    }

                    item {
                        // Address List Header
                        AddressListHeader(count = group.walletAddressCount)
                    }

                    // Display wallet addresses from GroupWallet objects
                    if (state.wallets.isNotEmpty()) {
                        items(state.wallets) { wallet ->

                            UnifiedAddressItem(
                                blockchainSymbol = wallet.blockchainTypeSymbol,
                                // If alias exists, use it; otherwise use contact name as main title
                                walletAlias = wallet.walletAlias?.takeIf { it.isNotBlank() } ?: "",
                                contactName = wallet.contactName,
                                walletAddress = WalletAddressFormatter.formatForDisplay(wallet.walletAddress),
                                fullWalletAddress = wallet.walletAddress,
                                showCard = true, // Show card in address list
                                showCheckbox = false, // No checkbox in address list
                                onCopyClick = null, // Handle overlay message internally
                                onQrCodeClick = { onQrCodeAddressClick(wallet.walletAddress) },
                                privacyModeEnabled = privacyModeEnabled,
                                isSensitive = false, // Groups might not have individual sensitivity by default
                            )
                        }

                        // Show loading indicator at the bottom when loading more
                        if (state.canLoadMore) {
                            item {
                                if (state.isLoadingMore) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.mangalaColors.iconPrimary
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                LaunchedEffect(Unit) {
                                    onLoadMore()
                                }
                            }
                        }
                    } else {
                        item {
                            EmptyAddressList()
                        }
                    }
                }
            }

            // Clipboard message is now handled by overlay in AddressListItem component
        }
    }
}

@Composable
fun GroupDetailNavBar(
    title: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    privacyModeEnabled: Boolean = false,
    onPrivacyToggle: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .safeDrawingPadding()
            .padding(horizontal = Dimensions.Padding.default, vertical = 12.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(24.dp).align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = MangalaWalletPack.IcBack,
                contentDescription = "Back",
                tint = MaterialTheme.mangalaColors.iconPrimary // Use semantic color
            )
        }

        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = MaterialTheme.mangalaColors.textPrimary
            )
        }

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Privacy Toggle Button
            PrivacyToggleButton(
                isEnabled = privacyModeEnabled,
                onToggle = onPrivacyToggle,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Three-dots menu button
            var showDropdown by remember { mutableStateOf(false) }

            IconButton(
                onClick = { showDropdown = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = MangalaWalletPack.MoreVertical,
                    contentDescription = "More options",
                    tint = MaterialTheme.mangalaColors.iconPrimary,
                )
            }

            // Dropdown menu
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                containerColor = MaterialTheme.mangalaColors.bgInnerCard,
                shape = RoundedCornerShape(CornerRadius.Medium),
                offset = DpOffset((-8).dp, 0.dp)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Edit group",
                            style = MangalaTypography.Size14Regular(),
                            color = MaterialTheme.mangalaColors.textPrimary
                        )
                    },
                    onClick = {
                        showDropdown = false
                        onEditClick()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ContactIcon.EditButton,
                            contentDescription = "Edit",
                            tint = MaterialTheme.mangalaColors.iconPrimary
                        )
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Delete",
                            style = MangalaTypography.Size14Regular(),
                            color = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                        )
                    },
                    onClick = {
                        showDropdown = false
                        onDeleteClick()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = MangalaWalletPack.Trash,
                            contentDescription = "Delete",
                            tint = MaterialTheme.mangalaColors.buttonDestructiveContainer
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun GroupInfoCard(group: GroupModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.mangalaColors.bgInnerCard)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            // Group Avatar
            GroupIcon(
                name = group.name,
                icon = group.icon,
                backgroundColor = getColorFromGroupColor(group.color),
                modifier = Modifier.size(96.dp),
                size = 96.dp
            )

            // Group Name
            Text(
                text = group.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            // Group Tags - using a Box + Column to create a wrapping layout
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    // First row - possibly more tags fit on this row
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        // Security Level Tag
                        GroupTag(text = "${group.securityLevel} Security")
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Privacy Level Tag
                        GroupTag(text = "${group.privacyLevel} Group")
                    }
                    
                    // Second row - for additional tags
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Main blockchain Tag if available
                        if (group.mainBlockchainSymbol != null) {
                            GroupTag(text = group.mainBlockchainSymbol)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        
                        // Addresses count Tag
                        GroupTag(text = "${group.walletAddressCount} Address")
                    }
                }
            }
        }
    }
}

@Composable
fun GroupTag(
    text: String,
    backgroundColor: Color = Color(0xFFEFD7FB), // Light purple background
    textColor: Color = Color(0xFF4D0570), // Dark purple text
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun GroupActionsCard(
    onSendClick: () -> Unit,
    onExportClick: () -> Unit,
    onQrCodeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.Small),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.mangalaColors.bgInnerCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                text = "Send",
                icon = MangalaWalletPack.SendButton,
                backgroundColor = MaterialTheme.mangalaColors.bg,
                iconTint = MaterialTheme.mangalaColors.iconPrimary,
                onClick = onSendClick,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))
            
            ActionButton(
                text = "Export",
                icon = MangalaWalletPack.Export,
                backgroundColor = MaterialTheme.mangalaColors.bg,
                iconTint = MaterialTheme.mangalaColors.iconPrimary,
                onClick = onExportClick,
                modifier = Modifier.weight(1f)
            )

            // QR Code button hidden until feature implementation
            /*
            Spacer(modifier = Modifier.weight(1f))
            
            ActionButton(
                text = "QR Code",
                icon = ContactIcon.Qrcode,
                backgroundColor = MaterialTheme.mangalaColors.bg.copy(alpha = 0.5f),
                iconTint = MaterialTheme.mangalaColors.iconPrimary.copy(alpha = 0.5f),
                onClick = { 
                    // Group QR feature is temporarily disabled - no scan implementation yet
                },
                modifier = Modifier.weight(1f),
                enabled = false
            )
            */
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(backgroundColor, RoundedCornerShape(100))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (enabled) MaterialTheme.mangalaColors.textPrimary else MaterialTheme.mangalaColors.textPrimary.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SearchAndFilterRow(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Field with fixed height
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.mangalaColors.bgInnerCard,
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.mangalaColors.iconPrimary,
                    modifier = Modifier.size(20.dp)
                )
                
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        color = MaterialTheme.mangalaColors.textPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .fillMaxHeight(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search member",
                                    color = MaterialTheme.mangalaColors.textSecondary,
                                    fontSize = 13.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
        
        // Filter Button
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.mangalaColors.bgInnerCard,
            modifier = Modifier.size(36.dp),
            onClick = onFilterClick
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.mangalaColors.iconPrimary
                )
            }
        }
    }
}

@Composable
fun AddressListHeader(count: Int = 0) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(Spacing.TINY),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Address list ($count)",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.mangalaColors.textPrimary
        )
    }
}

@Composable
fun EmptyAddressList() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    ) {
        Text(
            text = "No wallet addresses found in this group",
            fontSize = 14.sp,
            color = MaterialTheme.mangalaColors.textSecondary
        )
    }
}