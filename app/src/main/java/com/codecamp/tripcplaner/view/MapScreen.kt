package com.codecamp.tripcplaner.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.location.Location
import android.util.Log
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.codecamp.tripcplaner.MAPS_API_KEY
import com.codecamp.tripcplaner.MainActivity
import com.codecamp.tripcplaner.view.widgets.PermissionSnackbar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Calendar

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
            MapUiSettings(
                myLocationButtonEnabled = permissionsState.allPermissionsGranted,
                mapToolbarEnabled = true,
                compassEnabled = true,
                scrollGesturesEnabled = true
            )
        }
        val properties by remember {
            mutableStateOf(
                MapProperties(
                    isMyLocationEnabled = permissionsState.allPermissionsGranted,
                    isBuildingEnabled = true
                )
            )
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
                    deviceLatLng =
                        LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
                } else {
                    Log.d("TAG", "Current location is null. Using defaults.")
                    Log.e("TAG", "Exception: %s", task.exception)
                }
            }
        }

        Column() {
            Row() {
                CityPicker()
                DatePicker()
            }
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = properties,
            onMapClick = {
                Log.d("TestTTTT", "onMapClick: $it")

            },
        ) {
        }


    }
}

@Composable
fun CityPicker() {
    val context = LocalContext.current

    val intentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        when (it.resultCode) {
            Activity.RESULT_OK -> {
                it.data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    Log.i("MAP_ACTIVITY", "Place: ${place.name}, ${place.id}")
                }
            }
            2 -> {
                it.data?.let {
                    val status = Autocomplete.getStatusFromIntent(it)
                    Log.i("MAP_SCREEN", "Status: ${status.statusMessage}")
                }
            }
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }
    }

    val launchMapInputOverlay = {
        Places.initialize(context, MAPS_API_KEY)
        val fields = listOf(Place.Field.ID, Place.Field.NAME)
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(context)
        intentLauncher.launch(intent)
    }

    Column {
        Button(onClick = launchMapInputOverlay) {
            Text("Select Location")
        }
    }
}

@Composable
fun DatePicker() {
    val calendar = Calendar.getInstance()
    var selectedDateText by remember { mutableStateOf("") }
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
    val datePicker = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            selectedDateText = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        }, year, month, dayOfMonth
    )

    Button(
        onClick = {
            datePicker.show()
        }
    ) {
        Text(
            text = if (selectedDateText.isNotEmpty()) {
                selectedDateText
            } else {
                "Date"
            }
        )
    }
}