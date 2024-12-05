package com.example.transferapp.ui.selection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.transferapp.data.model.ReservationResponseItem

@Composable
fun TicketCard(
    reservation: ReservationResponseItem,
    onDismiss: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ticket de Reserva",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Divider(modifier = Modifier.fillMaxWidth())

            Text(
                text = "Folio: ${reservation.folio}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Cliente: ${reservation.clientName}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Fecha: ${reservation.reservationDate}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Hora de Recogida: ${reservation.pickupTime}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Asientos Reservados: ${reservation.seatNumber.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Adultos: ${reservation.adults}, Niños: ${reservation.children}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Estado: ${reservation.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (reservation.status == "Confirmado") Color.Green else Color.Red
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar")
            }
        }
    }
}


