package com.example.transferapp.data.model

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    val success: Boolean,
    val message: String,
    val data: HomeData
)

data class HomeData(
    val zones: List<Zone>,
    val agencies: List<Agency>,
    val hotels: List<Hotel>,
    val units: List<Unit>,
    val pickups: List<Pickup>,
    val stores: List<Store>
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
    @SerializedName("seatCount") val seatCount: Int?,
    val pricePerSeat: Double?,
    val description: String?,
    val isDelete: Boolean,
    val zoneId:String
)




data class Pickup(
    val id: String,
    val pickupTime: String,
    val hotelId: String
)

data class Store(
    val id: String,
    val name: String,
    val zoneId: String
)



//Ver disponibilidad

data class AvailabilityResponse(
    val success: Boolean,
    val message: String,
    val data: AvailabilityData
)

data class AvailabilityData(
    val unit: UnitInfo,
    val totalSeats: Int,
    val occupiedSeats: Int,
    val pendingSeats: Int,
    val availableSeats: Int
)

data class UnitInfo(
    val id: String,
    val name: String
)




