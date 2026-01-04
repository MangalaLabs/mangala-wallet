package com.mangala.wallet.features.chains.antelope_base.domain.model.proposal

import kotlinx.datetime.Instant

data class TransactionProposalDecoded(
    val expiration: Instant,
    val ref_block_num: Int,
    val ref_block_prefix: Long,
    val max_net_usage_words: Long,
    val max_cpu_usage_ms: Long,
    val delay_sec: Long,
    val actions: List<ActionAbi>,
    val transaction_extensions: List<String>,
    val signatures: List<String>,
    val context_free_data: List<String>
)