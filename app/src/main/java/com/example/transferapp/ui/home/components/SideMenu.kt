package com.example.transferapp.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SideMenuContent(
    isLoadingReservations: Boolean,
    reservations: List<Reservation>,
    onFetchReservations: () -> Unit,
    onCloseMenu: () -> Unit // Callback para cerrar el menú si es necesario
) {
    LaunchedEffect(Unit) {
        onFetchReservations()
    }

    // Limitar el ancho máximo del menú para evitar desbordamientos
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(max = 300.dp) // Ancho máximo del menú
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Título del menú
            item {
                Text(
                    text = "Menú Lateral",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Muestra un estado de carga si los datos aún no están listos
            if (isLoadingReservations) {
                items(3) {
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
                // Ordenar las reservas por número del folio
                val sortedReservations = reservations.sortedBy { reservation ->
                    reservation.folio.filter { it.isDigit() }.toIntOrNull() ?: Int.MAX_VALUE
                }

                // Lista de reservas ordenadas
                items(sortedReservations) { reservation ->
                    ReservationCard(reservation)
                }
            }

            // Botón para cerrar el menú
            item {
                Button(
                    onClick = { onCloseMenu() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Cerrar Menú")
                }
            }
        }
    }
}


@Composable
fun ReservationCard(reservation: Reservation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nombre del hotel
            Text(
                text = reservation.hotelName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Nombre del cliente
            Text(
                text = "Cliente: ${reservation.clientName}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Folio
            Text(
                text = "Folio: ${reservation.folio}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Fecha de la reserva
            Text(
                text = "Fecha: ${reservation.reservationDate}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Asientos seleccionados
            Text(
                text = "Asientos: ${reservation.seatNumber}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Detalles de pasajeros
            Text(
                text = "Pax: ${reservation.pax} (Adultos: ${reservation.adults}, Niños: ${reservation.children})",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Hora de recogida
            Text(
                text = "Recogida: ${reservation.pickupTime}",
                style = MaterialTheme.typography.bodyMedium
            )
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
