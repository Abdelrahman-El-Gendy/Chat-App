package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.core.ui.theme.ChatAppTheme
import com.example.chatapp.feature.auth_identity.presentation.UsernameViewModel
import com.example.chatapp.navigation.AppNavGraph
import com.example.chatapp.navigation.AppRoutes
import com.example.chatapp.navigation.BottomNavBar
import com.example.chatapp.navigation.BottomNavItem
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

    // Observe current route for bottom nav visibility
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomNav = currentRoute in AppRoutes.BOTTOM_NAV_ROUTES

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            // Pop up to the channel list to avoid stacking tabs
                            popUpTo(AppRoutes.CHANNEL_LIST) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        // Pass padding so content doesn't overlap the bottom bar
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            AppNavGraph(
                navController = navController,
                startDestination = startDestination
            )
        }
    }
}
