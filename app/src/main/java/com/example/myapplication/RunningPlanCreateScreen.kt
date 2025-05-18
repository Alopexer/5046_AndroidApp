package com.example.myapplication

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.RunningPlan
import com.example.myapplication.data.RunningPlanViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RunningPlanCreateScreen(navController: NavController, userViewModel: UserViewModel, runningPlanViewModel: RunningPlanViewModel) {
    val context = LocalContext.current
    var distance by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf("") }
    val userEmail: String = "test@gmail.com"
    val calendar = remember { Calendar.getInstance() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create new running plan", style = MaterialTheme.typography.titleLarge)
        Column (
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Plan start date: $selectedDateTime",style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        // 打开时间选择器
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)

                                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                selectedDateTime = formatter.format(calendar.time)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = "Choose start date",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Choose start date")
            }
        }



        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Distance \n (km)",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.width(110.dp) // 固定宽度避免挤压
            )

            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                placeholder = { Text("Enter Distance") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Duration \n (min)",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.width(110.dp)
            )

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                placeholder = { Text("Enter Duration") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }


        Button(
            onClick = {
                val email = userViewModel.currentUser?.email
                val distanceValue = distance.toDoubleOrNull()
                val durationValue = duration.toIntOrNull()

                if (selectedDateTime.isNotBlank() && distanceValue != null && durationValue != null && !email.isNullOrBlank()) {
                    val newPlan = RunningPlan(
                        dateTime = selectedDateTime,
                        distance = distanceValue,
                        duration = durationValue,
                        email = email,
                        isCompleted = false
                    )
                    runningPlanViewModel.insert(newPlan)
                    navController.popBackStack()
                } else {
                    Toast.makeText(
                        context,
                        "Please enter valid values: '$distance', '$duration', '$selectedDateTime', '$email'",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.Save, contentDescription = "Back")
            Text("Save")
        }


        Button(
            onClick = {
                navController.navigate("run")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            Text("Back")
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun CreatePreview() {
//    val navController = rememberNavController()
//
//    MyApplicationTheme {
//        RunningPlanCreateScreen(navController)
//    }
//}
