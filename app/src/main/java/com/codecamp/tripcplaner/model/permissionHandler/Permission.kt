package com.codecamp.tripcplaner.model.permissionHandler

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun permissionCheck(permissionsState: MultiplePermissionsState, context:Context){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
    permissionsState.permissions.forEach { perm ->
        when (perm.permission) {

            Manifest.permission.ACCESS_FINE_LOCATION -> {
                when {
                    perm.status.isGranted -> {
                        Text(text = "Fine Location permission accepted")
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    perm.status.shouldShowRationale -> {
                        Text(
                            text = "Fine Location permission is needed" +
                                    "to access the Exact Location"
                        )
                        //Request Permission
                       Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                           Text(text ="Request Permission")
                       }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    !perm.status.isGranted && !perm.status.shouldShowRationale -> {
                        Text(
                            text = "Fine Location permission was permanently" +
                                    "denied. You can enable it in the app" +
                                    "settings."
                        )
                        Button(onClick = { val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val uri: Uri = Uri.fromParts("package", context.packageName, null)
                            intent.data = uri
                            context.startActivity(intent) }) {
                            Text(text ="Open Settings")
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Manifest.permission.ACCESS_COARSE_LOCATION -> {
                when {
                    perm.status.isGranted -> {
                        Text(text = "Coarse Location permission accepted")
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    perm.status.shouldShowRationale -> {
                        Text(
                            text = "Coarse Location permission is needed" +
                                    "to access the Approximate Location"

                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    !perm.status.isGranted && !perm.status.shouldShowRationale -> {
                        Text(
                            text = "Coarse Location permission was permanently" +
                                    "denied. You can enable it in the app" +
                                    "settings."
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
            Manifest.permission.INTERNET -> {
                when {
                    perm.status.isGranted -> {
                        Text(text = "Internet permission accepted")
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    perm.status.shouldShowRationale -> {
                        Text(
                            text = "Internet permission is needed" +
                                    "to access the Internet"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    !perm.status.isGranted && !perm.status.shouldShowRationale -> {
                        Text(
                            text = "Internet permission was permanently" +
                                    "denied. You can enable it in the app" +
                                    "settings."
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}}