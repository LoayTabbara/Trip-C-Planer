package com.codecamp.tripcplaner

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.codecamp.tripcplaner.model.navigation.TripCPlanerNav
import com.codecamp.tripcplaner.ui.theme.TripCPlanerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        hideStatusBar(this)
        setContent {


            TripCPlanerTheme {
                TripCPlanerNav()
            }

        }

    }
}

fun hideStatusBar(activity: Activity) {
    WindowCompat.setDecorFitsSystemWindows(activity.window, false)
    activity.window.statusBarColor = Color.Transparent.toArgb()
    WindowCompat.setDecorFitsSystemWindows((activity).window, false)
    activity.window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TripCPlanerTheme {

    }
}
