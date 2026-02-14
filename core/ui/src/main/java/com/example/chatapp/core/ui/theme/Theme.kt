package com.example.chatapp.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable

@Composable
fun ChatAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val shapes = Shapes(
        extraSmall = RoundedCornerShape(Shape.extraSmall),
        small = RoundedCornerShape(Shape.small),
        medium = RoundedCornerShape(Shape.medium),
        large = RoundedCornerShape(Shape.large),
        extraLarge = RoundedCornerShape(Shape.extraLarge)
    )
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = shapes,
        content = content
    )
}
