package com.mangala.wallet.features.contract_wizard.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ContractWizardResponse (
    val contract: String?,
    val bytecode: String?,
    val abi: List<AbiEntry>?
)

@Serializable
data class AbiEntry(
    val inputs: List<AbiInput>?,
    val stateMutability: String?,
    val type: String?,
    val anonymous: Boolean?,
    val name: String?,
    val outputs: List<AbiOutput>?
)

@Serializable
data class AbiInput(
    val indexed: Boolean?,
    val internalType: String?,
    val name: String?,
    val type: String?
)

@Serializable
data class AbiOutput(
    val internalType: String?,
    val name: String?,
    val type: String?
)