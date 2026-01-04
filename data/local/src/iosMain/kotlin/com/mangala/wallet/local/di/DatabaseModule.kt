package com.mangala.wallet.local.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import com.mangala.wallet.database.MangalaWalletDatabase
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.local.cache.metadataTargetCacheEntityAdapter
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperImpl
import com.mangala.wallet.mokoresources.MR
import commangalawalletdatabase.TransactionMetadataEntity
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import org.koin.core.module.single
import org.koin.dsl.module
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringByAppendingPathComponent

@OptIn(ExperimentalForeignApi::class)
actual fun databaseModule() = module(createdAtStart = true) {
    single {
        val databaseName = "mangalawallet.db"
        val fileManager = NSFileManager.defaultManager
        val documentsPath = NSSearchPathForDirectoriesInDomains(
            directory = NSApplicationSupportDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true
        ).first() as NSString

        val dbDirectoryPath = documentsPath.stringByAppendingPathComponent("databases")
        val targetDbPath = documentsPath.stringByAppendingPathComponent("databases/$databaseName")
        val sourceDbPath = MR.files.mangalawallet_db.path

        val directoryExists = fileManager.fileExistsAtPath(dbDirectoryPath)
        val databaseExists = fileManager.fileExistsAtPath(targetDbPath)

        if (databaseExists.not()) {
            memScoped {
                // you can use it to log errors
                val error: ObjCObjectVar<NSError?> = alloc()

                if (directoryExists.not()) {
                    val createSuccess = fileManager.createDirectoryAtPath(
                        path = dbDirectoryPath,
                        withIntermediateDirectories = true,
                        attributes = null,
                        error = error.ptr
                    )
                    println("iOS database create success? $createSuccess")
                }

                val copySuccess = fileManager.copyItemAtPath(
                    srcPath = sourceDbPath,
                    toPath = targetDbPath,
                    error = error.ptr
                )
                println("iOS database copy success? $copySuccess error? $error")
            }
        }

        val driver = NativeSqliteDriver(
            MangalaWalletDatabase.Schema,
            databaseName,
            onConfiguration = { configuration ->
                configuration.copy(
                    extendedConfig = DatabaseConfiguration.Extended(
                        foreignKeyConstraints = true
                    )
                )
            }
        )
        MangalaWalletDatabaseWrapper(MangalaWalletDatabase(driver))
    }

    single<SecureStorageWrapper> {
        SecureStorageWrapperImpl()
    }
}