package com.example.myapplication.data

class UserRepository(private val userDao: UserDao) {

    suspend fun register(username: String, phone: String, email: String, password: String): String? {
        if (userDao.getUserByUsername(username) != null) return "Username already registered"
        if (userDao.getUserByEmail(email) != null) return "Email already registered"

        userDao.insertUser(
            UserEntity(
                username = username,
                phone = phone,
                email = email,
                password = password
            )
        )
        return null // 注册成功
    }

    suspend fun login(username: String, password: String): Boolean {
        val user = userDao.getUserByUsername(username)
        return user?.password == password
    }

    suspend fun updateUserProfile(user: UserEntity) {
        userDao.updateUser(user)
    }
}