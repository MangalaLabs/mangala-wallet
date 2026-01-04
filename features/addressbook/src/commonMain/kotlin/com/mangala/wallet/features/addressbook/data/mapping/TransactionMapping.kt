package com.mangala.wallet.features.addressbook.data.mapping

import com.mangala.antelope.base.api.model.EosAction
import com.mangala.wallet.features.addressbook.data.model.enum.TransactionStatus
import com.mangala.wallet.features.addressbook.data.model.transaction.TransactionHistoryEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.ext.toInstant

fun EosAction.toAddressBookTransactionHistory(
    blockchainType: BlockchainType,
    importedAccountNames: List<String> = emptyList()
) = TransactionHistoryEntity(
    id = "${trxId}_$actionOrdinal",
    fromAddress = this.act?.from ?: "",
    toAddress = this.act?.to ?: "",
    blockchainTypeId = blockchainType.uid,
    amount = this.act?.getDataAmountAsString() ?: "",
    tokenSymbol = this.act?.symbol ?: "",
    transactionHash = this.trxId ?: "",
    status = TransactionStatus.CONFIRMED,
    timestamp = this.timestampSimple.toInstant(),
    fee = this.act?.fee,
    note = this.act?.memo ?: this.act?.message,
    isFromImportedWallet = importedAccountNames.contains(this.act?.from ?: "")
)