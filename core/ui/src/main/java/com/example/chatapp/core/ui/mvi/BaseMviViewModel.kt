package com.example.chatapp.core.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Base MVI ViewModel that provides the foundation for Model-View-Intent architecture.
 * 
 * This ViewModel handles:
 * - **State**: The current UI state exposed as [StateFlow]
 * - **Intent**: User actions/events that trigger state changes
 * - **Effect**: One-time side effects (navigation, snackbar, etc.)
 * 
 * ## Usage
 * ```kotlin
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *     private val useCase: MyUseCase
 * ) : BaseMviViewModel<MyState, MyIntent, MyEffect>(MyState()) {
 * 
 *     override suspend fun handleIntent(intent: MyIntent) {
 *         when (intent) {
 *             is MyIntent.LoadData -> loadData()
 *             is MyIntent.ItemClicked -> handleItemClick(intent.id)
 *         }
 *     }
 * }
 * ```
 * 
 * @param State The UI state type, must implement [UiState]
 * @param Intent The user intent type, must implement [UiIntent]
 * @param Effect The side effect type, must implement [UiEffect]
 * @param initialState The initial state when ViewModel is created
 */
abstract class BaseMviViewModel<State : UiState, Intent : UiIntent, Effect : UiEffect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    
    /**
     * The current UI state as an observable [StateFlow].
     * Collect this in your Composable to react to state changes.
     */
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    
    /**
     * One-time side effects as a [Flow].
     * Collect this in a LaunchedEffect to handle navigation, snackbars, etc.
     */
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    /**
     * Quick access to the current state value.
     * Useful within the ViewModel for reading current state.
     */
    protected val currentState: State
        get() = _state.value

    /**
     * Entry point for processing user intents.
     * Call this from your UI when user performs an action.
     * 
     * @param intent The user intent to process
     */
    fun onIntent(intent: Intent) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }

    /**
     * Override this to handle specific intents.
     * This is called for each intent received via [onIntent].
     * 
     * @param intent The intent to handle
     */
    protected abstract suspend fun handleIntent(intent: Intent)

    /**
     * Update the current state using a reducer function.
     * The reducer receives the current state and returns the new state.
     * 
     * @param reducer A function that transforms the current state to a new state
     */
    protected fun setState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }

    /**
     * Send a one-time side effect to the UI.
     * Effects are consumed once and won't be re-emitted on configuration changes.
     * 
     * @param effect The side effect to emit
     */
    protected fun setEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
