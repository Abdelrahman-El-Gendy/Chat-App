package com.example.chatapp.feature.auth_identity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.domain.usecase.GetUsernameUseCase
import com.example.chatapp.core.domain.usecase.SetUsernameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsernameViewModel @Inject constructor(
    private val getUsernameUseCase: GetUsernameUseCase,
    private val setUsernameUseCase: SetUsernameUseCase
) : ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    init {
        viewModelScope.launch {
            getUsernameUseCase().collect { user ->
                _username.value = user ?: ""
            }
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            setUsernameUseCase(newUsername)
        }
    }
}
