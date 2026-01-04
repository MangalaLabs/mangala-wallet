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
package com.memtrip.eos.chain.actions.transaction.esr

import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi

data class EsrSigningRequestArgs(
    val chainAlias: Int? = null,
    private val chainId: String? = null,
    val transaction: TransactionAbi? = null,
    val actions: List<ActionAbi>? = null,
    val identityRequest: EsrIdentityArgs? = null,
    private val flags: Int,
    val callback: String,
    val info: List<EsrInfoArgs>
) {

    val isIdentityRequest = identityRequest != null
    val resolvedChainId: String?
        get() = chainId ?: run {
            // https://github.com/eosio-eps/EEPs/blob/master/EEPS/eep-7.md#chain-aliases
            when (chainAlias) {
                0 -> null // Multichain
                1 -> "aca376f206b8fc25a6ed44dbdc66547c36c6c33e3a119ffbeaef943642f0e906" // EOS
                2 -> "4667b205c6838ef70ff7988f6e8257e8be0e1284a2f59699054a018f743b1d11" // Telos
                3 -> "038f4b0fc8ff18a4f0842a8f0564611f6e96e8535901dd45e43ac8691a1c4dca" // Jungle
                4 -> "5fff1dae8dc8e2fc4d5b23b2c7665c97f9e9d8edf2b6485a86ba311c25639191" // Kylin
                5 -> "73647cde120091e0a4b85bced2f3cfdb3041e266cbbe95cee59b73235a1b3b6f" // WORBLI
                6 -> "d5a3d18fbb3c084e3b1f3fa98c21014b5f3db536cc15d08f9f6479517c6a3d86" // BOS
                7 -> "cfe6486a83bad4962f232d48003b1824ab5665c36778141034d75e57b956e422" // MEETONE
                8 -> "b042025541e25a472bffde2d62edd457b7e70cee943412b1ea0f044f88591664" // INSIGHTS
                9 -> "b912d19a6abd2b1b05611ae5be473355d64d95aeff0c09bedc8c166cd6468fe4" // BEOS
                10 -> "1064487b3cd1a897ce03ae5b6a865651747e2e152090f99c1d19d44e01aea5a4" // WAX
                11 -> "384da888112027f0321850a169f737c33e53b388aad48b5adace4bab97f437e0" // PROTON
                12 -> "21dcae42c0182200e93f954a074011f9048a7624c6fe81d3c9541a614a88bd1c" // FIO
                else -> null
            }
        }

    val isMultichainRequest = chainAlias == 0

    val shouldBroadcast = flags and 1 == 1
    val shouldPerformBackgroundCallback = flags and 2 == 2

    val authorizations: List<TransactionAuthorizationAbi> by lazy {
        (transaction?.actions ?: actions)?.map { action -> action.authorization }?.flatten()?.toSet()?.toList()
            ?.filterNot { it.actor == "greymassfuel" }
            ?: emptyList()
    }
    val link: EsrInfoArgs.Link? by lazy {
        info.firstOrNull { it is EsrInfoArgs.Link } as? EsrInfoArgs.Link
    }
}