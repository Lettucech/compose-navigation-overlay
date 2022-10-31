package com.lettucech.android.compose.navigation.overlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import com.lettucech.android.compose.navigation.overlay.ui.theme.ComposeNavigationOverlayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeNavigationOverlayTheme {
                val navController = rememberNavController(OverlayNavigator())
                val overlayNavigator =
                    navController.navigatorProvider.get<Navigator<out NavDestination>>(
                        OverlayNavigator.NAME
                    ) as? OverlayNavigator ?: return@ComposeNavigationOverlayTheme

                NavHost(
                    navController = navController,
                    startDestination = "1"
                ) {
                    composable(route = "1") {
                        Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(modifier = Modifier.fillMaxSize(0.5f)) {
                                    Text(
                                        text = "Page 1",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Button(onClick = { navController.navigate(route = "popup") }) {
                                        Text(text = "Open")
                                    }
                                }
                            }
                        }
                    }
                    overlay(route = "popup") {
                        Surface(color = Color.Gray, modifier = Modifier.fillMaxSize(0.5f)) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "I'm popup")
                            }
                        }
                    }
                    dialog(route = "dialog") {

                    }
                }

                // Show any dialog destinations
                OverlayHost(overlayNavigator)
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeNavigationOverlayTheme {
        Greeting("Android")
    }
}