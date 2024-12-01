package com.example.transferapp.repository

import com.example.transferapp.data.api.ApiService

class HomeRepository(private val apiService: ApiService) {
    suspend fun fetchAllInfo() = apiService.getAllInfo()
}
