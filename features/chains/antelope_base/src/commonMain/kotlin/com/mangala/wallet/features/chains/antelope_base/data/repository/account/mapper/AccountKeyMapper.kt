package com.mangala.wallet.features.chains.antelope_base.data.repository.account.mapper

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeKey
import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountKeyEntity
import com.mangala.wallet.utils.ext.toLong

fun AntelopeAccountKeyEntity.toAntelopeKey() = AntelopeKey(
    key = public_key,
    weight = weight.toInt(),
    id = id,
    isSynced = is_synced == 1L
)

fun AntelopeKey.toAntelopeAccountKeyEntity(accountName: String, permissionName: String, blockchainUid: String) = AntelopeAccountKeyEntity(
    public_key = key,
    weight = weight.toLong(),
    id = id,
    account_name = accountName,
    permission_name = permissionName,
    is_synced = isSynced.toLong(),
    blockchain_uid = blockchainUid
)