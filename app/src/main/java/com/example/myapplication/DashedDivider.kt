package com.example.myapplication
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DashedDivider(
    color: Color = Color.Gray,
    thickness: Dp = 1.dp,
    dashWidth: Dp = 8.dp,
    gapWidth: Dp = 4.dp,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Canvas(
        modifier = modifier
            .height(thickness)
    ) {
        val lineWidth = size.width
        val dash = dashWidth.toPx()
        val gap = gapWidth.toPx()
        var startX = 0f

        while (startX < lineWidth) {
            drawLine(
                color = color,
                start = Offset(x = startX, y = 0f),
                end = Offset(x = startX + dash, y = 0f),
                strokeWidth = thickness.toPx()
            )
            startX += dash + gap
        }
    }
}
