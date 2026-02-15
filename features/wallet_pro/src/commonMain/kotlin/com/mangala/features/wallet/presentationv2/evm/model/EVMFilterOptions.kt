package com.mangala.features.wallet.presentationv2.evm.model

data class EVMFilterOptions(
    val hideSmallBalances: Boolean = false,
    val sortBy: EVMTokenSortBy = EVMTokenSortBy.VALUE,
    val viewMode: EVMViewMode = EVMViewMode.SINGLE_ACCOUNT
)

enum class EVMTokenSortBy {
    NAME,
    VALUE
}

enum class EVMViewMode {
    SINGLE_ACCOUNT,
    ALL_ACCOUNTS
}
