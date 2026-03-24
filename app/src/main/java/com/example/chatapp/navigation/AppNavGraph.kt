package com.example.chatapp.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.chatapp.feature.auth_identity.presentation.UsernameScreen
import com.example.chatapp.feature.auth_identity.presentation.UsernameViewModel
import com.example.chatapp.feature.auth_identity.presentation.ProfileScreen
import com.example.chatapp.feature.auth_identity.presentation.ProfileViewModel
import com.example.chatapp.feature.auth_identity.presentation.SettingsScreen
import com.example.chatapp.feature.auth_identity.presentation.SettingsViewModel
import com.example.chatapp.feature.chat_room.presentation.ChatScreen
import com.example.chatapp.feature.chat_room.presentation.ChatViewModel
import com.example.chatapp.feature.chat_room.presentation.ChannelListScreen
import com.example.chatapp.feature.chat_room.presentation.ChannelListViewModel
import com.example.chatapp.feature.chat_room.presentation.SearchScreen
import com.example.chatapp.feature.chat_room.presentation.SearchViewModel

/**
 * Main Navigation Graph for the Chat App.
 *
 * Defines all composable destinations and their transitions.
 * Start destination is determined by whether the user has a username set.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ── Username Screen ──
        composable(AppRoutes.USERNAME) {
            val viewModel: UsernameViewModel = hiltViewModel()
            UsernameScreen(
                viewModel = viewModel,
                onUsernameSet = {
                    navController.navigate(AppRoutes.CHANNEL_LIST) {
                        popUpTo(AppRoutes.USERNAME) { inclusive = true }
                    }
                }
            )
        }

        // ── Channel List Screen ──
        composable(AppRoutes.CHANNEL_LIST) {
            val viewModel: ChannelListViewModel = hiltViewModel()
            ChannelListScreen(
                viewModel = viewModel,
                onChannelClick = { channelId ->
                    navController.navigate(AppRoutes.chat(channelId))
                },
                onProfileClick = {
                    navController.navigate(AppRoutes.PROFILE)
                },
                onSettingsClick = {
                    navController.navigate(AppRoutes.SETTINGS)
                }
            )
        }

        // ── Chat Screen ──
        composable(
            route = AppRoutes.CHAT,
            arguments = listOf(navArgument("channelId") { type = NavType.StringType })
        ) {
            val viewModel: ChatViewModel = hiltViewModel()
            ChatScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onProfileClick = { navController.navigate(AppRoutes.PROFILE) },
                onSearchClick = { navController.navigate(AppRoutes.SEARCH) }
            )
        }

        // ── Profile Screen ──
        composable(AppRoutes.PROFILE) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(AppRoutes.USERNAME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Settings Screen ──
        composable(AppRoutes.SETTINGS) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(AppRoutes.USERNAME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Search Screen ──
        composable(AppRoutes.SEARCH) {
            val viewModel: SearchViewModel = hiltViewModel()
            SearchScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onMessageClick = {
                    // Pop back to chat and scroll to that message
                    navController.popBackStack()
                }
            )
        }


    }
}
