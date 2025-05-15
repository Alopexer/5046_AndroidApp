package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RunningPlanDAO {
    @Query("SELECT * FROM running_plan_table WHERE email = :email ORDER BY id DESC")
    fun getPlansByEmail(email: String): Flow<List<RunningPlan>>

    @Query("SELECT * FROM running_plan_table WHERE email = :email ORDER BY dateTime DESC LIMIT 1")
    suspend fun getLatestPlanByEmail(email: String): RunningPlan?


    @Insert
    suspend fun insert(plan: RunningPlan)

    @Delete
    suspend fun delete(plan: RunningPlan)

    @Update
    suspend fun update(plan: RunningPlan)
}