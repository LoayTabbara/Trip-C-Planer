package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codecamp.tripcplaner.R


@Composable
fun TripCard(tripName:String,tripDescription:String,tripType:String) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp),elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp), border = BorderStroke(2.dp, color = Color.Gray)) {
        Row {

            when(tripType){
                "Bus"-> Image(painter = painterResource(id = R.drawable.bus), contentDescription = "Bus", modifier = Modifier.fillMaxWidth(0.3f), contentScale = ContentScale.Crop)
                "Car"-> Image(painter = painterResource(id = R.drawable.car), contentDescription = "Car", modifier = Modifier.fillMaxWidth(0.3f), contentScale = ContentScale.Crop)
                "Bicycle"-> Image(painter = painterResource(id = R.drawable.bicycle), contentDescription = "Bicycle", modifier = Modifier.fillMaxWidth(0.3f), contentScale = ContentScale.Crop)
                "Walk"-> Image(painter = painterResource(id = R.drawable.walk), contentDescription = "Walk", modifier = Modifier.fillMaxWidth(0.3f), contentScale = ContentScale.Crop)
            }

            Column(modifier= Modifier
                .fillMaxSize()
                .padding(10.dp)) {
                Text(text = tripName, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier =Modifier.height(5.dp))
                Text(text = tripDescription, color = Color.Gray, style = MaterialTheme.typography.bodySmall)

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CPrvw(){
    TripCard(tripName = "Trip Name",tripDescription = "Trip Description",tripType = "Bus")
}