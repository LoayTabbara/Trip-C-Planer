package com.codecamp.tripcplaner.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.codecamp.tripcplaner.R


@Composable
fun SplashScreen(navController: NavController) {

    Surface(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_back),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize()
        )

    }
}



