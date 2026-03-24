package com.example.chatapp.feature.chat_room.presentation

import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.domain.usecase.CreateChannelUseCase
import com.example.chatapp.core.domain.usecase.GetChannelsUseCase
import com.example.chatapp.core.ui.mvi.BaseMviViewModel
import com.example.chatapp.feature.chat_room.presentation.model.ChannelListEffect
import com.example.chatapp.feature.chat_room.presentation.model.ChannelListIntent
import com.example.chatapp.feature.chat_room.presentation.model.ChannelListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelListViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val createChannelUseCase: CreateChannelUseCase
) : BaseMviViewModel<ChannelListState, ChannelListIntent, ChannelListEffect>(
    initialState = ChannelListState()
) {
    init {
        loadChannels()
    }

    override suspend fun handleIntent(intent: ChannelListIntent) {
        when (intent) {
            is ChannelListIntent.LoadChannels -> loadChannels()
            is ChannelListIntent.SelectChannel -> selectChannel(intent.channelId)
            is ChannelListIntent.CreateChannel -> createChannel(intent.name)
            is ChannelListIntent.Refresh -> loadChannels()
        }
    }

    private fun loadChannels() {
        viewModelScope.launch {
            getChannelsUseCase()
                .catch { e ->
                    setState { copy(isLoading = false, error = e.message) }
                }
                .collect { channels ->
                    setState { copy(channels = channels, isLoading = false) }
                }
        }
    }

    private fun selectChannel(channelId: String) {
        setEffect(ChannelListEffect.NavigateToChat(channelId))
    }

    private suspend fun createChannel(name: String) {
        createChannelUseCase(name)
            .onSuccess {
                setState { copy(showCreateDialog = false) }
            }
            .onFailure { e ->
                setEffect(ChannelListEffect.ShowError("Failed to create channel: ${e.message}"))
            }
    }
}
