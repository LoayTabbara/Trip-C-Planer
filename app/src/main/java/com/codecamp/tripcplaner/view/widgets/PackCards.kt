package com.codecamp.tripcplaner.view.widgets

import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun PackCards(item:String,content : @Composable (checked:Boolean)-> Unit) {
    val checked= remember{ mutableStateOf(false)}
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp),elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp), border = BorderStroke(2.dp, color = Color.Gray)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,modifier=Modifier.fillMaxSize()) {
            Column(modifier= Modifier
                .fillMaxWidth(0.2f)
                .padding(start = 25.dp, end = 15.dp)) {
                Checkbox(checked = checked.value, onCheckedChange = { checked.value=!checked.value}, enabled = true, modifier = Modifier.scale(2f), colors = CheckboxDefaults.colors(Color.Gray))
            }
            Column(modifier= Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)) {
                Text(text = item, style= MaterialTheme.typography.displaySmall)

            }
            }
        }
    content(checked.value)
    }



@Preview(showBackground = true)
@Composable
fun PackCardsPreview() {
    PackCards("bag"){}
}