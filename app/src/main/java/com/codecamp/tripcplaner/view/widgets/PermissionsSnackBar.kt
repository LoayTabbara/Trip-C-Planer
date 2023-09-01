package com.codecamp.tripcplaner.view.widgets

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import java.util.Locale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionSnackbar(permissionsState: MultiplePermissionsState) {
    var permanentlyDenied = false
    val context = LocalContext.current

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
    permissionsState.permissions.forEach { perm ->
        when {
            !perm.status.isGranted && !perm.status.shouldShowRationale -> {
                permanentlyDenied = true
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        val (snackbarVisibleState, setSnackBarState) = remember { mutableStateOf(!permissionsState.allPermissionsGranted) }
        if (snackbarVisibleState)
            Snackbar(
                modifier = Modifier
                    .padding(8.dp),
                containerColor = Color.Gray,
                action = {
                    Row {
                        TextButton(onClick = {
                            if (permanentlyDenied) {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                                intent.data = uri
                                context.startActivity(intent)
                            } else {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        }) {
                            Text("grant", color = Color.Green)
                        }
                        TextButton(onClick = { setSnackBarState(false) }) {
                            Text("close", color= Color(0xFF9B392B))
                        }
                    }
                },

            ) { Text(text = "${if (permanentlyDenied) "permanently " else " "}denied location\n select 'always allow' access to the location", color= Color.White) }
        if (permissionsState.allPermissionsGranted) {
            setSnackBarState(false)
        }
    }
}