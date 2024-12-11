package com.example.transferapp.ui.selection.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReservationDetails(
    client: String,
    agencyName: String,
    userName: String,
    adult: Int,
    child: Int,
    folio: String
) {
    Text(
        text = "Folio: ${if (folio.isNotEmpty()) folio else "N/A"}",
        style = MaterialTheme.typography.bodyMedium
    )
    Text(text = "Cliente: $client")
    Text(text = "Agencia: $agencyName")
    Text(text = "Representante: $userName")
    Text(text = "Adultos: $adult")
    Text(text = "Niños: $child")
}
