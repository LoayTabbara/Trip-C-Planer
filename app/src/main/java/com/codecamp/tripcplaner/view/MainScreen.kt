package com.codecamp.tripcplaner.view

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import com.codecamp.tripcplaner.view.widgets.MainScreenDCard
import com.codecamp.tripcplaner.view.widgets.TripCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(navController: NavController) {
    Text("MainScreen")
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
        )
    )
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
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier= Modifier
            .fillMaxSize()
            .padding(top = 30.dp, start = 5.dp, end = 5.dp)) {
            Text(text ="This Week", style = MaterialTheme.typography.displayMedium)
            Spacer(modifier =Modifier.height(10.dp))
            MainScreenDCard()
            Spacer(modifier =Modifier.height(10.dp))

LazyColumn(){
    items(1){
        TripCard(tripName = "Mt. Everest", tripDescription = "Mount Everest is Earth's highest mountain above sea level, located in the Mahalangur Himal sub-range of the Himalayas.", tripType ="Walk" )
        Spacer(modifier =Modifier.height(10.dp))
    }
    items(1){
        TripCard(tripName = "Berlin", tripDescription = "The Capital of Germany", tripType ="Car" )
        Spacer(modifier =Modifier.height(10.dp))
    }
    items(1){
        TripCard(tripName = "Paris", tripDescription ="A wonderful journey. Traveled by bus" , tripType ="Bus" )
        Spacer(modifier =Modifier.height(10.dp))
    }
    items(1){
        TripCard(tripName = "Gottingen", tripDescription = "Visited with a b and c for fun" , tripType ="Bicycle" )
        Spacer(modifier =Modifier.height(10.dp))

    }
    items(1){
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp, bottom = 40.dp, top = 10.dp)) {
            Button(onClick = {
                navController.navigate(TripCPlanerScreens.MapScreen.name)
            }, shape = RoundedCornerShape(10.dp),modifier= Modifier
                .height(100.dp)
                .fillMaxWidth(),elevation = ButtonDefaults.buttonElevation(5.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.DarkGray )) {

                Icon(imageVector = Icons.Default.Add, contentDescription = "Add icon")
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(text = "Goto MapScreen")
            }
        }
    }



}










        }
    }

}