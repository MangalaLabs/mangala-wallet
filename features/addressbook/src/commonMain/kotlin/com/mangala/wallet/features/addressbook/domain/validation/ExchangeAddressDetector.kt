package com.mangala.wallet.features.addressbook.domain.validation

/**
 * Types of dangerous addresses
 */
enum class AddressType {
    EXCHANGE_HOT_WALLET,
    EXCHANGE_COLD_WALLET,
    SMART_CONTRACT,
    BURN_ADDRESS,
    SCAM_ADDRESS,
    NORMAL
}

/**
 * Severity levels for warnings
 */
enum class Severity {
    CRITICAL,  // Block transaction
    HIGH,      // Strong warning
    MEDIUM,    // Caution
    LOW        // Info only
}

/**
 * Detection result
 */
data class AddressDetectionResult(
    val addressType: AddressType,
    val name: String? = null,
    val severity: Severity,
    val message: String,
    val allowAsContact: Boolean,
    val allowForSending: Boolean
)

/**
 * Exchange and dangerous address detector
 */
class ExchangeAddressDetector {
    
    // Known exchange hot wallets
    private val exchangeHotWallets = mapOf(
        // Binance
        "0x28c6c06298d514db089934071355e5743bf21d60" to "Binance Hot Wallet 14",
        "0x21a31ee1afc51d94c2efccaa2092ad1028285549" to "Binance Hot Wallet 15",
        "0xdfd5293d8e347dfe59e90efd55b2956a1343963d" to "Binance Hot Wallet 16",
        "0x3f5ce5fbfe3e9af3971dd833d26ba9b5c936f0be" to "Binance Old Hot Wallet",
        
        // Coinbase
        "0x71660c4005ba85c37ccec55d0c4493e66fe775d3" to "Coinbase 1",
        "0x503828976d22510aad0201ac7ec88293211d23da" to "Coinbase 2",
        "0xb5d85cbf7cb3ee0d56b3bb207d5fc4b82f43f511" to "Coinbase 5",
        "0xeb2629a2734e272bcc07bda959863f316f4bd4cf" to "Coinbase 6",
        
        // Kraken
        "0x267be1c1d684f78cb4f6a176c4911b741e4ffdc0" to "Kraken Hot Wallet",
        "0x0a869d79a7052c7f1b55a8ebabbea3420f0d1e13" to "Kraken 2",
        
        // Crypto.com
        "0x7758e507850da48cd47df1fb5f875c23e3340c50" to "Crypto.com Hot Wallet",
        
        // KuCoin
        "0xd89350284c7732163765b23338f2ff27449e0bf5" to "KuCoin Hot Wallet",
        "0x1692e170361cefd1eb7240ec13d048fd9af6d667" to "KuCoin 2",
        
        // Bitfinex
        "0x876eabf441b2ee5b5b0554fd502a8e0600950cfa" to "Bitfinex Hot Wallet",
        
        // Huobi
        "0xab5c66752a9e8167967685f1450532fb96d5d24f" to "Huobi Hot Wallet",
        "0x6748f50f686bfbca6fe8ad62b22228b87f31ff2b" to "Huobi 2"
    )
    
    // Known exchange cold wallets
    private val exchangeColdWallets = mapOf(
        "0xbe0eb53f46cd790cd13851d5eff43d12404d33e8" to "Binance Cold Wallet",
        "0xf977814e90da44bfa03b6295a0616a897441acec" to "Binance: Binance-Peg Tokens",
        "0x8315177ab297ba92a06054ce80a67ed4dbd7ed3a" to "Arbitrum: Bridge"
    )
    
    // Smart contracts
    private val smartContracts = mapOf(
        // Uniswap
        "0x7a250d5630b4cf539739df2c5dacb4c659f2488d" to "Uniswap V2 Router",
        "0x68b3465833fb72a70ecdf485e0e4c7bd8665fc45" to "Uniswap V3 Router",
        "0x5c69bee701ef814a2b6a3edd4b1652cb9cc5aa6f" to "Uniswap V2 Factory",
        
        // SushiSwap
        "0xd9e1ce17f2641f24ae83637ab66a2cca9c378b9f" to "SushiSwap Router",
        
        // PancakeSwap
        "0x10ed43c718714eb63d5aa57b78b54704e256024e" to "PancakeSwap Router V2",
        
        // OpenSea
        "0x00000000006c3852cbef3e08e8df289169ede581" to "OpenSea: Seaport 1.1"
    )
    
    // Burn addresses
    private val burnAddresses = mapOf(
        // Ethereum
        "0x0000000000000000000000000000000000000000" to "ETH Zero Address (Burn)",
        "0x000000000000000000000000000000000000dead" to "ETH Dead Address (Burn)",
        
        // Bitcoin
        "1111111111111111111114oLvT2" to "Bitcoin Burn Address",
        "1BitcoinEaterAddressDontSendf59kuE" to "Bitcoin Eater Address",
        "1CounterpartyXXXXXXXXXXXXXXXUWLpVr" to "Counterparty Burn",
        
        // Other chains
        "11111111111111111111111111111111" to "Solana Burn Address",
        "eosio.null" to "EOS Null Account (Burn)",
        "TNuL1111111111111111111111111111BS" to "Tron Burn Address"
    )
    
    /**
     * Detect if an address is dangerous
     */
    fun detectAddress(address: String): AddressDetectionResult {
        val normalizedAddress = address.trim().lowercase()
        
        // Check burn addresses first (highest priority)
        burnAddresses[normalizedAddress]?.let { name ->
            return AddressDetectionResult(
                addressType = AddressType.BURN_ADDRESS,
                name = name,
                severity = Severity.CRITICAL,
                message = "This is a burn address! Funds sent here will be permanently destroyed. " +
                         "This address should never be saved as a contact.",
                allowAsContact = false,
                allowForSending = false
            )
        }
        
        // Check exchange hot wallets
        exchangeHotWallets[normalizedAddress]?.let { name ->
            return AddressDetectionResult(
                addressType = AddressType.EXCHANGE_HOT_WALLET,
                name = name,
                severity = Severity.CRITICAL,
                message = buildExchangeWarning(name, "hot wallet"),
                allowAsContact = true,  // Allow for tracking
                allowForSending = false // Never allow sending
            )
        }
        
        // Check exchange cold wallets
        exchangeColdWallets[normalizedAddress]?.let { name ->
            return AddressDetectionResult(
                addressType = AddressType.EXCHANGE_COLD_WALLET,
                name = name,
                severity = Severity.CRITICAL,
                message = buildExchangeWarning(name, "cold storage"),
                allowAsContact = true,
                allowForSending = false
            )
        }
        
        // Check smart contracts
        smartContracts[normalizedAddress]?.let { name ->
            return AddressDetectionResult(
                addressType = AddressType.SMART_CONTRACT,
                name = name,
                severity = Severity.HIGH,
                message = "This is $name smart contract. Do not send funds directly to contracts. " +
                         "Use the proper interface (website/app) to interact with this contract.",
                allowAsContact = true,  // Allow for tracking
                allowForSending = false // Prevent direct sends
            )
        }
        
        // Normal address
        return AddressDetectionResult(
            addressType = AddressType.NORMAL,
            severity = Severity.LOW,
            message = "",
            allowAsContact = true,
            allowForSending = true
        )
    }
    
    /**
     * Build warning message for exchange addresses
     */
    private fun buildExchangeWarning(name: String, type: String): String {
        val exchangeName = name.split(" ").first()
        
        return """
            🚨 CRITICAL: This is ${name}!
            
            This is ${exchangeName}'s ${type}, NOT your personal deposit address.
            
            DO NOT send funds here because:
            • This wallet belongs to $exchangeName, not you
            • Your funds will be mixed with millions of other users
            • There's no way to prove which funds are yours
            • Recovery is IMPOSSIBLE
            
            To deposit to $exchangeName:
            1. Log into your $exchangeName account
            2. Navigate to Wallet → Deposit
            3. Select your coin/token
            4. Copy YOUR personal deposit address
            5. That address will be different from this one
            
            You can save this address to monitor $exchangeName's activity, but NEVER send your own funds here.
        """.trimIndent()
    }
    
    /**
     * Check if address should be blocked from contact creation
     */
    fun shouldBlockContact(address: String): Boolean {
        val detection = detectAddress(address)
        return !detection.allowAsContact
    }
    
    /**
     * Check if address should be blocked from sending
     */
    fun shouldBlockSending(address: String): Boolean {
        val detection = detectAddress(address)
        return !detection.allowForSending
    }
    
    /**
     * Get a simple warning for UI display
     */
    fun getSimpleWarning(address: String): String? {
        val detection = detectAddress(address)
        return when (detection.addressType) {
            AddressType.EXCHANGE_HOT_WALLET, AddressType.EXCHANGE_COLD_WALLET -> 
                "${detection.name} - Never send funds here!"
            AddressType.SMART_CONTRACT -> 
                "${detection.name} - Use proper interface"
            AddressType.BURN_ADDRESS -> 
                "Burn address - Funds will be destroyed!"
            else -> null
        }
    }
}