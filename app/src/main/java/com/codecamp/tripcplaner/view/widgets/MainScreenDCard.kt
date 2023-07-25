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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@Preview(showBackground = true)
@Composable
fun MainScreenDCard() {
    val formatter = DateTimeFormatter.ofPattern("dd")
    val weekDate= mutableListOf<String>()
    val weekDay= mutableListOf<String>()


for(i in -1..5 ){
if(i<=0){
    weekDate.add(i+1,LocalDateTime.now().minusDays(abs(i).toLong()).format(formatter))

    weekDay.add(i+1,LocalDateTime.now().minusDays(abs(i).toLong()).dayOfWeek.toString().substring(0,3))
}else{
    weekDate.add(i+1,LocalDateTime.now().plusDays(abs(i).toLong()).format(formatter))
    weekDay.add(i+1,LocalDateTime.now().plusDays(abs(i).toLong()).dayOfWeek.toString().substring(0,3))
}
}


LazyRow(){
    for (i in 0..6){


    item {
        dayCard(date = weekDate[i],day = weekDay[i])
    }

    }
}

}

@Composable
fun dayCard(date:String,day:String){
    Card(modifier = Modifier
        .height(90.dp)
        .width(65.dp).padding(2.dp),elevation = CardDefaults.cardElevation(5.dp),shape = RoundedCornerShape(10.dp), border = BorderStroke(2.dp, color = currentColor(date)))
     {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = day)
            Text(text = date)
        }
    }
}
fun currentColor(date: String):Color{
    return if (date== LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd"))){
        Color.Red
    }else{
        Color.Gray
    }
}