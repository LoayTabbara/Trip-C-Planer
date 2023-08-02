package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.sourceInformationMarkerStart
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MainScreenDCard(travelInfoViewModel: TravelInfoViewModel) {
    val formatter = DateTimeFormatter.ofPattern("dd")
    val formatterDayName = DateTimeFormatter.ofPattern("EE")
    var startsList = mutableListOf<LocalDateTime>()
    travelInfoViewModel.tripRepo.getAllItems().forEach { trip ->
        startsList.add(trip.startDate)
    }
    startsList.sort()

    LazyRow() {
        item {
            startsList.forEach { date ->
                dayCard(
                    day = date.format(formatterDayName).toUpperCase(Locale.ROOT),
                    date = date.format(formatter)
                )
            }

        }
    }

}

@Composable
fun dayCard(date: String, day: String) {
    Card(
        modifier = Modifier
            .height(90.dp)
            .width(65.dp)
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, color = currentColor(date))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = day)
            Text(text = date)
        }
    }
}

fun currentColor(date: String): Color {
    return if (date == LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd"))) {
        Color.Red
    } else {
        Color.Gray
    }
}