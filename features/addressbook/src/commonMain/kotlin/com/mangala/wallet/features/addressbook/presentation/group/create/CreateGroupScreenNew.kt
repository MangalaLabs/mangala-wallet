package com.mangala.wallet.features.addressbook.presentation.group.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.localized
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Camera
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.features.addressbook.data.model.enum.PrivacyLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.di.AvatarFactory
import com.mangala.wallet.features.addressbook.domain.model.Avatar
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.DropDown
import com.mangala.wallet.features.addressbook.icon.contacticon.QuestionMark
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarPickerBottomSheetWrapper
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarRenderer
import com.mangala.wallet.features.addressbook.presentation.contact.create.NetworkSelectionBottomSheet
import com.mangala.wallet.features.addressbook.presentation.components.DeleteConfirmationDialog
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.desc.desc
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.KeyboardDismissBox

class CreateGroupScreenNew(
    private val groupId: String? = null,
    @Transient private val onBackClick: () -> Unit = {}
) : BaseScreen<CreateGroupScreenModel>() {

    override val screenName: String = if (groupId == null)
        "CREATE_GROUP"
    else
        "UPDATE_GROUP"
    override val screenClassName: String = "CreateGroupScreenNew"
    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun createScreenModel(): CreateGroupScreenModel {
        return getScreenModel { parametersOf(groupId) }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: CreateGroupScreenModel) {
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        // Track which wallet address is being deleted (if any)
        var showDeleteConfirmation by remember { mutableStateOf(false) }
        var selectedDeleteIndex by remember { mutableIntStateOf(-1) }

        // Create AvatarPickerViewModel outside of conditional to access from LaunchedEffect
        val onImageSelected = remember {
            { path: String ->
                screenModel.onIconSelected(path)
            }
        }

        val avatarPickerViewModel = remember {
            AvatarFactory.createAvatarPickerViewModel(onImageSelected)
        }

        // ✅ FIX: Proper navigation handling for both create and edit modes
        LaunchedEffect(uiState.saveCompleted, uiState.savedGroup) {
            if (uiState.saveCompleted && uiState.savedGroup != null) {
                val savedGroup = uiState.savedGroup!! // Safe to assert since we checked above

                // Save avatar to history if save was successful
                if (savedGroup.icon != null && !savedGroup.icon.startsWith("emoji:")) {
                    avatarPickerViewModel.onAvatarApplied(
                        savedGroup.icon,
                        "group",
                        savedGroup.id
                    )
                }
                // Navigate FIRST while loading is still showing
                navigator.pop()
                // Reset state AFTER navigation is complete
                // This ensures loading stays visible during navigation
                screenModel.resetState()
                onBackClick() // Call the callback to refresh detail screen
            }
        }

        // Show errors as snackbars
        LaunchedEffect(uiState.error) {
            uiState.error?.let { error ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = when (error) {
                            is ErrorState.LoadBlockchainTypesError -> "Failed to load blockchain networks: ${error.message}"
                            is ErrorState.LoadContactsError -> "Failed to load contacts: ${error.message}"
                            is ErrorState.LoadGroupError -> "Failed to load group: ${error.message}"
                            is ErrorState.SaveGroupError -> "Failed to save group: ${error.message}"
                            is ErrorState.ValidationError -> error.message
                            is ErrorState.MaxContactsLimitError -> error.message
                            is ErrorState.GroupNotFoundError -> "Group not found: ${error.message}"
                        },
                        duration = SnackbarDuration.Long
                    )
                    screenModel.clearError()
                }
            }
        }

        // Sử dụng MangalaBottomSheetNavigator thay vì trực tiếp Scaffold
        MangalaBottomSheetNavigator(
            hideOnBackPress = true,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            skipHalfExpanded = true, // Skip half-expanded state to always show full height
            sheetBackgroundColor = Color.Transparent,
        ) { bottomSheetNavigator ->

            // Collect StateFlow values
            val showContactBottomSheet by screenModel.showContactBottomSheet.collectAsState()

            // Theo dõi trạng thái hiển thị của bottom sheet
            LaunchedEffect(bottomSheetNavigator.isVisible) {
                // Nếu bottom sheet bị ẩn (do vuốt xuống) và trạng thái trong viewModel vẫn là true
                if (!bottomSheetNavigator.isVisible && showContactBottomSheet) {
                    screenModel.hideContactBottomSheet()
                }
            }

            // Sử dụng WalletAddressBottomSheet thay vì AddWalletToGroupBottomSheetScreen
            var showWalletBottomSheet by remember { mutableStateOf(false) }

            // Observe reactive flow - automatically updates when parent state changes
            // No need to differentiate between edit and create mode - use same source of truth
            val selectedWalletIds by screenModel.selectedWalletIdsFlow.collectAsState()

            // Luôn tạo ViewModel, nhưng sẽ tự chạy lại khi blockchainId thay đổi
            val bottomSheetViewModel = if (uiState.selectedBlockchainId != null) {
                getScreenModel<WalletAddressBottomSheetViewModel> {
                    parametersOf(
                        uiState.selectedBlockchainId,
                        screenModel.getAvailableContactsForBlockchainUseCase,
                        { selectedIds: List<String> -> screenModel.onWalletsSelected(selectedIds) },
                        selectedWalletIds
                    )
                }
            } else {
                null
            }

            // Refresh when blockchain changes OR when selectedWalletIds changes
            // This ensures bottom sheet stays in sync when wallets are deleted/added
            LaunchedEffect(uiState.selectedBlockchainId, selectedWalletIds) {
                if (bottomSheetViewModel != null && uiState.selectedBlockchainId != null) {
                    println("=== DEBUG: LaunchedEffect triggered - blockchain: ${uiState.selectedBlockchainId}, wallets count: ${selectedWalletIds.size} ===")
                    bottomSheetViewModel.refreshForBlockchain(
                        uiState.selectedBlockchainId!!,
                        selectedWalletIds
                    )
                }
            }

            // Quan sát thay đổi của contactBottomSheet để hiển thị hoặc ẩn bottom sheet
            LaunchedEffect(showContactBottomSheet) {
                showWalletBottomSheet = showContactBottomSheet
            }

            // Hiển thị bottom sheet thông qua bottomSheetNavigator khi cần
            // IMPORTANT: Use Flow.value to get LATEST state, not collected state (which might be stale)
            LaunchedEffect(showWalletBottomSheet, uiState.selectedBlockchainId) {
                if (showWalletBottomSheet && uiState.selectedBlockchainId != null) {
                    // ✅ FIX: Get the LATEST value from the flow, not the collected state
                    // This ensures we have fresh data even if state update happened asynchronously
                    val latestWalletIds = screenModel.selectedWalletIdsFlow.value

                    if (bottomSheetViewModel != null) {
                        println("=== DEBUG: Opening bottom sheet with ${latestWalletIds.size} selected wallets (latest from flow) ===")
                        // Refresh with LATEST data
                        bottomSheetViewModel.refreshForBlockchain(
                            uiState.selectedBlockchainId!!,
                            latestWalletIds
                        )

                        bottomSheetNavigator.show(
                            WalletSelectionBottomSheetScreen(
                                viewModel = bottomSheetViewModel,
                                initialSelectedWalletIds = latestWalletIds,
                                onDismiss = {
                                    screenModel.hideContactBottomSheet()
                                    showWalletBottomSheet = false
                                },
                                onWalletsSelected = { selectedWalletIds ->
                                    // Only update when user clicks "Add Address" button
                                    screenModel.onWalletsSelected(selectedWalletIds)
                                },
                                onSelectionChanged = null, // Disable real-time updates - only apply on confirm
                                onCopyClick = { index ->
                                    val addressToCopy =
                                        uiState.availableGroupWallets.getOrNull(index)?.walletAddress
                                    if (addressToCopy != null) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Address copied to clipboard",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                },
                                onQrCodeClick = { index ->
                                    val wallet = uiState.availableGroupWallets.getOrNull(index)
                                    if (wallet != null) {
                                        try {
                                            val mapper =
                                                com.mangala.wallet.features.addressbook.presentation.mapper.AddressBookToReceiveMapper
                                            val networkType =
                                                mapper.mapToNetworkType(wallet.blockchainTypeSymbol)
                                            val blockchainUid =
                                                mapper.mapToBlockchainUid(wallet.blockchainTypeSymbol)

                                            val receiveScreen =
                                                cafe.adriel.voyager.core.registry.ScreenRegistry.get(
                                                    com.mangala.wallet.ui.SharedScreen.ReceiveTokenScreen(
                                                        accountId = null,
                                                        address = wallet.walletAddress,
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
                        )
                    }
                }
            }

            // Biến state theo dõi việc hiển thị bottom sheet
            var showNetworkSelectionBottomSheet by remember { mutableStateOf(false) }

            // Nếu bottom sheet được hiển thị, render nó
            if (showNetworkSelectionBottomSheet) {
                NetworkSelectionBottomSheet(
                    blockchainEntities = uiState.blockchainTypes,
                    onBlockchainSelected = { blockchain ->
                        screenModel.onBlockchainSelected(blockchain.id)
                        showNetworkSelectionBottomSheet = false
                    },
                    onDismiss = {
                        showNetworkSelectionBottomSheet = false
                    }
                )
            }

            // Function to show network selection bottom sheet
            val showNetworkSelection = {
                if (!uiState.isEditMode) {
                    showNetworkSelectionBottomSheet = true
                } else {
                    // Show a snackbar notification that network can't be changed in edit mode
                    val errorMessage = "Network cannot be changed for existing groups"
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message = errorMessage)
                    }
                }
            }

            KeyboardDismissBox(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.mangalaColors.bg)
                    .safeDrawingPadding()
                    .imePadding() // Handle keyboard insets
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top Bar
                    TopBar(
                        title = if (uiState.isEditMode) MR.strings.label_edit_group.desc()
                            .localized() else MR.strings.label_create_group.desc()
                            .localized(),
                        onBackClick = {
                            screenModel.resetState()
                            navigator.pop()
                            onBackClick()
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.mangalaColors.bg)
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 16.dp)
                        ) {
                            // Avatar picker section
                            var showAvatarPicker by remember { mutableStateOf(false) }

                            val currentAvatarSource = remember(uiState.icon) {
                                AvatarSource.fromString(uiState.icon)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val avatar =
                                    Avatar(
                                        name = uiState.groupName,
                                        avatarSource = currentAvatarSource
                                    )

                                // Avatar display with camera icon
                                Box(
                                    modifier = Modifier.size(96.dp)
                                ) {
                                    AvatarRenderer.RenderAvatar(
                                        name = avatar.name,
                                        avatarSource = avatar.avatarSource,
                                        size = 96.dp
                                    )

                                    // Camera icon overlay
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.BottomEnd)
                                            .clickable { showAvatarPicker = true }
                                            .background(
                                                color = MaterialTheme.mangalaColors.buttonNeutralContainer,
                                                shape = CircleShape
                                            )
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = MangalaWalletPack.Camera,
                                            contentDescription = "Change avatar",
                                            tint = MaterialTheme.mangalaColors.iconPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            // Avatar picker bottom sheet
                            if (showAvatarPicker) {
                                AvatarPickerBottomSheetWrapper(
                                    onDismiss = { showAvatarPicker = false },
                                    onAvatarSelected = { source: AvatarSource ->
                                        val iconString = AvatarSource.toString(source)
                                        // Always call onIconSelected, even for null (AvatarSource.None)
                                        screenModel.onIconSelected(iconString)
                                        showAvatarPicker = false
                                    },
                                    viewModel = avatarPickerViewModel,
                                    currentAvatar = currentAvatarSource,
                                    entityName = uiState.groupName
                                )
                            }

                            // Group info section theo đúng Figma design
                            // Network dropdown với note
                            // Group info section - CHÍNH XÁC THEO FIGMA & HTML
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.mangalaColors.bgInnerCard)
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ), // Figma: padding 8px 16px
                                verticalArrangement = Arrangement.spacedBy(0.dp) // Không có gap, dùng divider
                            ) {
                                // Group name input
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Input row với divider
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp), // Figma: 12px 0
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Figma: gap 8px
                                    ) {
                                        BasicTextField(
                                            value = uiState.groupName,
                                            onValueChange = { screenModel.onGroupNameChanged(it) },
                                            modifier = Modifier.weight(1f),
                                            cursorBrush = SolidColor(MaterialTheme.mangalaColors.textPrimary),
                                            textStyle = TextStyle(
                                                fontSize = 14.sp, // Figma: 14px
                                                fontWeight = FontWeight.Normal, // Figma: 400
                                                letterSpacing = (-0.01).sp, // Figma: -1%
                                                color = MaterialTheme.mangalaColors.textPrimary
                                            ),
                                            decorationBox = { innerTextField ->
                                                Box(contentAlignment = Alignment.CenterStart) {
                                                    if (uiState.groupName.isEmpty()) {
                                                        Text(
                                                            text = "Enter group name",
                                                            color = MaterialTheme.mangalaColors.textSecondary,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Normal,
                                                            letterSpacing = (-0.01).sp
                                                        )
                                                    }
                                                    innerTextField()
                                                }
                                            },
                                            singleLine = true
                                        )
                                    }

                                    // Divider
                                    HorizontalDivider(
                                        color = MaterialTheme.mangalaColors.border,
                                        thickness = 0.5.dp
                                    )

                                    // Error message if needed
                                    if (uiState.groupNameError != null) {
                                        Text(
                                            text = uiState.groupNameError ?: "",
                                            color = MaterialTheme.mangalaColors.textLink,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }

                                // Network dropdown
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Dropdown row với divider
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !uiState.isEditMode) { showNetworkSelection() }
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 12.dp), // Figma: 12px 0
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp) // Figma: gap 8px
                                            ) {
                                                // Dropdown text
                                                val selectedNetwork = uiState.blockchainTypes.find {
                                                    it.id == uiState.selectedBlockchainId
                                                }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = selectedNetwork?.name
                                                            ?: "Choose network",
                                                        fontSize = 14.sp, // Figma: 14px
                                                        fontWeight = FontWeight.Normal,
                                                        letterSpacing = (-0.01).sp,
                                                        color = when {
                                                            uiState.isEditMode && selectedNetwork != null -> MaterialTheme.mangalaColors.textSecondary

                                                            selectedNetwork != null -> MaterialTheme.mangalaColors.textPrimary
                                                            else -> MaterialTheme.mangalaColors.textSecondary
                                                        }
                                                    )
                                                    // "(Cannot be changed)" note khi edit mode
                                                    if (uiState.isEditMode) {
                                                        Text(
                                                            text = "(Cannot be changed)",
                                                            fontSize = 14.sp, // hoặc 12.sp nếu muốn nhỏ hơn
                                                            color = MaterialTheme.mangalaColors.textSecondary,
                                                            fontWeight = FontWeight.Normal,
                                                            modifier = Modifier.padding(start = 4.dp)
                                                        )
                                                    }
                                                }
                                                // Dropdown icon
                                                if (!uiState.isEditMode) {
                                                    Icon(
                                                        imageVector = ContactIcon.DropDown,
                                                        contentDescription = "Dropdown Arrow",
                                                        tint = MaterialTheme.mangalaColors.iconSecondary,
                                                        modifier = Modifier.size(20.dp) // Figma: 20x20
                                                    )
                                                }
                                            }

                                            // Divider - không có cho item cuối
                                            // Vì đây là item cuối nên không cần divider
                                        }
                                    }
                                }
                            }

                            VerticalSpacer(Spacing.BASE)

                            // Privacy & Security section - Updated to match AdvancedSettingsSection
                            PrivacySecuritySection(
                                privacyLevel = uiState.privacyLevel,
                                securityLevel = uiState.securityLevel,
                                onPrivacyLevelChange = { screenModel.onPrivacyLevelChanged(it) },
                                onSecurityLevelChange = { screenModel.onSecurityLevelChanged(it) }
                            )

                            VerticalSpacer(Spacing.BASE)

                            // Address List section

                            // Show delete confirmation dialog if needed
                            if (showDeleteConfirmation && selectedDeleteIndex >= 0) {
                                val wallet = uiState.selectedWallets.getOrNull(selectedDeleteIndex)
                                if (wallet != null) {
                                    // Calculate shortened address for display
                                    val shortenedAddress = with(wallet.walletAddress) {
                                        if (length > 12) {
                                            val prefix = take(6)
                                            val suffix = takeLast(4)
                                            "$prefix...$suffix"
                                        } else {
                                            this
                                        }
                                    }

                                    DeleteConfirmationDialog(
                                        title = "Delete address?",
                                        message = "Are you sure you want to remove ${wallet.walletAlias?.takeIf { it.isNotBlank() } ?: wallet.contactName} ($shortenedAddress) from this group?",
                                        onConfirm = {
                                            // Delete the wallet address
                                            screenModel.deleteWallet(selectedDeleteIndex)
                                            showDeleteConfirmation = false
                                            selectedDeleteIndex = -1
                                        },
                                        onDismiss = {
                                            showDeleteConfirmation = false
                                            selectedDeleteIndex = -1
                                        }
                                    )
                                }
                            }

                            // Use either the HTML-style component (WalletListHtmlStyle) or the original component
                            // depending on preference. Here we use the HTML style to match the appearance
                            // with the bottom sheet for consistency
                            WalletListHtmlStyle(
                                wallets = uiState.selectedWallets,
                                onAddAddressClick = { screenModel.showContactBottomSheet() },
                                onQrCodeClick = { index ->
                                    val wallet = uiState.selectedWallets.getOrNull(index)
                                    if (wallet != null) {
                                        try {
                                            val mapper =
                                                com.mangala.wallet.features.addressbook.presentation.mapper.AddressBookToReceiveMapper
                                            val networkType =
                                                mapper.mapToNetworkType(wallet.blockchainTypeSymbol)
                                            val blockchainUid =
                                                mapper.mapToBlockchainUid(wallet.blockchainTypeSymbol)

                                            val receiveScreen =
                                                cafe.adriel.voyager.core.registry.ScreenRegistry.get(
                                                    com.mangala.wallet.ui.SharedScreen.ReceiveTokenScreen(
                                                        accountId = null,
                                                        address = wallet.walletAddress,
                                                        networkType = networkType,
                                                        initialBlockchainUid = blockchainUid
                                                    )
                                                )
                                            navigator.push(receiveScreen)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                },
                                onDeleteClick = { index ->
                                    // Show confirmation dialog before deletion
                                    selectedDeleteIndex = index
                                    showDeleteConfirmation = true
                                },
                                isLoading = uiState.isLoadingMoreWallets,
                                hasMoreData = uiState.hasMoreSelectedWallets,
                                onLoadMore = { screenModel.loadMoreSelectedWallets() },
                                totalCount = uiState.totalSelectedWalletsCount
                            )

                            // Auto-select first blockchain if none selected and not in edit mode
                            if (uiState.blockchainTypes.isNotEmpty() && uiState.selectedBlockchainId == null && !uiState.isEditMode) {
                                LaunchedEffect(Unit) {
                                    uiState.blockchainTypes.firstOrNull()?.let {
                                        screenModel.onBlockchainSelected(it.id)
                                    }
                                }
                            }

                            // Dynamic space based on scrolling position
                            VerticalSpacer(Spacing.BASE)

                            // Extra space at bottom for save button
                            Spacer(modifier = Modifier.height(90.dp))
                        }


                        // Loading overlay
                        if (uiState.isLoading || uiState.isSaving) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                MangalaCircularProgressIndicator()
                            }
                        }
                    }
                }

                // Fixed Save button at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.mangalaColors.bg)
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    MangalaGradientButton(
                        label = MR.strings.button_save_group.desc().localized(),
                        onClick = {
                            screenModel.saveGroup()
                        },
                        enabled = uiState.canSave,
                        size = MangalaButtonSize.Medium,
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    )
                }

                // SnackbarHost positioned at bottom center
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    @Composable
    fun TopBar(
        title: String,
        onBackClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(MaterialTheme.mangalaColors.bg)
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

            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = MaterialTheme.mangalaColors.textPrimary, // Use semantic color
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    @Composable
    fun LoadingOverlay(
        text: String = "Loading...",
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.mangalaColors.textPrimary.copy(alpha = 0.4f))
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.mangalaColors.bgInnerCard,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text)
                }
            }
        }
    }
}

/**
 * Privacy & Security section component matching AdvancedSettingsSection style
 */
@Composable
private fun PrivacySecuritySection(
    privacyLevel: PrivacyLevel,
    securityLevel: SecurityLevel,
    onPrivacyLevelChange: (PrivacyLevel) -> Unit,
    onSecurityLevelChange: (SecurityLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp), // Remove horizontal padding to match the parent's padding
        color = MaterialTheme.mangalaColors.bgInnerCard,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = MR.strings.label_privacy_security.desc().localized(),
                    style = MangalaTypography.Size14SemiBold(),
                    color = MaterialTheme.mangalaColors.textPrimary
                )

                Icon(
                    imageVector = if (!expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.mangalaColors.iconPrimary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Privacy level section
                    Column() {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = MR.strings.label_privacy_level.desc().localized(),
                                style = MangalaTypography.Size14Medium(),
                                color = MaterialTheme.mangalaColors.textPrimary
                            )
                            Icon(
                                imageVector = ContactIcon.QuestionMark,
                                contentDescription = "Privacy level info",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.mangalaColors.iconSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SelectionChip(
                                text = "Public",
                                selected = privacyLevel == PrivacyLevel.PUBLIC,
                                onClick = { onPrivacyLevelChange(PrivacyLevel.PUBLIC) }
                            )

                            SelectionChip(
                                text = "Private",
                                selected = privacyLevel == PrivacyLevel.PRIVATE,
                                onClick = { onPrivacyLevelChange(PrivacyLevel.PRIVATE) }
                            )

                            SelectionChip(
                                text = "Secret",
                                selected = privacyLevel == PrivacyLevel.SECRET,
                                onClick = { onPrivacyLevelChange(PrivacyLevel.SECRET) }
                            )
                        }
                    }
                    HorizontalDivider(
                        color = MaterialTheme.mangalaColors.border,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // Security Level section
                    Column() {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = MR.strings.label_security_level.desc().localized(),
                                style = MangalaTypography.Size14Medium(),
                                color = MaterialTheme.mangalaColors.textPrimary
                            )
                            Icon(
                                imageVector = ContactIcon.QuestionMark,
                                contentDescription = "Security level info",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.mangalaColors.iconSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SelectionChip(
                                text = "Normal",
                                selected = securityLevel == SecurityLevel.NORMAL,
                                onClick = { onSecurityLevelChange(SecurityLevel.NORMAL) }
                            )

                            SelectionChip(
                                text = "High",
                                selected = securityLevel == SecurityLevel.HIGH,
                                onClick = { onSecurityLevelChange(SecurityLevel.HIGH) }
                            )

                            SelectionChip(
                                text = "Maximum",
                                selected = securityLevel == SecurityLevel.MAXIMUM,
                                onClick = { onSecurityLevelChange(SecurityLevel.MAXIMUM) }
                            )
                        }

                        if (expanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Selection chip component matching AdvancedSettingsSection style
 */
@Composable
private fun SelectionChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = if (selected) MaterialTheme.mangalaColors.bgBadge else MaterialTheme.mangalaColors.bgInnerCard,
        border = if (!selected) BorderStroke(1.dp, MaterialTheme.mangalaColors.border) else null,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = text,
                style = MangalaTypography.Size14Medium().copy(
                    color = if (selected) MaterialTheme.mangalaColors.textPrimary else MaterialTheme.mangalaColors.textSecondary
                ),
            )
        }
    }
}

