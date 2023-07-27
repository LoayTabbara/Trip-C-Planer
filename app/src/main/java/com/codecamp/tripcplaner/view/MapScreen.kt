package com.codecamp.tripcplaner.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.location.Geocoder
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codecamp.tripcplaner.MAPS_API_KEY
import com.codecamp.tripcplaner.MainActivity
import com.codecamp.tripcplaner.view.widgets.PermissionSnackbar
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    navController: NavController,
    typeActivity: String?,
    travelInfoViewModel: TravelInfoViewModel
) {
    val initialized = remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
    }
    var showIndicator = remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yy")
    val startMarker = rememberMarkerState()
    val endMarker = rememberMarkerState()
    //start place, end place, start date, end date
    val tripPickerList = mutableListOf(remember { mutableStateOf("") },
        remember { mutableStateOf("") },
        remember { mutableStateOf("") },
        remember { mutableStateOf("") })
    val citiesList = mutableListOf("Kassel", "Berlin", "Hamburg")

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET

        )
    )

    fun canCreate(): Boolean {
        return (tripPickerList[0].value.isNotEmpty() && tripPickerList[1].value.isNotEmpty() && tripPickerList[2].value.isNotEmpty() && tripPickerList[3].value.isNotEmpty() && LocalDate.parse(
            tripPickerList[2].value,
            formatter
        ) < LocalDate.parse(
            tripPickerList[3].value,
            formatter
        )) && tripPickerList[0].value != tripPickerList[1].value
    }

    val context = LocalContext.current
    Box {

        Scaffold(floatingActionButtonPosition = FabPosition.Center, floatingActionButton = {
            if (canCreate()) FloatingActionButton(
                onClick = {
                    val duration =
                        ChronoUnit.DAYS.between(
                            LocalDate.parse(tripPickerList[2].value, formatter),
                            LocalDate.parse(tripPickerList[3].value, formatter)
                        )
                    Toast.makeText(context, "Created!", Toast.LENGTH_SHORT).show()
                    Log.d("DAYS", duration.toString())

                    travelInfoViewModel.sendMessage(
                        listOf(
                            tripPickerList[0].value,
                            tripPickerList[1].value
                        ), duration.toInt(), context
                    )
                    travelInfoViewModel.hasResult.value = false
                    showIndicator.value = true

//                    if (travelInfoViewModel.citiesWithActivity.isNotEmpty()) {
//                        showIndicator.value = false
//                    }
                    Log.d("Result!!!", travelInfoViewModel.citiesWithActivity.toString())

                }) {
                Text(text = "Generate the Trip")
            }

        }, bottomBar = {
            if (!travelInfoViewModel.hasResult.value) {
                Column(
                    Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom,
                ) {

                    StopPicker(
                        tripPickerList,
                        true,
                        cameraPositionState,
                        startMarker
                    )
                    StopPicker(
                        tripPickerList,
                        false,
                        cameraPositionState,
                        endMarker
                    )

                }
            }
        }) { it ->
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
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
                }

                initialized.value = true
            }


            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                properties = properties,
                onMapClick = {
                    Log.d("TestTTTT", "onMapClick: $it")

                },
            ) {


                if (tripPickerList[0].value.isNotEmpty() && tripPickerList[1].value.isNotEmpty()) {
                    var positions =
                        mutableListOf(startMarker.position)
                    if (travelInfoViewModel.hasResult.value) {
                        for(i in 1 until travelInfoViewModel.citiesWithActivity.size-1){
                            val cityMarker = rememberMarkerState()
                            LaunchedEffect(Unit) {
                                cityMarker.position = getLatLng(travelInfoViewModel.citiesWithActivity.keys.elementAt(i))
                            }
                            Marker(
                                state = cityMarker,
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            )
                            positions.add(cityMarker.position)
                        }

//                        travelInfoViewModel.hasResult.value = false
                        showIndicator.value = false
                    }

                    positions.add(endMarker.position)
                    Polyline(
                        points = positions, color = Color.Blue
                    )
                }
                if (tripPickerList[0].value.isNotEmpty())
                    Marker(
                        state = startMarker,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                if (tripPickerList[1].value.isNotEmpty())
                    Marker(
                        state = endMarker,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
            }

            Log.i("ACTIVITY FROM ASM", "$typeActivity")


        }
        if (showIndicator.value && !travelInfoViewModel.hasResult.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xaaffffff)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(64.dp)
                )
            }
        }
    }
}

suspend fun getLatLng(locationName: String): LatLng {
    val serviceLatLng: LatLngService = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(LatLngService::class.java)
    val response = serviceLatLng.generateResponse(MAPS_API_KEY, locationName)
    val results = response.body()?.get("results") as JsonArray
    val geometry = results[0].asJsonObject.get("geometry") as JsonObject
    val location = geometry.get("location") as JsonObject
    return LatLng(location.get("lat").asDouble, location.get("lng").asDouble)

//    Log.i("Response", response.body())
}

interface LatLngService {
    @Headers("Content-Type: application/json")
    @POST("geocode/json")
    suspend fun generateResponse(
        @Query("key") apikey: String,
        @Query("address") address: String
    ): Response<JsonObject>
}

@Composable
fun StopPicker(
    tripPickerList: MutableList<MutableState<String>>,
    isStart: Boolean,
    cameraPositionState: CameraPositionState,
    markerState: MarkerState
) {
    val dateIndex = if (isStart) 2 else 3
    val placeIndex = if (isStart) 0 else 1
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .background(Color(0x99ffffff))
            .fillMaxWidth()
    ) {

        fun isValidDate(): Boolean {
            return tripPickerList[2].value.isEmpty() || tripPickerList[3].value.isEmpty() || (LocalDate.parse(
                tripPickerList[2].value,
                DateTimeFormatter.ofPattern("dd.MM.yy")
            ) < LocalDate.parse(tripPickerList[3].value, DateTimeFormatter.ofPattern("dd.MM.yy")))
        }

        fun isValidPlace(): Boolean {
            return tripPickerList[0].value.isEmpty() || tripPickerList[1].value.isEmpty() || tripPickerList[0].value != tripPickerList[1].value
        }

        val calendar = Calendar.getInstance()

        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePicker = DatePickerDialog(
            LocalContext.current,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                val monthText =
                    if ((selectedMonth + 1) < 10) "0${selectedMonth + 1}" else "${selectedMonth + 1}"
                val dayText =
                    if (selectedDayOfMonth < 10) "0$selectedDayOfMonth" else "$selectedDayOfMonth"
                tripPickerList[dateIndex].value = "$dayText.$monthText.${selectedYear % 2000}"
            },
            year,
            month,
            dayOfMonth
        )

        val context = LocalContext.current
        val intentLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    it.data?.let {
                        val place = Autocomplete.getPlaceFromIntent(it)
                        Log.i(
                            "MAP_ACTIVITY",
                            "Place: ${place.name}, ${place.latLng},wooooooooooork: ${
                                getCityName(
                                    context,
                                    place.latLng!!.latitude,
                                    place.latLng!!.longitude
                                )
                            }"
                        )
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(place.latLng!!, 18f)

                        tripPickerList[placeIndex].value = place.name as String

                        markerState.position =
                            LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                    }
                }
            }
        }


        val launchMapInputOverlay = {
            Places.initialize(context, MAPS_API_KEY)
            val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
            val intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(context)
            intentLauncher.launch(intent)
        }

        Text(text = if (isStart) "Start:" else "Dest:", modifier = Modifier.padding(10.dp))
        Button(
            modifier = Modifier.fillMaxWidth(0.4f),
            shape = RoundedCornerShape(10),
            onClick = launchMapInputOverlay
        ) {
            Text(
                color = if (isValidPlace()) Color.Black else Color.Red,
                text = tripPickerList[placeIndex].value.ifEmpty {
                    "Location"
                })
        }
        Button(shape = RoundedCornerShape(10), modifier = Modifier.fillMaxWidth(0.55f), onClick = {
            datePicker.show()
        }) {
            Text(
                color = if (isValidDate()) Color.Black else Color.Red,
                text = tripPickerList[dateIndex].value.ifEmpty {
                    "Date"
                })
        }
        Button(
            modifier = Modifier.fillMaxWidth(0.55f),
            enabled = tripPickerList[dateIndex].value.isNotEmpty() || tripPickerList[placeIndex].value.isNotEmpty(),
            onClick = {
                tripPickerList[dateIndex].value = ""
                tripPickerList[placeIndex].value = ""
            }, colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, contentColor = Color.Red
            )
        ) {
            Text(text = "\u239A", fontSize = 24.sp, textAlign = TextAlign.Center)
        }
    }

}

private fun getCityName(context: Context, lat: Double, lng: Double): String? {
    var cityName: String? = ""
    val geocoder = Geocoder(context)
    try {
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        if (Geocoder.isPresent()) {
            if (addresses!!.size > 0) {
                val returnAddress = addresses[0]
                cityName = returnAddress.locality
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return cityName
}

