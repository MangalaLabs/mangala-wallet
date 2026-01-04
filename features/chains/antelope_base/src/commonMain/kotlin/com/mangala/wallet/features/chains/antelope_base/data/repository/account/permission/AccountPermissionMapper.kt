package com.mangala.wallet.features.chains.antelope_base.data.repository.account.permission

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeKey
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeRequiredAuth
import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountPermissionEntity

fun AntelopeAccountPermissionEntity.toAntelopeAccountPermission(keys: List<AntelopeKey>) = AntelopeAccountPermission(
    permissionType = AntelopePermissionType.fromName(permission_name),
    parent = AntelopePermissionType.fromName(parent),
    requiredAuth = AntelopeRequiredAuth(0, keys, emptyList(), emptyList()),
    linkedActions = emptyList(),
)

fun AntelopeAccountPermission.toAntelopeAccountPermissionEntity(accountName: String, blockchainUid: String) = AntelopeAccountPermissionEntity(
    permission_name = permissionType.permissionName,
    parent = parent.permissionName,
    account_name = accountName,
    blockchain_uid = blockchainUid
)