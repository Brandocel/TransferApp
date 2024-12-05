package com.example.transferapp.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SideMenuContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Menú Lateral",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Tarjeta 1
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Tarjeta 1", style = MaterialTheme.typography.titleMedium)
                Text(text = "Descripción de la tarjeta 1")
            }
        }
        // Tarjeta 2
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Tarjeta 2", style = MaterialTheme.typography.titleMedium)
                Text(text = "Descripción de la tarjeta 2")
            }
        }
    }
}
