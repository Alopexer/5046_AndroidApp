package com.example.myapplication

import com.example.myapplication.data.RunningPlan
import com.example.myapplication.data.RunningPlanDAO
import com.example.myapplication.data.RunningSummary
import kotlinx.coroutines.flow.Flow

class RunningPlanRepository(private val dao: RunningPlanDAO) {

    fun getIncompletePlansByEmail(email: String): Flow<List<RunningPlan>> =
        dao.getIncompletePlansByEmail(email)

    fun getCompletedPlansByEmail(email: String): Flow<List<RunningPlan>> =
        dao.getCompletedPlansByEmail(email)

    suspend fun getCompletedSummaryByEmail(email: String): RunningSummary? =
        dao.getCompletedSummaryByEmail(email)

    suspend fun getLatestPlanByEmail(email: String): RunningPlan? =
        dao.getLatestPlanByEmail(email)

    suspend fun insertAndReturnId(plan: RunningPlan): Long {
        return dao.insert(plan)
    }

    suspend fun insert(plan: RunningPlan) = dao.insert(plan)

    suspend fun delete(plan: RunningPlan) = dao.delete(plan)

    suspend fun update(plan: RunningPlan) = dao.update(plan)
}
