package com.lettucech.android.compose.navigation.overlay

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun Overlay(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    BackHandler {
        visible = false
    }

    AnimatedVisibility(visible = visible, modifier = Modifier.fillMaxSize()) {
        DisposableEffect(Unit) {
            onDispose {
                onDismissRequest()
            }
        }
        content()
    }
}