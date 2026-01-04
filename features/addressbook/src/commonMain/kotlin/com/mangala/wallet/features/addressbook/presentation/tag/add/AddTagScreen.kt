package com.mangala.wallet.features.addressbook.presentation.tag.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeftNavigation
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Camera
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Delete
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.di.AvatarFactory
import com.mangala.wallet.features.addressbook.domain.model.Avatar
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarPickerBottomSheetWrapper
import com.mangala.wallet.features.addressbook.presentation.components.ContactColumnWithActionRowAndMultipleBlockchainsRow
import com.mangala.wallet.features.addressbook.presentation.components.DeleteConfirmationDialog
import com.mangala.wallet.features.addressbook.presentation.contact.qr.ShowContactQrScreen
import com.mangala.wallet.features.addressbook.presentation.tag.TagIcon
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

/**
 * Screen for creating or editing a tag
 */
class AddTagScreen(
    val tagId: String? = null,
) : BaseScreen<AddTagScreenModel>() {

    override val key: ScreenKey = "CreateTagScreen"
    override val screenName: String = "CREATE_TAG"
    override val screenClassName: String = "CreateTagScreen"
    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun createScreenModel(): AddTagScreenModel = getScreenModel { parametersOf(tagId) }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: AddTagScreenModel) {
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        var searchQuery by remember { mutableStateOf("") }
        var saveSuccess by remember { mutableStateOf(false) }
        var showRemoveContactConfirmation by remember { mutableStateOf(false) }
        var contactToRemove by remember { mutableStateOf<String?>(null) }

        val clipboardManager = LocalClipboardManager.current

        // SystemUiController để thay đổi màu status bar
//        val systemUiController = rememberSystemUiController()
//
//        // Set status bar color to match screen background
//        SideEffect {
//            systemUiController.setStatusBarColor(
//                color = MaterialTheme.mangalaColors.bg,
//                darkIcons = true // Dark icons for light background
//            )
//            systemUiController.setNavigationBarColor(
//                color = MaterialTheme.mangalaColors.bgInnerCard,
//                darkIcons = true
//            )
//        }
//
//        // Reset màu khi dispose screen
//        DisposableEffect(Unit) {
//            onDispose {
//                // Reset về default background
//                systemUiController.setSystemBarsColor(
//                    color = MaterialTheme.mangalaColors.bg,
//                    darkIcons = true
//                )
//            }
//        }

        // Tạo tham chiếu đến AddressSelectionScreen để hiển thị khi cần
        val addressSelectionScreen = rememberScreen(
            SharedScreen.AddressSelectionScreen(
                tagId = tagId,
                initialSelectedContactIds = uiState.selectedContactIds,
                onApplySelections = { selectedIds ->
                    screenModel.applyContactSelections(selectedIds)
                }
            )
        )

        // Show error message in snackbar if there is an error
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = error,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

        // Sử dụng MangalaBottomSheetNavigator thay vì ModalBottomSheetLayout
        Box(modifier = Modifier.fillMaxSize()) {
            MangalaBottomSheetNavigator(
                hideOnBackPress = true,
                sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                skipHalfExpanded = true // Skip half-expanded state to always show full height
            ) { bottomSheetNavigator ->

                // Update ViewModel based on bottom sheet visibility
                LaunchedEffect(screenModel.showAddressBottomSheet) {
                    if (screenModel.showAddressBottomSheet) {
                        bottomSheetNavigator.show(addressSelectionScreen)
                    }
                }

                // Main content of the screen (outside the bottom sheet)
                Box(
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
                            title = if (tagId != null) "Edit Tag" else "Create Tag",
                            onBackClick = { navigator.pop() },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.mangalaColors.bg)
                        ) {
                            // Main content with scroll
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp)
                            ) {
                                item {
                                    // Avatar section
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        var showAvatarPicker by remember { mutableStateOf(false) }

                                        // Tạo đối tượng Avatar từ trạng thái hiện tại
                                        val currentAvatarSource = remember(uiState.icon) {
                                            AvatarSource.fromString(uiState.icon)
                                        }

                                        val avatar = Avatar(
                                            name = uiState.tagName,
                                            avatarSource = currentAvatarSource
                                        )

                                        val onImageSelected = remember {
                                            { path: String ->
                                                screenModel.onIconSelected(path)
                                            }
                                        }

                                        val avatarPickerViewModel = remember {
                                            AvatarFactory.createAvatarPickerViewModel(
                                                onImageSelected
                                            )
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            // Avatar display with camera icon overlay
                                            Box(
                                                modifier = Modifier
                                                    .size(96.dp)
                                            ) {
                                                // Tag icon with background color - giữ nguyên như cũ
                                                TagIcon(
                                                    name = uiState.tagName,
                                                    icon = uiState.icon,
                                                    modifier = Modifier.fillMaxSize(),
                                                    size = 96.dp,
                                                    backgroundColor = uiState.selectedBackgroundColor,
                                                    contentColor = uiState.selectedTextColor,
                                                    useFullOpacityBackground = true
                                                )

                                                // Camera icon overlay ở góc dưới phải
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .align(Alignment.BottomEnd)
                                                        .background(
                                                            color = MaterialTheme.mangalaColors.iconSecondary,
                                                            shape = CircleShape
                                                        )
                                                        .clickable { showAvatarPicker = true }
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
                                                    screenModel.onIconSelected(iconString ?: "")
                                                    showAvatarPicker = false
                                                },
                                                viewModel = avatarPickerViewModel,
                                                currentAvatar = currentAvatarSource,
                                                entityName = uiState.tagName
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Container for the tag name input
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.mangalaColors.bgInnerCard)
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        BasicTextField(
                                            value = uiState.tagName,
                                            onValueChange = { screenModel.setTagName(it) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp),
                                            textStyle = androidx.compose.ui.text.TextStyle(
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = MaterialTheme.mangalaColors.textPrimary
                                            ),
                                            singleLine = true,
                                            cursorBrush = SolidColor(MaterialTheme.mangalaColors.textLink),
                                            decorationBox = { innerTextField ->
                                                Box(
                                                    contentAlignment = Alignment.CenterStart
                                                ) {
                                                    if (uiState.tagName.isEmpty()) {
                                                        Text(
                                                            text = "Enter tag name",
                                                            color = MaterialTheme.mangalaColors.textSecondary,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Normal
                                                        )
                                                    }
                                                    innerTextField()
                                                }
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Text color selection
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.mangalaColors.bgInnerCard
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Choose text color",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.mangalaColors.textPrimary,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(bottom = 16.dp)
                                            )

                                            LazyRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                items(screenModel.getAvailableTextColors()) { color ->
                                                    ColorButton(
                                                        color = color,
                                                        isSelected = uiState.selectedTextColor == color,
                                                        onClick = {
                                                            screenModel.setSelectedTextColor(
                                                                color
                                                            )
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Background color selection
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.mangalaColors.bgInnerCard
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Choose background color",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.mangalaColors.textPrimary,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(bottom = 16.dp)
                                            )

                                            LazyRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                items(screenModel.getAvailableColors()) { color ->
                                                    ColorButton(
                                                        color = color,
                                                        isSelected = uiState.selectedBackgroundColor == color,
                                                        onClick = {
                                                            screenModel.setSelectedBackgroundColor(
                                                                color
                                                            )
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Filter only selected contacts by ID
                                    // Use key based on selectedContactIds size to force recalculation
                                    val selectedContacts = remember(
                                        uiState.selectedContactIds.size,
                                        uiState.selectedContactIds,
                                        uiState.availableContacts
                                    ) {
                                        // Find which selected IDs are missing from available contacts
                                        val availableContactIds =
                                            uiState.availableContacts.map { it.contact.id }.toSet()
                                        val missingContactIds =
                                            uiState.selectedContactIds.filter { it !in availableContactIds }
                                        if (missingContactIds.isNotEmpty()) {
                                        }

                                        val filtered =
                                            uiState.availableContacts.filter { contactWithAddresses ->
                                                contactWithAddresses.contact.id in uiState.selectedContactIds
                                            }
                                        filtered
                                    }

                                    // Address list section
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.mangalaColors.bgInnerCard
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(
                                                vertical = 12.dp,
                                                horizontal = 16.dp
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        bottomSheetNavigator.show(
                                                            addressSelectionScreen
                                                        )
                                                    }
                                                    .padding(vertical = 12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Contacts list (${selectedContacts.size})",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.mangalaColors.textPrimary,
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.ChevronRight,
                                                    contentDescription = "Select addresses",
                                                    tint = MaterialTheme.mangalaColors.iconSecondary
                                                )
                                            }

                                            // Display selected addresses
                                            if (uiState.availableContacts.isNotEmpty()) {

                                                // Display each selected contact
                                                if (selectedContacts.isEmpty()) {
                                                    Text(
                                                        text = "No contacts selected",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        color = MaterialTheme.mangalaColors.textSecondary,
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                    )
                                                } else {
                                                    selectedContacts.forEachIndexed { index, contactWithAddresses ->
                                                        val isLast = index == selectedContacts.size - 1
                                                        val isFirst = index == 0

                                                        // Determine primary address and network if available
                                                        val primaryAddress =
                                                            contactWithAddresses.addresses.firstOrNull()?.address
                                                        val primaryNetwork =
                                                            contactWithAddresses.addresses.firstOrNull()?.networkName
                                                        val primaryNetworkSymbol =
                                                            contactWithAddresses.addresses.firstOrNull()?.networkSymbol ?: ""

                                                        val dividerColor = MaterialTheme.mangalaColors.border

                                                        MaxWidthRow(
                                                            modifier = Modifier
                                                                .drawWithCache {
                                                                    onDrawWithContent {
                                                                        drawContent()

                                                                        if (isLast.not())
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
                                                                    contact = ContactWithMultipleBlockchainsModel(
                                                                        contactId = contactWithAddresses.contact.id,
                                                                        contactName = contactWithAddresses.contact.name,
                                                                        primaryWalletAddress = primaryAddress ?: "",
                                                                        primaryWalletAddressId = "",
                                                                        primaryWalletAlias = "",
                                                                        primaryWalletSensitive = false,
                                                                        primaryBlockchainName = primaryNetwork ?: "",
                                                                        primaryBlockchainSymbol = primaryNetworkSymbol,
                                                                        primaryBlockchainIcon = "",
                                                                        primaryBlockChainColor = "",
                                                                        isFavorite = uiState.contactFavoriteStatus[contactWithAddresses.contact.id] == true,
                                                                        addedTime = 0L,
                                                                        isSensitive = false,
                                                                        avatar = contactWithAddresses.contact.avatar ?: "",
                                                                        additionalBlockchainsSymbol = contactWithAddresses.addresses.drop(1)
                                                                            .map { it.networkSymbol ?: "" }
                                                                    ),
                                                                    privacyModeEnabled = false,
                                                                    isDisplayStar = true,
                                                                    onQrCodeClick = {
                                                                        // Navigate to ReceiveTokenScreen for contact QR
                                                                        try {
                                                                            val primaryAddress = contactWithAddresses.addresses.firstOrNull()
                                                                            if (primaryAddress != null) {
                                                                                val mapper = com.mangala.wallet.features.addressbook.presentation.mapper.AddressBookToReceiveMapper
                                                                                val networkType = mapper.mapToNetworkType(primaryAddress.networkName)
                                                                                val blockchainUid = mapper.mapToBlockchainUid(primaryAddress.networkName)
                                                                                
                                                                                val receiveScreen = cafe.adriel.voyager.core.registry.ScreenRegistry.get(
                                                                                    com.mangala.wallet.ui.SharedScreen.ReceiveTokenScreen(
                                                                                        accountId = null,
                                                                                        address = primaryAddress.address,
                                                                                        networkType = networkType,
                                                                                        initialBlockchainUid = blockchainUid
                                                                                    )
                                                                                )
                                                                                navigator.push(receiveScreen)
                                                                            }
                                                                        } catch (e: Exception) {
                                                                            e.printStackTrace()
                                                                        }
                                                                    },
                                                                )
                                                            }

                                                            IconButton(
                                                                onClick = {
                                                                    contactToRemove = contactWithAddresses.contact.id
                                                                    showRemoveContactConfirmation = true
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
                                            } else {
                                                Text(
                                                    text = "No contacts selected",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    color = MaterialTheme.mangalaColors.textSecondary,
                                                    modifier = Modifier.padding(vertical = 8.dp)
                                                )
                                            }
                                        }
                                    }
                                    // Extra space at bottom for button
                                    Spacer(modifier = Modifier.height(90.dp))
                                }
                            }

                            // Fixed Save button at bottom
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.mangalaColors.bg)
                                    .padding(vertical = 16.dp)
                                    .align(Alignment.BottomCenter),
                            ) {
                                MangalaGradientButton(
                                    label = if (tagId != null) "Update tag" else "Save tag",
                                    onClick = {
                                        scope.launch {
                                            val savedTagId = screenModel.saveTagSuspend()

                                            if (savedTagId != null) {
                                                saveSuccess = true

                                                navigator.pop()
                                            }
                                        }
                                    },
                                    enabled = !uiState.isSaving && uiState.tagName.isNotBlank(),
                                    size = MangalaButtonSize.Medium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                )
                            }
                        }
                    }

                    // SnackbarHost positioned at bottom center
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                // Remove contact confirmation dialog
                if (showRemoveContactConfirmation && contactToRemove != null) {
                    val contactName = uiState.availableContacts
                        .find { it.contact.id == contactToRemove }?.contact?.name
                    DeleteConfirmationDialog(
                        title = "Remove contact from tag?",
                        message = "Are you sure you want to remove ${contactName ?: "this contact"} from this tag?",
                        confirmButtonText = "Remove",
                        onConfirm = {
                            contactToRemove?.let { contactId ->
                                screenModel.removeContactFromSelection(contactId)
                            }
                        },
                        onDismiss = {
                            showRemoveContactConfirmation = false
                            contactToRemove = null
                        }
                    )
                }

                // Loading overlay - covers entire screen including top bar
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        MangalaCircularProgressIndicator()
                    }
                }
            }
        }
    }

    @Composable
    private fun TopBar(
        title: String,
        onBackClick: () -> Unit,
        modifier: Modifier = Modifier,
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
                    imageVector = MangalaWalletPack.ArrowLeftNavigation,
                    contentDescription = "Back",
                    tint = MaterialTheme.mangalaColors.iconPrimary
                )
            }

            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    /**
     * Color selection button with selection indicator
     */
    @Composable
    fun ColorButton(
        color: Color,
        isSelected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Box(
            modifier = modifier
                .size(32.dp) // Size according to Figma design
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.mangalaColors.iconSecondary else MaterialTheme.mangalaColors.border,
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

