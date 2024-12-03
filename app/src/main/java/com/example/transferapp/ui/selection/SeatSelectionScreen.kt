package com.example.transferapp.ui.selection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.transferapp.viewmodel.SeatSelectionViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack


import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    navController: NavController,
    viewModel: SeatSelectionViewModel
) {
    val seatStatus by viewModel.seatStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Asientos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            seatStatus?.data?.let { data ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(text = "Asientos Ocupados: ${data.paid.joinToString(", ")}")
                    Text(text = "Asientos Pendientes: ${data.pending.joinToString(", ")}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { /* Confirmar selección de asientos */ }) {
                        Text("Confirmar Reserva")
                    }
                }
            } ?: run {
                Text(
                    text = "No se pudieron cargar los datos de los asientos.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

