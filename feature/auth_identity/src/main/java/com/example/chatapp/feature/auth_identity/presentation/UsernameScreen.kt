package com.example.chatapp.feature.auth_identity.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatapp.feature.auth_identity.R
import com.example.chatapp.feature.auth_identity.presentation.model.UsernameEffect
import com.example.chatapp.feature.auth_identity.presentation.model.UsernameIntent
import com.example.chatapp.feature.auth_identity.presentation.model.UsernameState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun UsernameScreen(
    viewModel: UsernameViewModel,
    onUsernameSet: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is UsernameEffect.NavigateToChat -> {
                    onUsernameSet()
                }
                is UsernameEffect.ShowError -> {
                    // Could show snackbar here
                }
            }
        }
    }

    UsernameScreenContent(
        state = state,
        onInputChange = { text ->
            viewModel.onIntent(UsernameIntent.UpdateInputText(text))
        },
        onSubmit = { username ->
            viewModel.onIntent(UsernameIntent.SubmitUsername(username))
        }
    )
}

@Composable
private fun UsernameScreenContent(
    state: UsernameState,
    onInputChange: (String) -> Unit,
    onSubmit: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome emoji
            WelcomeAvatar()

            Spacer(modifier = Modifier.height(32.dp))

            // Main card
            UsernameCard(
                state = state,
                onInputChange = onInputChange,
                onSubmit = onSubmit
            )
        }
    }
}

@Composable
private fun WelcomeAvatar() {
    Surface(
        modifier = Modifier.size(100.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        border = BorderStroke(2.dp, Color.White.copy(alpha = 0.5f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "👋",
                fontSize = 48.sp
            )
        }
    }
}

@Composable
private fun UsernameCard(
    state: UsernameState,
    onInputChange: (String) -> Unit,
    onSubmit: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = stringResource(R.string.join_the_chat),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = stringResource(R.string.username_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Username input
            OutlinedTextField(
                value = state.inputText,
                onValueChange = onInputChange,
                placeholder = { Text(stringResource(R.string.username_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                isError = state.error != null,
                supportingText = {
                    state.error?.let { Text(it) }
                },
                singleLine = true,
                enabled = !state.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit button
            Button(
                onClick = { onSubmit(state.inputText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !state.isLoading && state.inputText.isNotBlank(),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        stringResource(R.string.get_started),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
