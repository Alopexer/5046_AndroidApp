package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.myapplication.data.RunningPlan
import com.example.myapplication.data.RunningPlanViewModel
import com.example.myapplication.viewmodel.UserViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.*

@Composable
fun MapScreen(
    userViewModel: UserViewModel,
    runningPlanViewModel: RunningPlanViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cr = runningPlanViewModel.currentRunningPlan.value
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            Toast.makeText(
                context,
                if (isGranted) "Location granted" else "Location denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    )

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).build()
    }

    var elapsedSeconds by remember { mutableStateOf(0) }
    var distance by remember { mutableStateOf(0.0) }
    var currentAddress by remember { mutableStateOf("Detecting location...") }
    val routePoints = remember { mutableStateListOf<LatLng>() }
    val mapView = remember { MapView(context) }
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }

    val selectedPlan = runningPlanViewModel.currentRunningPlan.value
    val targetDistance = selectedPlan?.distance ?: 0.0
    val targetTime = selectedPlan?.duration?.times(60) ?: 0

//    LaunchedEffect(Unit) {
//        userViewModel.currentUser?.email?.let { email ->
//            runningPlanViewModel.getLatestPlanByEmail(email) { latest ->
//                if (latest != null && !latest.isCompleted) {
//                    selectedPlan = latest
//                    targetDistance = latest.distance
//                    targetTime = latest.duration * 60
//                }
//            }
//        }
//    }

    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var polyline by remember { mutableStateOf<Polyline?>(null) }

    DisposableEffect(Unit) {
        mapView.onCreate(null)
        mapView.onStart()
        mapView.onResume()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                val latLng = LatLng(location.latitude, location.longitude)
                if (isRunning && !isPaused) {
                    if (routePoints.isNotEmpty()) {
                        val last = routePoints.last()
                        val results = FloatArray(1)
                        Location.distanceBetween(
                            last.latitude,
                            last.longitude,
                            latLng.latitude,
                            latLng.longitude,
                            results
                        )
                        distance += results[0] / 1000 // km
                    }
                    routePoints.add(latLng)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    polyline?.remove()
                    polyline = googleMap?.addPolyline(
                        PolylineOptions().addAll(routePoints).color(0xFF2196F3.toInt()).width(8f)
                    )
                }

                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        currentAddress = addresses[0].getAddressLine(0) ?: "Unknown location"
                    }
                } catch (e: Exception) {
                    currentAddress = "Location unavailable"
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    LaunchedEffect(isRunning, isPaused) {
        while (isRunning && !isPaused) {
            delay(1000)
            elapsedSeconds++
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                mapView.getMapAsync { map ->
                    googleMap = map
                    map.mapType = GoogleMap.MAP_TYPE_NORMAL
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        map.isMyLocationEnabled = true
                    }
                }
                mapView
            },
            modifier = Modifier.weight(1f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(currentAddress, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${"%.2f".format(distance)} km", fontSize = 20.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFEBEE), shape = RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Duration", fontSize = 12.sp); Text(
                    String.format(
                        "%02d:%02d",
                        elapsedSeconds / 60,
                        elapsedSeconds % 60
                    )
                )
                }
                Column { Text("Distance", fontSize = 12.sp); Text("${"%.2f".format(distance)} km") }
                val pace = if (distance > 0) elapsedSeconds / distance else 0.0
                Column { Text("Pace", fontSize = 12.sp); Text("${"%.1f".format(pace)} sec/km") }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    isRunning = true
                    isPaused = false
                    Log.d("MapScreen","Current: $cr")
                }) { Text("Start") }
                Button(onClick = { isPaused = true }) { Text("Pause") }
                Button(onClick = {
                    isRunning = false
                    isPaused = false

                    val totalMinutes = elapsedSeconds / 60
                    val paceMinutes = if (distance > 0) elapsedSeconds / 60.0 / distance else 0.0
                    val paceMin = paceMinutes.toInt()
                    val paceSec = ((paceMinutes - paceMin) * 60).toInt()
                    val paceStr = "${paceMin}'${String.format("%02d", paceSec)}\"/km"
                    val calories = (distance * 60).toInt()
                    val routeString =
                        routePoints.joinToString("|") { "${it.latitude},${it.longitude}" }

                    selectedPlan?.let {
                        val updated = it.copy(
                            isCompleted = true,
                            pace = paceStr,
                            calories = calories,
                            duration = totalMinutes,
                            distance = distance,
                            route = routeString
                        )

                        // ✅ 添加调试日志
                        Log.d("MapScreen", "Before update: $it")
                        Log.d("MapScreen", "Updated plan: $updated")
                        runningPlanViewModel.update(updated)
                        // Clear current plan
                        runningPlanViewModel.setCurrentRunningPlan(null)
                        // Toast to remind user
                        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                })
                { Text("End") }

                Button(onClick = {
                    isRunning = false
                    isPaused = false
                    elapsedSeconds = 0
                    distance = 0.0
                    routePoints.clear()
                    polyline?.remove()
                }) { Text("Reset") }
            }
        }
    }
}
