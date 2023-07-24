package com.codecamp.tripCPlaner.model.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun CodeCampZNav() {
    val navController= rememberNavController()

    NavHost(navController = navController, startDestination = CodeCampZScreens.SplashScreen.name){
        composable(CodeCampZScreens.SplashScreen.name){
           // SplashScreen(navController)
        }
        composable(CodeCampZScreens.MainScreen.name){
            //MainScreen(navController)
        }
    }
}