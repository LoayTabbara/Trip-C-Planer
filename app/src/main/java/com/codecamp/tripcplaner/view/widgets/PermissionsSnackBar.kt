package com.codecamp.tripcplaner.view.widgets

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.codecamp.tripcplaner.model.permissionHandler.permissionCheck
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale


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
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom){
        var (snackbarVisibleState, setSnackBarState) = remember { mutableStateOf(!permissionsState.allPermissionsGranted) }
        if(snackbarVisibleState)
            Snackbar(

                action = {
                    Row {
                        Button(onClick = {
                            if(permanentlyDenied) {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                                intent.data = uri
                                context.startActivity(intent)
                            }else{
                                permissionsState.launchMultiplePermissionRequest()
                            }

                        }) {
                            Text("grant")
                        }
                        Button(onClick = { setSnackBarState(!snackbarVisibleState) }) {
                            Text("close")
                        }
                    }


                },
                modifier = Modifier
                    .padding(8.dp)
            ) { Text(text = "${if(permanentlyDenied) "permanently " else " " }denied location") }
        if (permissionsState.allPermissionsGranted){
            setSnackBarState(false)
        }
    }
}