package com.example.chatapp.feature.auth_identity.presentation.model

import com.example.chatapp.core.ui.mvi.UiIntent

sealed class ProfileIntent : UiIntent {
    data class UpdateUsername(val username: String) : ProfileIntent()
    data class UpdateAvatar(val uri: String) : ProfileIntent()
    object SaveProfile : ProfileIntent()
    object Logout : ProfileIntent()
    object LoadProfile : ProfileIntent()
}
