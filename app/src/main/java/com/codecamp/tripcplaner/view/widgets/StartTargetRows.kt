package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codecamp.tripcplaner.model.data.Trip
import com.codecamp.tripcplaner.view.GoogleMapsButton
import com.codecamp.tripcplaner.viewModel.DetailViewModel
import java.time.format.DateTimeFormatter

@Composable
fun StartTargetRows(
    thisTrip: Trip,
    viewModel: DetailViewModel
) {
    val formatter = DateTimeFormatter.ofPattern("EE dd.MM.yy HH")
    var showDialog by remember { mutableStateOf(false) }
    val clickedPlace = remember { mutableStateOf("") }
    val firstActivity = remember { mutableStateOf("") }
    val secondActivity = remember { mutableStateOf("") }
    for (i in 1 until thisTrip.cities.size) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Column(modifier = Modifier.fillMaxWidth(0.4f)) {
                TextButton(onClick = {

                    clickedPlace.value = thisTrip.cities.keys.elementAt(i - 1)
                    firstActivity.value =
                        thisTrip.activities.elementAt((2 * i - 2))
                    secondActivity.value =
                        thisTrip.activities.elementAt((2 * i - 1))
                    showDialog = true


                }) {
                    Text(buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            ),
                        ) {
                            append(thisTrip.cities.keys.elementAt(i - 1))
                        }
                        withStyle(style = SpanStyle(color = Color.White, fontSize = 16.sp)) {
                            append(" ⓘ")
                        }
                        withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp)) {
                            append(
                                "\n${
                                    thisTrip.cities.values.elementAt(i - 1).second.format(
                                        formatter
                                    )
                                }h"
                            )
                        }
                    })
                }
            }
            GoogleMapsButton(
                start = thisTrip.cities.values.elementAt(i - 1).first,
                target = thisTrip.cities.values.elementAt(i).first,
                travelMode = viewModel.getTransportMean(),
                context = LocalContext.current
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                    clickedPlace.value = thisTrip.cities.keys.elementAt(i)
                    firstActivity.value = thisTrip.activities.elementAt(2 * i)
                    secondActivity.value = thisTrip.activities.elementAt((2 * i + 1))
                    showDialog = true

                }) {

                    Text(buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            ),
                        ) {
                            append(thisTrip.cities.keys.elementAt(i))
                        }
                        withStyle(style = SpanStyle(color = Color.White, fontSize = 16.sp)) {
                            append(" ⓘ")
                        }
                        withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp)) {
                            append("\n${thisTrip.cities.values.elementAt(i).second.format(formatter)}h")
                        }
                    })
                }
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
    }
    if (showDialog) {
        AlertDialog(

            onDismissRequest = {
                showDialog = false
            },
            {
                Column {
                    Text(
                        text = "Activities at ${clickedPlace.value}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🅐 " + firstActivity.value,
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Left
                    )//for inverted value of the bullet use: Ⓐ
                    Text(
                        text = "\uD83C\uDD51 " + secondActivity.value,
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Left
                    )//for inverted value of the bullet use: Ⓑ
                }

            }

        )
    }
}
