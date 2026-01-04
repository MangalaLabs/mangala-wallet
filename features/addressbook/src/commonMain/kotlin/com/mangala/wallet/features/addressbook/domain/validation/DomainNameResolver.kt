package com.mangala.wallet.features.addressbook.domain.validation

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

/**
 * Result of domain resolution
 */
sealed class ResolveResult {
    data class Success(
        val originalInput: String,
        val resolvedAddress: String,
        val blockchain: String,
        val domainType: String,
        val expiryDate: Instant? = null
    ) : ResolveResult()
    
    data class NotDomain(val input: String) : ResolveResult()
    object InvalidFormat : ResolveResult()
    data class UnsupportedDomain(val extension: String) : ResolveResult()
    data class ResolutionFailed(val error: String) : ResolveResult()
    object Timeout : ResolveResult()
    data class NotFound(val domain: String) : ResolveResult()
}

/**
 * Cached result with timestamp
 */
data class CachedResult(
    val result: ResolveResult,
    val timestamp: Instant,
    val ttl: Duration = 1.hours
) {
    fun isValid(): Boolean {
        val now = Clock.System.now()
        return (now - timestamp) < ttl
    }
}

/**
 * Domain resolver interface
 */
interface DomainResolver {
    suspend fun resolve(domainName: String): String?
    val supportedExtension: String
}

/**
 * Mock resolvers for different domain services
 * In production, these would make actual API calls
 */
class EnsResolver : DomainResolver {
    override val supportedExtension = "eth"
    override suspend fun resolve(domainName: String): String? {
        // Mock implementation - in production would call ENS API
        return when (domainName) {
            "vitalik" -> "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045"
            "uniswap" -> "0x1a9C8182C09F50C8318d769245beA52c32BE35BC"
            else -> null
        }
    }
}

class MangalaNameResolver : DomainResolver {
    override val supportedExtension = "man"
    override suspend fun resolve(domainName: String): String? {
        // Mock implementation for Mangala Name Service
        return when (domainName) {
            "alice" -> "0xAliceMangalaAddress123456789"
            "bob" -> "0xBobMangalaAddress987654321"
            else -> null
        }
    }
}

class GmNetworkResolver : DomainResolver {
    override val supportedExtension = "gm"
    override suspend fun resolve(domainName: String): String? {
        // Mock implementation for GM Network
        return when (domainName) {
            "user" -> "0xGmUserAddress123456789"
            else -> null
        }
    }
}

class SolanaNameResolver : DomainResolver {
    override val supportedExtension = "sol"
    override suspend fun resolve(domainName: String): String? {
        // Mock implementation for Solana Name Service
        return when (domainName) {
            "wallet" -> "SolanaWalletAddress123456789"
            else -> null
        }
    }
}

/**
 * Main domain name resolver with caching and multiple resolver support
 */
class DomainNameResolver {
    private val resolvers = mapOf(
        "eth" to EnsResolver(),
        "man" to MangalaNameResolver(),
        "gm" to GmNetworkResolver(),
        "sol" to SolanaNameResolver(),
        // Add more resolvers as needed
    )
    
    private val cache = mutableMapOf<String, CachedResult>()
    private val pendingRequests = mutableMapOf<String, Deferred<ResolveResult>>()
    
    /**
     * Supported domain extensions
     */
    val supportedExtensions = resolvers.keys.toList()
    
    /**
     * Resolve a domain name to wallet address
     */
    suspend fun resolveAddress(input: String): ResolveResult = coroutineScope {
        val trimmed = input.trim().lowercase()
        
        // Check if input is a domain name
        val domainRegex = Regex("^[a-zA-Z0-9-]+\\.[a-zA-Z]+$")
        if (!trimmed.matches(domainRegex)) {
            return@coroutineScope ResolveResult.NotDomain(input)
        }
        
        // Check cache first
        cache[trimmed]?.let { cached ->
            if (cached.isValid()) return@coroutineScope cached.result
        }
        
        // Check if already resolving
        pendingRequests[trimmed]?.let { 
            return@coroutineScope it.await() 
        }
        
        // Parse domain
        val parts = trimmed.split(".")
        if (parts.size != 2) {
            return@coroutineScope ResolveResult.InvalidFormat
        }
        
        val name = parts[0]
        val extension = parts[1]
        
        // Check for supported extension
        val resolver = resolvers[extension]
            ?: return@coroutineScope ResolveResult.UnsupportedDomain(extension)
        
        // Start resolution with timeout
        val deferred = async {
            try {
                withTimeout(5.seconds) {
                    val address = resolver.resolve(name)
                    if (address != null) {
                        val result = ResolveResult.Success(
                            originalInput = input,
                            resolvedAddress = address,
                            blockchain = getBlockchainForDomain(extension),
                            domainType = extension
                        )
                        
                        // Cache the result
                        cache[trimmed] = CachedResult(result, Clock.System.now())
                        result
                    } else {
                        ResolveResult.NotFound(trimmed)
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is kotlinx.coroutines.TimeoutCancellationException -> ResolveResult.Timeout
                    else -> ResolveResult.ResolutionFailed(e.message ?: "Unknown error")
                }
            }
        }
        
        pendingRequests[trimmed] = deferred
        val result = deferred.await()
        pendingRequests.remove(trimmed)
        
        return@coroutineScope result
    }
    
    /**
     * Get blockchain type for domain extension
     */
    private fun getBlockchainForDomain(extension: String): String {
        return when (extension) {
            "eth", "gm", "arb", "crypto" -> "ETH"
            "man" -> "MAN" // Mangala blockchain
            "sol" -> "SOL"
            "bnb" -> "BSC"
            else -> "UNKNOWN"
        }
    }
    
    /**
     * Check if input might be a domain name
     */
    fun isPossibleDomain(input: String): Boolean {
        val trimmed = input.trim().lowercase()
        
        // Basic check for domain format
        if (!trimmed.contains(".")) return false
        
        val parts = trimmed.split(".")
        if (parts.size != 2) return false
        
        // Check if extension is supported
        return parts[1] in supportedExtensions
    }
    
    /**
     * Suggest corrections for typos in domain extensions
     */
    fun suggestCorrections(input: String): List<String> {
        val parts = input.split(".")
        if (parts.size != 2) return emptyList()
        
        val name = parts[0]
        val ext = parts[1].lowercase()
        
        // Find similar extensions using Levenshtein distance
        val suggestions = supportedExtensions
            .filter { known -> levenshteinDistance(ext, known) <= 1 }
            .map { "$name.$it" }
        
        return suggestions
    }
    
    /**
     * Calculate Levenshtein distance between two strings
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        
        for (i in 1..m) {
            for (j in 1..n) {
                dp[i][j] = if (s1[i - 1] == s2[j - 1]) {
                    dp[i - 1][j - 1]
                } else {
                    minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1
                }
            }
        }
        
        return dp[m][n]
    }
    
    /**
     * Detect homograph attacks (characters that look similar)
     */
    fun detectHomographAttack(domain: String): Boolean {
        // Check for non-ASCII characters
        if (domain.any { it.code > 127 }) {
            return true
        }
        
        // Check for commonly confused characters
        val suspiciousPatterns = listOf(
            "vіtalik" to "vitalik", // і is Cyrillic
            "vítálik" to "vitalik", // accented characters
            "vitaIik" to "vitalik", // capital I instead of l
            "vita1ik" to "vitalik", // 1 instead of l
            "vita|ik" to "vitalik", // pipe instead of l
        )
        
        return suspiciousPatterns.any { (suspicious, _) ->
            domain.contains(suspicious, ignoreCase = true)
        }
    }
    
    /**
     * Clear cache (useful for testing or when needed)
     */
    fun clearCache() {
        cache.clear()
    }
}