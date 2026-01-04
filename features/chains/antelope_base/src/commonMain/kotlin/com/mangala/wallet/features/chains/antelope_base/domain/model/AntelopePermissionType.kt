package com.mangala.wallet.features.chains.antelope_base.domain.model

sealed interface AntelopePermissionType {
    val permissionName: String

    object Owner : AntelopePermissionType {
        override val permissionName: String
            get() = "owner"
    }

    object Active : AntelopePermissionType {
        override val permissionName: String
            get() = "active"
    }

    data class Other(override val permissionName: String) : AntelopePermissionType

    companion object {
        fun fromName(permissionName: String): AntelopePermissionType {
            return when (permissionName) {
                Owner.permissionName -> Owner
                Active.permissionName -> Active
                else -> Other(permissionName)
            }
        }
    }
}