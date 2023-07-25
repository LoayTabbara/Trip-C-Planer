package com.codecamp.tripcplaner.view

import android.app.Activity
import android.view.WindowInsetsController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.codecamp.tripcplaner.R


@Composable
fun SplashScreen (navController: NavController) {

hideStatusBar(LocalContext.current as Activity)
Surface(
    modifier = Modifier
        .fillMaxSize()

) {
    Image(painter = painterResource(id = R.drawable.splash_back) , contentDescription =null, contentScale = ContentScale.FillHeight, modifier = Modifier.fillMaxSize())
Text(text = "asdasd")

}}
fun hideStatusBar(activity: Activity) {
    WindowCompat.setDecorFitsSystemWindows(activity.window, false)
    activity.window.statusBarColor = Color.Transparent.toArgb()
}

