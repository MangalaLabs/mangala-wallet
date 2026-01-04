package com.mangala.wallet.features.addressbook.presentation.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.presentation.components.calendar.CalendarBottomSheetScreen
import com.mangala.wallet.features.addressbook.presentation.components.calendar.CalendarBottomSheetState
import com.mangala.wallet.features.addressbook.presentation.components.calendar.CalendarSelectionChannel
import com.mangala.wallet.features.addressbook.presentation.components.calendar.CalendarEvent
import com.mangala.wallet.features.addressbook.presentation.components.EnhancedContactFormSkeleton
import com.mangala.wallet.features.addressbook.presentation.components.InputBasic
import com.mangala.wallet.features.addressbook.presentation.contact.create.AdvancedSettingsSection
import com.mangala.wallet.features.addressbook.presentation.contact.create.AvatarSection
import com.mangala.wallet.features.addressbook.presentation.contact.create.ContactHeader
import com.mangala.wallet.features.addressbook.presentation.contact.create.ContactInfoSection
import com.mangala.wallet.features.addressbook.presentation.contact.model.*
import com.mangala.wallet.features.addressbook.presentation.contact.create.TagSelectionChips
import com.mangala.wallet.features.addressbook.presentation.contact.create.WalletAddressSection
import com.mangala.wallet.features.addressbook.presentation.security.EditContactSecureButton
import com.mangala.wallet.features.addressbook.presentation.security.SecureActionId
import com.mangala.wallet.features.addressbook.presentation.security.SecureAuthProvider
import com.mangala.wallet.features.addressbook.presentation.security.SecureGradientButton
import com.mangala.wallet.ui.component.KeyboardDismissBox
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.delay
import org.koin.core.parameter.parameterArrayOf
import kotlin.jvm.Transient

class ContactScreen(
    private val contactId: String? = null,  // null = create mode, value = edit mode
    private val prefilledName: String = "",
    private val prefilledAddress: String = "",
    private val prefilledBlockchain: String = "",
    @Transient private val onBackClick: () -> Unit = {},
    @Transient private val onSaveSuccess: (String) -> Unit = {}
) : BaseScreen<ContactScreenModel>() {
    
    companion object {
        private val FALLBACK_TIMESTAMP = kotlinx.datetime.Instant.fromEpochMilliseconds(0)
        
        val FALLBACK_BLOCKCHAIN = com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity(
            id = "",
            name = "Unknown",
            symbol = "UNKNOWN",
            addressFormat = null,
            validationRegex = null,
            icon = null,
            color = null,
            networkType = "mainnet",
            isActive = true,
            createdAt = FALLBACK_TIMESTAMP,
            updatedAt = FALLBACK_TIMESTAMP
        )
    }

    @Composable
    override fun createScreenModel(): ContactScreenModel =
        getScreenModel<ContactScreenModel> {
            parameterArrayOf(contactId, prefilledName, prefilledAddress, prefilledBlockchain)
        }

    override val screenName: String = if (contactId != null) "CONTACT_EDIT" else "CONTACT_CREATE"
    override val screenClassName: String = ContactScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean = false

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: ContactScreenModel) {
        val rootNavigator = cafe.adriel.voyager.navigator.LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsState()
        val walletAddresses by screenModel.walletAddresses.collectAsState()
        val emailAddresses by screenModel.emailAddresses.collectAsState()
        val phoneNumbers by screenModel.phoneNumbers.collectAsState()
        val physicalAddresses by screenModel.physicalAddresses.collectAsState()
        val socialProfiles by screenModel.socialProfiles.collectAsState()
        val nicknames by screenModel.nicknames.collectAsState()
        val importantDates by screenModel.importantDates.collectAsState()
        val availableTags by screenModel.availableTags.collectAsState()
        val availableBlockchains by screenModel.availableBlockchains.collectAsState()
        
        var calendarState by remember { mutableStateOf(CalendarBottomSheetState()) }

        // Update time in edit mode periodically for Save anyway feature
        if (uiState.isEditMode && !uiState.hasModifications) {
            LaunchedEffect(uiState.editModeEnteredAt) {
                while (true) {
                    delay(1000) // Update every second
                    screenModel.updateSaveButtonState()
                }
            }
        }

        // Register navigation callback
        LaunchedEffect(Unit) {
            screenModel.registerNavigationCallback { savedContactId ->
                if (contactId == null) {
                    // Create mode - track analytics
                    MangalaAnalytics.trackEvent(
                        MangalaAnalytics.EventName.CONTACT_CREATE_COMPLETED
                    )
                }
                onSaveSuccess(savedContactId)
            }
        }

        SecureAuthProvider.Provider(rootNavigator = rootNavigator) {
            MangalaBottomSheetNavigator(
                hideOnBackPress = true,
                sheetShape = androidx.compose.foundation.shape.RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp
                ),
                skipHalfExpanded = true // Skip half-expanded state to always show full height
            ) { bottomSheetNavigator ->
                ContactContent(
                    uiState = uiState,
                    importantDates = importantDates,
                    walletAddresses = walletAddresses,
                    emailAddresses = emailAddresses,
                    phoneNumbers = phoneNumbers,
                    physicalAddresses = physicalAddresses,
                    socialProfiles = socialProfiles,
                    nicknames = nicknames,
                    availableTags = availableTags,
                    availableBlockchains = availableBlockchains,
                    screenModel = screenModel,
                    bottomSheetNavigator = bottomSheetNavigator,
                    calendarState = calendarState,
                    onCalendarStateChange = { calendarState = it },
                    onBackClick = onBackClick
                )

                // Calendar event handling
                HandleCalendarEvents(
                    calendarState = calendarState,
                    onCalendarStateChange = { calendarState = it },
                    screenModel = screenModel,
                    bottomSheetNavigator = bottomSheetNavigator
                )
            }
        }
    }

    @Composable
    private fun ContactContent(
        uiState: com.mangala.wallet.features.addressbook.presentation.contact.ContactUiState,
        importantDates: List<ImportantDateUiState>,
        walletAddresses: List<WalletAddressUiState>,
        emailAddresses: List<EmailAddressUiState>,
        phoneNumbers: List<PhoneNumberUiState>,
        physicalAddresses: List<PhysicalAddressUiState>,
        socialProfiles: List<SocialProfileUiState>,
        nicknames: List<RelatedNameUiState>,
        availableTags: List<com.mangala.wallet.features.addressbook.data.model.tag.TagEntity>,
        availableBlockchains: List<com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity>,
        screenModel: ContactScreenModel,
        bottomSheetNavigator: cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator,
        calendarState: CalendarBottomSheetState,
        onCalendarStateChange: (CalendarBottomSheetState) -> Unit,
        onBackClick: () -> Unit
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        val scrollState = rememberScrollState()
        var isContactInfoExpanded by remember { mutableStateOf(true) }
        val clipboardManager = LocalClipboardManager.current
        
        // Track focus states for multi-field management (AC-12.3, AC-12.5, AC-12.6)
        var focusedWalletFieldId by remember { mutableStateOf<String?>(null) }
        var focusedEmailFieldIndex by remember { mutableStateOf<Int?>(null) }
        var focusedPhoneFieldIndex by remember { mutableStateOf<Int?>(null) }
        var focusedAddressFieldIndex by remember { mutableStateOf<Int?>(null) }

        // Error handling
        LaunchedEffect(uiState.error) {
            uiState.error?.let {
                snackbarHostState.showSnackbar(it)
                screenModel.clearError()
            }
        }

        KeyboardDismissBox(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.mangalaColors.bg)
                .safeDrawingPadding()
                .imePadding() // Handle keyboard insets
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                ContactHeader(
                    title = uiState.screenTitle,
                    onBackClick = onBackClick,
                    isFavorite = uiState.isFavorite,
                    onFavoriteToggle = screenModel::toggleFavorite
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.mangalaColors.bg)
                ) {
                    when {
                        uiState.isLoading -> {
                            EnhancedContactFormSkeleton()
                        }
                        uiState.isSaving -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    MangalaCircularProgressIndicator()
                                }
                            }
                        }
                        else -> {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .verticalScroll(scrollState)
                                        .padding(bottom = 16.dp)
                                ) {
                                    // Avatar Section
                                    AvatarSection(
                                        icon = uiState.icon ?: "",
                                        onAvatarSelected = screenModel::updateIcon,
                                        contactName = uiState.name
                                    )

                                    // Name field
                                    InputBasic(
                                        value = uiState.name,
                                        placeholder = "Name",
                                        onNameChange = screenModel::updateName
                                    )

                                    // Wallet Address Section
                                    WalletAddressSection(
                                        addresses = walletAddresses.map { wallet -> 
                                            val blockchain = availableBlockchains.find { it.id == wallet.blockchainUid }
                                                ?: availableBlockchains.firstOrNull()
                                                ?: FALLBACK_BLOCKCHAIN
                                            Pair(wallet.address, blockchain)
                                        },
                                        walletAddresses = walletAddresses,
                                        addressTypes = walletAddresses.associate { 
                                            walletAddresses.indexOf(it) to (it.alias ?: "")
                                        },
                                        onAddressChange = { index, address ->
                                            if (index < walletAddresses.size && availableBlockchains.isNotEmpty()) {
                                                val wallet = walletAddresses[index]
                                                val blockchain = availableBlockchains.find { it.id == wallet.blockchainUid }
                                                    ?: availableBlockchains.first()
                                                screenModel.updateWalletAddress(
                                                    wallet.id,
                                                    address,
                                                    blockchain
                                                )
                                            }
                                        },
                                        onNetworkChange = { index, network ->
                                            if (index < walletAddresses.size) {
                                                val wallet = walletAddresses[index]
                                                screenModel.updateWalletAddress(
                                                    wallet.id,
                                                    wallet.address,
                                                    network
                                                )
                                            }
                                        },
                                        onAddressTypeSelect = { index, alias ->
                                            if (index < walletAddresses.size) {
                                                screenModel.updateWalletAddressAlias(
                                                    walletAddresses[index].id,
                                                    alias
                                                )
                                            }
                                        },
                                        onAddAddress = screenModel::addWalletAddress,
                                        onRemoveAddress = { index ->
                                            if (index < walletAddresses.size) {
                                                screenModel.removeWalletAddress(walletAddresses[index].id)
                                            }
                                        },
                                        onPasteClick = { index ->
                                            val clipboardContent = clipboardManager.getText()?.text ?: ""
                                            if (clipboardContent.isNotEmpty() && index < walletAddresses.size && availableBlockchains.isNotEmpty()) {
                                                val wallet = walletAddresses[index]
                                                val blockchain = availableBlockchains.find { it.id == wallet.blockchainUid }
                                                    ?: availableBlockchains.first()
                                                screenModel.updateWalletAddress(
                                                    wallet.id,
                                                    clipboardContent,
                                                    blockchain,
                                                    isPaste = true  // Mark as paste for instant validation
                                                )
                                            }
                                        },
                                        onScanQrClick = { index ->
                                            // Handle QR code scanning
                                        },
                                        onPrimaryAddressSelect = { index ->
                                            if (index < walletAddresses.size) {
                                                screenModel.setPrimaryWalletAddress(walletAddresses[index].id)
                                            }
                                        },
                                        primaryAddressIndex = walletAddresses.indexOfFirst { it.isPrimary },
                                        sensitiveFlagList = walletAddresses.map { it.isSensitive },
                                        onSensitiveToggle = { index, isSensitive ->
                                            if (index < walletAddresses.size) {
                                                screenModel.toggleWalletSensitive(
                                                    walletAddresses[index].id,
                                                    isSensitive
                                                )
                                            }
                                        },
                                        onAddCustomType = {},
                                        availableBlockchains = availableBlockchains,
                                        focusedFieldId = focusedWalletFieldId,
                                        onFocusChanged = { fieldId -> focusedWalletFieldId = fieldId }
                                    )

                                    // Tag selection
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        color = MaterialTheme.mangalaColors.bgInnerCard,
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Tag",
                                                style = MangalaTypography.Size14Medium(),
                                                color = MaterialTheme.mangalaColors.textPrimary,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )

                                            Spacer(modifier = Modifier.width(Spacing.XTINY))

                                            TagSelectionChips(
                                                availableTags = availableTags,
                                                selectedTags = uiState.selectedTagIds,
                                                onTagSelected = screenModel::toggleTag,
                                                onAddTagClick = {},
                                                onTagCreated = screenModel::loadAvailableTags,
                                                onCreateTag = screenModel::createTag,
                                                isCreatingTag = false
                                            )
                                        }
                                    }

                                    // Note field
                                    InputBasic(
                                        value = uiState.note,
                                        placeholder = "Note",
                                        onNameChange = screenModel::updateNote,
                                        singleLine = false,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )

                                    // Contact Info Section
                                    ContactInfoSection(
                                        emailFields = emailAddresses.map { Pair(it.label, it.email) },
                                        onEmailAdd = screenModel::addEmail,
                                        onEmailChange = { index, label, value ->
                                            if (index < emailAddresses.size) {
                                                screenModel.updateEmail(
                                                    emailAddresses[index].id,
                                                    value,
                                                    label
                                                )
                                            }
                                            // Don't auto-add when typing in empty field
                                        },
                                        onEmailRemove = { index ->
                                            if (index < emailAddresses.size) {
                                                screenModel.removeEmail(emailAddresses[index].id)
                                            }
                                        },
                                        phoneFields = phoneNumbers.map { Pair(it.label, it.number) },
                                        onPhoneAdd = screenModel::addPhone,
                                        onPhoneChange = { index, label, value ->
                                            if (index < phoneNumbers.size) {
                                                screenModel.updatePhone(
                                                    phoneNumbers[index].id,
                                                    value,
                                                    label
                                                )
                                            }
                                            // Don't auto-add when typing in empty field
                                        },
                                        onPhoneRemove = { index ->
                                            if (index < phoneNumbers.size) {
                                                screenModel.removePhone(phoneNumbers[index].id)
                                            }
                                        },
                                        addressFields = physicalAddresses.map { 
                                            Pair(it.label, it.street)
                                        },
                                        onAddressChange = { index, label, value ->
                                            if (index < physicalAddresses.size) {
                                                screenModel.updateAddress(index, label, value)
                                            }
                                            // Don't auto-add when typing in empty field
                                        },
                                        onAddressAdd = screenModel::addAddress,
                                        onAddressRemove = { index ->
                                            screenModel.removeAddress(index)
                                        },
                                        nicknameFields = nicknames.map { Pair("Nickname", it.name) },
                                        onNicknameAdd = screenModel::addNickname,
                                        onNicknameChange = { index, _, value ->
                                            if (index < nicknames.size) {
                                                screenModel.updateNickname(
                                                    nicknames[index].id,
                                                    value
                                                )
                                            }
                                            // Don't auto-add when typing in empty field
                                        },
                                        onNicknameRemove = { index ->
                                            if (index < nicknames.size) {
                                                screenModel.removeNickname(nicknames[index].id)
                                            }
                                        },
                                        importantDateFields = importantDates,
                                        onImportantDateChange = { index, instant, description ->
                                            if (index < importantDates.size) {
                                                val dateUiState = importantDates[index]
                                                // Update the calendar type based on the description (Solar/Lunar)
                                                val newCalendarType = when (description) {
                                                    "Lunar" -> com.mangala.wallet.features.addressbook.domain.model.CalendarType.LUNAR
                                                    else -> com.mangala.wallet.features.addressbook.domain.model.CalendarType.SOLAR
                                                }
                                                screenModel.updateImportantDateDescription(dateUiState.id, description, newCalendarType)
                                            }
                                        },
                                        onImportantDateAdd = {
                                            onCalendarStateChange(
                                                CalendarBottomSheetState(
                                                    isVisible = true,
                                                    editingDate = null,
                                                    calendarType = com.mangala.wallet.features.addressbook.domain.model.CalendarType.SOLAR
                                                )
                                            )
                                        },
                                        onImportantDateRemove = { index ->
                                            if (index < importantDates.size) {
                                                screenModel.removeImportantDate(importantDates[index].id)
                                            }
                                        },
                                        onImportantDateClick = { index ->
                                            val dateUiState = importantDates.getOrNull(index)
                                            if (dateUiState != null) {
                                                val importantDate = com.mangala.wallet.features.addressbook.domain.model.ImportantDate(
                                                    id = dateUiState.id,
                                                    title = dateUiState.label,
                                                    date = dateUiState.date.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date,
                                                    calendarType = com.mangala.wallet.features.addressbook.domain.model.CalendarType.valueOf(dateUiState.calendarType.uppercase()),
                                                    lunarDate = null
                                                )
                                                onCalendarStateChange(
                                                    CalendarBottomSheetState(
                                                        isVisible = true,
                                                        editingDate = importantDate,
                                                        calendarType = com.mangala.wallet.features.addressbook.domain.model.CalendarType.valueOf(dateUiState.calendarType.uppercase())
                                                    )
                                                )
                                            } else {
                                                // New date - show calendar with no pre-selected date
                                                onCalendarStateChange(
                                                    CalendarBottomSheetState(
                                                        isVisible = true,
                                                        editingDate = null,
                                                        calendarType = com.mangala.wallet.features.addressbook.domain.model.CalendarType.SOLAR
                                                    )
                                                )
                                            }
                                        },
                                        importantDates = screenModel.getImportantDatesAsDomain(),
                                        socialField = socialProfiles.map { Pair(it.platform, it.username) },
                                        onSocialChange = { index, platform, value ->
                                            if (index < socialProfiles.size) {
                                                screenModel.updateSocialProfile(index, platform, value)
                                            }
                                            // Don't auto-add when typing in empty field
                                        },
                                        onSocialAdd = screenModel::addSocialProfile,
                                        onSocialRemove = { index ->
                                            screenModel.removeSocialProfile(index)
                                        },
                                        expanded = isContactInfoExpanded,
                                        onToggleExpand = {
                                            isContactInfoExpanded = !isContactInfoExpanded
                                        },
                                        validationErrors = uiState.validationErrors,
                                        modifiedFields = uiState.modifiedFields,
                                        isNewContact = !uiState.isEditMode,
                                        focusedEmailIndex = focusedEmailFieldIndex,
                                        focusedPhoneIndex = focusedPhoneFieldIndex,
                                        focusedAddressIndex = focusedAddressFieldIndex,
                                        onEmailFocusChanged = { index -> 
                                            focusedEmailFieldIndex = index
                                        },
                                        onPhoneFocusChanged = { index -> 
                                            focusedPhoneFieldIndex = index
                                        },
                                        onAddressFocusChanged = { index -> 
                                            focusedAddressFieldIndex = index
                                        }
                                    )

                                    // Advanced Settings
                                    AdvancedSettingsSection(
                                        privacyDisplayMode = uiState.privacyDisplayMode,
                                        securityLevel = uiState.securityLevel,
                                        selectedGroupIds = uiState.selectedGroupIds.toList(),
                                        availableGroups = screenModel.getAvailableGroupsAsEntities(),
                                        onPrivacyDisplayModeChange = screenModel::updateDisplayMode,
                                        onSecurityLevelChange = screenModel::updateSecurityLevel
                                    )
                                }

                                // Bottom Save Button with helper text
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.mangalaColors.bgInnerCard)
                                        .padding(16.dp)
                                        .navigationBarsPadding()
                                ) {
                                    if (uiState.isEditMode) {
                                        EditContactSecureButton(
                                            currentSecurityLevel = uiState.securityLevel,
                                            originalSecurityLevel = uiState.originalSecurityLevel,
                                            isAuthenticated = uiState.isAuthenticated,
                                            label = uiState.saveButtonText,
                                            onClick = {
                                                screenModel.saveContact()
                                            },
                                            onAuthenticationSuccess = screenModel::markAsAuthenticated,
                                            enabled = uiState.isSaveEnabled && !uiState.isSaving,
                                            size = MangalaButtonSize.Medium,
                                            modifier = Modifier.fillMaxWidth().height(44.dp)
                                        )
                                    } else {
                                        EditContactSecureButton(
                                            currentSecurityLevel = uiState.securityLevel,
                                            originalSecurityLevel = SecurityLevel.NORMAL, // New contact starts with NORMAL
                                            isAuthenticated = uiState.isAuthenticated,
                                            label = uiState.saveButtonText,
                                            onClick = {
                                                screenModel.saveContact()
                                            },
                                            onAuthenticationSuccess = screenModel::markAsAuthenticated,
                                            enabled = uiState.isSaveEnabled && !uiState.isSaving,
                                            size = MangalaButtonSize.Medium,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        // SnackbarHost
        SnackbarHost(hostState = snackbarHostState)
    }

    @Composable
    private fun HandleCalendarEvents(
        calendarState: CalendarBottomSheetState,
        onCalendarStateChange: (CalendarBottomSheetState) -> Unit,
        screenModel: ContactScreenModel,
        bottomSheetNavigator: cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
    ) {
        val calendarScreenId = remember { "contact_${kotlinx.datetime.Clock.System.now().toEpochMilliseconds()}" }

        // Listen for calendar events
        LaunchedEffect(calendarScreenId) {
            try {
                CalendarSelectionChannel.events.collect { event ->
                    when (event) {
                        is CalendarEvent.DateSelected -> {
                            if (event.screenId == calendarScreenId) {
                                val editingDate = calendarState.editingDate
                                if (editingDate != null) {
                                    screenModel.updateImportantDate(
                                        editingDate.id,
                                        event.date.date
                                    )
                                    // Also update the description/title if changed
                                    screenModel.updateImportantDateDescription(
                                        editingDate.id,
                                        event.date.title,
                                        event.date.calendarType
                                    )
                                } else {
                                    // Pass title and calendar type when adding new date
                                    screenModel.addImportantDate(
                                        event.date.date,
                                        event.date.title,
                                        event.date.calendarType
                                    )
                                }
                                onCalendarStateChange(CalendarBottomSheetState())
                            }
                        }
                        is CalendarEvent.Dismissed -> {
                            if (event.screenId == calendarScreenId) {
                                onCalendarStateChange(CalendarBottomSheetState())
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle collection cancellation
            }
        }

        // Show calendar bottom sheet when needed
        LaunchedEffect(calendarState) {
            if (calendarState.isVisible) {
                val editingDate = calendarState.editingDate
                bottomSheetNavigator.show(
                    CalendarBottomSheetScreen(
                        screenId = calendarScreenId,
                        existingDateId = editingDate?.id,
                        existingDateTitle = editingDate?.title,
                        existingDateDay = editingDate?.date?.dayOfMonth,
                        existingDateMonth = editingDate?.date?.monthNumber,
                        existingDateYear = editingDate?.date?.year
                    )
                )
            }
        }

        // Monitor bottom sheet state
        LaunchedEffect(bottomSheetNavigator.isVisible) {
            if (!bottomSheetNavigator.isVisible && calendarState.isVisible) {
                onCalendarStateChange(CalendarBottomSheetState())
            }
        }
    }
}