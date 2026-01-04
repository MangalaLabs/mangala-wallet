package com.mangala.wallet.features.addressbook.presentation.common

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey

/**
 * Extension functions for LazyListScope to handle paging items with headers
 * Following best practices for Compose and Paging3 integration
 */

/**
 * Generic extension for rendering paging items with different item types
 * Supports headers, content items, and proper key generation
 */
inline fun <T : Any> LazyListScope.itemsIndexed(
    items: LazyPagingItems<T>,
    noinline key: ((index: Int, item: T) -> Any)? = null,
    crossinline contentType: (index: Int, item: T) -> Any? = { _, _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit
) {
    items(
        count = items.itemCount,
        key = if (key != null) { index ->
            val item = items.peek(index)
            if (item != null) key(index, item) else index
        } else null,
        contentType = { index ->
            val item = items.peek(index)
            if (item != null) contentType(index, item) else null
        }
    ) { index ->
        items[index]?.let { item ->
            itemContent(index, item)
        }
    }
}

/**
 * Specialized extension for handling items that can be headers or content
 * Properly manages sticky headers and regular items with type-safe handling
 */
@Suppress("UNCHECKED_CAST")
inline fun <T : Any> LazyListScope.itemsWithHeaders(
    items: LazyPagingItems<T>,
    crossinline isHeader: (T) -> Boolean,
    crossinline headerKey: (index: Int, item: T) -> Any,
    crossinline itemKey: (index: Int, item: T) -> Any,
    crossinline headerContent: @Composable (item: T) -> Unit,
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
    var index = 0
    while (index < items.itemCount) {
        val currentItem = items.peek(index)
        if (currentItem != null) {
            if (isHeader(currentItem)) {
                stickyHeader(
                    key = headerKey(index, currentItem),
                    contentType = "header"
                ) {
                    headerContent(currentItem)
                }
            } else {
                item(
                    key = itemKey(index, currentItem),
                    contentType = "item"
                ) {
                    itemContent(currentItem)
                }
            }
        }
        index++
    }
}

/**
 * Extension specifically for contact list with alphabet headers
 * Encapsulates the logic for rendering contacts grouped by alphabet
 */
fun LazyListScope.contactsWithAlphabetHeaders(
    items: LazyPagingItems<com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel>,
    headerContent: @Composable (alphabet: String) -> Unit,
    itemContent: @Composable LazyItemScope.(contact: com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel) -> Unit
) {
    itemsWithHeaders(
        items = items,
        isHeader = { it is com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel.AlphabetHeader },
        headerKey = { index, item ->
            val previousItemId = if (index > 0) {
                (items.peek(index - 1) as? com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel.ContactItem)?.contact?.contactId
            } else null
            com.mangala.wallet.features.addressbook.presentation.contact.list.ContactListKeyGenerator.generateKey(item, index, previousItemId)
        },
        itemKey = { index, item ->
            val previousItemId = if (index > 0) {
                (items.peek(index - 1) as? com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel.ContactItem)?.contact?.contactId
            } else null
            com.mangala.wallet.features.addressbook.presentation.contact.list.ContactListKeyGenerator.generateKey(item, index, previousItemId)
        },
        headerContent = { item ->
            headerContent((item as com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel.AlphabetHeader).alphabet)
        },
        itemContent = { item ->
            itemContent((item as com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel.ContactItem).contact)
        }
    )
}