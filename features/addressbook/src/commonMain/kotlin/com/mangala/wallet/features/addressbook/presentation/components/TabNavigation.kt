package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun TabNavigation(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    val tabs = remember {
        listOf("Favorites", "My Recents", "Contacts", "Tags")
    }

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.mangalaColors.bg,
        contentColor = MaterialTheme.mangalaColors.textPrimary,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                height = 2.dp,
                color = MaterialTheme.mangalaColors.textPrimary
            )
        },
        divider = { },
        edgePadding = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MangalaTypography.Size14Medium()
                    )
                },
                selectedContentColor = MaterialTheme.mangalaColors.textPrimary,
                unselectedContentColor = MaterialTheme.mangalaColors.textSecondary,
                modifier = Modifier
                    .wrapContentWidth()
                    .semantics {
                        contentDescription = "Switch to $title tab"
                    }
            )
        }
    }
}