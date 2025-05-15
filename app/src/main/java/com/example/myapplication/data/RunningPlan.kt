package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_plan_table")
data class RunningPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val dateTime: String,
    val distance: String,
    val duration: String,
    val email: String,

    val isCompleted: Boolean = false,

    val calories: Int = 0,
    val pace: String = "No Data"
)
