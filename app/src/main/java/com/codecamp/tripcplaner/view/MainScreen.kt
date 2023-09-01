package com.codecamp.tripcplaner.view

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import com.codecamp.tripcplaner.view.widgets.MainScreenDCard
import com.codecamp.tripcplaner.view.widgets.ThemeSwitch
import com.codecamp.tripcplaner.view.widgets.TripCard
import com.codecamp.tripcplaner.viewModel.DetailViewModel
import com.codecamp.tripcplaner.viewModel.ThemeViewModel
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    travelInfoViewModel: TravelInfoViewModel,
    detailsViewModel: DetailViewModel,
    themeViewModel: ThemeViewModel,
    routedId: String?
) {

    Text("MainScreen")
    //variables to store the means of transport and the shared code
    val meansOfTransport = remember { mutableStateOf("") }
    val shareCode = remember { mutableStateOf("") }
    //variable to store the state of the popup
    val popUpOn = remember {
        mutableStateOf(false)
    }
    //variable to store the state of the permissions
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
        )
    )
    //variable to store the lifecycle owner
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    //effect to launch the permissions request and fetch the trip
    DisposableEffect(key1 = lifecycleOwner, effect = {

        val observer = LifecycleEventObserver { _, event ->
            // Launch the permissions request when the screen is resumed
            if (event == Lifecycle.Event.ON_START) {
                permissionsState.launchMultiplePermissionRequest()
            }
            //fetch the trip if the shared code is not null and to check the intentSharedCodeUsed variable from the viewModel
            if (routedId != null && !travelInfoViewModel.intentSharedCodeUsed.value) {
                travelInfoViewModel.intentSharedCodeUsed.value = true
                travelInfoViewModel.fetchTrip(routedId, detailsViewModel, context, navController)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    Scaffold(bottomBar = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiary)
        ) {
            //Button to add a new trip by choosing the means of transport
            Button(
                onClick = {
                    if (meansOfTransport.value != "" && !popUpOn.value) {
                        navController.navigate(TripCPlanerScreens.MapScreen.name + "/${meansOfTransport.value}")
                    } else {
                        popUpOn.value = true
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                elevation = ButtonDefaults.buttonElevation(5.dp),
                colors = ButtonDefaults.outlinedButtonColors
                    (containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add icon",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(text = "Choose Transportation Options",
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .blur(if (popUpOn.value) 20.dp else 0.dp),
            color = MaterialTheme.colorScheme.tertiary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp, start = 5.dp, end = 5.dp)

            ) {
                //Row to display the title, the switch to change the theme and also to display the shared code
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                        Text(text = "Your Trips", style = MaterialTheme.typography.displayMedium,
                            color = Color.White)
                            ThemeSwitch(scale = 1.2f,themeViewModel=themeViewModel)
                        Box(modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth()){
                        }

                    }

                    TextField(

                        value = shareCode.value,
                        onValueChange = { shareCode.value = it },
                        placeholder = {
                            Text(
                                text = "Shared Code",
                                style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.onPrimary),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            MaterialTheme.colorScheme.onPrimary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            // Calling fetchTrip from viewModel using the sharedCode value
                            travelInfoViewModel.fetchTrip(
                                shareCode.value,
                                detailsViewModel,
                                context,
                                navController
                            )
                            shareCode.value = ""

                        }),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                //Card to display the trip dates
                MainScreenDCard(travelInfoViewModel, navController)
                Spacer(modifier = Modifier.height(10.dp))
                //LazyColumn to display the trips with short descriptions , TripCard is used to display each trip
                LazyColumn {
                    items(
                        items = travelInfoViewModel.tripRepo.getAllItems().reversed()
                    ) { item ->
                        TripCard(
                            tripName = item.title + "\n${item.cities.keys.first()} - ${item.cities.keys.last()}",


                            tripDescription = item.startDate.format(
                                DateTimeFormatter.ofPattern(
                                    "yyyy-MM-dd"
                                )
                            ) + " -" + " " + item.endDate.format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            ) + "\n" + item.cities.keys.first() + "(${item.activities[0]}, ${item.activities[1]}) .." + ". ${item.cities.keys.last()}(${item.activities[item.activities.lastIndex - 1]}, ${item.activities.last()})",
                            tripType = item.transportType,
                            onShareClicked = {
                                travelInfoViewModel.shareTrip(item, context)
                            },
                            onClicked = {
                                navController.navigate(TripCPlanerScreens.DetailsScreen.name + "/${item.id}")
                            },
                        )
                    }
                }
            }
        }


    }
    //Select mean of transport Walking , car , bus , bicycling
    if (popUpOn.value) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = { popUpOn.value = false },
            properties = PopupProperties(
                focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.5f)


            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = "Choose Transport Mean",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Button(
                        onClick = {
                            meansOfTransport.value = "walking"
                            popUpOn.value = false
                            navController.navigate(TripCPlanerScreens.MapScreen.name + "/${meansOfTransport.value}")

                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(text = "Walking",
                          color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Button(
                        onClick = {
                            meansOfTransport.value = "driving"
                            popUpOn.value = false
                            navController.navigate(TripCPlanerScreens.MapScreen.name + "/${meansOfTransport.value}")

                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors
                            (MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(text = "Car",
                            color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Button(
                        onClick = {
                            meansOfTransport.value = "transit"
                            popUpOn.value = false
                            navController.navigate(TripCPlanerScreens.MapScreen.name + "/${meansOfTransport.value}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.
                        buttonColors(MaterialTheme.colorScheme.secondary)
                    ) {

                        Text(text = "Bus",color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Button(
                        onClick = {
                            meansOfTransport.value = "bicycling"
                            popUpOn.value = false
                            navController.navigate(TripCPlanerScreens.MapScreen.name + "/${meansOfTransport.value}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                    ) {

                        Text(text = "Bicycling",color = MaterialTheme.colorScheme.onPrimary)
                    }

                }
            }
        }
    }
}
