package com.mangala.wallet.features.transactionhistory.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.features.transactionhistory.presentation.evm.TransactionHistoryScreen
import com.mangala.wallet.features.transactionhistory.presentation.evm.TransactionHistoryScreenModel
import com.mangala.wallet.features.transactionhistory.presentation.antelope.TransactionHistoryAntelopeScreen
import com.mangala.wallet.features.transactionhistory.presentation.antelope.TransactionHistoryAntelopeScreenModel
import com.mangala.wallet.features.transactionhistory.presentation.antelope.filter.TransactionHistoryFilterAntelopeBottomSheetScreenModel
import com.mangala.wallet.features.transactionhistory.presentation.bitcoin.TransactionHistoryBitcoinScreen
import com.mangala.wallet.features.transactionhistory.presentation.bitcoin.TransactionHistoryBitcoinScreenModel
import com.mangala.wallet.features.transactionhistory.presentation.bitcoin.info.TransactionInfoBitcoinScreen
import com.mangala.wallet.features.transactionhistory.presentation.bitcoin.info.TransactionInfoBitcoinScreenModel
import com.mangala.wallet.features.transactionhistory.presentation.evm.filter.TransactionHistoryFilterBottomSheetScreenModel
import com.mangala.wallet.features.transactionhistory.presentation.evm.info.TransactionInfoScreen
import com.mangala.wallet.features.transactionhistory.presentation.evm.info.TransactionInfoScreenModel
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.SharedScreen
import kotlinx.datetime.Instant
import org.koin.dsl.module

val transactionHistoryModule = module {
    factory { (accountId: String) ->
        TransactionHistoryScreenModel(
            accountId = accountId,
            getTransactionHistoryUseCase = get(),
            getAccountByIdUseCase = get()
        )
    }

    factory { (accountName: String) ->
        TransactionHistoryAntelopeScreenModel(
            accountName = accountName,
            getActionsPagingUseCase = get(),
            getSelectedNetworkUseCase = get()
        )
    }
    
    factory { (bitcoinAddress: String, blockchainType: BlockchainType) ->
        TransactionHistoryBitcoinScreenModel(
            bitcoinAddress = bitcoinAddress,
            blockchainType = blockchainType,
            getTransactionHistoryUseCase = get()
        )
    }
    
    factory { (bitcoinAddress: String, txHash: String) ->
        TransactionInfoBitcoinScreenModel(
            bitcoinAddress = bitcoinAddress,
            txHash = txHash,
            bitcoinTransactionRepository = get(),
            getSelectedNetworkUseCase = get(),
            getBlockchainExplorerLinkUseCase = get(),
            clipboardFactory = get(),
            shareFactory = get()
        )
    }

    factory { (accountId: String, txHash: String) ->
        TransactionInfoScreenModel(
            accountId = accountId,
            txHash = txHash,
            getCurrentCurrencyCodeUseCase = get(),
            getAccountByIdUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getBlockchainExplorerLinkUseCase = get(),
            getTransactionByTxHashUseCase = get(),
            fetchHistoricalTokenPriceUseCase = get(),
            clipboardFactory = get(),
            shareFactory = get(),
            getNativeCoinUseCase = get()
        )
    }

    factory { (typeFilter: TransactionType?, statusFilter: TransactionStatus?, startDateFilter: Instant?, endDateFilter: Instant?) ->
        TransactionHistoryFilterBottomSheetScreenModel(
            typeFilter,
            statusFilter,
            startDateFilter,
            endDateFilter
        )
    }

    factory { (startDateFilter: Instant?, endDateFilter: Instant?) ->
        TransactionHistoryFilterAntelopeBottomSheetScreenModel(startDateFilter, endDateFilter)
    }
}

val transactionHistoryScreenModule = screenModule {
    register<SharedScreen.TransactionHistoryScreen> {
        TransactionHistoryScreen(it.accountId)
    }

    register<SharedScreen.TransactionInfoScreen> {
        TransactionInfoScreen(accountId = it.accountId, txHash = it.txHash)
    }

    register<SharedScreen.TransactionHistoryAntelopeScreen> {
        TransactionHistoryAntelopeScreen(it.accountName)
    }
    
    register<SharedScreen.TransactionHistoryBitcoinScreen> {
        TransactionHistoryBitcoinScreen(it.bitcoinAddress, it.blockchainUid)
    }
    
    register<SharedScreen.TransactionInfoBitcoinScreen> {
        TransactionInfoBitcoinScreen(bitcoinAddress = it.bitcoinAddress, txHash = it.txHash)
    }
}