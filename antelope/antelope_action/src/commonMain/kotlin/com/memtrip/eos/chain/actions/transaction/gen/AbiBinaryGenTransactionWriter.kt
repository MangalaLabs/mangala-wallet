/*
 * Copyright 2013-present memtrip LTD.
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// ------------------------------------------------------------------
// MODIFICATION NOTICE:
// Modified by Mangala Wallet
// Description: Adapted for Kotlin Multiplatform compatibility.
// ------------------------------------------------------------------

package com.memtrip.eos.chain.actions.transaction.gen

import com.memtrip.eos.abi.writer.ByteWriter
import com.memtrip.eos.abi.writer.bytewriter.DefaultByteWriter
import com.memtrip.eos.abi.writer.compression.CompressionFactory
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.keycert.EncryptedPrivateKeyArgs
import com.memtrip.eos.chain.actions.keycert.KeyCertArgs
import com.memtrip.eos.chain.actions.powerup.PowerUpArgs
import com.memtrip.eos.chain.actions.powerup.PowerUpBody
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.ProposalActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.ProposalTransactionAbi
import com.memtrip.eos.chain.actions.transaction.abi.Signed2TransactionAbi
import com.memtrip.eos.chain.actions.transaction.abi.SignedTransactionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopeActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamBody
import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamBytesArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamBytesBody
import com.memtrip.eos.chain.actions.transaction.account.actions.delegatebw.DelegateBandwidthArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.delegatebw.DelegateBandwidthBody
import com.memtrip.eos.chain.actions.transaction.account.actions.multisigs.approve.ApproveProposalActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.multisigs.cancel.CancelProposalActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.multisigs.createpropose.CreateProposeAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.multisigs.exec.ExecuteProposalActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.AccountKeyAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.AccountRequiredAuthAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.NewAccountArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.NewAccountBody
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.AccountRequiredAuthAccountAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.AccountRequiredAuthWaitAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.AuthDataBody
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.DeletePermissionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.LinkAuthAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.RequiredAuthAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.UnLinkAuthAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.ramtransfer.RamTransferArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.refund.RefundArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.refund.RefundBody
import com.memtrip.eos.chain.actions.transaction.account.actions.rentviarex.DepositRexArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.rentviarex.DepositRexBody
import com.memtrip.eos.chain.actions.transaction.account.actions.rentviarex.RentViaRexArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.rentviarex.RentViaRexBody
import com.memtrip.eos.chain.actions.transaction.account.actions.sellram.SellRamArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.sellram.SellRamBody
import com.memtrip.eos.chain.actions.transaction.account.actions.undelegatebw.UnDelegateBandwidthArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.undelegatebw.UnDelegateBandwidthBody
import com.memtrip.eos.chain.actions.transaction.esr.EsrIdentityBody
import com.memtrip.eos.chain.actions.transaction.gen.mulsigs.ApproveProposalAbiSquishable
import com.memtrip.eos.chain.actions.transaction.gen.mulsigs.CancelProposalAbiSquishable
import com.memtrip.eos.chain.actions.transaction.gen.mulsigs.CreateProposeAbiSquishable
import com.memtrip.eos.chain.actions.transaction.gen.mulsigs.ExecuteProposalAbiSquishable
import com.memtrip.eos.chain.actions.transaction.gen.permission.PermissionAbiSquishable
import com.memtrip.eos.chain.actions.transaction.gen.permission.WaitArgsSquishable
import com.memtrip.eos.chain.actions.transaction.gen.ramgift.GiftRamActionAbiSquishable
import com.memtrip.eos.chain.actions.transaction.ramgift.GiftRamActionAbi
import com.memtrip.eos.chain.actions.transaction.transfer.actions.TransferArgs
import com.memtrip.eos.chain.actions.transaction.transfer.actions.TransferBody
import com.memtrip.eos.chain.actions.transaction.vote.actions.VoteArgs
import com.memtrip.eos.chain.actions.transaction.vote.actions.VoteBody
import com.memtrip.eos.core.hex.DefaultHexWriter
import com.memtrip.eos.core.hex.HexWriter

class AbiBinaryGenTransactionWriter(
    private val byteWriter: ByteWriter,
    private val hexWriter: HexWriter,
    private val compressionType: CompressionType?
) {
    private val voteargsSquishable: VoteArgsSquishable
    private val votebodySquishable: VoteBodySquishable
    private val transferargsSquishable: TransferArgsSquishable
    private val transferbodySquishable: TransferBodySquishable
    private val transactionauthorizationabiSquishable: TransactionAuthorizationAbiSquishable
    private val signedtransactionabiSquishable: SignedTransactionAbiSquishable
    private val actionabiSquishable: ActionAbiSquishable
    private val transactionabiSquishable: TransactionAbiSquishable
    private val delegatebandwidthbodySquishable: DelegateBandwidthBodySquishable
    private val delegatebandwidthargsSquishable: DelegateBandwidthArgsSquishable
    private val newaccountargsSquishable: NewAccountArgsSquishable
    private val accountrequiredauthabiSquishable: AccountRequiredAuthAbiSquishable
    private val newaccountbodySquishable: NewAccountBodySquishable
    private val accountkeyabiSquishable: AccountKeyAbiSquishable
    private val buyrambodySquishable: BuyRamBodySquishable
    private val buyrambytesbodySquishable: BuyRamBytesBodySquishable
    private val buyramargsSquishable: BuyRamArgsSquishable
    private val buyrambytesargsSquishable: BuyRamBytesArgsSquishable
    private val sellramargsSquishable: SellRamArgsSquishable
    private val sellrambodySquishable: SellRamBodySquishable
    private val ramtransferargsSquishable: RamTransferArgsSquishable
    private val undelegatebandwidthbodySquishable: UnDelegateBandwidthBodySquishable
    private val undelegatebandwidthargsSquishable: UnDelegateBandwidthArgsSquishable
    private val refundbodySquishable: RefundBodySquishable
    private val refundargsSquishable: RefundArgsSquishable

    private val permissionAbiSquishable: PermissionAbiSquishable
    private val waitArgsSquishable: WaitArgsSquishable

    private val powerUpBodySquishable: PowerUpBodySquishable
    private val powerUpArgsSquishable: PowerUpArgsSquishable
    private val depositRexArgsSquishable: DepositRexArgsSquishable
    private val depositRexBodySquishable: DepositRexBodySquishable
    private val rentViaRexArgsSquishable: RentViaRexArgsSquishable
    private val rentViaRexBodySquishable: RentViaRexBodySquishable

    private val esrIdentityBodySquishable: EsrIdentityBodySquishable

    private val keyCertArgsSquishable: KeyCertArgsSquishable
    private val encryptedPrivateKeyArgsSquishable: EncryptedPrivateKeyArgsSquishable

    private val createProposeAbiSquishable: CreateProposeAbiSquishable
    private val proposalActionAbiSquishable: ProposalActionAbiSquishable
    private val proposalTransactionAbiSquishable: ProposalTransactionAbiSquishable
    private val signed2TransactionabiSquishable: Signed2TransactionAbiSquishable
    private val approveProposalAbiSquishable: ApproveProposalAbiSquishable
    private val cancelProposalAbiSquishable: CancelProposalAbiSquishable
    private val executeProposalAbiSquishable: ExecuteProposalAbiSquishable
    private val antelopeActionAbiSquishable: AntelopeActionAbiSquishable
    private val giftRamActionAbiSquishable: GiftRamActionAbiSquishable

    constructor(compressionType: CompressionType?) : this(
        DefaultByteWriter(),
        DefaultHexWriter(),
        compressionType
    )

    init {
        voteargsSquishable = VoteArgsSquishable(this)
        votebodySquishable = VoteBodySquishable(this)
        transferargsSquishable = TransferArgsSquishable(this)
        transferbodySquishable = TransferBodySquishable(this)
        transactionauthorizationabiSquishable = TransactionAuthorizationAbiSquishable(this)
        signedtransactionabiSquishable = SignedTransactionAbiSquishable(this)
        actionabiSquishable = ActionAbiSquishable(this)
        transactionabiSquishable = TransactionAbiSquishable(this)
        delegatebandwidthbodySquishable = DelegateBandwidthBodySquishable(this)
        delegatebandwidthargsSquishable = DelegateBandwidthArgsSquishable(this)
        newaccountargsSquishable = NewAccountArgsSquishable(this)
        accountrequiredauthabiSquishable = AccountRequiredAuthAbiSquishable(this)
        newaccountbodySquishable = NewAccountBodySquishable(this)
        accountkeyabiSquishable = AccountKeyAbiSquishable(this)
        buyrambodySquishable = BuyRamBodySquishable(this)
        buyrambytesbodySquishable = BuyRamBytesBodySquishable(this)
        buyramargsSquishable = BuyRamArgsSquishable(this)
        buyrambytesargsSquishable = BuyRamBytesArgsSquishable(this)
        sellramargsSquishable = SellRamArgsSquishable(this)
        sellrambodySquishable = SellRamBodySquishable(this)
        ramtransferargsSquishable = RamTransferArgsSquishable(this)
        undelegatebandwidthbodySquishable = UnDelegateBandwidthBodySquishable(this)
        undelegatebandwidthargsSquishable = UnDelegateBandwidthArgsSquishable(this)
        refundbodySquishable = RefundBodySquishable(this)
        refundargsSquishable = RefundArgsSquishable(this)

        permissionAbiSquishable = PermissionAbiSquishable(this)
        waitArgsSquishable = WaitArgsSquishable(this)

        powerUpArgsSquishable = PowerUpArgsSquishable(this)
        powerUpBodySquishable = PowerUpBodySquishable(this)
        depositRexArgsSquishable = DepositRexArgsSquishable(this)
        depositRexBodySquishable = DepositRexBodySquishable(this)
        rentViaRexArgsSquishable = RentViaRexArgsSquishable(this)
        rentViaRexBodySquishable = RentViaRexBodySquishable(this)
        esrIdentityBodySquishable = EsrIdentityBodySquishable(this)

        createProposeAbiSquishable = CreateProposeAbiSquishable(this)
        proposalActionAbiSquishable = ProposalActionAbiSquishable(this)
        proposalTransactionAbiSquishable = ProposalTransactionAbiSquishable(this)
        signed2TransactionabiSquishable = Signed2TransactionAbiSquishable(this)
        approveProposalAbiSquishable = ApproveProposalAbiSquishable(this)
        cancelProposalAbiSquishable = CancelProposalAbiSquishable(this)
        executeProposalAbiSquishable = ExecuteProposalAbiSquishable(this)

        keyCertArgsSquishable = KeyCertArgsSquishable(this)
        encryptedPrivateKeyArgsSquishable = EncryptedPrivateKeyArgsSquishable(this)
        antelopeActionAbiSquishable = AntelopeActionAbiSquishable()
        giftRamActionAbiSquishable = GiftRamActionAbiSquishable(this)
    }

    fun toBytes(): ByteArray {
        return CompressionFactory(compressionType!!).create().compress(byteWriter.toBytes())
    }

    fun toHex(): String {
        val compressedBytes = toBytes()
        return hexWriter.bytesToHex(compressedBytes, 0, compressedBytes.size, null)
    }

    fun squishVoteArgs(voteargs: VoteArgs): AbiBinaryGenTransactionWriter {
        voteargsSquishable.squish(voteargs, byteWriter)
        return this
    }

    fun squishCollectionVoteArgs(voteargsList: List<VoteArgs>, byteWriter: ByteWriter) {
        byteWriter.putVariableUInt(voteargsList.size.toLong())
        for (voteargs in voteargsList) {
            voteargsSquishable.squish(voteargs, byteWriter)
        }
    }

    fun squishAuthDataBody(authDataBody: AuthDataBody): AbiBinaryGenTransactionWriter {
        byteWriter.putAccountName(authDataBody.account)
        byteWriter.putName(authDataBody.permission)
        byteWriter.putName(authDataBody.parent)
        squishAuthArg(authDataBody.auth, byteWriter)
        return this
    }

    private fun squishAuthArg(
        updateAuthAbi: RequiredAuthAbi,
        byteWriter: ByteWriter,
    ): AbiBinaryGenTransactionWriter {
        byteWriter.putInt(updateAuthAbi.threshold)
        squishCollectionAccountKeyAbi(updateAuthAbi.keys, byteWriter)
        squishCollectionAccountPermission(updateAuthAbi.accounts, byteWriter)
        squishCollectionWaitArgs(updateAuthAbi.waits, byteWriter)
        return this
    }

    fun squishDeletePermissionAbi(deletePermissionAbi: DeletePermissionAbi): AbiBinaryGenTransactionWriter {
        byteWriter.putAccountName(deletePermissionAbi.account)
        byteWriter.putName(deletePermissionAbi.permission)
        return this
    }

    fun squishVoteBody(votebody: VoteBody): AbiBinaryGenTransactionWriter {
        votebodySquishable.squish(votebody, byteWriter)
        return this
    }

    fun squishCollectionVoteBody(votebodyList: List<VoteBody>, byteWriter: ByteWriter) {
        byteWriter.putVariableUInt(votebodyList.size.toLong())
        for (votebody in votebodyList) {
            votebodySquishable.squish(votebody, byteWriter)
        }
    }

    fun squishTransferArgs(transferargs: TransferArgs): AbiBinaryGenTransactionWriter {
        transferargsSquishable.squish(transferargs, byteWriter)
        return this
    }

    fun squishCollectionTransferArgs(
        transferargsList: List<TransferArgs>,
        byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(transferargsList.size.toLong())
        for (transferargs in transferargsList) {
            transferargsSquishable.squish(transferargs, byteWriter)
        }
    }

    fun squishTransferBody(transferbody: TransferBody): AbiBinaryGenTransactionWriter {
        transferbodySquishable.squish(transferbody, byteWriter)
        return this
    }

    fun squishCollectionTransferBody(
        transferbodyList: List<TransferBody>,
        byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(transferbodyList.size.toLong())
        for (transferbody in transferbodyList) {
            transferbodySquishable.squish(transferbody, byteWriter)
        }
    }

    fun squishTransactionAuthorizationAbi(
        transactionauthorizationabi: TransactionAuthorizationAbi
    ): AbiBinaryGenTransactionWriter {
        transactionauthorizationabiSquishable.squish(transactionauthorizationabi, byteWriter)
        return this
    }

    fun squishTransactionAuthorizationAbiNullable(
        transactionauthorizationabi: TransactionAuthorizationAbi?
    ): AbiBinaryGenTransactionWriter {
        val presenceFlag = if (transactionauthorizationabi == null) 0 else 1
        byteWriter.putByte(presenceFlag.toByte())

        if (transactionauthorizationabi != null) {
            transactionauthorizationabiSquishable.squish(transactionauthorizationabi, byteWriter)
        }

        return this
    }

    fun squishCollectionTransactionAuthorizationAbi(
        transactionauthorizationabiList: List<TransactionAuthorizationAbi>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(transactionauthorizationabiList.size.toLong())
        for (transactionauthorizationabi in transactionauthorizationabiList) {
            transactionauthorizationabiSquishable.squish(transactionauthorizationabi, byteWriter)
        }
    }

    fun squishSignedTransactionAbi(
        signedtransactionabi: SignedTransactionAbi
    ): AbiBinaryGenTransactionWriter {
        signedtransactionabiSquishable.squish(signedtransactionabi, byteWriter)
        return this
    }

    fun squishSigned2TransactionAbi(
        signedtransactionabi: Signed2TransactionAbi,
    ): AbiBinaryGenTransactionWriter {
        signed2TransactionabiSquishable.squish(signedtransactionabi, byteWriter)
        return this
    }

    fun squishCollectionProposalActionAbi(
        proposalActionsAbi: List<ProposalActionAbi>,
        byteWriter: ByteWriter,
    ) {
        byteWriter.putVariableUInt(proposalActionsAbi.size.toLong())
        for (proposalActionAbi in proposalActionsAbi) {
            proposalActionAbiSquishable.squish(proposalActionAbi, byteWriter)
        }
    }

    fun squishCollectionSignedTransactionAbi(
        signedtransactionabiList: List<SignedTransactionAbi>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(signedtransactionabiList.size.toLong())
        for (signedtransactionabi in signedtransactionabiList) {
            signedtransactionabiSquishable.squish(signedtransactionabi, byteWriter)
        }
    }

    fun squishActionAbi(actionabi: ActionAbi): AbiBinaryGenTransactionWriter {
        actionabiSquishable.squish(actionabi, byteWriter)
        return this
    }

    fun squishCollectionActionAbi(actionabiList: List<ActionAbi>, byteWriter: ByteWriter) {
        byteWriter.putVariableUInt(actionabiList.size.toLong())
        for (actionabi in actionabiList) {
            actionabiSquishable.squish(actionabi, byteWriter)
        }
    }

    fun squishTransactionAbi(transactionabi: TransactionAbi): AbiBinaryGenTransactionWriter {
        transactionabiSquishable.squish(transactionabi, byteWriter)
        return this
    }

    fun squishProposalTransactionAbi(proposalTransactionAbi: ProposalTransactionAbi): AbiBinaryGenTransactionWriter {
        proposalTransactionAbiSquishable.squish(proposalTransactionAbi, byteWriter)
        return this
    }

    fun squishCollectionTransactionAbi(
        transactionabiList: List<TransactionAbi>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(transactionabiList.size.toLong())
        for (transactionabi in transactionabiList) {
            transactionabiSquishable.squish(transactionabi, byteWriter)
        }
    }

    fun squishDelegateBandwidthBody(
        delegatebandwidthbody: DelegateBandwidthBody
    ): AbiBinaryGenTransactionWriter {
        delegatebandwidthbodySquishable.squish(delegatebandwidthbody, byteWriter)
        return this
    }

    fun squishDepositRexBody(depositRexBody: DepositRexBody): AbiBinaryGenTransactionWriter {
        depositRexBodySquishable.squish(depositRexBody, byteWriter)
        return this
    }

    fun squishRentViaRexBody(rentViaRexBody: RentViaRexBody): AbiBinaryGenTransactionWriter {
        rentViaRexBodySquishable.squish(rentViaRexBody, byteWriter)
        return this
    }

    fun squishIdentityBody(esrIdentityBody: EsrIdentityBody): AbiBinaryGenTransactionWriter {
        esrIdentityBodySquishable.squish(esrIdentityBody, byteWriter)
        return this
    }

    fun squishKeyCertArgsSquishable(keyCertArgs: KeyCertArgs): AbiBinaryGenTransactionWriter {
        keyCertArgsSquishable.squish(keyCertArgs, byteWriter)
        return this
    }

    fun squishEncryptedPrivateKeyArgsSquishable(encryptedPrivateKeyArgs: EncryptedPrivateKeyArgs): AbiBinaryGenTransactionWriter {
        encryptedPrivateKeyArgsSquishable.squish(encryptedPrivateKeyArgs, byteWriter)
        return this
    }

    fun squishCollectionDelegateBandwidthBody(
        delegatebandwidthbodyList: List<DelegateBandwidthBody>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(delegatebandwidthbodyList.size.toLong())
        for (delegatebandwidthbody in delegatebandwidthbodyList) {
            delegatebandwidthbodySquishable.squish(delegatebandwidthbody, byteWriter)
        }
    }

    fun squishDelegateBandwidthArgs(
        delegatebandwidthargs: DelegateBandwidthArgs
    ): AbiBinaryGenTransactionWriter {
        delegatebandwidthargsSquishable.squish(delegatebandwidthargs, byteWriter)
        return this
    }

    fun squishDepositRexArgs(depositRexArgs: DepositRexArgs): AbiBinaryGenTransactionWriter {
        depositRexArgsSquishable.squish(depositRexArgs, byteWriter)
        return this
    }

    fun squishRentViaRexArgs(rentViaRexArgs: RentViaRexArgs): AbiBinaryGenTransactionWriter {
        rentViaRexArgsSquishable.squish(rentViaRexArgs, byteWriter)
        return this
    }

    fun squishCollectionDelegateBandwidthArgs(
        delegatebandwidthargsList: List<DelegateBandwidthArgs>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(delegatebandwidthargsList.size.toLong())
        for (delegatebandwidthargs in delegatebandwidthargsList) {
            delegatebandwidthargsSquishable.squish(delegatebandwidthargs, byteWriter)
        }
    }

    fun squishNewAccountArgs(newaccountargs: NewAccountArgs): AbiBinaryGenTransactionWriter {
        newaccountargsSquishable.squish(newaccountargs, byteWriter)
        return this
    }

    fun squishCollectionNewAccountArgs(
        newaccountargsList: List<NewAccountArgs>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(newaccountargsList.size.toLong())
        for (newaccountargs in newaccountargsList) {
            newaccountargsSquishable.squish(newaccountargs, byteWriter)
        }
    }

    fun squishAccountRequiredAuthAbi(
        accountrequiredauthabi: AccountRequiredAuthAbi
    ): AbiBinaryGenTransactionWriter {
        accountrequiredauthabiSquishable.squish(accountrequiredauthabi, byteWriter)
        return this
    }

    fun squishCollectionAccountRequiredAuthAbi(
        accountrequiredauthabiList: List<AccountRequiredAuthAbi>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(accountrequiredauthabiList.size.toLong())
        for (accountrequiredauthabi in accountrequiredauthabiList) {
            accountrequiredauthabiSquishable.squish(accountrequiredauthabi, byteWriter)
        }
    }

    fun squishNewAccountBody(newaccountbody: NewAccountBody): AbiBinaryGenTransactionWriter {
        newaccountbodySquishable.squish(newaccountbody, byteWriter)
        return this
    }

    fun squishCollectionNewAccountBody(
        newaccountbodyList: List<NewAccountBody>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(newaccountbodyList.size.toLong())
        for (newaccountbody in newaccountbodyList) {
            newaccountbodySquishable.squish(newaccountbody, byteWriter)
        }
    }

    fun squishAccountKeyAbi(accountkeyabi: AccountKeyAbi): AbiBinaryGenTransactionWriter {
        accountkeyabiSquishable.squish(accountkeyabi, byteWriter)
        return this
    }

    fun squishCollectionAccountKeyAbi(
        accountkeyabiList: List<AccountKeyAbi>,
        byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(accountkeyabiList.size.toLong())
        for (accountkeyabi in accountkeyabiList) {
            accountkeyabiSquishable.squish(accountkeyabi, byteWriter)
        }
    }

    fun squishBuyRamBody(buyrambody: BuyRamBody): AbiBinaryGenTransactionWriter {
        buyrambodySquishable.squish(buyrambody, byteWriter)
        return this
    }

    fun squishCollectionBuyRamBody(buyrambodyList: List<BuyRamBody>, byteWriter: ByteWriter) {
        byteWriter.putVariableUInt(buyrambodyList.size.toLong())
        for (buyrambody in buyrambodyList) {
            buyrambodySquishable.squish(buyrambody, byteWriter)
        }
    }

    fun squishBuyRamBytesBody(buyrambytesbody: BuyRamBytesBody): AbiBinaryGenTransactionWriter {
        buyrambytesbodySquishable.squish(buyrambytesbody, byteWriter)
        return this
    }

    fun squishPowerUpArgs(powerUpArgs: PowerUpArgs): AbiBinaryGenTransactionWriter {
        powerUpArgsSquishable.squish(powerUpArgs, byteWriter)
        return this
    }
    fun squishPowerUpBody(powerUpBody: PowerUpBody): AbiBinaryGenTransactionWriter {
        powerUpBodySquishable.squish(powerUpBody, byteWriter)
        return this
    }

    fun squishCollectionBuyRamBytesBody(
        buyrambytesbodyList: List<BuyRamBytesBody>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(buyrambytesbodyList.size.toLong())
        for (buyrambytesbody in buyrambytesbodyList) {
            buyrambytesbodySquishable.squish(buyrambytesbody, byteWriter)
        }
    }

    fun squishBuyRamArgs(buyramargs: BuyRamArgs): AbiBinaryGenTransactionWriter {
        buyramargsSquishable.squish(buyramargs, byteWriter)
        return this
    }

    fun squishCollectionBuyRamArgs(buyramargsList: List<BuyRamArgs>, byteWriter: ByteWriter) {
        byteWriter.putVariableUInt(buyramargsList.size.toLong())
        for (buyramargs in buyramargsList) {
            buyramargsSquishable.squish(buyramargs, byteWriter)
        }
    }

    fun squishBuyRamBytesArgs(buyrambytesargs: BuyRamBytesArgs): AbiBinaryGenTransactionWriter {
        buyrambytesargsSquishable.squish(buyrambytesargs, byteWriter)
        return this
    }

    fun squishRamTransferArgs(ramtransferargs: RamTransferArgs): AbiBinaryGenTransactionWriter {
        ramtransferargsSquishable.squish(ramtransferargs, byteWriter)
        return this
    }

    fun squishCollectionBuyRamBytesArgs(
        buyrambytesargsList: List<BuyRamBytesArgs>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(buyrambytesargsList.size.toLong())
        for (buyrambytesargs in buyrambytesargsList) {
            buyrambytesargsSquishable.squish(buyrambytesargs, byteWriter)
        }
    }

    fun squishSellRamArgs(sellramargs: SellRamArgs): AbiBinaryGenTransactionWriter {
        sellramargsSquishable.squish(sellramargs, byteWriter)
        return this
    }

    fun squishCollectionSellRamArgs(sellramargsList: List<SellRamArgs>, byteWriter: ByteWriter) {
        byteWriter.putVariableUInt(sellramargsList.size.toLong())
        for (sellramargs in sellramargsList) {
            sellramargsSquishable.squish(sellramargs, byteWriter)
        }
    }

    fun squishSellRamBody(sellrambody: SellRamBody): AbiBinaryGenTransactionWriter {
        sellrambodySquishable.squish(sellrambody, byteWriter)
        return this
    }

    fun squishCollectionSellRamBody(sellrambodyList: List<SellRamBody>, byteWriter: ByteWriter) {
        byteWriter.putVariableUInt(sellrambodyList.size.toLong())
        for (sellrambody in sellrambodyList) {
            sellrambodySquishable.squish(sellrambody, byteWriter)
        }
    }

    fun squishUnDelegateBandwidthBody(
        undelegatebandwidthbody: UnDelegateBandwidthBody
    ): AbiBinaryGenTransactionWriter {
        undelegatebandwidthbodySquishable.squish(undelegatebandwidthbody, byteWriter)
        return this
    }

    fun squishCollectionUnDelegateBandwidthBody(
        undelegatebandwidthbodyList: List<UnDelegateBandwidthBody>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(undelegatebandwidthbodyList.size.toLong())
        for (undelegatebandwidthbody in undelegatebandwidthbodyList) {
            undelegatebandwidthbodySquishable.squish(undelegatebandwidthbody, byteWriter)
        }
    }

    fun squishUnDelegateBandwidthArgs(
        undelegatebandwidthargs: UnDelegateBandwidthArgs
    ): AbiBinaryGenTransactionWriter {
        undelegatebandwidthargsSquishable.squish(undelegatebandwidthargs, byteWriter)
        return this
    }

    fun squishCollectionUnDelegateBandwidthArgs(
        undelegatebandwidthargsList: List<UnDelegateBandwidthArgs>, byteWriter: ByteWriter
    ) {
        byteWriter.putVariableUInt(undelegatebandwidthargsList.size.toLong())
        for (undelegatebandwidthargs in undelegatebandwidthargsList) {
            undelegatebandwidthargsSquishable.squish(undelegatebandwidthargs, byteWriter)
        }
    }

    fun squishRefundBody(refundbody: RefundBody): AbiBinaryGenTransactionWriter {
        refundbodySquishable.squish(refundbody, byteWriter)
        return this
    }

    fun squishCollectionRefundBody(refundbodyList: List<RefundBody>, byteWriter: ByteWriter) {
        byteWriter.putVariableUInt(refundbodyList.size.toLong())
        for (refundbody in refundbodyList) {
            refundbodySquishable.squish(refundbody, byteWriter)
        }
    }

    fun squishRefundArgs(refundargs: RefundArgs): AbiBinaryGenTransactionWriter {
        refundargsSquishable.squish(refundargs, byteWriter)
        return this
    }

    fun squishCollectionRefundArgs(refundargsList: List<RefundArgs>, byteWriter: ByteWriter) {
        byteWriter.putVariableUInt(refundargsList.size.toLong())
        for (refundargs in refundargsList) {
            refundargsSquishable.squish(refundargs, byteWriter)
        }
    }

    fun squishLinkAuthAbi(linkAuthAbi: LinkAuthAbi): AbiBinaryGenTransactionWriter {
        byteWriter.putAccountName(linkAuthAbi.account)
        byteWriter.putName(linkAuthAbi.code)
        byteWriter.putName(linkAuthAbi.type)
        byteWriter.putName(linkAuthAbi.requirement)
        return this
    }

    fun squishUnLinkAuthAbi(unLinkAuthAbi: UnLinkAuthAbi): AbiBinaryGenTransactionWriter {
        byteWriter.putAccountName(unLinkAuthAbi.account)
        byteWriter.putName(unLinkAuthAbi.code)
        byteWriter.putName(unLinkAuthAbi.type)
        return this
    }

    fun squishCreateProposeAbi(createProposeAbi: CreateProposeAbi): AbiBinaryGenTransactionWriter {
        createProposeAbiSquishable.squish(createProposeAbi, byteWriter)
        return this
    }

    fun squishApproveProposalAbi(approveProposalAbi: ApproveProposalActionAbi): AbiBinaryGenTransactionWriter {
        approveProposalAbiSquishable.squish(approveProposalAbi, byteWriter)
        return this
    }


    fun squishCancelProposalAbi(cancelProposalAbi: CancelProposalActionAbi): AbiBinaryGenTransactionWriter {
        cancelProposalAbiSquishable.squish(cancelProposalAbi, byteWriter)
        return this
    }

    fun squishExecuteProposalAbi(executeProposalAbi: ExecuteProposalActionAbi): AbiBinaryGenTransactionWriter {
        executeProposalAbiSquishable.squish(executeProposalAbi, byteWriter)
        return this
    }

    private fun squishCollectionAccountPermission(
        accountPermissionList: List<AccountRequiredAuthAccountAbi>,
        byteWriter: ByteWriter,
    ) {
        byteWriter.putVariableUInt(accountPermissionList.size.toLong())
        for (accountPermission in accountPermissionList) {
            squishAccountPermission(accountPermission, byteWriter)
        }
    }

    private fun squishCollectionWaitArgs(
        waitArgsList: List<AccountRequiredAuthWaitAbi>,
        byteWriter: ByteWriter,
    ) {
        byteWriter.putVariableUInt(waitArgsList.size.toLong())
        for (waitArgs in waitArgsList) {
            waitArgsSquishable.squish(waitArgs, byteWriter)
        }
    }

    private fun squishAccountPermission(
        accountPermission: AccountRequiredAuthAccountAbi,
        byteWriter: ByteWriter,
    ) {
        permissionAbiSquishable.squish(accountPermission.permission, byteWriter)
        byteWriter.putShort(accountPermission.weight.toShort())
    }


    fun squishAntelopeActionAbi(abiList: List<AntelopeActionAbi>): AbiBinaryGenTransactionWriter {
        antelopeActionAbiSquishable.squish(abiList, byteWriter)
        return this
    }

    fun squishGiftRamActionAbi(giftRamActionAbi: GiftRamActionAbi): AbiBinaryGenTransactionWriter {
        giftRamActionAbiSquishable.squish(giftRamActionAbi, byteWriter)
        return this
    }
}