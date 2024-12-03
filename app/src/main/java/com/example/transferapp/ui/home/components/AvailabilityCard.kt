package com.example.transferapp.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AvailabilityCard(
    unitName: String,
    totalSeats: Int,
    occupiedSeats: Int,
    pendingSeats: Int,
    availableSeats: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Disponibilidad de la Unidad",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Unidad: $unitName")
            Text(text = "Total de Asientos: $totalSeats")
            Text(text = "Asientos Ocupados: $occupiedSeats")
            Text(text = "Asientos Pendientes: $pendingSeats")
            Text(text = "Asientos Disponibles: $availableSeats")
        }
    }

}
