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

package com.memtrip.eos.chain.actions.transaction.decoder

import com.memtrip.eos.abi.reader.ByteReader
import com.memtrip.eos.abi.reader.bytereader.DefaultByteReader
import com.memtrip.eos.abi.writer.ByteWriter
import com.memtrip.eos.chain.actions.keycert.EncryptedPrivateKeyArgs
import com.memtrip.eos.chain.actions.keycert.KeyCertArgs
import com.memtrip.eos.chain.actions.transaction.abi.AbiPrimitiveDataType
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.ActionFieldName.ASSET
import com.memtrip.eos.chain.actions.transaction.account.actions.ActionFieldName.BOOL
import com.memtrip.eos.chain.actions.transaction.account.actions.ActionFieldName.BYTE
import com.memtrip.eos.chain.actions.transaction.account.actions.ActionFieldName.INT_64
import com.memtrip.eos.chain.actions.transaction.account.actions.ActionFieldName.NAME
import com.memtrip.eos.chain.actions.transaction.account.actions.ActionFieldName.PUBLIC_KEY
import com.memtrip.eos.chain.actions.transaction.account.actions.ActionFieldName.STRING
import com.memtrip.eos.chain.actions.transaction.account.actions.ActionFieldName.UNIT_16
import com.memtrip.eos.chain.actions.transaction.account.actions.ActionFieldName.UNIT_32
import com.memtrip.eos.chain.actions.transaction.account.actions.ActionFieldName.UNIT_64
import com.memtrip.eos.chain.actions.transaction.account.actions.ActionFieldName.VARIABLE_UNIT
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopeActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.sellram.SellRamArgs
import com.memtrip.eos.chain.actions.transaction.esr.EsrIdentityArgs
import com.memtrip.eos.chain.actions.transaction.esr.EsrInfoArgs
import com.memtrip.eos.chain.actions.transaction.esr.EsrSigningRequestArgs
import com.memtrip.eos.chain.actions.transaction.esr.anchorlink.AnchorLinkSealedMessageArg
import com.memtrip.eos.chain.actions.transaction.transfer.actions.TransferArgs
import com.memtrip.eos.core.crypto.EosPublicKey
import com.memtrip.eos.core.crypto.KeyType
import kotlinx.datetime.Instant

class AbiBinaryTransactionReader(
    private val hexValue: String,
) {

    fun readBuyRamArgs(): BuyRamArgs {
        val byteReader: ByteReader = DefaultByteReader()
        byteReader.load(hexValue)
        val payer = byteReader.getAccountName(byteReader)
        val receiver = byteReader.getAccountName(byteReader)
        val quantity = byteReader.getAsset(byteReader)

        return BuyRamArgs(payer, receiver, quantity)
    }

    fun readSellRamArgs(): SellRamArgs {
        val byteReader: ByteReader = DefaultByteReader()
        byteReader.load(hexValue)
        val account = byteReader.getAccountName(byteReader)
        val bytes = byteReader.getVariableUInt(byteReader)

        return SellRamArgs(account, bytes)
    }

    fun readTransferArgs(): TransferArgs {
        val byteReader: ByteReader = DefaultByteReader()
        byteReader.load(hexValue)

        val from = byteReader.getAccountName(byteReader)
        val to = byteReader.getAccountName(byteReader)
        val quantity = byteReader.getAsset(byteReader)
        val memo = byteReader.getString(byteReader)

        return TransferArgs(from, to, quantity, memo)
    }

    fun readEsrSigningRequestArgs(): EsrSigningRequestArgs {
        val byteReader: ByteReader = DefaultByteReader()
        byteReader.load(hexValue)

        // Variant, can either be chainId or chainAlias
        val variantId = byteReader.getUByte(byteReader)
        val chainAlias = if (variantId == 0) byteReader.getUByte(byteReader) else null
        val chainId =
            if (variantId == 1) byteReader.getChecksum256(byteReader) else null // checksum256

        val variantReq = byteReader.getUByte(byteReader)
        val transaction = if (variantReq == 2) {
            readTransactionAbi(byteReader)
        } else null
        val actions = if (variantReq == 1) {
            readCollectionActionAbi(byteReader)
        } else if (variantReq == 0) {
            listOf(readActionAbi(byteReader))
        } else null
        val identity = if (variantReq == 3) {
            readEsrIdentityAbi(byteReader)
        } else null

        val flags = byteReader.getUByte(byteReader)
        val callback = byteReader.getString(byteReader)
        val info = readCollectionEsrInfoAbi(byteReader).map {
            if (it.key == "link" && identity != null) {
                readEsrLinkArg(it)
            } else it
        }

        return EsrSigningRequestArgs(
            chainAlias = chainAlias,
            chainId = chainId,
            transaction = transaction,
            actions = actions,
            identityRequest = identity,
            flags = flags,
            callback = callback,
            info = info
        )
    }

    fun readProposalTransactionAbi(): TransactionAbi {
        val byteReader: ByteReader = DefaultByteReader()
        byteReader.load(hexValue)
        return readTransactionAbi(byteReader)
    }

    fun readKeyCertArgs(): KeyCertArgs {
        val byteReader: ByteReader = DefaultByteReader()
        byteReader.load(hexValue)

        val chainId = byteReader.getChecksum256(byteReader)
        val permissionLevel = readAuthorizationAbi(byteReader)
        val encryptedPrivateKey = readEncryptedPrivateKey(byteReader)

        return KeyCertArgs(chainId, permissionLevel, encryptedPrivateKey)
    }

    fun readEncryptedPrivateKey(): EncryptedPrivateKeyArgs {
        val byteReader: ByteReader = DefaultByteReader()
        byteReader.load(hexValue)

        return readEncryptedPrivateKey(byteReader)
    }

    // https://github.com/greymass/eosio-key-encryption/blob/ac4eec83356d7af1609ef443c0d2c499ae51647c/src/encrypted-private-key.ts#L56
    private fun readEncryptedPrivateKey(byteReader: ByteReader): EncryptedPrivateKeyArgs {
        val keyType = byteReader.getKeyType(byteReader)
        val flags = byteReader.getByte(byteReader)
        val checksum = byteReader.getBytes(byteReader, 4)
        val cipherText = when (keyType) {
            KeyType.K1 -> byteReader.getBytes(byteReader, 32)
            else -> throw IllegalArgumentException("Unsupported key type")
        }

        return EncryptedPrivateKeyArgs(keyType, flags, checksum, cipherText)
    }

    fun readAnchorLinkSealedMessage(): AnchorLinkSealedMessageArg {
        // https://github.com/greymass/anchor-link/blob/62c6e7775e3a637e1acfd95544902d174b698a7f/src/link-types.ts#L4
        val byteReader: ByteReader = DefaultByteReader()
        byteReader.load(hexValue)

        val publicKey = byteReader.getPublicKey(byteReader)
        val nonce = byteReader.getULong(byteReader)
        val bytes = byteReader.getBytes(byteReader)
        val checksum = byteReader.getUInt(byteReader)

        return AnchorLinkSealedMessageArg(publicKey, nonce, bytes, checksum)
    }

    private fun readEsrLinkArg(it: EsrInfoArgs.GenericArg): EsrInfoArgs.Link {
        val linkByteReader = DefaultByteReader()
        linkByteReader.load(it.value)

        val sessionName = linkByteReader.getName(linkByteReader)
        val requestKey = linkByteReader.getPublicKey(linkByteReader)
        val userAgent = linkByteReader.getString(linkByteReader)

        return EsrInfoArgs.Link(it.key, requestKey, sessionName, userAgent)
    }

    fun readCollectionActionAbi(byteReader: ByteReader): List<ActionAbi> {
        val size = byteReader.getVariableUInt(byteReader)
        val actions = mutableListOf<ActionAbi>()

        for (i in 0 until size) {
            actions.add(readActionAbi(byteReader))
        }

        return actions
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun readActionAbi(byteReader: ByteReader): ActionAbi {
        val accountName = byteReader.getAccountName(byteReader)
        val name = byteReader.getName(byteReader)
        val authorization = readCollectionAuthorizationAbi(byteReader)
        val data = byteReader.getBytes(byteReader).toHexString()

        return ActionAbi(accountName, name, authorization, data)
    }

    fun readCollectionAuthorizationAbi(byteReader: ByteReader): List<TransactionAuthorizationAbi> {
        val size = byteReader.getVariableUInt(byteReader)
        val authorizations = mutableListOf<TransactionAuthorizationAbi>()

        for (i in 0 until size) {
            authorizations.add(readAuthorizationAbi(byteReader))
        }

        return authorizations
    }

    fun readAuthorizationAbi(byteReader: ByteReader): TransactionAuthorizationAbi {
        val actor = byteReader.getAccountName(byteReader)
        val permission = byteReader.getAccountName(byteReader)

        return TransactionAuthorizationAbi(actor, permission)
    }

    private fun readCollectionEsrInfoAbi(byteReader: ByteReader): List<EsrInfoArgs.GenericArg> {
        val size = byteReader.getVariableUInt(byteReader)
        val esrInfo = mutableListOf<EsrInfoArgs.GenericArg>()

        for (i in 0 until size) {
            val key = byteReader.getString(byteReader)
            val info = byteReader.getBytes(byteReader)

            esrInfo.add(EsrInfoArgs.GenericArg(key, info))
        }

        return esrInfo
    }

    fun readTransactionAbi(): TransactionAbi {
        val byteReader: ByteReader = DefaultByteReader()
        byteReader.load(hexValue)

        return readTransactionAbi(byteReader)
    }

    fun readBuyRamAbi(): BuyRamArgs {
        val byteReader: ByteReader = DefaultByteReader()
        byteReader.load(hexValue)

        val payer = byteReader.getAccountName(byteReader)
        val receiver = byteReader.getAccountName(byteReader)
        val quant = byteReader.getAsset(byteReader)

        return BuyRamArgs(payer, receiver, quant)
    }

    private fun readTransactionAbi(byteReader: ByteReader): TransactionAbi {
        val expiration = byteReader.getTimestampMs(byteReader)
        val refBlockNum = byteReader.getBlockNum(byteReader)
        val refBlockPrefix = byteReader.getBlockPrefix(byteReader)
        val maxNetUsageWords = byteReader.getVariableUInt(byteReader)
        val maxCpuUsageMs = byteReader.getVariableUInt(byteReader)
        val delaySec = byteReader.getVariableUInt(byteReader)
        val contextFreeActions = readCollectionActionAbi(byteReader)
        val actions = readCollectionActionAbi(byteReader)
        val transactionExtensions = byteReader.getStringCollection(byteReader)

        return TransactionAbi(
            expiration = Instant.fromEpochMilliseconds(expiration),
            ref_block_num = refBlockNum,
            ref_block_prefix = refBlockPrefix,
            max_net_usage_words = maxNetUsageWords,
            max_cpu_usage_ms = maxCpuUsageMs,
            delay_sec = delaySec,
            context_free_actions = contextFreeActions,
            actions = actions,
            transaction_extensions = transactionExtensions,
            context_free_data = emptyList(),
            signatures = emptyList()
        )
    }

    private fun readEsrIdentityAbi(byteReader: ByteReader): EsrIdentityArgs {
        val scope = byteReader.getName(byteReader)

        val presenceFlag = byteReader.getByte(byteReader)
        val permissionLevel = if (presenceFlag == 1) readAuthorizationAbi(byteReader) else null

        return EsrIdentityArgs(scope, permissionLevel)
    }

    fun decodeHex(actionAbi: List<AntelopeActionAbi>): List<AntelopeActionAbi> {
        val byteReader = DefaultByteReader().apply {
            load(hexValue)
        }
        return decodeFields(byteReader, actionAbi)
    }

    private fun decodeFields(
        byteReader: ByteReader,
        fields: List<AntelopeActionAbi>
    ): List<AntelopeActionAbi> {
        return fields.map { field ->
            decodeField(byteReader, field)
        }
    }

    private fun decodeField(
        byteReader: ByteReader,
        field: AntelopeActionAbi
    ): AntelopeActionAbi {
        val array = mutableListOf<AbiPrimitiveDataType>()
        val mapValue = mutableMapOf<String, List<AbiPrimitiveDataType>>()

        when {
            field.isPrimitive -> {
                val primitiveValue = readPrimitiveType(byteReader, field.fieldType)
                array.add(primitiveValue)
                mapValue[field.fieldName] = listOf(primitiveValue)
            }

            field.isArray -> {
                val arrayValue = decodeArrayField(byteReader, field)
                array.addAll(arrayValue)
                mapValue[field.fieldName] = arrayValue
                field.arraySize = arrayValue.size
            }

            field.subFields.isNotEmpty() -> {
                field.subFields.map { subField ->
                    decodeField(byteReader, subField)
                }
            }
            else -> throw IllegalArgumentException("Unsupported field type: ${field.fieldType}")
        }
        field.valueArr = array
        field.mapValue = mapValue

        return field
    }

    private fun decodeArrayField(
        byteReader: ByteReader,
        field: AntelopeActionAbi
    ): List<AbiPrimitiveDataType> {
        val arraySize = byteReader.getVariableUInt(byteReader)
        val array = mutableListOf<AbiPrimitiveDataType>()
        for (i in 0 until arraySize) {
            if (field.isArrayPrimitive) {
                array.add(readPrimitiveType(byteReader, field.baseType))
            } else {
                array.addAll(decodeSubFields(byteReader, field))
            }
        }
        return array
    }

    private fun decodeSubFields(
        byteReader: ByteReader,
        field: AntelopeActionAbi
    ): List<AbiPrimitiveDataType> {
        val array = mutableListOf<AbiPrimitiveDataType>()
        field.subFields.map { subField ->
            array.addAll(decodeField(byteReader, subField).valueArr)
        }
        return array
    }

    private fun readPrimitiveType(byteReader: ByteReader, fieldType: String): AbiPrimitiveDataType {
        return when (fieldType) {
            NAME -> AbiPrimitiveDataType.NameType(byteReader.getAccountName(byteReader))
            ASSET -> AbiPrimitiveDataType.AssetType(byteReader.getAsset(byteReader))
            VARIABLE_UNIT -> AbiPrimitiveDataType.VariableUIntType(
                byteReader.getVariableUInt(
                    byteReader
                )
            )
            PUBLIC_KEY -> AbiPrimitiveDataType.PublicKeyType(byteReader.getPublicKey(byteReader))
            STRING -> AbiPrimitiveDataType.StringType(byteReader.getString(byteReader))
            BYTE -> AbiPrimitiveDataType.ByteType(byteReader.getByte(byteReader))
            INT_64 -> AbiPrimitiveDataType.Int64Type(byteReader.getULong(byteReader).toLong())
            UNIT_64 -> AbiPrimitiveDataType.UInt64Type(byteReader.getULong(byteReader).toLong())
            UNIT_32 -> AbiPrimitiveDataType.UInt32Type(byteReader.getUInt(byteReader).toInt())
            UNIT_16 -> AbiPrimitiveDataType.UInt16Type(byteReader.getUShort(byteReader).toInt())
            BOOL -> AbiPrimitiveDataType.BoolType(byteReader.getByte(byteReader) == 1)
            else -> throw IllegalArgumentException("Unsupported field type: $fieldType")
        }
    }
}