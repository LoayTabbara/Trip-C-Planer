package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel

@Composable
fun TravelSegmentRow(
    it: String,
    travelInfoViewModel: TravelInfoViewModel,
    i: Int,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            fontSize = 16.sp, text = it,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth(0.3f),
            color = MaterialTheme.colorScheme.inversePrimary
        )
        Text(
            text = " ➱",
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth(0.16f),
            color = MaterialTheme.colorScheme.inversePrimary
        )
        Text(
            fontSize = 16.sp,
            text = travelInfoViewModel.citiesWithActivity.keys.elementAt(i),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth(0.45f),
            color = MaterialTheme.colorScheme.inversePrimary
        )
    }
}