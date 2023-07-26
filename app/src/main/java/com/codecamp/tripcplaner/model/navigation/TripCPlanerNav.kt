package com.codecamp.tripcplaner.model.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codecamp.tripcplaner.view.DetailsScreen
import com.codecamp.tripcplaner.view.MainScreen
import com.codecamp.tripcplaner.view.MapScreen
import com.codecamp.tripcplaner.view.PackScreen
import com.codecamp.tripcplaner.view.SplashScreen

@Composable
fun TripCPlanerNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TripCPlanerScreens.PackScreen.name
    ) {
        composable(TripCPlanerScreens.MainScreen.name) {
            MainScreen(navController)
        }
        composable(TripCPlanerScreens.SplashScreen.name) {
            SplashScreen(navController)
        }
        composable(TripCPlanerScreens.MapScreen.name) {
            MapScreen(navController)
        }
        composable(TripCPlanerScreens.PackScreen.name) {
            PackScreen(navController =navController)
        }
        composable(TripCPlanerScreens.DetailsScreen.name) {
            DetailsScreen(navController =navController)
        }

    }
}