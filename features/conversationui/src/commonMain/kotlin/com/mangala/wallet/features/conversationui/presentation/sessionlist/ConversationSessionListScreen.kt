package com.mangala.wallet.features.conversationui.presentation.sessionlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.MoreVertical
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Trash
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.EditButton
import com.mangala.wallet.features.addressbook.icon.contacticon.OutlineStar
import com.mangala.wallet.features.addressbook.icon.contacticon.Star
import com.mangala.wallet.features.conversationui.domain.model.ConversationSession
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ConversationSessionListScreen : BaseScreen<ConversationSessionListScreenModel>() {

    override val screenName: String = "CONVERSATION_SESSION_LIST"
    override val screenClassName: String = ConversationSessionListScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ConversationSessionListScreenModel {
        return getScreenModel()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ScreenContent(screenModel: ConversationSessionListScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsState()
        var showDeleteConfirmation by remember { mutableStateOf<ConversationSession?>(null) }

        LaunchedEffect(Unit) {
            screenModel.navigationEvents.collect { event ->
                when (event) {
                    is ConversationSessionListScreenModel.NavigationEvent.NavigateToConversation -> {
                        // Navigate to conversation screen with sessionId
                        val conversationScreen = ScreenRegistry.get(SharedScreen.ConversationUiScreen(event.sessionId))
                        navigator.push(conversationScreen)
                    }
                    is ConversationSessionListScreenModel.NavigationEvent.NavigateToNewConversation -> {
                        // Navigate to new conversation (no sessionId)
                        val conversationScreen = ScreenRegistry.get(SharedScreen.ConversationUiScreen(null))
                        navigator.push(conversationScreen)
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                MangalaWalletTopBarCenteredTitle(
                    title = "Conversations",
                    modifier = Modifier.statusBarsPadding(),
                    navigationIcon = {},
                    trailingButton = {
                        IconButton(
                            onClick = { screenModel.createNewSession() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Conversation",
                                tint = MaterialTheme.mangalaColors.textPrimary
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.mangalaColors.bg)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.mangalaColors.textLink
                        )
                    }
                    uiState.sessions.isEmpty() -> {
                        EmptySessionList(
                            onCreateNew = { screenModel.createNewSession() }
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(
                                items = uiState.sessions,
                                key = { it.id }
                            ) { session ->
                                SessionItem(
                                    session = session,
                                    isActive = session.id == uiState.currentSessionId,
                                    onClick = { screenModel.navigateToSession(session.id) },
                                    onDelete = { showDeleteConfirmation = session }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Delete confirmation dialog
        showDeleteConfirmation?.let { session ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = null },
                title = { Text("Delete Conversation?") },
                text = { 
                    Text("Are you sure you want to delete \"${session.title ?: "Untitled Conversation"}\"? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            screenModel.deleteSession(session.id)
                            showDeleteConfirmation = null
                        }
                    ) {
                        Text("Delete", color = Color(0xFFE53935))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptySessionList(
    onCreateNew: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.mangalaColors.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No conversations yet",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.mangalaColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start a new conversation to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.mangalaColors.textSecondary
        )
        Spacer(modifier = Modifier.height(24.dp))
        MangalaGradientButton(
            onClick = onCreateNew,
            size = MangalaButtonSize.Big,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Conversation")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionItem(
    session: ConversationSession,
    isActive: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                MaterialTheme.mangalaColors.bgBadge.copy(alpha = 0.1f)
            } else {
                MaterialTheme.mangalaColors.bgInnerCard
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Session icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isActive) {
                            MaterialTheme.mangalaColors.bgBadge
                        } else {
                            MaterialTheme.mangalaColors.bgBadge.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    tint = if (isActive) {
                        Color.White
                    } else {
                        MaterialTheme.mangalaColors.bgBadge
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Session details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = session.title ?: "Untitled Conversation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            containerColor = MaterialTheme.mangalaColors.bgBadge
                        ) {
                            Text("Active", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${session.messages.size} messages",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.mangalaColors.textSecondary
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.mangalaColors.textSecondary
                    )
                    Text(
                        text = formatTime(session.lastUpdatedTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.mangalaColors.textSecondary
                    )
                }
            }

            // Delete button
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.mangalaColors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun formatTime(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    
    return when {
        localDateTime.date == now.date -> {
            // Today - show time
            "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
        }
        localDateTime.date.dayOfYear == now.date.dayOfYear - 1 && 
        localDateTime.date.year == now.date.year -> {
            // Yesterday
            "Yesterday"
        }
        else -> {
            // Show date
            "${localDateTime.dayOfMonth}/${localDateTime.monthNumber}/${localDateTime.year}"
        }
    }
}