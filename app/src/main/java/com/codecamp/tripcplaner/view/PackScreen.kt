package com.codecamp.tripcplaner.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import com.codecamp.tripcplaner.view.widgets.PackCards
import com.google.gson.JsonArray
import com.google.gson.JsonParser


@Composable
fun PackScreen(navController: NavController){
//
//    var list2part = JsonParser().parse(list2).asJsonObject
//
//    // Access the data using the variable
//    var list3=list2part.get("packing_list")
//    var list4= list3.asJsonArray.toList()
//    var list5= list4.get(0).toString()
val list= remember{mutableListOf<String>("Clothes", "Toiletries", "Travel documents",
    "Money/Credit cards", "Phone and charger", "Camera", "Travel adapter", "Comfortable shoes", "Guidebook/map", "Reusable water bottle")}

val myList=remember{ mutableListOf<String>() }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
        Text(text ="Your Packlist", style = MaterialTheme.typography.displayMedium)
        LazyColumn( modifier=Modifier.padding(top=10.dp) ){
            for(item in list){
                item {
                    PackCards( item = item){
                        if (it){
                            myList.add(item)
                            Log.d("myList", myList.toString())
                        }
                        else{
                            myList.remove(item)
                            Log.d("myList", myList.toString())
                        }
                    }
                    Spacer(modifier =Modifier.padding(5.dp))
                }

            }
            item {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp, bottom = 40.dp, top = 10.dp)) {
                    Button(onClick = {
                        navController.navigate(TripCPlanerScreens.MapScreen.name)
                    }, shape = RoundedCornerShape(10.dp),modifier= Modifier
                        .height(150.dp)
                        .fillMaxWidth(),elevation = ButtonDefaults.buttonElevation(5.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.DarkGray )) {

                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add icon", modifier = Modifier.scale(2f).padding(end = 10.dp))

                        Text(text = "Add New Item",style= MaterialTheme.typography.displaySmall)
                    }
                }
            }
            item {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp, bottom = 40.dp, top = 10.dp)) {
                    Button(onClick = {
                        navController.navigate(TripCPlanerScreens.DetailsScreen.name)
                    }, shape = RoundedCornerShape(10.dp),modifier= Modifier
                        .height(150.dp)
                        .fillMaxWidth(),elevation = ButtonDefaults.buttonElevation(5.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.DarkGray )) {

                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add icon", modifier = Modifier.scale(2f).padding(end = 10.dp))

                        Text(text = "Save ",style= MaterialTheme.typography.displaySmall)
                    }
                }
            }

        }

    }



}
@Preview(showBackground = true)
@Composable
fun PackScreenPreview() {

}