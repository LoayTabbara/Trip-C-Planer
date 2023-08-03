package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codecamp.tripcplaner.model.data.Trip
import com.codecamp.tripcplaner.view.GoogleMapsButton
import com.codecamp.tripcplaner.viewModel.DetailViewModel
import java.time.format.DateTimeFormatter

@Composable
fun NavigableRoutes(
    thisTrip: Trip,
    viewModel: DetailViewModel
) {
    val formatter = DateTimeFormatter.ofPattern("EE dd.MM.yy HH")
    for (i in 1 until thisTrip.cities.size) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Column(modifier = Modifier.fillMaxWidth(0.4f)) {
                TextButton(onClick = { /*TODO Show corresponding activities*/ }) {
                    Text(buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            ),
                        ) {
                            append(thisTrip.cities.keys.elementAt(i - 1))
                        }
                        withStyle(style = SpanStyle(color = Color.Blue, fontSize = 16.sp)) {
                            append(" ⓘ")
                        }
                        withStyle(style = SpanStyle(color = Color.Black, fontSize = 12.sp)) {
                            append("\n${thisTrip.cities.values.elementAt(i - 1).second.format(formatter)}h")
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
                TextButton(modifier = Modifier.fillMaxWidth(),onClick = { /*TODO Show corresponding activities*/ }) {

                    Text(buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            ),
                        ) {
                            append(thisTrip.cities.keys.elementAt(i))
                        }
                        withStyle(style = SpanStyle(color = Color.Blue, fontSize = 16.sp)) {
                            append(" ⓘ")
                        }
                        withStyle(style = SpanStyle(color = Color.Black, fontSize = 12.sp)) {
                            append("\n${thisTrip.cities.values.elementAt(i).second.format(formatter)}h")
                        }
                    })
                }
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
