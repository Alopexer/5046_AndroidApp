package com.example.myapplication.data

class UserRepository(private val userDao: UserDao) {
    private var loggedInUser: UserEntity? = null

    suspend fun login(email: String, password: String): UserEntity? {
        val user = userDao.login(email, password)
        loggedInUser = user
        return user
    }

    fun isLoggedIn(): Boolean = loggedInUser != null

    fun logout() {
        loggedInUser = null
    }

    suspend fun getUserByUsername(username: String): UserEntity? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun clearAllUsers() {
        userDao.deleteAllUsers()
    }
}