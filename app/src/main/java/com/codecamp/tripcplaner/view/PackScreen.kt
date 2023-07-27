package com.codecamp.tripcplaner.view

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import com.codecamp.tripcplaner.view.widgets.PackCards
import com.codecamp.tripcplaner.viewModel.DetailViewModel
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackScreen(
    navController: NavController,
    viewModel: DetailViewModel,
    travelInfoViewModel: TravelInfoViewModel
) {
//
//    var list2part = JsonParser().parse(list2).asJsonObject
//
//    // Access the data using the variable
//    var list3=list2part.get("packing_list")
//    var list4= list3.asJsonArray.toList()
//    var list5= list4.get(0).toString()

//    val list =
//       remember{ mutableStateListOf<String>(
//            "Clothes",
//            "Toiletries",
//            "Travel documents",
//            "Money/Credit cards",
//            "Phone and charger",
//            "Camera",
//            "Travel adapter",
//            "Comfortable shoes",
//            "Guidebook/map",
//            "Reusable water bottle"
//        )}

    val popUpOn=remember {
        mutableStateOf(false)
    }
    val newItem=remember {
        mutableStateOf("")
    }
    val myList =
        mutableListOf<String>()

    val deletedList = mutableListOf<String>()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(if (popUpOn.value) 0.dp else 10.dp)
            .blur(if (popUpOn.value) 20.dp else 0.dp)
            .verticalScroll(enabled = true, state = rememberScrollState())
    ) {
        Text(text = "Your Packlist", style = MaterialTheme.typography.displayMedium)
        Column(modifier = Modifier.padding(top = 10.dp)) {
            for (item in travelInfoViewModel.packingList) {
                Log.d("item", viewModel.getLongPressed().toString())
                if (viewModel.getLongPressed()) {
                    deletedList.add(item) // Collect the items to be deleted
                    viewModel.setLongPressed(false)
                }
                    PackCards(item = item,viewModel = viewModel) {
                        if (it) {
                            myList.add(item)
                            Log.d("myList  +", myList.toString())
                        } else {
                            myList.remove(item)
                            Log.d("myList   -", myList.toString())
                        }
                    }
                    Spacer(modifier = Modifier.padding(5.dp))

            }

               Row(){
                   Column(
                       modifier = Modifier
                           .fillMaxWidth(0.5f)
                           .padding(horizontal = 5.dp)

                   ) {
                       Button(
                           onClick = {
                               popUpOn.value=true

                           },
                           shape = RoundedCornerShape(10.dp),
                           modifier = Modifier
                               .height(60.dp)
                               .fillMaxWidth(),
                           elevation = ButtonDefaults.buttonElevation(5.dp),
                           colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.DarkGray)
                       ) {

                           Icon(
                               imageVector = Icons.Default.Add,
                               contentDescription = "Add icon",
                               modifier = Modifier
                                   .scale(1.2f)
                                   .padding(end = 5.dp)
                           )

                           Text(text = "Add New Item", style = MaterialTheme.typography.bodyMedium)
                       }
                   }


                   Column(
                       modifier = Modifier
                           .fillMaxWidth(1f)
                           .padding(horizontal = 5.dp)

                   ) {
                       Button(
                           onClick = {

                               viewModel.setList(myList)

                               navController.navigate(TripCPlanerScreens.DetailsScreen.name)
                           },
                           shape = RoundedCornerShape(10.dp),
                           modifier = Modifier
                               .height(60.dp)
                               .fillMaxWidth(),
                           elevation = ButtonDefaults.buttonElevation(5.dp),
                           colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.DarkGray)
                       ) {

                           Icon(
                               imageVector = Icons.Default.Add,
                               contentDescription = "Add icon",
                               modifier = Modifier
                                   .scale(1.2f)
                                   .padding(end = 5.dp)
                           )

                           Text(text = "Save ", style = MaterialTheme.typography.bodyMedium)
                       }
                   }
               }


        }

        if (popUpOn.value){
            Popup(alignment = Alignment.Center, onDismissRequest = {popUpOn.value=false},
                properties = PopupProperties(focusable= true, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                    Card(modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.3f)){
                        Column(modifier=Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                            Text(text ="Add new item", style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(10.dp))
                            TextField(value = newItem.value, onValueChange ={newItem.value=it}, label = {Text(text = " + Enter new item")}, modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp), singleLine = true, shape = RoundedCornerShape(10.dp), colors = TextFieldDefaults.textFieldColors(Color.LightGray),
                                 keyboardActions = KeyboardActions(onDone = {

                                        if (newItem.value!=""){travelInfoViewModel.addToPackingList(newItem.value)}
                                     newItem.value=""
                                     popUpOn.value=false}),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text)
                            )

                        }
                    }
            }
        }
    }
    for (item in deletedList) {
        travelInfoViewModel.removeFromPackingList(item)
    }
    deletedList.clear()

}

@Preview(showBackground = true)
@Composable
fun PackScreenPreview() {

}