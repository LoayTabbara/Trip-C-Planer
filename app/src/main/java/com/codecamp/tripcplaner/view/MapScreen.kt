package com.codecamp.tripcplaner.view

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.codecamp.tripcplaner.MainActivity
import com.codecamp.tripcplaner.view.widgets.PermissionSnackbar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    navController: NavController
) {

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
        )
    )
    Column() {
        PermissionSnackbar(permissionsState = permissionsState)

        val uiSettings = remember {
            MapUiSettings(myLocationButtonEnabled = permissionsState.allPermissionsGranted)
        }
        val properties by remember {
            mutableStateOf(MapProperties(isMyLocationEnabled = permissionsState.allPermissionsGranted))
        }
        val context = LocalContext.current

        val fusedLocationProviderClient =
            remember { LocationServices.getFusedLocationProviderClient(context) }

        var lastKnownLocation by remember {
            mutableStateOf<Location?>(null)
        }

        var deviceLatLng by remember {
            mutableStateOf(LatLng(52.52437, 13.41053))
        }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(deviceLatLng, 13f)
        }

        if (permissionsState.allPermissionsGranted) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(context as MainActivity) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    deviceLatLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
                } else {
                    Log.d("TAG", "Current location is null. Using defaults.")
                    Log.e("TAG", "Exception: %s", task.exception)
                }
            }
        }


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = properties
        ){
        }

    }
}