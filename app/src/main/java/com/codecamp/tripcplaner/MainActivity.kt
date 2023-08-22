package com.codecamp.tripcplaner

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import com.codecamp.tripcplaner.model.data.Trip
import com.codecamp.tripcplaner.model.navigation.TripCPlanerNav
import com.codecamp.tripcplaner.ui.theme.TripCPlanerTheme
import com.codecamp.tripcplaner.viewModel.DetailViewModel
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tripsStateFlow = MutableStateFlow<List<Trip>>(emptyList())

//        hideStatusBar(this)
        setContent {
            val travelInfoViewModel: TravelInfoViewModel = hiltViewModel()
            travelInfoViewModel.intentSharedCodeUsed.value = false
            val viewModel: DetailViewModel = hiltViewModel()
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(key1 = lifecycleOwner, effect = {

                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START) {
                        travelInfoViewModel.viewModelScope.launch {
                            tripsStateFlow.emit(travelInfoViewModel.tripRepo.getAllItems())
                            travelInfoViewModel.savedTrips =
                                travelInfoViewModel.tripRepo.populateTrips(tripsStateFlow)
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            })

            TripCPlanerTheme {

                TripCPlanerNav(viewModel, travelInfoViewModel)
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