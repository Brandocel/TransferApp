package com.example.transferapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse


    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    companion object {
        private const val BASE_URL = "https://e899-2806-10be-a-833e-5c6a-da6b-3184-1bee.ngrok-free.app/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }

}

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val user: User)
data class RegisterRequest(val email: String, val password: String, val name: String, val roleId: String)
data class RegisterResponse(val success: Boolean, val message: String)
data class User(val id: String, val name: String, val email: String)
