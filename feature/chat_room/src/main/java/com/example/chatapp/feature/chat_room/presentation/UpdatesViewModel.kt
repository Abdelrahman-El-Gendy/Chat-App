package com.example.chatapp.feature.chat_room.presentation

import com.example.chatapp.core.ui.mvi.BaseMviViewModel
import com.example.chatapp.feature.chat_room.presentation.model.ChannelUpdate
import com.example.chatapp.feature.chat_room.presentation.model.StatusStory
import com.example.chatapp.feature.chat_room.presentation.model.SuggestedUser
import com.example.chatapp.feature.chat_room.presentation.model.UpdatesEffect
import com.example.chatapp.feature.chat_room.presentation.model.UpdatesIntent
import com.example.chatapp.feature.chat_room.presentation.model.UpdatesState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdatesViewModel @Inject constructor() : BaseMviViewModel<UpdatesState, UpdatesIntent, UpdatesEffect>(
    initialState = UpdatesState()
) {
    init {
        loadSampleData()
    }

    override suspend fun handleIntent(intent: UpdatesIntent) {
        when (intent) {
            is UpdatesIntent.LoadUpdates -> loadSampleData()
            is UpdatesIntent.ViewStatus -> setEffect(UpdatesEffect.NavigateToStory(intent.storyId))
            is UpdatesIntent.CreateStatus -> setEffect(UpdatesEffect.OpenCamera)
            is UpdatesIntent.FollowChannel -> toggleFollow(intent.channelId)
            is UpdatesIntent.ViewChannel -> setEffect(UpdatesEffect.NavigateToChannel(intent.channelId))
            is UpdatesIntent.Refresh -> loadSampleData()
        }
    }

    private fun toggleFollow(channelId: String) {
        setState {
            copy(channels = channels.map {
                if (it.id == channelId) it.copy(isFollowing = !it.isFollowing) else it
            })
        }
    }

    private fun loadSampleData() {
        val statuses = listOf(
            StatusStory("m", "My Status", null, System.currentTimeMillis(), isMine = true),
            StatusStory("1", "Leo", null, System.currentTimeMillis() - 3600000),
            StatusStory("2", "Elena", null, System.currentTimeMillis() - 7200000, isViewed = true),
            StatusStory("3", "Marcus", null, System.currentTimeMillis() - 10800000),
        )
        val channels = listOf(
            ChannelUpdate("c1", "Terra Explorers", "Discovering the most remote corners of the Amazon.", isFollowing = true),
            ChannelUpdate("c2", "Botany Weekly", "Tips for recognizing native ferns in temperate forests."),
            ChannelUpdate("c3", "Urban Nocturne", "Capturing the soul of the city after midnight using high-ISO…"),
        )
        val recentUpdates = listOf(
            ChannelUpdate("r1", "Earth Watchers", "New satellite imagery of the Amazon rainforest changes."),
            ChannelUpdate("r2", "Plant Parent Tips", "Network design and safe alternatives for your home plants."),
            ChannelUpdate("r3", "Macro Nature", "The microscopic world of tree bark in the Pacific Northwest."),
        )
        val suggested = listOf(
            SuggestedUser("s1", "Eco Warriors", "Eco scientists"),
            SuggestedUser("s2", "Trail Seekers", "Eco hikers"),
        )
        setState {
            copy(
                myStatus = statuses.first(),
                recentStatuses = statuses.drop(1),
                channels = channels,
                recentUpdates = recentUpdates,
                suggestedUsers = suggested,
                isLoading = false
            )
        }
    }
}
