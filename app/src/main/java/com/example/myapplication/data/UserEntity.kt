package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val phone: String,
    val email: String,
    val password: String,
    val age: Int = 0,
    val gender: String = "",
    val height: Int = 0,
    val weight: Int = 0
)