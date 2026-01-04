/*
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

package com.mangala.antelope.base.core.error

object ErrorConstants {
    //EOSFormatter() Errors
    /**
     * The private key provided is not in the EOS format.
     */
    const val INVALID_EOS_PRIVATE_KEY = "The EOS private key provided is invalid!"

    /**
     * The public key provided is not in the EOS format.
     */
    const val INVALID_EOS_PUBLIC_KEY = "The EOS public key provided is invalid!"

    /**
     * An error occurred while Base58 decoding the EOS key.
     */
    const val BASE58_DECODING_ERROR = "An error occurred while Base58 decoding the EOS key!"

    /**
     * The key provided for Base58 decoding was empty.
     */
    const val BASE58_EMPTY_KEY = "Input key to decode can't be empty!"

    /**
     * Input key, checksum or key type were empty and are needed for validation.
     */
    const val BASE58_EMPTY_CHECKSUM_OR_KEY =
        "Input key, checksum and key type to validate can't be empty!"

    /**
     * Input key, checksum or key type were empty and are needed for validation.
     */
    const val BASE58_EMPTY_CHECKSUM_OR_KEY_OR_KEY_TYPE =
        "Input key, checksum and key type to validate can't be empty!"

    /**
     * Input key has invalid checksum.
     */
    const val BASE58_INVALID_CHECKSUM = "Input key has invalid checksum!"

    /**
     * Error converting DER encoded key to PEM format.
     */
    const val DER_TO_PEM_CONVERSION = "Error converting DER encoded key to PEM format!"

    /**
     * The algorithm used to generate the object is unsupported.
     */
    const val UNSUPPORTED_ALGORITHM = "Unsupported algorithm!"

    /**
     * The private key is not in PEM format.
     */
    const val INVALID_PEM_PRIVATE_KEY = "This is not a PEM formatted private key!"

    /**
     * The private key is not in DER format.
     */
    const val INVALID_DER_PRIVATE_KEY = "DER format of private key is incorrect!"

    /**
     * Checksum generation failed.
     */
    const val CHECKSUM_GENERATION_ERROR = "Could not generate checksum!"

    /**
     * The object could not be Base58 encoded.
     */
    const val BASE58_ENCODING_ERROR = "Unable to Base58 encode object!"

    /**
     * The public key could not be decompressed.
     */
    const val PUBLIC_KEY_DECOMPRESSION_ERROR = "Problem decompressing public key!"

    /**
     * The public key could not be compressed.
     */
    const val PUBLIC_KEY_COMPRESSION_ERROR = "Problem compressing public key!"

    /**
     * The public key provided for decoding was empty.
     */
    const val PUBLIC_KEY_IS_EMPTY = "Input key to decode can't be empty!"

    /**
     * Chain id or serialized transaction parameter was empty.
     */
    const val EMPTY_INPUT_PREPARE_SERIALIZIED_TRANS_FOR_SIGNING =
        "Chain id, serialized transaction, and serialized context free data can't be empty!"

    /**
     * The signable transaction parameter was empty.
     */
    const val EMPTY_INPUT_EXTRACT_SERIALIZIED_TRANS_FROM_SIGNABLE =
        "Signable transaction can't be empty!"

    /**
     * The length of the signable transaction was incorrect and the serialized transaction could not
     * be extracted.
     */
    const val INVALID_INPUT_SIGNABLE_TRANS_LENGTH_EXTRACT_SERIALIZIED_TRANS_FROM_SIGNABLE =
        "Length of the signable transaction must be larger than %s"

    /**
     * Unable to extract the serialized transaction from the signable transaction.
     */
    const val EXTRACT_SERIALIZIED_TRANS_FROM_SIGNABLE_ERROR =
        "Something went wrong when trying to extract serialized transaction from signable transaction."

    /**
     * Signature formatting failed.
     */
    const val SIGNATURE_FORMATTING_ERROR = "An error occured formating the signature!"

    /**
     * A public key could not be recovered from the signature.
     */
    const val COULD_NOT_RECOVER_PUBLIC_KEY_FROM_SIG = "Could not recover public key from Signature."

    /**
     * The signature provided failed the canonical check.
     */
    const val NON_CANONICAL_SIGNATURE = "Input signature is not canonical."

    /**
     * The public key could not be extracted from the provided private key.  The private key is most
     * likely invalid.
     */
    const val PUBLIC_KEY_COULD_NOT_BE_EXTRACTED_FROM_PRIVATE_KEY = "This is not a private key!"

    // ABIProviderImpl Errors
    const val NO_RESPONSE_RETRIEVING_ABI = "No response retrieving ABI."
    const val MISSING_ABI_FROM_RESPONSE = "Missing ABI from GetRawAbiResponse."
    const val CALCULATED_HASH_NOT_EQUAL_RETURNED =
        "Calculated ABI hash does not match returned hash."
    const val REQUESTED_ACCCOUNT_NOT_EQUAL_RETURNED =
        "Requested account name does not match returned account name."
    const val NO_ABI_FOUND = "No ABI found for requested account name."
    const val ERROR_RETRIEVING_ABI = "Error retrieving ABI from the chain."
    //PEMProcessor Errors
    /**
     * The object provided is not in the PEM format.
     */
    const val ERROR_READING_PEM_OBJECT = "Error reading PEM object!"

    /**
     * The PEM object could not be parsed.
     */
    const val ERROR_PARSING_PEM_OBJECT = "Error parsing PEM object!"

    /**
     * There was no key data in the PEM object.
     */
    const val KEY_DATA_NOT_FOUND = "Key data not found in PEM object!"

    /**
     * PEM object could not be read.
     */
    const val INVALID_PEM_OBJECT = "Cannot read PEM object!"
    //TransactionProcessor Errors
    /**
     * Error message get thrown if actions list is empty during processes of [TransactionProcessor].
     */
    const val TRANSACTION_PROCESSOR_ACTIONS_EMPTY_ERROR_MSG = "Action list can't be empty!"

    /**
     * Error message get thrown if [IRPCProvider.getInfo] thrown exception during processes of [TransactionProcessor]
     */
    const val TRANSACTION_PROCESSOR_RPC_GET_INFO = "Error happened on calling GetInfo RPC."

    /**
     * Error message get thrown if [IRPCProvider.getBlockInfo] thrown exception during process of [TransactionProcessor.prepare]
     */
    const val TRANSACTION_PROCESSOR_PREPARE_RPC_GET_BLOCK_INFO =
        "Error happened on calling GetBlockInfo RPC."

    /**
     * Error message get thrown if chain id from [GetInfoResponse.getChainId] does not match with the input chain id
     */
    const val TRANSACTION_PROCESSOR_PREPARE_CHAINID_NOT_MATCH =
        "Provided chain id %s does not match chain id %s"

    /**
     * Error message get thrown if chain id from [GetInfoResponse.getChainId] is empty.
     */
    const val TRANSACTION_PROCESSOR_PREPARE_CHAINID_RPC_EMPTY = "Chain id from back end is empty!"

    /**
     * Error message get thrown if parsing head block time from [GetInfoResponse.getHeadBlockTime] get error
     */
    const val TRANSACTION_PROCESSOR_TAPOS_BLOCK_TIME_PARSE_ERROR =
        "Failed to parse TAPOS block time"

    /**
     * Error message get thrown if making clone version of transaction is failed.
     */
    const val TRANSACTION_PROCESSOR_PREPARE_CLONE_ERROR = "Error happened on cloning transaction."

    /**
     * Error message get thrown if making clone version of transaction is failed by [ClassNotFoundException]
     */
    const val TRANSACTION_PROCESSOR_PREPARE_CLONE_CLASS_NOT_FOUND =
        "Transaction class was not found"

    /**
     * Error message get thrown if the current transaction inside [TransactionProcessor] has not been initialized or empty.
     */
    const val TRANSACTION_PROCESSOR_TRANSACTION_HAS_TO_BE_INITIALIZED =
        "Transaction must be initialized before this method could be called! call prepare for initialize Transaction"

    /**
     * Error message get thrown if [IABIProvider.getAbi] get error.
     */
    const val TRANSACTION_PROCESSOR_GET_ABI_ERROR =
        "Error happened on getting abi for contract [%s]"

    /**
     * Error message get thrown if Action's serialization process execute successfully but its result is empty.
     */
    const val TRANSACTION_PROCESSOR_SERIALIZE_ACTION_WORKED_BUT_EMPTY_RESULT =
        "Serialization of action worked fine but got back empty result!"

    /**
     * Error message get thrown if Transaction's serialization process execute successfully but its result is empty.
     */
    const val TRANSACTION_PROCESSOR_SERIALIZE_TRANSACTION_WORKED_BUT_EMPTY_RESULT =
        "Serialization of transaction worked fine but got back empty result!"

    /**
     * Error message get thrown if Action's serialization process get error by calling [ISerializationProvider.serialize]
     */
    const val TRANSACTION_PROCESSOR_SERIALIZE_ACTION_ERROR =
        "Error happened on serializing action [%s]"

    /**
     * Error message get thrown if Transaction's serialization process get error by calling [ISerializationProvider.serializeTransaction]
     */
    const val TRANSACTION_PROCESSOR_SERIALIZE_TRANSACTION_ERROR =
        "Error happened on serializing transaction"

    /**
     * Error message get thrown if [ISignatureProvider.getAvailableKeys] returns error.
     */
    const val TRANSACTION_PROCESSOR_GET_AVAILABLE_KEY_ERROR =
        "Error happened on getAvailableKeys from SignatureProvider!"

    /**
     * Error message get thrown if [ISignatureProvider.getAvailableKeys] returns no key.
     */
    const val TRANSACTION_PROCESSOR_GET_AVAILABLE_KEY_EMPTY =
        "Signature provider return no available key"

    /**
     * Error message get thrown if [IRPCProvider.getRequiredKeys] get error.
     */
    const val TRANSACTION_PROCESSOR_RPC_GET_REQUIRED_KEYS =
        "Error happened on calling getRequiredKeys RPC call."

    /**
     * Error message get thrown if [IRPCProvider.getRequiredKeys] returns no key.
     */
    const val GET_REQUIRED_KEY_RPC_EMPTY_RESULT = "GetRequiredKeys RPC returned no required keys"

    /**
     * Error message get thrown if [ISignatureProvider.signTransaction] returns error
     */
    const val TRANSACTION_PROCESSOR_SIGN_TRANSACTION_ERROR =
        "Error happened on calling sign transaction of Signature provider"

    /**
     * Error message get thrown if [ISignatureProvider.signTransaction] return empty serialized transaction.
     */
    const val TRANSACTION_PROCESSOR_SIGN_TRANSACTION_TRANS_EMPTY_ERROR =
        "Serialized transaction come back empty from Signature Provider"

    /**
     * Error message get thrown if [ISignatureProvider.signTransaction] return no signature.
     */
    const val TRANSACTION_PROCESSOR_SIGN_TRANSACTION_SIGN_EMPTY_ERROR =
        "Signatures come back empty from Signature Provider"

    /**
     * Error message get thrown if [EosioTransactionSignatureResponse] which return from [ISignatureProvider.signTransaction] has modified serialized transaction but [TransactionProcessor.isTransactionModificationAllowed] is false
     */
    const val TRANSACTION_IS_NOT_ALLOWED_TOBE_MODIFIED =
        "The transaction is not allowed to be modified but was modified by signature provider!"

    /**
     * Error message get thrown if [ISerializationProvider.deserializeTransaction] returns error during deserialize modified serialized transaction inside [EosioTransactionSignatureResponse] which return from [ISignatureProvider.signTransaction]
     */
    const val TRANSACTION_PROCESSOR_GET_SIGN_DESERIALIZE_TRANS_ERROR =
        "Error happened on calling deserializeTransaction to refresh transaction object with new values"

    /**
     * Error message get thrown if [IRPCProvider.sendTransaction] returns error.
     */
    const val TRANSACTION_PROCESSOR_RPC_SEND_TRANSACTION =
        "Error happened on calling sendTransaction RPC call"

    /**
     * Error message get thrown if [TransactionProcessor.serialize]
     */
    const val TRANSACTION_PROCESSOR_SERIALIZE_ERROR =
        "Error happened on calling serializeTransaction"

    /**
     * Error message get thrown if error happens during creating signature process of [TransactionProcessor.sign]
     */
    const val TRANSACTION_PROCESSOR_SIGN_CREATE_SIGN_REQUEST_ERROR =
        "Error happened on creating signature request for Signature Provider to sign!"

    /**
     * Error message get thrown if error happens during sending transaction to backend
     */
    const val TRANSACTION_PROCESSOR_BROADCAST_TRANS_ERROR =
        "Error happened on sending transaction to chain!"

    /**
     * Error message get thrown if required keys from [GetRequiredKeysResponse] is not subset of keys from [ISignatureProvider.getAvailableKeys]
     */
    const val TRANSACTION_PROCESSOR_REQUIRED_KEY_NOT_SUBSET =
        "Required keys from back end are not available in available keys from Signature Provider."

    /**
     * Error message get thrown if serialized transaction is empty or has not been populated during process of [TransactionProcessor.broadcast]
     */
    const val TRANSACTION_PROCESSOR_BROADCAST_SERIALIZED_TRANSACTION_EMPTY =
        "Serialized Transaction is empty or has not been populated. Make sure to call prepare then sign before calling broadcast"

    /**
     * Error message get thrown if serialized transaction is empty or has not been populated during process of [TransactionProcessor.signAndBroadcast] ()}
     */
    const val TRANSACTION_PROCESSOR_SIGN_BROADCAST_SERIALIZED_TRANSACTION_EMPTY =
        "Serialized Transaction is empty or has not been populated. Make sure to call prepare then sign before calling sign and broadcast"

    /**
     * Error message get thrown if [ISignatureProvider.signTransaction] return error during process of [TransactionProcessor.sign]
     */
    const val TRANSACTION_PROCESSOR_SIGN_SIGNATURE_RESPONSE_ERROR =
        "Error happened on the response of getSignature."

    /**
     * Error message get thrown if [ISerializationProvider.deserializeTransaction] returns empty result during deserialize modified serialized transaction inside [EosioTransactionSignatureResponse] which return from [ISignatureProvider.signTransaction]
     */
    const val TRANSACTION_PROCESSOR_GET_SIGN_DESERIALIZE_TRANS_EMPTY_ERROR =
        "Deserialized transaction is null or empty"

    /**
     * Error message get thrown if [TransactionProcessor.getSignatures] is empty during process of [TransactionProcessor.broadcast]
     */
    const val TRANSACTION_PROCESSOR_BROADCAST_SIGN_EMPTY =
        "Can't call broadcast because Signature is empty. Make sure of calling sign before calling broadcast."

    /**
     * Error message get thrown if [TransactionProcessor.getSignatures] is empty during process of [TransactionProcessor.signAndBroadcast] ()}
     */
    const val TRANSACTION_PROCESSOR_SIGN_BROADCAST_SIGN_EMPTY =
        "Can't call sign and broadcast because Signature is empty. Make sure of calling sign before calling sign and broadcast."
}
