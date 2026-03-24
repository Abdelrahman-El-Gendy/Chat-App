package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.core.ui.theme.ChatAppTheme
import com.example.chatapp.feature.auth_identity.presentation.UsernameViewModel
import com.example.chatapp.navigation.AppNavGraph
import com.example.chatapp.navigation.AppRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                MainContent()
            }
        }
    }
}

@Composable
private fun MainContent() {
    val navController = rememberNavController()
    val usernameViewModel: UsernameViewModel = hiltViewModel()
    val usernameState by usernameViewModel.state.collectAsState()

    // Determine start destination based on whether username exists
    val startDestination = remember(usernameState.username) {
        if (usernameState.username.isNotEmpty()) {
            AppRoutes.CHANNEL_LIST
        } else {
            AppRoutes.USERNAME
        }
    }

    AppNavGraph(
        navController = navController,
        startDestination = startDestination
    )
}
