package com.codecamp.tripcplaner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.codecamp.tripcplaner.model.navigation.TripCPlanerNav
import com.codecamp.tripcplaner.ui.theme.TripCPlanerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripCPlanerTheme {
                TripCPlanerNav()
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TripCPlanerTheme {

    }
}