package com.mangala.wallet.features.addressbook.presentation.contact.list

import androidx.compose.ui.graphics.Color
import com.benasher44.uuid.uuid4
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.enum.TransactionStatus
import com.mangala.wallet.features.addressbook.data.model.transaction.ContactTransactionEntity
import com.mangala.wallet.features.addressbook.data.model.transaction.TransactionHistoryEntity
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import com.mangala.wallet.features.addressbook.domain.repository.transaction.TransactionRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt
import kotlin.random.Random

//TODO: for test
class TestDataGenerator(
    private val contactRepository: ContactRepository,
    private val tagRepository: TagRepository,
    private val groupRepository: GroupRepository,
    private val walletAddressRepository: WalletAddressRepository,
    private val blockchainRepository: BlockchainRepository,
    private val transactionHistoryRepository: TransactionRepository
) {
    private val firstNames = listOf(
        "James",
        "John",
        "Robert",
        "Michael",
        "William",
        "David",
        "Richard",
        "Joseph",
        "Thomas",
        "Charles",
        "Mary",
        "Patricia",
        "Jennifer",
        "Linda",
        "Elizabeth",
        "Barbara",
        "Susan",
        "Jessica",
        "Sarah",
        "Karen"
    )

    private val lastNames = listOf(
        "Smith",
        "Johnson",
        "Williams",
        "Brown",
        "Jones",
        "Garcia",
        "Miller",
        "Davis",
        "Rodriguez",
        "Martinez",
        "Anderson",
        "Taylor",
        "Thomas",
        "Hernandez",
        "Moore",
        "Martin",
        "Jackson",
        "Thompson",
        "White",
        "Lopez"
    )

    private val tokenSymbols =
        listOf("ETH", "BTC", "SOL", "MATIC", "AVAX", "BNB", "ADA", "DOT", "USDT", "USDC")

    // Thêm cấu trúc để random status giao dịch
    private val transactionStatuses = listOf(
        TransactionStatus.CONFIRMED,
        TransactionStatus.CONFIRMED,
        TransactionStatus.DRAFT,
        TransactionStatus.FAILED,
        TransactionStatus.PENDING
    ) // 60% là CONFIRMED


    private val emailDomains =
        listOf("gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "protonmail.com")

    private val phoneAreaCodes =
        listOf("201", "202", "212", "213", "214", "215", "301", "302", "303", "304", "305")

    private val blockchainAddressPrefixes = mapOf(
        "ethereum" to "0x",
        "bitcoin" to "bc1",
        "solana" to "So1"
    )

    suspend fun generateTestData(contactCount: Int = 1000) {
        // Make sure default tags and groups exist
        createDefaultTagsIfNeeded()
        createDefaultGroupsIfNeeded()
        createDefaultBlockchainsIfNeeded()
        setupGroupBlockchains()

        // Get all tags, groups, and blockchain types
        val allTags = tagRepository.getActiveTags()
        val allGroups = groupRepository.getAllGroups()
        val allBlockchains = blockchainRepository.getAllBlockchainTypes()

        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())

        // Generate contacts
        repeat(contactCount) {
            val firstName = firstNames.random()
            val lastName = lastNames.random()

            // Create a contact
            val contactId = uuid4().toString()
            val contact = ContactEntity.create(
                id = contactId,
                name = "$firstName $lastName",
            )

            contactRepository.insertContact(contact)

            // Add to favorite (20% chance)
            if (Random.nextInt(5) == 0) {
                contactRepository.addFavorite(contactId)
            }

            // Add email addresses (1-3 per contact)
            val emailCount = Random.nextInt(1, 4)
            val emails = mutableListOf<EmailAddressEntity>()

            repeat(emailCount) { emailIdx ->
                val email = EmailAddressEntity(
                    id = "",
                    contactId = contactId,
                    email = "${firstName.lowercase()}.${lastName.lowercase()}${if (emailIdx > 0) emailIdx else ""}@${emailDomains.random()}",
                    label = if (emailIdx == 0) "Primary" else "Other",
                    isPrimary = emailIdx == 0,
                    createdAt = now,
                    updatedAt = now
                )
                emails.add(email)
            }
            contactRepository.insertEmailAddressesBatch(emails)

            // Add phone numbers (1-2 per contact)
            val phoneCount = Random.nextInt(1, 3)
            val phoneNumbers = mutableListOf<PhoneNumberEntity>()

            repeat(phoneCount) { phoneIdx ->
                val areaCode = phoneAreaCodes.random()
                val phone = PhoneNumberEntity(
                    id = "",
                    contactId = contactId,
                    phoneNumber = "$areaCode-${Random.nextInt(100, 1000)}-${
                        Random.nextInt(
                            1000,
                            10000
                        )
                    }",
                    label = if (phoneIdx == 0) "Mobile" else "Work",
                    isPrimary = phoneIdx == 0,
                    createdAt = now,
                    updatedAt = now
                )
                phoneNumbers.add(phone)
            }
            contactRepository.insertPhoneNumbersBatch(phoneNumbers)

            // Assign tags (0-3 per contact)
            if (allTags.isNotEmpty()) {
                val tagCount = Random.nextInt(0, 4)
                val shuffledTags = allTags.shuffled()
                val selectedTags = shuffledTags.take(tagCount).map { it.id }

                if (selectedTags.isNotEmpty()) {
                    tagRepository.batchAssignTagsToContact(contactId, selectedTags)
                }
            }

            // Add to groups (0-2 per contact)
            var hasPrimaryWallet = false

            // Chỉnh sửa phần thêm contact vào group
            if (allGroups.isNotEmpty()) {
                val groupCount = Random.nextInt(0, 3)
                val shuffledGroups = allGroups.shuffled()

                println("Contact ID: $contactId, Group Count: $groupCount")

                shuffledGroups.take(groupCount).forEach { group ->
                    // Chọn blockchain phù hợp với group.main_blockchain_id nếu có
                    val blockchainToUse = if (group.mainBlockchainId != null) {
                        // Ưu tiên dùng blockchain chính của group nếu được định nghĩa
                        allBlockchains.find { it.id == group.mainBlockchainId }
                    } else {
                        // Nếu không, chọn random một blockchain
                        allBlockchains.random()
                    }

                    // Nếu tìm được blockchain phù hợp
                    if (blockchainToUse != null) {
                        val prefix = blockchainAddressPrefixes[blockchainToUse.name.lowercase()] ?: "0x"

                        // Kiểm tra xem contact đã có ví với blockchain này chưa
                        val existingWallet = walletAddressRepository.getWalletAddressesForContact(contactId)
                            .firstOrNull { it.blockchainNetworkId == blockchainToUse.id }

                        val walletId = if (existingWallet != null) {
                            // Sử dụng ví hiện có nếu contact đã có ví với blockchain này
                            existingWallet.id
                        } else {
                            // Tạo ví mới nếu chưa có
                            val walletAddress = WalletAddressEntity.create(
                                id = "",
                                contactId = contactId,
                                blockchainTypeId = blockchainToUse.id,
                                address = "$prefix${uuid4().toString().replace("-", "").substring(0, 10)}",
                                alias = "${blockchainToUse.name} Wallet for ${group.name}",
                                isPrimary = !hasPrimaryWallet, // Chỉ primary nếu chưa có ví primary
                                isVerified = Random.nextBoolean()
                            )
                            val newWalletId = walletAddressRepository.insertWalletAddress(walletAddress)

                            // Cập nhật flag sau khi tạo ví
                            if (!hasPrimaryWallet) {
                                hasPrimaryWallet = true
                            }

                            newWalletId
                        }

                        groupRepository.addContactToGroup(contactId, group.id, walletId)
                    }
                }
            }

            // Add wallet addresses (1-3 per contact)
            if (allBlockchains.isNotEmpty()) {
                val walletCount = Random.nextInt(1, 4)
                val shuffledBlockchains = allBlockchains.shuffled().take(walletCount)

                shuffledBlockchains.forEachIndexed { idx, blockchain ->
                    val prefix = blockchainAddressPrefixes[blockchain.name.lowercase()] ?: "0x"
                    val walletAddress = WalletAddressEntity.create(
                        id = "",
                        contactId = contactId,
                        blockchainTypeId = blockchain.id,
                        address = "$prefix${uuid4().toString().replace("-", "").substring(0, 10)}",
                        alias = "${blockchain.name} Wallet",
                        isPrimary = !hasPrimaryWallet && idx == 0, // Primary nếu chưa có ví primary và là ví đầu tiên
                        isVerified = Random.nextBoolean()
                    )

                    walletAddressRepository.insertWalletAddress(walletAddress)

                    // Cập nhật flag sau khi tạo ví primary
                    if (!hasPrimaryWallet && idx == 0) {
                        hasPrimaryWallet = true
                    }
                }
            }
        }
        // Generate transaction history
        generateTransactionHistory(contactCount)
    }

    private suspend fun setupGroupBlockchains() {
        val allGroups = groupRepository.getAllGroups()
        val allBlockchains = blockchainRepository.getAllBlockchainTypes()

        if (allBlockchains.isEmpty()) return

        allGroups.forEach { group ->
            if (group.mainBlockchainId == null) {
                val blockchain = when {
                    group.name.contains("DeFi", ignoreCase = true) ->
                        allBlockchains.find { it.name.equals("Ethereum", ignoreCase = true) }
                    group.name.contains("Trading", ignoreCase = true) ->
                        allBlockchains.find { it.name.equals("Bitcoin", ignoreCase = true) }
                    group.name.contains("NFT", ignoreCase = true) ->
                        allBlockchains.find { it.name.equals("Solana", ignoreCase = true) }
                    else -> allBlockchains.random()
                }

                blockchain?.let {
                    groupRepository.updateGroupMainBlockchain(group.id, it.id)
                }
            }
        }
    }

    // Public method to generate transactions for existing contacts
    suspend fun generateTransactionsForExistingContacts(targetTransactionCount: Int = 50) {
        println("Generating $targetTransactionCount transactions for existing contacts...")
        
        // Get all existing contacts
        val allContacts = contactRepository.getAllContacts().first()
        if (allContacts.isEmpty()) {
            println("No contacts found. Please generate contacts first.")
            return
        }
        
        var totalTransactionsCreated = 0
        val contactsToProcess = allContacts.shuffled() // Randomize order
        
        // Generate transactions until we reach target count
        for (contact in contactsToProcess) {
            if (totalTransactionsCreated >= targetTransactionCount) break
            
            val contactWallets = walletAddressRepository.getWalletAddressesForContact(contact.id)
            if (contactWallets.isEmpty()) continue
            
            // Calculate how many transactions to create for this contact
            val remainingTransactions = targetTransactionCount - totalTransactionsCreated
            val transactionCount = minOf(Random.nextInt(1, 6), remainingTransactions)
            
            repeat(transactionCount) {
                val wallet = contactWallets.random()
                val otherContact = allContacts.filter { it.id != contact.id }.randomOrNull()
                
                if (otherContact != null) {
                    // Generate a transaction between these two contacts
                    val transactionCreated = generateSingleTransaction(contact, wallet, otherContact)
                    if (transactionCreated) {
                        totalTransactionsCreated++
                    }
                }
            }
        }
        
        println("Generated $totalTransactionsCreated transactions total.")
    }
    
    // Helper method to generate a single transaction
    private suspend fun generateSingleTransaction(
        contact: ContactEntity,
        wallet: WalletAddressWithNetworkModel,
        otherContact: ContactEntity
    ): Boolean {
        try {
            // Get or create wallet for other contact with same blockchain
            val otherContactWallets = walletAddressRepository
                .getWalletAddressesForContact(otherContact.id)
                .filter { it.blockchainNetworkId == wallet.blockchainNetworkId }
            
            val otherWallet = if (otherContactWallets.isEmpty()) {
                // Create a new wallet for the other contact
                val blockchain = blockchainRepository.getBlockchainTypeById(wallet.blockchainNetworkId)
                val prefix = blockchainAddressPrefixes[blockchain?.name?.lowercase()] ?: "0x"
                val newWallet = WalletAddressEntity.create(
                    id = "",
                    contactId = otherContact.id,
                    blockchainTypeId = wallet.blockchainNetworkId,
                    address = "$prefix${uuid4().toString().replace("-", "").substring(0, 10)}",
                    alias = "${blockchain?.name ?: "Unknown"} Wallet",
                    isPrimary = false,
                    isVerified = Random.nextBoolean()
                )
                walletAddressRepository.insertWalletAddress(newWallet)
                // Create a WalletAddressWithNetworkModel to return
                WalletAddressWithNetworkModel(
                    id = newWallet.id,
                    contactId = newWallet.contactId,
                    blockchainNetworkId = newWallet.blockchainTypeId,
                    address = newWallet.address,
                    alias = newWallet.alias,
                    walletType = null,
                    isDefault = newWallet.isPrimary,
                    networkName = blockchain?.name,
                    networkSymbol = blockchain?.symbol,
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                    updatedAt = null,
                    isSensitive = newWallet.isSensitive,
                    isVerified = newWallet.isVerified
                )
            } else {
                otherContactWallets.random()
            }
            
            // Create transaction
            val now = Clock.System.now()
            val transactionId = uuid4().toString()
            val isSender = Random.nextBoolean()
            
            val fromAddress = if (isSender) wallet.address else otherWallet.address
            val toAddress = if (isSender) otherWallet.address else wallet.address
            
            val amount = when (Random.nextInt(100)) {
                in 0..30 -> Random.nextDouble(0.001, 0.1)
                in 31..60 -> Random.nextDouble(0.1, 1.0)
                in 61..85 -> Random.nextDouble(1.0, 10.0)
                else -> Random.nextDouble(10.0, 100.0)
            }
            
            val tokenSymbol = tokenSymbols.random()
            val transactionHash = "0x${uuid4().toString().replace("-", "")}"
            val status = transactionStatuses.random()
            val fee = Random.nextDouble(0.0001, 0.01)
            
            val note = if (Random.nextBoolean()) {
                val notes = listOf(
                    "Payment for services",
                    "Monthly payment",
                    "Refund",
                    "Loan repayment",
                    "Investment return",
                    "Split bill",
                    "Gift",
                    "Charity donation",
                    "NFT purchase",
                    "Staking rewards",
                    "Trading profit"
                )
                notes.random()
            } else null
            
            val transaction = TransactionHistoryEntity(
                id = transactionId,
                fromAddress = fromAddress,
                toAddress = toAddress,
                blockchainTypeId = wallet.blockchainNetworkId,
                amount = amount.toString(),
                tokenSymbol = tokenSymbol,
                transactionHash = transactionHash,
                status = status,
                timestamp = now,
                fee = fee.toString(),
                note = note
            )
            
            transactionHistoryRepository.insertTransaction(transaction)
            
            // Link transaction to both contacts
            transactionHistoryRepository.linkTransactionToContact(
                contact.id,
                transactionId,
                wallet.id,
                isSender
            )
            
            transactionHistoryRepository.linkTransactionToContact(
                otherContact.id,
                transactionId,
                otherWallet.id,
                !isSender
            )
            
            return true
        } catch (e: Exception) {
            println("Error generating transaction: ${e.message}")
            return false
        }
    }

    //
    private suspend fun generateTransactionHistory(contactCount: Int) {
        println("Generating transaction history...")

        // Lấy tất cả contacts và wallet addresses
        val allContacts = contactRepository.getAllContacts().first()

        // Tạo từ 1-5 giao dịch cho mỗi contact
        allContacts.forEach { contact ->
            // Get wallet addresses of the contact
            val contactWallets = walletAddressRepository.getWalletAddressesForContact(contact.id)

            if (contactWallets.isNotEmpty()) {
                // Number of transactions for each contact
                val transactionCount = Random.nextInt(1, 6)

                repeat(transactionCount) { txIdx ->
                    // Choose random wallet address for transaction
                    val wallet = contactWallets.random()

                    // Choose random other contact for transaction partner
                    val otherContact = allContacts.filter { it.id != contact.id }.randomOrNull()

                    if (otherContact != null) {
                        // Get wallet addresses of partner with same blockchain
                        val otherContactWallets = walletAddressRepository
                            .getWalletAddressesForContact(otherContact.id)
                            .filter { it.blockchainNetworkId == wallet.blockchainNetworkId }

                        // If partner doesn't have a wallet with same blockchain, create one
                        val otherWallet = if (otherContactWallets.isEmpty()) {
                            val blockchain =
                                blockchainRepository.getBlockchainTypeById(wallet.blockchainNetworkId)
                            val prefix =
                                blockchainAddressPrefixes[blockchain?.name?.lowercase()] ?: "0x"
                            val newWallet = WalletAddressEntity.create(
                                id = "",
                                contactId = otherContact.id,
                                blockchainTypeId = wallet.blockchainNetworkId,
                                address = "$prefix${
                                    uuid4().toString().replace("-", "").substring(0, 10)
                                }",
                                alias = "${blockchain?.name} Wallet",
                                isPrimary = false,
                                isVerified = Random.nextBoolean()
                            )
                            val newWalletId = walletAddressRepository.insertWalletAddress(newWallet)
                            walletAddressRepository.getWalletAddressById(newWalletId)
                        } else {
                            otherContactWallets.random()
                        }

                        // Randomly decide who is sender and who is receiver
                        val isSender = Random.nextBoolean()
                        val fromAddress = if (isSender) wallet.address else otherWallet.address
                        val toAddress = if (isSender) otherWallet.address else wallet.address

                        // Random token amount and symbol
                        val amount = ((Random.nextDouble() * 10.0) * 100.0).roundToInt() / 100.0

                        // Get blockchain type
                        val blockchain =
                            blockchainRepository.getBlockchainTypeById(wallet.blockchainNetworkId)

                        // Choose token symbol based on blockchain
                        val tokenSymbol = when (blockchain?.name?.lowercase()) {
                            "ethereum" -> "ETH"
                            "bitcoin" -> "BTC"
                            "solana" -> "SOL"
                            "polygon" -> "MATIC"
                            "avalanche" -> "AVAX"
                            "binance smart chain" -> "BNB"
                            "cardano" -> "ADA"
                            "polkadot" -> "DOT"
                            else -> tokenSymbols.random()
                        }

                        // Random transaction fee
                        val fee = (Random.nextDouble() * 0.01 * 1000.0).roundToInt() / 1000.0

                        // Random transaction time (within last 30 days)
                        val now = Clock.System.now()

                        // Generate transaction history
                        val transactionId = uuid4().toString()
                        val transactionHash = uuid4().toString().replace("-", "")
                        val status = transactionStatuses.random()
                        val note = if (Random.nextBoolean()) {
                            val notes = listOf(
                                "Payment for services",
                                "Monthly subscription",
                                "Return investment",
                                "Split bill",
                                "Gift",
                                "Charity donation",
                                "NFT purchase",
                                "Staking rewards",
                                "Trading profit"
                            )
                            notes.random()
                        } else null

                        // Create transaction entity
                        val transaction = TransactionHistoryEntity(
                            id = transactionId,
                            fromAddress = fromAddress,
                            toAddress = toAddress,
                            blockchainTypeId = wallet.blockchainNetworkId,
                            amount = amount.toString(),
                            tokenSymbol = tokenSymbol,
                            transactionHash = transactionHash,
                            status = status,
                            timestamp = Clock.System.now(),
                            fee = fee.toString(),
                            note = note
                        )

                        // Save transaction
                        transactionHistoryRepository.insertTransaction(transaction)

                        // Link transaction to contacts
                        val contactTransaction1 = ContactTransactionEntity(
                            id = uuid4().toString(),
                            contactId = contact.id,
                            transactionId = transactionId,
                            walletAddressId = wallet.id,
                            isSender = isSender,
                            createdAt = now
                        )

                        val contactTransaction2 = ContactTransactionEntity(
                            id = uuid4().toString(),
                            contactId = otherContact.id,
                            transactionId = transactionId,
                            walletAddressId = otherWallet.id,
                            isSender = !isSender,
                            createdAt = now
                        )

                        // Link transactions to both contacts
                        transactionHistoryRepository.linkTransactionToContact(
                            contactTransaction1.contactId,
                            contactTransaction1.transactionId,
                            contactTransaction1.walletAddressId,
                            contactTransaction1.isSender
                        )

                        transactionHistoryRepository.linkTransactionToContact(
                            contactTransaction2.contactId,
                            contactTransaction2.transactionId,
                            contactTransaction2.walletAddressId,
                            contactTransaction2.isSender
                        )
                    }
                }
            }
        }

        println("Transaction history generation completed.")
    }

    /**
     * Creates default tags if they don't already exist in the database
     */
    private suspend fun createDefaultTagsIfNeeded() {
        val defaultTags = listOf(
            "Family" to ColorsNew.colorToIndex(ColorsNew.avatarA),
            "Friend" to ColorsNew.colorToIndex(ColorsNew.avatarB),
            "Business" to ColorsNew.colorToIndex(ColorsNew.avatarC),
            "VIP" to ColorsNew.colorToIndex(ColorsNew.avatarD),
            "Customer" to ColorsNew.colorToIndex(ColorsNew.avatarE),
            "Partner" to ColorsNew.colorToIndex(ColorsNew.avatarF)
        )

        val existingTags = tagRepository.getActiveTags()
        val existingTagNames = existingTags.map { it.name.lowercase() }

        TagEntity

        defaultTags.forEach { (name, color) ->
            if (!existingTagNames.contains(name.lowercase())) {
                try {
                    tagRepository.createTag(
                        TagEntity.create(
                            id = uuid4().toString(),
                            name = name,
                            color = color.toString()
                        )
                    )
                } catch (e: Exception) {
                    // Ignore duplicate key errors if they happen
                }
            }
        }
    }

    /**
     * Creates default groups if they don't already exist in the database
     */
//    private suspend fun createDefaultGroupsIfNeeded() {
//        val defaultGroups = listOf(
//            "Close Friends",
//            "Work Colleagues",
//            "Investment Group",
//            "Trading Partners",
//            "DeFi Projects"
//        )
//
//        val existingGroups = groupRepository.getAllGroups()
//        val existingGroupNames = existingGroups.map { it.name.lowercase() }
//
//        defaultGroups.forEach { name ->
//            if (!existingGroupNames.contains(name.lowercase())) {
//                try {
//                    groupRepository.insertGroup(
//                        GroupEntity.create(
//                            id = uuid4().toString(),
//                            name = name
//                        )
//                    )
//                } catch (e: Exception) {
//                    // Ignore duplicate key errors if they happen
//                }
//            }
//        }
//    }
    fun saveColorToDatabase(color: Color): String {
        // Convert Color to a hex string
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()
        return "#${red.toString(16).padStart(2, '0')}${green.toString(16).padStart(2, '0')}${blue.toString(16).padStart(2, '0')}".uppercase()
    }


    private suspend fun createDefaultGroupsIfNeeded() {
        val defaultGroups = listOf(
            GroupInfo(
                name = "Close Friends",
                description = "Personal friends and family members",
                icon = "👪",
                color = ColorsNew.colorToIndex(ColorsNew.avatarC).toString()
            ),
            GroupInfo(
                name = "Work Colleagues",
                description = "Professional contacts and colleagues",
                icon = "💼",
                color = ColorsNew.colorToIndex(ColorsNew.avatarW).toString()
            ),
            GroupInfo(
                name = "Investment Group",
                description = "Long-term investment partners and opportunities",
                icon = "📈",
                color = ColorsNew.colorToIndex(ColorsNew.avatarI).toString()
            ),
            GroupInfo(
                name = "Trading Partners",
                description = "Active trading contacts and exchanges",
                icon = "🔄",
                color = ColorsNew.colorToIndex(ColorsNew.avatarT).toString()
            ),
            GroupInfo(
                name = "DeFi Projects",
                description = "Decentralized finance projects and partners",
                icon = "🏦",
                color = ColorsNew.colorToIndex(ColorsNew.avatarD).toString()
            )
        )

        val existingGroups = groupRepository.getAllGroups()
        val existingGroupNames = existingGroups.map { it.name.lowercase() }

        defaultGroups.forEach { groupInfo ->
            if (!existingGroupNames.contains(groupInfo.name.lowercase())) {
                try {
                    groupRepository.insertGroup(
                        GroupEntity.create(
                            id = uuid4().toString(),
                            name = groupInfo.name,
                            description = groupInfo.description,
                            icon = groupInfo.icon,
                            color = groupInfo.color
                        )
                    )
                } catch (e: Exception) {
                    // Ignore duplicate key errors if they happen
                }
            }
        }
    }

    // Helper class để định nghĩa thông tin nhóm
    private data class GroupInfo(
        val name: String,
        val description: String? = null,
        val icon: String? = null,
        val color: String? = null
    )

    private suspend fun createDefaultBlockchainsIfNeeded() {
        val defaultBlockchains = listOf(
            BlockchainInfo(
                "ethereum",
                "Ethereum",
                "ETH",
                ColorsNew.colorToIndex(ColorsNew.avatarE).toString(),
                "0x[a-fA-F0-9]{40}",  // Định dạng địa chỉ Ethereum
                "^0x[a-fA-F0-9]{40}$" // Regex validation Ethereum
            ),
            BlockchainInfo(
                "bitcoin",
                "Bitcoin",
                "BTC",
                ColorsNew.colorToIndex(ColorsNew.avatarB).toString(),
                "bc1[a-zA-Z0-9]{39,59}|[13][a-km-zA-HJ-NP-Z1-9]{25,34}", // Định dạng Bitcoin
                "^(bc1[a-zA-Z0-9]{39,59}|[13][a-km-zA-HJ-NP-Z1-9]{25,34})$" // Regex validation
            ),
            BlockchainInfo(
                "solana",
                "Solana",
                "SOL",
                ColorsNew.colorToIndex(ColorsNew.avatarS).toString(),
                "[1-9A-HJ-NP-Za-km-z]{32,44}", // Định dạng Solana
                "^[1-9A-HJ-NP-Za-km-z]{32,44}$" // Regex validation
            ),
            // Thêm các blockchain khác tương tự
        )

        val existingBlockchains = blockchainRepository.getAllBlockchainTypes()
        val existingBlockchainNames = existingBlockchains.map { it.name.lowercase() }

        defaultBlockchains.forEach { blockchain ->
            if (!existingBlockchainNames.contains(blockchain.name.lowercase())) {
                try {
                    blockchainRepository.insertBlockchainType(
                        BlockchainTypeEntity.create(
                            id = blockchain.id,
                            name = blockchain.name,
                            symbol = blockchain.symbol,
                            color = blockchain.color,
                            addressFormat = blockchain.addressFormat,
                            validationRegex = blockchain.validationRegex,
                            icon = null,
                            networkType = BlockchainTypeEntity.NETWORK_MAINNET,
                            isActive = true
                        )
                    )
                } catch (e: Exception) {
                    println("Error creating blockchain ${blockchain.name}: ${e.message}")
                }
            }
        }
    }

    // Class để hỗ trợ việc định nghĩa blockchain
    private data class BlockchainInfo(
        val id: String,
        val name: String,
        val symbol: String,
        val color: String,
        val addressFormat: String?,
        val validationRegex: String?
    )

    /**
     * Generates 200 diverse groups for performance testing
     */
    suspend fun generateLargeGroupDataset() {
        val groupCategories = listOf(
            "Business", "Technology", "Finance", "Investment", "Trading", "DeFi", "NFT", 
            "Gaming", "Social", "Education", "Healthcare", "Travel", "Food", "Sports",
            "Music", "Art", "Science", "Research", "Development", "Marketing"
        )
        
        val businessTypes = listOf(
            "Startup", "Enterprise", "Corporation", "Agency", "Consultancy", "Firm", 
            "Group", "Association", "Club", "Society", "Network", "Alliance", "Union"
        )
        
        val descriptors = listOf(
            "Professional", "Elite", "Premium", "Advanced", "Expert", "Global", "International",
            "Regional", "Local", "Community", "Exclusive", "Private", "Public", "Active",
            "Dynamic", "Innovation", "Strategic", "Creative", "Technical", "Digital"
        )
        
        val icons = listOf(
            "🏢", "💼", "📊", "💰", "🚀", "🔬", "🎯", "⚡", "🌟", "🎮", "🎨", "📱", "💻", "🌐",
            "🔥", "💎", "🏆", "🎪", "🎭", "🎵", "📚", "🏥", "✈️", "🍔", "⚽", "🎸", "🎨", "🔬"
        )
        
        val availableColors = listOf(
            ColorsNew.avatarA, ColorsNew.avatarB, ColorsNew.avatarC, ColorsNew.avatarD, 
            ColorsNew.avatarE, ColorsNew.avatarF, ColorsNew.avatarG, ColorsNew.avatarH,
            ColorsNew.avatarI, ColorsNew.avatarJ, ColorsNew.avatarK, ColorsNew.avatarL,
            ColorsNew.avatarM, ColorsNew.avatarN, ColorsNew.avatarO, ColorsNew.avatarP,
            ColorsNew.avatarQ, ColorsNew.avatarR, ColorsNew.avatarS, ColorsNew.avatarT,
            ColorsNew.avatarU, ColorsNew.avatarV, ColorsNew.avatarW, ColorsNew.avatarX,
            ColorsNew.avatarY, ColorsNew.avatarZ
        )

        val existingGroups = groupRepository.getAllGroups()
        val existingGroupNames = existingGroups.map { it.name.lowercase() }

        val groupsToCreate = mutableListOf<GroupInfo>()

        repeat(200) { index ->
            val category = groupCategories.random()
            val businessType = businessTypes.random()
            val descriptor = descriptors.random()
            val icon = icons.random()
            val color = ColorsNew.colorToIndex(availableColors.random()).toString()
            
            val groupName = when (Random.nextInt(4)) {
                0 -> "$descriptor $category $businessType"
                1 -> "$category $businessType Network"
                2 -> "$descriptor $category Alliance"
                3 -> "$category ${businessType}s ${index + 1}"
                else -> "$descriptor $category Group"
            }
            
            val description = when (Random.nextInt(5)) {
                0 -> "A $descriptor network of $category professionals working together"
                1 -> "Connecting $category experts and enthusiasts worldwide"
                2 -> "Premier $category $businessType for industry leaders"
                3 -> "Collaborative $category community focused on innovation"
                4 -> "$descriptor $category group dedicated to excellence and growth"
                else -> "Professional $category network for business development"
            }

            if (!existingGroupNames.contains(groupName.lowercase())) {
                groupsToCreate.add(
                    GroupInfo(
                        name = groupName,
                        description = description,
                        icon = icon,
                        color = color
                    )
                )
            }
        }

        // Insert groups in batches for better performance
        groupsToCreate.forEach { groupInfo ->
            try {
                groupRepository.insertGroup(
                    GroupEntity.create(
                        id = uuid4().toString(),
                        name = groupInfo.name,
                        description = groupInfo.description,
                        icon = groupInfo.icon,
                        color = groupInfo.color
                    )
                )
            } catch (e: Exception) {
                println("Error creating group ${groupInfo.name}: ${e.message}")
            }
        }
        
        println("Generated ${groupsToCreate.size} new groups for performance testing")
    }
}