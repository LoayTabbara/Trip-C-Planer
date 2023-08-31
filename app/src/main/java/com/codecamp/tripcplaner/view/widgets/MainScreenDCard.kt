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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MainScreenDCard(travelInfoViewModel: TravelInfoViewModel, navController: NavController) {
    val formatter = DateTimeFormatter.ofPattern("dd")
    val formatterDayName = DateTimeFormatter.ofPattern("EE")
    val formatterMonthName = DateTimeFormatter.ofPattern("MMM")
    val startsMap = mutableMapOf<LocalDateTime, MutableList<Int>>()

    // Grouping trips by start date
    travelInfoViewModel.tripRepo.getAllItems().forEach { trip ->
        if (startsMap.containsKey(trip.startDate))
            startsMap[trip.startDate]!!.add(trip.id)
        else
            startsMap[trip.startDate] = mutableListOf(trip.id)
    }
    startsMap.toSortedMap()

    // Displaying trips date in a lazy row
    LazyRow {
        item {
            startsMap.toSortedMap().forEach { entry ->
                entry.value.forEach { id ->
                    val trip= travelInfoViewModel.tripRepo.getById(id)
                    // Displaying trips date in a lazy row
                    DayCard(
                        day = entry.key.format(formatterDayName).uppercase(Locale.ROOT),
                        date = entry.key.format(formatter),
                        month = entry.key.format(formatterMonthName).uppercase(Locale.ROOT),
                        title = trip.title,
                        onClick = {
                            navController.navigate(TripCPlanerScreens.DetailsScreen.name + "/" + id)
                        }
                    )
                }

            }

        }
    }

}
// Displaying trips date in a card
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayCard(date: String, day: String, onClick: () -> Unit, month: String, title: String) {
    Card(
        modifier = Modifier
            .height(90.dp)
            .width(65.dp)
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, color = currentColor(date)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = day)
            Text(text = date)
            Text(text = month)
            Text(text = title, fontSize = 12.sp, overflow = TextOverflow.Ellipsis)
        }
    }
}

// Changing the color of the card if the date is today
fun currentColor(date: String): Color {
    return if (date == LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd"))) {
        Color.Red
    } else {
        Color.White
    }
}