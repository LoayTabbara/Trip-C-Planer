package com.codecamp.tripcplaner.view

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codecamp.tripcplaner.R
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import kotlinx.coroutines.delay

// Splash Screen
@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        // Animatable is a composable function that can be used to animate a value
        Animatable(0f)
    }
    // Scale animation for logo image in splash screen using Animatable composable function and animateTo function of Animatable
    LaunchedEffect(key1 = true, block = {

        scale.animateTo(targetValue = 0.9f,
            //tween is used to define the animation behavior
            animationSpec = tween(
                // durationMillis is the duration of the animation in milliseconds
                durationMillis = 800,
                easing = {
                    // OvershootInterpolator is used to animate the value in a way that it overshoots the target value and then comes back to it
                    OvershootInterpolator(8f)
                        .getInterpolation(it)
                })
        )

    })



    Surface(
        modifier = Modifier
            .fillMaxSize()

    ) {
        // Image composable is used to load the image from drawable folder
        Image(
            painter = painterResource(id = R.drawable.splash_back),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to The C Plan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(.12f)
            )
            // TypewriterText composable is used to display the text in typewriter animation
            TypewriterText(
                texts = listOf(
                    "Travel Smarter!\n\n not harder!\n\n - by The C Team -",
                ), navController = navController
            )
            Column(modifier= Modifier
                .fillMaxSize()
                .padding(24.dp), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(id = R.drawable.tripc_icon_black),
                    contentDescription ="logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(128.dp)
                        .scale(scale.value), alignment = Alignment.Center
                )
                TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                    navController.navigate(TripCPlanerScreens.MainScreen.name){
                        popUpTo(0)
                    }

                }, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary)) {

                    Text("Skip ->", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }


        }

    }

}


@Composable
fun TypewriterText(
    texts: List<String>,
    navController: NavController,
) {
    var textIndex by remember {
        mutableIntStateOf(0)
    }
    var textToDisplay by remember {
        mutableStateOf("")
    }

    LaunchedEffect(
        key1 = texts,
    ) {
        for (text in texts) {
            text.forEachIndexed { charIndex, _ ->
                textToDisplay = text.substring(
                    startIndex = 0,
                    endIndex = charIndex + 1,
                )
                // delay for each character
                delay(92)
            }
            // delay for each text
            delay(512)
        }
        navController.navigate(TripCPlanerScreens.MainScreen.name){
            popUpTo(0)
        }
    }
// Text composable is used to display the text and the style can be changed here
    Text(
        text = textToDisplay,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color.DarkGray,
        modifier = Modifier.fillMaxWidth().fillMaxHeight(.20f)
    )
}
