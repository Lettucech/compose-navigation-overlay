package com.lettucech.android.compose.navigation.overlay

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.LocalOwnersProvider
import androidx.navigation.compose.NavHost


/**
 * Show each [Destination] on the [DialogNavigator]'s back stack as a [Dialog].
 *
 * Note that [NavHost] will call this for you; you do not need to call it manually.
 */
@Composable
fun OverlayHost(overlayNavigator: OverlayNavigator) {
    val saveableStateHolder = rememberSaveableStateHolder()
    val overlayBackStack by overlayNavigator.backStack.collectAsState()
    val visibleBackStack = rememberVisibleList(overlayBackStack)
    visibleBackStack.PopulateVisibleList(overlayBackStack)

    visibleBackStack.forEach { backStackEntry ->
        val destination = backStackEntry.destination as OverlayNavigator.Destination

        Overlay(
            onDismissRequest = {overlayNavigator.dismiss(backStackEntry)}
        ) {
            DisposableEffect(backStackEntry) {
                onDispose {
                    overlayNavigator.onTransitionComplete(backStackEntry)
                }
            }

            // while in the scope of the composable, we provide the navBackStackEntry as the
            // ViewModelStoreOwner and LifecycleOwner
            backStackEntry.LocalOwnersProvider(saveableStateHolder) {
                destination.content(backStackEntry)
            }
        }
    }
}

@Composable
internal fun MutableList<NavBackStackEntry>.PopulateVisibleList(
    transitionsInProgress: Collection<NavBackStackEntry>
) {
    transitionsInProgress.forEach { entry ->
        DisposableEffect(entry.lifecycle) {
            val observer = LifecycleEventObserver { _, event ->
                // ON_START -> add to visibleBackStack, ON_STOP -> remove from visibleBackStack
                if (event == Lifecycle.Event.ON_START) {
                    // We want to treat the visible lists as Sets but we want to keep
                    // the functionality of mutableStateListOf() so that we recompose in response
                    // to adds and removes.
                    if (!contains(entry)) {
                        add(entry)
                    }
                }
                if (event == Lifecycle.Event.ON_STOP) {
                    remove(entry)
                }
            }
            entry.lifecycle.addObserver(observer)
            onDispose {
                entry.lifecycle.removeObserver(observer)
            }
        }
    }
}

@Composable
internal fun rememberVisibleList(transitionsInProgress: Collection<NavBackStackEntry>) =
    remember(transitionsInProgress) {
        mutableStateListOf<NavBackStackEntry>().also {
            it.addAll(
                transitionsInProgress.filter { entry ->
                    entry.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
                }
            )
        }
    }
