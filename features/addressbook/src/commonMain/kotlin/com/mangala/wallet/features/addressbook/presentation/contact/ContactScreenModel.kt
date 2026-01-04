package com.mangala.wallet.features.addressbook.presentation.contact

import cafe.adriel.voyager.core.model.screenModelScope
import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ImportantDateEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhysicalAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.RelatedNameEntity
import com.mangala.wallet.features.addressbook.data.model.contact.SocialProfileEntity
import com.mangala.wallet.features.addressbook.data.model.ContactDetailModel
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SyncStatus
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.features.addressbook.domain.model.ImportantDate
import com.mangala.wallet.features.addressbook.domain.model.CalendarType
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetAllBlockchainTypesUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.CreateContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdateContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactDetailByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.InsertEmailAddressesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.InsertImportantDatesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.DeleteImportantDateUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.email.InsertSocialProfilesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.favorite.AddFavoriteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.RemoveFavoriteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.phone.InsertPhoneNumbersBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.physical_address.InsertPhysicalAddressesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.related.InsertRelatedNamesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.wallet_address.InsertWalletAddressesBatchUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.BatchAssignGroupsToContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetAllGroupsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.BatchAssignTagsToContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.CreateTagUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.GetActiveTagsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.ValidateAddressUseCase
import com.mangala.wallet.features.addressbook.domain.validation.ExchangeAddressDetector
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants
import com.mangala.wallet.features.addressbook.domain.validation.ComprehensiveAddressValidator
import com.mangala.wallet.features.addressbook.domain.validation.WalletValidationResult
import com.mangala.wallet.features.addressbook.domain.validation.ValidationContext
import com.mangala.wallet.features.addressbook.domain.validation.SafeValidationManager
import com.mangala.wallet.features.addressbook.domain.validation.createSafeValidationManager
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConfig
import com.mangala.wallet.features.addressbook.domain.usecase.clipboard.CopyToClipboardUseCase
import com.mangala.wallet.features.addressbook.presentation.contact.model.*
import com.mangala.wallet.features.addressbook.utils.ContactValidationUtils
import com.mangala.wallet.features.addressbook.presentation.contact.edit.FieldValidationState
import com.mangala.wallet.features.addressbook.presentation.contact.validation.ValidationLoadingState
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

/**
 * Data class to store original contact data for change detection
 */
data class OriginalContactData(
    val walletIds: Set<String>,
    val emailIds: Set<String>,
    val phoneIds: Set<String>,
    val addressIds: Set<String>,
    val socialIds: Set<String>,
    val dateIds: Set<String>,
    val nicknameIds: Set<String>,
    // Store original collections for deep comparison
    val wallets: List<WalletAddressUiState>,
    val emails: List<EmailAddressUiState>,
    val phones: List<PhoneNumberUiState>,
    val addresses: List<PhysicalAddressUiState>,
    val socials: List<SocialProfileUiState>,
    val dates: List<ImportantDateUiState>,
    val nicknames: List<RelatedNameUiState>
)

/**
 * Unified UI State for both Create and Edit modes
 */
data class ContactUiState(
    // Mode detection
    val isEditMode: Boolean = false,
    val contactId: String? = null,
    
    // Basic fields
    val name: String = "",
    val icon: String? = null,
    val note: String = "",
    val pronouns: String = "", // ADDED: Pronouns field support
    val isFavorite: Boolean = false,
    
    // Security and privacy
    val securityLevel: SecurityLevel = SecurityLevel.NORMAL,
    val originalSecurityLevel: SecurityLevel = SecurityLevel.NORMAL,
    val authRequirement: AuthRequirement = AuthRequirement.NONE,
    val privacyDisplayMode: DisplayMode = DisplayMode.HIDDEN,
    val isAuthenticated: Boolean = false,
    val requiresAuth: Boolean = false,
    val isAuthenticating: Boolean = false,
    
    // Tags and groups
    val selectedTagIds: Set<String> = emptySet(),
    val selectedGroupIds: Set<String> = emptySet(),
    val availableGroups: List<GroupModel> = emptyList(),
    
    // Warning tracking - ADDED from old implementation
    val exchangeAddressWarnings: Map<String, String> = emptyMap(),
    val testnetAddressWarnings: Map<String, String> = emptyMap(),
    
    // Field validation states - ADDED from old implementation  
    // Note: Using presentation.state.FieldValidationState
    val fieldValidationStates: Map<String, com.mangala.wallet.features.addressbook.presentation.state.FieldValidationState> = emptyMap(),
    
    // Validation tracking - ADDED from old implementation
    val walletValidationNeeded: Set<String> = emptySet(),
    
    // UI states
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val modifiedFields: Set<String> = emptySet(),
    
    // Computed properties
    val screenTitle: String = "",
    val saveButtonText: String = "",
    val isSaveEnabled: Boolean = false,
    val hasModifications: Boolean = false,
    
    // Smart Progressive Enablement fields
    val touchedFields: Set<String> = emptySet(),
    val editModeEnteredAt: Instant = Clock.System.now(),
    val saveButtonHelperText: String? = null,
    val showSaveWarningDialog: Boolean = false,
    val saveWarnings: List<String> = emptyList(),
    
    // Time tracking for edit mode
    val timeInEditMode: Long = 0,
    
    // Change tracking for edit mode - NEW
    val originalData: OriginalContactData? = null,
    val hasWalletChanges: Boolean = false,
    val hasEmailChanges: Boolean = false,
    val hasPhoneChanges: Boolean = false,
    val hasAddressChanges: Boolean = false,
    val hasSocialChanges: Boolean = false,
    val hasDateChanges: Boolean = false,
    val hasNicknameChanges: Boolean = false
)

/**
 * Validation cache data class
 */
data class ValidationCacheEntry(
    val result: Triple<ValidationLoadingState, String?, String?>,
    val timestamp: Long
)

/**
 * Cache data class for validation results
 */
data class CachedValidation(
    val result: WalletValidationResult,
    val timestamp: Instant
)

/**
 * Unified ContactScreenModel that handles both create and edit modes
 */
class ContactScreenModel(
    private val contactId: String? = null,  // null = create mode, value = edit mode
    private val prefilledName: String = "",
    private val prefilledAddress: String = "",
    private val prefilledBlockchain: String = "",
    private val getAllBlockchainsUseCase: GetAllBlockchainTypesUseCase,
    private val getActiveTagsUseCase: GetActiveTagsUseCase,
    private val createTagUseCase: CreateTagUseCase,
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val createContactUseCase: CreateContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase,
    private val getContactByIdUseCase: GetContactByIdUseCase,
    private val getContactDetailByIdUseCase: GetContactDetailByIdUseCase,
    private val batchAssignTagsToContactUseCase: BatchAssignTagsToContactUseCase,
    private val batchAssignGroupsToContactUseCase: BatchAssignGroupsToContactUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val insertWalletAddressesBatchUseCase: InsertWalletAddressesBatchUseCase,
    private val insertEmailAddressesBatchUseCase: InsertEmailAddressesBatchUseCase,
    private val insertPhoneNumbersBatchUseCase: InsertPhoneNumbersBatchUseCase,
    private val insertPhysicalAddressesBatchUseCase: InsertPhysicalAddressesBatchUseCase,
    private val insertSocialProfilesBatchUseCase: InsertSocialProfilesBatchUseCase,
    private val insertRelatedNamesBatchUseCase: InsertRelatedNamesBatchUseCase,
    private val insertImportantDatesBatchUseCase: InsertImportantDatesBatchUseCase,
    private val validateAccountUseCase: ValidateAddressUseCase,
    private val isValidBitcoinAddressUseCase: Any, // Not used in unified implementation
    private val evmAddressValidator: Any, // Not used in unified implementation
    private val exchangeAddressDetector: ExchangeAddressDetector,
    private val copyToClipboardUseCase: CopyToClipboardUseCase,
    private val comprehensiveValidator: ComprehensiveAddressValidator, // CHANGED: Made required, not optional
    // ADD FACADE USE CASES FOR FULL CRUD - Optional parameters with defaults
    private val communicationUseCase: com.mangala.wallet.features.addressbook.domain.usecase.facade.CommunicationUseCase? = null,
    private val walletUseCase: com.mangala.wallet.features.addressbook.domain.usecase.facade.WalletUseCase? = null
) : BaseScreenModel() {
    companion object {
        // Validation cache constants
        private const val CACHE_DURATION_MS = 5 * 60 * 1000L // 5 minutes
        
        // Temporary: Skip network validation on emulator with DNS issues
        private val SKIP_NETWORK_VALIDATION = false // Set to true only if DNS issues persist
        private const val MAX_VALIDATIONS_PER_MINUTE_NETWORK = 10 // Rate limit for network validations
        private const val MAX_VALIDATIONS_PER_MINUTE_LOCAL = 60 // Rate limit for local validations
        
        // Debounce delays for better UX
        private const val TYPING_DEBOUNCE_MS = 300L // Standard typing delay
        private const val PASTE_DEBOUNCE_MS = 0L // Instant validation for paste
        private const val EOS_SHORT_DEBOUNCE_MS = 400L // For EOS accounts 4-6 chars
        private const val EOS_LONG_DEBOUNCE_MS = 600L // For EOS accounts 7+ chars
        
        // Minimum lengths for validation
        private const val MIN_EOS_ACCOUNT_LENGTH = 4 // Raised from 3 for better UX
        private const val EOS_SHORT_NAME_THRESHOLD = 6 // Threshold for short vs long debounce
        
        // Extended VAULTA system accounts
        private val VAULTA_SYSTEM_ACCOUNTS = setOf(
            "vaulta", "vaulta.token", "vaulta.stake", "vaulta.msig",
            "vaulta.ram", "vaulta.rex", "vaulta.names", "vaulta.bpay",
            "vaulta.vpay", "vaulta.saving", "vaulta.wrap", "vaulta.fees"
        ) + ValidationConstants.ANTELOPE_SYSTEM_ACCOUNTS
    }

    // Main UI state
    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()
    
    // Collection states
    private val _walletAddresses = MutableStateFlow<List<WalletAddressUiState>>(emptyList())
    val walletAddresses: StateFlow<List<WalletAddressUiState>> = _walletAddresses.asStateFlow()
    
    private val _emailAddresses = MutableStateFlow<List<EmailAddressUiState>>(emptyList())
    val emailAddresses: StateFlow<List<EmailAddressUiState>> = _emailAddresses.asStateFlow()
    
    private val _phoneNumbers = MutableStateFlow<List<PhoneNumberUiState>>(emptyList())
    val phoneNumbers: StateFlow<List<PhoneNumberUiState>> = _phoneNumbers.asStateFlow()
    
    private val _physicalAddresses = MutableStateFlow<List<PhysicalAddressUiState>>(emptyList())
    val physicalAddresses: StateFlow<List<PhysicalAddressUiState>> = _physicalAddresses.asStateFlow()
    
    private val _socialProfiles = MutableStateFlow<List<SocialProfileUiState>>(emptyList())
    val socialProfiles: StateFlow<List<SocialProfileUiState>> = _socialProfiles.asStateFlow()
    
    private val _nicknames = MutableStateFlow<List<RelatedNameUiState>>(emptyList())
    val nicknames: StateFlow<List<RelatedNameUiState>> = _nicknames.asStateFlow()
    
    private val _importantDates = MutableStateFlow<List<ImportantDateUiState>>(emptyList())
    val importantDates: StateFlow<List<ImportantDateUiState>> = _importantDates.asStateFlow()
    
    // Available data
    private val _availableTags = MutableStateFlow<List<TagEntity>>(emptyList())
    val availableTags: StateFlow<List<TagEntity>> = _availableTags.asStateFlow()
    
    private val _availableBlockchains = MutableStateFlow<List<BlockchainTypeEntity>>(emptyList())
    val availableBlockchains: StateFlow<List<BlockchainTypeEntity>> = _availableBlockchains.asStateFlow()
    
    // Navigation callback
    private var onNavigateCallback: ((String) -> Unit)? = null
    
    // Safe Validation Manager for thread-safe validation
    private val validationManager = screenModelScope.createSafeValidationManager(
        comprehensiveValidator = comprehensiveValidator
    )
    
    // Validation context
    private var currentValidationContext = ValidationContext.ADDING_CONTACT
    
    // Validation cache and rate limiting
    private val validationCache = mutableMapOf<String, CachedValidation>()
    private val validationTimestamps = mutableListOf<Instant>()
    
    // Track original contact data for change detection
    private var originalContactDetail: ContactDetailModel? = null
    
    init {
        loadAvailableData()
        observeValidationResults() // Start observing validation state changes
        initializeScreenInternal()
    }
    
    private fun observeValidationResults() {
        screenModelScope.launch {
            validationManager.validationState.collect { validationState ->
                // Process each field's validation state
                validationState.fields.forEach { (fieldId, fieldState) ->
                    if (fieldId.startsWith("wallet_")) {
                        val walletId = fieldId.removePrefix("wallet_")
                        
                        // Map validation status to UI state
                        val newValidationState = when (fieldState.status) {
                            com.mangala.wallet.features.addressbook.presentation.state.ValidationStatus.IDLE -> ValidationLoadingState.IDLE
                            com.mangala.wallet.features.addressbook.presentation.state.ValidationStatus.TYPING -> ValidationLoadingState.TYPING
                            com.mangala.wallet.features.addressbook.presentation.state.ValidationStatus.VALIDATING -> ValidationLoadingState.VALIDATING
                            com.mangala.wallet.features.addressbook.presentation.state.ValidationStatus.VALID -> ValidationLoadingState.VALID
                            com.mangala.wallet.features.addressbook.presentation.state.ValidationStatus.WARNING -> ValidationLoadingState.WARNING
                            com.mangala.wallet.features.addressbook.presentation.state.ValidationStatus.ERROR -> ValidationLoadingState.INVALID
                        }
                        
                        // Update wallet with new validation state
                        updateWalletValidationState(walletId, newValidationState, fieldState)
                    }
                }
                
                // Update save button based on validation state
                updateSaveButtonState()
            }
        }
    }
    
    private fun updateWalletValidationState(
        walletId: String,
        validationState: ValidationLoadingState,
        fieldState: com.mangala.wallet.features.addressbook.presentation.state.FieldValidationState
    ) {
        // Update wallet addresses with new validation state
        _walletAddresses.update { wallets ->
            wallets.map { wallet ->
                if (wallet.id == walletId) {
                    wallet.copy(
                        validationState = validationState,
                        error = fieldState.errorMessage,
                        suggestion = when (fieldState.validationResult) {
                            is WalletValidationResult.Warning -> fieldState.validationResult.message
                            else -> null
                        },
                        resolvedAddress = if (validationState == ValidationLoadingState.VALID) {
                            when (val result = fieldState.validationResult) {
                                is WalletValidationResult.Success -> result.cleanAddress
                                else -> wallet.address
                            }
                        } else null
                    )
                } else wallet
            }
        }
        
        // Track warnings separately for better UX (from old implementation)
        when {
            fieldState.hasWarning && fieldState.errorMessage?.contains("exchange", ignoreCase = true) == true -> {
                _uiState.update { state ->
                    state.copy(
                        exchangeAddressWarnings = state.exchangeAddressWarnings + 
                            (walletId to (fieldState.errorMessage ?: ""))
                    )
                }
            }
            fieldState.hasWarning && fieldState.errorMessage?.contains("testnet", ignoreCase = true) == true -> {
                _uiState.update { state ->
                    state.copy(
                        testnetAddressWarnings = state.testnetAddressWarnings + 
                            (walletId to (fieldState.errorMessage ?: ""))
                    )
                }
            }
            validationState == ValidationLoadingState.VALID || validationState == ValidationLoadingState.IDLE -> {
                // Clear warnings when valid or empty
                _uiState.update { state ->
                    state.copy(
                        exchangeAddressWarnings = state.exchangeAddressWarnings - walletId,
                        testnetAddressWarnings = state.testnetAddressWarnings - walletId
                    )
                }
            }
        }
        
        // Update field validation states
        _uiState.update { state ->
            state.copy(
                fieldValidationStates = state.fieldValidationStates + ("wallet_$walletId" to fieldState)
            )
        }
        
        // Remove from validation needed if validated
        if (validationState != ValidationLoadingState.TYPING && validationState != ValidationLoadingState.VALIDATING) {
            _uiState.update { state ->
                state.copy(
                    walletValidationNeeded = state.walletValidationNeeded - walletId
                )
            }
        }
    }
    
    private fun initializeScreenInternal() {
        screenModelScope.launch {
            try {
                if (contactId != null) {
                    // Edit mode
                    _uiState.update { it.copy(
                        isEditMode = true,
                        contactId = contactId,
                        screenTitle = "Edit Contact",
                        saveButtonText = "Save Changes",
                        editModeEnteredAt = Clock.System.now() // Track when user entered edit mode
                    )}
                    loadExistingContact(contactId)
                } else {
                    // Create mode
                    _uiState.update { it.copy(
                        isEditMode = false,
                        screenTitle = "Add Contact",
                        saveButtonText = "Create Contact",
                        isLoading = false
                    )}

                    // Always add a default wallet address field in create mode
                    addWalletAddress()

                    // Add default empty fields for better UX
                    addPhone("", "Mobile")
                    addEmail("", "Home")
                    addAddress("", "Home")
                    addNickname("")
                    addSocialProfile("", "Facebook")

                    // Apply prefilled data if provided
                    if (prefilledName.isNotEmpty()) {
                        updateName(prefilledName)
                    }
                    if (prefilledAddress.isNotEmpty() && prefilledBlockchain.isNotEmpty()) {
                        val blockchain = _availableBlockchains.value.find {
                            it.name.equals(prefilledBlockchain, ignoreCase = true)
                        }
                        if (blockchain != null) {
                            val walletId = _walletAddresses.value.firstOrNull()?.id
                            if (walletId != null) {
                                updateWalletAddress(walletId, prefilledAddress, blockchain)
                            }
                        }
                    }
                }

                updateSaveButtonState()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = "Failed to initialize: ${e.message}",
                    isLoading = false
                )}
            }
        }
    }
    
    private suspend fun loadExistingContact(contactId: String) {
        try {
            val contactDetail = getContactDetailByIdUseCase(contactId)
            contactDetail?.let { detail ->
                // Check if authentication is required
                val hasHighSecurity = detail.contact.securityLevel == SecurityLevel.HIGH ||
                                     detail.contact.securityLevel == SecurityLevel.MAXIMUM
                
                // Update main UI state
                _uiState.update { it.copy(
                    name = detail.contact.name,
                    icon = detail.contact.avatar,
                    note = detail.contact.notes ?: "",
                    // pronouns field will be added when ContactEntity supports it
                    isFavorite = detail.isFavorite,
                    securityLevel = detail.contact.securityLevel,
                    originalSecurityLevel = detail.contact.securityLevel,
                    authRequirement = detail.contact.authRequirement,
                    privacyDisplayMode = detail.contact.privacyDisplayMode,
                    selectedTagIds = detail.tags.map { it.id }.toSet(),
                    selectedGroupIds = detail.groups.map { it.id }.toSet(), // Load group selections
                    isLoading = false,
                    // Authentication state
                    isAuthenticated = !hasHighSecurity, // Authenticated if not high security
                    requiresAuth = hasHighSecurity
                )}
                
                // Load collections
                // Load wallet addresses - need to get blockchain from ID
                // Note: Always load real addresses without obfuscation since this screen is for editing
                // Extract to local variables first to prevent race conditions when reading StateFlow multiple times
                val walletUiStates = detail.walletAddresses.map { walletWithBlockchain ->
                    val wallet = walletWithBlockchain.walletAddress
                    val blockchain = walletWithBlockchain.blockchainType
                    WalletAddressUiState(
                        id = wallet.id,
                        address = wallet.address,
                        label = wallet.alias ?: "",
                        blockchain = blockchain.name,
                        blockchainUid = blockchain.id,
                        isPrimary = wallet.isPrimary,
                        alias = wallet.alias,
                        isSensitive = wallet.isSensitive,
                        error = null,
                        validationState = ValidationLoadingState.IDLE,
                        suggestion = null,
                        resolvedAddress = null,
                        isResolvingDomain = false
                    )
                }

                val emailUiStates = detail.emailAddresses.map { email ->
                    EmailAddressUiState(
                        id = email.id,
                        email = email.email,
                        label = email.label ?: "Email",
                        isPrimary = email.isPrimary
                    )
                }

                val phoneUiStates = detail.phoneNumbers.map { phone ->
                    PhoneNumberUiState(
                        id = phone.id,
                        number = phone.phoneNumber,
                        label = phone.label ?: "Phone",
                        isPrimary = phone.isPrimary
                    )
                }

                val physicalAddressUiStates = detail.physicalAddresses.map { addr ->
                    PhysicalAddressUiState(
                        id = addr.id,
                        street = addr.streetAddress ?: "",
                        street2 = addr.ward ?: "",
                        city = addr.city ?: "",
                        state = addr.stateProvince ?: "",
                        zip = addr.postalCode ?: "",
                        country = addr.country ?: "",
                        label = addr.addressType ?: "Home",
                        isPrimary = addr.isPrimary,
                        streetAddress = addr.streetAddress ?: "",
                        ward = addr.ward ?: "",
                        district = addr.district ?: "",
                        stateProvince = addr.stateProvince ?: "",
                        postalCode = addr.postalCode ?: "",
                        addressType = addr.addressType ?: ""
                    )
                }

                val socialProfileUiStates = detail.socialProfiles.map { social ->
                    SocialProfileUiState(
                        id = social.id,
                        platform = social.platform,
                        handle = social.username,
                        username = social.username,
                        url = social.url
                    )
                }

                val nicknameUiStates = detail.relatedNames.map { related ->
                    RelatedNameUiState(
                        id = related.id,
                        name = related.name,
                        relationship = related.relationship ?: "Nickname"
                    )
                }

                val importantDateUiStates = detail.importantDates.map { date ->
                    // Parse description if it's in pipe-delimited format
                    val parsedDescription = if (date.description?.contains("|") == true) {
                        date.description.split("|").firstOrNull() ?: "Birthday"
                    } else {
                        date.description ?: "Birthday"
                    }

                    ImportantDateUiState(
                        id = date.id,
                        date = date.date,
                        label = parsedDescription,
                        calendarType = date.calendarType.name,
                        description = date.description
                    )
                }

                // Assign local variables to StateFlows
                _walletAddresses.value = walletUiStates
                _emailAddresses.value = emailUiStates
                _phoneNumbers.value = phoneUiStates
                _physicalAddresses.value = physicalAddressUiStates
                _socialProfiles.value = socialProfileUiStates
                _nicknames.value = nicknameUiStates
                _importantDates.value = importantDateUiStates

                // Store original data for change tracking using local variables to prevent race conditions
                // This ensures consistency even if StateFlows are updated by other coroutines
                val originalData = OriginalContactData(
                    walletIds = walletUiStates.map { it.id }.toSet(),
                    emailIds = emailUiStates.map { it.id }.toSet(),
                    phoneIds = phoneUiStates.map { it.id }.toSet(),
                    addressIds = physicalAddressUiStates.map { it.id }.toSet(),
                    socialIds = socialProfileUiStates.map { it.id }.toSet(),
                    dateIds = importantDateUiStates.map { it.id }.toSet(),
                    nicknameIds = nicknameUiStates.map { it.id }.toSet(),
                    // Deep copy collections for change tracking
                    wallets = walletUiStates.map { it.copy() },
                    emails = emailUiStates.map { it.copy() },
                    phones = phoneUiStates.map { it.copy() },
                    addresses = physicalAddressUiStates.map { it.copy() },
                    socials = socialProfileUiStates.map { it.copy() },
                    dates = importantDateUiStates.map { it.copy() },
                    nicknames = nicknameUiStates.map { it.copy() }
                )
                
                _uiState.update { it.copy(originalData = originalData) }

                if (walletUiStates.isEmpty()) {
                    addWalletAddress()
                }
                if (phoneUiStates.isEmpty()) {
                    addPhone("", "Mobile")
                }
                if (emailUiStates.isEmpty()) {
                    addEmail("", "Home")
                }
                if (physicalAddressUiStates.isEmpty()) {
                    addAddress("", "Home")
                }
                if (nicknameUiStates.isEmpty()) {
                    addNickname("")
                }
                if (socialProfileUiStates.isEmpty()) {
                    addSocialProfile("", "Facebook")
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(
                error = "Failed to load contact: ${e.message}",
                isLoading = false
            )}
        }
    }
    
    private fun loadAvailableData() {
        screenModelScope.launch {
            try {
                // Load tags
                _availableTags.value = getActiveTagsUseCase().getOrDefault(emptyList())
                
                // Load blockchains
                _availableBlockchains.value = getAllBlockchainsUseCase()
                
                // Load groups
                _uiState.update { it.copy(
                    availableGroups = getAllGroupsUseCase()
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = "Failed to load data: ${e.message}"
                )}
            }
        }
    }
    
    fun registerNavigationCallback(callback: (String) -> Unit) {
        onNavigateCallback = callback
    }
    
    // Basic field updates
    fun updateName(name: String) {
        _uiState.update { it.copy(
            name = name,
            modifiedFields = it.modifiedFields + "name"
        )}
        updateSaveButtonState()
    }

    fun updateIcon(icon: String?) {
        _uiState.update { it.copy(
            icon = icon,
            modifiedFields = it.modifiedFields + "icon"
        )}
        updateSaveButtonState()
    }
    
    fun updateNote(note: String) {
        _uiState.update { it.copy(
            note = note,
            modifiedFields = it.modifiedFields + "note"
        )}
        updateSaveButtonState()
    }
    
    fun updatePronounsField(pronouns: String) {
        _uiState.update { it.copy(
            pronouns = pronouns,
            modifiedFields = it.modifiedFields + "pronouns"
        )}
        updateSaveButtonState()
    }
    
    fun toggleFavorite() {
        _uiState.update { it.copy(
            isFavorite = !it.isFavorite,
            modifiedFields = it.modifiedFields + "isFavorite"
        )}
        updateSaveButtonState()
    }
    
    fun updateSecurityLevel(level: SecurityLevel) {
        _uiState.update { it.copy(
            securityLevel = level,
            modifiedFields = it.modifiedFields + "securityLevel"
        )}
        updateSaveButtonState()
    }
    
    fun updateDisplayMode(mode: DisplayMode) {
        _uiState.update { it.copy(
            privacyDisplayMode = mode,
            modifiedFields = it.modifiedFields + "privacyDisplayMode"
        )}
        updateSaveButtonState()
    }
    
    fun toggleTag(tagId: String) {
        _uiState.update { state ->
            val newTagIds = if (state.selectedTagIds.contains(tagId)) {
                state.selectedTagIds - tagId
            } else {
                state.selectedTagIds + tagId
            }
            state.copy(
                selectedTagIds = newTagIds,
                modifiedFields = state.modifiedFields + "tags"
            )
        }
        updateSaveButtonState()
    }
    
    fun toggleGroup(groupId: String) {
        _uiState.update { state ->
            val newGroups = state.selectedGroupIds.toMutableSet()
            if (groupId in newGroups) {
                newGroups.remove(groupId)
            } else {
                newGroups.add(groupId)
            }
            state.copy(
                selectedGroupIds = newGroups,
                modifiedFields = state.modifiedFields + "groups"
            )
        }
        updateSaveButtonState()
    }
    
    fun markAsAuthenticated() {
        _uiState.update { it.copy(isAuthenticated = true) }
    }
    
    // Wallet address management
    fun addWalletAddress() {
        val blockchain = _availableBlockchains.value.firstOrNull() ?: BlockchainTypeEntity(
                id = "ethereum",
                name = "Ethereum",
                icon = null,
                symbol = "ETH"
            )
        val newWallet = WalletAddressUiState(
            id = uuid4().toString(),
            address = "",
            label = "",
            blockchain = blockchain.name,
            blockchainUid = blockchain.id,
            isPrimary = _walletAddresses.value.isEmpty(),
            alias = null,
            error = null,
            isSensitive = false,
            validationState = ValidationLoadingState.IDLE,
            suggestion = null,
            resolvedAddress = null,
            isResolvingDomain = false
        )
        _walletAddresses.update { it + newWallet }
        _uiState.update { it.copy(
            modifiedFields = it.modifiedFields + "walletAddresses"
        )}
        updateSaveButtonState()
    }
    
    fun updateWalletAddress(id: String, address: String, blockchain: BlockchainTypeEntity, isPaste: Boolean = false) {
        val currentWallets = _walletAddresses.value
        val walletIndex = currentWallets.indexOfFirst { it.id == id }
        if (walletIndex == -1) return
        
        val currentWallet = currentWallets[walletIndex]
        val previousAddress = currentWallet.address
        val isDeletion = address.length < previousAddress.length
        val isBlockchainChanged = currentWallet.blockchain != blockchain.name
        
        // Extract blockchain identifier with special handling (from old implementation)
        val blockchainIdentifier = when {
            blockchain.name.contains("Vaulta EVM", ignoreCase = true) -> {
                if (blockchain.networkType == "testnet") "VAULTA_EVM_TESTNET" else "VAULTA_EVM"
            }
            blockchain.name.contains("Vaulta", ignoreCase = true) -> {
                if (blockchain.networkType == "testnet") "VAULTA_TESTNET" else "VAULTA"
            }
            blockchain.symbol == "VAULTA" -> "antelope"
            else -> {
                if (blockchain.networkType == "testnet") "${blockchain.symbol}_TESTNET" else blockchain.symbol
            }
        }
        
        // Get existing addresses for duplicate check - IMPORTANT from old implementation
        val existingAddresses = currentWallets
            .filter { it.id != id && it.address.isNotBlank() }
            .map { WalletAddressEntity(
                id = it.id,
                contactId = _uiState.value.contactId ?: "",
                blockchainTypeId = it.blockchainUid,
                address = it.address,
                alias = it.alias,
                walletType = "normal",
                isPrimary = it.isPrimary,
                isSensitive = it.isSensitive,
                isVerified = false,
                verifiedDate = null,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )}
        
        // Synchronous state update - ALWAYS
        _walletAddresses.value = currentWallets.toMutableList().apply {
            this[walletIndex] = currentWallet.copy(
                address = address,
                blockchain = blockchain.name,
                blockchainUid = blockchain.id,
                validationState = when {
                    address.isEmpty() -> ValidationLoadingState.IDLE
                    isDeletion -> ValidationLoadingState.TYPING
                    else -> ValidationLoadingState.TYPING
                },
                error = null,
                suggestion = null
            )
        }
        
        // Update all state in a single atomic update to avoid race conditions
        _uiState.update { state ->
            state.copy(
                modifiedFields = state.modifiedFields + "walletAddresses",
                validationErrors = state.validationErrors - "wallet_address_$walletIndex",
                exchangeAddressWarnings = if (address.isEmpty() || isBlockchainChanged) {
                    state.exchangeAddressWarnings - id
                } else {
                    state.exchangeAddressWarnings
                },
                testnetAddressWarnings = if (address.isEmpty() || isBlockchainChanged) {
                    state.testnetAddressWarnings - id
                } else {
                    state.testnetAddressWarnings
                }
            )
        }
        
        // Update save button state immediately
        updateSaveButtonState()
        
        // Async validation through SafeValidationManager
        screenModelScope.launch {
            when {
                // Empty address - clear validation
                address.isEmpty() -> {
                    validationManager.clearFieldValidation("wallet_$id")
                    _uiState.update { state ->
                        state.copy(
                            walletValidationNeeded = state.walletValidationNeeded - id
                        )
                    }
                }
                
                // Blockchain changed - clear address and skip validation
                isBlockchainChanged && previousAddress.isNotEmpty() -> {
                    _walletAddresses.value = _walletAddresses.value.toMutableList().apply {
                        this[walletIndex] = this[walletIndex].copy(
                            address = "", // Clear on blockchain change
                            validationState = ValidationLoadingState.IDLE
                        )
                    }
                    validationManager.clearFieldValidation("wallet_$id")
                }
                
                // Validate address - SafeValidationManager handles all complexity
                else -> {
                    // Mark as needing validation
                    _uiState.update { state ->
                        state.copy(
                            walletValidationNeeded = state.walletValidationNeeded + id
                        )
                    }
                    
                    // Validate with SafeValidationManager
                    validationManager.validateField(
                        fieldId = "wallet_$id",
                        value = address,
                        blockchain = blockchainIdentifier,
                        existingAddresses = existingAddresses,
                        context = currentValidationContext,
                        isPaste = isPaste,
                        isRequired = true
                    )
                }
            }
        }
    }
    
    fun updateWalletAddressAlias(id: String, alias: String) {
        _walletAddresses.update { wallets ->
            wallets.map { wallet ->
                if (wallet.id == id) wallet.copy(alias = alias) else wallet
            }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "walletAddresses") }
        updateSaveButtonState()
    }
    
    fun removeWalletAddress(id: String) {
        // Cancel any pending validation
        screenModelScope.launch {
            validationManager.cancelFieldValidation("wallet_$id")
        }
        
        _walletAddresses.update { it.filter { wallet -> wallet.id != id } }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "walletAddresses") }
        updateSaveButtonState()
    }
    
    fun setPrimaryWalletAddress(id: String) {
        _walletAddresses.update { wallets ->
            wallets.map { wallet ->
                wallet.copy(isPrimary = wallet.id == id)
            }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "walletAddresses") }
        updateSaveButtonState()
    }
    
    fun toggleWalletSensitive(id: String, isSensitive: Boolean) {
        _walletAddresses.update { wallets ->
            wallets.map { wallet ->
                if (wallet.id == id) wallet.copy(isSensitive = isSensitive) else wallet
            }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "walletAddresses") }
        updateSaveButtonState()
    }
    
    // Email management
    fun addEmail(initialEmail: String = "", initialLabel: String = "Home") {
        val newEmail = EmailAddressUiState(
            id = uuid4().toString(),
            email = initialEmail,
            label = initialLabel,
            isPrimary = _emailAddresses.value.isEmpty()  // First email is primary
        )
        _emailAddresses.update { it + newEmail }
        _uiState.update { it.copy(
            modifiedFields = it.modifiedFields + "emails"
        )}
        updateSaveButtonState()
    }
    
    fun updateEmail(id: String, email: String, label: String) {
        _emailAddresses.update { emails ->
            emails.map { e ->
                if (e.id == id) e.copy(email = email, label = label) else e
            }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "emails") }
        
        // Validate email format using ContactValidationUtils
        if (email.isNotEmpty()) {
            val validationResult = ContactValidationUtils.validateEmail(email)
            val index = _emailAddresses.value.indexOfFirst { it.id == id }
            
            _uiState.update { state ->
                state.copy(
                    validationErrors = if (!validationResult.isValid && validationResult.errorMessage != null) {
                        state.validationErrors + ("email_$index" to validationResult.errorMessage)
                    } else {
                        state.validationErrors - "email_$index"
                    }
                )
            }
            
            // Handle suggestions (e.g., typo corrections)
            validationResult.suggestion?.let { suggestion ->
            }
        } else {
            // Clear error when email is empty
            val index = _emailAddresses.value.indexOfFirst { it.id == id }
            _uiState.update { state ->
                state.copy(validationErrors = state.validationErrors - "email_$index")
            }
        }
        
        updateSaveButtonState()
    }
    
    fun removeEmail(id: String) {
        _emailAddresses.update { it.filter { email -> email.id != id } }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "emails") }
        updateSaveButtonState()
    }
    
    // Phone management
    fun addPhone(initialNumber: String = "", initialLabel: String = "Mobile") {
        val newPhone = PhoneNumberUiState(
            id = uuid4().toString(),
            number = initialNumber,
            label = initialLabel,
            isPrimary = _phoneNumbers.value.isEmpty()  // First phone is primary
        )
        _phoneNumbers.update { it + newPhone }
        _uiState.update { it.copy(
            modifiedFields = it.modifiedFields + "phones"
        )}
        updateSaveButtonState()
    }
    
    fun updatePhone(id: String, number: String, label: String) {
        // Validate phone number using ContactValidationUtils
        val validationResult = ContactValidationUtils.validatePhone(number)
        val formattedNumber = validationResult.formattedNumber ?: number
        
        _phoneNumbers.update { phones ->
            phones.map { p ->
                if (p.id == id) p.copy(number = formattedNumber, label = label) else p
            }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "phones") }
        
        // Update validation errors
        if (number.isNotEmpty()) {
            val index = _phoneNumbers.value.indexOfFirst { it.id == id }
            _uiState.update { state ->
                state.copy(
                    validationErrors = if (!validationResult.isValid && validationResult.errorMessage != null) {
                        state.validationErrors + ("phone_$index" to validationResult.errorMessage)
                    } else {
                        state.validationErrors - "phone_$index"
                    }
                )
            }
        } else {
            // Clear error when phone is empty
            val index = _phoneNumbers.value.indexOfFirst { it.id == id }
            _uiState.update { state ->
                state.copy(validationErrors = state.validationErrors - "phone_$index")
            }
        }
        
        updateSaveButtonState()
    }
    
    fun removePhone(id: String) {
        _phoneNumbers.update { it.filter { phone -> phone.id != id } }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "phones") }
        updateSaveButtonState()
    }
    
    // Physical address management
    fun addAddress(initialStreet: String = "", initialType: String = "Home") {
        val newAddress = PhysicalAddressUiState(
            id = uuid4().toString(),
            street = initialStreet,
            street2 = "",
            city = "",
            state = "",
            zip = "",
            country = "",
            label = initialType,
            isPrimary = _physicalAddresses.value.isEmpty(),
            streetAddress = initialStreet,
            ward = "",
            district = "",
            stateProvince = "",
            postalCode = "",
            addressType = initialType
        )
        _physicalAddresses.update { it + newAddress }
        _uiState.update { it.copy(
            modifiedFields = it.modifiedFields + "addresses"
        )}
        updateSaveButtonState()
    }
    
    fun updateAddress(index: Int, label: String, value: String) {
        // Validate address
        val (isValid, errorMessage) = ContactValidationUtils.validateAddress(value)
        
        _physicalAddresses.update { addresses ->
            addresses.mapIndexed { i, addr ->
                if (i == index) addr.copy(
                    addressType = label, 
                    streetAddress = value,
                    street = value,  // Update both street and streetAddress
                    label = label    // Update label too
                ) else addr
            }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "addresses") }
        
        // Update validation errors
        if (value.isNotEmpty()) {
            _uiState.update { state ->
                state.copy(
                    validationErrors = if (!isValid && errorMessage != null) {
                        state.validationErrors + ("address_$index" to errorMessage)
                    } else {
                        state.validationErrors - "address_$index"
                    }
                )
            }
        } else {
            _uiState.update { state ->
                state.copy(validationErrors = state.validationErrors - "address_$index")
            }
        }
        
        updateSaveButtonState()
    }
    
    fun removeAddress(index: Int) {
        _physicalAddresses.update { addresses ->
            addresses.filterIndexed { i, _ -> i != index }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "addresses") }
        updateSaveButtonState()
    }
    
    // Nickname management
    fun addNickname(initialName: String = "") {
        val newNickname = RelatedNameUiState(
            id = uuid4().toString(),
            name = initialName,
            relationship = "Nickname"
        )
        _nicknames.update { it + newNickname }
        _uiState.update { it.copy(
            modifiedFields = it.modifiedFields + "nicknames"
        )}
        updateSaveButtonState()
    }
    
    fun updateNickname(id: String, name: String) {
        // Validate nickname
        val (isValid, errorMessage) = ContactValidationUtils.validateNickname(name)
        
        _nicknames.update { nicknames ->
            nicknames.map { n ->
                if (n.id == id) n.copy(name = name) else n
            }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "nicknames") }
        
        // Update validation errors
        if (name.isNotEmpty()) {
            val index = _nicknames.value.indexOfFirst { it.id == id }
            _uiState.update { state ->
                state.copy(
                    validationErrors = if (!isValid && errorMessage != null) {
                        state.validationErrors + ("nickname_$index" to errorMessage)
                    } else {
                        state.validationErrors - "nickname_$index"
                    }
                )
            }
        } else {
            val index = _nicknames.value.indexOfFirst { it.id == id }
            _uiState.update { state ->
                state.copy(validationErrors = state.validationErrors - "nickname_$index")
            }
        }
        
        updateSaveButtonState()
    }
    
    fun removeNickname(id: String) {
        _nicknames.update { it.filter { nickname -> nickname.id != id } }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "nicknames") }
        updateSaveButtonState()
    }
    
    // Important date management
    fun addImportantDate(date: LocalDate, title: String = "Birthday", calendarType: CalendarType = CalendarType.SOLAR) {
        val newDate = ImportantDateUiState(
            id = uuid4().toString(),
            date = date.atStartOfDayIn(TimeZone.currentSystemDefault()),
            label = title, // Use the provided title
            calendarType = calendarType.name,
            description = title
        )
        _importantDates.update { it + newDate }
        _uiState.update { it.copy(
            modifiedFields = it.modifiedFields + "importantDates"
        )}
        updateSaveButtonState()
    }
    
    fun updateImportantDate(id: String, date: LocalDate) {
        _importantDates.update { dates ->
            dates.map { d ->
                if (d.id == id) {
                    d.copy(date = date.atStartOfDayIn(TimeZone.currentSystemDefault()))
                } else d
            }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "importantDates") }
        updateSaveButtonState()
    }
    
    fun updateImportantDateDescription(id: String, description: String, calendarType: CalendarType) {
        _importantDates.update { dates ->
            dates.map { d ->
                if (d.id == id) {
                    d.copy(label = description, calendarType = calendarType.name, description = description)
                } else d
            }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "importantDates") }
        updateSaveButtonState()
    }
    
    fun removeImportantDate(id: String) {
        _importantDates.update { it.filter { date -> date.id != id } }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "importantDates") }
        updateSaveButtonState()
    }
    
    // Social profile management
    fun addSocialProfile(initialUsername: String = "", initialPlatform: String = "Facebook") {
        val newProfile = SocialProfileUiState(
            id = uuid4().toString(),
            platform = initialPlatform,
            handle = initialUsername,
            username = initialUsername,
            url = null
        )
        _socialProfiles.update { it + newProfile }
        _uiState.update { it.copy(
            modifiedFields = it.modifiedFields + "socialProfiles"
        )}
        updateSaveButtonState()
    }
    
    fun updateSocialProfile(index: Int, platform: String, username: String) {
        // Validate social profile
        val (isValid, errorOrSuggestion) = ContactValidationUtils.validateSocialProfile(platform, username)
        
        _socialProfiles.update { profiles ->
            profiles.mapIndexed { i, profile ->
                if (i == index) {
                    profile.copy(
                        platform = platform, 
                        handle = username,
                        username = username,
                        url = if (isValid && errorOrSuggestion != null && errorOrSuggestion.contains(".")) {
                            // If it's a suggestion (contains domain), use it as URL
                            if (!errorOrSuggestion.startsWith("http")) "https://$errorOrSuggestion" else errorOrSuggestion
                        } else {
                            profile.url
                        }
                    )
                } else profile
            }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "socialProfiles") }
        
        // Update validation errors
        if (username.isNotEmpty()) {
            _uiState.update { state ->
                state.copy(
                    validationErrors = if (!isValid && errorOrSuggestion != null) {
                        state.validationErrors + ("social_$index" to errorOrSuggestion)
                    } else {
                        state.validationErrors - "social_$index"
                    }
                )
            }
        } else {
            _uiState.update { state ->
                state.copy(validationErrors = state.validationErrors - "social_$index")
            }
        }
        
        updateSaveButtonState()
    }
    
    fun removeSocialProfile(index: Int) {
        _socialProfiles.update { profiles ->
            profiles.filterIndexed { i, _ -> i != index }
        }
        _uiState.update { it.copy(modifiedFields = it.modifiedFields + "socialProfiles") }
        updateSaveButtonState()
    }
    
    // Helper functions for enhanced validation
    
    /**
     * Clean address from invisible characters and whitespace (AC-1.3)
     */
    private fun cleanAddress(input: String): String {
        var cleaned = input.trim()
            .replace("\n", "")
            .replace("\r", "")
            .replace("\t", "")
        
        // Remove invisible characters
        val invisibleChars = listOf(
            "\u200B", "\u200C", "\u200D", "\uFEFF", "\u2060",
            "\u202A", "\u202B", "\u202C", "\u200E", "\u200F"
        )
        
        invisibleChars.forEach { char ->
            cleaned = cleaned.replace(char, "")
        }
        
        // Remove wrapping quotes if present
        if (cleaned.length >= 3) {
            val wrappingPairs = listOf(
                Pair("'", "'"),
                Pair("\"", "\""),
                Pair("`", "`")
            )
            
            for ((start, end) in wrappingPairs) {
                if (cleaned.startsWith(start) && cleaned.endsWith(end)) {
                    cleaned = cleaned.substring(1, cleaned.length - 1)
                    break
                }
            }
        }
        
        return cleaned
    }
    
    /**
     * Extract address from protocol URLs (AC-1.4)
     */
    private fun extractFromProtocolUrl(input: String): String {
        val protocolPatterns = mapOf(
            "bitcoin:" to "bitcoin:([^?]+)",
            "ethereum:" to "ethereum:([^?]+)",
            "solana:" to "solana:([^?]+)",
            "eos:" to "eos:([^?]+)",
            "vaulta:" to "vaulta:([^?]+)"
        )
        
        for ((protocol, pattern) in protocolPatterns) {
            if (input.startsWith(protocol)) {
                val regex = Regex(pattern)
                val match = regex.find(input)
                return match?.groupValues?.get(1) ?: input
            }
        }
        
        return input
    }
    
    // Cache management functions (AC-11)
    private fun getCacheKey(address: String, blockchain: String): String {
        return "${address}_${blockchain}_${currentValidationContext}"
    }
    
    private fun getFromCache(key: String): Triple<ValidationLoadingState, String?, String?>? {
        val entry = validationCache[key] ?: return null
        val now = Clock.System.now()
        
        return if ((now - entry.timestamp).inWholeMilliseconds < CACHE_DURATION_MS) {
            // Convert WalletValidationResult to Triple
            when (val result = entry.result) {
                is WalletValidationResult.Success -> Triple(ValidationLoadingState.VALID, null, null)
                is WalletValidationResult.Warning -> Triple(ValidationLoadingState.WARNING, result.message, null)
                is WalletValidationResult.Error -> Triple(ValidationLoadingState.INVALID, result.message, null)
                else -> Triple(ValidationLoadingState.IDLE, null, null)
            }
        } else {
            validationCache.remove(key)
            null
        }
    }
    
    private fun putInCache(key: String, result: Triple<ValidationLoadingState, String?, String?>) {
        // Convert Triple to WalletValidationResult for caching
        val validationResult = when (result.first) {
            ValidationLoadingState.VALID -> WalletValidationResult.Success("")
            ValidationLoadingState.WARNING -> WalletValidationResult.Warning(result.second ?: "Warning")
            ValidationLoadingState.INVALID -> WalletValidationResult.Error(result.second ?: "Invalid")
            else -> WalletValidationResult.Error("Unknown state")
        }
        validationCache[key] = CachedValidation(
            result = validationResult,
            timestamp = Clock.System.now()
        )
    }
    
    private fun applyCachedValidationResult(id: String, result: Triple<ValidationLoadingState, String?, String?>) {
        _walletAddresses.update { wallets ->
            wallets.map { wallet ->
                if (wallet.id == id) {
                    wallet.copy(
                        validationState = result.first,
                        error = result.second,
                        suggestion = result.third,
                        resolvedAddress = if (result.first == ValidationLoadingState.VALID) wallet.address else null
                    )
                } else wallet
            }
        }
    }
    
    // Rate limiting function - only for network calls
    private fun checkRateLimit(fieldId: String, isNetworkCall: Boolean = false): Boolean {
        // No rate limiting for local validation
        if (!isNetworkCall) return true
        
        val now = Clock.System.now()
        
        // Remove timestamps older than 1 minute
        validationTimestamps.removeAll { timestamp ->
            (now - timestamp).inWholeSeconds > 60
        }
        
        // For network calls, limit to 10 per minute
        val limit = if (isNetworkCall) MAX_VALIDATIONS_PER_MINUTE_NETWORK else MAX_VALIDATIONS_PER_MINUTE_LOCAL
        if (validationTimestamps.size >= limit) {
            return false
        }
        
        validationTimestamps.add(now)
        return true
    }
    
    private fun isBurnAddress(address: String, blockchain: BlockchainTypeEntity): Boolean {
        val lowercaseAddress = address.lowercase()
        
        return when {
            // Ethereum burn addresses
            lowercaseAddress == "0x0000000000000000000000000000000000000000" -> true
            lowercaseAddress == "0x000000000000000000000000000000000000dead" -> true
            
            // Antelope burn addresses
            lowercaseAddress == "eosio.null" -> true
            lowercaseAddress == "vaulta.null" -> true
            
            // Bitcoin burn addresses
            address == "1111111111111111111114oLvT2" -> true
            address == "1BitcoinEaterAddressDontSendf59kuE" -> true
            
            // Solana burn address
            address == "11111111111111111111111111111111" -> true
            
            else -> false
        }
    }
    
    private fun getVaultaSpecificWarning(address: String): String? {
        val lowercaseAddress = address.lowercase()
        
        // Premium account warnings (AC-3.2)
        return when (address.length) {
            1 -> "⚠️ Single character accounts are premium and extremely rare. Verify this account exists."
            2 -> "⚠️ Two character accounts are premium and rare. Verify this account exists."
            3 -> "⚠️ Three character accounts may require special creation process. Verify before sending."
            else -> {
                // System account warning (AC-3.3)
                if (lowercaseAddress in VAULTA_SYSTEM_ACCOUNTS) {
                    "⚠️ This is a system account. Please verify you intend to send to this address."
                } else null
            }
        }
    }
    
    /**
     * Set validation context (AC-10)
     */
    fun setValidationContext(context: ValidationContext) {
        currentValidationContext = context
        // Clear cache when context changes
        validationCache.clear()
    }
    
    
    // Helper function to apply validation result from ComprehensiveAddressValidator
    private fun applyValidationResult(id: String, result: WalletValidationResult) {
        when (result) {
            is WalletValidationResult.Success -> {
                _walletAddresses.update { wallets ->
                    wallets.map { wallet ->
                        if (wallet.id == id) {
                            wallet.copy(
                                validationState = ValidationLoadingState.VALID,
                                error = null,
                                suggestion = null,
                                resolvedAddress = result.cleanAddress,
                                isResolvingDomain = false
                            )
                        } else wallet
                    }
                }
            }
            is WalletValidationResult.Warning -> {
                _walletAddresses.update { wallets ->
                    wallets.map { wallet ->
                        if (wallet.id == id) {
                            wallet.copy(
                                validationState = ValidationLoadingState.WARNING,
                                error = null,
                                suggestion = result.message,
                                resolvedAddress = result.cleanAddress ?: wallet.address,
                                isResolvingDomain = false
                            )
                        } else wallet
                    }
                }
            }
            is WalletValidationResult.Error -> {
                _walletAddresses.update { wallets ->
                    wallets.map { wallet ->
                        if (wallet.id == id) {
                            wallet.copy(
                                validationState = ValidationLoadingState.INVALID,
                                error = result.message,
                                suggestion = null,
                                resolvedAddress = null,
                                isResolvingDomain = false
                            )
                        } else wallet
                    }
                }
            }
        }
    }
    
    // REMOVED: Old validateWalletAddress method - replaced with SafeValidationManager
    // The validation is now handled by SafeValidationManager in updateWalletAddress method
    /*
    private fun validateWalletAddress(id: String, address: String, blockchain: BlockchainTypeEntity) {
        // Cancel existing validation job for this wallet
        validationJobs[id]?.cancel()
        
        // For now, skip rate limiting check at the beginning
        // We'll check it only for network calls later
        
        if (address.isEmpty()) {
            _walletAddresses.update { wallets ->
                wallets.map { wallet ->
                    if (wallet.id == id) {
                        wallet.copy(
                            validationState = ValidationLoadingState.IDLE,
                            error = null,
                            suggestion = null,
                            resolvedAddress = null,
                            isResolvingDomain = false
                        )
                    } else wallet
                }
            }
            return
        }
        
        // Immediately show typing state (FR-2.4)
        _walletAddresses.update { wallets ->
            wallets.map { wallet ->
                if (wallet.id == id) {
                    wallet.copy(
                        validationState = ValidationLoadingState.TYPING,
                        error = null,
                        suggestion = null
                    )
                } else wallet
            }
        }
        
        validationJobs[id] = screenModelScope.launch {
            // Debounce validation (AC-8.1)
            delay(300) // Faster feedback for better UX
            
            // Check cache first (AC-11)
            val cacheKey = getCacheKey(address, blockchain.id)
            val cachedResult = getFromCache(cacheKey)
            if (cachedResult != null) {
                applyCachedValidationResult(id, cachedResult)
                return@launch
            }
            
            // Set validating state
            _walletAddresses.update { wallets ->
                wallets.map { wallet ->
                    if (wallet.id == id) {
                        wallet.copy(validationState = ValidationLoadingState.VALIDATING)
                    } else wallet
                }
            }
            
            try {
                // Clean and extract address (AC-1.3, AC-1.4)
                val cleanedAddress = cleanAddress(address)
                val extractedAddress = extractFromProtocolUrl(cleanedAddress)
                
                // Check if input might be a domain (AC-7)
                val isDomainLike = extractedAddress.contains(".") && 
                    !extractedAddress.startsWith("0x") && 
                    !extractedAddress.contains(" ") &&
                    (extractedAddress.endsWith(".eth") || 
                     extractedAddress.endsWith(".sol") ||
                     extractedAddress.endsWith(".man") ||
                     extractedAddress.endsWith(".gm"))
                
                if (isDomainLike) {
                    // Check rate limit for network calls
                    if (!checkRateLimit(id, isNetworkCall = true)) {
                        _walletAddresses.update { wallets ->
                            wallets.map { wallet ->
                                if (wallet.id == id) {
                                    wallet.copy(
                                        validationState = ValidationLoadingState.INVALID,
                                        error = "Too many domain resolution attempts. Please wait a moment.",
                                        suggestion = null
                                    )
                                } else wallet
                            }
                        }
                        return@launch
                    }
                    
                    // Handle domain resolution
                    _walletAddresses.update { wallets ->
                        wallets.map { wallet ->
                            if (wallet.id == id) {
                                wallet.copy(
                                    isResolvingDomain = true,
                                    suggestion = "Resolving domain..."
                                )
                            } else wallet
                        }
                    }
                    
                    // Use comprehensive validator for domain resolution if available
                    if (comprehensiveValidator != null) {
                        screenModelScope.launch {
                            try {
                                val result = comprehensiveValidator.validateAddressOrDomain(
                                    extractedAddress,
                                    blockchain.id,
                                    existingAddresses = emptyList(),
                                    context = currentValidationContext,
                                    blockchainEntity = blockchain
                                )
                                applyValidationResult(id, result)
                                putInCache(cacheKey, when (result) {
                                    is WalletValidationResult.Success -> Triple(ValidationLoadingState.VALID, null, null)
                                    is WalletValidationResult.Warning -> Triple(ValidationLoadingState.WARNING, null, result.message)
                                    is WalletValidationResult.Error -> Triple(ValidationLoadingState.INVALID, result.message, null)
                                })
                            } catch (e: Exception) {
                                _walletAddresses.update { wallets ->
                                    wallets.map { wallet ->
                                        if (wallet.id == id) {
                                            wallet.copy(
                                                validationState = ValidationLoadingState.INVALID,
                                                error = "Domain resolution failed: ${e.message}",
                                                suggestion = null,
                                                isResolvingDomain = false
                                            )
                                        } else wallet
                                    }
                                }
                            }
                        }
                        return@launch
                    } else {
                        // No domain resolver available
                        delay(500) // Simulate network call
                        _walletAddresses.update { wallets ->
                            wallets.map { wallet ->
                                if (wallet.id == id) {
                                    wallet.copy(
                                        validationState = ValidationLoadingState.INVALID,
                                        error = "Domain resolution not yet implemented",
                                        suggestion = null,
                                        isResolvingDomain = false
                                    )
                                } else wallet
                            }
                        }
                        putInCache(cacheKey, Triple(ValidationLoadingState.INVALID, "Domain resolution not yet implemented", null))
                        return@launch
                    }
                }
                
                // Basic address validation
                val isValid = validateAccountUseCase(extractedAddress, blockchain.id)
                
                if (!isValid) {
                    // Format validation failed - use enhanced error messages
                    val errorMessage = getEnhancedFormatErrorMessage(extractedAddress, blockchain)
                    _walletAddresses.update { wallets ->
                        wallets.map { wallet ->
                            if (wallet.id == id) {
                                wallet.copy(
                                    validationState = ValidationLoadingState.INVALID,
                                    error = errorMessage,
                                    suggestion = null
                                )
                            } else wallet
                        }
                    }
                    putInCache(cacheKey, Triple(ValidationLoadingState.INVALID, errorMessage, null))
                    return@launch
                }
                
                // Security checks (FR-2.3)
                val detectionResult = exchangeAddressDetector.detectAddress(extractedAddress)
                val isExchange = detectionResult.addressType != com.mangala.wallet.features.addressbook.domain.validation.AddressType.NORMAL
                val isTestnet = testnetDetector.isTestnetAddress(extractedAddress, blockchain.id)
                
                // Check for burn addresses (AC-2.1)
                val isBurnAddressResult = isBurnAddress(extractedAddress, blockchain)
                
                // Check for duplicates within the same contact (AC-2.3)
                val isDuplicate = _walletAddresses.value.any { wallet ->
                    wallet.id != id && wallet.address.equals(extractedAddress, ignoreCase = true)
                }
                
                // VAULTA specific checks (AC-3.2, AC-3.3)
                var vaultaWarning: String? = null
                if (blockchain.symbol.equals("VAULTA", ignoreCase = true)) {
                    vaultaWarning = getVaultaSpecificWarning(extractedAddress)
                }
                
                // Determine final validation state and messages
                val (validationState, errorMessage, warningMessage) = when {
                    isBurnAddressResult -> Triple(
                        ValidationLoadingState.INVALID,
                        "🚨 BURN ADDRESS - Funds will be lost!",
                        null
                    )
                    isDuplicate -> Triple(
                        ValidationLoadingState.WARNING,
                        null,
                        "⚠️ This address is already added to this contact"
                    )
                    vaultaWarning != null -> Triple(
                        ValidationLoadingState.WARNING,
                        null,
                        vaultaWarning
                    )
                    isExchange -> Triple(
                        ValidationLoadingState.WARNING,
                        null,
                        "⚠️ ${detectionResult.name ?: "Exchange"} address detected"
                    )
                    isTestnet && blockchain.symbol.equals("BTC", ignoreCase = true) -> Triple(
                        ValidationLoadingState.INVALID,
                        "This is a testnet address. Please select BTC_TESTNET or enter a mainnet address",
                        null
                    )
                    isTestnet -> Triple(
                        ValidationLoadingState.WARNING,
                        null,
                        "⚠️ Testnet address - not for mainnet use"
                    )
                    // EVM network info (AC-5.3)
                    blockchain.symbol in listOf("ETH", "BSC", "POLYGON", "AVAX", "FTM", "ARB", "OP") &&
                    (blockchain.id.contains("TESTNET", ignoreCase = true) || 
                     blockchain.id.contains("GOERLI", ignoreCase = true) ||
                     blockchain.id.contains("SEPOLIA", ignoreCase = true)) -> Triple(
                        ValidationLoadingState.WARNING,
                        null,
                        "ℹ️ EVM addresses are the same for mainnet and testnet. Ensure you've selected the correct network"
                    )
                    else -> Triple(
                        ValidationLoadingState.VALID,
                        null,
                        null
                    )
                }
                
                _walletAddresses.update { wallets ->
                    wallets.map { wallet ->
                        if (wallet.id == id) {
                            wallet.copy(
                                validationState = validationState,
                                error = errorMessage,
                                suggestion = warningMessage,
                                resolvedAddress = extractedAddress
                            )
                        } else wallet
                    }
                }
                
                // Cache the result
                putInCache(cacheKey, Triple(validationState, errorMessage, warningMessage))
                
            } catch (e: Exception) {
                _walletAddresses.update { wallets ->
                    wallets.map { wallet ->
                        if (wallet.id == id) {
                            wallet.copy(
                                validationState = ValidationLoadingState.INVALID,
                                error = "Validation failed: ${e.message}",
                                suggestion = null,
                                isResolvingDomain = false
                            )
                        } else wallet
                    }
                }
            }
        }
    }
    
    
    // Enhanced error messages with specific suggestions (AC-1.2, AC-1.5)
    private fun getEnhancedFormatErrorMessage(address: String, blockchain: BlockchainTypeEntity): String {
        val trimmedAddress = address.trim()
        
        return when {
            // Empty check first
            trimmedAddress.isEmpty() -> "Address cannot be empty"
            
            // VAULTA/Antelope specific validation (AC-3.1, AC-3.2, AC-3.3)
            blockchain.symbol.equals("VAULTA", ignoreCase = true) || 
            blockchain.symbol.equals("VLT", ignoreCase = true) || 
            blockchain.name.contains("Vaultar", ignoreCase = true) ||
            blockchain.symbol.equals("EOS", ignoreCase = true) || 
            blockchain.symbol.equals("WAX", ignoreCase = true) ||
            blockchain.symbol.equals("TELOS", ignoreCase = true) ||
            blockchain.symbol.equals("FIO", ignoreCase = true) -> {
                when {
                    trimmedAddress.length < 3 -> "${blockchain.name} account name is too short (minimum 3 characters)"
                    trimmedAddress.length > 12 -> "${blockchain.name} account name is too long (maximum 12 characters)"
                    !trimmedAddress.matches(Regex("^[a-z1-5.]+$")) -> "${blockchain.name} account name can only contain lowercase letters a-z, numbers 1-5, and dots"
                    trimmedAddress.startsWith(".") || trimmedAddress.endsWith(".") -> "${blockchain.name} account name cannot start or end with a dot"
                    trimmedAddress.contains("..") -> "${blockchain.name} account name cannot contain consecutive dots"
                    else -> "Invalid ${blockchain.name} account format"
                }
            }
            
            // Ethereum/EVM validation with 0x prefix suggestion (AC-1.5)
            blockchain.symbol in listOf("ETH", "BSC", "POLYGON", "AVAX", "FTM", "ARB", "OP") ||
            blockchain.name.contains("Ethereum", ignoreCase = true) ||
            blockchain.name.contains("BNB Smart Chain", ignoreCase = true) ||
            blockchain.name.contains("Polygon", ignoreCase = true) ||
            blockchain.name.contains("Avalanche", ignoreCase = true) -> {
                when {
                    // Check if it looks like ETH address without 0x
                    trimmedAddress.matches(Regex("^[a-fA-F0-9]{40}$")) -> 
                        "💡 Did you mean: 0x${trimmedAddress}?"
                    !trimmedAddress.startsWith("0x") -> "Ethereum addresses must start with 0x"
                    trimmedAddress.length != 42 -> "Ethereum address must be 42 characters (0x + 40 hex chars)"
                    !trimmedAddress.matches(Regex("^0x[a-fA-F0-9]{40}$")) -> "Invalid Ethereum address format (only hex characters allowed after 0x)"
                    else -> "Invalid ${blockchain.name} address format"
                }
            }
            
            // Bitcoin validation with testnet proper message (AC-1.2)
            blockchain.symbol.equals("BTC", ignoreCase = true) -> {
                when {
                    trimmedAddress.startsWith("0x") -> "Looks like an Ethereum address. Please switch to ETH network or enter a Bitcoin address"
                    // Testnet detection with proper error message
                    trimmedAddress.startsWith("m") || trimmedAddress.startsWith("n") || 
                    trimmedAddress.startsWith("2") || trimmedAddress.startsWith("tb1") -> 
                        "This is a testnet address. Please select BTC_TESTNET or enter a mainnet address"
                    // Legacy P2PKH
                    trimmedAddress.startsWith("1") && trimmedAddress.length !in 26..35 -> 
                        "Bitcoin legacy address (P2PKH) should be 26-35 characters"
                    // P2SH
                    trimmedAddress.startsWith("3") && trimmedAddress.length !in 26..35 -> 
                        "Bitcoin P2SH address should be 26-35 characters"
                    // Bech32 SegWit
                    trimmedAddress.startsWith("bc1") -> {
                        when {
                            trimmedAddress != trimmedAddress.lowercase() -> 
                                "💡 Use lowercase → ${trimmedAddress.lowercase()}"
                            trimmedAddress.length !in 42..62 -> "Bitcoin SegWit address should be 42-62 characters"
                            else -> "Invalid Bitcoin SegWit address format"
                        }
                    }
                    // Taproot
                    trimmedAddress.startsWith("bc1p") && trimmedAddress.length != 62 ->
                        "Bitcoin Taproot address should be exactly 62 characters"
                    else -> "Bitcoin address should start with 1, 3, or bc1"
                }
            }
            
            // Solana validation (AC-4.1)
            blockchain.symbol.equals("SOL", ignoreCase = true) -> {
                when {
                    trimmedAddress.length !in 32..44 -> "Solana address should be 32-44 characters"
                    !trimmedAddress.matches(Regex("^[1-9A-HJ-NP-Za-km-z]{32,44}$")) -> 
                        "Invalid Solana address format (base58 characters only)"
                    else -> "Invalid Solana address format"
                }
            }
            
            // Tron validation (AC-4.2)
            blockchain.symbol.equals("TRX", ignoreCase = true) ||
            blockchain.name.contains("Tron", ignoreCase = true) -> {
                when {
                    !trimmedAddress.startsWith("T") -> "Tron addresses must start with T"
                    trimmedAddress.length != 34 -> "Tron address must be exactly 34 characters"
                    !trimmedAddress.matches(Regex("^T[1-9A-HJ-NP-Za-km-z]{33}$")) -> 
                        "Invalid Tron address format (base58 characters only)"
                    else -> "Invalid Tron address format"
                }
            }
            
            // Cardano validation (AC-4.3)
            blockchain.symbol.equals("ADA", ignoreCase = true) ||
            blockchain.name.contains("Cardano", ignoreCase = true) -> {
                when {
                    !trimmedAddress.startsWith("addr1") && !trimmedAddress.startsWith("DdzFF") -> 
                        "Cardano addresses must start with 'addr1' (Shelley) or 'DdzFF' (Byron)"
                    trimmedAddress.startsWith("addr1") && trimmedAddress.length < 90 -> 
                        "Shelley address too short (minimum 90 characters)"
                    trimmedAddress.startsWith("DdzFF") && trimmedAddress.length < 59 -> 
                        "Byron address too short (minimum 59 characters)"
                    else -> "Invalid Cardano address format"
                }
            }
            
            // Polkadot validation (AC-4.4)
            blockchain.symbol.equals("DOT", ignoreCase = true) ||
            blockchain.name.contains("Polkadot", ignoreCase = true) -> {
                when {
                    !trimmedAddress.startsWith("1") -> "Polkadot addresses must start with 1"
                    trimmedAddress.length != 48 -> "Polkadot address must be exactly 48 characters"
                    !trimmedAddress.matches(Regex("^1[1-9A-HJ-NP-Za-km-z]{47}$")) -> 
                        "Invalid Polkadot address format (base58 characters only)"
                    else -> "Invalid Polkadot address format"
                }
            }
            
            // Cosmos validation (AC-4.5)
            blockchain.symbol.equals("ATOM", ignoreCase = true) ||
            blockchain.name.contains("Cosmos", ignoreCase = true) -> {
                when {
                    !trimmedAddress.startsWith("cosmos1") -> "Cosmos addresses must start with 'cosmos1'"
                    trimmedAddress.length != 45 -> "Cosmos address must be exactly 45 characters"
                    !trimmedAddress.matches(Regex("^cosmos1[a-z0-9]{38}$")) -> 
                        "Invalid Cosmos address format (lowercase alphanumeric only)"
                    else -> "Invalid Cosmos address format"
                }
            }
            
            // Binance (BNB) validation
            blockchain.symbol.equals("BNB", ignoreCase = true) -> {
                when {
                    trimmedAddress.startsWith("0x") -> {
                        // BSC format
                        when {
                            trimmedAddress.length != 42 -> "BSC address must be 42 characters (0x + 40 hex)"
                            !trimmedAddress.matches(Regex("^0x[a-fA-F0-9]{40}$")) -> 
                                "Invalid BSC address format (only hex characters allowed)"
                            else -> "Invalid BSC address format"
                        }
                    }
                    trimmedAddress.startsWith("bnb1") -> {
                        // Native BNB format
                        when {
                            trimmedAddress.length != 42 -> "BNB native address must be 42 characters"
                            !trimmedAddress.matches(Regex("^bnb1[a-z0-9]{38}$")) -> 
                                "Invalid BNB address format (lowercase alphanumeric only)"
                            else -> "Invalid BNB address format"
                        }
                    }
                    else -> "BNB address must start with 'bnb1' (native) or '0x' (BSC)"
                }
            }
            
            // General validation
            trimmedAddress.length > 150 -> "Address is too long (maximum 150 characters)"
            trimmedAddress.contains("\u0000") || trimmedAddress.contains("\u200B") -> 
                "Address contains invisible characters. Please retype it."
            else -> "Invalid ${blockchain.name} address format"
        }
    }
    */
    
    fun updateSaveButtonState() {
        val currentState = _uiState.value
        val validationState = validationManager.validationState.value
        
        // 1. Basic requirements - ALWAYS needed
        val hasValidName = currentState.name.isNotBlank()
        val hasWalletAddresses = _walletAddresses.value.isNotEmpty()
        val allWalletAddressesNotEmpty = _walletAddresses.value.all { it.address.isNotBlank() }
        
        // 2. Validation status checks
        val hasActiveValidation = validationState.fields.any { (_, state) ->
            state.status in listOf(
                com.mangala.wallet.features.addressbook.presentation.state.ValidationStatus.VALIDATING,
                com.mangala.wallet.features.addressbook.presentation.state.ValidationStatus.TYPING
            )
        }
        
        val hasBlockingErrors = validationState.fields.any { (_, state) ->
            state.status == com.mangala.wallet.features.addressbook.presentation.state.ValidationStatus.ERROR
        }
        
        // Email and phone validation - check from validation errors instead
        val hasEmailErrors = _emailAddresses.value.indices.any { index ->
            _uiState.value.validationErrors.containsKey("email_$index")
        }
        
        val hasPhoneErrors = _phoneNumbers.value.indices.any { index ->
            _uiState.value.validationErrors.containsKey("phone_$index")
        }
        
        val hasAddressErrors = _physicalAddresses.value.indices.any { index ->
            _uiState.value.validationErrors.containsKey("address_$index")
        }
        
        val hasSocialErrors = _socialProfiles.value.indices.any { index ->
            _uiState.value.validationErrors.containsKey("social_$index")
        }
        
        val hasNicknameErrors = _nicknames.value.indices.any { index ->
            _uiState.value.validationErrors.containsKey("nickname_$index")
        }
        
        // Calculate time in edit mode in seconds
        val timeInEditMode = if (currentState.isEditMode) {
            (Clock.System.now() - currentState.editModeEnteredAt).inWholeSeconds
        } else {
            0
        }
        
        // 3. Edit mode logic - Always allow in edit mode
        val editModeRequirements = true // Simplified - security check moved to save action
        
        // 4. Determine button state and text
        // IMPORTANT: Button text should be stable and clear, not change during validation
        // Button should be disabled during validation, but keep the same text

        // Determine the stable button text based on mode and state
        val stableButtonText = when {
            currentState.isSaving -> "Saving..."
            currentState.isEditMode && currentState.hasModifications -> "Save changes"
            currentState.isEditMode -> "Save"
            else -> "Create contact"
        }

        val (isEnabled, buttonText, helperText) = when {
            // Basic validation failures
            !hasValidName -> Triple(false, stableButtonText, "Contact name is required")
            !hasWalletAddresses -> Triple(false, stableButtonText, "At least one wallet address required")
            !allWalletAddressesNotEmpty -> Triple(false, stableButtonText, "Wallet addresses cannot be empty")

            // Active validation - disable button but keep stable text (improved UX)
            hasActiveValidation && currentState.modifiedFields.isNotEmpty() ->
                Triple(false, stableButtonText, "Please wait while we validate addresses")

            // Contact info validation errors
            hasEmailErrors -> Triple(false, stableButtonText, "Please fix invalid email addresses")
            hasPhoneErrors -> Triple(false, stableButtonText, "Please fix invalid phone numbers")
            hasAddressErrors -> Triple(false, stableButtonText, "Please fix invalid addresses")
            hasSocialErrors -> Triple(false, stableButtonText, "Please fix invalid social profiles")
            hasNicknameErrors -> Triple(false, stableButtonText, "Please fix invalid nicknames")

            // Validation errors
            hasBlockingErrors -> Triple(false, stableButtonText, "Please fix validation errors")

            // All good - enable button with stable text
            else -> Triple(true, stableButtonText, null)
        }
        
        // 5. Special handling for warnings (non-blocking)
        val warnings = mutableListOf<String>()
        if (currentState.exchangeAddressWarnings.isNotEmpty()) {
            warnings.add("⚠️ Exchange addresses detected")
        }
        if (currentState.testnetAddressWarnings.isNotEmpty()) {
            warnings.add("⚠️ Testnet addresses detected")
        }
        
        // Debug logging for button state changes
        if (currentState.saveButtonText != buttonText || currentState.isSaveEnabled != isEnabled) {
            println("🔍 DEBUG: Button state updated - text: '$buttonText', enabled: $isEnabled, helperText: '$helperText'")
        }

        // Update state
        _uiState.update {
            it.copy(
                isSaveEnabled = isEnabled,
                saveButtonText = buttonText,
                saveButtonHelperText = helperText,
                saveWarnings = warnings,
                showSaveWarningDialog = isEnabled && warnings.isNotEmpty(),
                hasModifications = currentState.modifiedFields.isNotEmpty(),
                timeInEditMode = timeInEditMode
            )
        }
        
        // Check for changes if in edit mode
        if (currentState.isEditMode) {
            detectChanges()
        }
    }
    
    // Change detection functions - NEW
    private fun detectChanges() {
        val original = _uiState.value.originalData
        if (original == null) {
            // Not in edit mode or no original data
            return
        }
        
        _uiState.update { state ->
            state.copy(
                hasWalletChanges = hasWalletListChanged(original.wallets, _walletAddresses.value),
                hasEmailChanges = hasEmailListChanged(original.emails, _emailAddresses.value),
                hasPhoneChanges = hasPhoneListChanged(original.phones, _phoneNumbers.value),
                hasAddressChanges = hasAddressListChanged(original.addresses, _physicalAddresses.value),
                hasSocialChanges = hasSocialListChanged(original.socials, _socialProfiles.value),
                hasDateChanges = hasDateListChanged(original.dates, _importantDates.value),
                hasNicknameChanges = hasNicknameListChanged(original.nicknames, _nicknames.value)
            )
        }
    }
    
    private fun hasWalletListChanged(
        original: List<WalletAddressUiState>,
        current: List<WalletAddressUiState>
    ): Boolean {
        if (original.size != current.size) return true
        
        // Check each wallet for changes
        return original.any { originalWallet ->
            val currentWallet = current.find { it.id == originalWallet.id }
            currentWallet == null || walletHasChanged(originalWallet, currentWallet)
        }
    }
    
    private fun walletHasChanged(
        original: WalletAddressUiState,
        current: WalletAddressUiState
    ): Boolean {
        return original.address != current.address ||
               original.blockchain != current.blockchain ||
               original.alias != current.alias ||
               original.isPrimary != current.isPrimary ||
               original.isSensitive != current.isSensitive
    }
    
    private fun hasEmailListChanged(
        original: List<EmailAddressUiState>,
        current: List<EmailAddressUiState>
    ): Boolean {
        if (original.size != current.size) return true
        
        return original.any { originalEmail ->
            val currentEmail = current.find { it.id == originalEmail.id }
            currentEmail == null || emailHasChanged(originalEmail, currentEmail)
        }
    }
    
    private fun emailHasChanged(
        original: EmailAddressUiState,
        current: EmailAddressUiState
    ): Boolean {
        return original.email != current.email ||
               original.label != current.label ||
               original.isPrimary != current.isPrimary
    }
    
    private fun hasPhoneListChanged(
        original: List<PhoneNumberUiState>,
        current: List<PhoneNumberUiState>
    ): Boolean {
        if (original.size != current.size) return true
        
        return original.any { originalPhone ->
            val currentPhone = current.find { it.id == originalPhone.id }
            currentPhone == null || phoneHasChanged(originalPhone, currentPhone)
        }
    }
    
    private fun phoneHasChanged(
        original: PhoneNumberUiState,
        current: PhoneNumberUiState
    ): Boolean {
        return original.number != current.number ||
               original.label != current.label ||
               original.isPrimary != current.isPrimary
    }
    
    private fun hasAddressListChanged(
        original: List<PhysicalAddressUiState>,
        current: List<PhysicalAddressUiState>
    ): Boolean {
        if (original.size != current.size) return true
        
        return original.any { originalAddr ->
            val currentAddr = current.find { it.id == originalAddr.id }
            currentAddr == null || addressHasChanged(originalAddr, currentAddr)
        }
    }
    
    private fun addressHasChanged(
        original: PhysicalAddressUiState,
        current: PhysicalAddressUiState
    ): Boolean {
        return original.streetAddress != current.streetAddress ||
               original.ward != current.ward ||
               original.district != current.district ||
               original.city != current.city ||
               original.stateProvince != current.stateProvince ||
               original.postalCode != current.postalCode ||
               original.country != current.country ||
               original.addressType != current.addressType ||
               original.isPrimary != current.isPrimary
    }
    
    private fun hasSocialListChanged(
        original: List<SocialProfileUiState>,
        current: List<SocialProfileUiState>
    ): Boolean {
        if (original.size != current.size) return true
        
        return original.any { originalSocial ->
            val currentSocial = current.find { it.id == originalSocial.id }
            currentSocial == null || socialHasChanged(originalSocial, currentSocial)
        }
    }
    
    private fun socialHasChanged(
        original: SocialProfileUiState,
        current: SocialProfileUiState
    ): Boolean {
        return original.platform != current.platform ||
               original.username != current.username ||
               original.url != current.url
    }
    
    private fun hasDateListChanged(
        original: List<ImportantDateUiState>,
        current: List<ImportantDateUiState>
    ): Boolean {
        if (original.size != current.size) return true
        
        return original.any { originalDate ->
            val currentDate = current.find { it.id == originalDate.id }
            currentDate == null || dateHasChanged(originalDate, currentDate)
        }
    }
    
    private fun dateHasChanged(
        original: ImportantDateUiState,
        current: ImportantDateUiState
    ): Boolean {
        return original.date != current.date ||
               original.description != current.description ||
               original.calendarType != current.calendarType
    }
    
    private fun nicknameHasChanged(
        original: RelatedNameUiState,
        current: RelatedNameUiState
    ): Boolean {
        return original.name != current.name ||
               original.relationship != current.relationship
    }
    
    private fun hasNicknameListChanged(
        original: List<RelatedNameUiState>,
        current: List<RelatedNameUiState>
    ): Boolean {
        if (original.size != current.size) return true
        
        return original.any { originalNickname ->
            val currentNickname = current.find { it.id == originalNickname.id }
            currentNickname == null || nicknameHasChanged(originalNickname, currentNickname)
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    // Tag management
    fun loadAvailableTags() {
        screenModelScope.launch {
            try {
                _availableTags.value = getActiveTagsUseCase().getOrDefault(emptyList())
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load tags: ${e.message}") }
            }
        }
    }
    
    fun createTag(name: String, color: String) {
        screenModelScope.launch {
            try {
                // Call the use case with name and color parameters
                val result = createTagUseCase(name = name, color = color)
                
                result.fold(
                    onSuccess = { tagEntity ->
                        // Reload available tags
                        loadAvailableTags()
                        
                        // Auto-select the newly created tag
                        _uiState.update { state ->
                            state.copy(
                                selectedTagIds = state.selectedTagIds + tagEntity.id,
                                modifiedFields = state.modifiedFields + "tags"
                            )
                        }
                        
                        updateSaveButtonState()
                    },
                    onFailure = { exception ->
                        _uiState.update { it.copy(error = exception.message ?: "Failed to create tag") }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to create tag: ${e.message}") }
            }
        }
    }
    
    // Mapping functions for clean architecture
    fun mapImportantDateUiStateToDomain(dateUiState: ImportantDateUiState): ImportantDate? {
        return try {
            val domain = ImportantDate(
                id = dateUiState.id,
                title = dateUiState.label,
                date = dateUiState.date.toLocalDateTime(TimeZone.currentSystemDefault()).date,
                calendarType = CalendarType.valueOf(dateUiState.calendarType.uppercase()),
                lunarDate = null
            )
            domain
        } catch (e: Exception) {
            null
        }
    }
    
    fun mapGroupModelToEntity(groupModel: GroupModel): com.mangala.wallet.features.addressbook.data.model.group.GroupEntity {
        val fixedTimestamp = kotlinx.datetime.Instant.fromEpochMilliseconds(0)
        return com.mangala.wallet.features.addressbook.data.model.group.GroupEntity(
            id = groupModel.id,
            name = groupModel.name,
            description = groupModel.description,
            color = groupModel.color,
            icon = groupModel.icon,
            mainBlockchainId = groupModel.mainBlockchainId,
            privacyLevel = com.mangala.wallet.features.addressbook.data.model.enum.PrivacyLevel.valueOf(
                groupModel.privacyLevel.uppercase()
            ),
            securityLevel = com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel.valueOf(
                groupModel.securityLevel.uppercase()
            ),
            createdAt = fixedTimestamp,
            updatedAt = fixedTimestamp
        )
    }
    
    fun getAvailableGroupsAsEntities(): List<com.mangala.wallet.features.addressbook.data.model.group.GroupEntity> {
        return _uiState.value.availableGroups.map { mapGroupModelToEntity(it) }
    }
    
    fun getImportantDatesAsDomain(): List<ImportantDate> {
        return _importantDates.value.mapNotNull { mapImportantDateUiStateToDomain(it) }
    }
    
    // Save contact
    fun saveContact() {
        // Check security authentication if needed
        val currentState = _uiState.value
        if (currentState.securityLevel in listOf(SecurityLevel.HIGH, SecurityLevel.MAXIMUM) 
            && !currentState.isAuthenticated) {
            // Show authentication dialog instead of blocking save
            _uiState.update { it.copy(
                error = "Authentication required to save secure contacts"
            )}
            return
        }
        
        if (_uiState.value.isEditMode) {
            updateContact()
        } else {
            createContact()
        }
    }
    
    private fun createContact() {
        screenModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true, error = null) }
                
                // Create contact entity
                val contactEntity = ContactEntity(
                    id = uuid4().toString(),
                    name = _uiState.value.name,
                    notes = _uiState.value.note.takeIf { it.isNotBlank() },
                    avatar = _uiState.value.icon,
                    solarBirthday = null,
                    lunarBirthday = null,
                    isSensitive = _uiState.value.securityLevel == SecurityLevel.HIGH,
                    securityLevel = _uiState.value.securityLevel,
                    authRequirement = _uiState.value.authRequirement,
                    privacyDisplayMode = _uiState.value.privacyDisplayMode,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now(),
                    lastViewedAt = null,
                    syncStatus = SyncStatus.SYNCED,
                    encryptedData = null
                )
                
                // Create contact
                createContactUseCase(contactEntity)
                
                // Filter empty fields before saving
                val nonEmptyEmails = _emailAddresses.value.filter { it.email.isNotBlank() }
                val nonEmptyPhones = _phoneNumbers.value.filter { it.number.isNotBlank() }
                val nonEmptyAddresses = _physicalAddresses.value.filter { !it.streetAddress.isNullOrBlank() }
                val nonEmptyProfiles = _socialProfiles.value.filter { it.username.isNotBlank() }
                val nonEmptyNicknames = _nicknames.value.filter { it.name.isNotBlank() }
                
                // Save related data in parallel
                listOf(
                    // Wallet addresses
                    if (_walletAddresses.value.isNotEmpty()) {
                        launch {
                            val entities = _walletAddresses.value.map { wallet ->
                                WalletAddressEntity(
                                    id = wallet.id,
                                    contactId = contactEntity.id,
                                    blockchainTypeId = wallet.blockchainUid,
                                    address = wallet.address,
                                    alias = wallet.alias?.takeIf { it.isNotBlank() },
                                    walletType = "normal",
                                    isPrimary = wallet.isPrimary,
                                    isSensitive = wallet.isSensitive,
                                    isVerified = false,
                                    verifiedDate = null,
                                    createdAt = Clock.System.now(),
                                    updatedAt = Clock.System.now()
                                )
                            }
                            insertWalletAddressesBatchUseCase(entities)
                        }
                    } else null,
                    
                    // Emails - only save non-empty emails
                    if (nonEmptyEmails.isNotEmpty()) {
                        launch {
                            val entities = nonEmptyEmails.map { email ->
                                EmailAddressEntity(
                                    id = email.id,
                                    contactId = contactEntity.id,
                                    email = email.email,
                                    label = email.label.takeIf { it.isNotBlank() },
                                    isPrimary = email.isPrimary,
                                    createdAt = Clock.System.now(),
                                    updatedAt = Clock.System.now()
                                )
                            }
                            insertEmailAddressesBatchUseCase(entities)
                        }
                    } else null,
                    
                    // Phone numbers - only save non-empty phone numbers
                    if (nonEmptyPhones.isNotEmpty()) {
                        launch {
                            val entities = nonEmptyPhones.map { phone ->
                                PhoneNumberEntity(
                                    id = phone.id,
                                    contactId = contactEntity.id,
                                    phoneNumber = phone.number,
                                    label = phone.label.takeIf { it.isNotBlank() },
                                    isPrimary = phone.isPrimary,
                                    createdAt = Clock.System.now(),
                                    updatedAt = Clock.System.now()
                                )
                            }
                            insertPhoneNumbersBatchUseCase(entities)
                        }
                    } else null,
                    
                    // Tags
                    if (_uiState.value.selectedTagIds.isNotEmpty()) {
                        launch {
                            batchAssignTagsToContactUseCase(
                                contactEntity.id,
                                _uiState.value.selectedTagIds.toList()
                            )
                        }
                    } else null,
                    
                    // Groups
                    if (_uiState.value.selectedGroupIds.isNotEmpty() && _walletAddresses.value.isNotEmpty()) {
                        launch {
                            val primaryWallet = _walletAddresses.value.find { it.isPrimary } ?: _walletAddresses.value.first()
                            batchAssignGroupsToContactUseCase(
                                contactEntity.id,
                                _uiState.value.selectedGroupIds.toList(),
                                primaryWallet.id
                            )
                        }
                    } else null,
                    
                    // Favorite
                    if (_uiState.value.isFavorite) {
                        launch {
                            addFavoriteUseCase(contactEntity.id)
                        }
                    } else null,
                    
                    // Physical addresses - only save addresses with at least street address
                    if (nonEmptyAddresses.isNotEmpty()) {
                        launch {
                            val entities = nonEmptyAddresses.mapIndexed { index, address ->
                                PhysicalAddressEntity(
                                    id = uuid4().toString(),
                                    contactId = contactEntity.id,
                                    addressType = address.addressType?.takeIf { it.isNotBlank() },
                                    streetAddress = address.streetAddress,
                                    ward = address.ward?.takeIf { it.isNotBlank() },
                                    district = address.district?.takeIf { it.isNotBlank() },
                                    city = address.city?.takeIf { it.isNotBlank() },
                                    stateProvince = address.stateProvince?.takeIf { it.isNotBlank() },
                                    postalCode = address.postalCode?.takeIf { it.isNotBlank() },
                                    country = address.country?.takeIf { it.isNotBlank() },
                                    isPrimary = index == 0,
                                    createdAt = Clock.System.now(),
                                    updatedAt = Clock.System.now()
                                )
                            }
                            insertPhysicalAddressesBatchUseCase(entities)
                        }
                    } else null,
                    
                    // Social profiles - only save profiles with username
                    if (nonEmptyProfiles.isNotEmpty()) {
                        launch {
                            val entities = nonEmptyProfiles.map { profile ->
                                SocialProfileEntity(
                                    id = uuid4().toString(),
                                    contactId = contactEntity.id,
                                    platform = profile.platform,
                                    username = profile.username,
                                    url = profile.url?.takeIf { it.isNotBlank() },
                                    createdAt = Clock.System.now(),
                                    updatedAt = Clock.System.now()
                                )
                            }
                            insertSocialProfilesBatchUseCase(entities)
                        }
                    } else null,
                    
                    // Important dates
                    if (_importantDates.value.isNotEmpty()) {
                        launch {
                            val entities = _importantDates.value.mapNotNull { dateUiState ->
                                dateUiState.date?.let { instant ->
                                    ImportantDateEntity(
                                        id = uuid4().toString(),
                                        contactId = contactEntity.id,
                                        date = instant,
                                        description = dateUiState.description ?: "",
                                        calendarType = CalendarType.valueOf(dateUiState.calendarType),
                                        createdAt = Clock.System.now(),
                                        updatedAt = Clock.System.now()
                                    )
                                }
                            }
                            insertImportantDatesBatchUseCase(entities)
                        }
                    } else null,
                    
                    // Nicknames (Related names) - only save nicknames with name
                    if (nonEmptyNicknames.isNotEmpty()) {
                        launch {
                            val entities = nonEmptyNicknames.map { nickname ->
                                RelatedNameEntity(
                                    id = uuid4().toString(),
                                    contactId = contactEntity.id,
                                    name = nickname.name,
                                    relationship = nickname.relationship.takeIf { it.isNotBlank() } ?: "Nickname",
                                    createdAt = Clock.System.now(),
                                    updatedAt = Clock.System.now()
                                )
                            }
                            insertRelatedNamesBatchUseCase(entities)
                        }
                    } else null
                ).mapNotNull { it }.forEach { it.join() }
                
                // Navigate to success
                onNavigateCallback?.invoke(contactEntity.id)
                
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = "Failed to create contact: ${e.message}",
                    isSaving = false
                )}
            }
        }
    }
    
    private fun updateContact() {
        val contactId = _uiState.value.contactId ?: return
        
        screenModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true, error = null) }
                
                // 1. Update main contact entity
                val contactEntity = ContactEntity(
                    id = contactId,
                    name = _uiState.value.name,
                    notes = _uiState.value.note.takeIf { it.isNotBlank() },
                    avatar = _uiState.value.icon,
                    solarBirthday = null,
                    lunarBirthday = null,
                    isSensitive = _uiState.value.securityLevel == SecurityLevel.HIGH,
                    securityLevel = _uiState.value.securityLevel,
                    authRequirement = _uiState.value.authRequirement,
                    privacyDisplayMode = _uiState.value.privacyDisplayMode,
                    createdAt = Clock.System.now(), // Will be ignored in update
                    updatedAt = Clock.System.now(),
                    lastViewedAt = null,
                    syncStatus = SyncStatus.SYNCED,
                    encryptedData = null
                )
                
                updateContactUseCase(contactEntity)
                
                // 2. Handle collections with Hybrid Pattern
                // Only update collections that have changes
                val currentState = _uiState.value
                val originalData = currentState.originalData
                
                // Wallet addresses - Check if changed
                if (currentState.hasWalletChanges && originalData != null) {
                    updateWalletAddresses(contactId, originalData.walletIds)
                }
                
                // Email addresses - Check if changed
                if (currentState.hasEmailChanges && originalData != null) {
                    updateEmailAddresses(contactId, originalData.emailIds)
                }
                
                // Phone numbers - Check if changed
                if (currentState.hasPhoneChanges && originalData != null) {
                    updatePhoneNumbers(contactId, originalData.phoneIds)
                }
                
                // Physical addresses - Check if changed
                if (currentState.hasAddressChanges && originalData != null) {
                    updatePhysicalAddresses(contactId, originalData.addressIds)
                }
                
                // Social profiles - Check if changed
                if (currentState.hasSocialChanges && originalData != null) {
                    updateSocialProfiles(contactId, originalData.socialIds)
                }
                
                // Important dates - Check if changed
                if (currentState.hasDateChanges && originalData != null) {
                    updateImportantDates(contactId, originalData.dateIds)
                }
                
                // Nicknames - Check if changed
                if (currentState.hasNicknameChanges && originalData != null) {
                    updateNicknames(contactId, originalData.nicknameIds)
                }
                
                // Update tags (always update as we have proper use case)
                batchAssignTagsToContactUseCase(
                    contactId,
                    _uiState.value.selectedTagIds.toList()
                )
                
                // Update favorite status
                val originalDetail = originalData?.let { getContactDetailByIdUseCase(contactId) }
                if (_uiState.value.isFavorite && originalDetail?.isFavorite != true) {
                    addFavoriteUseCase(contactId)
                } else if (!_uiState.value.isFavorite && originalDetail?.isFavorite == true) {
                    removeFavoriteUseCase(contactId)
                }
                
                _uiState.update { it.copy(isSaving = false) }
                
                // Navigate to success
                onNavigateCallback?.invoke(contactId)
                
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = "Failed to update contact: ${e.message}",
                    isSaving = false
                )}
            }
        }
    }
    
    // Helper functions for updating collections - NEW
    private suspend fun updateWalletAddresses(contactId: String, originalIds: Set<String>) {
        val activeWallets = _walletAddresses.value.filter { it.address.isNotBlank() }
        val currentWalletIds = activeWallets.map { it.id }.toSet()
        
        // Use facade if available for full CRUD
        if (walletUseCase != null) {
            // 1. DELETE removed wallets
            (originalIds - currentWalletIds).forEach { walletId ->
                walletUseCase.deleteWalletAddress(walletId)
            }
            
            // 2. UPDATE existing wallets
            activeWallets.filter { wallet -> 
                originalIds.contains(wallet.id) 
            }.forEach { wallet ->
                val original = _uiState.value.originalData?.wallets?.find { it.id == wallet.id }
                if (original != null && walletHasChanged(original, wallet)) {
                    walletUseCase.updateWalletAddress(
                        WalletAddressEntity(
                            id = wallet.id,
                            contactId = contactId,
                            blockchainTypeId = wallet.blockchainUid,
                            address = wallet.address,
                            alias = wallet.alias?.takeIf { it.isNotBlank() },
                            walletType = "normal",
                            isPrimary = wallet.isPrimary,
                            isSensitive = wallet.isSensitive,
                            isVerified = false,
                            verifiedDate = null,
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                    )
                }
            }
            
            // 3. INSERT new wallets
            val newWallets = activeWallets.filter { wallet ->
                !originalIds.contains(wallet.id)
            }
            if (newWallets.isNotEmpty()) {
                val walletEntities = newWallets.map { wallet ->
                    WalletAddressEntity(
                        id = wallet.id,
                        contactId = contactId,
                        blockchainTypeId = wallet.blockchainUid,
                        address = wallet.address,
                        alias = wallet.alias?.takeIf { it.isNotBlank() },
                        walletType = "normal",
                        isPrimary = wallet.isPrimary,
                        isSensitive = wallet.isSensitive,
                        isVerified = false,
                        verifiedDate = null,
                        createdAt = Clock.System.now(),
                        updatedAt = Clock.System.now()
                    )
                }
                insertWalletAddressesBatchUseCase(walletEntities)
            }
        } else {
            // Fallback to INSERT-only
            val newWallets = activeWallets.filter { wallet ->
                !originalIds.contains(wallet.id)
            }
            
            val walletEntities = newWallets.map { wallet ->
                WalletAddressEntity(
                    id = wallet.id,
                    contactId = contactId,
                    blockchainTypeId = wallet.blockchainUid,
                    address = wallet.address,
                    alias = wallet.alias?.takeIf { it.isNotBlank() },
                    walletType = "normal",
                    isPrimary = wallet.isPrimary,
                    isSensitive = wallet.isSensitive,
                    isVerified = false,
                    verifiedDate = null,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            }
            
            if (walletEntities.isNotEmpty()) {
                insertWalletAddressesBatchUseCase(walletEntities)
            }
        }
    }
    
    private suspend fun updateEmailAddresses(contactId: String, originalIds: Set<String>) {
        val activeEmails = _emailAddresses.value.filter { it.email.isNotBlank() }
        val currentEmailIds = activeEmails.map { it.id }.toSet()
        
        // Use facade if available for full CRUD
        if (communicationUseCase != null) {
            // 1. DELETE removed emails
            (originalIds - currentEmailIds).forEach { emailId ->
                communicationUseCase.deleteEmailAddress(emailId)
            }
            
            // 2. UPDATE existing emails
            activeEmails.filter { email -> 
                originalIds.contains(email.id) 
            }.forEach { email ->
                val original = _uiState.value.originalData?.emails?.find { it.id == email.id }
                if (original != null && emailHasChanged(original, email)) {
                    communicationUseCase.updateEmailAddress(
                        EmailAddressEntity(
                            id = email.id,
                            contactId = contactId,
                            email = email.email,
                            label = email.label.takeIf { it.isNotBlank() },
                            isPrimary = email.isPrimary,
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                    )
                }
            }
            
            // 3. INSERT new emails
            val newEmails = activeEmails.filter { email ->
                !originalIds.contains(email.id)
            }
            if (newEmails.isNotEmpty()) {
                val emailEntities = newEmails.map { email ->
                    EmailAddressEntity(
                        id = email.id,
                        contactId = contactId,
                        email = email.email,
                        label = email.label.takeIf { it.isNotBlank() },
                        isPrimary = email.isPrimary,
                        createdAt = Clock.System.now(),
                        updatedAt = Clock.System.now()
                    )
                }
                insertEmailAddressesBatchUseCase(emailEntities)
            }
        } else {
            // Fallback to INSERT-only
            val newEmails = activeEmails.filter { email ->
                !originalIds.contains(email.id)
            }
            
            val emailEntities = newEmails.map { email ->
                EmailAddressEntity(
                    id = email.id,
                    contactId = contactId,
                    email = email.email,
                    label = email.label.takeIf { it.isNotBlank() },
                    isPrimary = email.isPrimary,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            }
            
            if (emailEntities.isNotEmpty()) {
                insertEmailAddressesBatchUseCase(emailEntities)
            }
        }
    }
    
    private suspend fun updatePhoneNumbers(contactId: String, originalIds: Set<String>) {
        val activePhones = _phoneNumbers.value.filter { it.number.isNotBlank() }
        val currentPhoneIds = activePhones.map { it.id }.toSet()
        
        // Use facade if available, otherwise fallback to current limited implementation
        if (communicationUseCase != null) {
            // 1. DELETE removed phones
            (originalIds - currentPhoneIds).forEach { phoneId ->
                communicationUseCase.deletePhoneNumber(phoneId)
            }
            
            // 2. UPDATE existing phones
            activePhones.filter { phone -> 
                originalIds.contains(phone.id) 
            }.forEach { phone ->
                val original = _uiState.value.originalData?.phones?.find { it.id == phone.id }
                if (original != null && phoneHasChanged(original, phone)) {
                    communicationUseCase.updatePhoneNumber(
                        PhoneNumberEntity(
                            id = phone.id,
                            contactId = contactId,
                            phoneNumber = phone.number,
                            label = phone.label.takeIf { it.isNotBlank() },
                            isPrimary = phone.isPrimary,
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                    )
                }
            }
            
            // 3. INSERT new phones
            val newPhones = activePhones.filter { phone ->
                !originalIds.contains(phone.id)
            }
            if (newPhones.isNotEmpty()) {
                val phoneEntities = newPhones.map { phone ->
                    PhoneNumberEntity(
                        id = phone.id,
                        contactId = contactId,
                        phoneNumber = phone.number,
                        label = phone.label.takeIf { it.isNotBlank() },
                        isPrimary = phone.isPrimary,
                        createdAt = Clock.System.now(),
                        updatedAt = Clock.System.now()
                    )
                }
                insertPhoneNumbersBatchUseCase(phoneEntities)
            }
        } else {
            // Fallback to INSERT-only implementation
            val newPhones = activePhones.filter { phone ->
                !originalIds.contains(phone.id)
            }
            
            val phoneEntities = newPhones.map { phone ->
                PhoneNumberEntity(
                    id = phone.id,
                    contactId = contactId,
                    phoneNumber = phone.number,
                    label = phone.label.takeIf { it.isNotBlank() },
                    isPrimary = phone.isPrimary,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            }
            
            if (phoneEntities.isNotEmpty()) {
                insertPhoneNumbersBatchUseCase(phoneEntities)
            }
        }
    }
    
    private suspend fun updatePhysicalAddresses(contactId: String, originalIds: Set<String>) {
        val activeAddresses = _physicalAddresses.value.filter { !it.streetAddress.isNullOrBlank() }
        val currentAddressIds = activeAddresses.map { it.id }.toSet()
        
        // Use facade if available for full CRUD
        if (communicationUseCase != null) {
            // 1. DELETE removed addresses
            (originalIds - currentAddressIds).forEach { addressId ->
                try {
                    communicationUseCase.deletePhysicalAddress(addressId).fold(
                        onSuccess = { /* Deleted successfully */ },
                        onFailure = { error ->
                            // Log error but continue with other operations
                            error.printStackTrace()
                        }
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // 2. UPDATE existing addresses that changed
            activeAddresses.filter { address ->
                originalIds.contains(address.id)
            }.forEach { address ->
                val original = _uiState.value.originalData?.addresses?.find { it.id == address.id }
                if (original != null && addressHasChanged(original, address)) {
                    try {
                        val entity = PhysicalAddressEntity(
                            id = address.id,
                            contactId = contactId,
                            addressType = address.addressType,
                            streetAddress = address.streetAddress,
                            ward = address.ward,
                            district = address.district,
                            city = address.city,
                            stateProvince = address.stateProvince,
                            postalCode = address.postalCode,
                            country = address.country,
                            isPrimary = activeAddresses.indexOf(address) == 0,
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                        communicationUseCase.updatePhysicalAddress(entity).fold(
                            onSuccess = { /* Updated successfully */ },
                            onFailure = { error ->
                                error.printStackTrace()
                            }
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            
            // 3. INSERT only new addresses
            val newAddresses = activeAddresses.filter { address ->
                !originalIds.contains(address.id)
            }
            val newAddressEntities = newAddresses.mapIndexed { index, address ->
                PhysicalAddressEntity(
                    id = address.id,
                    contactId = contactId,
                    addressType = address.addressType?.takeIf { it.isNotBlank() },
                    streetAddress = address.streetAddress,
                    ward = address.ward?.takeIf { it.isNotBlank() },
                    district = address.district?.takeIf { it.isNotBlank() },
                    city = address.city?.takeIf { it.isNotBlank() },
                    stateProvince = address.stateProvince?.takeIf { it.isNotBlank() },
                    postalCode = address.postalCode?.takeIf { it.isNotBlank() },
                    country = address.country?.takeIf { it.isNotBlank() },
                    isPrimary = activeAddresses.indexOf(address) == 0,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            }
            if (newAddressEntities.isNotEmpty()) {
                insertPhysicalAddressesBatchUseCase(newAddressEntities)
            }
        } else {
            // Fallback to INSERT-only if no facade available
            val newAddresses = activeAddresses.filter { address ->
                !originalIds.contains(address.id)
            }
            val addressEntities = newAddresses.mapIndexed { index, address ->
                PhysicalAddressEntity(
                    id = address.id,
                    contactId = contactId,
                    addressType = address.addressType?.takeIf { it.isNotBlank() },
                    streetAddress = address.streetAddress,
                    ward = address.ward?.takeIf { it.isNotBlank() },
                    district = address.district?.takeIf { it.isNotBlank() },
                    city = address.city?.takeIf { it.isNotBlank() },
                    stateProvince = address.stateProvince?.takeIf { it.isNotBlank() },
                    postalCode = address.postalCode?.takeIf { it.isNotBlank() },
                    country = address.country?.takeIf { it.isNotBlank() },
                    isPrimary = activeAddresses.indexOf(address) == 0,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            }
            if (addressEntities.isNotEmpty()) {
                insertPhysicalAddressesBatchUseCase(addressEntities)
            }
        }
    }
    
    private suspend fun updateSocialProfiles(contactId: String, originalIds: Set<String>) {
        val activeProfiles = _socialProfiles.value.filter { it.username.isNotBlank() }
        val currentProfileIds = activeProfiles.map { it.id }.toSet()
        
        // Use facade if available - social profiles don't have individual update/delete,
        // so we need to use delete all and re-insert approach
        if (communicationUseCase != null && (originalIds != currentProfileIds || 
            activeProfiles.any { profile ->
                val original = _uiState.value.originalData?.socials?.find { it.id == profile.id }
                original != null && socialHasChanged(original, profile)
            })) {
            
            // For social profiles, we need to delete all and re-insert
            // because the facade only has batch operations
            try {
                // 1. Delete all existing social profiles
                communicationUseCase.deleteSocialProfilesByContactId(contactId).fold(
                    onSuccess = {
                        // 2. Re-insert all active profiles
                        val allSocialEntities = activeProfiles.map { profile ->
                            SocialProfileEntity(
                                id = profile.id,
                                contactId = contactId,
                                platform = profile.platform,
                                username = profile.username,
                                url = profile.url?.takeIf { it.isNotBlank() },
                                createdAt = Clock.System.now(),
                                updatedAt = Clock.System.now()
                            )
                        }
                        
                        if (allSocialEntities.isNotEmpty()) {
                            communicationUseCase.saveSocialProfiles(contactId, allSocialEntities)
                        }
                    },
                    onFailure = { error ->
                        error.printStackTrace()
                        // Fallback to insert-only approach
                        updateSocialProfilesFallback(contactId, originalIds)
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to insert-only approach
                updateSocialProfilesFallback(contactId, originalIds)
            }
        } else {
            // Fallback to INSERT-only if no facade available or no changes
            updateSocialProfilesFallback(contactId, originalIds)
        }
    }
    
    private suspend fun updateSocialProfilesFallback(contactId: String, originalIds: Set<String>) {
        val activeProfiles = _socialProfiles.value.filter { it.username.isNotBlank() }
        val newProfiles = activeProfiles.filter { profile ->
            !originalIds.contains(profile.id)
        }
        
        val socialEntities = newProfiles.map { profile ->
            SocialProfileEntity(
                id = profile.id,
                contactId = contactId,
                platform = profile.platform,
                username = profile.username,
                url = profile.url?.takeIf { it.isNotBlank() },
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
        }
        
        if (socialEntities.isNotEmpty()) {
            insertSocialProfilesBatchUseCase(socialEntities)
        }
    }
    
    private suspend fun updateImportantDates(contactId: String, originalIds: Set<String>) {
        val activeDates = _importantDates.value.filter { it.date != null }
        val currentDateIds = activeDates.map { it.id }.toSet()
        
        // Use facade if available for full CRUD
        if (communicationUseCase != null) {
            // 1. DELETE removed dates
            (originalIds - currentDateIds).forEach { dateId ->
                communicationUseCase.deleteImportantDate(dateId)
            }
            
            // 2. UPDATE existing dates
            activeDates.filter { date -> 
                originalIds.contains(date.id) 
            }.forEach { date ->
                val original = _uiState.value.originalData?.dates?.find { it.id == date.id }
                if (original != null && dateHasChanged(original, date)) {
                    communicationUseCase.updateImportantDate(
                        date.id,
                        date.date!!,
                        date.description ?: "",
                        CalendarType.valueOf(date.calendarType)
                    )
                }
            }
            
            // 3. INSERT new dates
            val newDates = activeDates.filter { date ->
                !originalIds.contains(date.id)
            }
            if (newDates.isNotEmpty()) {
                val dateEntities = newDates.mapNotNull { dateUiState ->
                    dateUiState.date?.let { instant ->
                        ImportantDateEntity(
                            id = dateUiState.id,
                            contactId = contactId,
                            date = instant,
                            description = dateUiState.description ?: "",
                            calendarType = CalendarType.valueOf(dateUiState.calendarType),
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                    }
                }
                insertImportantDatesBatchUseCase(dateEntities)
            }
        } else {
            // Fallback to INSERT-only
            val newDates = activeDates.filter { date ->
                !originalIds.contains(date.id)
            }
            
            val dateEntities = newDates.mapNotNull { dateUiState ->
                dateUiState.date?.let { instant ->
                    ImportantDateEntity(
                        id = dateUiState.id,
                        contactId = contactId,
                        date = instant,
                        description = dateUiState.description ?: "",
                        calendarType = CalendarType.valueOf(dateUiState.calendarType),
                        createdAt = Clock.System.now(),
                        updatedAt = Clock.System.now()
                    )
                }
            }
            
            if (dateEntities.isNotEmpty()) {
                insertImportantDatesBatchUseCase(dateEntities)
            }
        }
    }
    
    private suspend fun updateNicknames(contactId: String, originalIds: Set<String>) {
        val activeNicknames = _nicknames.value.filter { it.name.isNotBlank() }
        val currentNicknameIds = activeNicknames.map { it.id }.toSet()
        
        // Use facade if available for full CRUD
        if (communicationUseCase != null) {
            // 1. DELETE removed nicknames
            (originalIds - currentNicknameIds).forEach { nicknameId ->
                try {
                    communicationUseCase.deleteRelatedName(nicknameId).fold(
                        onSuccess = { /* Deleted successfully */ },
                        onFailure = { error ->
                            // Log error but continue with other operations
                            error.printStackTrace()
                        }
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // 2. UPDATE existing nicknames that changed
            activeNicknames.filter { nickname ->
                originalIds.contains(nickname.id)
            }.forEach { nickname ->
                val original = _uiState.value.originalData?.nicknames?.find { it.id == nickname.id }
                if (original != null && nicknameHasChanged(original, nickname)) {
                    try {
                        val entity = RelatedNameEntity(
                            id = nickname.id,
                            contactId = contactId,
                            name = nickname.name,
                            relationship = nickname.relationship.takeIf { it.isNotBlank() } ?: "Nickname",
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                        communicationUseCase.updateRelatedName(entity).fold(
                            onSuccess = { /* Updated successfully */ },
                            onFailure = { error ->
                                error.printStackTrace()
                            }
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            
            // 3. INSERT only new nicknames
            val newNicknames = activeNicknames.filter { nickname ->
                !originalIds.contains(nickname.id)
            }
            val newNicknameEntities = newNicknames.map { nickname ->
                RelatedNameEntity(
                    id = nickname.id,
                    contactId = contactId,
                    name = nickname.name,
                    relationship = nickname.relationship.takeIf { it.isNotBlank() } ?: "Nickname",
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            }
            if (newNicknameEntities.isNotEmpty()) {
                insertRelatedNamesBatchUseCase(newNicknameEntities)
            }
        } else {
            // Fallback to INSERT-only if no facade available
            val newNicknames = activeNicknames.filter { nickname ->
                !originalIds.contains(nickname.id)
            }
            val nicknameEntities = newNicknames.map { nickname ->
                RelatedNameEntity(
                    id = nickname.id,
                    contactId = contactId,
                    name = nickname.name,
                    relationship = nickname.relationship.takeIf { it.isNotBlank() } ?: "Nickname",
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            }
            if (nicknameEntities.isNotEmpty()) {
                insertRelatedNamesBatchUseCase(nicknameEntities)
            }
        }
    }
    
    
    // Authentication methods
    fun authenticateUser() {
        val currentState = _uiState.value
        if (!currentState.requiresAuth) {
            // No authentication required
            return
        }
        
        _uiState.update { it.copy(isAuthenticating = true) }
        
        screenModelScope.launch {
            try {
                // Mark as authenticated
                _uiState.update { it.copy(
                    isAuthenticated = true,
                    isAuthenticating = false,
                    requiresAuth = false
                ) }
                
                // Reload full wallet addresses now that user is authenticated
                // Get the unmasked wallet addresses from originalData
                currentState.originalData?.let { originalData ->
                    _walletAddresses.value = originalData.wallets
                }
                
                // Also update other sensitive data if it was hidden
                // In the future, we might hide other fields like emails, phones etc for HIGH security
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isAuthenticating = false,
                    error = "Authentication failed: ${e.message}"
                ) }
            }
        }
    }
    /**
     * Check if an operation requires authentication (for sensitive operations)
     */
    private fun requiresAuthenticationForOperation(): Boolean {
        val currentState = _uiState.value
        return (currentState.securityLevel == SecurityLevel.HIGH ||
                currentState.securityLevel == SecurityLevel.MAXIMUM) &&
                !currentState.isAuthenticated
    }
    
    /**
     * Initialize authentication state when loading a contact
     */
    private fun initializeAuthenticationState() {
        if (!_uiState.value.isEditMode) {
            // For new contacts, no authentication required
            _uiState.update { it.copy(
                isAuthenticated = true,
                requiresAuth = false
            ) }
            return
        }
        
        // For existing contacts, check security level
        val currentState = _uiState.value
        val hasHighSecurity = currentState.securityLevel == SecurityLevel.HIGH ||
                             currentState.securityLevel == SecurityLevel.MAXIMUM
                             
        if (hasHighSecurity) {
            _uiState.update { it.copy(
                isAuthenticated = false,
                requiresAuth = true,
                authRequirement = currentState.authRequirement
            ) }
        } else {
            _uiState.update { it.copy(
                isAuthenticated = true,
                requiresAuth = false
            ) }
        }
    }
    
    override fun onDispose() {
        super.onDispose()
        onNavigateCallback = null
        // Clean up validation manager
        screenModelScope.launch {
            validationManager.cancelAllValidations()
        }
    }
}