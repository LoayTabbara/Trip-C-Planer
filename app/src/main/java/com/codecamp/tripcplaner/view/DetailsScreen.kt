package com.codecamp.tripcplaner.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.codecamp.tripcplaner.R
import com.codecamp.tripcplaner.model.data.Trip
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
    // get the trip from the database by id
    val thisTrip = travelInfoViewModel.tripRepo.getById(myId)
    if (thisTrip != null) {//leave this line here   it is not correct what the android studio says
        // save the trip to the detail view model, so that we can use it form detail view model
        saveToDVM(myId, travelInfoViewModel, viewModel)

        // get the painting depending on the transport mean
        val paintings = mutableMapOf<String, Int>()
        when (viewModel.getTransportMean()) {
            "walking" -> paintings["walking"] = R.drawable.walking
            "driving" -> paintings["driving"] = R.drawable.driving
            "transit" -> paintings["transit"] = R.drawable.transit
            "bicycling" -> paintings["bicycling"] = R.drawable.bicycling
        }

        // to save the selected city from the item alert popup
        var selectedCity by remember { mutableStateOf("") }

        // State to control the visibility of the dropdown menu for the cities in item alert popup
        var isDropdownMenuVisible by remember { mutableStateOf(false) }


        // State to control the visibility of the notification permissions dialog
        var showNotificationPermissionsDialog by remember { mutableStateOf(false) }


        //date picker save date as string in the format of "yyyy.mm.dd" under the item alert popup
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
        //pop up alert after pressing cancel reminder
        val reminderCancelAlert = remember { mutableStateOf(false) }
        //pop up alert after pressing checkbox
        val checkCancelAlert = remember { mutableStateOf(false) }
        //checkbox state
        val isChecked = remember { mutableStateOf("") }
        //clicked item title for the checkbox alert
        val clickedItemTitle = remember { mutableStateOf("") }
        //clicked item message for the checkbox alert
        val clickedItemMessage = remember { mutableStateOf("") }
        val initialContext: Context = LocalContext.current
        //item id for the item whose reminder or checkbox is pressed
        var itemIdforA by remember { mutableIntStateOf(0) }

        //number of unchecked items
        var remainingChecks by remember { mutableIntStateOf(getUncheckedItems(viewModel.getPackList())) }

        //it will pop up a modal asking for time and location for the alert, it also contains other information
        // from the loop of the exact item for which the alert is pressed and passes value to the confirmed variable on confirm button pressed
        val popUpOn = remember { mutableStateListOf("false") }

        // when its true, it passes the extra values to the scheduleNotification function
        val confirmed = remember { mutableStateListOf("false", "", "", "", "") }
        //get the notification manager
        val notificationManager = NotificationManagerCompat.from(initialContext)
        //check if notification enabled or not
        var areNotificationEnabled by remember { mutableStateOf(notificationManager.areNotificationsEnabled()) }
        //get the active notifications
        val activeNotifications = notificationManager.activeNotifications
        //cancel all the active notifications for this trip if the user was already notified. Its checked every time the details screen is opened
        for (notification in activeNotifications) {
            if (notification.id != null) {
                viewModel.viewModelScope.launch {
                    updatedList(
                        travelInfoViewModel,
                        viewModel.getPackList(),
                        notification.id.toString(),
                        cancelReminder = true
                    )
                }
                cancelNotification(
                    initialContext,
                    notification.id
                )
            }

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (popUpOn[0].toBooleanStrict()) 0.dp else 10.dp)
                .blur(if (popUpOn[0].toBooleanStrict()) 20.dp else 0.dp)
                .verticalScroll(enabled = true, state = rememberScrollState())
                .background(MaterialTheme.colorScheme.onTertiary)


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
                modifier = Modifier.padding(start = 10.dp),
                color = MaterialTheme.colorScheme.onSecondary
            )
            StartTargetRows(thisTrip, viewModel)
            var i = 0
            Text(
                text = remainingChecks.toString() + " of " + viewModel.getPackList().size + " items remaining",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(10.dp)
            )
            for (item in viewModel.getPackList()) {

                val itemId = (myId * 100) + i
                Spacer(modifier = Modifier.height(10.dp))
                //passing the values to the DetailCard composable and getting the values back
                //reminderPressed is true when the reminder button is pressed
                //checkedPressed is true when the checkbox is pressed
                //isChecked is the state of the checkbox
                //reminderCancelAlert is true when the cancel reminder alert is pressed
                //checkCancelAlert is true when the checkbox alert is pressed
                //it also shows the items in the packing list
                DetailCard(
                    text = item.key,
                    item.value[1],
                    isChecked = item.value[0]
                ) { reminderPressed, checkedPressed ->
                    if (checkedPressed) {
                        itemIdforA = itemId
                        if (item.value[0]) {
                            isChecked.value = "false"
                            clickedItemTitle.value = "Unchecked " + item.key
                            clickedItemMessage.value =
                                "Do not forget to pack your " + item.key + "!"
                        } else {
                            isChecked.value = "true"
                            clickedItemTitle.value = "Checked " + item.key
                            clickedItemMessage.value = "You have packed your " + item.key + "!"
                        }

                        checkCancelAlert.value = true
                    }

                    if (reminderPressed) {
                        if (!areNotificationEnabled) {
                            showNotificationPermissionsDialog = true
                        } else if (!item.value[1]) {
                            popUpOn[0] = "true"
                            popUpOn.add(1, itemId.toString())
                            popUpOn.add(2, item.key)
                        } else {
                            itemIdforA = itemId
                            reminderCancelAlert.value = true
                        }
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
                i++
            }

            Spacer(modifier = Modifier.height(10.dp))
            //delete the trip from database and navigate to main screen
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

                Text(text = "Delete this Trip",color = MaterialTheme.colorScheme.onTertiary)
            }
            //on resume checks if notification enabled of not and updates the state of areNotificationEnabled
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(
                key1 = lifecycleOwner,
                effect = {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_START) {
                            showNotificationPermissionsDialog =
                                !notificationManager.areNotificationsEnabled()
                            areNotificationEnabled = notificationManager.areNotificationsEnabled()

                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
            )
        }
        //item alert popup for reminder and select date and location for the reminder
        if (popUpOn[0].toBooleanStrict()) {
            Popup(
                alignment = Alignment.Center, onDismissRequest = {
                    popUpOn[0] = "false"
                    date.value = ""
                    selectedCity = ""
                },
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
                        Button(enabled = !(date.value == "" && selectedCity == ""), onClick = {
                            confirmed[0] = "true"
                            confirmed.add(1, date.value)
                            confirmed.add(2, selectedCity)
                            confirmed.add(3, popUpOn[1])//id
                            confirmed.add(4, popUpOn[2])//item name
                            date.value = ""
                            selectedCity = ""
                            popUpOn[0] = "false"
                        }) {
                            Text(text = "Confirm")
                        }


                    }
                }
            }
        }
        //cancel reminder alert
        if (reminderCancelAlert.value) {

            AlertDialog(
                onDismissRequest = {
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
                            if (itemIdforA != 0) {
                                cancelNotification(
                                    initialContext,
                                    itemIdforA
                                )
                                viewModel.viewModelScope.launch {
                                    updatedList(
                                        travelInfoViewModel,
                                        viewModel.getPackList(),
                                        itemIdforA.toString(),
                                        cancelReminder = true
                                    )
                                }
                                itemIdforA = 0
                                reminderCancelAlert.value = false
                            } else {
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

        if (showNotificationPermissionsDialog) {
            AlertDialog(

                onDismissRequest = {
                    showNotificationPermissionsDialog = false
                },
                {
                    Column {
                        Text(
                            text = "Notifications are disabled",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Please enable the notifications in order to use the reminder functions",
                            fontSize = 16.sp,
                            color = Color.White,
                            textAlign = TextAlign.Left
                        )
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            onClick = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                val uri: Uri =
                                    Uri.fromParts("package", initialContext.packageName, null)
                                intent.data = uri
                                initialContext.startActivity(intent)

                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth()

                        ) {
                            Text(text = "Go to Settings")
                        }
                    }

                }

            )
        }

        //checkbox alert
        if (checkCancelAlert.value) {

            AlertDialog(
                onDismissRequest = {// can only be dismissed by confirm
                },
                title = {
                    Text(text = clickedItemTitle.value)
                },
                text = {
                    Text(clickedItemMessage.value)
                },
                confirmButton = {
                    Button(

                        onClick = {
                            if (itemIdforA != 0) {

                                viewModel.viewModelScope.launch {
                                    updatedList(
                                        travelInfoViewModel,
                                        viewModel.getPackList(),
                                        itemIdforA.toString(),
                                        cancelReminder = false,
                                        updateCheck = true,
                                        isChecked = isChecked.value.toBooleanStrict()
                                    )
                                }
                                itemIdforA = 0
                                isChecked.value = ""
                                checkCancelAlert.value = false
                                remainingChecks = getUncheckedItems(viewModel.getPackList())
                            } else {
                                Log.e("errorDetailsScreen", "CheckboxError")
                            }
                        }) {
                        Text("Ok!")
                    }
                }
            )
        }


        //when the confirm button is pressed, it schedules the notification and updates the packing list in the database
        if (confirmed[0].toBooleanStrict()) {
            val (cityMessage, currentDateMessage) = getMessageContents(
                confirmed[1],
                confirmed[2],
                thisTrip
            )
            scheduleNotification(
                LocalContext.current,
                confirmed[3].toInt(),
                currentDateMessage,
                "PackAlert",
                city = cityMessage,
                itemName = confirmed[4]
            )
            viewModel.viewModelScope.launch {
                updatedList(
                    travelInfoViewModel,
                    viewModel.getPackList(),
                    confirmed[3],
                    cancelReminder = false
                )
            }
            confirmed.clear()
            confirmed.addAll(listOf("false", "", "", "", ""))


        }
    }

}


/**
 * Updates the packing list in the database
 * @param travelInfoViewModel the travel info view model
 * @param myList the packing list
 * @param itemId the item ID
 * @param cancelReminder `true` if the reminder should be cancelled, `false` otherwise
 * @param updateCheck `true` if the checkbox should be updated, `false` otherwise
 * @param isChecked `true` if the checkbox is checked, `false` otherwise
 * @see TravelInfoViewModel
 * @see TravelInfoViewModel.tripRepo
 */
suspend fun updatedList(
    travelInfoViewModel: TravelInfoViewModel,
    myList: MutableMap<String, MutableList<Boolean>>,
    itemId: String,
    cancelReminder: Boolean = false,
    updateCheck: Boolean = false,
    isChecked: Boolean = false
) {
    val id = itemId.substring(4).toInt()
    if (updateCheck) {
        myList.values.elementAt(id)[0] = isChecked
    } else {
        myList.values.elementAt(id)[1] = !cancelReminder
    }
    travelInfoViewModel.tripRepo.updatePackingList(
        (itemId.toInt() - itemId.last().toString().toInt()) / 100, myList
    )
}


/**
 * Cancels the notification with the given notification ID.
 * @param context the context
 * @param notificationId the notification ID
 * @see NotificationManager.cancel
 * @see AlarmManager.cancel
 * @see NotificationReceiver
 */
private fun cancelNotification(context: Context, notificationId: Int) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

}

/**
 * Returns the message for the notification.
 * @param dateTime the date and time
 * @param city the city
 * @param trip the trip
 * @return the message for the notification
 */
private fun getMessageContents(dateTime: String, city: String, trip: Trip): Pair<String, String> {
    val cityMessage: String
    val currentDate = trip.cities[city]?.second


    var currentDateMessage = dateTime
    if (city != "") {
        cityMessage = trip.cities[city]!!.first.toString() + "-" + city
        currentDateMessage =
            if (dateTime == "") "${currentDate?.year}.${currentDate?.monthValue}.${currentDate?.dayOfMonth}" else dateTime

    } else {
        cityMessage = "No place specified"

    }
    return Pair(cityMessage, currentDateMessage)
}


/**
 * button for google maps routing
 * @param start the start location
 * @param target the target location
 * @param travelMode the travel mode
 * @param context the context
 * @see buildGoogleMapsUrl
 */
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

/**
 * Returns the number of unchecked items in the packing list
 * @param packList the packing list
 * @return the number of unchecked items
 */
fun getUncheckedItems(packList: MutableMap<String, MutableList<Boolean>>): Int {
    var uncheckedCounter = 0
    for (item in packList) {

        if (!item.value[0]) {
            uncheckedCounter++
        }
    }
    return uncheckedCounter

}


/**
 * Builds the Google Maps URL.
 * @param start the start location
 * @param target the target location
 * @param travelMode the travel mode
 * @return the Google Maps URL
 */
fun buildGoogleMapsUrl(start: LatLng, target: LatLng, travelMode: String): String {
    return "https://www.google.com/maps/dir/?api=1&origin=${start.latitude},${start.longitude}&destination=${target.latitude},${target.longitude}&travelmode=$travelMode"
}