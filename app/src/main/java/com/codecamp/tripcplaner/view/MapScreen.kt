package com.codecamp.tripcplaner.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.drawable.Icon
import android.location.Location
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codecamp.tripcplaner.MAPS_API_KEY
import com.codecamp.tripcplaner.MainActivity
import com.codecamp.tripcplaner.R
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

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    navController: NavController,
    typeActivity: String?
) {

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET

        )
    )
    val context = LocalContext.current
    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(onClick = { Toast.makeText(context, "Created!", Toast.LENGTH_SHORT).show() }) {
                Text(text = "Create Trip", )

            }
            FloatingActionButton(modifier = Modifier.offset(100.dp),containerColor= Color(0xFF006400), onClick = { Toast.makeText(context, "Added!", Toast.LENGTH_SHORT).show() }) {
                Icon(Icons.Filled.Add, "Add")

            }
        }
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(it)
        ) {
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
                mutableStateOf(LatLng(52.52, 13.41))
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
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
                    } else {
                        Log.d("TAG", "Current location is null. Using defaults.")
                        Log.e("TAG", "Exception: %s", task.exception)
                    }
                }
            }
//        mutableStateListOf<Composable>(StopRow(),StopRow())
            Column(
                Modifier
                    .background(Color.White)
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CityPicker()

                    DatePicker()

                }
//            var uiState = StateFlow
//            LazyColumn {
//                items(items = uiState.listItems, key = { item -> item.getKey() }) { listItem ->
//                    if (listItem == uiState.listItems.first()) {
//                        AddMoreHeader(onClick = {headerClick()})
//                    }
//                    ListItem(listItem)
//                    if (listItem == uiState.listItems.last()) {
//                        AddMoreFooter(onClick = {viewModel.footerClick()})
//                    }
//                }
//            }

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
}

@Composable
fun StopRow() {
    CityPicker()
    DatePicker()
    RemoveButton()
}

@Composable
fun RemoveButton() {
    Button(
        onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, contentColor = Color.Red
        )
    ) {
        Text(text = "\uD83D\uDDD1", fontSize = 24.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun CityPicker() {
    val id = remember { mutableStateOf("") }
    val context = LocalContext.current
    var selectedPlace by remember { mutableStateOf("") }

    val intentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        when (it.resultCode) {
            Activity.RESULT_OK -> {
                it.data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    id.value = place.id as String
                    Log.i("MAP_ACTIVITY", "Place: ${place.name}, ${place.latLng}")

                    selectedPlace = place.name as String
                }
            }
        }
    }

    val launchMapInputOverlay = {
        Places.initialize(context, MAPS_API_KEY)
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(context)
        intentLauncher.launch(intent)
    }
    Column(Modifier.background(Color.Transparent)) {
        Button(onClick = launchMapInputOverlay) {
//            Text("Select Location")
            Text(text = selectedPlace.ifEmpty {
                "Select Location"
            })
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
            text = selectedDateText.ifEmpty {
                "Date"
            }
        )
    }
}