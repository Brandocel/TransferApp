package com.example.transferapp.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SideMenuContent(
    isLoadingReservations: Boolean,
    reservations: List<Reservation>,
    onFetchReservations: () -> Unit
) {
    LaunchedEffect(Unit) {
        onFetchReservations()
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "Menú Lateral",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoadingReservations) {
            repeat(3) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    )
                }
            }
        } else {
            reservations.forEach { reservation ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = reservation.hotelName, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Folio: ${reservation.folio}")
                        Text(text = "Pax: ${reservation.pax}, Adults: ${reservation.adults}, Children: ${reservation.children}")
                        Text(text = "Pickup: ${reservation.pickupTime}")
                    }
                }
            }
        }
    }
}
data class ReservationResponse(
    val success: Boolean,
    val message: String,
    val data: List<Reservation>
)

data class Reservation(
    val id: String,
    val zoneName: String,
    val agencyName: String,
    val hotelName: String,
    val unitName: String,
    val storeName: String,
    val seatNumber: Int,
    val pickupTime: String,
    val reservationDate: String,
    val clientName: String,
    val observations: String,
    val pax: Int,
    val adults: Int,
    val children: Int,
    val status: String,
    val folio: String
)

