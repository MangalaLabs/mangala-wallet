package com.mangala.wallet.features.addressbook.presentation.privacy

import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.setting.UserSettingEntity

/**
 * Address Obfuscation Utility
 *
 * Provides consistent address obfuscation logic across the application,
 * aligned with existing DisplayMode system (FULL, HIDDEN, SECRET).
 *
 * Features:
 * - FULL mode: EOS shows full address, other chains truncate to 6 prefix + 4 suffix
 * - HIDDEN mode: EOS with subdomain shows ••••subdomain, others show ••••4suffix
 * - SECRET mode: Always shows ••••••••
 * - Sensitive flag: Overrides all modes and shows ••••••••
 * - Performance optimized with caching
 */
object AddressObfuscator {
    fun shouldObfuscate(
        contact: ContactEntity,
        walletAddressIsSensitive: Boolean,
        privacyModeEnabled: Boolean,
        userSettings: UserSettingEntity? = null
    ): Boolean {
        // If privacy mode is disabled globally, never obfuscate
        if (!privacyModeEnabled) return false
        
        // Priority 1: If specific wallet address is marked as sensitive, obfuscate
        if (walletAddressIsSensitive) return true
        
        // Priority 2: If contact is marked as sensitive, always obfuscate when privacy mode is on
        if (contact.isSensitive == true) return true
        
        // Priority 3: Check contact's privacy display mode
        return when (contact.privacyDisplayMode) {
            DisplayMode.FULL -> true      // DEFAULT: Obfuscate when privacy mode is enabled
            DisplayMode.HIDDEN -> true    // Always obfuscate for HIDDEN mode
            DisplayMode.SECRET -> true    // Always obfuscate for SECRET mode
        }
    }
    // Cached obfuscated addresses to avoid repeated computation
    private val obfuscationCache = mutableMapOf<String, String>()

    /**
     * Obfuscate a wallet address based on display mode and privacy settings
     *
     * Priority:
     * 1. If isSensitive = true: Always show "••••••••" (SECRET mode)
     * 2. If privacyModeEnabled = false: Return full address
     * 3. Apply obfuscation based on displayMode:
     *    - FULL: EOS = full address, others = 6prefix••••4suffix
     *    - HIDDEN: EOS with subdomain = ••••subdomain, others = ••••4suffix
     *    - SECRET: Always "••••••••"
     *
     * @param address The wallet address to obfuscate
     * @param privacyDisplayMode The privacy display mode (FULL, HIDDEN, SECRET)
     * @param isSensitive If true, overrides all modes and shows ••••••••
     * @param privacyModeEnabled If false, returns full address regardless of displayMode
     * @param forceRefresh Skip cache and recompute obfuscation
     * @return Obfuscated address string
     */
    fun obfuscate(
        address: String,
        privacyDisplayMode: DisplayMode = DisplayMode.HIDDEN,
        isSensitive: Boolean = false,
        privacyModeEnabled: Boolean = false ,
        forceRefresh: Boolean = false,
    ): String {
        if (address.isEmpty()) {
            return address
        }

        // Priority 1: Sensitive always shows SECRET
        if (isSensitive) {
            return obfuscateSecret(address)
        }

        // Priority 2: Privacy mode disabled shows FULL
        if (!privacyModeEnabled) {
            return obfuscateFull(address)
        }

        // Priority 3: Apply display mode
        val cacheKey = "${address}:${privacyDisplayMode.name}:${isSensitive}:${privacyModeEnabled}"
        if (!forceRefresh && obfuscationCache.containsKey(cacheKey)) {
            return obfuscationCache[cacheKey]!!
        }

        val obfuscated = when (privacyDisplayMode) {
            DisplayMode.FULL -> obfuscateFull(address)
            DisplayMode.HIDDEN -> obfuscateHidden(address)
            DisplayMode.SECRET -> obfuscateSecret(address)
        }

        // Cache the result
        obfuscationCache[cacheKey] = obfuscated

        return obfuscated
    }

    /**
     * FULL mode obfuscation
     * - EOS account: Show full address (no obfuscation)
     * - Other chains (Bitcoin/Ethereum/SOL): Truncate to 6 prefix + 4 suffix
     */
    private fun obfuscateFull(address: String): String {
        val isEosAccount = isEosAccountName(address)

        return when {
            isEosAccount -> address  // EOS: display full address
            else -> {
                // Bitcoin/Ethereum/SOL/others: 6prefix••••4suffix
                val prefix = address.take(6)
                val suffix = address.takeLast(4)
                "$prefix...$suffix"
            }
        }
    }

    /**
     * HIDDEN mode obfuscation
     * - EOS with subdomain: Show ••••subdomain (last part after dot)
     * - EOS without subdomain: Show ••••
     * - Other chains: Show ••••4suffix (last 4 characters)
     * - Very short addresses (≤4 chars): Show ••••
     */
    private fun obfuscateHidden(address: String): String {
        val isEosAccount = isEosAccountName(address)

        return when {
            address.length <= 4 -> "••••"
            isEosAccount && address.contains(".") -> {
                // EOS with subdomain: extract part after last dot
                val subdomain = address.substringAfterLast(".")
                "••••.$subdomain"
            }
            else -> {
                // Other chains or EOS without subdomain: show last 4 chars
                val suffix = address.takeLast(4)
                "••••$suffix"
            }
        }
    }

    /**
     * SECRET mode obfuscation
     * Always returns masked asterisks regardless of address content
     */
    private fun obfuscateSecret(address: String): String {
        return "••••••••"
    }

    /**
     * Detect if address is an EOS account name
     *
     * EOS account names:
     * - Length: 1-12 characters (typically 12)
     * - Characters: [a-z1-5.] only (lowercase letters a-z, numbers 1-5, and dots)
     * - No 0x prefix
     */
    private fun isEosAccountName(address: String): Boolean {
        if (address.isEmpty() || address.length > 12) return false
        if (address.startsWith("0x")) return false

        // EOS accounts contain only [a-z1-5.]
        val eosPattern = Regex("^[a-z1-5.]+$")
        return eosPattern.matches(address)
    }

    /**
     * Clear the obfuscation cache
     * Call this when switching themes, display preferences, or privacy mode
     */
    fun clearCache() {
        obfuscationCache.clear()
    }

    /**
     * Get cache statistics for debugging/monitoring
     */
    fun getCacheStats(): CacheStats {
        return CacheStats(
            size = obfuscationCache.size,
            entries = obfuscationCache.keys.toList()
        )
    }
}

/**
 * Cache statistics for monitoring and debugging
 */
data class CacheStats(
    val size: Int,
    val entries: List<String>
)