package com.example.myapplication.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.UserEntity
import com.example.myapplication.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    var currentUser by mutableStateOf<UserEntity?>(null)
        private set

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun login(email: String, password: String, onResult: (UserEntity?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.login(email, password)
            withContext(Dispatchers.Main) {
                currentUser = user
                onResult(user)
            }
        }
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()

    fun logout(onComplete: () -> Unit = {}) {
        repository.logout()
        currentUser = null
        onComplete()
    }

    fun getUserByUsername(username: String, onResult: (UserEntity?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserByUsername(username)
            onResult(user)
        }
    }

    fun getUserByEmail(email: String, onResult: (UserEntity?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserByEmail(email)
            onResult(user)
        }
    }

    fun register(user: UserEntity, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.getUserByEmail(user.email)
            if (existing == null) {
                repository.insertUser(user)
                withContext(Dispatchers.Main) {
                    onComplete(true)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }

    fun updateUser(user: UserEntity, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateUser(user)
                withContext(Dispatchers.Main) {
                    currentUser = user
                    onComplete(true)
                }
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun loginOrRegisterByGoogle(user: UserEntity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.getUserByEmail(user.email)
            val finalUser = existing ?: run {
                repository.insertUser(user)
                user
            }
            currentUser = finalUser
            withContext(Dispatchers.Main) {
                onResult(true)
            }
        }
    }

    fun clearLocalUsers(onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllUsers() // 你需确保 repository 有这个方法
            currentUser = null
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }


}
