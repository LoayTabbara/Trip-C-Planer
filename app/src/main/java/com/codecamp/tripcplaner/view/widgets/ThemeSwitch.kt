package com.codecamp.tripcplaner.view.widgets

import android.graphics.Paint.Style
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.codecamp.tripcplaner.viewModel.ThemeViewModel

@Composable
fun ThemeSwitch(scale: Float = 2f,
                width: Dp = 36.dp,
                height: Dp = 20.dp,
                strokeWidth: Dp = 2.dp,
                checkedTrackColor: Color = Color(0xFF35898F),
                uncheckedTrackColor: Color = Color(0xFFe0e0e0),
                gapBetweenThumbAndTrackEdge: Dp = 4.dp,themeViewModel: ThemeViewModel) {

    val switchON = remember {
        mutableStateOf(themeViewModel.getIsDark()) // Initially the switch is off
    }

    val thumbRadius = (height / 2) - gapBetweenThumbAndTrackEdge

    // To move thumb, we need to calculate the position (along x axis)
    val animatePosition = animateFloatAsState(
        targetValue = if (switchON.value)
            with(LocalDensity.current) { (width - thumbRadius - gapBetweenThumbAndTrackEdge).toPx() }
        else
            with(LocalDensity.current) { (thumbRadius + gapBetweenThumbAndTrackEdge).toPx() },
        label = "switchAnime"
    )
Row {
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .size(width = width, height = height)
            .scale(scale = scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // This is called when the user taps on the canvas
                        themeViewModel.setIsDark(!switchON.value)
                        switchON.value=!switchON.value
                    }
                )
            }
    ) {
        // Track
        drawRoundRect(
            color = if (switchON.value) checkedTrackColor else uncheckedTrackColor,
            cornerRadius = CornerRadius(x = 10.dp.toPx(), y = 10.dp.toPx()),
            style = Stroke(width = strokeWidth.toPx())
        )

        // Thumb
        drawCircle(
            color = if (switchON.value) checkedTrackColor else uncheckedTrackColor,
            radius = thumbRadius.toPx(),
            center = Offset(
                x = animatePosition.value,
                y = size.height / 2
            )
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Text(modifier = Modifier.padding(start = 8.dp),
        color = Color.White, text = if (switchON.value) "Dark Mode ON" else "Dark Mode OFF")
}




}