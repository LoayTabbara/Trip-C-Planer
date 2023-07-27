package com.codecamp.tripcplaner.view

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.codecamp.tripcplaner.view.widgets.TripCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(navController: NavController) {
    Text("MainScreen")
    val typeActivity= remember{ mutableStateOf("") }
    val popUpOn=remember {
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
        modifier = Modifier.fillMaxSize().blur(if (popUpOn.value) 20.dp else 0.dp),
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
                if(typeActivity.value!="" && !popUpOn.value){
                    navController.navigate(TripCPlanerScreens.MapScreen.name+"/$typeActivity")

                }
                else{
                    popUpOn.value=true


                }
            }, shape = RoundedCornerShape(10.dp),modifier= Modifier
                .height(60.dp)
                .fillMaxWidth().padding(bottom = 10.dp),elevation = ButtonDefaults.buttonElevation(5.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.DarkGray )) {

                Icon(imageVector = Icons.Default.Add, contentDescription = "Add icon")
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(text = if (typeActivity.value!=""){"Continue"} else{"Add Activity To Create New Trip"})
            }
            if (typeActivity.value!=""){
                Button(onClick = {
                    typeActivity.value=""
                }, shape = RoundedCornerShape(10.dp),modifier= Modifier
                    .height(60.dp)
                    .fillMaxWidth(),elevation = ButtonDefaults.buttonElevation(5.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.DarkGray )) {

                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete icon")
                    Spacer(modifier = Modifier.width(width = 8.dp))
                    Text(text = "Cancel the trip")
                }
            }
        }
    }



}


        }
    }



    if (popUpOn.value){
        Popup(alignment = Alignment.Center, onDismissRequest = {popUpOn.value=false},
            properties = PopupProperties(focusable= true, dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Card(modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f)){
                Column(modifier= Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp), verticalArrangement = Arrangement.SpaceAround) {
                    Text(text ="Add Activity", style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(10.dp))
                   Button(onClick = {
                       typeActivity.value="Walk"
                       popUpOn.value=false
                                    },modifier=Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color.Gray)) {
                       Text(text ="Walk")
                   }
                    Button(onClick = {
                        typeActivity.value= "Car"
                        popUpOn.value=false
                    },modifier=Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color.Gray)) {
                        Text(text ="Car")
                    }
                    Button(onClick = {
                        typeActivity.value="Bus"
                        popUpOn.value=false
                    },modifier=Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color.Gray)) {
                        Text(text ="Bus")
                    }
                    Button(onClick = {
                        typeActivity.value= "Bicycle"
                        popUpOn.value=false
                    },modifier=Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color.Gray)) {
                        Text(text ="Bicycle")
                    }

                }
            }
        }
    }
}