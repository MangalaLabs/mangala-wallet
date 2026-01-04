package com.mangala.wallet.features.chains.evmcompatible.domain

import cafe.adriel.voyager.core.concurrent.AtomicInt32
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.token.usecases.ScanTokenByChainNetworkUseCase
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.repository.TransactionRepository
import com.mangala.wallet.features.chains.BlockSyncer
import com.mangala.wallet.features.chains.BlockSyncerPlugin
import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Used to update the wallet's balance and transaction history after making transactions
 */
internal class EvmBlockSyncer(
    private val nodeRepository: NodeRepository,
    private val transactionRepository: TransactionRepository,
    private val scanTokenByChainNetworkUseCase: ScanTokenByChainNetworkUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val blockSyncerPlugins: List<BlockSyncerPlugin>
): BlockSyncer {
    private var job: Job? = null
    private var latestBlockNumber: Long = 0
    private val currentId = AtomicInt32(Random.nextInt(100))

    private var url: String = ""
    private var chain: Chain? = null

    /**
     * Regularly polls the blockchain to check the status of pending transactions and updates the wallet's balance if necessary
     */
    override fun startSyncIfNotRunning(url: String, chain: Chain) {
        if (job?.isActive == true && this.url == url && this.chain == chain) {

            return
        }

        this.url = url
        this.chain = chain
        val blockchainType = chain.toBlockchainType()

        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(chain.syncIntervalMillis)
                try {
                    val pendingTransactions = transactionRepository.getPendingTransactions(chain.toBlockchainType().uid)

                    if (pendingTransactions.isEmpty()) return@launch

                    val blockNumber = nodeRepository.getBlockNumber(url, currentId.getAndIncrement())


                    if (blockNumber != null && blockNumber > latestBlockNumber) {
                        latestBlockNumber = blockNumber

                        val pendingTransactionHashes = pendingTransactions.map { it.txHash }
                        val receipts = nodeRepository.getTransactionReceipt(
                            url,
                            currentId.getAndIncrement(),
                            pendingTransactionHashes
                        )


                        val confirmedTransaction = receipts.filter { it.blockNumber != 0L }

                        val (successfulTransactionReceipts, failedTransactionReceipts) = confirmedTransaction.partition { it.status == 1 }

                        val failedTransactionHashes = failedTransactionReceipts.map { it.transactionHash }
                        val successfulTransactionHashes = successfulTransactionReceipts.map { it.transactionHash }
                        val confirmedTransactionHashes = confirmedTransaction.map { it.transactionHash }

                        val updatedTransactions = pendingTransactions.filter { confirmedTransactionHashes.contains(it.txHash) }
                        val successfulTransactions = pendingTransactions.filter { successfulTransactionHashes.contains(it.txHash) }

                        val successfulTransactionsByAccount = successfulTransactions.groupBy { it.accountId }

                        val updatedAccountIds = updatedTransactions.map { it.accountId }.distinct()

                        updatedAccountIds.forEach { accountId ->
                            val account = getAccountByIdUseCase(accountId)
                            val address = account.bip44Address

                            transactionRepository.updateTransactionStatus(accountId, blockchainType.uid, successfulTransactionHashes, TransactionStatus.SUCCESS)
                            transactionRepository.updateTransactionStatus(accountId, blockchainType.uid, failedTransactionHashes, TransactionStatus.FAILED)

                            scanTokenByChainNetworkUseCase(true, address, blockchainType, accountId)

                            blockSyncerPlugins.forEach { plugin ->
                                plugin.onTransactionStatusUpdatedForAccount(
                                    blockchainType,
                                    successfulTransactionsByAccount,
                                    accountId
                                )
                            }
                        }
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    override suspend fun waitUntilTransactionConfirmed(
        blockchainType: BlockchainType,
        txHash: String,
    ): String? {
        val rpcUrl = blockchainType.getRpcUrl().first()
        while (true) {
            delay(Chain.fromBlockchainType(blockchainType).blockIntervalMillis + WAIT_TRANSACTION_CONFIRM_DELAY)
            try {
                val receipt = nodeRepository.getTransactionReceipt(
                    rpcUrl,
                    currentId.getAndIncrement(),
                    txHash
                )

                if (receipt == null || receipt.blockNumber == 0L) {
                    continue
                }
                if (receipt.status == 0) {
                    return null
                } else if (receipt.status == 1) {
                    return txHash
                }
            } catch (e: Exception) {

            }
        }
    }

    override fun stopSync() {
        job?.cancel()
        job = null
    }

    companion object {
        private const val WAIT_TRANSACTION_CONFIRM_DELAY = 2000L // Wait for an additional time in case the block is not created yet
    }
}