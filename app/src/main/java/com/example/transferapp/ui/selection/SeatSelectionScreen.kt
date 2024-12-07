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
import com.example.transferapp.ui.Screen
import com.example.transferapp.ui.selection.components.TicketCard
import com.example.transferapp.viewmodel.SeatSelectionViewModel
import kotlinx.coroutines.launch

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
    userId: String,
    folio: String
) {
    val clientDisplayName = client.replace('+', ' ')

    LaunchedEffect(Unit) {
        viewModel.fetchAgencyName(agencyId)
        viewModel.fetchUserName(userId)
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

    val agencyName by viewModel.agencyName.collectAsState()
    val userName by viewModel.userName.collectAsState()

    Log.d("SeatSelectionScreen", "AgencyName: ${agencyName ?: "Cargando..."}")
    Log.d("SeatSelectionScreen", "UserName: ${userName ?: "Cargando..."}")

    val coroutineScope = rememberCoroutineScope()

    val maxSelectableSeats = adult + child
    val selectedSeats = remember { mutableStateListOf<Int>() }
    var showDialog by remember { mutableStateOf(false) }
    var showTicket by remember { mutableStateOf(false) }
    var reservationData by remember { mutableStateOf<ReservationResponseItem?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        showTicket = false
        reservationData = null
    }

    LaunchedEffect(Unit) {
        Log.d("SeatSelectionScreen", "Reinicializando pantalla")
        showTicket = false
        reservationData = null
        showDialog = false
        viewModel.clearReservationResponse()

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
            reservationResponse!!.data?.let { folioReceived ->
                Log.d("SeatSelectionScreen", "Reservation updated successfully. Folio: $folioReceived")
                reservationData = ReservationResponseItem(
                    id = folioReceived,
                    userId = userId,
                    zoneId = zoneId,
                    agencyId = agencyId,
                    hotelId = hotelId,
                    unitId = unitId,
                    seatNumber = selectedSeats.toList(),
                    pickupTime = pickupTime,
                    reservationDate = reservationDate,
                    clientName = clientDisplayName,
                    observations = "Sin observaciones",
                    storeId = storeId,
                    pax = maxSelectableSeats,
                    adults = adult,
                    children = child,
                    status = "paid",
                    folio = folioReceived
                )
                showTicket = true
            } ?: run {
                Log.e("SeatSelectionScreen", "Reservation data is null.")
            }
        } else if (reservationResponse != null) {
            Log.d("SeatSelectionScreen", "Reservation update failed. Message: ${reservationResponse!!.message}")
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Error al actualizar la reserva: ${reservationResponse!!.message}")
            }
        }

        showDialog = false
    }

    LaunchedEffect(Unit) {
        showTicket = false
        reservationData = null
        selectedSeats.clear()
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                    Text(
                        text = "Folio: ${if (folio.isNotEmpty()) folio else "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(text = "Cliente: $clientDisplayName")
                    Text(text = "Agencia: ${agencyName ?: "Cargando..."}")
                    Text(text = "Representante: ${userName ?: "Cargando..."}")
                    Text(text = "Adultos: $adult")
                    Text(text = "Niños: $child")

                    Spacer(modifier = Modifier.height(16.dp))

                    SeatGrid(
                        totalSeats = data.totalSeats,
                        occupiedSeats = data.paid ?: emptyList(),
                        pendingSeats = data.pending ?: emptyList(),
                        selectedSeats = selectedSeats,
                        maxSelectableSeats = maxSelectableSeats
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (selectedSeats.size == maxSelectableSeats) {
                                showDialog = true
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Debes seleccionar exactamente $maxSelectableSeats asientos.")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedSeats.size <= maxSelectableSeats
                    ) {
                        Text("Reservar")
                    }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Confirmar Reserva") },
                            text = { Text("¿Estás seguro de querer actualizar esta reserva?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                    val request = MultipleReservationsRequest(
                                        userId = userId,
                                        zoneId = zoneId,
                                        agencyId = agencyId,
                                        hotelId = hotelId,
                                        unitId = unitId,
                                        seatNumber = selectedSeats.toList(),
                                        pickupTime = pickupTime,
                                        reservationDate = reservationDate,
                                        clientName = clientDisplayName,
                                        observations = "Sin observaciones",
                                        storeId = storeId,
                                        pax = maxSelectableSeats,
                                        adults = adult,
                                        children = child,
                                        status = "paid",
                                        folio = folio
                                    )
                                    viewModel.updateReservation(request)
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
                        .background(Color(0xAA000000)),
                    contentAlignment = Alignment.Center
                ) {
                    TicketCard(
                        reservation = reservationData!!,
                        onDismiss = {
                            showTicket = false
                            reservationData = null
                            selectedSeats.clear()
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SeatGrid(
    totalSeats: Int,
    occupiedSeats: List<Int>?,
    pendingSeats: List<Int>?,
    selectedSeats: MutableList<Int>,
    maxSelectableSeats: Int
) {
    val seatsPerRow = 4
    val rows = (totalSeats / seatsPerRow) + if (totalSeats % seatsPerRow != 0) 1 else 0

    // Mapeo de índice de columna a letra
    val columnLetters = listOf('A', 'B', 'C', 'D')

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (rowIndex in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (colIndex in 0 until seatsPerRow) {
                    val seatNumber = rowIndex * seatsPerRow + colIndex + 1
                    if (seatNumber <= totalSeats) {
                        val letter = if (colIndex < columnLetters.size) columnLetters[colIndex] else 'X'
                        val seatLabel = "${seatNumber}$letter"

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            SeatButton(
                                seatLabel = seatLabel,
                                seatNumber = seatNumber,
                                isOccupied = occupiedSeats?.contains(seatNumber) == true,
                                isPending = pendingSeats?.contains(seatNumber) == true,
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
                        }
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
    seatLabel: String,
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
            text = seatLabel,
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}
