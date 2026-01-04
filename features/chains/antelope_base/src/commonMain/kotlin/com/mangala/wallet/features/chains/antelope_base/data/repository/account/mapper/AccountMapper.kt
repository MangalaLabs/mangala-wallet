package com.mangala.wallet.features.chains.antelope_base.data.repository.account.mapper

import com.mangala.antelope.base.api.model.GetAccountResponse
import com.mangala.antelope.base.api.model.GetAccountsByAuthorizersResponse
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeKey
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeRequiredAuth
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeRequiredAuthAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeRequiredAuthWaits
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountEntity
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.ext.toBoolean
import com.mangala.wallet.utils.ext.toLong
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun AntelopeAccountEntity.toAccount() = AntelopeAccount(
    accountName = account_name,
    isActive = is_active.toBoolean(),
    isTemp = is_temp.toBoolean(),
    createAccountState = AntelopeAccount.CreateAccountState.valueOf(create_account_state),
    purchaseToken = purchase_token,
    purchaseId = purchase_id,
    permissions = emptyList(),
    coreLiquidBalance = core_liquid_balance,
    cpuLimit = AntelopeAccount.ResourceLimit(
        max = cpu_limit_max,
        available = cpu_limit_available,
        currentUsed = cpu_limit_current_used
    ),
    netLimit = AntelopeAccount.ResourceLimit(
        max = net_limit_max,
        available = net_limit_available,
        currentUsed = net_limit_current_used
    ),
    ramQuota = ram_quota,
    ramUsage = ram_usage,
    rexBalance = rex_balance,
    selfDelegatedBandwidthCpuWeight = self_delegated_bandwidth_cpu_weight,
    selfDelegatedBandwidthNetWeight = self_delegated_bandwidth_net_weight,
    totalResources = if (total_resources_cpu_weight != null && total_resources_net_weight != null) {
        AntelopeAccount.TotalResources(
            netWeight = BalanceFormatter.deserialize(total_resources_net_weight),
            cpuWeight = BalanceFormatter.deserialize(total_resources_cpu_weight)
        )
    } else null,
    lastUpdated = last_updated.let { Instant.fromEpochMilliseconds(last_updated) },
    isNotificationRegistered = is_notification_registered.toBoolean()
)

fun GetAccountResponse.toEntity(blockchainUid: String) = AntelopeAccountEntity(
    account_name = accountName.orEmpty(),
    is_active = true.toLong(),
    is_temp = false.toLong(),
    create_account_state = AntelopeAccount.CreateAccountState.DONE.name,
    purchase_token = null,
    purchase_id = null,
    core_liquid_balance = coreLiquidBalance,
    cpu_limit_max = cpuLimit?.max,
    cpu_limit_available = cpuLimit?.available,
    cpu_limit_current_used = cpuLimit?.currentUsed,
    net_limit_max = netLimit?.max,
    net_limit_available = netLimit?.available,
    net_limit_current_used = netLimit?.currentUsed,
    ram_quota = ramQuota,
    ram_usage = ramUsage,
    rex_balance = rexInfo?.rexBalance,
    self_delegated_bandwidth_cpu_weight = selfDelegatedBandwidth?.cpuWeight,
    self_delegated_bandwidth_net_weight = selfDelegatedBandwidth?.netWeight,
    last_updated = Clock.System.now().toEpochMilliseconds(),
    total_resources_cpu_weight = totalResources?.cpuWeight,
    total_resources_net_weight = totalResources?.netWeight,
    blockchain_uid = blockchainUid,
    is_notification_registered = false.toLong(),
    is_deleted = false.toLong()
)

fun GetAccountResponse.toAccount() = AntelopeAccount(
    accountName = accountName.orEmpty(),
    isActive = false,
    isTemp = false,
    createAccountState = AntelopeAccount.CreateAccountState.DONE,
    purchaseToken = null,
    purchaseId = null,
    permissions = permissions?.map {
        AntelopeAccountPermission(
            permissionType = AntelopePermissionType.fromName(it.permName.orEmpty()),
            parent = AntelopePermissionType.fromName(it.parent.orEmpty()),
            requiredAuth = it.requiredAuth.let { requiredAuth ->
                AntelopeRequiredAuth(
                    threshold = requiredAuth?.threshold ?: 0,
                    keys = requiredAuth?.keys?.map { key ->
                        AntelopeKey(
                            key = key.key.orEmpty(),
                            weight = key.weight ?: 0,
                            id = "",
                            isSynced = false
                        )
                    }.orEmpty(),
                    accounts = requiredAuth?.accounts?.map { account ->
                        AntelopeRequiredAuthAccount(
                            permission = AntelopeRequiredAuthAccount.Permission(
                                actor = account.permission?.actor.orEmpty(),
                                permission = account.permission?.permission.orEmpty()
                            ),
                            weight = account.weight ?: 0
                        )
                    }.orEmpty(),
                    waits = requiredAuth?.waits?.map { wait ->
                        AntelopeRequiredAuthWaits(
                            waitSec = wait.waitSec ?: 0,
                            weight = wait.weight ?: 0
                        )
                    }.orEmpty()
                )
            },
            linkedActions = it.linkedActions?.map { linkedAction ->
                linkedAction.action.orEmpty()
            }.orEmpty()
        )
    }.orEmpty(),
    coreLiquidBalance = coreLiquidBalance,
    cpuLimit = AntelopeAccount.ResourceLimit(
        max = cpuLimit?.max,
        available = cpuLimit?.available,
        currentUsed = cpuLimit?.currentUsed
    ),
    netLimit = AntelopeAccount.ResourceLimit(
        max = netLimit?.max,
        available = netLimit?.available,
        currentUsed = netLimit?.currentUsed
    ),
    ramQuota = ramQuota,
    ramUsage = ramUsage,
    rexBalance = rexInfo?.rexBalance,
    selfDelegatedBandwidthCpuWeight = selfDelegatedBandwidth?.cpuWeight,
    selfDelegatedBandwidthNetWeight = selfDelegatedBandwidth?.netWeight,
    totalResources = if (totalResources?.cpuWeight != null && totalResources?.netWeight != null) {
        AntelopeAccount.TotalResources(
            netWeight = BalanceFormatter.deserialize(totalResources?.netWeight.orEmpty()),
            cpuWeight = BalanceFormatter.deserialize(totalResources?.cpuWeight.orEmpty())
        )
    } else null,
    lastUpdated = null,
    isNotificationRegistered = false
)

fun GetAccountsByAuthorizersResponse.toAntelopeAccountByAuthorizer(blockchainUid: String): List<AntelopeAccountByAuthorizer>? =
    accounts?.map {
        AntelopeAccountByAuthorizer(
            accountName = it.accountName.orEmpty(),
            permissionName = it.permissionName.orEmpty(),
            authorizingKey = it.authorizer.orEmpty(),
            weight = it.weight.orZero(),
            threshold = it.threshold.orZero(),
            blockchainUid = blockchainUid
        )
    }