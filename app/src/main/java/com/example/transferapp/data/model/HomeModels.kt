package com.example.transferapp.data.model

data class HomeResponse(
    val success: Boolean,
    val message: String,
    val data: HomeData
)

data class HomeData(
    val zones: List<Zone>,
    val agencies: List<Agency>,
    val hotels: List<Hotel>,
    val units: List<Unit>
)

data class Zone(
    val id: String,
    val name: String
)

data class Agency(
    val id: String,
    val name: String
)

data class Hotel(
    val id: String,
    val name: String,
    val zoneId: String // Vinculado al ID de la Zona
)

data class Unit(
    val id: String,
    val name: String,
    val agencyId: String // Vinculado al ID de la Agencia
)
