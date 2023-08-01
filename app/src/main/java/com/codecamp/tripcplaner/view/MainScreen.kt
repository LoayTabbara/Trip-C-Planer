package com.codecamp.tripcplaner.view

import android.Manifest
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.codecamp.tripcplaner.R
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import com.codecamp.tripcplaner.view.widgets.MainScreenDCard
import com.codecamp.tripcplaner.view.widgets.TripCard
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, travelInfoViewModel: TravelInfoViewModel) {

    Text("MainScreen")
    val transportMean = remember { mutableStateOf("") }
    val shareCode = remember { mutableStateOf("") }
    val popUpOn = remember {
        mutableStateOf(false)
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
        )
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, effect = {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                permissionsState.launchMultiplePermissionRequest()
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
                .padding(start = 5.dp, end = 5.dp, bottom = 10.dp, top = 10.dp)
        ) {
            Button(
                onClick = {
                    if (transportMean.value != "" && !popUpOn.value) {
                        navController.navigate(TripCPlanerScreens.MapScreen.name + "/${transportMean.value}")
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
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.DarkGray)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add icon"
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(text = "Choose Means of Transport to create a trip")
            }
        }
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .blur(if (popUpOn.value) 20.dp else 0.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp, start = 5.dp, end = 5.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "This Week", style = MaterialTheme.typography.displayMedium)
                    TextField(
                        value = shareCode.value,
                        onValueChange = { shareCode.value = it },
                        placeholder = {
                            Text(
                                text = "Shared code",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.7f).padding(end = 8.dp).border(
                                width = 2.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(10.dp)),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.textFieldColors(Color.White, focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,),
                        keyboardActions = KeyboardActions(onDone = {
                            /*TODO() use shareCode.value to do GET request to get the saved json object from the other side and navigate*/
//                            navController.navigate(TripCPlanerScreens.PackScreen.name)
                            shareCode.value = ""

                        }),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        )
                    )
//                    Button(
//                        onClick = { /*TODO*/ },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.Transparent,
//                            contentColor = Color.White
//                        )
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.add_trip_icon),
//                            contentDescription = "Add Trip"
//                        )
//                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                MainScreenDCard()
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn {
                    items(items = travelInfoViewModel.tripRepo.getAllItems().reversed()) { item ->
                        TripCard(
                            tripName = item.title + "\n${item.cities.keys.first()} - ${item.cities.keys.last()}",
                            tripDescription = item.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " -" +
                                    " " + item.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n" + item.cities.keys.first() + "(${item.activities[0]}, ${item.activities[1]}) .." +
                                    ". ${item.cities.keys.last()}(${item.activities[item.activities.lastIndex - 1]}, ${item.activities.last()})",
                            tripType = item.transportType,
                            onShareClicked = {

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
                        modifier = Modifier.padding(10.dp)
                    )
                    Button(
                        onClick = {
                            transportMean.value = "walking"
                            popUpOn.value = false
                            navController.navigate(TripCPlanerScreens.MapScreen.name + "/${transportMean.value}")

                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text(text = "Walking")
                    }
                    Button(
                        onClick = {
                            transportMean.value = "driving"
                            popUpOn.value = false
                            navController.navigate(TripCPlanerScreens.MapScreen.name + "/${transportMean.value}")

                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text(text = "Car")
                    }
                    Button(
                        onClick = {
                            transportMean.value = "transit"
                            popUpOn.value = false
                            navController.navigate(TripCPlanerScreens.MapScreen.name + "/${transportMean.value}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text(text = "Bus")
                    }
                    Button(
                        onClick = {
                            transportMean.value = "bicycling"
                            popUpOn.value = false
                            navController.navigate(TripCPlanerScreens.MapScreen.name + "/${transportMean.value}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text(text = "Bicycling")
                    }

                }
            }
        }
    }

}
