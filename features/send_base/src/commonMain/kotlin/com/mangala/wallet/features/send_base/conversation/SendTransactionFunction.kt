package com.mangala.wallet.features.send_base.conversation

import com.mangala.wallet.core.ai.domain.model.function.FunctionParameter
import com.mangala.wallet.core.ai.domain.model.function.ParameterType
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import com.mangala.wallet.core.security.models.SecurityLevel
import com.mangala.wallet.features.send_base.conversation.WalletFunctions.Companion.MODULE_ID

const val SEND_TRANSACTION_FUNCTION_NAME = "send_transaction"

const val SEND_TRANSACTION_PARAM_RECIPIENT_ADDRESS = "recipient_address"
const val SEND_TRANSACTION_PARAM_AMOUNT = "amount"
const val SEND_TRANSACTION_PARAM_ASSET = "asset"
const val SEND_TRANSACTION_PARAM_ASSET_LOGO = "asset_logo"
const val SEND_TRANSACTION_PARAM_MEMO = "memo"
const val SEND_TRANSACTION_PARAM_FEE = "fee"
const val SEND_TRANSACTION_PARAM_TRANSACTION = "transaction"
const val SEND_TRANSACTION_SENDER_ADDRESS = "sender_address"
const val SEND_TRANSACTION_RECIPIENT_CONTACT_NAME = "recipient_contact_name"
const val SEND_TRANSACTION_BLOCKCHAIN_UID = "blockchain_uid"

val sendTransactionParameters = listOf(
    FunctionParameter(
        name = SEND_TRANSACTION_PARAM_RECIPIENT_ADDRESS,
        type = ParameterType.STRING,
        description = "The blockchain address of the recipient",
        required = true
    ),
    FunctionParameter(
        name = SEND_TRANSACTION_PARAM_AMOUNT,
        type = ParameterType.NUMBER,
        description = "The amount of cryptocurrency to send",
        required = true
    ),
    FunctionParameter(
        name = SEND_TRANSACTION_PARAM_ASSET,
        type = ParameterType.STRING,
        description = "The cryptocurrency asset to send (e.g., BTC, ETH)",
        required = true
    ),
    FunctionParameter(
        name = SEND_TRANSACTION_PARAM_MEMO,
        type = ParameterType.STRING,
        description = "Optional memo or note to attach to the transaction",
        required = false
    ),
    FunctionParameter(
        name = SEND_TRANSACTION_PARAM_FEE,
        type = ParameterType.STRING,
        description = "Transaction fee level (low, medium, high)",
        required = false,
        enumValues = listOf("low", "medium", "high")
    )
)

val sendTransactionFunction = FunctionDefinition(
    name = SEND_TRANSACTION_FUNCTION_NAME,
    description = "Send cryptocurrency to a specified address",
    parameters = sendTransactionParameters.associateBy { it.name },
    requiredParameters = sendTransactionParameters.mapNotNull { if (it.required) it.name else null },
    moduleId = MODULE_ID,
    securityLevel = SecurityLevel.RequireBiometryOrPin
)
