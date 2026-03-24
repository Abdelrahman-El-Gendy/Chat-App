package com.example.chatapp.feature.auth_identity.presentation

import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.domain.model.User
import com.example.chatapp.core.domain.usecase.*
import com.example.chatapp.core.ui.mvi.BaseMviViewModel
import com.example.chatapp.feature.auth_identity.presentation.model.ProfileEffect
import com.example.chatapp.feature.auth_identity.presentation.model.ProfileIntent
import com.example.chatapp.feature.auth_identity.presentation.model.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val getDeviceIdUseCase: GetDeviceIdUseCase
) : BaseMviViewModel<ProfileState, ProfileIntent, ProfileEffect>(
    initialState = ProfileState()
) {

    init {
        loadProfile()
    }

    override suspend fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.UpdateUsername -> setState { copy(username = intent.username) }
            is ProfileIntent.UpdateAvatar -> setState { copy(selectedAvatarUri = intent.uri) }
            is ProfileIntent.SaveProfile -> saveProfile()
            is ProfileIntent.Logout -> logout()
            is ProfileIntent.LoadProfile -> loadProfile()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            combine(getUserProfileUseCase(), getDeviceIdUseCase()) { profile, deviceId ->
                profile to deviceId
            }.catch { e ->
                setState { copy(isLoading = false, error = e.message) }
            }.collect { (profile, deviceId) ->
                setState {
                    copy(
                        username = profile.username,
                        avatarUrl = profile.profilePictureUrl,
                        deviceId = deviceId,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun saveProfile() {
        setState { copy(isSaving = true, error = null) }

        try {
            // Upload avatar if new one was selected
            var avatarUrl = currentState.avatarUrl
            currentState.selectedAvatarUri?.let { uri ->
                uploadAvatarUseCase(uri)
                    .onSuccess { url -> avatarUrl = url }
                    .onFailure { throw it }
            }

            // Update profile
            val user = User(
                id = currentState.deviceId,
                username = currentState.username,
                profilePictureUrl = avatarUrl,
                isOnline = true
            )
            updateUserProfileUseCase(user)
                .onSuccess {
                    setState { copy(isSaving = false, selectedAvatarUri = null, avatarUrl = avatarUrl) }
                    setEffect(ProfileEffect.ShowSuccess("Profile updated!"))
                }
                .onFailure { throw it }
        } catch (e: Exception) {
            setState { copy(isSaving = false, error = e.message) }
            setEffect(ProfileEffect.ShowError("Failed to update profile"))
        }
    }

    private fun logout() {
        setEffect(ProfileEffect.NavigateToLogin)
    }
}
