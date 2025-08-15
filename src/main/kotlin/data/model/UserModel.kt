package com.example.data.model

import io.ktor.auth.Principal

data class UserModel(
    val email: String,
    val password: String,
    val userName: String
) : Principal