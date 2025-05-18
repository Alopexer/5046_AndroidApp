package com.example.myapplication

import com.example.myapplication.data.RunningPlan
import com.example.myapplication.data.RunningPlanDAO
import com.example.myapplication.data.RunningSummary
import kotlinx.coroutines.flow.Flow

class RunningPlanRepository(private val dao: RunningPlanDAO) {

    // ✅ 获取未完成计划
    fun getIncompletePlansByEmail(email: String): Flow<List<RunningPlan>> =
        dao.getIncompletePlansByEmail(email)

    // ✅ 获取已完成计划
    fun getCompletedPlansByEmail(email: String): Flow<List<RunningPlan>> =
        dao.getCompletedPlansByEmail(email)

    // ✅ 获取已完成计划的汇总数据
    suspend fun getCompletedSummaryByEmail(email: String): RunningSummary? =
        dao.getCompletedSummaryByEmail(email)

    // ✅ 获取最新计划
    suspend fun getLatestPlanByEmail(email: String): RunningPlan? =
        dao.getLatestPlanByEmail(email)

    // ✅ 插入计划
    suspend fun insert(plan: RunningPlan) = dao.insert(plan)

    // ✅ 删除计划
    suspend fun delete(plan: RunningPlan) = dao.delete(plan)

    // ✅ 更新计划
    suspend fun update(plan: RunningPlan) = dao.update(plan)
}
