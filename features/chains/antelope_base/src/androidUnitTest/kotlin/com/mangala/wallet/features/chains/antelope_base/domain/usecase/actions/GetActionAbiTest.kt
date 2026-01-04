package com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class GetActionAbiTest {
    val getActionAbiByContractAndActionNameUseCase: GetActionAbiByContractAndActionNameUseCase =
        mockk()

    val sut = GetActionAbi(getActionAbiByContractAndActionNameUseCase)

    @Test
    fun `Given an action ABI with two fields with same data type, when invoke, then get correct mapped data`() = runTest {
        coEvery {
            getActionAbiByContractAndActionNameUseCase.invoke(
                "eosio",
                "authority",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("authority", "eosio", "threshold", "uint32", isVariant = false),
                AntelopeActionAbi("authority", "eosio", "keys", "key_weight[]", isVariant = false),
                AntelopeActionAbi(
                    "authority",
                    "eosio",
                    "accounts",
                    "permission_level_weight[]",
                    isVariant = false
                ),
                AntelopeActionAbi(
                    "authority",
                    "eosio",
                    "waits",
                    "wait_weight[]",
                    isVariant = false
                ),
            )
        )
        coEvery {
            getActionAbiByContractAndActionNameUseCase.invoke(
                "eosio",
                "key_weight",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("key_weight", "eosio", "key", "public_key", isVariant = false),
                AntelopeActionAbi("key_weight", "eosio", "weight", "uint16", isVariant = false),
            )
        )
        coEvery {
            getActionAbiByContractAndActionNameUseCase.invoke(
                "eosio",
                "permission_level_weight",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi(
                    "permission_level_weight",
                    "eosio",
                    "permission",
                    "permission_level",
                    isVariant = false
                ),
                AntelopeActionAbi(
                    "permission_level_weight",
                    "eosio",
                    "weight",
                    "uint16",
                    isVariant = false
                ),
            )
        )
        coEvery {
            getActionAbiByContractAndActionNameUseCase.invoke(
                "eosio",
                "permission_level",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("permission_level", "eosio", "actor", "name"),
                AntelopeActionAbi(
                    "permission_level",
                    "eosio",
                    "permission",
                    "name",
                    isVariant = false
                ),
            )
        )
        coEvery {
            getActionAbiByContractAndActionNameUseCase.invoke(
                "eosio",
                "wait_weight",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("wait_weight", "eosio", "wait_sec", "uint32"),
                AntelopeActionAbi("wait_weight", "eosio", "wait", "uint16"),
            )
        )
        val action = com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ActionAbi(
            account = "eosio",
            name = "actfinkey",
            listOf(TransactionAuthorizationAbi("", "")),
            null
        )
        val subfield = listOf(
            AntelopeActionAbi(
                actionName = "authority",
                accountName = "eosio",
                fieldName = "threshold",
                fieldType = "uint32",
                level = 1,
                isVariant = false
            ),
            AntelopeActionAbi(
                actionName = "authority",
                accountName = "eosio",
                fieldName = "keys",
                fieldType = "key_weight[]",
                level = 1,
                subFields = listOf(
                    AntelopeActionAbi(
                        actionName = "key_weight",
                        accountName = "eosio",
                        fieldName = "key",
                        fieldType = "public_key",
                        level = 2,
                        isVariant = false
                    ),
                    AntelopeActionAbi(
                        actionName = "key_weight",
                        accountName = "eosio",
                        fieldName = "weight",
                        fieldType = "uint16",
                        level = 2,
                        isVariant = false
                    )
                ),
                isVariant = false
            ),
            AntelopeActionAbi(
                actionName = "authority",
                accountName = "eosio",
                fieldName = "accounts",
                fieldType = "permission_level_weight[]",
                level = 1,
                subFields = listOf(
                    AntelopeActionAbi(
                        actionName = "permission_level_weight",
                        accountName = "eosio",
                        fieldName = "permission",
                        fieldType = "permission_level",
                        level = 2,
                        subFields = listOf(
                            AntelopeActionAbi(
                                actionName = "permission_level",
                                accountName = "eosio",
                                fieldName = "actor",
                                fieldType = "name",
                                level = 3,
                                isVariant = false
                            ),
                            AntelopeActionAbi(
                                actionName = "permission_level",
                                accountName = "eosio",
                                fieldName = "permission",
                                fieldType = "name",
                                level = 3,
                                isVariant = false
                            )
                        )
                    ),
                    AntelopeActionAbi(
                        actionName = "permission_level_weight",
                        accountName = "eosio",
                        fieldName = "weight",
                        fieldType = "uint16",
                        level = 2
                    ),
                    AntelopeActionAbi(
                        actionName = "authority",
                        accountName = "eosio",
                        fieldName = "waits",
                        fieldType = "wait_weight[]",
                        level = 1,
                        subFields = listOf(
                            AntelopeActionAbi(
                                actionName = "wait_weight",
                                accountName = "eosio",
                                fieldName = "wait_sec",
                                fieldType = "uint32",
                                level = 2,
                            ), AntelopeActionAbi(
                                actionName = "wait_weight",
                                accountName = "eosio",
                                fieldName = "weight",
                                fieldType = "uint16",
                                level = 2,
                            )
                        )
                    )
                )
            )
        )

        val actionAbi = listOf(
            AntelopeActionAbi("newaccount", "eosio", "creator", "name"),
            AntelopeActionAbi("newaccount", "eosio", "name", "name"),
            AntelopeActionAbi("newaccount", "eosio", "owner", "authority", subFields = subfield),
            AntelopeActionAbi("newaccount", "eosio", "active", "authority", subFields = subfield)
        )

        val result = sut.invoke(actionAbi, action)

        result.actionMapIndex.forEachIndexed { index, i -> println("[$index: $i]") }

        assertEquals(12, result.actionMapField.values.flatten().size)
        assertEquals(12, result.actionMapIndex.size)
        assertEquals(listOf(-1, -1, -1, 2, 2, 2, 2, -1, 7, 7, 7, 7), result.actionMapIndex)
    }
}