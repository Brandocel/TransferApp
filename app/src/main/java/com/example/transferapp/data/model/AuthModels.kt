package com.example.transferapp.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val roleId: String
)

data class RegisterResponse(
    val message: String
)


data class User(
    val id: String,
    val name: String,
    val email: String
)
