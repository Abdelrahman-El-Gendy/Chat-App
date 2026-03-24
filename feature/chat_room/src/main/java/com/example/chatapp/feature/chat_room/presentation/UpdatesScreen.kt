package com.example.chatapp.feature.chat_room.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.chatapp.core.ui.component.UserAvatar
import com.example.chatapp.feature.chat_room.presentation.model.ChannelUpdate
import com.example.chatapp.feature.chat_room.presentation.model.StatusStory
import com.example.chatapp.feature.chat_room.presentation.model.SuggestedUser
import com.example.chatapp.feature.chat_room.presentation.model.UpdatesEffect
import com.example.chatapp.feature.chat_room.presentation.model.UpdatesIntent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesScreen(
    viewModel: UpdatesViewModel
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is UpdatesEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is UpdatesEffect.NavigateToStory -> { /* TODO */
                }

                is UpdatesEffect.NavigateToChannel -> { /* TODO */
                }

                is UpdatesEffect.OpenCamera -> { /* TODO */
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
                        "Terra Chat",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(onClick = { /* search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
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
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // ── Status / Stories Section ──
                item {
                    SectionHeader("Status")
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    StatusRow(
                        myStatus = state.myStatus,
                        statuses = state.recentStatuses,
                        onMyStatusClick = { viewModel.onIntent(UpdatesIntent.CreateStatus) },
                        onStatusClick = { viewModel.onIntent(UpdatesIntent.ViewStatus(it.id)) }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ── Channels Section ──
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Channels",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { /* explore */ }) {
                            Text("Explore")
                        }
                    }
                }
                item {
                    ChannelCards(
                        channels = state.channels,
                        onChannelClick = { viewModel.onIntent(UpdatesIntent.ViewChannel(it.id)) },
                        onFollowClick = { viewModel.onIntent(UpdatesIntent.FollowChannel(it.id)) }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ── Recent Updates Section ──
                item {
                    SectionHeader("Recent Updates")
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(state.recentUpdates, key = { it.id }) { update ->
                    RecentUpdateItem(
                        update = update,
                        onClick = { viewModel.onIntent(UpdatesIntent.ViewChannel(update.id)) }
                    )
                }

                // ── Suggested For You ──
                if (state.suggestedUsers.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionHeader("Suggested for you")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(state.suggestedUsers, key = { it.id }) { user ->
                        SuggestedUserItem(user = user)
                    }
                }

                // Bottom spacer for nav bar
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

@Composable
private fun StatusRow(
    myStatus: StatusStory?,
    statuses: List<StatusStory>,
    onMyStatusClick: () -> Unit,
    onStatusClick: (StatusStory) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // My status with + badge
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onMyStatusClick)
            ) {
                Box {
                    UserAvatar(
                        username = myStatus?.userName ?: "Me",
                        modifier = Modifier.size(64.dp)
                    )
                    // Green + badge
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add status",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "My Status",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        // Other statuses
        items(statuses, key = { it.id }) { story ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onStatusClick(story) }
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .border(
                            width = 2.dp,
                            color = if (story.isViewed)
                                MaterialTheme.colorScheme.outlineVariant
                            else
                                MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .padding(3.dp)
                ) {
                    UserAvatar(
                        username = story.userName,
                        modifier = Modifier.size(62.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    story.userName,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ChannelCards(
    channels: List<ChannelUpdate>,
    onChannelClick: (ChannelUpdate) -> Unit,
    onFollowClick: (ChannelUpdate) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(channels, key = { it.id }) { channel ->
            Card(
                onClick = { onChannelClick(channel) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.width(180.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Channel icon placeholder
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                channel.channelName.first().toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        channel.channelName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        channel.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Follow button
                    if (channel.isFollowing) {
                        OutlinedButton(
                            onClick = { onFollowClick(channel) },
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Following", style = MaterialTheme.typography.labelSmall)
                        }
                    } else {
                        Button(
                            onClick = { onFollowClick(channel) },
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Follow", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentUpdateItem(
    update: ChannelUpdate,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    update.channelName.first().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                update.channelName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                update.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun SuggestedUserItem(user: SuggestedUser) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(username = user.name, modifier = Modifier.size(44.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                user.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                user.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        FilledTonalButton(
            onClick = { /* follow */ },
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text("Follow", style = MaterialTheme.typography.labelSmall)
        }
    }
}
