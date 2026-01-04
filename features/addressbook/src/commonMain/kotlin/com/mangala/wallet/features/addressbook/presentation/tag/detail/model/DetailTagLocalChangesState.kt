package com.mangala.wallet.features.addressbook.presentation.tag.detail.model

data class DetailTagLocalChangesState(
    val removedContactIds: Set<String> = emptySet()
)