package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapp.feature.chat_room.ChatScreen
import com.example.chatapp.feature.chat_room.ChatViewModel
import com.example.chatapp.feature.auth_identity.UsernameScreen
import com.example.chatapp.feature.auth_identity.UsernameViewModel
import com.example.chatapp.core.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                val usernameViewModel: UsernameViewModel = hiltViewModel()
                val chatViewModel: ChatViewModel = hiltViewModel()
                
                val username by usernameViewModel.username.collectAsState()
                var showUsernameScreen by remember { mutableStateOf(true) }

                LaunchedEffect(username) {
                    if (username.isNotEmpty()) {
                        showUsernameScreen = false
                    }
                }

                if (showUsernameScreen) {
                    UsernameScreen(
                        viewModel = usernameViewModel,
                        onUsernameSet = { showUsernameScreen = false }
                    )
                } else {
                    ChatScreen(
                        viewModel = chatViewModel,
                        onBack = { /* Could handle exit or something */ }
                    )
                }
            }
        }
    }
}
