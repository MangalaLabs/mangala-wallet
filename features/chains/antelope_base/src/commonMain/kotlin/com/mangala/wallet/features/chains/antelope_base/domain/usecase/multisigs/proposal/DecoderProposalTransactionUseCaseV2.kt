package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.TransactionProposalDecoded
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionAbiByContractAndActionNameUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.chain.actions.transaction.decoder.AbiBinaryTransactionReader


class DecoderProposalTransactionUseCaseV2(
    private val getActionAbiByContractAndActionNameUseCase: GetActionAbiByContractAndActionNameUseCase
) {
    suspend operator fun invoke(
        input: String,
        blockchainType: BlockchainType
    ): TransactionProposalDecoded {
        return processDecodeFromInput(input) { action ->
            getActionAbiByContractAndActionNameUseCase(
                accountName = action.getAccount,
                actionName = action.getName,
                forceRefresh = false,
                blockchainType = blockchainType
            ).getOrDefault(emptyList())
        }
    }

    suspend operator fun invoke(input: String): TransactionProposalDecoded {
        return processDecodeFromInput(input) { action ->
            getActionAbiByContractAndActionNameUseCase(
                accountName = action.getAccount,
                actionName = action.getName,
                forceRefresh = false
            ).getOrDefault(emptyList())
        }
    }

    private suspend fun processDecodeFromInput(
        input: String,
        getActionAbiByContractAndActionName: suspend (action: com.memtrip.eos.chain.actions.transaction.abi.ActionAbi) -> List<AntelopeActionAbi>,
    ): TransactionProposalDecoded {
        val transactionReader = AbiBinaryTransactionReader(input)
        val transaction = transactionReader.readProposalTransactionAbi()
        val actions = transaction.actions

        val decodedActionsAbi = actions.map { action ->
            val actionReader = AbiBinaryTransactionReader(action.getData.orEmpty())
            val actionAbi = getActionAbiByContractAndActionName(action)

            val fieldObjectAbi =
                actionAbi.filter { !it.isPrimitive }
            val fieldObjectMap = getObjectAbi(fieldObjectAbi)

            resolveNestedFields(actionAbi, fieldObjectMap)
            val actionAbiMem = actionAbi.map { convertToMemtripAbi(it) }

            val actionValue = actionReader.decodeHex(actionAbiMem).map { convertToActionAbi(it) }

            ActionAbi(
                account = action.getAccount,
                name = action.getName,
                authorization = action.getAuthorization,
                null,
                dataDecoded = actionValue
            )
        }

        return TransactionProposalDecoded(
            expiration = transaction.expiration,
            ref_block_num = transaction.ref_block_num,
            ref_block_prefix = transaction.ref_block_prefix,
            max_net_usage_words = transaction.max_net_usage_words,
            max_cpu_usage_ms = transaction.max_cpu_usage_ms,
            delay_sec = transaction.delay_sec,
            actions = decodedActionsAbi,
            transaction_extensions = transaction.transaction_extensions,
            signatures = transaction.signatures,
            context_free_data = transaction.context_free_data,
        )
    }

    private fun resolveNestedFields(
        actionAbi: List<AntelopeActionAbi>,
        fieldObjectMap: Map<String, List<AntelopeActionAbi>>
    ) {
        actionAbi.forEach { actionField ->
            var actionQuery = actionField.fieldType
            if (actionField.isArray) {
                actionQuery = actionField.baseType
            }
            fieldObjectMap[actionQuery]?.let { subFields ->
                actionField.subFields = subFields
                actionField.subFields.forEach { subField ->
                    subField.level = actionField.level + 1
                }

                resolveNestedFields(actionField.subFields, fieldObjectMap)
            }
        }
    }

    private suspend fun getObjectAbi(fieldObjectAbi: List<AntelopeActionAbi>): Map<String, List<AntelopeActionAbi>> {
        val fieldObjectMap = mutableMapOf<String, List<AntelopeActionAbi>>()

        suspend fun resolveNestedObjects(fieldAbi: AntelopeActionAbi) {
            if (!fieldAbi.isPrimitive) {
                var actionQuery = fieldAbi.fieldType
                if (fieldAbi.isArray) {
                    actionQuery = fieldAbi.baseType
                }
                val subFields = getActionAbiByContractAndActionNameUseCase(
                    fieldAbi.accountName,
                    actionQuery,
                    false
                ).getOrDefault(emptyList())
                fieldObjectMap[actionQuery] = subFields
                subFields.forEach { subField ->
                    resolveNestedObjects(subField)
                }
            }
        }

        fieldObjectAbi.forEach {
            resolveNestedObjects(it)
        }

        return fieldObjectMap
    }

    private fun convertToMemtripAbi(mangalaAbi: AntelopeActionAbi): com.memtrip.eos.chain.actions.transaction.account.actions.AntelopeActionAbi {
        return com.memtrip.eos.chain.actions.transaction.account.actions.AntelopeActionAbi(
            actionName = mangalaAbi.actionName,
            accountName = mangalaAbi.accountName,
            fieldName = mangalaAbi.fieldName,
            fieldType = mangalaAbi.fieldType,
            value = mangalaAbi.value,
            level = mangalaAbi.level,
            subFields = mangalaAbi.subFields.map { convertToMemtripAbi(it) },
            arraySize = 0,
            subfieldCount = 0
        )
    }

    private fun convertToActionAbi(memtripAbi: com.memtrip.eos.chain.actions.transaction.account.actions.AntelopeActionAbi): AntelopeActionAbi {
        return AntelopeActionAbi(
            actionName = memtripAbi.actionName,
            accountName = memtripAbi.accountName,
            fieldName = memtripAbi.fieldName,
            fieldType = memtripAbi.fieldType,
            value = memtripAbi.value,
            level = memtripAbi.level,
            subFields = memtripAbi.subFields.map { convertToActionAbi(it) },
            arraySize = memtripAbi.arraySize,
            mapValue = memtripAbi.mapValue,
            isVariant = false
        )
    }
}