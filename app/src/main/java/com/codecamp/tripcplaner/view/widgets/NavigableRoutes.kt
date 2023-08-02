package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
            Column(modifier = Modifier.fillMaxWidth(0.3f)) {
                Text(
                    fontSize = 16.sp,
                    text = thisTrip.cities.keys.elementAt(i - 1),
                    fontWeight = FontWeight.Bold,
                )
                Text(text = "${thisTrip.cities.values.elementAt(i - 1).second.format(formatter)}h",style = MaterialTheme.typography.bodySmall)
            }
            GoogleMapsButton(
                start = thisTrip.cities.values.elementAt(i - 1).first,
                target = thisTrip.cities.values.elementAt(i).first,
                travelMode = viewModel.getTransportMean(),
                context = LocalContext.current
            )
            Column(modifier = Modifier.fillMaxWidth(0.55f)) {
                Text(
                    fontSize = 16.sp,
                    text = thisTrip.cities.keys.elementAt(i),
                    fontWeight = FontWeight.Bold,

                )
                Text(text = "${thisTrip.cities.values.elementAt(i).second.format(formatter)}h",style = MaterialTheme.typography.bodySmall,)
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
