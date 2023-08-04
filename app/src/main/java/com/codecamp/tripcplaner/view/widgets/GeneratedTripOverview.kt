package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel


@Composable
fun GeneratedTripOverview(
    transportMean: String?,
    travelInfoViewModel: TravelInfoViewModel
) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .verticalScroll(enabled = true, state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Generated Trip\n(Means of travel: $transportMean)",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
            fontSize = 24.sp
        )

        for (i in 1 until travelInfoViewModel.citiesWithActivity.keys.size) {
            TravelSegmentRow(
                travelInfoViewModel.citiesWithActivity.keys.elementAt(i - 1),
                travelInfoViewModel, i
            )
        }
        Text(
            fontSize = 20.sp,
            text = "\nSuggested Packing List(editable later)",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = travelInfoViewModel.packingList.toString()
                .substring(1, travelInfoViewModel.packingList.toString().length - 1),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Thin,
        )
    }
}
