package com.codecamp.tripcplaner.view

import android.Manifest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(navController: NavController) {
    Text("MainScreen")
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
        )
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    permissionsState.launchMultiplePermissionRequest()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
//        var goToMapScreen = remember { mutableStateOf(false) }
//        if(goToMapScreen.value)
//            permissionCheck(permissionsState = permissionsState, context)
        Button(onClick = {
//            goToMapScreen.value = true
            navController.navigate(TripCPlanerScreens.MapScreen.name)
        }) {
            Text(text = "Goto MapScreen")
        }
    }

}