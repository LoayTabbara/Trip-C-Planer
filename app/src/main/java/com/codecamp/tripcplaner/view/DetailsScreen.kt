package com.codecamp.tripcplaner.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.codecamp.tripcplaner.R
import com.codecamp.tripcplaner.view.widgets.DetailCard
import com.codecamp.tripcplaner.viewModel.DetailViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavController, viewModel: DetailViewModel) {
    val paintings = mutableMapOf<String, Int>()
    when (viewModel.getActivity()) {
        "Walk" -> paintings["Walk"] = R.drawable.walk
        "Car" -> paintings["Car"] = R.drawable.car
        "Bus" -> paintings["Bus"] = R.drawable.bus
        "Bicycle" -> paintings["Bicycle"] = R.drawable.bicycle
    }


    val popUpOn = remember { mutableStateOf(false) }
    val newItem = remember { mutableStateOf("") }
    Log.d("DetailsScreen", "DetailsScreen: ${viewModel.getPackList()}")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(if (popUpOn.value) 0.dp else 10.dp)
            .blur(if (popUpOn.value) 20.dp else 0.dp)
            .verticalScroll(enabled = true, state = rememberScrollState())
    ) {

        Image(
            painter = painterResource(id = paintings[viewModel.getActivity()]!!),
            contentDescription = "walking",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (viewModel.getNewTitle() != "") viewModel.getNewTitle() else "Anonymous",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(start = 10.dp)
        )
        for (item in viewModel.getPackList()) {
            Spacer(modifier = Modifier.height(10.dp))

            DetailCard(text = item) { checked ->
                if (checked) {
                    popUpOn.value = true
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

        }
        Spacer(modifier = Modifier.height(10.dp))

    }

    if (popUpOn.value) {
        Popup(
            alignment = Alignment.Center, onDismissRequest = { popUpOn.value = false },
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.3f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Add new item",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                    TextField(
                        value = newItem.value,
                        onValueChange = { newItem.value = it },
                        label = { Text(text = " + Enter new item") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            Color.LightGray
                        ),
                        keyboardActions = KeyboardActions(onDone = {


                            popUpOn.value = false
                        }),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        )
                    )

                }
            }
        }
    }


}

