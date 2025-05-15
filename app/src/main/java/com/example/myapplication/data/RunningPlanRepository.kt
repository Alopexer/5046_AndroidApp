package com.example.myapplication

import com.example.myapplication.data.RunningPlan
import com.example.myapplication.data.RunningPlanDAO
import kotlinx.coroutines.flow.Flow

class RunningPlanRepository(private val dao: RunningPlanDAO) {
    fun getPlansByEmail(email: String): Flow<List<RunningPlan>> = dao.getPlansByEmail(email)

    suspend fun insert(plan: RunningPlan) = dao.insert(plan)

    suspend fun delete(plan: RunningPlan) = dao.delete(plan)

    suspend fun update(plan: RunningPlan) = dao.update(plan)
}
