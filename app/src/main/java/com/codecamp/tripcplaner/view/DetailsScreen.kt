package com.codecamp.tripcplaner.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.codecamp.tripcplaner.R
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import com.codecamp.tripcplaner.model.reminder.NotificationReceiver
import com.codecamp.tripcplaner.model.reminder.scheduleNotification
import com.codecamp.tripcplaner.view.widgets.DetailCard
import com.codecamp.tripcplaner.view.widgets.StartTargetRows
import com.codecamp.tripcplaner.view.widgets.saveToDVM
import com.codecamp.tripcplaner.viewModel.DetailViewModel
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.util.Calendar


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetailsScreen(
    navController: NavController,
    id: Int,
    viewModel: DetailViewModel,
    travelInfoViewModel: TravelInfoViewModel
) {
    val myId: Int = id
    val thisTrip = travelInfoViewModel.tripRepo.getById(myId)
    if (thisTrip != null) {//leave this line here   it is not correct what the android studio says
        saveToDVM(myId, travelInfoViewModel, viewModel)

        val paintings = mutableMapOf<String, Int>()
        when (viewModel.getTransportMean()) {
            "walking" -> paintings["walking"] = R.drawable.walking
            "driving" -> paintings["driving"] = R.drawable.driving
            "transit" -> paintings["transit"] = R.drawable.transit
            "bicycling" -> paintings["bicycling"] = R.drawable.bicycling
        }
        var selectedCity by remember { mutableStateOf("") }

        // State to control the visibility of the dropdown menu
        var isDropdownMenuVisible by remember { mutableStateOf(false) }
        val calendar = Calendar.getInstance()
        val date = remember { mutableStateOf("") }
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
                date.value = "$selectedYear.$monthText.$dayText"
            },
            year,
            month,
            dayOfMonth
        )
        val reminderCancelAlert = remember { mutableStateOf(false)  }
        val initialContext: Context = LocalContext.current
        var itemIdforA by remember { mutableIntStateOf(0) }

        val popUpOn = remember { mutableStateListOf("false") }


        val confirmed = remember { mutableStateListOf("false", "", "", "", "") }

        val notificationManager = NotificationManagerCompat.from(initialContext)
        val activeNotifications = notificationManager.activeNotifications

        for (notification in activeNotifications) {
            Log.d("notif","${notification.id}")
            if(notification.id!=null){
                viewModel.viewModelScope.launch { updatedList(travelInfoViewModel, viewModel.getPackList(), notification.id.toString(),cancelReminder = true) }
                cancelNotification(
                    initialContext,
                    notification.id
                )
            }

        }


        Log.d("DetailsScreen", "DetailsScreen: ${viewModel.getPackList()}")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (popUpOn[0].toBooleanStrict()) 0.dp else 10.dp)
                .blur(if (popUpOn[0].toBooleanStrict()) 20.dp else 0.dp)
                .verticalScroll(enabled = true, state = rememberScrollState())
        ) {


            Image(
                painter = painterResource(id = paintings[viewModel.getTransportMean()]!!),
                contentDescription = "walking",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (viewModel.getNewTitle() != "") viewModel.getNewTitle() else "Untitled Trip",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(start = 10.dp)
            )
            StartTargetRows(thisTrip, viewModel)
            var i=0

            for (item in viewModel.getPackList()) {
                val itemId=myId+i
                Spacer(modifier = Modifier.height(10.dp))

                DetailCard(text = item.key,item.value[1]) {reminderPressed->

                    if (reminderPressed&&!item.value[1]) {
                        popUpOn[0] = "true"
                        popUpOn.add(1, itemId.toString())
                        popUpOn.add(2, item.key)
                    }
                    if (reminderPressed&&item.value[1]) {
                        itemIdforA=itemId
                        reminderCancelAlert.value = true

                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                i++
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                onClick = {
                    travelInfoViewModel.tripRepo.deleteById(myId)
                    navController.navigate(TripCPlanerScreens.MainScreen.name)

                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Delete this Trip")
            }
            //if notification enabled of not
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(
                key1 = lifecycleOwner,
                effect = {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_START) {

                                areNotificationEnabled = notificationManager.areNotificationsEnabled()

                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
            )
         if (!areNotificationEnabled){
             Button(
                 colors = ButtonDefaults.buttonColors(
                     containerColor = Color.Red,
                     contentColor = Color.White
                 ),
                 onClick = {
                     val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                     val uri: Uri = Uri.fromParts("package", initialContext.packageName, null)
                     intent.data = uri
                     initialContext.startActivity(intent)

                 },
                 shape = RoundedCornerShape(10.dp),
                 modifier = Modifier
                     .padding(20.dp)
                     .fillMaxWidth()

             ) {
                 Text(text = "Please Allow the Notifications from the Settings before adding a reminder")
             }
         }
            //

        }

        if (popUpOn[0].toBooleanStrict()) {
            Popup(
                alignment = Alignment.Center, onDismissRequest = { popUpOn[0] = "false"
                    date.value=""
                    selectedCity="" },
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
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Add Time+",
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                        Button(
                            onClick = { datePicker.show() },
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = if (date.value == "") "Select Date" else date.value)
                        }
                        Button(
                            onClick = { isDropdownMenuVisible = true },
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(if (selectedCity == "") "Select City" else selectedCity)
                        }

                        DropdownMenu(
                            expanded = isDropdownMenuVisible,
                            onDismissRequest = { isDropdownMenuVisible = false }) {
                            viewModel.cities.forEach { city ->
                                DropdownMenuItem(text = { Text(text = city) }, onClick = {
                                    selectedCity = city
                                    isDropdownMenuVisible = false
                                })
                            }
                        }
                        Button(enabled = !(date.value=="" || selectedCity==""),onClick = {
                            Log.d("DetailsScreen", "DetailsScreen: ${date.value} and $selectedCity")
                            confirmed[0] = "true"
                            confirmed.add(1, date.value)
                            confirmed.add(2, selectedCity)
                            confirmed.add(3, popUpOn[1])//id
                            confirmed.add(4, popUpOn[2])//item name
                            date.value=""
                            selectedCity=""
                            popUpOn[0] = "false"
                        }) {
                            Text(text = "Confirm")
                        }


                    }
                }
            }
        }

        if (reminderCancelAlert.value) {

            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                    // button. If you want to disable that functionality, simply use an empty
                    // onCloseRequest.
                    reminderCancelAlert.value = false
                },
                title = {
                    Text(text = "Alert")
                },
                text = {
                    Text("Do you want to cancel the reminder?")
                },
                confirmButton = {
                    Button(

                        onClick = {
                            if (itemIdforA!=0) {
                                cancelNotification(
                                    initialContext,
                                    itemIdforA
                                )
                                viewModel.viewModelScope.launch { updatedList(travelInfoViewModel, viewModel.getPackList(), itemIdforA.toString(),cancelReminder = true) }
                                itemIdforA=0
                                reminderCancelAlert.value = false
                            }
                            else{
                                Log.e("errorDetailsScreen", "cancelNotification: itemIdforA is 0")
                            }
                        }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(

                        onClick = {
                            reminderCancelAlert.value = false
                        }) {
                        Text("No")
                    }
                }
            )
        }
//        var notificationScheduled by remember { mutableStateOf(false) }
        if (confirmed[0].toBooleanStrict()) {

            scheduleNotification(
                LocalContext.current, confirmed[3].toInt(), confirmed[1], "PackAlert", city = confirmed[2], itemName = confirmed[4]
            )
           viewModel.viewModelScope.launch { updatedList(travelInfoViewModel, viewModel.getPackList(), confirmed[3],cancelReminder = false) }
            confirmed.clear()
            confirmed.addAll(listOf("false", "", "", "", ""))


        }
    }

}


suspend fun updatedList(travelInfoViewModel: TravelInfoViewModel, myList:MutableMap<String,MutableList<Boolean>>, itemId:String, cancelReminder:Boolean=false){
    val id= itemId.substring(2).toInt()
    myList.values.elementAt(id)[1]=!cancelReminder
    travelInfoViewModel.tripRepo.updatePackingList(itemId.toInt()-itemId.last().toString().toInt(),myList)
}





private fun cancelNotification(context: Context, notificationId: Int) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val alarmIntent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        alarmIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)

    notificationManager.cancel(notificationId)
    Log.d("notif", "cancelNotification: $notificationId")

}

@Composable
fun GoogleMapsButton(
    start: LatLng,
    target: LatLng,
    travelMode: String,
    context: Context
) {
    val googleMapsUrl = buildGoogleMapsUrl(start, target, travelMode)
    TextButton(
        onClick = {
            // Open the URL in a web browser or the Google Maps app
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsUrl))
            ContextCompat.startActivity(context, intent, null)
        },
        modifier = Modifier.fillMaxSize(0.2f)
    ) {
        Image(
            painter = painterResource(id = R.drawable.directions_icon),
            contentDescription = "Directions",
        )
    }
}

fun buildGoogleMapsUrl(start: LatLng, target: LatLng, travelMode: String): String {
    return "https://www.google.com/maps/dir/?api=1&origin=${start.latitude},${start.longitude}&destination=${target.latitude},${target.longitude}&travelmode=$travelMode"
}