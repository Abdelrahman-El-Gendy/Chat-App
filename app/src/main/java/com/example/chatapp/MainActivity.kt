package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapp.core.ui.theme.ChatAppTheme
import com.example.chatapp.feature.auth_identity.presentation.UsernameScreen
import com.example.chatapp.feature.auth_identity.presentation.UsernameViewModel
import com.example.chatapp.feature.chat_room.presentation.ChatScreen
import com.example.chatapp.feature.chat_room.presentation.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                MainContent(onFinish = { finish() })
            }
        }
    }
}

@Composable
private fun MainContent(onFinish: () -> Unit) {
    val usernameViewModel: UsernameViewModel = hiltViewModel()
    val chatViewModel: ChatViewModel = hiltViewModel()

    val usernameState by usernameViewModel.state.collectAsState()
    var showUsernameScreen by remember { mutableStateOf(true) }

    LaunchedEffect(usernameState.username) {
        if (usernameState.username.isNotEmpty()) {
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
            onBack = onFinish
        )
    }
}
