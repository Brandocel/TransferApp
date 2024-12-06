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
    userId: String // Se obtiene del token
) {
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

    //Agencia,Name
    val agencyName by viewModel.agencyName.collectAsState()
    val userName by viewModel.userName.collectAsState()
    // Agrega logs para confirmar los valores
    Log.d("SeatSelectionScreen", "AgencyName en Composable: ${agencyName ?: "Cargando..."}")
    Log.d("SeatSelectionScreen", "UserName en Composable: ${userName ?: "Cargando..."}")

    val coroutineScope = rememberCoroutineScope()

    val maxSelectableSeats = adult + child
    val selectedSeats = remember { mutableStateListOf<Int>() }
    var showDialog by remember { mutableStateOf(false) }
    var showTicket by remember { mutableStateOf(false) }
    var reservationData by remember { mutableStateOf<ReservationResponseItem?>(null) }
    val gson = remember { Gson() }
    val snackbarHostState = remember { SnackbarHostState() }

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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) } // Host para mostrar mensajes
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
                    // Mostrar los datos actualizados
                    Text(text = "Cliente: $client")
                    Text(text = "Agencia: ${agencyName ?: "Cargando..."}")
                    Text(text = "Representante: ${userName ?: "Cargando..."}")
                    Text(text = "Adultos: $adult")
                    Text(text = "Niños: $child")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Asientos
                    SeatGrid(
                        totalSeats = data.totalSeats,
                        occupiedSeats = data.paid ?: emptyList(),
                        pendingSeats = data.pending ?: emptyList(),
                        selectedSeats = selectedSeats,
                        maxSelectableSeats = maxSelectableSeats
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val coroutineScope = rememberCoroutineScope()

                    Button(
                        onClick = {
                            if (selectedSeats.size == maxSelectableSeats) {
                                showDialog = true
                            } else {
                                // Llamar a `showSnackbar` dentro de la corrutina del scope
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



                    // Diálogo de Confirmación
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




    // Mostrar Ticket flotante si `showTicket` es true
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
                            // Limpiar los datos al cerrar
                            showTicket = false
                            reservationData = null
                            selectedSeats.clear()
                        }
                    )
                }
            }
        }
    }

// Manejo del estado de la reserva
    LaunchedEffect(reservationResponse) {
        Log.d("SeatSelectionScreen", "Reservation response changed: $reservationResponse")

        if (reservationResponse?.success == true) {
            reservationResponse!!.data?.let { folio ->
                Log.d("SeatSelectionScreen", "Reservation was successful. Folio: $folio")

                reservationData = ReservationResponseItem(
                    id = folio,
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
                    status = "",
                    folio = folio
                )

                showTicket = true
                Log.d("SeatSelectionScreen", "Ticket is being displayed with folio: $folio")
            } ?: run {
                Log.e("SeatSelectionScreen", "Reservation data is null.")
            }
        } else if (reservationResponse != null) {
            Log.d("SeatSelectionScreen", "Reservation failed. Message: ${reservationResponse?.message}")
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Error en la reserva: ${reservationResponse?.message}")
            }
        }

        // Siempre cierra el diálogo después de procesar la reserva
        showDialog = false
    }


// Reiniciar estados al entrar en la pantalla
    LaunchedEffect(Unit) {
        showTicket = false
        reservationData = null
        selectedSeats.clear()
    }




//Otra seccion
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



@Composable
fun SeatGrid(
    totalSeats: Int,
    occupiedSeats: List<Int>?, // Permitir nulos
    pendingSeats: List<Int>?, // Permitir nulos
    selectedSeats: MutableList<Int>,
    maxSelectableSeats: Int
) {
    val seatsPerRow = 4 // Número de asientos por fila (2 columnas)
    val rows = (totalSeats / seatsPerRow) + if (totalSeats % seatsPerRow != 0) 1 else 0

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
                        val seatLabel = "${('A' + rowIndex)}${colIndex + 1}" // Genera etiqueta de asiento (ejemplo: A1, B2)
                        SeatButton(
                            seatLabel = seatLabel,
                            seatNumber = seatNumber,
                            isOccupied = occupiedSeats?.contains(seatNumber) == true, // Manejo seguro
                            isPending = pendingSeats?.contains(seatNumber) == true, // Manejo seguro
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
            text = seatLabel, // Muestra la etiqueta en lugar del número
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

