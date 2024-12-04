package com.example.transferapp.data.model
import android.os.Parcelable

data class MultipleReservationsRequest(
    val userId: String,
    val zoneId: String,
    val agencyId: String,
    val hotelId: String,
    val unitId: String,
    val seatNumber: List<Int>,
    val pickupTime: String,
    val reservationDate: String,
    val clientName: String,
    val observations: String,
    val storeId: String,
    val pax: Int,
    val adults: Int,
    val children: Int,
    val status: String , // Por defecto
    val folio: String = "" // Puede generarse en el backend
)

data class ReservationResponse(
    val success: Boolean,
    val message: String,
    val data: List<ReservationResponseItem>
)

data class ReservationResponseItem(
    val ReservationId: String,
    val UserName: String,
    val ZoneName: String,
    val AgencyName: String,
    val HotelName: String,
    val UnitName: String,
    val StoreName: String,
    val seatNumber: List<Int>,
    val PickupTime: String,
    val ReservationDate: String,
    val ClientName: String,
    val Observations: String?,
    val Pax: Int,
    val Adults: Int,
    val Children: Int,
    val Status: String,
    val Folio: String
)