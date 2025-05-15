package com.example.myapplication.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.RunningPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RunningPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).runningPlanDAO()
    private val repository = RunningPlanRepository(dao)

    fun getPlansByEmail(email: String): Flow<List<RunningPlan>> = repository.getPlansByEmail(email)

    fun insert(plan: RunningPlan) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(plan)
    }

    fun delete(plan: RunningPlan) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(plan)
    }

    fun update(plan: RunningPlan) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(plan) // ➤ 新增
    }
}
