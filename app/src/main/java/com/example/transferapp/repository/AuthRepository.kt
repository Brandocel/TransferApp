package com.example.transferapp.repository

import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.api.LoginRequest
import com.example.transferapp.data.api.RegisterRequest


class AuthRepository(private val apiService: ApiService) {
    suspend fun login(email: String, password: String) =
        apiService.login(LoginRequest(email, password))


    suspend fun register(
        name: String,
        email: String,
        password: String,
        roleId: String
    ) = apiService.register(
        RegisterRequest(
            name = name,        // Correctamente mapeado al campo "name"
            email = email,      // Correctamente mapeado al campo "email"
            password = password, // Correctamente mapeado al campo "password"
            roleId = roleId      // Correctamente mapeado al campo "roleId"
        )
    )
}

