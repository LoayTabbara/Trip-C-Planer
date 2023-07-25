package com.codecamp.tripcplaner.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.codecamp.tripcplaner.MainActivity
import com.codecamp.tripcplaner.view.widgets.PermissionSnackbar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.IOException
import java.util.Calendar

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    navController: NavController
) {


//    val account = GoogleSignIn.getAccountForExtension(LocalContext.current, )
//
//    if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
//        GoogleSignIn.requestPermissions(
//            this,
//            1,
//            account,
//            fitnessOptions)
//    }
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

fun getJsonDataFromAsset(
    context: Context,
    fileName: String
): String? {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use {
            it.readText()
        }
    } catch (exp: IOException) {
        exp.printStackTrace()
        return null
    }

    return jsonString
}

fun citiesList(context: Context): MutableList<City> {
    val jsonFileString = getJsonDataFromAsset(context, "cities.json")
    val type = object : TypeToken<List<City>>() {}.type
    return Gson().fromJson(jsonFileString, type)
}

data class City(
    val country: String,
    val name: String,
    val lat: Double,
    val lng: Double,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPicker() {
    google.maps.places.AutocompleteService()
    val cities = mutableListOf("Berlin", "Munich", "Hamburg", "Frankfurt", "Cologne", "Cassel", "Paris", "Frankfurt", "Cologne", "Cassel", "Paris", "Frankfurt", "Cologne", "Cassel", "Paris", "Frankfurt", "Cologne", "Cassel", "Paris", "Frankfurt", "Cologne", "Cassel", "Paris", )
//    val cities = citiesList(LocalContext.current)

    SearchableExpandedDropDownMenu(
        listOfItems = cities, // provide the list of items of any type you want to populated in the dropdown,
        onDropDownItemSelected = { item -> // Returns the item selected in the dropdown
//            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
        },
        placeholder = { Text(text = "Select Option") },
        dropdownItem = { // Provide a Composable that will be used to populate the dropdown and that takes a type i.e String,Int or even a custom type
            Text(it)
                       },
        defaultItem = { cities } // Provide a default item to be selected when the dropdown is opened
    )
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
                "$selectedDateText"
            } else {
                "Date"
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableExpandedDropDownMenu(
    modifier: Modifier = Modifier,
    listOfItems: List<T>,
    enable: Boolean = true,
    readOnly: Boolean = true,
    placeholder: @Composable (() -> Unit) = { Text(text = "Select Option") },
    openedIcon: ImageVector = Icons.Outlined.KeyboardArrowUp,
    closedIcon: ImageVector = Icons.Outlined.KeyboardArrowDown,
    parentTextFieldCornerRadius: Dp = 12.dp,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
    onDropDownItemSelected: (T) -> Unit = {},
    dropdownItem: @Composable (T) -> Unit,
    isError: Boolean = false,
    showDefaultSelectedItem: Boolean = false,
    defaultItemIndex: Int = 0,
    defaultItem: (T) -> Unit
) {
    var selectedOptionText by rememberSaveable { mutableStateOf("") }
    var searchedOption by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var filteredItems = mutableListOf<T>()

    val itemHeights = remember { mutableStateMapOf<Int, Int>() }
    val baseHeight = 530.dp
    val density = LocalDensity.current

    if (showDefaultSelectedItem) {
        selectedOptionText = selectedOptionText.ifEmpty { listOfItems[defaultItemIndex].toString() }

        defaultItem(
            listOfItems[defaultItemIndex]
        )
    }

    val maxHeight = remember(itemHeights.toMap()) {
        if (itemHeights.keys.toSet() != listOfItems.indices.toSet()) {
            // if we don't have all heights calculated yet, return default value
            return@remember baseHeight
        }
        val baseHeightInt = with(density) { baseHeight.toPx().toInt() }

        // top+bottom system padding
        var sum = with(density) { DropdownMenuVerticalPadding.toPx().toInt() } * 2
        for ((_, itemSize) in itemHeights.toSortedMap()) {
            sum += itemSize
            if (sum >= baseHeightInt) {
                return@remember with(density) { (sum - itemSize / 2).toDp() }
            }
        }
        // all items fit into base height
        baseHeight
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = modifier,
            colors = colors,
            value = selectedOptionText,
            readOnly = readOnly,
            enabled = enable,
            onValueChange = { selectedOptionText = it },
            placeholder = placeholder,
            trailingIcon = {
                IconToggleButton(
                    checked = expanded,
                    onCheckedChange = {
                        expanded = it
                    }
                ) {
                    if (expanded) Icon(
                        imageVector = openedIcon,
                        contentDescription = null
                    ) else Icon(
                        imageVector = closedIcon,
                        contentDescription = null
                    )
                }
            },
            shape = RoundedCornerShape(parentTextFieldCornerRadius),
            isError = isError,
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                expanded = !expanded
                            }
                        }
                    }
                }
        )
        if (expanded) {
            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .requiredSizeIn(maxHeight = maxHeight),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        value = searchedOption,
                        onValueChange = { selectedSport ->
                            searchedOption = selectedSport
                            filteredItems = listOfItems.filter {
                                it.toString().contains(
                                    searchedOption,
                                    ignoreCase = true
                                )
                            }.toMutableList()
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                        },
                        placeholder = {
                            Text(text = "Search")
                        }
                    )

                    val items = if (filteredItems.isEmpty()) {
                        listOfItems
                    } else {
                        filteredItems
                    }

                    items.forEach { selectedItem ->
                        DropdownMenuItem(
                            onClick = {
                                selectedOptionText = selectedItem.toString()
                                onDropDownItemSelected(selectedItem)
                                searchedOption = ""
                                expanded = false
                            },
                            text = {
                                dropdownItem(selectedItem)
                            },
                            colors = MenuDefaults.itemColors()
                        )
                    }
                }
            }
        }
    }
}

private val DropdownMenuVerticalPadding = 8.dp