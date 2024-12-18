package com.example.transferapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transferapp.data.local.SessionManager
import com.example.transferapp.repository.AuthRepository
import extractUserId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _loginState = MutableStateFlow<String?>(null)
    val loginState: StateFlow<String?> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                val token = response.token
                sessionManager.saveAuthToken(token) // Guardar el token
                val userId = extractUserId(token) // Extraer el userId del token
                sessionManager.saveUserId(userId) // Guardar el userId en el SessionManager
                _loginState.value = "Login Successful"
            } catch (e: Exception) {
                _loginState.value = "Error: ${e.message}"
            }
        }
    }


    fun register(name: String, email: String, password: String, roleId: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.register(name, email, password, roleId)
                _loginState.value = response.message
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _loginState.value = "Error: $errorBody"
                println("Server Error: $errorBody") // Log para depuración
            } catch (e: Exception) {
                _loginState.value = "Error: ${e.message}"
                println("Error: ${e.message}")
            }
        }
    }

}
