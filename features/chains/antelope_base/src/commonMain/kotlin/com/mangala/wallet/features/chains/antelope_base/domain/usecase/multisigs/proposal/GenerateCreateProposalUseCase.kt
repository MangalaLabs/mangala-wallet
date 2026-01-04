package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.BaseGenerateSignRequestUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.toBigDecimalOrNull
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes.Companion.ASSET_TYPE_DELIMITER
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter
import com.memtrip.eos.core.block.BlockIdDetails

class GenerateCreateProposalUseCase(
    getInfoUseCase: GetInfoUseCase
) : BaseGenerateSignRequestUseCase(getInfoUseCase) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        proposerAccountName: String,
        proposalName: String,
        requestedPermissions: List<TransactionAuthorizationAbi>,
        accountPermissionExecuted: String,
        actions: List<MultisigAction>,
        expiryTimestamp: Long
    ): SignTransactionRequest? {
        val chainInfo = getInfoUseCase(blockchainType)

        val blockDetails = chainInfo?.let {
            BlockIdDetails(
                it.headBlockId.orEmpty()
            )
        }
        return constructSignRequest(
            blockchainType,
            constructAuthorization = {
                constructAuthorization(proposerAccountName, accountPermissionExecuted)
            },
            constructActions = { authorization ->
                val transferAction = SignTransactionRequest.Action.CreateProposal(
                    authorization = authorization,
                    proposerAccountName = proposerAccountName,
                    proposalName = proposalName,
                    requestedPermissions = requestedPermissions,
                    accountPermissionExecuted = accountPermissionExecuted,
                    actions = convertToProposeActions(actions),
                    blockNum = blockDetails?.blockNum ?: 0,
                    blockPrefix = blockDetails?.blockPrefix ?: 0,
                    expiryTimestamp = expiryTimestamp
                )
                listOf(transferAction)
            },
            signTransactionType = SignTransactionType.CREATE_PROPOSE,
        )
    }

    @Deprecated("Use version that takes in List<MultisigAction> instead")
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        proposerAccountName: String,
        proposalName: String,
        requestedPermissions: List<TransactionAuthorizationAbi>,
        accountPermissionExecuted: String,
        actionAbiMap: Map<ActionAbi, List<AntelopeActionAbi>>,
        expiryTimestamp: Long
    ): SignTransactionRequest? {
        val chainInfo = getInfoUseCase(blockchainType)

        val blockDetails = chainInfo?.let {
            BlockIdDetails(
                it.headBlockId.orEmpty()
            )
        }
        return constructSignRequest(
            blockchainType,
            constructAuthorization = {
                constructAuthorization(proposerAccountName, accountPermissionExecuted)
            },
            constructActions = { authorization ->
                val transferAction = SignTransactionRequest.Action.CreateProposal(
                    authorization = authorization,
                    proposerAccountName = proposerAccountName,
                    proposalName = proposalName,
                    requestedPermissions = requestedPermissions,
                    accountPermissionExecuted = accountPermissionExecuted,
                    actions = convertToProposeActions(actionAbiMap),
                    blockNum = blockDetails?.blockNum ?: 0,
                    blockPrefix = blockDetails?.blockPrefix ?: 0,
                    expiryTimestamp = expiryTimestamp
                )
                listOf(transferAction)
            },
            signTransactionType = SignTransactionType.CREATE_PROPOSE
        )
    }

    private fun convertToProposeActions(actionStates: Map<ActionAbi, List<AntelopeActionAbi>>): List<ActionAbi> {
        return actionStates.map { (key, value) ->
            ActionAbi(
                account = key.account,
                name = key.name,
                authorization = key.authorization,
                data = constructAntelopeActions(value.toList())
            )
        }
    }

    private fun convertToProposeActions(actions: List<MultisigAction>): List<ActionAbi> {
        return actions.map {
            ActionAbi(
                account = it.contractName,
                name = it.actionName,
                authorization = it.authorizations.map {
                    TransactionAuthorizationAbi(it.authorizationName, it.permissionName)
                },
                data = constructAntelopeActions(it.fields.actionList)
            )
        }
    }

    private fun constructAntelopeActions(antelopeActionAbi: List<AntelopeActionAbi>): String =
        AbiBinaryGenTransactionWriter(CompressionType.NONE).apply {
            squishAntelopeActionAbi(
                antelopeActionAbi.map { convertToMemtripAbi(it) }
            )
        }.toHex()

    private fun convertToMemtripAbi(mangalaAbi: AntelopeActionAbi): com.memtrip.eos.chain.actions.transaction.account.actions.AntelopeActionAbi {
        val updatedValue = if (mangalaAbi.baseType == AntelopePrimitiveDataTypes.ASSET.value && mangalaAbi.symbolDecimals != null) {
            val amount = mangalaAbi.value.split(ASSET_TYPE_DELIMITER).getOrNull(0)?.trim()
            val symbol = mangalaAbi.value.split(ASSET_TYPE_DELIMITER).getOrNull(1)?.trim()
            val value = amount?.toBigDecimalOrNull()

            if (amount == null || value == null) {
                mangalaAbi.value
            } else {
                val decimalDigits = amount.substringAfter('.', "")
                val decimalPlace = if (decimalDigits.isEmpty() && mangalaAbi.symbolDecimals > 0) "." else ""
                val desiredPadLength = mangalaAbi.symbolDecimals - decimalDigits.length + decimalPlace.length

                val formattedValue = value.scale(mangalaAbi.symbolDecimals.toLong()).toStringExpanded() + decimalPlace.padEnd(desiredPadLength, '0')
                formattedValue + ASSET_TYPE_DELIMITER + symbol
            }
        } else {
            mangalaAbi.value
        }

        return com.memtrip.eos.chain.actions.transaction.account.actions.AntelopeActionAbi(
            actionName = mangalaAbi.actionName,
            accountName = mangalaAbi.accountName,
            fieldName = mangalaAbi.fieldName,
            fieldType = mangalaAbi.fieldType,
            value = updatedValue,
            level = mangalaAbi.level,
            arraySize = mangalaAbi.arraySize,
            subfieldCount = mangalaAbi.subFields.size,
            isOptionalValueSet = mangalaAbi.isOptionalValueSet,
            variantTypeIndex = mangalaAbi.variantTypeIndex,
        )
    }
}

