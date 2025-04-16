package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RunningPlanCreateScreen() {
    val context = LocalContext.current
    var distance by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf("") }

    // 保存时间选择结果
    val calendar = remember { Calendar.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create new running plan", style = MaterialTheme.typography.titleLarge)

        Text("Plan start date：$selectedDateTime")

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

                            // 格式化时间显示
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
        Text("Distance goal (km)")
        TextField(
            value = distance,
            onValueChange = { distance = it },
            label = { Text("km") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        Text("Time goal (min)")
        TextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("min") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Button(
            onClick = {
                // TODO：保存逻辑
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Plan")
        }

        Button(
            onClick = {
                // TODO：保存逻辑
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CreatePreview() {
    MyApplicationTheme {
        RunningPlanCreateScreen()
    }
}
