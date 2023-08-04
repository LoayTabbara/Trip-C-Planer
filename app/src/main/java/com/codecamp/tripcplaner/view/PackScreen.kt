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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import com.codecamp.tripcplaner.view.widgets.PackCard
import com.codecamp.tripcplaner.viewModel.DetailViewModel
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import java.time.LocalDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackScreen(
    navController: NavController,
    viewModel: DetailViewModel,
    travelInfoViewModel: TravelInfoViewModel
) {
    val counter = remember {
        mutableIntStateOf(0)
    }

    val popUpOnAdd = remember {
        mutableStateOf(false)
    }
    val popUpOnSave = remember {
        mutableStateOf(false)
    }
    val newItem = remember {
        mutableStateOf("")
    }
    val newTitle = remember {
        mutableStateOf("")

    }
    val myList =
        mutableMapOf<String, Boolean>()

    val deletedList = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(if (popUpOnAdd.value || popUpOnSave.value) 0.dp else 10.dp)
            .blur(if (popUpOnAdd.value || popUpOnSave.value) 20.dp else 0.dp)
            .verticalScroll(enabled = true, state = rememberScrollState())
    ) {
        Text(text = "Travel Period ", style = MaterialTheme.typography.displaySmall)
        Row {

            Text(
                text = "from ${
                    viewModel.getStartDate().toString().substring(0, 10)
                } to ${viewModel.getEndDate().toString().substring(0, 10)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(text = "Places to Visit", style = MaterialTheme.typography.displaySmall)
        travelInfoViewModel.citiesWithActivity.forEach {
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth(0.2f)) {

                    Text(
                        text = "${it.key}:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(Modifier.fillMaxWidth()) {

                    Text(
                        text = "${it.value[0]}\n${it.value[1]}\n",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        travelInfoViewModel.citiesWithActivity.keys.forEach {
            Log.d("citiesWithActivity", it)
        }
        Text(text = "Your Packlist", style = MaterialTheme.typography.displayMedium)
        Column(modifier = Modifier.padding(top = 10.dp)) {

            for (item in travelInfoViewModel.packingList) {

                PackCard(item = item, viewModel = viewModel, onDelete = {
                    counter.value++
                    deletedList.add(it)

                }) {
                    if (it) {
                        myList[item] = false
                        Log.d("myList  +", myList.toString())
                    } else {
                        myList.remove(item)
                        Log.d("myList   -", myList.toString())
                    }
                }
                Spacer(modifier = Modifier.padding(5.dp))

            }

            Row {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(horizontal = 5.dp)

                ) {
                    Button(
                        onClick = {
                            popUpOnAdd.value = true

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
                            popUpOnSave.value = true
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

        if (popUpOnAdd.value) {
            Popup(
                alignment = Alignment.Center, onDismissRequest = { popUpOnAdd.value = false },
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.3f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Add new item",
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                        TextField(
                            value = newItem.value,
                            onValueChange = { newItem.value = it },
                            label = { Text(text = " + Enter new item") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.textFieldColors(Color.LightGray),
                            keyboardActions = KeyboardActions(onDone = {

                                if (newItem.value != "") {
                                    travelInfoViewModel.addToPackingList(newItem.value)
                                }
                                newItem.value = ""
                                popUpOnAdd.value = false
                            }),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Text
                            )
                        )

                    }
                }
            }
        }
        if (popUpOnSave.value) {
            Popup(
                alignment = Alignment.Center, onDismissRequest = { popUpOnSave.value = false },
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.5f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Add Title and Save Your Choices",
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                        TextField(
                            value = newTitle.value,
                            onValueChange = { newTitle.value = it },
                            label = { Text(text = " + Enter Plan Title") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.textFieldColors(Color.LightGray),
                            keyboardActions = KeyboardActions(onDone = {

                                travelInfoViewModel.citiesWithActivity.values.forEach {
                                    viewModel.activities.addAll(it)
                                }
                                if (newTitle.value != "") {
                                    viewModel.setNewTitle(newTitle.value)
                                }
                                Log.i("vor packlist", travelInfoViewModel.packingList.toString())

                                viewModel.setPackList(myList)
                                viewModel.cities =
                                    travelInfoViewModel.citiesWithActivity.keys.toMutableList()
                                viewModel.setDates(
                                    travelInfoViewModel.startDate,
                                    travelInfoViewModel.endDate
                                )
                                travelInfoViewModel.packingList =
                                    viewModel.getPackList().keys.toMutableList()

                                travelInfoViewModel.sendDateToSave(
                                    title = viewModel.getNewTitle(),
                                    activities = viewModel.activities,
                                    transportType = viewModel.getTransportMean(),
                                )
                                newTitle.value = ""
                                popUpOnSave.value = false
                                navController.navigate(TripCPlanerScreens.MainScreen.name)

                            }),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Text
                            )
                        )

                    }
                }
            }
        }
    }
    if (counter.value > 0) {
        for (item in deletedList) {
            travelInfoViewModel.removeFromPackingList(item)
            counter.value--
        }
        Log.d("ditem", deletedList.toString())
        deletedList.clear()
    }


}
