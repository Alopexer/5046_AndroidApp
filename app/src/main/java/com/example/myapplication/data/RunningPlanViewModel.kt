package com.example.myapplication.data

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.RunningPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RunningPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).runningPlanDAO()
    private val repository = RunningPlanRepository(dao)

    private val _currentRunningPlan = mutableStateOf<RunningPlan?>(null)
    val currentRunningPlan: State<RunningPlan?> = _currentRunningPlan

    fun setCurrentRunningPlan(plan: RunningPlan?) {
        _currentRunningPlan.value = plan
    }


    fun getIncompletePlansByEmail(email: String): Flow<List<RunningPlan>> =
        repository.getIncompletePlansByEmail(email)

    fun getCompletedPlansByEmail(email: String): Flow<List<RunningPlan>> =
        repository.getCompletedPlansByEmail(email)

    fun getCompletedSummaryByEmail(
        email: String,
        onResult: (RunningSummary?) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val summary = repository.getCompletedSummaryByEmail(email)
        onResult(summary)
    }

    fun getLatestPlanByEmail(
        email: String,
        onResult: (RunningPlan?) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val latestPlan = repository.getLatestPlanByEmail(email)
        onResult(latestPlan)
    }

    fun insertAndSetCurrent(plan: RunningPlan) = viewModelScope.launch(Dispatchers.IO) {
        val generatedId = repository.insertAndReturnId(plan)
        val completePlan = plan.copy(id = generatedId.toInt())
        _currentRunningPlan.value = completePlan
    }

    fun getRunningSummary(email: String): Flow<RunningSummary> {
        return repository.getCompletedPlansByEmail(email).map { plans ->
            val totalDistance = plans.sumOf { it.distance }
            val totalDuration = plans.sumOf { it.duration }
            val totalCalories = plans.sumOf { it.calories }
            RunningSummary(totalDistance, totalDuration, totalCalories)
        }
    }

    fun insert(plan: RunningPlan) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(plan)
    }

    fun delete(plan: RunningPlan) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(plan)
    }

    fun update(plan: RunningPlan) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(plan)
    }
}
