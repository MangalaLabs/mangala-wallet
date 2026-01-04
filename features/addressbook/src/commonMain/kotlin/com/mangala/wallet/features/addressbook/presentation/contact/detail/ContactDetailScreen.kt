package com.mangala.wallet.features.addressbook.presentation.contact.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithBlockchainModel
import com.mangala.wallet.features.addressbook.domain.model.CalendarType
import com.mangala.wallet.features.addressbook.domain.model.ImportantDate
import com.mangala.wallet.features.addressbook.domain.model.ImportantDateCategory
import com.mangala.wallet.features.addressbook.domain.model.LunarDate
import com.mangala.wallet.features.addressbook.presentation.components.DeleteConfirmationDialog
import com.mangala.wallet.features.addressbook.presentation.components.DocumentCopyButton
import com.mangala.wallet.features.addressbook.presentation.components.EnhancedContactDetailSkeleton
import com.mangala.wallet.features.addressbook.presentation.contact.ContactScreen
import com.mangala.wallet.features.addressbook.presentation.security.ContextAwareSecureAuthPolicyProvider
import com.mangala.wallet.features.addressbook.presentation.security.SecureActionId
import com.mangala.wallet.features.addressbook.presentation.security.SecureAuthPolicyProvider
import com.mangala.wallet.features.addressbook.presentation.security.SecureAuthProvider
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.ToastFactory
import com.mangala.wallet.utils.isNotNullOrBlank
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parameterArrayOf
import kotlin.jvm.Transient

class ContactDetailScreen(
    private val contactId: String,
    private val privacyModeEnabled: Boolean,
    @Transient private val onBackClick: () -> Unit = {},
    @Transient val onSaveSuccess: (contactId: String) -> Unit = {}
) : BaseScreen<ContactDetailScreenModel>(), KoinComponent {

    @Composable
    override fun createScreenModel(): ContactDetailScreenModel =
        getScreenModel<ContactDetailScreenModel> { parameterArrayOf(contactId) }

    override val screenName: String = "CONTACT_DETAIL"
    override val screenClassName: String = ContactDetailScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun ScreenContent(screenModel: ContactDetailScreenModel) {
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val contactDetail = uiState.contactDetail
        val toastFactory = get<ToastFactory>()
        var showSaveSuccessMessage by remember { mutableStateOf(false) }

        // Check if deletion is in progress and navigate back immediately
        LaunchedEffect(screenModel.isDeletingState) {
            if (screenModel.isDeletingState) {
                onBackClick()
            }
        }

        // Add LaunchedEffect to reload contact details when this screen becomes active again
        // This ensures the data is refreshed when returning from the edit screen
        LaunchedEffect(Unit) {
            screenModel.loadContactDetail()
        }

        // Handle error states with toast messages
        LaunchedEffect(uiState.error) {
            uiState.error?.let { errorMessage ->
                toastFactory.show(errorMessage)
                screenModel.clearError()
            }
        }

        // Show success message after edit
        LaunchedEffect(showSaveSuccessMessage) {
            if (showSaveSuccessMessage) {
                toastFactory.show("Contact updated successfully")
                showSaveSuccessMessage = false
            }
        }

        // Handle navigation events
        // Use screenModel as key to ensure proper collection of events
        LaunchedEffect(screenModel) {
            screenModel.navigationEvents.collect { event ->
                when (event) {
                    is ContactDetailNavigationEvent.NavigateBack -> {
                        onBackClick()
                    }

                    is ContactDetailNavigationEvent.NavigateToSelectAddress -> {
                        val selectAddressScreen = ScreenRegistry.get(
                            SharedScreen.SelectContactAddressScreen(
                                contactId = event.contactId,
                                accountId = event.accountId
                            )
                        )
                        navigator.push(selectAddressScreen)
                    }

                    is ContactDetailNavigationEvent.NavigateToStep3 -> {
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

        SecureAuthProvider.Provider {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.mangalaColors.bg)
                    .safeDrawingPadding()
            ) {
                when {
                    uiState.isLoading -> {
                        // Loading state - skeleton loading
                        EnhancedContactDetailSkeleton()
                    }

                    uiState.contactDetail == null && !uiState.isLoading -> {
                        // Empty state when no contact is loaded and not loading
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Contact not found",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.mangalaColors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                MangalaGradientButton(
                                    label = "Retry",
                                    onClick = screenModel::loadContactDetail
                                )
                            }
                        }
                    }

                    contactDetail != null -> {
                        // Content state - show contact details
                        ContactDetailContext(
                            uiState = uiState,
                            screenModel = screenModel,
                            onBackClick = onBackClick,
                            onEditClick = {
                                navigator.push(
                                    ContactScreen(
                                        contactId = contactId, // Edit mode
                                        onBackClick = {
                                            // When user manually navigates back without saving
                                            navigator.pop()
                                            // Reload in case any partial changes were made
                                            screenModel.loadContactDetail()
                                        },
                                        onSaveSuccess = { savedContactId ->
                                            // 1. Close edit screen first
                                            navigator.pop()
                                            // 2. Notify parent screen if needed
                                            onSaveSuccess(savedContactId)
                                            // 3. Reload contact details to show updated data
                                            screenModel.loadContactDetail()
                                            // 4. Show success message
                                            showSaveSuccessMessage = true
                                        }
                                    ))
                            },
                            onSendClick = screenModel::onContactSendClick,
                            onHistoryClick = { /* Implement history action */ },
                            onShareClick = { /* Implement share action */ },
                            onFavoriteClick = screenModel::toggleFavorite,
                            onMessageClick = { /* Implement message action */ },
                            onCallClick = { /* Implement call action */ },
                            onEmailClick = { /* Implement email action */ },
                            onShowQrCodeClick = { address ->
                                val blockchainType =
                                    BlockchainType.fromUid(address.blockchainType.id)
                                val receiveScreen = ScreenRegistry.get(
                                    SharedScreen.ReceiveTokenScreen(
                                        accountId = null,
                                        address = address.walletAddress.address,
                                        networkType = blockchainType.networkType,
                                        initialBlockchainUid = blockchainType.uid
                                    )
                                )
                                navigator.push(receiveScreen)
                            },
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ContactDetailContext(
        uiState: ContactDetailUiState,
        screenModel: ContactDetailScreenModel,
        onBackClick: () -> Unit,
        onEditClick: () -> Unit,
        onSendClick: () -> Unit,
        onHistoryClick: () -> Unit,
        onShareClick: () -> Unit,
        onFavoriteClick: () -> Unit,
        onMessageClick: () -> Unit,
        onCallClick: () -> Unit,
        onEmailClick: () -> Unit,
        onShowQrCodeClick: (WalletAddressWithBlockchainModel) -> Unit,
    ) {
        val contactDetail = uiState.contactDetail
        val isFavorite = uiState.isFavorite
        var showCopyMessage by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        val secureAuthProvider = SecureAuthProvider.current
        val onClickShowQrCodeRef = rememberUpdatedState(onShowQrCodeClick)

        Box(modifier = Modifier.fillMaxSize()) {
            MaxSizeColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.mangalaColors.bg)
                    .safeDrawingPadding(),
            ) {
                ContactDetailTopBar(
                    onBackClick = onBackClick,
                    onEditClick = onEditClick,
                    onDeleteClick = { showDeleteDialog = true },
                    isFavorite = isFavorite,
                    onFavoriteClick = onFavoriteClick,
                    // Hide edit button when authentication is required but not completed
                    isEditEnabled = !uiState.requiresAuth || uiState.isAuthenticated
                )

                if (contactDetail != null) {
                    if (uiState.requiresAuth && !uiState.isAuthenticated && !uiState.isFullInfoVisible) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.mangalaColors.bgInnerCard,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(24.dp)
                            ) {
                                Text(
                                    text = "Authentication Required",
                                    style = MangalaTypography.Size17SemiBold(),
                                    color = MaterialTheme.mangalaColors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "This contact has high security settings. Please authenticate to view full information.",
                                    style = MangalaTypography.Size14Regular(),
                                    color = MaterialTheme.mangalaColors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                MangalaGradientButton(
                                    label = "Authenticate",
                                    onClick = {
                                        // Use SecureAuthProvider to handle authentication
                                        secureAuthProvider.runSecureActionForId(
                                            actionId = SecureActionId.ViewHighSecurityContact,
                                            onSuccess = {
                                                screenModel.onAuthenticationSuccess()
                                            },
                                            onCancel = {
                                                screenModel.onAuthenticationCancelled()
                                            }
                                        )
                                    },
                                    size = MangalaButtonSize.Medium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else {
                        val onReceiveClick: () -> Unit = remember(contactDetail.walletAddresses) {
                            {
                                contactDetail.walletAddresses.firstOrNull { it.walletAddress.isPrimary }
                                    ?.let { primaryWalletAddress ->
                                        onClickShowQrCodeRef.value(primaryWalletAddress)
                                    }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Contact Header with photo, name and tags (favorite moved to top bar)
                            ContactHeader(
                                name = contactDetail.contact.name,
                                tags = contactDetail.tags,
                                icon = contactDetail.contact.avatar
                            )

                            // Quick Action Buttons (Send, Request, History, Share)
                            ContactQuickActions(
                                onSendClick = onSendClick,
                                onReceiveClick = onReceiveClick,
                                onHistoryClick = onHistoryClick,
                                onShareClick = onShareClick
                            )

                            // Wallet Addresses Section
                            WalletAddressesSection(
                                walletAddresses = contactDetail.walletAddresses,
                                onShowQrCodeClick = onShowQrCodeClick,
                                isViewOnly = true,
                                onCopyComplete = { showCopyMessage = true },
                                privacyModeEnabled = privacyModeEnabled,
                                privacyDisplayMode = contactDetail.contact.privacyDisplayMode
                            )

                            // Note section - only show if there's content
                            if (!contactDetail.contact.notes.isNullOrBlank()) {
                                ContactInfoSection(
                                    title = "Note",
                                    items = listOf(
                                        ContactInfoItem(
                                            icon = Icons.AutoMirrored.Filled.Notes,
                                            value = contactDetail.contact.notes ?: "",
                                            label = ""
                                        )
                                    ),
                                    onCopyComplete = { showCopyMessage = true }
                                )
                            }

                            // Contact Details Sections - Separate sections as per Figma design
                            with(contactDetail) {
                                // Phone Numbers
                                if (phoneNumbers.isNotEmpty()) {
                                    ContactInfoSection(
                                        title = "Phone number",
                                        items = phoneNumbers.map {
                                            ContactInfoItem(
                                                icon = Icons.Default.Phone,
                                                value = it.phoneNumber,
                                                label = it.label ?: "Other"
                                            )
                                        },
                                        onCopyComplete = { showCopyMessage = true }
                                    )
                                }

                                // Email Addresses (duplicated in two sections as per design)
                                if (emailAddresses.isNotEmpty()) {
                                    ContactInfoSection(
                                        title = "Email Address",
                                        items = emailAddresses.map {
                                            ContactInfoItem(
                                                icon = Icons.Default.Email,
                                                value = it.email,
                                                label = it.label ?: "Other"
                                            )
                                        },
                                        onCopyComplete = { showCopyMessage = true }
                                    )
                                }

                                // Physical Addresses
                                if (physicalAddresses.isNotEmpty()) {
                                    ContactInfoSection(
                                        title = "Physical Address",
                                        items = physicalAddresses.map { address ->
                                            val formattedAddress = buildString {
                                                address.streetAddress?.let { append(it) }
                                                if (address.city != null) {
                                                    append(", ")
                                                    append(address.city)
                                                }
                                                if (address.country != null) {
                                                    append(", ")
                                                    append(address.country)
                                                }
                                            }
                                            ContactInfoItem(
                                                icon = Icons.Default.LocationOn,
                                                value = formattedAddress,
                                                label = address.addressType ?: "Other"
                                            )
                                        },
                                        onCopyComplete = { showCopyMessage = true }
                                    )
                                }

                                // Nicknames (from Related Names)
                                if (relatedNames.isNotEmpty()) {
                                    ContactInfoSection(
                                        title = "Nickname",
                                        items = relatedNames.map {
                                            ContactInfoItem(
                                                icon = Icons.Default.Person,
                                                value = it.name,
                                                label = it.relationship.replaceFirstChar { char -> char.uppercase() }
                                            )
                                        },
                                        onCopyComplete = { showCopyMessage = true }
                                    )
                                }

                                // Important Dates - only show if there's data
                                if (importantDates.isNotEmpty()) {
                                    ContactInfoSection(
                                        title = "Important Date",
                                        items = importantDates.map { dateEntity ->
                                            // ✅ Convert ImportantDateEntity to ImportantDate domain model
                                            val localDate =
                                                dateEntity.date.toLocalDateTime(TimeZone.currentSystemDefault()).date

                                            // Parse stored lunar date from description if available - handle both old "|" format and new "~" format
                                            val lunarDate = try {
                                                val parts = dateEntity.description?.split("|")
                                                    ?: emptyList()
                                                val lunarPart = parts.getOrNull(4)
                                                if (lunarPart?.startsWith("LUNAR:") == true && lunarPart.length > 6) {
                                                    val lunarInfo = lunarPart.substring(6)
                                                    // Try new "~" format first, then fall back to old "|" format
                                                    val lunarParts =
                                                        if (lunarInfo.contains("~")) {
                                                            lunarInfo.split("~")
                                                        } else {
                                                            lunarInfo.split("|")
                                                        }
                                                    if (lunarParts.size >= 3) {
                                                        val dateParts = lunarParts[0].split("/")
                                                        if (dateParts.size == 3) {
                                                            LunarDate(
                                                                day = dateParts[0].toInt(),
                                                                month = dateParts[1].toInt(),
                                                                year = dateParts[2].toInt(),
                                                                isLeapMonth = lunarParts[1].toBoolean(),
                                                                yearCycle = lunarParts.getOrNull(
                                                                    2
                                                                ) ?: ""
                                                            )
                                                        } else null
                                                    } else null
                                                } else null
                                            } catch (e: Exception) {
                                                null
                                            }

                                            // Create ImportantDate domain model
                                            val importantDate = ImportantDate(
                                                id = dateEntity.id,
                                                title = dateEntity.description?.split("|")
                                                    ?.getOrNull(0) ?: "Important Date",
                                                date = localDate,
                                                calendarType = dateEntity.calendarType,
                                                lunarDate = lunarDate,
                                                category = ImportantDateCategory.OTHER,
                                                notes = ""
                                            )

                                            // ✅ Simple date display based on calendar type
                                            val displayValue =
                                                when (importantDate.calendarType) {
                                                    CalendarType.SOLAR -> {
                                                        // Simple solar date: "15/4/2025"
                                                        val date = importantDate.date
                                                        "${date.dayOfMonth}/${date.monthNumber}/${date.year}"
                                                    }

                                                    CalendarType.LUNAR -> {
                                                        // Simple lunar date: "15/4/2025" (lunar format)
                                                        val lunarDate = importantDate.lunarDate
                                                        if (lunarDate != null) {
                                                            "${lunarDate.day}/${lunarDate.month}/${lunarDate.year}"
                                                        } else {
                                                            // Fallback to solar if lunar not available
                                                            val date = importantDate.date
                                                            "${date.dayOfMonth}/${date.monthNumber}/${date.year}"
                                                        }
                                                    }
                                                }


                                            ContactInfoItem(
                                                icon = Icons.Default.CalendarToday,
                                                value = displayValue,
                                                label = when (dateEntity.calendarType) {
                                                    CalendarType.SOLAR -> "Solar"
                                                    CalendarType.LUNAR -> "Lunar"
                                                }
                                            )
                                        },
                                        onCopyComplete = { showCopyMessage = true }
                                    )
                                }

                                // Social Profiles - only show if there's data
                                if (socialProfiles.isNotEmpty()) {
                                    ContactInfoSection(
                                        title = "Social Profile",
                                        items = socialProfiles.map {
                                            ContactInfoItem(
                                                icon = Icons.Default.Person,
                                                value = it.url.toString(),
                                                label = it.platform
                                            )
                                        },
                                        onCopyComplete = { showCopyMessage = true }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Copy message overlay positioned at the bottom center of the screen
            AnimatedVisibility(
                visible = showCopyMessage,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .zIndex(1000f)
            ) {
                LaunchedEffect(Unit) {
                    delay(2000)
                    showCopyMessage = false
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Copied to clipboard",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Delete confirmation dialog
            if (showDeleteDialog && contactDetail != null) {
                val secureActionHandler = SecureAuthProvider.current
                val policyProvider =
                    koinInject<SecureAuthPolicyProvider>() as? ContextAwareSecureAuthPolicyProvider

                // Preload contact security level when dialog is shown
                LaunchedEffect(contactDetail.contact.id) {
                    policyProvider?.preloadContactSecurity(contactDetail.contact.id)
                }

                DeleteConfirmationDialog(
                    title = "Delete contact?",
                    message = "Are you sure you want to delete ${contactDetail.contact.name}? This action cannot be undone.",
                    onConfirm = {
                        showDeleteDialog = false
                        policyProvider?.setContactContext(contactDetail.contact.id)
                        secureActionHandler.runSecureActionForId(
                            actionId = SecureActionId.DeleteContact,
                            onSuccess = {
                                policyProvider?.clearContactContext()

                                // Delete contact - navigation will be handled by screenModel
                                screenModel.deleteContact(contactDetail.contact.id)
                            },
                            onCancel = {
                                policyProvider?.clearContactContext()
                            }
                        )
                    },
                    onDismiss = {
                        showDeleteDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun ContactInfoSection(
    title: String,
    items: List<ContactInfoItem>,
    onCopyComplete: () -> Unit = {}
) {
    Column {
        Text(
            text = title,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            style = MangalaTypography.Size14SemiBold(),
            color = MaterialTheme.mangalaColors.textPrimary
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.mangalaColors.bgInnerCard,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                items.forEachIndexed { index, item ->
                    ContactDetailItem(
                        label = item.value,
                        subtitle = item.label,
                        onCopyComplete = onCopyComplete
                    )

                    if (index < items.size - 1) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.mangalaColors.bgButton
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun ContactDetailItem(
    label: String,
    subtitle: String?,
    onCopyComplete: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (subtitle.isNotNullOrBlank()) {
                Text(
                    text = subtitle.toString(),
                    style = MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary,
                    fontWeight = FontWeight.Light
                )

                Spacer(modifier = Modifier.height(2.dp))
            }

            Text(
                text = label,
                style = MangalaTypography.Size14Regular(),
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.Normal
            )
        }

        // Copy icon aligned with wallet address section
        DocumentCopyButton(
            textToCopy = label,
            label = subtitle ?: "",
            iconTint = MaterialTheme.mangalaColors.iconSecondary, // Match wallet address section tint
            onCopyComplete = onCopyComplete,
        )
    }
}

