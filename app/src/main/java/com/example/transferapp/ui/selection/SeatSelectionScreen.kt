package com.example.transferapp.ui.selection

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.transferapp.R
import com.example.transferapp.viewmodel.SeatSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    navController: NavController,
    viewModel: SeatSelectionViewModel,
    agencyId: String,
    client: String,
    adult: Int,
    child: Int
) {
    val seatStatus by viewModel.seatStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()

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
                        .verticalScroll(scrollState)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Mostrar datos heredados
                    Text(text = "Cliente: $client")
                    Text(text = "Agencia ID: $agencyId")
                    Text(text = "Adultos: $adult")
                    Text(text = "Niños: $child")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Asientos Ocupados: ${data.paid.joinToString(", ")}")
                    Text(text = "Asientos Pendientes: ${data.pending.joinToString(", ")}")
                    Text(text = "Asientos Totales: ${data.totalSeats}")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Estado para los asientos seleccionados
                    val selectedSeats = remember { mutableStateListOf<Int>() }

                    // Renderizar asientos
                    SeatGrid(
                        totalSeats = data.totalSeats,
                        occupiedSeats = data.paid,
                        pendingSeats = data.pending,
                        selectedSeats = selectedSeats
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para confirmar selección
                    Button(
                        onClick = {
                            // Acción para confirmar la reserva
                            if (selectedSeats.size == (adult + child)) {
                                // Lógica para confirmar reserva aquí
                            } else {
                                // Mostrar mensaje de error si no se seleccionan suficientes asientos
                                println("Por favor selecciona ${adult + child} asientos.")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
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

@Composable
fun SeatGrid(
    totalSeats: Int,
    occupiedSeats: List<Int>,
    pendingSeats: List<Int>,
    selectedSeats: MutableList<Int>
) {
    val rows = (totalSeats / 4) + if (totalSeats % 4 != 0) 1 else 0

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (rowIndex in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (colIndex in 0..3) {
                    val seatNumber = rowIndex * 4 + colIndex + 1
                    if (seatNumber <= totalSeats) {
                        SeatButton(
                            seatNumber = seatNumber,
                            isOccupied = seatNumber in occupiedSeats,
                            isPending = seatNumber in pendingSeats,
                            isSelected = seatNumber in selectedSeats,
                            onSeatSelected = { selected ->
                                if (selected) {
                                    selectedSeats.add(seatNumber)
                                } else {
                                    selectedSeats.remove(seatNumber)
                                }
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(50.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SeatButton(
    seatNumber: Int,
    isOccupied: Boolean,
    isPending: Boolean,
    isSelected: Boolean,
    onSeatSelected: (Boolean) -> Unit
) {
    val seatState = when {
        isOccupied -> R.drawable.reservado
        isPending -> R.drawable.pendiente
        isSelected -> R.drawable.seleccionado
        else -> R.drawable.disponible
    }

    val isClickable = !isOccupied && !isPending

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(70.dp)
            .clickable(enabled = isClickable) {
                onSeatSelected(!isSelected)
            }
    ) {
        Image(
            painter = painterResource(id = seatState),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Text(
            text = seatNumber.toString(),
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}
