package com.codecamp.tripcplaner.view

import android.app.Activity
import android.view.WindowInsetsController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.codecamp.tripcplaner.R
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import kotlinx.coroutines.delay


@Composable
 fun SplashScreen (navController: NavController) {

hideStatusBar(LocalContext.current as Activity)
    LaunchedEffect(key1 = true, block ={
        navigate(navController)
    } )
Surface(
    modifier = Modifier
        .fillMaxSize()

) {
    Image(painter = painterResource(id = R.drawable.splash_back) , contentDescription =null, contentScale = ContentScale.FillHeight, modifier = Modifier.fillMaxSize())
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(40.dp), horizontalAlignment= Alignment.CenterHorizontally) {
        Text(text = "Welcome to TripCPlaner", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, style = MaterialTheme.typography.displayLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 30.dp))
        TypewriterText(
            texts = listOf(
                " LIVE YOUR LIFE BY A COMPASS,",
                "NOT A CLOCK......",
                "– ERICA JONG"
            ),
        )

        
    }

}

}
suspend fun navigate(navController: NavController){
    delay(7000L)
    navController.navigate(TripCPlanerScreens.MainScreen.name)
}
fun hideStatusBar(activity: Activity) {
    WindowCompat.setDecorFitsSystemWindows(activity.window, false)
    activity.window.statusBarColor = Color.Transparent.toArgb()
}
@Composable
fun TypewriterText(
    texts: List<String>,
) {
    var textIndex by remember {
        mutableStateOf(0)
    }
    var textToDisplay by remember {
        mutableStateOf("")
    }

    LaunchedEffect(
        key1 = texts,
    ) {
        while (textIndex < texts.size) {
            texts[textIndex].forEachIndexed { charIndex, _ ->
                textToDisplay = texts[textIndex]
                    .substring(
                        startIndex = 0,
                        endIndex = charIndex + 1,
                    )
                delay(100)
            }
            textIndex = (textIndex + 1) % texts.size
            delay(300)
        }
    }

    Text(
        text = textToDisplay,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray,
    )
}

