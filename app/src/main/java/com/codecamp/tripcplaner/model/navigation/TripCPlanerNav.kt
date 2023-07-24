package com.codecamp.tripcplaner.model.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun TripCPlanerNav() {
    val navController= rememberNavController()

    NavHost(navController = navController, startDestination = TripCPlanerScreens.SplashScreen.name){
        composable(TripCPlanerScreens.SplashScreen.name){
           // SplashScreen(navController)
        }
        composable(TripCPlanerScreens.MainScreen.name){
            //MainScreen(navController)
        }
    }
}