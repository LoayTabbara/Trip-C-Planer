package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.codecamp.tripcplaner.model.data.Trip
import com.codecamp.tripcplaner.view.GoogleMapsButton
import com.codecamp.tripcplaner.viewModel.DetailViewModel

@Composable
fun NavigableRoutes(
    thisTrip: Trip,
    viewModel: DetailViewModel
) {
    for (i in 1 until thisTrip.cities.size) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                fontSize = 16.sp, text = thisTrip.cities.keys.elementAt(i - 1),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.fillMaxWidth(0.3f)
            )
            GoogleMapsButton(
                start = thisTrip.cities.values.elementAt(i - 1).first,
                target = thisTrip.cities.values.elementAt(i).first,
                travelMode = viewModel.getTransportMean(),
                context = LocalContext.current
            )
            Text(
                fontSize = 16.sp,
                text = thisTrip.cities.keys.elementAt(i),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.fillMaxWidth(0.45f)
            )
        }
    }
}
