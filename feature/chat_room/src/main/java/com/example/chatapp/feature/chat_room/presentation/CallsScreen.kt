package com.example.chatapp.feature.chat_room.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.chatapp.core.ui.component.UserAvatar
import com.example.chatapp.feature.chat_room.presentation.model.CallItem
import com.example.chatapp.feature.chat_room.presentation.model.CallType
import com.example.chatapp.feature.chat_room.presentation.model.CallsEffect
import com.example.chatapp.feature.chat_room.presentation.model.CallsIntent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsScreen(
    viewModel: CallsViewModel
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is CallsEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is CallsEffect.NavigateToCall -> {
                    /* TODO: launch call UI */
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Recent Calls",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    // Start New call button (from Figma)
                    FilledTonalButton(
                        onClick = { /* TODO: start new call */ },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Start New",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Most Contacted section (Figma top area)
                item {
                    MostContactedSection()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Missed Calls card
                if (state.missedCallsCount > 0) {
                    item {
                        MissedCallsCard(
                            count = state.missedCallsCount,
                            onClick = { viewModel.onIntent(CallsIntent.ViewAllMissed) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Call list header
                item {
                    Text(
                        "TODAY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }

                items(state.recentCalls, key = { it.id }) { call ->
                    CallItemRow(
                        call = call,
                        onCallBack = { viewModel.onIntent(CallsIntent.CallBack(call.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MostContactedSection() {
    Column {
        Text(
            "MOST CONTACTED",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Placeholder contacts from Figma
            listOf("Elena Vance", "Marcus W.", "Sarah Chen").forEach { name ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    UserAvatar(username = name, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        name.split(" ").first(),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Action buttons row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Videocam,
                                    contentDescription = "Video",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = "Audio",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MissedCallsCard(
    count: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Missed Calls",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    "$count",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
            TextButton(onClick = onClick) {
                Text("View All", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun CallItemRow(
    call: CallItem,
    onCallBack: () -> Unit
) {
    val callColor = when (call.callType) {
        CallType.MISSED -> MaterialTheme.colorScheme.error
        CallType.INCOMING -> MaterialTheme.colorScheme.primary
        CallType.OUTGOING -> MaterialTheme.colorScheme.onSurface
    }

    val callLabel = when (call.callType) {
        CallType.MISSED -> "Missed"
        CallType.INCOMING -> "Incoming"
        CallType.OUTGOING -> "Outgoing"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(username = call.name, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = call.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (call.callType == CallType.MISSED) callColor else MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Call direction indicator
                Surface(
                    shape = CircleShape,
                    color = callColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(6.dp)
                ) {}
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$callLabel • ${call.duration ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        // Call action icons
        IconButton(onClick = onCallBack) {
            Icon(
                if (call.isVideo) Icons.Default.Videocam else Icons.Default.Phone,
                contentDescription = if (call.isVideo) "Video call" else "Audio call",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
