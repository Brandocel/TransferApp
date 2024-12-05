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
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Detalles de la Reserva",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Gray)

            // Folio
            Text(
                text = "Folio: ${reservation.folio}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            // Cliente
            Text(
                text = "Cliente: ${reservation.clientName}",
                style = MaterialTheme.typography.bodyMedium
            )



            // Fecha y hora
            Text(
                text = "Fecha: ${reservation.reservationDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Hora de Recogida: ${reservation.pickupTime}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Asientos reservados
            Text(
                text = "Asientos Reservados: ${reservation.seatNumber.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Detalles de pasajeros
            Text(
                text = "Pasajeros (Pax): ${reservation.pax} (Adultos: ${reservation.adults}, Niños: ${reservation.children})",
                style = MaterialTheme.typography.bodyMedium
            )





            Spacer(modifier = Modifier.height(16.dp))

            // Botón para cerrar el ticket
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar")
            }
        }
    }
}



