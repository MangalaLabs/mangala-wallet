package com.mangala.wallet.features.addressbook.domain.validation

import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.domain.usecase.CheckAccountWithErrorHandlingUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AccountCheckResult
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsByQueryUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.features.addressbook.domain.util.BlockchainSymbolMapper
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Input type detection
 */
enum class InputType {
    WalletAddress,
    DomainName,
    EosAccount,
    Unknown
}

/**
 * Comprehensive validator that combines address validation and domain resolution
 */
class ComprehensiveAddressValidator(
    private val walletValidator: WalletAddressValidator = WalletAddressValidator(),
    private val domainResolver: DomainNameResolver = DomainNameResolver(),
    getAccountsByQueryUseCase: GetAccountsByQueryUseCase? = null,
    private val logger: ValidationLogger = NoOpValidationLogger,
    private val validationCache: ValidationCache = ValidationCache()
) {
    // Use internal enhanced error handling for addressbook module
    private val checkAccountUseCase = getAccountsByQueryUseCase?.let {
        CheckAccountWithErrorHandlingUseCase(it, logger)
    }
    
    // State for UI feedback
    private val _validationState = MutableStateFlow<ValidationState>(ValidationState.Idle)
    val validationState: StateFlow<ValidationState> = _validationState
    
    /**
     * Auto-detect blockchain type for a given address
     * Returns list of possible blockchain types that match the address format
     */
    fun autoDetectBlockchainType(address: String): List<String> {
        val trimmed = address.trim()
        val possibleBlockchains = mutableListOf<String>()
        
        // Bitcoin format detection
        when {
            // Testnet addresses
            trimmed.matches(Regex("^[mn][a-km-zA-HJ-NP-Z1-9]{25,34}$")) -> {
                possibleBlockchains.add("BTC_TESTNET")
            }
            trimmed.startsWith("2") && trimmed.length in 26..35 -> {
                possibleBlockchains.add("BTC_TESTNET")
            }
            trimmed.startsWith("tb1") && trimmed.length in 42..62 -> {
                possibleBlockchains.add("BTC_TESTNET")
            }
            // Mainnet addresses
            trimmed.startsWith("1") && trimmed.length in 26..35 -> {
                possibleBlockchains.add("BTC")
            }
            trimmed.startsWith("3") && trimmed.length in 26..35 -> {
                possibleBlockchains.add("BTC")
            }
            trimmed.startsWith("bc1") && trimmed.length in 42..62 -> {
                possibleBlockchains.add("BTC")
            }
        }
        
        // EVM format detection (0x + 40 hex chars)
        if (trimmed.matches(Regex("^0x[a-fA-F0-9]{40}$"))) {
            // Add all EVM-compatible chains
            possibleBlockchains.addAll(listOf(
                "ETH", "ETH_GOERLI", "ETH_SEPOLIA",
                "BSC", "BSC_TESTNET",
                "POLYGON", "POLYGON_MUMBAI",
                "AVAX", "AVAX_FUJI",
                "FTM", "FTM_TESTNET",
                "ARB", "ARB_SEPOLIA",
                "OP", "OP_SEPOLIA"
            ))
        }
        
        // Antelope format detection (1-12 chars, a-z, 1-5, dots)
        if (trimmed.matches(Regex("^[a-z1-5.]{1,12}$")) && !trimmed.startsWith(".") && !trimmed.endsWith(".")) {
            possibleBlockchains.addAll(listOf(
                "VAULTA", "VAULTA_TESTNET",
                "TELOS", "TELOS_TESTNET", 
                "WAX", "WAX_TESTNET",
                "FIO", "FIO_TESTNET"
            ))
        }
        
        // Solana format detection (base58, 32-44 chars)
        if (trimmed.matches(Regex("^[1-9A-HJ-NP-Za-km-z]{32,44}$"))) {
            possibleBlockchains.addAll(listOf("SOL", "SOL_DEVNET"))
        }
        
        return possibleBlockchains
    }
    
    /**
     * Validate address or domain with full feature set
     */
    suspend fun validateAddressOrDomain(
        input: String,
        selectedBlockchain: String,
        existingAddresses: List<WalletAddressEntity> = emptyList(),
        context: ValidationContext = ValidationContext.ADDING_CONTACT,
        blockchainEntity: BlockchainTypeEntity? = null
    ): WalletValidationResult {
        logger.debug("Validating address: $input for blockchain: $selectedBlockchain")
        
        val trimmed = input.trim()
        
        // Check cache first for non-empty addresses
        if (trimmed.isNotBlank()) {
            val cacheKey = validationCache.generateKey(trimmed, selectedBlockchain, context)
            validationCache.get(cacheKey)?.let { cachedResult ->
                logger.debug("Using cached validation result for: $trimmed")
                _validationState.value = when (cachedResult) {
                    is WalletValidationResult.Success -> ValidationState.Valid(cachedResult.cleanAddress)
                    is WalletValidationResult.Warning -> ValidationState.Warning(cachedResult.message, cachedResult.cleanAddress)
                    is WalletValidationResult.Error -> ValidationState.Invalid(cachedResult.message)
                }
                return cachedResult
            }
        }
        
        // Update state to validating
        _validationState.value = ValidationState.Validating
        
        try {
            // 1. Clean common paste issues
            val cleaned = walletValidator.cleanAddress(trimmed)
            logger.debug("Cleaned address: $cleaned")
            
            // 2. Extract from protocol URL if needed
            val extracted = walletValidator.extractAddressFromUrl(cleaned)
            logger.debug("Extracted address: $extracted")
            
            // 3. Detect input type
            val inputType = detectInputType(extracted, selectedBlockchain)
            logger.debug("Detected input type: $inputType")
            
            // 4. Handle based on type
            val result = when (inputType) {
                InputType.DomainName -> handleDomainInput(extracted, selectedBlockchain, existingAddresses, context)
                InputType.EosAccount -> handleEosInput(extracted, selectedBlockchain, existingAddresses, context, blockchainEntity)
                InputType.WalletAddress -> {
                    // Check if it's an Antelope account that looks like a wallet address
                    if (isAntelopeChain(selectedBlockchain) && extracted.length <= 12) {
                        // Treating as EOS account
                        handleEosInput(extracted, selectedBlockchain, existingAddresses, context, blockchainEntity)
                    } else {
                        // Using standard wallet validation
                        walletValidator.validateAddress(extracted, selectedBlockchain, existingAddresses, context)
                    }
                }
                InputType.Unknown -> WalletValidationResult.Error("Unable to determine input type")
            }
            
            // Update state with result
            _validationState.value = when (result) {
                is WalletValidationResult.Success -> ValidationState.Valid(result.cleanAddress)
                is WalletValidationResult.Warning -> ValidationState.Warning(result.message, result.cleanAddress)
                is WalletValidationResult.Error -> ValidationState.Invalid(result.message)
            }
            
            logger.debug("Validation result: $result")
            
            // Cache the result for future use
            if (trimmed.isNotBlank()) {
                val cacheKey = validationCache.generateKey(trimmed, selectedBlockchain, context)
                validationCache.put(cacheKey, result)
            }
            
            return result
        } catch (e: Exception) {
            logger.error("Validation failed", e)
            val error = WalletValidationResult.Error("Validation failed: ${e.message}")
            _validationState.value = ValidationState.Invalid(error.message)
            // Validation ended with error
            return error
        }
    }
    
    /**
     * Detect the type of input
     */
    private fun detectInputType(input: String, selectedBlockchain: String): InputType {
        // Check if it's a Bitcoin address first (to avoid confusion with domains)
        if (input.matches(Regex("^[13][a-km-zA-HJ-NP-Z1-9]{25,34}$")) ||
            input.matches(Regex("^bc1[a-z0-9]{39,59}$"))) {
            return InputType.WalletAddress
        }
        
        // Check if it's an Ethereum address (0x followed by 40 hex chars)
        if (input.matches(Regex("^0x[a-fA-F0-9]{40}$"))) {
            return InputType.WalletAddress
        }
        
        // Check for Antelope accounts FIRST (before domain check)
        // This prevents valid Antelope accounts like "user.gm" from being treated as domains
        if (isAntelopeChain(selectedBlockchain)) {
            // Antelope accounts can have dots and must be 1-12 chars with only a-z, 1-5, and dots
            if (input.matches(Regex("^[a-z1-5.]{1,12}$"))) {
                return InputType.EosAccount
            }
        }
        
        // Check if it's a possible domain (after Antelope check)
        if (domainResolver.isPossibleDomain(input)) {
            return InputType.DomainName
        }
        
        // Default to wallet address
        return InputType.WalletAddress
    }
    
    /**
     * Handle domain name input
     */
    private suspend fun handleDomainInput(
        input: String,
        selectedBlockchain: String,
        existingAddresses: List<WalletAddressEntity>,
        context: ValidationContext
    ): WalletValidationResult {
        // Check for homograph attacks
        if (domainResolver.detectHomographAttack(input)) {
            return WalletValidationResult.Error("Domain contains suspicious characters")
        }
        
        // Update state to show resolving
        _validationState.value = ValidationState.Resolving
        
        // Resolve domain
        return when (val resolved = domainResolver.resolveAddress(input)) {
            is ResolveResult.Success -> {
                // Check blockchain compatibility
                if (resolved.blockchain != selectedBlockchain && 
                    !isBlockchainCompatible(resolved.blockchain, selectedBlockchain)) {
                    WalletValidationResult.Warning(
                        "${resolved.domainType} domain resolved to ${resolved.blockchain} address. " +
                        "Please verify it's valid for $selectedBlockchain",
                        resolved.resolvedAddress
                    )
                } else {
                    // Validate the resolved address
                    walletValidator.validateAddress(
                        resolved.resolvedAddress,
                        selectedBlockchain,
                        existingAddresses,
                        context
                    )
                }
            }
            is ResolveResult.NotFound -> {
                // Suggest corrections
                val suggestions = domainResolver.suggestCorrections(input)
                if (suggestions.isNotEmpty()) {
                    WalletValidationResult.Error("Domain not found. Did you mean: ${suggestions.first()}?")
                } else {
                    WalletValidationResult.Error("Domain not found")
                }
            }
            is ResolveResult.Timeout -> {
                WalletValidationResult.Error("Domain resolution timed out. Please try again.")
            }
            is ResolveResult.UnsupportedDomain -> {
                WalletValidationResult.Error("Domain extension .${resolved.extension} is not supported")
            }
            is ResolveResult.ResolutionFailed -> {
                WalletValidationResult.Error("Failed to resolve domain: ${resolved.error}")
            }
            else -> WalletValidationResult.Error("Invalid domain format")
        }
    }
    
    /**
     * Handle EOS account input
     */
    private suspend fun handleEosInput(
        input: String,
        selectedBlockchain: String,
        existingAddresses: List<WalletAddressEntity>,
        context: ValidationContext,
        blockchainEntity: BlockchainTypeEntity? = null
    ): WalletValidationResult {
        // Disambiguate if it contains a dot and selectedBlockchain is not an Antelope chain
        if (input.contains(".") && !isAntelopeChain(selectedBlockchain)) {
            // Check if it's a known domain extension
            val parts = input.split(".")
            if (parts.size == 2 && parts[1] in domainResolver.supportedExtensions) {
                // It's probably a domain, not an Antelope account
                return WalletValidationResult.Error(
                    "Ambiguous input: '$input' looks like a ${parts[1]} domain. " +
                    "Select an Antelope blockchain (EOS, VAULTA, etc.) if this is an account name."
                )
            }
        }
        
        // First validate format
        logger.debug("Validating EOS format for: $input")
        val formatValidation = walletValidator.validateAddress(input, selectedBlockchain, existingAddresses, context)
        // Format validation completed
        
        if (formatValidation is WalletValidationResult.Error) {
            // Format validation failed
            return formatValidation
        }
        
        // Check if blockchain is testnet - if yes, skip account existence check
        // Also check if blockchain identifier contains _TESTNET suffix
        val isTestnet = blockchainEntity?.networkType == BlockchainTypeEntity.NETWORK_TESTNET || 
                       selectedBlockchain.endsWith("_TESTNET", ignoreCase = true)
        
        
        // Check if it's a known system account - skip API check for these
        if (isAntelopeChain(selectedBlockchain) && input in ValidationConstants.ANTELOPE_SYSTEM_ACCOUNTS) {
            logger.debug("Recognized system account: $input")
            return WalletValidationResult.Warning(
                "⚠️ This is a system account. Please verify you intend to send to this address.",
                input
            )
        }
        
        // Check account existence for Antelope chains if use case is available
        // Skip API call for known system accounts (already handled above)
        if (checkAccountUseCase != null && isAntelopeChain(selectedBlockchain)) {
            logger.debug("Checking account existence for: $input on $selectedBlockchain")
            logger.debug("Will check account existence (checkAccountUseCase != null, isAntelopeChain=true)")
            
            try {
                val blockchainType = mapToBlockchainType(selectedBlockchain)
                logger.debug("Mapped blockchain type: $blockchainType")
                // Mapped BlockchainType
                
                // Log API endpoint selection (inferred from blockchain type)
                if (blockchainType != null) {
                    val endpoint = when (blockchainType) {
                        BlockchainType.EosJungleTestnet -> "https://jungle4.greymass.com/"
                        BlockchainType.Eos -> "https://eos.greymass.com/"
                        else -> "unknown"
                    }
                }
                
                if (blockchainType != null) {
                    // Calling internal use case with enhanced error handling
                    logger.debug("About to call checkAccountUseCase.invoke with blockchainType=$blockchainType, input=$input")
                    when (val result = checkAccountUseCase.invoke(blockchainType, input, timeoutMillis = 10000L)) {
                        is AccountCheckResult.NotExists -> {
                            logger.info("API returned: Account does not exist: $input on blockchain: $blockchainType")
                            val symbol = BlockchainSymbolMapper.normalizeToSymbol(selectedBlockchain)
                            logger.debug("Mapped symbol: $symbol from selectedBlockchain: $selectedBlockchain")
                            
                            // Both testnet and mainnet show error for non-existent accounts
                            val errorMessage = if (isTestnet) {
                                // For testnet: Show testnet-specific error message
                                when (symbol) {
                                    "VAULTA", "VAULTA_TESTNET" -> "VAULTA testnet account '$input' does not exist. Please verify the account name or create it first on VAULTA testnet."
                                    "TELOS", "TELOS_TESTNET" -> "TELOS testnet account '$input' does not exist. Please verify the account name or create it first on testnet."
                                    "WAX", "WAX_TESTNET" -> "WAX testnet account '$input' does not exist. Please verify the account name or create it first on testnet."
                                    "FIO", "FIO_TESTNET" -> "FIO testnet account '$input' does not exist. Please verify the account name or create it first on testnet."
                                    else -> "Testnet account '$input' does not exist on $symbol"
                                }
                            } else {
                                // For mainnet: Show error as before
                                when (symbol) {
                                    "VAULTA" -> "VAULTA account '$input' does not exist. Please verify the account name or create it first on VAULTA network."
                                    "TELOS" -> "TELOS account '$input' does not exist. Please verify the account name or create it first."
                                    "WAX" -> "WAX account '$input' does not exist. Please verify the account name or create it first."
                                    "FIO" -> "FIO account '$input' does not exist. Please verify the account name or create it first."
                                    else -> "Account '$input' does not exist on $symbol"
                                }
                            }
                            return WalletValidationResult.Error(errorMessage)
                        }
                        is AccountCheckResult.Exists -> {
                            logger.info("API returned: Account EXISTS: $input on blockchain: $blockchainType")
                        }
                        is AccountCheckResult.NetworkError -> {
                            // Network error - return warning to allow user to continue
                            return WalletValidationResult.Warning(
                                "⚠️ Network error: Could not verify account existence. Please ensure the account exists before sending funds.",
                                input
                            )
                        }
                        is AccountCheckResult.Timeout -> {
                            // Timeout - return warning to allow user to continue
                            return WalletValidationResult.Warning(
                                "⚠️ Request timed out: Could not verify account existence. Please ensure the account exists before sending funds.",
                                input
                            )
                        }
                        is AccountCheckResult.Error -> {
                            // Other error - return warning with details
                            return WalletValidationResult.Warning(
                                "⚠️ Could not verify account: ${result.message}. Please ensure the account exists before sending funds.",
                                input
                            )
                        }
                    }
                } else {
                    logger.warning("Could not map blockchain type: $selectedBlockchain")
                }
            } catch (e: Exception) {
                // Exception during account check: ${e.message}
                // If account check fails, return warning but allow to continue
                return WalletValidationResult.Warning(
                    "Could not verify account existence. Please ensure the account exists before sending funds.",
                    input
                )
            }
        }
        
        return formatValidation
    }
    
    /**
     * Check if blockchain is Antelope-based
     */
    private fun isAntelopeChain(blockchain: String): Boolean {
        val symbol = BlockchainSymbolMapper.normalizeToSymbol(blockchain)
        return symbol in setOf("VAULTA", "VAULTA_TESTNET", "TELOS", "TELOS_TESTNET", 
                               "WAX", "WAX_TESTNET", "FIO", "FIO_TESTNET")
    }
    
    /**
     * Map blockchain string to BlockchainType
     */
    private fun mapToBlockchainType(blockchain: String): BlockchainType? {
        // Direct mapping for known testnet identifiers
        if (blockchain == "eos-jungle-testnet") {
            return BlockchainType.EosJungleTestnet
        }
        
        // Check if it's a testnet blockchain first
        val isTestnet = blockchain.contains("_TESTNET", ignoreCase = true) || 
                       blockchain.contains("-testnet", ignoreCase = true)
        
        // Remove _TESTNET suffix if present
        val cleanedBlockchain = blockchain.replace("_TESTNET", "", ignoreCase = true)
            .replace("-testnet", "", ignoreCase = true)
            .uppercase()
        
        val result = when (cleanedBlockchain) {
            "EOS", "VAULTA", "EOS-JUNGLE" -> {
                if (isTestnet) {
                    BlockchainType.EosJungleTestnet
                } else {
                    BlockchainType.Eos
                }
            }
            // For now, treat all Antelope chains as EOS for account checking
            // since they use the same account system
            "TELOS", "WAX", "FIO" -> {
                // Note: Currently only EOS has testnet support in BlockchainType
                // Other Antelope testnets will use mainnet API (which may not work correctly)
                if (isTestnet) {
                    BlockchainType.EosJungleTestnet
                } else {
                    BlockchainType.Eos
                }
            }
            else -> null
        }
        return result
    }
    
    /**
     * Check if blockchains are compatible (e.g., ETH address can be used on BSC)
     */
    private fun isBlockchainCompatible(source: String, target: String): Boolean {
        val evmChains = setOf("ETH", "BSC", "POLYGON", "AVAX", "FTM", "ARB", "OP")
        
        return when {
            source == target -> true
            source in evmChains && target in evmChains -> true
            else -> false
        }
    }
    
    /**
     * Reset validation state
     */
    fun resetState() {
        _validationState.value = ValidationState.Idle
    }
}

/**
 * Validation state for UI feedback
 */
sealed class ValidationState {
    object Idle : ValidationState()
    object Validating : ValidationState()
    object Resolving : ValidationState()
    data class Valid(val address: String) : ValidationState()
    data class Warning(val message: String, val address: String?) : ValidationState()
    data class Invalid(val message: String) : ValidationState()
}