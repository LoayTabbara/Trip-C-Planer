package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailCard(text: String,hasReminder:Boolean,isChecked:Boolean, content: @Composable (reminded: Boolean,checkedPressed:Boolean) -> Unit = { _, _ ->}) {
    val reminderPressed = remember { mutableStateOf(false) }
    val checked = remember { mutableStateOf(isChecked) }
    val checkedPressed = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp), border = BorderStroke(2.dp, color = Color.Gray),

        ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {

            Checkbox(
                checked = checked.value,
                onCheckedChange = {
                                 checkedPressed.value=true
                    checked.value = !checked.value
                                  },
                enabled = true,
                modifier = Modifier
                    .scale(2f)
                    .padding(start = 10.dp),
                colors = CheckboxDefaults.colors(Color.Gray)
            )
            Column(modifier = Modifier.fillMaxWidth(0.6f)) {
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Card(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp), elevation = CardDefaults.cardElevation(5.dp),
                shape = RoundedCornerShape(10.dp), border = BorderStroke(2.dp, color = Color.Gray),
                onClick = { reminderPressed.value = !reminderPressed.value }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "reminder",
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Text(
                        text = if(!hasReminder){"Press to set reminder"}else{"Cancel reminder"},
                        modifier = Modifier.padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }


            content(reminderPressed.value,checkedPressed.value)
            if (reminderPressed.value) reminderPressed.value = false
            if (checkedPressed.value) checkedPressed.value = false

        }
    }
}
