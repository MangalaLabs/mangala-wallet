package com.mangala.wallet.features.chains.antelope_base.domain.model

import com.mangala.antelope.base.api.model.ActionTrace
import com.mangala.antelope.base.api.model.EosAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetActionsPagingModel(
    @SerialName("actions")
    val actions: List<ActionPagingModel>? = null
)

@Serializable
data class ActionPagingModel(
    @SerialName("account_action_seq")
    val accountActionSeq: List<Long>? = null,
    @SerialName("action_trace")
    val actionTrace: List<ActionTrace>? = null,
    @SerialName("block_num")
    val blockNum: List<Long>? = null,
    @SerialName("block_time")
    val blockTime: List<String>? = null,
    @SerialName("global_action_seq")
    val globalActionSeq: List<Long>? = null,
    @SerialName("irreversible")
    val irreversible: List<Boolean>? = null,
    @SerialName("trx_id")
    val trxId: String? = null
)

class ActionPagingModelBuilder(
    val trxId: String,
    val accountActionSeqs: MutableList<Long> = mutableListOf(),
    val actionTraces: MutableList<ActionTrace> = mutableListOf(),
    val blockNums: MutableList<Long> = mutableListOf(),
    val blockTimes: MutableList<String> = mutableListOf(),
    val globalActionSeqs: MutableList<Long> = mutableListOf(),
    val irreversibles: MutableList<Boolean> = mutableListOf()
) {
    fun addActionPaging(actionPaging: EosAction) {
        actionPaging.actionTrace?.let {
            actionTraces.add(it)
            actionPaging.accountActionSeq?.let { seq -> accountActionSeqs.add(seq) }
            actionPaging.blockNum?.let { num -> blockNums.add(num) }
            actionPaging.blockTime?.let { time -> blockTimes.add(time) }
            actionPaging.globalActionSeq?.let { seq -> globalActionSeqs.add(seq) }
            actionPaging.irreversible?.let { irr -> irreversibles.add(irr) }
        }
    }

    fun addAct(eosAction: EosAction) {
        actionTraces.add(
            ActionTrace(
                act = eosAction.act,
                actionOrdinal = eosAction.actionOrdinal,
                blockNum = eosAction.blockNum,
                blockTime = eosAction.timestampSimple,
                creatorActionOrdinal = eosAction.creatorActionOrdinal,
                trxId = eosAction.trxId
            )
        )

        blockNums.add(eosAction.blockNum ?: 0L)
        blockTimes.add(eosAction.timestampSimple ?: "")
    }

    fun build(): ActionPagingModel {

//        val sortedActionTraces = actionTraces.sortedWith(compareBy { it.act?.name != "transfer" })

        return ActionPagingModel(
            accountActionSeq = accountActionSeqs,
            actionTrace = actionTraces,
            blockNum = blockNums,
            blockTime = blockTimes,
            globalActionSeq = globalActionSeqs,
            irreversible = irreversibles,
            trxId = trxId
        )
    }
}
