package com.example.transferapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.transferapp.repository.AuthRepository
import com.example.transferapp.data.local.SessionManager

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
