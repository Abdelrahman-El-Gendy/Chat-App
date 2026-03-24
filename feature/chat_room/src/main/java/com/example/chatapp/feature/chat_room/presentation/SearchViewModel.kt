package com.example.chatapp.feature.chat_room.presentation

import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.domain.usecase.SearchMessagesUseCase
import com.example.chatapp.core.ui.mvi.BaseMviViewModel
import com.example.chatapp.feature.chat_room.presentation.model.SearchEffect
import com.example.chatapp.feature.chat_room.presentation.model.SearchIntent
import com.example.chatapp.feature.chat_room.presentation.model.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMessagesUseCase: SearchMessagesUseCase
) : BaseMviViewModel<SearchState, SearchIntent, SearchEffect>(
    initialState = SearchState()
) {
    private var searchJob: Job? = null

    override suspend fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateQuery -> updateQuery(intent.query)
            is SearchIntent.ClearSearch -> clearSearch()
            is SearchIntent.ScrollToMessage -> {
                setEffect(SearchEffect.ScrollToMessage(intent.messageId))
            }
        }
    }

    private fun updateQuery(query: String) {
        setState { copy(query = query) }

        // Debounce 300ms
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            if (query.isBlank()) {
                setState { copy(results = emptyList(), isEmpty = false, isLoading = false) }
                return@launch
            }

            setState { copy(isLoading = true) }
            searchMessagesUseCase(query)
                .catch { e ->
                    setState { copy(isLoading = false) }
                    setEffect(SearchEffect.ShowError(e.message ?: "Search failed"))
                }
                .collect { results ->
                    setState {
                        copy(
                            results = results,
                            isLoading = false,
                            isEmpty = results.isEmpty() && query.isNotBlank()
                        )
                    }
                }
        }
    }

    private fun clearSearch() {
        searchJob?.cancel()
        setState { copy(query = "", results = emptyList(), isEmpty = false, isLoading = false) }
    }
}
