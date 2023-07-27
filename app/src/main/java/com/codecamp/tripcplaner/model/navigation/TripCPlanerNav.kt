package com.codecamp.tripcplaner.model.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codecamp.tripcplaner.view.DetailsScreen
import com.codecamp.tripcplaner.view.MainScreen
import com.codecamp.tripcplaner.view.MapScreen
import com.codecamp.tripcplaner.view.PackScreen
import com.codecamp.tripcplaner.view.SplashScreen
import com.codecamp.tripcplaner.viewModel.DetailViewModel
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel

@Composable
fun TripCPlanerNav(viewModel: DetailViewModel, travelInfoViewModel: TravelInfoViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TripCPlanerScreens.MainScreen.name
    ) {
        composable(TripCPlanerScreens.MainScreen.name) {
            MainScreen(navController)
        }
        composable(TripCPlanerScreens.SplashScreen.name) {
            SplashScreen(navController)
        }
        composable(TripCPlanerScreens.MapScreen.name+"/{typeActivity}",
                arguments = listOf(navArgument(name="typeActivity"){type= NavType.StringType})
        ) {backStackEntry->
            travelInfoViewModel.hasResult.value = false
            MapScreen(navController,backStackEntry.arguments?.getString("typeActivity"), travelInfoViewModel)
        }
        composable(TripCPlanerScreens.PackScreen.name) {
            PackScreen(navController = navController, viewModel)
        }
        composable(TripCPlanerScreens.DetailsScreen.name) {
            DetailsScreen(navController = navController, viewModel)
        }

    }
}