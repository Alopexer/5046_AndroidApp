package com.example.myapplication.data

class UserRepository(private val userDao: UserDao) {

    suspend fun register(username: String, phone: String, email: String, password: String): Boolean {
        if (userDao.getUserByUsername(username) != null) return false
        userDao.insertUser(UserEntity(
            username = username,
            phone = phone,
            email = email,
            password = password
        ))
        return true
    }

    suspend fun login(username: String, password: String): Boolean {
        val user = userDao.getUserByUsername(username)
        return user?.password == password
    }

    suspend fun updateUserProfile(user: UserEntity) {
        userDao.updateUser(user)
    }
}