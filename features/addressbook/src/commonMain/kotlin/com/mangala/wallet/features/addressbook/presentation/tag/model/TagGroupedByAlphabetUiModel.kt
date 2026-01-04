package com.mangala.wallet.features.addressbook.presentation.tag.model

import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity

sealed interface TagGroupedByAlphabetUiModel {
    data class TagItem(val tag: TagEntity) : TagGroupedByAlphabetUiModel
    data class AlphabetHeader(val alphabet: String) : TagGroupedByAlphabetUiModel
}