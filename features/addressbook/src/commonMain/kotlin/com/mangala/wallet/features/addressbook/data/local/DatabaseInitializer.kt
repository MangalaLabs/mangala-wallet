package com.mangala.wallet.features.addressbook.data.local

import com.mangala.wallet.features.addressbook.database.AddressBookDatabase
import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.mangala.wallet.utils.DebugLog

class DatabaseInitializer(
    private val database: AddressBookDatabase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider,
) {
    fun initializeDefaultData() {
        try {
            // Check if blockchain types table is empty
            val blockchainCount = database.addressBookDatabaseQueries.getAllBlockchainTypes().executeAsList().size
            
            if (blockchainCount == 0) {
                println("DatabaseInitializer, Initializing default blockchain types...")
                database.addressBookDatabaseQueries.insertDefaultBlockchainTypes()
                if (buildEnvironmentProvider.isDevelopmentEnvironment()) {
                    database.addressBookDatabaseQueries.insertDefaultTestnetBlockchainTypes()
                }
                println("DatabaseInitializer, Default blockchain types initialized successfully")
            }
            
            // Check if tags table is empty
            val tagCount = database.addressBookDatabaseQueries.getActiveTags().executeAsList().size
            
            if (tagCount == 0) {
                println("DatabaseInitializer, Initializing default tags...")
                database.addressBookDatabaseQueries.insertDefaultTags()
                println("DatabaseInitializer, Default tags initialized successfully")
            }
        } catch (e: Exception) {
            println("DatabaseInitializer, Error initializing default data: ${e.message}")
        }
    }
}