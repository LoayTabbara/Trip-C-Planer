package com.codecamp.tripcplaner.view.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codecamp.tripcplaner.viewModel.DetailViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackCard(
    viewModel: DetailViewModel,
    item: String,
    onDelete: @Composable (item: String) -> Unit = {},
    content: @Composable (checked: Boolean) -> Unit
) {
    val checked = remember { mutableStateOf(false) }
    val deleted = remember { mutableStateOf(false) }
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp), elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp), border = BorderStroke(2.dp, color = Color.Gray),
        onClick = { checked.value = !checked.value }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(50.dp) // Adjust the width of the checkbox column
                    .padding(horizontal = 16.dp)
            ) {
                Checkbox(
                    checked = checked.value,
                    onCheckedChange = { checked.value = !checked.value },
                    enabled = true,
                    modifier = Modifier
                        .scale(2f)
                        .padding(start = 10.dp),
                    colors = CheckboxDefaults.colors(Color.Gray)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = item, style = MaterialTheme.typography.bodyMedium)
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .padding(end = 10.dp)
                    .clickable { deleted.value = true }) {

                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.scale(2f)
                )

            }
        }
    }
    if (deleted.value) {
        onDelete(item)
        deleted.value = false
    }
    content(checked.value)

}


@Preview(showBackground = true)
@Composable
fun PackCardsPreview() {

}