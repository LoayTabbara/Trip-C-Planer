package com.codecamp.tripcplaner.view

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codecamp.tripcplaner.MainActivity
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import com.codecamp.tripcplaner.view.widgets.CustomMarker
import com.codecamp.tripcplaner.view.widgets.GeneratedTripOverview
import com.codecamp.tripcplaner.view.widgets.PermissionSnackbar
import com.codecamp.tripcplaner.view.widgets.StopPicker
import com.codecamp.tripcplaner.viewModel.DetailViewModel
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    navController: NavController,
    transportMean: String?,
    travelInfoViewModel: TravelInfoViewModel,
    detailsViewModel: DetailViewModel
) {

    val initialized = remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {}
    val showIndicator = remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startMarker = rememberMarkerState()
    val endMarker = rememberMarkerState()
    //start place, end place, start date, end date
    val tripPickerList = mutableListOf(remember { mutableStateOf("") },
        remember { mutableStateOf("") },
        remember { mutableStateOf("") },
        remember { mutableStateOf("") })

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET

        )
    )
    fun canCreate(): Boolean {
        return (tripPickerList[0].value.isNotEmpty() && tripPickerList[1].value.isNotEmpty() &&
                tripPickerList[2].value.isNotEmpty() && tripPickerList[3].value.isNotEmpty() &&
                LocalDate.parse(tripPickerList[2].value, formatter) <
                LocalDate.parse(tripPickerList[3].value, formatter)) &&
                tripPickerList[0].value != tripPickerList[1].value
    }

    val context = LocalContext.current
    Box {
        Scaffold(bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .background(Color.Gray),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!travelInfoViewModel.hasResult.value) {
                    StopPicker(
                        tripPickerList, true, cameraPositionState, startMarker
                    )
                    StopPicker(
                        tripPickerList, false, cameraPositionState, endMarker
                    )
                } else {
                    GeneratedTripOverview(transportMean, travelInfoViewModel)
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Button(
                        shape = RoundedCornerShape(10),

                        enabled = canCreate(),
                        modifier = if (travelInfoViewModel.hasResult.value) Modifier.fillMaxWidth(
                            0.25f
                        ) else Modifier.fillMaxWidth(),
                        onClick = {
                            val startDate = LocalDate.parse(tripPickerList[2].value, formatter)
                            val endDate = LocalDate.parse(tripPickerList[3].value, formatter)
                            val duration = ChronoUnit.DAYS.between(
                                startDate, endDate
                            )
                            travelInfoViewModel.startDate = startDate.atStartOfDay()
                            travelInfoViewModel.sendMessage(
                                listOf(tripPickerList[0].value, tripPickerList[1].value),
                                duration.toInt(),
                                context,
                                if (startDate.monthValue < 4 || startDate.monthValue > 10) "Winter" else "Summer"
                            )
                            travelInfoViewModel.hasResult.value = false
                            showIndicator.value = true
                        },
                        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.Transparent, containerColor = Color(if (travelInfoViewModel.hasResult.value) 0XFFE0BB70 else 0XFF388E3C))

                    ) {
                        Text(
                            text = if (travelInfoViewModel.hasResult.value) "\u21BA" else "Generate a Trip",
                            fontWeight = if (travelInfoViewModel.hasResult.value) FontWeight.Bold else FontWeight.Normal,
                            fontSize = if (travelInfoViewModel.hasResult.value) 24.sp else 16.sp,
                            color = if(canCreate()) Color.White else Color.LightGray
                        )
                    }
                    if (travelInfoViewModel.hasResult.value)
                        Button(shape = RoundedCornerShape(10),
                            modifier = Modifier
                                .fillMaxWidth(0.32f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF388E3C)),
                            onClick = {
                                travelInfoViewModel.hasResult.value = false
                                val startDate =
                                    if (travelInfoViewModel.generatePseudo) LocalDateTime.parse(
                                        travelInfoViewModel.times.value.first()
                                    ) else LocalDate.parse(tripPickerList[2].value).atStartOfDay()
                                val endDate =
                                    if (travelInfoViewModel.generatePseudo) LocalDateTime.parse(
                                        travelInfoViewModel.times.value.last()
                                    ) else LocalDate.parse(tripPickerList[3].value).atStartOfDay()
                                detailsViewModel.setDates(startDate, endDate)

                                navController.navigate(TripCPlanerScreens.PackScreen.name)
                            }) {
                            Text(text = "✔︎", fontSize = 24.sp, color = Color.White)
                        }
                }

            }


        }) {
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
            val fusedLocationProviderClient =
                remember { LocationServices.getFusedLocationProviderClient(context) }

            var lastKnownLocation by remember {
                mutableStateOf<Location?>(null)
            }

            var deviceLatLng by remember {
                mutableStateOf(LatLng(52.52, 13.41))
            }

            if (!initialized.value) {
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
                } else {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
                }
                detailsViewModel.setTransportMean(transportMean!!)
                initialized.value = true
            }
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                properties = properties,
            ) {
                if (tripPickerList[0].value.isNotEmpty() && tripPickerList[1].value.isNotEmpty()) {

                    if (travelInfoViewModel.hasResult.value) {
                        if (!travelInfoViewModel.generatePseudo) travelInfoViewModel.latLngList =
                            mutableListOf(startMarker.position) else startMarker.position =
                            travelInfoViewModel.latLngList.first()
                        for (i in 1 until travelInfoViewModel.citiesWithActivity.size - 1) {
                            val cityMarker = rememberMarkerState()
                            LaunchedEffect(Unit) {
                                cityMarker.position =
                                    if (!travelInfoViewModel.generatePseudo) travelInfoViewModel.getLatLng(
                                        travelInfoViewModel.citiesWithActivity.keys.elementAt(i)
                                    ) else
                                        travelInfoViewModel.latLngList[i]
                            }
                            CustomMarker(cityMarker, travelInfoViewModel, i)
                            travelInfoViewModel.latLngList.add(cityMarker.position)
                        }
                        if (!travelInfoViewModel.generatePseudo)
                            travelInfoViewModel.latLngList.add(endMarker.position) else endMarker.position =
                            travelInfoViewModel.latLngList.last()
                        showIndicator.value = false
                    }
                    Polyline(
                        points = if (travelInfoViewModel.hasResult.value) travelInfoViewModel.latLngList else listOf(
                            startMarker.position,
                            endMarker.position
                        ), color = Color.Blue
                    )


                }
                if (tripPickerList[0].value.isNotEmpty()) {
                    CustomMarker(startMarker, travelInfoViewModel, 0)
                }
                if (tripPickerList[1].value.isNotEmpty()) {
                    CustomMarker(
                        endMarker,
                        travelInfoViewModel,
                        travelInfoViewModel.citiesWithActivity.size - 1
                    )
                }
            }
        }
        if (showIndicator.value && !travelInfoViewModel.hasResult.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xaaffffff)),

                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}
