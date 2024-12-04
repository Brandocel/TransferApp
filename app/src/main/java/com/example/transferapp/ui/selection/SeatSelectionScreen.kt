package com.example.transferapp.ui.selection

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.transferapp.data.model.MultipleReservationsRequest
import com.example.transferapp.data.model.ReservationResponseItem
import com.example.transferapp.ui.selection.components.TicketCard
import com.example.transferapp.viewmodel.SeatSelectionViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    navController: NavController,
    viewModel: SeatSelectionViewModel,
    agencyId: String,
    client: String,
    adult: Int,
    child: Int,
    zoneId: String,
    storeId: String,
    unitId: String,
    pickupTime: String,
    reservationDate: String,
    hotelId: String,
    userId: String // Se obtiene del token
) {
    LaunchedEffect(Unit) {
        viewModel.fetchSeatStatus(
            unitId = unitId,
            pickupTime = pickupTime,
            reservationDate = reservationDate,
            hotelId = hotelId
        )
    }

    val seatStatus by viewModel.seatStatus.collectAsState()
    val reservationResponse by viewModel.reservationResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()

    val maxSelectableSeats = adult + child
    val selectedSeats = remember { mutableStateListOf<Int>() }
    var showDialog by remember { mutableStateOf(false) }
    var showTicket by remember { mutableStateOf(false) }
    var reservationData by remember { mutableStateOf<ReservationResponseItem?>(null) }
    val gson = remember { Gson() }

    // Limpia el estado de showTicket y reservationData al iniciar
    LaunchedEffect(Unit) {
        showTicket = false
        reservationData = null
    }

    // Muestra el ticket si la reserva es exitosa

    LaunchedEffect(Unit) {
        Log.d("SeatSelectionScreen", "Initializing SeatSelectionScreen")
        // Reinicia los estados al entrar en la pantalla
        showTicket = false
        reservationData = null

        // Llama al ViewModel para cargar los datos de los asientos
        viewModel.fetchSeatStatus(
            unitId = unitId,
            pickupTime = pickupTime,
            reservationDate = reservationDate,
            hotelId = hotelId
        )
    }

    LaunchedEffect(Unit) {
        Log.d("SeatSelectionScreen", "Initializing SeatSelectionScreen")
        // Reinicia los estados al entrar en la pantalla
        showTicket = false
        reservationData = null
        showDialog = false

        // Llama al ViewModel para cargar los datos de los asientos
        viewModel.fetchSeatStatus(
            unitId = unitId,
            pickupTime = pickupTime,
            reservationDate = reservationDate,
            hotelId = hotelId
        )
    }

    LaunchedEffect(Unit) {
        Log.d("SeatSelectionScreen", "Initializing SeatSelectionScreen")
        // Reinicia los estados al entrar en la pantalla
        showTicket = false
        reservationData = null
        showDialog = false
        viewModel.clearReservationResponse() // Limpia la respuesta anterior si es necesario

        // Llama al ViewModel para cargar los datos de los asientos
        viewModel.fetchSeatStatus(
            unitId = unitId,
            pickupTime = pickupTime,
            reservationDate = reservationDate,
            hotelId = hotelId
        )
    }

    LaunchedEffect(reservationResponse) {
        Log.d("SeatSelectionScreen", "Reservation response changed: $reservationResponse")

        if (reservationResponse?.success == true) {
            reservationResponse!!.data?.let { jsonData ->
                Log.d("SeatSelectionScreen", "Reservation was successful. Data: $jsonData")

                try {
                    // Parsear el JSON como una lista explícita
                    val reservations: List<ReservationResponseItem> = gson.fromJson(
                        jsonData.toString(),
                        object : TypeToken<List<ReservationResponseItem>>() {}.type
                    )

                    // Verifica si la lista no está vacía
                    if (reservations.isNotEmpty()) {
                        reservationData = reservations.first()
                        Log.d("SeatSelectionScreen", "Parsed reservation data: $reservationData")

                        // Muestra el ticket solo si `showDialog` es true
                        if (showDialog) {
                            showTicket = true
                            Log.d("SeatSelectionScreen", "Ticket is being displayed.")
                        } else {
                            Log.d("SeatSelectionScreen", "Ticket is not displayed because showDialog is false.")
                        }
                    } else {
                        Log.e("SeatSelectionScreen", "Reservations list is empty.")
                    }
                } catch (e: Exception) {
                    Log.e("SeatSelectionScreen", "Error parsing reservation data", e)
                }
            } ?: run {
                Log.e("SeatSelectionScreen", "Reservation data is null.")
            }
        } else if (reservationResponse != null) {
            Log.d("SeatSelectionScreen", "Reservation failed. Message: ${reservationResponse?.message}")
            SnackbarHostState().showSnackbar("Error en la reserva: ${reservationResponse?.message}")
        }

        // Siempre cierra el diálogo después de procesar la reserva
        showDialog = false
    }







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
            Box(modifier = Modifier.fillMaxSize()) {
                seatStatus?.data?.let { data ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .verticalScroll(scrollState)
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(text = "Cliente: $client")
                        Text(text = "Agencia ID: $agencyId")
                        Text(text = "Representante: $userId")
                        Text(text = "Adultos: $adult")
                        Text(text = "Niños: $child")

                        Spacer(modifier = Modifier.height(16.dp))

                        SeatGrid(
                            totalSeats = data.totalSeats,
                            occupiedSeats = data.paid,
                            pendingSeats = data.pending,
                            selectedSeats = selectedSeats,
                            maxSelectableSeats = maxSelectableSeats
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reservar")
                        }

                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text("Confirmar Reserva") },
                                text = { Text("¿Estás seguro de querer reservar estos asientos?") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showDialog = false
                                        viewModel.createMultipleReservations(
                                            MultipleReservationsRequest(
                                                userId = userId,
                                                zoneId = zoneId,
                                                agencyId = agencyId,
                                                hotelId = hotelId,
                                                unitId = unitId,
                                                seatNumber = selectedSeats.toList(),
                                                pickupTime = pickupTime,
                                                reservationDate = reservationDate,
                                                clientName = client,
                                                observations = "Sin observaciones",
                                                storeId = storeId,
                                                pax = maxSelectableSeats,
                                                adults = adult,
                                                children = child,
                                                status = ""
                                            )
                                        )
                                    }) {
                                        Text("Confirmar")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDialog = false }) {
                                        Text("Cancelar")
                                    }
                                }
                            )
                        }
                    }
                } ?: run {
                    Text(
                        text = "No se pudieron cargar los datos de los asientos.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (showTicket && reservationData != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xAA000000)), // Fondo translúcido
                        contentAlignment = Alignment.Center
                    ) {
                        TicketCard(
                            reservation = reservationData!!,
                            onDismiss = {
                                showTicket = false
                                reservationData = null
                                navController.navigate("home") { popUpTo("home") }
                            }
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun SeatGrid(
    totalSeats: Int,
    occupiedSeats: List<Int>,
    pendingSeats: List<Int>,
    selectedSeats: MutableList<Int>,
    maxSelectableSeats: Int
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
                                    if (selectedSeats.size < maxSelectableSeats) {
                                        selectedSeats.add(seatNumber)
                                    }
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
                if (isClickable) {
                    onSeatSelected(!isSelected)
                }
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
