package com.codecamp.tripcplaner.view.widgets

import android.app.Activity
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codecamp.tripcplaner.MAPS_API_KEY
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


@Composable
fun StopPicker(
    tripPickerList: MutableList<MutableState<String>>,
    isStart: Boolean,
    cameraPositionState: CameraPositionState,
    markerState: MarkerState
) {
    val dateIndex = if (isStart) 2 else 3
    val placeIndex = if (isStart) 0 else 1
    val backColorHex = if (isStart) 0xFF006E90 else 0xFF9B392B
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight((if (isStart) 0.4f else 0.8f))
    ) {
        if (!isStart) Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text(
                text = if (isStart) "Start:" else "Dest:",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(10.dp),
                color = Color.White
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = tripPickerList[dateIndex].value.isNotEmpty() || tripPickerList[placeIndex].value.isNotEmpty(),
                onClick = {
                    tripPickerList[dateIndex].value = ""
                    tripPickerList[placeIndex].value = ""
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, contentColor = Color.Red, disabledContainerColor = Color.Transparent
                )
            ) {
                Text(text = "⌫", fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            fun isValidDate(): Boolean {
                return tripPickerList[2].value.isEmpty() || tripPickerList[3].value.isEmpty() || (LocalDate.parse(
                    tripPickerList[2].value,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                ) < LocalDate.parse(
                    tripPickerList[3].value,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                ))
            }

            fun isValidPlace(): Boolean {
                return tripPickerList[0].value.isEmpty() || tripPickerList[1].value.isEmpty() || tripPickerList[0].value != tripPickerList[1].value
            }

            val calendar = Calendar.getInstance()

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
                    tripPickerList[dateIndex].value = "$selectedYear-$monthText-$dayText"
                },
                year,
                month,
                dayOfMonth
            )

            val context = LocalContext.current
            val intentLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) {
                when (it.resultCode) {
                    Activity.RESULT_OK -> {
                        it.data?.let {
                            val place = Autocomplete.getPlaceFromIntent(it)
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(place.latLng!!, 18f)

                            tripPickerList[placeIndex].value = place.name as String

                            markerState.position =
                                LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                        }
                    }
                }
            }


            val launchMapInputOverlay = {
                Places.initialize(context, MAPS_API_KEY)
                val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
                val intent =
                    Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(context)
                intentLauncher.launch(intent)
            }


            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(backColorHex),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.7f),
                shape = RoundedCornerShape(10),
                onClick = launchMapInputOverlay
            ) {
                Text(
                    color = if (isValidPlace()) Color.White else Color.Red,
                    text = tripPickerList[placeIndex].value.ifEmpty {
                        "Location"
                    })
            }
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = Color(backColorHex),
                contentColor = Color.White
            ), shape = RoundedCornerShape(10), modifier = Modifier
                .fillMaxWidth(0.7f),
                onClick = {
                    datePicker.show()
                }) {
                Text(
                    color = if (isValidDate()) Color.White else Color.Red,
                    text = tripPickerList[dateIndex].value.ifEmpty {
                        "Date"
                    })
            }

        }

    }

}
