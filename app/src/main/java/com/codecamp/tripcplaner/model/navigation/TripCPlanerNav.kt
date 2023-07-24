package com.codecamp.tripcplaner.model.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codecamp.tripcplaner.view.MainScreen

@Composable
fun TripCPlanerNav() {
    val navController= rememberNavController()

    NavHost(navController = navController, startDestination = TripCPlanerScreens.MainScreen.name){
        composable(TripCPlanerScreens.SplashScreen.name){
           // SplashScreen(navController)
        }
        composable(TripCPlanerScreens.MainScreen.name){
            MainScreen(navController)
        }
    }
}