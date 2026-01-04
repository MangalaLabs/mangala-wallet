package com.mangala.wallet.features.addressbook.domain.validation

import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants.ANTELOPE_BURN_ADDRESSES
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants.ANTELOPE_PREMIUM_ACCOUNT_LENGTH_1
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants.ANTELOPE_PREMIUM_ACCOUNT_LENGTH_2
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants.ANTELOPE_PREMIUM_ACCOUNT_LENGTH_3
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants.ANTELOPE_SYSTEM_ACCOUNTS
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants.MAX_ADDRESS_LENGTH
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants.MAX_ANTELOPE_ACCOUNT_LENGTH
import com.mangala.wallet.features.addressbook.domain.util.BlockchainSymbolMapper
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants.VAULTA_ACCOUNT_REGEX
import com.mangala.wallet.features.addressbook.domain.validation.WalletValidationResult.*
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.address.IsValidBitcoinAddressUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.features.chains.evmcompatible.core.AddressValidator as EvmAddressValidator
import com.mangala.wallet.model.blockchain.BlockchainType

/**
 * Validation result for wallet addresses
 */
sealed class WalletValidationResult {
    data class Success(val cleanAddress: String) : WalletValidationResult()
    data class Warning(
        val message: String, 
        val cleanAddress: String? = null,
        val type: ValidationWarningType = ValidationWarningType.GENERIC
    ) : WalletValidationResult()
    data class Error(
        val message: String,
        val type: ValidationErrorType = ValidationErrorType.GENERIC
    ) : WalletValidationResult()
}

/**
 * Specific error types for better error handling
 */
enum class ValidationErrorType {
    GENERIC,
    NETWORK_MISMATCH,  // Address belongs to different network (mainnet/testnet)
    INVALID_FORMAT,    // Address format is invalid
    TOO_SHORT,
    TOO_LONG,
    INVALID_CHARACTERS,
    MISSING_PREFIX,
    WRONG_NETWORK_TYPE, // e.g., BTC address on ETH network
    BURN_ADDRESS,      // Critical: burn address
    ACCOUNT_NOT_FOUND  // Antelope-specific: account doesn't exist
}

/**
 * Specific warning types
 */
enum class ValidationWarningType {
    GENERIC,
    EXCHANGE_ADDRESS,
    TESTNET_ADDRESS,
    SYSTEM_ACCOUNT,
    PREMIUM_ACCOUNT,
    EVM_NETWORK_INFO,  // Info about EVM mainnet/testnet same format
    DUPLICATE_ADDRESS
}

/**
 * Validation context to differentiate between adding contact and sending
 */
enum class ValidationContext {
    ADDING_CONTACT,
    EDITING_CONTACT,
    SENDING_TRANSACTION
}

/**
 * Comprehensive wallet address validator
 * Now uses chain-specific validators for better consistency and maintainability
 */
class WalletAddressValidator(
    private val bitcoinValidator: IsValidBitcoinAddressUseCase? = null,
    private val antelopeValidator: ValidateAccountUseCase? = null
) {
    
    companion object {
        private const val MAX_ADDRESS_LENGTH = 150
    }
    
    private val exchangeDetector = ExchangeAddressDetector()
    
    /**
     * Validate a wallet address with comprehensive checks
     */
    fun validateAddress(
        address: String,
        blockchain: String,
        existingAddresses: List<WalletAddressEntity> = emptyList(),
        context: ValidationContext = ValidationContext.ADDING_CONTACT
    ): WalletValidationResult {
        // 1. Basic validation
        val cleanAddress = address.trim()
        if (cleanAddress.isBlank()) {
            return WalletValidationResult.Error("Wallet address is required")
        }
        
        // 1.5. Length validation
        if (cleanAddress.length > MAX_ADDRESS_LENGTH) {
            return WalletValidationResult.Error("Wallet address is too long (max $MAX_ADDRESS_LENGTH characters)")
        }
        
        // 2. Exchange and dangerous address detection
        val detection = exchangeDetector.detectAddress(cleanAddress)
        
        // Handle based on context
        when (context) {
            ValidationContext.ADDING_CONTACT -> {
                if (!detection.allowAsContact) {
                    return Error(detection.message)
                }
                if (detection.severity == Severity.HIGH || detection.severity == Severity.CRITICAL) {
                    // Return warning but allow continuation for exchange addresses
                    if (detection.addressType == AddressType.EXCHANGE_HOT_WALLET || 
                        detection.addressType == AddressType.EXCHANGE_COLD_WALLET) {
                        return Warning(
                            "${detection.name}: ${detection.message}",
                            cleanAddress,
                            ValidationWarningType.EXCHANGE_ADDRESS
                        )
                    }
                }
            }
            ValidationContext.SENDING_TRANSACTION -> {
                if (!detection.allowForSending) {
                    return Error(detection.message)
                }
            }

            ValidationContext.EDITING_CONTACT -> {
                // Same logic as ADDING_CONTACT - when editing, we still want to warn about dangerous addresses
                if (!detection.allowAsContact) {
                    return Error(detection.message)
                }
                if (detection.severity == Severity.HIGH || detection.severity == Severity.CRITICAL) {
                    // Return warning but allow continuation for exchange addresses
                    if (detection.addressType == AddressType.EXCHANGE_HOT_WALLET || 
                        detection.addressType == AddressType.EXCHANGE_COLD_WALLET) {
                        return Warning(
                            "${detection.name}: ${detection.message}",
                            cleanAddress,
                            ValidationWarningType.EXCHANGE_ADDRESS
                        )
                    }
                }
            }
        }
        
        // 3. Format validation based on blockchain - now uses chain-specific validators when available
        // Keep the full blockchain identifier for network compatibility check
        val normalizedBlockchain = BlockchainSymbolMapper.normalizeToSymbol(blockchain)
        
        // Remove _TESTNET suffix for format validation only
        val blockchainForValidation = normalizedBlockchain.replace("_TESTNET", "", ignoreCase = true).uppercase()
        
        val formatResult = when {
            blockchainForValidation in setOf("BTC", "BITCOIN") -> validateBitcoinAddressWithChainValidator(cleanAddress)
            blockchainForValidation in ValidationConstants.EVM_CHAINS || 
                blockchainForValidation in setOf("ETH", "ETHEREUM", "BINANCE-SMART-CHAIN", "POLYGON-POS", "MATIC", "AVALANCHE", "FANTOM", "ARBITRUM-ONE", "OPTIMISTIC-ETHEREUM", "VAULTA_EVM") -> 
                validateEthereumAddressWithChainValidator(cleanAddress)
            blockchainForValidation in setOf("BNB", "BINANCECOIN") -> validateBnbAddress(cleanAddress)
            blockchainForValidation in setOf("SOL", "SOLANA") -> validateSolanaAddress(cleanAddress)
            blockchainForValidation in setOf("TELOS", "WAX", "FIO") -> validateAntelopeAccountWithChainValidator(cleanAddress)
            blockchainForValidation in setOf("EOS", "VAULTA") -> validateVaultaAccount(cleanAddress)
            blockchainForValidation in setOf("TRX", "TRON") -> validateTronAddress(cleanAddress)
            blockchainForValidation in setOf("ADA", "CARDANO") -> validateCardanoAddress(cleanAddress)
            blockchainForValidation in setOf("DOT", "POLKADOT") -> validatePolkadotAddress(cleanAddress)
            blockchainForValidation in setOf("ATOM", "COSMOS") -> validateCosmosAddress(cleanAddress)
            else -> WalletValidationResult.Warning("Unknown blockchain type", cleanAddress)
        }
        
        if (formatResult is WalletValidationResult.Error) {
            return formatResult
        }
        
        // 3.5. Network compatibility validation - NEW!
        val networkCompatibilityResult = validateNetworkCompatibility(cleanAddress, normalizedBlockchain)
        if (networkCompatibilityResult is WalletValidationResult.Error) {
            return networkCompatibilityResult
        }
        
        // 4. Duplicate check
        val duplicate = existingAddresses.find { 
            it.address.equals(cleanAddress, ignoreCase = true) 
        }
        if (duplicate != null) {
            return WalletValidationResult.Warning(
                "Address already exists in your contacts",
                cleanAddress,
                ValidationWarningType.DUPLICATE_ADDRESS
            )
        }
        
        // 5. Additional security checks
        val securityResult = checkAddressSecurity(cleanAddress, blockchain)
        if (securityResult is WalletValidationResult.Error) {
            return securityResult
        }
        
        // 6. Check for testnet addresses - only if formatResult didn't already handle it
        // Bitcoin validation already includes testnet detection, so skip for BTC
        val testnetResult = if (blockchain.uppercase() != "BTC" && formatResult !is WalletValidationResult.Warning) {
            checkTestnetAddress(cleanAddress, blockchain)
        } else {
            WalletValidationResult.Success(cleanAddress)
        }
        
        // Return the final result
        return when {
            securityResult is WalletValidationResult.Warning -> securityResult
            formatResult is WalletValidationResult.Warning -> formatResult
            networkCompatibilityResult is WalletValidationResult.Warning -> networkCompatibilityResult
            testnetResult is WalletValidationResult.Warning -> testnetResult
            else -> WalletValidationResult.Success(cleanAddress)
        }
    }
    
    /**
     * Validate Bitcoin address using chain-specific validator if available
     */
    private fun validateBitcoinAddressWithChainValidator(address: String): WalletValidationResult {
        // Check Bech32 case sensitivity first (BIP-173 requires lowercase)
        if (address.startsWith("bc1", ignoreCase = true) || address.startsWith("tb1", ignoreCase = true)) {
            // Bech32 addresses MUST be lowercase
            if (address != address.lowercase()) {
                return WalletValidationResult.Error("Bech32 addresses must be lowercase")
            }
        }
        
        
        // Use injected Bitcoin validator if available
        if (bitcoinValidator != null) {
            return try {
                // Map to appropriate BlockchainType - you may need to adjust this based on your actual enum
                val blockchainType = BlockchainType.Bitcoin
                if (bitcoinValidator.invoke(blockchainType, address)) {
                    // Valid address - network compatibility will be checked separately
                    WalletValidationResult.Success(address)
                } else {
                    // Chain validator says invalid, use local validation for detailed error
                    validateBitcoinAddress(address)
                }
            } catch (e: Exception) {
                // Fallback to local validation if chain validator fails
                validateBitcoinAddress(address)
            }
        }
        
        // Fallback to local validation
        return validateBitcoinAddress(address)
    }
    
    /**
     * Validate Ethereum address using chain-specific validator if available
     */
    private fun validateEthereumAddressWithChainValidator(address: String): WalletValidationResult {
        return try {
            EvmAddressValidator.validate(address)
            WalletValidationResult.Success(address)
        } catch (e: EvmAddressValidator.AddressValidationException) {
            // EvmAddressValidator only returns "address: <address>" without descriptive error
            // So we use our local validation to get better error messages
            validateEthereumAddress(address)
        } catch (e: Exception) {
            // Fallback to local validation for any other errors
            validateEthereumAddress(address)
        }
    }
    
    /**
     * Validate Antelope account using chain-specific validator if available
     */
    private fun validateAntelopeAccountWithChainValidator(account: String): WalletValidationResult {
        // Always check for consecutive dots first - chain validator might not check this
        if (account.contains("..")) {
            return WalletValidationResult.Error("No double dots (..) in EOS accounts")
        }
        
        // Check for burn addresses
        if (account == "eosio.null" || account == "vaulta.null") {
            return WalletValidationResult.Error(
                "⚠️ WARNING: This is a burn address. Any funds sent here will be permanently destroyed and cannot be recovered!",
                ValidationErrorType.BURN_ADDRESS
            )
        }
        
        // Use injected Antelope validator if available
        if (antelopeValidator != null) {
            return try {
                if (antelopeValidator.validateAccountName(account)) {
                    WalletValidationResult.Success(account)
                } else {
                    WalletValidationResult.Error("Invalid EOS account name. Use only lowercase letters (a-z), numbers (1-5), and dots. Maximum $MAX_ANTELOPE_ACCOUNT_LENGTH characters")
                }
            } catch (e: Exception) {
                // Fallback to local validation if chain validator fails
                validateEosAccount(account)
            }
        }
        
        // Fallback to local validation
        return validateEosAccount(account)
    }
    
    /**
     * Validate Bitcoin address formats - local implementation as fallback
     */
    private fun validateBitcoinAddress(address: String): WalletValidationResult {
        // Define charsets for different address types
        val base58Charset = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        val bech32Charset = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"
        
        return when {
            // Check for uppercase bech32 (common mistake)
            address.startsWith("BC1") || address.startsWith("TB1") -> 
                WalletValidationResult.Error("Use lowercase → ${address.lowercase()}", ValidationErrorType.INVALID_FORMAT)
            
            // Check for Ethereum address pattern (common mistake)
            address.startsWith("0x") && address.length == 42 ->
                WalletValidationResult.Error("Looks like ETH address. Switch network?", ValidationErrorType.WRONG_NETWORK_TYPE)
            
            // Testnet P2PKH - starts with m or n
            address.length in 26..35 && address.matches(Regex("^[mn][$base58Charset]+$")) ->
                WalletValidationResult.Success(address)
            
            // P2PKH (Legacy) - starts with 1, 26-35 characters total
            address.length in 26..35 && address.matches(Regex("^1[$base58Charset]+$")) -> 
                WalletValidationResult.Success(address)
            
            // Testnet P2SH - starts with 2
            address.length in 26..35 && address.matches(Regex("^2[$base58Charset]+$")) ->
                WalletValidationResult.Success(address)
            
            // P2SH - starts with 3, 26-35 characters total
            address.length in 26..35 && address.matches(Regex("^3[$base58Charset]+$")) -> 
                WalletValidationResult.Success(address)
            
            // Testnet Bech32 - starts with tb1
            address.length in 42..62 && address.matches(Regex("^tb1[$bech32Charset]+$")) ->
                WalletValidationResult.Success(address)
            
            // Bech32 (Native SegWit) - starts with bc1, 42-62 characters total
            // Bech32 charset: qpzry9x8gf2tvdw0s3jn54khce6mua7l (excludes 1, b, i, o)
            address.length in 42..62 && address.matches(Regex("^bc1[$bech32Charset]+$")) -> 
                WalletValidationResult.Success(address)
            
            // Bech32m (Taproot) - starts with bc1p, exactly 62 characters
            address.length == 62 && address.matches(Regex("^bc1p[$bech32Charset]+$")) -> 
                WalletValidationResult.Success(address)
            
            // Length checks with helpful messages
            address.length < 26 -> 
                WalletValidationResult.Error("Too short for Bitcoin (needs 26+ characters)", ValidationErrorType.TOO_SHORT)
            
            address.length > 62 -> 
                WalletValidationResult.Error("Too long for Bitcoin (max 62 characters)", ValidationErrorType.TOO_LONG)
            
            // Check for invalid characters based on address type
            (address.startsWith("bc1") || address.startsWith("tb1")) && 
            !address.substring(3).all { it in bech32Charset } -> {
                val invalidChar = address.substring(3).firstOrNull { it !in bech32Charset }
                WalletValidationResult.Error("Bech32 addresses can't contain '${invalidChar}'")
            }
            
            // Check for invalid characters in addresses that start with Bitcoin prefixes
            address.startsWith("1") || address.startsWith("3") || 
            address.startsWith("m") || address.startsWith("n") || address.startsWith("2") -> {
                // First check for invalid characters
                val invalidChar = address.firstOrNull { it !in base58Charset }
                if (invalidChar != null) {
                    WalletValidationResult.Error("Bitcoin addresses can't contain '${invalidChar}'")
                } else {
                    // Valid characters but invalid format/checksum
                    WalletValidationResult.Error("Invalid Bitcoin address format")
                }
            }
            
            // Invalid format but looks like it might be Bech32 Bitcoin
            address.startsWith("bc1") || address.startsWith("tb1") ->
                WalletValidationResult.Error("Invalid Bitcoin address format")
            
            else -> 
                WalletValidationResult.Error("Doesn't look like a Bitcoin address. Check the first characters")
        }
    }
    
    /**
     * Validate Ethereum and EVM-compatible addresses
     */
    private fun validateEthereumAddress(address: String): WalletValidationResult {
        when {
            // Check if it's just "0x" without any hex digits
            address == "0x" ->
                return WalletValidationResult.Error("Incomplete ETH address - missing 40 hex characters after 0x")
            
            // Check if missing 0x prefix
            !address.startsWith("0x") && address.length == 40 && address.matches(Regex("^[a-fA-F0-9]{40}$")) ->
                return WalletValidationResult.Error("Add 0x prefix → 0x$address")
            
            // Check length
            address.length < 42 ->
                return WalletValidationResult.Error("Too short for ETH (needs 42 chars with 0x)")
            
            address.length > 42 ->
                return WalletValidationResult.Error("Too long for ETH (max 42 chars)")
            
            // Check if it's a Bitcoin address entered by mistake
            (address.startsWith("1") || address.startsWith("3") || address.startsWith("bc1")) && address.length in 26..62 ->
                return WalletValidationResult.Error("Looks like BTC address. Switch network?")
            
            // Check format
            !address.matches(Regex("^0x[a-fA-F0-9]{40}$")) ->
                return WalletValidationResult.Error("ETH addresses use only 0-9, a-f after 0x")
        }
        
        val addressPart = address.substring(2) // Remove 0x prefix
        
        // Check if address has mixed case (potential EIP-55 checksum)
        val hasUpper = addressPart.any { it.isUpperCase() }
        val hasLower = addressPart.any { it.isLowerCase() }
        
        if (hasUpper && hasLower) {
            // Mixed case - this is likely an EIP-55 checksummed address
            // TODO: Implement full EIP-55 checksum validation
            // For now, accept it as valid since it's a deliberate checksum format
            return WalletValidationResult.Success(address)
        }
        
        // All lowercase or all uppercase - both are valid formats
        return WalletValidationResult.Success(address)
    }
    
    /**
     * Validate Solana addresses
     */
    private fun validateSolanaAddress(address: String): WalletValidationResult {
        // Solana addresses are base58 encoded, 32-44 characters
        val base58Regex = Regex("^[1-9A-HJ-NP-Za-km-z]{32,44}$")
        
        return if (address.matches(base58Regex)) {
            WalletValidationResult.Success(address)
        } else {
            WalletValidationResult.Error("Invalid SOL address (32-44 chars, no special chars)")
        }
    }
    
    /**
     * Validate VAULTA accounts with specific rules and messages
     */
    private fun validateVaultaAccount(account: String): WalletValidationResult {
        // VAULTA uses same format as EOS but with specific messaging
        
        // Check for empty or blank input
        if (account.isBlank()) {
            return WalletValidationResult.Error("VAULTA account cannot be empty")
        }
        
        // Check if starts with dot
        if (account.startsWith(".")) {
            return WalletValidationResult.Error("VAULTA accounts cannot start with a dot (.)")
        }
        
        // Check special cases
        if (account in ValidationConstants.ANTELOPE_BURN_ADDRESSES) {
            return WalletValidationResult.Error(
                "⚠️ WARNING: This is a burn address. Any funds sent here will be permanently destroyed!",
                ValidationErrorType.BURN_ADDRESS
            )
        }
        
        if (account in ValidationConstants.ANTELOPE_SYSTEM_ACCOUNTS) {
            return WalletValidationResult.Warning(
                "⚠️ This is a system account. Please verify you intend to send to this address.",
                account,
                ValidationWarningType.SYSTEM_ACCOUNT
            )
        }
        
        // Check length first for better error message
        if (account.length > MAX_ANTELOPE_ACCOUNT_LENGTH) {
            return WalletValidationResult.Error("VAULTA accounts cannot be longer than $MAX_ANTELOPE_ACCOUNT_LENGTH characters (you have ${account.length})")
        }
        
        // Check structural issues
        if (account.endsWith(".")) {
            return WalletValidationResult.Error("VAULTA accounts cannot end with a dot (.)")  
        }
        
        if (account.contains("..")) {
            return WalletValidationResult.Error("VAULTA accounts cannot have consecutive dots (..)")
        }
        
        // Check allowed characters
        val vaultaRegex = Regex("^[a-z1-5.]+$")
        if (!account.matches(vaultaRegex)) {
            return WalletValidationResult.Error("VAULTA accounts: use only lowercase a-z, numbers 1-5, and dots")
        }
        
        // Additional checks for premium/short accounts
        when (account.length) {
            1 -> return WalletValidationResult.Warning(
                "⚠️ Single character accounts are premium and extremely rare. Verify this account exists.",
                account,
                ValidationWarningType.PREMIUM_ACCOUNT
            )
            2 -> return WalletValidationResult.Warning(
                "⚠️ Two character accounts are premium and rare. Verify this account exists.",
                account,
                ValidationWarningType.PREMIUM_ACCOUNT
            )
            3 -> return WalletValidationResult.Warning(
                "⚠️ Three character accounts may require special creation process. Verify before sending.",
                account,
                ValidationWarningType.PREMIUM_ACCOUNT
            )
        }
        
        return WalletValidationResult.Success(account)
    }
    
    /**
     * Validate EOS account names
     */
    private fun validateEosAccount(account: String): WalletValidationResult {
        // EOS account rules:
        // - 1-$MAX_ANTELOPE_ACCOUNT_LENGTH characters
        // - Only lowercase a-z, numbers 1-5, and dots
        // - Cannot end with a dot
        // - Cannot have consecutive dots
        
        // Check special cases first
        if (account == "eosio.null") {
            return WalletValidationResult.Warning("⚠️ WARNING: This is the EOS burn address. Any funds sent here will be permanently destroyed and cannot be recovered!", account)
        }
        
        // Check structural issues before regex
        if (account.endsWith(".")) {
            return WalletValidationResult.Error("EOS accounts can't end with dot (.)")  
        }
        
        if (account.contains("..")) {
            return WalletValidationResult.Error("No double dots (..) in EOS accounts")
        }
        
        // Now check allowed characters
        val eosRegex = Regex("^[a-z1-5.]{1,$MAX_ANTELOPE_ACCOUNT_LENGTH}$")
        if (!account.matches(eosRegex)) {
            return WalletValidationResult.Error("EOS: only a-z, 1-5, dots (max $MAX_ANTELOPE_ACCOUNT_LENGTH chars)")
        }
        
        return WalletValidationResult.Success(account)
    }
    
    /**
     * Validate BNB addresses (both native BNB Chain and BSC formats)
     */
    private fun validateBnbAddress(address: String): WalletValidationResult {
        return when {
            // BSC format (EVM-compatible, same as Ethereum)
            address.matches(Regex("^0x[a-fA-F0-9]{40}$")) -> {
                // Use Ethereum validation for BSC addresses
                validateEthereumAddress(address)
            }
            // Native BNB Chain format (Bech32, starts with bnb1)
            address.matches(Regex("^bnb1[a-z0-9]{38}$")) -> {
                WalletValidationResult.Success(address)
            }
            // Invalid format
            else -> {
                WalletValidationResult.Error("BNB addresses start with 'bnb1' or '0x'")
            }
        }
    }
    
    /**
     * Validate Tron addresses
     */
    private fun validateTronAddress(address: String): WalletValidationResult {
        // Tron addresses start with T and are 34 characters
        val tronRegex = Regex("^T[1-9A-HJ-NP-Za-km-z]{33}$")
        
        return if (address.matches(tronRegex)) {
            WalletValidationResult.Success(address)
        } else {
            WalletValidationResult.Error("TRX addresses start with 'T' (34 chars total)")
        }
    }
    
    /**
     * Validate Cardano addresses
     */
    private fun validateCardanoAddress(address: String): WalletValidationResult {
        // Cardano addresses can be quite long and start with addr1
        return when {
            address.startsWith("addr1") && address.length > 50 -> 
                WalletValidationResult.Success(address)
            address.startsWith("stake1") -> 
                WalletValidationResult.Warning("⚠️ Staking address - not for payments", address)
            else -> 
                WalletValidationResult.Error("ADA addresses start with 'addr1'")
        }
    }
    
    /**
     * Validate Polkadot addresses
     */
    private fun validatePolkadotAddress(address: String): WalletValidationResult {
        // Polkadot addresses start with 1 and are 47-48 characters
        val polkadotRegex = Regex("^1[1-9A-HJ-NP-Za-km-z]{46,47}$")
        
        return if (address.matches(polkadotRegex)) {
            WalletValidationResult.Success(address)
        } else {
            WalletValidationResult.Error("DOT addresses start with '1' (47-48 chars)")
        }
    }
    
    /**
     * Validate Cosmos addresses
     */
    private fun validateCosmosAddress(address: String): WalletValidationResult {
        // Cosmos addresses start with cosmos1 and are 45 characters
        val cosmosRegex = Regex("^cosmos1[a-z0-9]{38}$")
        
        return if (address.matches(cosmosRegex)) {
            WalletValidationResult.Success(address)
        } else {
            WalletValidationResult.Error("ATOM addresses start with 'cosmos1'")
        }
    }
    
    /**
     * Security checks for known malicious or burn addresses
     */
    private fun checkAddressSecurity(address: String, blockchain: String): WalletValidationResult {
        // Check burn addresses
        val burnAddresses = mapOf(
            "ETH" to listOf(
                "0x0000000000000000000000000000000000000000",
                "0x000000000000000000000000000000000000dEaD"
            ),
            "ETHEREUM" to listOf(
                "0x0000000000000000000000000000000000000000",
                "0x000000000000000000000000000000000000dEaD"
            ),
            "BTC" to listOf(
                "1111111111111111111114oLvT2",
                "1BitcoinEaterAddressDontSendf59kuE"
            ),
            "BITCOIN" to listOf(
                "1111111111111111111114oLvT2",
                "1BitcoinEaterAddressDontSendf59kuE"
            ),
            "BNB" to listOf(
                "0x0000000000000000000000000000000000000000",
                "0x000000000000000000000000000000000000dEaD",
                "bnb1hn8ym9xht925jkncjpf7lhjnax6z8nv24fv2yq" // BNB Chain burn address
            ),
            "BSC" to listOf(
                "0x0000000000000000000000000000000000000000",
                "0x000000000000000000000000000000000000dEaD"
            ),
            "BINANCE-SMART-CHAIN" to listOf(
                "0x0000000000000000000000000000000000000000",
                "0x000000000000000000000000000000000000dEaD"
            ),
            "EOS" to listOf("eosio.null"),
            "SOL" to listOf("11111111111111111111111111111111"),
            "SOLANA" to listOf("11111111111111111111111111111111")
        )
        
        val burns = burnAddresses[blockchain.uppercase()] ?: burnAddresses[blockchain] ?: emptyList()
        if (burns.any { it.equals(address, ignoreCase = true) }) {
            return WalletValidationResult.Warning("This is a burn address. Funds sent here cannot be recovered!", address)
        }
        
        // Check for all zeros pattern (common mistake)
        if (address.matches(Regex("^0+$")) || address.matches(Regex("^0x0+$"))) {
            return WalletValidationResult.Error("Invalid - all zeros")
        }
        
        return WalletValidationResult.Success(address)
    }
    
    /**
     * Check for testnet addresses
     */
    private fun checkTestnetAddress(address: String, blockchain: String): WalletValidationResult {
        val testnetPatterns = when (blockchain.uppercase()) {
            "BTC", "BITCOIN" -> {
                // Bitcoin testnet addresses
                when {
                    address.startsWith("m") || address.startsWith("n") -> true // P2PKH testnet
                    address.startsWith("2") -> true // P2SH testnet
                    address.startsWith("tb1") -> true // Bech32 testnet
                    else -> false
                }
            }
            "ETH", "ETHEREUM", "BSC", "BINANCE-SMART-CHAIN", "POLYGON", "POLYGON-POS", "MATIC", "AVAX", "AVALANCHE" -> {
                // Common testnet addresses (not definitive, but common patterns)
                val knownTestnetAddresses = listOf(
                    "0x0000000000000000000000000000000000001000", // Common testnet faucet
                    "0x0000000000000000000000000000000000001001"  // Common testnet contract
                )
                knownTestnetAddresses.any { it.equals(address, ignoreCase = true) }
            }
            else -> false
        }
        
        return if (testnetPatterns) {
            WalletValidationResult.Warning("⚠️ Testnet address - verify network", address)
        } else {
            WalletValidationResult.Success(address)
        }
    }
    
    /**
     * Check if string has uppercase characters
     */
    private fun hasUpperCase(str: String): Boolean {
        return str.any { it.isUpperCase() }
    }
    
    /**
     * Clean common copy-paste issues while preserving address integrity
     * CRITICAL: Only removes truly invisible characters, never modifies visible content
     */
    fun cleanAddress(input: String): String {
        var cleaned = input.trim()
            .replace("\n", "")
            .replace("\r", "")
            .replace("\t", "")
        
        // Only remove truly invisible characters - NEVER modify visible content
        val invisibleChars = listOf(
            "\u200B", // Zero-width space
            "\u200C", // Zero-width non-joiner
            "\u200D", // Zero-width joiner
            "\uFEFF", // Zero-width no-break space
            "\u2060", // Word joiner
            "\u202A", // Left-to-Right embedding
            "\u202B", // Right-to-Left embedding
            "\u202C", // Pop directional formatting
            "\u200E", // Left-to-Right mark
            "\u200F"  // Right-to-Left mark
        )
        
        invisibleChars.forEach { char ->
            cleaned = cleaned.replace(char, "")
        }
        
        // Only remove wrapping quotes if they wrap the ENTIRE address
        if (cleaned.length >= 3) {
            val wrappingPairs = listOf(
                Pair("'", "'"),
                Pair("\"", "\""),
                Pair("`", "`")
            )
            
            for ((start, end) in wrappingPairs) {
                if (cleaned.startsWith(start) && cleaned.endsWith(end)) {
                    cleaned = cleaned.substring(1, cleaned.length - 1)
                    break // Only remove one set of quotes
                }
            }
        }
        
        // Critical: Do NOT remove internal spaces or special characters
        // Let validation handle those cases
        return cleaned
    }
    
    /**
     * Extract address from protocol URLs (bitcoin:, ethereum:, etc)
     */
    fun extractAddressFromUrl(input: String): String {
        val protocolRegex = Regex("^(bitcoin:|ethereum:|solana:)([^?]+)")
        val match = protocolRegex.find(input)
        return match?.groupValues?.get(2) ?: input
    }
    
    /**
     * Validate that the address is compatible with the selected network
     * This prevents mainnet/testnet address mismatches
     */
    private fun validateNetworkCompatibility(address: String, blockchain: String): WalletValidationResult {
        val symbol = BlockchainSymbolMapper.normalizeToSymbol(blockchain)
        
        // Bitcoin network validation
        if (symbol.startsWith("BTC")) {
            val isTestnetAddress = when {
                address.startsWith("m") || address.startsWith("n") -> true // P2PKH testnet
                address.startsWith("2") && address.length in 26..35 -> true // P2SH testnet
                address.startsWith("tb1") -> true // Bech32 testnet
                else -> false
            }
            
            val isMainnetAddress = when {
                address.startsWith("1") && address.length in 26..35 -> true // P2PKH mainnet
                address.startsWith("3") && address.length in 26..35 -> true // P2SH mainnet
                address.startsWith("bc1") -> true // Bech32 mainnet
                else -> false
            }
            
            return when {
                symbol == "BTC" && isTestnetAddress -> 
                    WalletValidationResult.Error(
                        "This is a testnet address. Please select BTC_TESTNET or enter a mainnet address",
                        ValidationErrorType.NETWORK_MISMATCH
                    )
                symbol == "BTC_TESTNET" && isMainnetAddress -> 
                    WalletValidationResult.Error(
                        "This is a mainnet address. Please select BTC or enter a testnet address",
                        ValidationErrorType.NETWORK_MISMATCH
                    )
                else -> WalletValidationResult.Success(address)
            }
        }
        
        // EVM networks - addresses are the same format for mainnet/testnet
        if (symbol in listOf("ETH", "ETH_GOERLI", "ETH_SEPOLIA", "BSC", "BSC_TESTNET", 
                            "POLYGON", "POLYGON_MUMBAI", "AVAX", "AVAX_FUJI")) {
            if (symbol.contains("TESTNET") || symbol.contains("GOERLI") || 
                symbol.contains("SEPOLIA") || symbol.contains("MUMBAI") || symbol.contains("FUJI")) {
                // Info message for testnet selection
                return WalletValidationResult.Warning(
                    "ℹ️ EVM addresses are the same for mainnet and testnet. Ensure you've selected the correct network",
                    address,
                    ValidationWarningType.EVM_NETWORK_INFO
                )
            }
        }
        
        return WalletValidationResult.Success(address)
    }
}