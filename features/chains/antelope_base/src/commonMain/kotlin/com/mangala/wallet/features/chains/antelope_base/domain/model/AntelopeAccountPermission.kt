package com.mangala.wallet.features.chains.antelope_base.domain.model

import com.benasher44.uuid.uuid4

data class AntelopeAccountPermission(
    val permissionType: AntelopePermissionType,
    val parent: AntelopePermissionType,
    val requiredAuth: AntelopeRequiredAuth,
    val linkedActions: List<String>
) {
    companion object {
        fun createInitialActivePermission(
            activePublicKey: String,
            isSynced: Boolean
        ): AntelopeAccountPermission {
            return AntelopeAccountPermission(
                AntelopePermissionType.Active,
                AntelopePermissionType.Owner,
                AntelopeRequiredAuth(
                    threshold = 1,
                    keys = listOf(
                        AntelopeKey(
                            uuid4().toString(),
                            key = activePublicKey,
                            1,
                            isSynced
                        )
                    ),
                    accounts = emptyList(),
                    waits = emptyList()
                ),
                emptyList()
            )
        }

        fun createInitialOwnerPermission(
            ownerPublicKey: String,
            isSynced: Boolean
        ): AntelopeAccountPermission {
            return AntelopeAccountPermission(
                AntelopePermissionType.Owner,
                AntelopePermissionType.fromName(""),
                AntelopeRequiredAuth(
                    threshold = 1,
                    keys = listOf(
                        AntelopeKey(
                            uuid4().toString(),
                            key = ownerPublicKey,
                            1,
                            isSynced = isSynced
                        )
                    ),
                    accounts = emptyList(),
                    waits = emptyList()
                ),
                emptyList()
            )
        }
    }
}