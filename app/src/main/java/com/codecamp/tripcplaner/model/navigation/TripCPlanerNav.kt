package com.codecamp.tripcplaner.model.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.codecamp.tripcplaner.view.DetailsScreen
import com.codecamp.tripcplaner.view.MainScreen
import com.codecamp.tripcplaner.view.MapScreen
import com.codecamp.tripcplaner.view.PackScreen
import com.codecamp.tripcplaner.view.SplashScreen
import com.codecamp.tripcplaner.viewModel.DetailViewModel
import com.codecamp.tripcplaner.viewModel.ThemeViewModel
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel

@Composable
fun TripCPlanerNav(detailsViewModel: DetailViewModel, travelInfoViewModel: TravelInfoViewModel,themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TripCPlanerScreens.SplashScreen.name
    ) {

        composable(TripCPlanerScreens.MainScreen.name,deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "https://pink-trudy-95.tiiny.site/?id={id}"
                        action = Intent.ACTION_VIEW
                    }
                ),
            ) {backStackEntry ->
            MainScreen(navController, travelInfoViewModel, detailsViewModel,themeViewModel,backStackEntry.arguments?.getString("id"))
        }
        composable(TripCPlanerScreens.SplashScreen.name) {
            SplashScreen(navController)
        }
        composable(
            TripCPlanerScreens.MapScreen.name + "/{transportMean}",
            arguments = listOf(navArgument(name = "transportMean") { type = NavType.StringType })
        ) { backStackEntry ->
            travelInfoViewModel.hasResult.value = false
            MapScreen(
                navController,
                backStackEntry.arguments?.getString("transportMean"),

                travelInfoViewModel,
                detailsViewModel
            )
        }
        composable(TripCPlanerScreens.PackScreen.name) {
            PackScreen(navController = navController, detailsViewModel, travelInfoViewModel)
        }
        composable(TripCPlanerScreens.DetailsScreen.name + "/{id}",
            arguments = listOf(navArgument(name = "id") { type = NavType.IntType }
            )) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")!!
            detailsViewModel.clearVM()
            DetailsScreen(
                navController,
                id,
                detailsViewModel,
                travelInfoViewModel
            )

        }
    }
}