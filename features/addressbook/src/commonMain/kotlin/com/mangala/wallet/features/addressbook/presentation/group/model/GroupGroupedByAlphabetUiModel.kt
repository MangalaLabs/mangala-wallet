package com.mangala.wallet.features.addressbook.presentation.group.model

import com.mangala.wallet.features.addressbook.data.model.group.GroupModel

sealed interface GroupGroupedByAlphabetUiModel {
    data class GroupItem(val group: GroupModel) : GroupGroupedByAlphabetUiModel
    data class AlphabetHeader(val alphabet: String) : GroupGroupedByAlphabetUiModel
}