package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.ResourceOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OtherScreen() {
    val coroutineScope = rememberCoroutineScope()
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var timeInSeconds by remember { mutableStateOf(0) }
    var streetName by remember { mutableStateOf("Detecting location...") }

    // 模拟距离（单位 km），真实情况应结合 GPS 数据
    var distance by remember { mutableStateOf(0.0) }

    LaunchedEffect(isRunning, isPaused) {
        while (isRunning && !isPaused) {
            delay(1000)
            timeInSeconds++
            distance += 0.01
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Mapview
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { context -> createMapView(context) }
        )

        // 信息区
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(streetName, style = MaterialTheme.typography.titleLarge)
                Text("${"%.2f".format(distance)} km", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDADA)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Duration", style = MaterialTheme.typography.labelSmall)
                        val minutes = timeInSeconds / 60
                        val seconds = timeInSeconds % 60
                        Text(String.format("%02d:%02d", minutes, seconds), style = MaterialTheme.typography.titleMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Distance", style = MaterialTheme.typography.labelSmall)
                        Text("${"%.2f".format(distance)} km", style = MaterialTheme.typography.titleMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pace", style = MaterialTheme.typography.labelSmall)
                        val pace = if (distance > 0) timeInSeconds / distance else 0.0
                        Text("${"%.1f".format(pace)} sec/km", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    isRunning = true
                    isPaused = false
                }) {
                    Text("Start")
                }
                Button(onClick = {
                    isPaused = true
                }) {
                    Text("Pause")
                }
                Button(onClick = {
                    isRunning = false
                    isPaused = false
                    // TODO: 保存当前记录
                }) {
                    Text("Stop")
                }
                Button(onClick = {
                    isRunning = false
                    isPaused = false
                    timeInSeconds = 0
                    distance = 0.0
                }) {
                    Text("Reset")
                }
            }
        }
    }
}

fun createMapView(context: Context): MapView {
    val accessToken = "pk.pk.eyJ1IjoiYWlkZW5qYW01MzMiLCJhIjoiY205aTZidDdoMDRkaDJpcG4zbXhkOGgyYyJ9.UXbrG_s9aiOV0pkQ2L0VWA"
    val resourceOptions = ResourceOptions.Builder().accessToken(accessToken).build()
    val mapInitOptions = MapInitOptions(context, resourceOptions)
    val mapView = MapView(context, mapInitOptions)
    mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
    return mapView
}
