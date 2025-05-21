package com.example.myapplication

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.RunningPlan
import com.example.myapplication.data.RunningPlanViewModel
import com.example.myapplication.data.RunningSummary
import com.example.myapplication.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RunningScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    runningPlanViewModel: RunningPlanViewModel
) {
    val email = userViewModel.currentUser?.email
    val context = LocalContext.current

    val runningPlans = email?.let {
        runningPlanViewModel.getIncompletePlansByEmail(it).collectAsState(initial = emptyList())
    }

    val summary = email?.let {
        runningPlanViewModel.getRunningSummary(it).collectAsState(
            initial = RunningSummary(0.0, 0, 0)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        summary?.value?.let { s ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("\uD83C\uDFC1 Completed Summary", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("\uD83C\uDFC3 Distance: ${"%.2f".format(s.totalDistance)} km")
                    Text("⏱️ Duration: ${s.totalDuration} min")
                    Text("\uD83D\uDD25 Calories: ${s.totalCalories} kcal")
                }
            }
        }

        Button(
            onClick = {
                if (!email.isNullOrBlank()) {
                    val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    val newPlan = RunningPlan(dateTime = now, email = email)
                    runningPlanViewModel.insertAndSetCurrent(newPlan)
                    Log.d("RunScreen", "newplan: $newPlan")
                    navController.navigate("run/map")
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.3f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.DirectionsRun, contentDescription = null, Modifier.padding(end = 8.dp))
            Text("Run!", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Your running plans:", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            runningPlans?.value?.let { plans ->
                items(plans.size) { index ->
                    val plan = plans[index]
                    RunningPlanItemCard(
                        plan = plan,
                        onStart = {
                            runningPlanViewModel.setCurrentRunningPlan(plan)
                            navController.navigate("run/map")
                        },
                        onDelete = { runningPlanViewModel.delete(plan) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("run/run-plan") },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
            Text("New")
        }

        Spacer(modifier = Modifier.height(12.dp))
        AddTestPlanButton(
            userViewModel = userViewModel,
            runningPlanViewModel = runningPlanViewModel,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun RunningPlanItemCard(plan: RunningPlan, onStart: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4C3))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = plan.dateTime, fontSize = 14.sp, color = Color.Gray)
                Text(text = "\uD83C\uDFC6 Distance: ${plan.distance} km", fontSize = 16.sp)
                Text(text = "⏱ Duration: ${plan.duration} min", fontSize = 16.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onStart) {
                    Icon(Icons.Default.DirectionsRun, contentDescription = "Start")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun AddTestPlanButton(
    userViewModel: UserViewModel,
    runningPlanViewModel: RunningPlanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Button(
        onClick = {
            val email = userViewModel.currentUser?.email

            if (!email.isNullOrBlank()) {
                val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

                val distance = 15.0
                val duration = 100
                val calories = 400

                // pace = duration / distance（单位：min/km）
                val paceMinutes = duration.toDouble() / distance
                val paceMin = paceMinutes.toInt()
                val paceSec = ((paceMinutes - paceMin) * 60).toInt()
                val paceFormatted = "${paceMin}'${String.format("%02d", paceSec)}\""

                val testPlan = RunningPlan(
                    dateTime = now,
                    distance = distance,
                    duration = duration,
                    email = email,
                    isCompleted = true,
                    calories = calories,
                    pace = paceFormatted
                )

                runningPlanViewModel.insert(testPlan)

                Toast.makeText(context, "测试计划已添加", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "请先登录以生成测试计划", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = modifier
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Test Plan")
        Text("Add Test Plan")
    }
}