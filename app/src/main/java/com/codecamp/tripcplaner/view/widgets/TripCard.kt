package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codecamp.tripcplaner.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCard(
    tripName: String,
    tripDescription: String,
    tripType: String,
    onShareClicked: () -> Unit,
    onClicked: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {

            Button(
                onClick = onShareClicked, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, contentColor = Color.White
                ), modifier = Modifier
            ) {
                Text(text = "\u27A6", fontSize = 24.sp, textAlign = TextAlign.Right)
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .height(192.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(it),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(3.dp),
            border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.secondary),
            onClick = onClicked,
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
        ) {
            Row {

                when (tripType) {
                    "transit" -> Image(
                        painter = painterResource(id = R.drawable.transit),
                        contentDescription = "transit",
                        modifier = Modifier.fillMaxWidth(0.3f).padding(5.dp),
                        contentScale = ContentScale.Crop
                    )

                    "driving" -> Image(
                        painter = painterResource(id = R.drawable.driving),
                        contentDescription = "driving",
                        modifier = Modifier.fillMaxWidth(0.3f),
                        contentScale = ContentScale.Crop
                    )

                    "bicycling" -> Image(
                        painter = painterResource(id = R.drawable.bicycling),
                        contentDescription = "bicycling",
                        modifier = Modifier.fillMaxWidth(0.3f),
                        contentScale = ContentScale.Crop
                    )

                    "walking" -> Image(
                        painter = painterResource(id = R.drawable.walking),
                        contentDescription = "walking",
                        modifier = Modifier.fillMaxWidth(0.3f),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    Text(text = tripName, style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = tripDescription,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodySmall
                    )

                }
            }
        }

    }
    Spacer(modifier = Modifier.height(10.dp))

}

