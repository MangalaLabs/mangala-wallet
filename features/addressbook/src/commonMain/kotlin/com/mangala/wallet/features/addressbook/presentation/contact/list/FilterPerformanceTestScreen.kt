package com.mangala.wallet.features.addressbook.presentation.contact.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.features.addressbook.presentation.security.SecureActionId
import com.mangala.wallet.ui.theme.MangalaTypography
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import androidx.compose.material.Card
import com.mangala.wallet.features.addressbook.presentation.security.SecureButton
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform


//TODO: for test
class FilterPerformanceTestScreen : Screen, KoinComponent {

    @Composable
    override fun Content() {
        com.mangala.wallet.ui.LocalBottomNavigationVisibility.current.value = false
        
        val screenModel =
            rememberScreenModel { FilterTestScreenModel(get(), get(), get(), get(), get(), get(), get()) }

        FilterPerformanceTestUI(screenModel)
    }
}

@Composable
fun FilterPerformanceTestUI(screenModel: FilterTestScreenModel) {
    var searchQuery by remember { mutableStateOf("") }
    var showFavorites by remember { mutableStateOf(false) }
    var selectedSortOrder by remember { mutableStateOf("name_asc") }
    var selectedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedGroups by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedBlockchains by remember { mutableStateOf<List<String>>(emptyList()) }
    var availableTags by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var availableGroups by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var availableBlockchains by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var executionTime by remember { mutableStateOf(0L) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val filteredContacts by screenModel.filteredContacts.collectAsStateMultiplatform()

    LaunchedEffect(Unit) {
        availableTags = screenModel.loadTags()
        availableGroups = screenModel.loadGroups()
        availableBlockchains = screenModel.loadBlockchains()
    }

    LaunchedEffect(filteredContacts) {
        println("LaunchedEffect: filteredContacts updated, size = ${filteredContacts.size}")
    }

//    val navigator = LocalNavigator.currentOrThrow
//
//    val coordinator = remember { GlobalContext.get().get<SecureAuthFlowCoordinator>() }
//    val secureAuthPolicyProvider = remember { GlobalContext.get().get<SecureAuthPolicyProvider>() }
//
//    val voyagerNavigator = rememberNavigator(
//        navigator = navigator,
//        coordinator = coordinator
//    )
//
//    val secureActionHandler = rememberSecureActionHandler(
//        coordinator = coordinator,
//        navigator = voyagerNavigator
//    )

    // Make the entire screen scrollable
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Filter Performance Test",
                    style = MangalaTypography.Size14Medium(),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Control buttons
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { screenModel.generateTestData(1000) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Generate 1000 Contacts")
                        }

                        Button(
                            onClick = { screenModel.generate200Groups() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Generate 200 Groups")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { screenModel.generate50Transactions() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate 50 Transactions")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SecureButton(
                        actionId = SecureActionId.AddContact,
                        onClick = {
                            // Now simply call testFilter without all the auth boilerplate
                            screenModel.testFilter(
                                query = searchQuery,
                                tagIds = selectedTags,
                                groupIds = selectedGroups,
                                blockchainIds = selectedBlockchains,
                                onlyFavorites = showFavorites,
                                sortOrder = selectedSortOrder
                            )
                        },
                        onCancel = {
                            // Handle cancellation
                            println("On auth flow cancel")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Run Filter Test")
                    }
                }

                println("filteredContacts: ${filteredContacts.size}")

                Spacer(modifier = Modifier.height(16.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Query") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Filter options
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Favorites Only:", modifier = Modifier.width(120.dp))
                    Switch(
                        checked = showFavorites,
                        onCheckedChange = { showFavorites = it }
                    )
                }

                // Sort order selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Sort Order:", modifier = Modifier.width(120.dp))
                    var expanded by remember { mutableStateOf(false) }

                    Button(onClick = { expanded = true }) {
                        Text(
                            when (selectedSortOrder) {
                                "name_asc" -> "Name (A-Z)"
                                "name_desc" -> "Name (Z-A)"
                                "date_desc" -> "Date (Newest)"
                                "date_asc" -> "Date (Oldest)"
                                else -> "Name (A-Z)"
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                selectedSortOrder = "name_asc"
                                expanded = false
                            }
                        ) {
                            Text("Name (A-Z)")
                        }
                        DropdownMenuItem(
                            onClick = {
                                selectedSortOrder = "name_desc"
                                expanded = false
                            }
                        ) {
                            Text("Name (Z-A)")
                        }
                        DropdownMenuItem(
                            onClick = {
                                selectedSortOrder = "date_desc"
                                expanded = false
                            }
                        ) {
                            Text("Date (Newest)")
                        }
                        DropdownMenuItem(
                            onClick = {
                                selectedSortOrder = "date_asc"
                                expanded = false
                            }
                        ) {
                            Text("Date (Oldest)")
                        }
                    }
                }

                // Filter sections with chips
                Text("Tags:", style = MangalaTypography.Size14Medium())
                FilterChips(
                    items = availableTags,
                    selectedIds = selectedTags,
                    onSelectionChanged = { selectedTags = it }
                )

                Text("Groups:", style = MangalaTypography.Size14Medium())
                FilterChips(
                    items = availableGroups,
                    selectedIds = selectedGroups,
                    onSelectionChanged = { selectedGroups = it }
                )

                Text("Blockchains:", style = MangalaTypography.Size14Medium())
                FilterChips(
                    items = availableBlockchains,
                    selectedIds = selectedBlockchains,
                    onSelectionChanged = { selectedBlockchains = it }
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // Results section
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Results: ${filteredContacts.size} contacts (${executionTime}ms)",
                            style = MangalaTypography.Size14Medium()
                        )

                        IconButton(onClick = {
                            selectedTags = emptyList()
                            selectedGroups = emptyList()
                            selectedBlockchains = emptyList()
                            showFavorites = false
                            searchQuery = ""
                            selectedSortOrder = "name_asc"
                            executionTime = 0
                            screenModel.clearFilter()
                        }) {
                            Icon(Icons.Default.Delete, "Clear filters")
                        }
                    }

                    if (errorMessage != null) {
                        Text(
                            "Error: $errorMessage",
                            color = ColorsNew.black,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

//                    // Contact results
//                    filteredContacts.forEach { contact ->
//                        println("Contact: $contact")
//                        Card(
//                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
//                        ) {
//                            Column(modifier = Modifier.padding(16.dp)) {
//                                Text(
//                                    text = contact.name,
//                                    style = MangalaTypography.Size14Medium()
//                                )
//                                if (contact.notes != null) {
//                                    Text(
//                                        text = contact.notes,
//                                        style = MangalaTypography.Size14Medium()
//                                    )
//                                }
//                            }
//                        }
//                    }


                    // Replace the contacts display loop with this
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (filteredContacts.isEmpty() && !isLoading) {
                            Text(
                                "No contacts found. Please run a filter test or generate more data.",
                                style = MangalaTypography.Size14Medium(),
                                modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally)
                            )
                        } else {
                            // Add this debug text
                            Text(
                                "Displaying ${filteredContacts.size} contacts",
                                style = MangalaTypography.Size14Medium(),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Display each contact
                            filteredContacts.forEach { contact ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = contact.name,
                                            style = MangalaTypography.Size14Medium()
                                        )
                                        if (contact.notes != null) {
                                            Text(
                                                text = contact.notes,
                                                style = MangalaTypography.Size14Medium()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun FilterChips(
    items: List<Pair<String, String>>,
    selectedIds: List<String>,
    onSelectionChanged: (List<String>) -> Unit
) {
    // Make chips wrap to multiple rows using FlowRow
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        items.forEach { (id, name) ->
            FilterChip(
                selected = selectedIds.contains(id),
                onClick = {
                    val newSelectedIds = if (selectedIds.contains(id)) {
                        selectedIds - id
                    } else {
                        selectedIds + id
                    }
                    onSelectionChanged(newSelectedIds)
                },
                content = { Text(name) }
            )
        }
    }
}