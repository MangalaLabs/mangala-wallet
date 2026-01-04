package com.mangala.wallet.features.chains.antelope_base.domain.model.transaction

import com.mangala.wallet.features.chains.antelope_base.domain.APPROVE_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.domain.CANCEL_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.domain.CONTRACT_EOSIO_MULTISIG
import com.mangala.wallet.features.chains.antelope_base.domain.EXECUTE_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.domain.FUNCTION_GIFT_RAM
import com.mangala.wallet.features.chains.antelope_base.domain.UN_APPROVE_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.ext.toInstant
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.powerup.PowerUpArgs
import com.memtrip.eos.chain.actions.powerup.PowerUpBody
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamBody
import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamBytesArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.delegatebw.DelegateBandwidthArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.delegatebw.DelegateBandwidthBody
import com.memtrip.eos.chain.actions.transaction.account.actions.multisigs.approve.ApproveLevelAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.multisigs.approve.ApproveProposalActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.multisigs.cancel.CancelProposalActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.multisigs.createpropose.CreateProposeAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.multisigs.exec.ExecuteProposalActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.AccountKeyAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.AccountRequiredAuthAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.NewAccountArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.ramtransfer.RamTransferArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.rentviarex.DepositRexArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.rentviarex.DepositRexBody
import com.memtrip.eos.chain.actions.transaction.account.actions.rentviarex.RentViaRexArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.rentviarex.RentViaRexBody
import com.memtrip.eos.chain.actions.transaction.account.actions.sellram.SellRamArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.sellram.SellRamBody
import com.memtrip.eos.chain.actions.transaction.account.actions.undelegatebw.UnDelegateBandwidthArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.undelegatebw.UnDelegateBandwidthBody
import com.memtrip.eos.chain.actions.transaction.esr.EsrIdentityArgs
import com.memtrip.eos.chain.actions.transaction.esr.EsrIdentityBody
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter
import com.memtrip.eos.chain.actions.transaction.ramgift.GiftRamActionAbi
import com.memtrip.eos.chain.actions.transaction.transfer.actions.TransferArgs
import com.memtrip.eos.core.block.BlockIdDetails
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.serialization.Serializable
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Serializable
data class SignTransactionRequest(
    val chainId: String,
    val expiryTimestamp: Long,
    val headBlockId: String,
    val authorization: List<Authorization>,
    val actions: List<Action>,
    val signTransactionType: SignTransactionType
) {
    @Serializable
    data class Authorization(
        val actor: String,
        val permission: String
    )

    // TODO: this sealed interface can be shared between all variants for an encapsulated version of actions
    @Serializable
    sealed interface Action {
        val account: String
        val name: String
        val authorization: List<Authorization>

        @Serializable
        data class CreateAccount(
            override val authorization: List<Authorization>,
            val accountCreator: String,
            val newAccountName: String,
            val ownerPublicKey: String,
            val activePublicKey: String
        ): Action {
            override val account: String = EOS_SYSTEM_CONTRACT
            override val name: String = "newaccount"

            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishNewAccountArgs(
                        constructNewAccountArgs(
                            accountCreator,
                            newAccountName,
                            ownerPublicKey,
                            activePublicKey
                        )
                    )
                }.toHex()
            }

            private fun constructNewAccountArgs(
                creator: String,
                accountName: String,
                ownerPublicKey: String,
                activePublicKey: String
            ) = NewAccountArgs(
                creator = creator,
                name = accountName,
                owner = AccountRequiredAuthAbi(
                    threshold = 1,
                    keys = listOf(
                        AccountKeyAbi(
                            key = ownerPublicKey,
                            weight = 1
                        )
                    ),
                    accounts = emptyList(),
                    waits = emptyList()
                ),
                active = AccountRequiredAuthAbi(
                    threshold = 1,
                    keys = listOf(
                        AccountKeyAbi(
                            key = activePublicKey,
                            weight = 1
                        )
                    ),
                    accounts = emptyList(),
                    waits = emptyList()
                )
            )
        }
        @Serializable
        data class BuyRamBytes(
            override val authorization: List<Authorization>,
            val payer: String,
            val receiver: String,
            val bytes: Long
        ): Action {
            override val account: String = EOS_SYSTEM_CONTRACT
            override val name: String = "buyrambytes"

            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishBuyRamBytesArgs(BuyRamBytesArgs(payer, receiver, bytes))
                }.toHex()
            }
        }
        @Serializable
        data class BuyRam(
            override val authorization: List<Authorization>,
            val payer: String,
            val receiver: String,
            val quantity: String
        ) : Action {
            override val account: String = EOS_SYSTEM_CONTRACT
            override val name: String = "buyram"

            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishBuyRamBody(
                        BuyRamBody(
                            BuyRamArgs(
                                payer = payer,
                                receiver = receiver,
                                quant = quantity
                            )
                        )
                    )
                }.toHex()
            }
        }
        @Serializable
        data class SellRam(
            override val authorization: List<Authorization>,
            val sellAccount: String,
            val bytes: Long
        ) : Action {
            override val account: String = EOS_SYSTEM_CONTRACT
            override val name: String = "sellram"

            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishSellRamBody(
                        SellRamBody(
                            SellRamArgs(
                                account = sellAccount,
                                bytes = bytes
                            )
                        )
                    )
                }.toHex()
            }
        }
        @Serializable
        data class TransferRam(
            override val authorization: List<Authorization>,
            val from: String,
            val memo: String = "",
            val to: String,
            val bytes: Long
        ): Action {
            override val account: String = EOS_SYSTEM_CONTRACT
            override val name: String = "ramtransfer"

            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishRamTransferArgs(RamTransferArgs(from, memo, to, bytes))
                }.toHex()
            }
        }
        @Serializable
        data class TransferToken(
            override val authorization: List<Authorization>,
            val from: String,
            val to: String,
            val quantity: String,
            val memo: String,
            override val account: String
        ): Action {
            override val name: String = "transfer"

            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishTransferArgs(
                        TransferArgs(
                            from,
                            to,
                            quantity,
                            memo
                        )
                    )
                }.toHex()
            }
        }
        @Serializable
        data class PowerUp(
            override val authorization: List<Authorization>,
            val authorizingAccountName: String,
            val receiver: String,
            val days: Int,
            val netFrac: Long,
            val cpuFrac: Long,
            val maxPayment: String
        ): Action {
            override val account: String = EOS_SYSTEM_CONTRACT
            override val name: String = "powerup"

            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishPowerUpBody(
                        PowerUpBody(
                            PowerUpArgs(
                                authorizingAccountName,
                                receiver,
                                days,
                                netFrac,
                                cpuFrac,
                                maxPayment
                            )
                        )
                    )
                }.toHex()
            }
        }

        @Serializable
        data class DelegateRex(
            override val authorization: List<Authorization>,
            val authorizingAccountName: String,
            val receiver: String,
            val stakeCpuQuantity: String,
            val stakeNetQuantity: String,
            val transfer: Int
        ) : Action {
            override val account: String = EOS_SYSTEM_CONTRACT
            override val name: String = "delegatebw"
            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishDelegateBandwidthBody(
                        DelegateBandwidthBody(
                            DelegateBandwidthArgs(
                                from = authorizingAccountName,
                                receiver = receiver,
                                stake_cpu_quantity = stakeCpuQuantity,
                                stake_net_quantity = stakeNetQuantity,
                                transfer = transfer,
                            )
                        )
                    )
                }.toHex()
            }
        }

        @Serializable
        data class UnDelegateRex(
            override val authorization: List<Authorization>,
            val authorizingAccountName: String,
            val receiver: String,
            val stakeCpuQuantity: String,
            val stakeNetQuantity: String,
        ) : Action {
            override val account: String = EOS_SYSTEM_CONTRACT
            override val name: String = "undelegatebw"
            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishUnDelegateBandwidthBody(
                        UnDelegateBandwidthBody(
                            UnDelegateBandwidthArgs(
                                from = authorizingAccountName,
                                receiver = receiver,
                                stake_cpu_quantity = stakeCpuQuantity,
                                stake_net_quantity = stakeNetQuantity,
                            )
                        )
                    )
                }.toHex()
            }
        }
        @Serializable
        data class DepositRex(
            override val authorization: List<Authorization>,
            val authorizingAccountName: String,
            val amount: String,
        ) : Action {
            override val account: String = VAULTA_SYSTEM_CONTRACT
            override val name: String = "deposit"
            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishDepositRexBody(
                        DepositRexBody(
                            DepositRexArgs(
                                account = authorizingAccountName,
                                amount = amount
                            )
                        )
                    )
                }.toHex()
            }
        }

        @Serializable
        data class RentViaRexCpu(
            override val authorization: List<Authorization>,
            val authorizingAccountName: String,
            val receiver: String,
            val loadAmount: String,
            val loadFund: String,
        ) : Action {
            override val account: String = EOS_SYSTEM_CONTRACT
            override val name: String = "rentcpu"
            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishRentViaRexBody(
                        RentViaRexBody(
                            RentViaRexArgs(
                                from = authorizingAccountName ,
                                receiver = receiver,
                                load_amount = loadAmount,
                                load_fund = loadFund
                            )
                        )
                    )
                }.toHex()
            }
        }
        @Serializable
        data class RentViaRexNet(
            override val authorization: List<Authorization>,
            val authorizingAccountName: String,
            val receiver: String,
            val loadAmount: String,
            val loadFund: String,
        ) : Action {
            override val account: String = EOS_SYSTEM_CONTRACT
            override val name: String = "rentnet"
            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishRentViaRexBody(
                        RentViaRexBody(
                            RentViaRexArgs(
                                from = authorizingAccountName ,
                                receiver = receiver,
                                load_amount = loadAmount,
                                load_fund = loadFund
                            )
                        )
                    )
                }.toHex()
            }
        }

        @Serializable
        data class IdentityProof(
            override val authorization: List<Authorization>,
            val scope: String,
            val authorizingAccountName: String,
            val authorizingPermission: String
        ) : Action {
            override val account: String = ""
            override val name: String = "identity"
            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishIdentityBody(
                        EsrIdentityBody(
                            args = EsrIdentityArgs(
                                scope = scope,
                                permission = TransactionAuthorizationAbi(
                                    actor = authorizingAccountName,
                                    permission = authorizingPermission
                                )
                            )

                        )
                    )
                }.toHex()
            }
        }

        @Serializable
        data class CreateProposal(
            override val authorization: List<Authorization>,
            val proposerAccountName: String,
            val proposalName: String,
            val requestedPermissions: List<TransactionAuthorizationAbi>,
            val accountPermissionExecuted: String,
            val actions:  List<ActionAbi>,
            val blockNum: Int,
            val blockPrefix: Long,
            val expiryTimestamp: Long
        ) : Action {
            override val account: String = "eosio.msig"
            override val name: String = "propose"
            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishCreateProposeAbi(
                        CreateProposeAbi(
                            proposerAccountName,
                            proposalName,
                            requestedPermissions,
                            createProposeTrx(
                                blockNum,
                                blockPrefix,
                                actions,
                                expiryTimestamp
                            )
                        )
                    )
                }.toHex()
            }
        }
        @Serializable
        data class ExecuteProposal(
            override val authorization: List<Authorization>,
            val proposerAccountName: String,
            val proposalName: String,
            val accountPermissionExecuted: String,
            val accountNameExecuted: String,
            val blockNum: Int,
            val blockPrefix: Long
        ) : Action {
            override val account: String = CONTRACT_EOSIO_MULTISIG
            override val name: String = EXECUTE_PROPOSAL_ACTION
            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishExecuteProposalAbi(
                        ExecuteProposalActionAbi(
                            proposerAccountName,
                            proposalName,
                            accountNameExecuted
                        )
                    )
                }.toHex()
            }
        }

        @Serializable
        data class ApproveProposal(
            override val authorization: List<Authorization>,
            val proposerAccountName: String,
            val proposalName: String,
            val accountPermissionExecuted: String,
            val accountNameExecuted: String,
            val blockNum: Int,
            val blockPrefix: Long
        ) : Action {
            override val account: String = CONTRACT_EOSIO_MULTISIG
            override val name: String = APPROVE_PROPOSAL_ACTION
            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishApproveProposalAbi(
                        ApproveProposalActionAbi(
                            proposerAccountName,
                            proposalName,
                            ApproveLevelAbi(
                                accountNameExecuted,
                                accountPermissionExecuted
                            )
                        )
                    )
                }.toHex()
            }
        }

        @Serializable
        data class UnApproveProposal(
            override val authorization: List<Authorization>,
            val proposerAccountName: String,
            val proposalName: String,
            val accountPermissionExecuted: String,
            val accountNameExecuted: String,
            val blockNum: Int,
            val blockPrefix: Long
        ) : Action {
            override val account: String = CONTRACT_EOSIO_MULTISIG
            override val name: String = UN_APPROVE_PROPOSAL_ACTION
            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishApproveProposalAbi(
                        ApproveProposalActionAbi(
                            proposerAccountName,
                            proposalName,
                            ApproveLevelAbi(
                                accountNameExecuted,
                                accountPermissionExecuted
                            )
                        )
                    )
                }.toHex()
            }
        }

        @Serializable
        data class CancelProposal(
            override val authorization: List<Authorization>,
            val proposerAccountName: String,
            val proposalName: String,
            val accountPermissionExecuted: String,
            val accountNameExecuted: String,
            val blockNum: Int,
            val blockPrefix: Long
        ) : Action {
            override val account: String = CONTRACT_EOSIO_MULTISIG
            override val name: String = CANCEL_PROPOSAL_ACTION
            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishCancelProposalAbi(
                        CancelProposalActionAbi(
                            proposerAccountName,
                            proposalName,
                            accountNameExecuted
                        )
                    )
                }.toHex()
            }
        }

        @Serializable
        data class GiftRam(
            override val authorization: List<Authorization>,
            val from: String,
            val memo: String = "",
            val to: String,
            val ramBytes: Long
        ): Action {
            override val account: String = EOS_SYSTEM_CONTRACT
            override val name: String = FUNCTION_GIFT_RAM

            override fun getActionData(): String {
                return getTransactionWriter().apply {
                    squishGiftRamActionAbi(GiftRamActionAbi(from, to, ramBytes, memo))
                }.toHex()
            }
        }

        fun toActionAbi(): ActionAbi {
            return ActionAbi(
                account = account,
                name = name,
                authorization = authorization.map { TransactionAuthorizationAbi(it.actor, it.permission) },
                data = getActionData()
            )
        }
        fun getTransactionWriter(): AbiBinaryGenTransactionWriter {
            return AbiBinaryGenTransactionWriter(CompressionType.NONE)
        }
        fun getActionData(): String?

        fun createProposeTrx(
            blockNum: Int,
            blockPrefix: Long,
            actions: List<ActionAbi>,
            expiryTimestamp: Long
        ): TransactionAbi {
            return TransactionAbi(
                Instant.fromEpochMilliseconds(expiryTimestamp),
                blockNum,
                blockPrefix,
                0,
                0,
                0,
                emptyList(),
                actions,
                emptyList(),
                emptyList(),
                emptyList()
            )
        }

        companion object {
            protected const val EOS_SYSTEM_CONTRACT = "eosio"
            protected const val VAULTA_SYSTEM_CONTRACT = "core.vaulta"
        }
    }

    fun toTransactionAbi(): TransactionAbi {
        val blockIdDetails = BlockIdDetails(headBlockId)

        return TransactionAbi(
            Instant.fromEpochMilliseconds(expiryTimestamp),
            blockIdDetails.blockNum,
            blockIdDetails.blockPrefix,
            0,
            0,
            0,
            emptyList(),
            actions.map { it.toActionAbi() },
            emptyList(),
            emptyList(),
            emptyList()
        )
    }
}

fun TransactionAbi.toPackedTrx() = AbiBinaryGenTransactionWriter(CompressionType.NONE).squishTransactionAbi(
    this
).toHex()