package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState

@Composable
fun CustomMarker(
    markerState: MarkerState,
    travelInfoViewModel: TravelInfoViewModel,
    i: Int
) {
    MarkerInfoWindow(
        state = MarkerState(markerState.position)
    ) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary)) {
            val standardMessage =
                travelInfoViewModel.citiesWithActivity.isEmpty() || travelInfoViewModel.citiesWithActivity[travelInfoViewModel.citiesWithActivity.keys.elementAt(
                    i
                )] == null
            val firstPart =
                if (standardMessage)
                    "You can see suggested activities"
                else travelInfoViewModel.citiesWithActivity[travelInfoViewModel.citiesWithActivity.keys.elementAt(
                    i
                )]!![0]

            val secondPart = if (standardMessage)
                "after you have generated a trip"
            else travelInfoViewModel.citiesWithActivity[travelInfoViewModel.citiesWithActivity.keys.elementAt(
                i
            )]!![1]
            Column(
                Modifier.fillMaxWidth(0.7f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "City: ${
                        if (standardMessage) "" else travelInfoViewModel.citiesWithActivity.keys.elementAt(
                            i
                        )
                    }"
                )
                Text(
                    text = firstPart,
                    modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center
                )
                Text(
                    text = secondPart,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    Marker(
        state = markerState,
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
        snippet = "Click to see the activities!",
        onClick = {

            if (it.isInfoWindowShown) {
                markerState.hideInfoWindow()
            } else {
                markerState.showInfoWindow()
            }
            true
        },
    )
}

