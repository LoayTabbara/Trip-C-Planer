package com.codecamp.tripcplaner.view

import android.app.DatePickerDialog
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.codecamp.tripcplaner.R
import com.codecamp.tripcplaner.model.reminder.scheduleNotification
import com.codecamp.tripcplaner.view.widgets.DetailCard
import com.codecamp.tripcplaner.viewModel.DetailViewModel
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import java.time.LocalDateTime
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavController, viewModel: DetailViewModel, travelInfoViewModel: TravelInfoViewModel) {
    val paintings = mutableMapOf<String, Int>()
    when (viewModel.getTransportMean()) {
        "Walk" -> paintings["Walk"] = R.drawable.walk
        "Car" -> paintings["Car"] = R.drawable.car
        "Bus" -> paintings["Bus"] = R.drawable.bus
        "Bicycle" -> paintings["Bicycle"] = R.drawable.bicycle
    }
    var selectedItem by remember { mutableStateOf("") }

    // State to control the visibility of the dropdown menu
    var isDropdownMenuVisible by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()
    val date=remember{mutableStateOf("")}
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
            date.value = "$dayText.$monthText.${selectedYear % 2000}"
        },
        year,
        month,
        dayOfMonth
    )


    val popUpOn = remember { mutableStateOf(false) }
    val confirmed = remember { mutableStateOf(false) }
    Log.d("DetailsScreen", "DetailsScreen: ${viewModel.getPackList()}")
    val id = remember { mutableIntStateOf(0) }

    Log.d("DetailsScreen", "DetailsScreen: ${viewModel.getPackList()}")
    Column( modifier = Modifier
        .fillMaxWidth()
        .padding(if (popUpOn.value) 0.dp else 10.dp)
        .blur(if (popUpOn.value) 20.dp else 0.dp)
        .verticalScroll(enabled = true, state = rememberScrollState())) {



        Image(
            painter = painterResource(id = paintings[viewModel.getTransportMean()]!!),
            contentDescription = "walking",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (viewModel.getNewTitle() != "") viewModel.getNewTitle() else "Anonymous",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(start = 10.dp)
        )
        for (item in viewModel.getPackList()) {
            Spacer(modifier = Modifier.height(10.dp))

            DetailCard(text = item) { checked ->
                if (checked) {
                    popUpOn.value = true
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

        }
        Spacer(modifier = Modifier.height(10.dp))

    }

    if (popUpOn.value) {
        Popup(
            alignment = Alignment.Center, onDismissRequest = { popUpOn.value = false },
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
                    verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Add Time",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(10.dp))
                    Button(onClick = { datePicker.show() }, modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text(text =if (date.value=="") "Select Date" else date.value)
                    }
                    Button(
                        onClick = { isDropdownMenuVisible = true },
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    ) {
                        Text(if (selectedItem == "") "Select City" else selectedItem)
                    }

                    DropdownMenu(expanded = isDropdownMenuVisible , onDismissRequest = { isDropdownMenuVisible = false }) {
                        travelInfoViewModel.citiesWithActivity.keys.forEach { city ->
                            DropdownMenuItem(text = { Text(text =city) }, onClick = { selectedItem = city
                                isDropdownMenuVisible = false })
                        }
                    }
                    Button(onClick = {
                        Log.d("DetailsScreen", "DetailsScreen: ${date} and ${selectedItem}")
                        confirmed.value=true
                        popUpOn.value = false }) {
                        Text(text = "Confirm")
                    }



                }
            }
        }
    }
    var notificationScheduled by remember { mutableStateOf(false) }
    if (confirmed.value){

        scheduleNotification(LocalContext.current, 2, 7000, "button1"
        )
        notificationScheduled = true
        confirmed.value=false

    }

}