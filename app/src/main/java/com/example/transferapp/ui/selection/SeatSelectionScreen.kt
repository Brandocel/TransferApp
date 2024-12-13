package com.example.transferapp.ui.selection

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.transferapp.data.model.MultipleReservationsRequest
import com.example.transferapp.data.model.ReservationResponseItem
import com.example.transferapp.ui.selection.components.ReservationConfirmationDialog
import com.example.transferapp.ui.selection.components.ReservationDetails
import com.example.transferapp.ui.selection.components.SeatGrid
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
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val seatStatus by viewModel.seatStatus.collectAsState()
    val reservationResponse by viewModel.reservationResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val agencyName by viewModel.agencyName.collectAsState()
    val userName by viewModel.userName.collectAsState()

    var isReservationConfirmed by remember { mutableStateOf(false) }
    val maxSelectableSeats = adult + child
    val selectedSeats = remember { mutableStateListOf<Int>() }
    var showDialog by remember { mutableStateOf(false) }
    var showTicket by remember { mutableStateOf(false) }
    var reservationData by remember { mutableStateOf<ReservationResponseItem?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Limpieza inicial al cargar la pantalla
    LaunchedEffect(Unit) {
        viewModel.fetchAgencyName(agencyId)
        viewModel.fetchUserName(userId)
        viewModel.fetchSeatStatus(unitId, reservationDate, zoneId)
        showTicket = false
        reservationData = null
        selectedSeats.clear()
    }

    // Limpieza al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            if (!isReservationConfirmed) {
                coroutineScope.launch {
                    viewModel.updateReservation(
                        MultipleReservationsRequest(
                            userId = userId,
                            zoneId = zoneId,
                            agencyId = agencyId,
                            hotelId = hotelId,
                            unitId = unitId,
                            seatNumber = emptyList(), // Limpiar asientos seleccionados
                            seatSectionIdentifier = "",
                            pickupTime = pickupTime,
                            reservationDate = reservationDate,
                            clientName = client,
                            observations = "Limpieza al salir de la pantalla",
                            storeId = storeId,
                            pax = maxSelectableSeats,
                            adults = adult,
                            children = child,
                            status = "", // Dejar estado vacío
                            folio = folio
                        ),
                        onError = { errorMessage ->
                            Log.e("SeatSelectionScreen", "Error limpiando asientos: $errorMessage")
                        },
                        onSuccess = {
                            Log.d("SeatSelectionScreen", "Asientos limpiados correctamente al salir.")
                        }
                    )
                }
            }
        }
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
                    ReservationDetails(
                        client = client,
                        agencyName = agencyName ?: "Cargando...",
                        userName = userName ?: "Cargando...",
                        adult = adult,
                        child = child,
                        folio = folio
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SeatGrid(
                        totalSeats = data.totalSeats,
                        occupiedSeats = data.paid ?: emptyList(),
                        pendingSeats = data.pending ?: emptyList(),
                        selectedSeats = selectedSeats,
                        maxSelectableSeats = maxSelectableSeats,
                        onUpdateSeat = {
                            viewModel.updateReservation(
                                MultipleReservationsRequest(
                                    userId = userId,
                                    zoneId = zoneId,
                                    agencyId = agencyId,
                                    hotelId = hotelId,
                                    unitId = unitId,
                                    seatNumber = selectedSeats.toList(),
                                    seatSectionIdentifier = "",
                                    pickupTime = pickupTime,
                                    reservationDate = reservationDate,
                                    clientName = client,
                                    observations = "Selección de asientos en proceso",
                                    storeId = storeId,
                                    pax = maxSelectableSeats,
                                    adults = adult,
                                    children = child,
                                    status = "pending",
                                    folio = folio
                                ),
                                onError = { errorMessage ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(errorMessage)
                                    }
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                    selectedSeats.clear()
                                },
                                onSuccess = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Actualización exitosa.")
                                    }
                                }
                            )
                        },
                        onError = { errorMessage ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(errorMessage)
                            }
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                            selectedSeats.clear()
                        },
                        onSuccess = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Actualización exitosa.")
                            }
                        }
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reservar")
                    }

                    if (showDialog) {
                        ReservationConfirmationDialog(
                            onConfirm = {
                                showDialog = false
                                isReservationConfirmed = true
                                viewModel.updateReservation(
                                    MultipleReservationsRequest(
                                        userId = userId,
                                        zoneId = zoneId,
                                        agencyId = agencyId,
                                        hotelId = hotelId,
                                        unitId = unitId,
                                        seatNumber = selectedSeats.toList(),
                                        seatSectionIdentifier ="",
                                        pickupTime = pickupTime,
                                        reservationDate = reservationDate,
                                        clientName = client,
                                        observations = "Reserva confirmada",
                                        storeId = storeId,
                                        pax = maxSelectableSeats,
                                        adults = adult,
                                        children = child,
                                        status = "paid",
                                        folio = folio
                                    ),
                                    onError = { errorMessage ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(errorMessage)
                                        }
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                        selectedSeats.clear()
                                    },
                                    onSuccess = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Reserva confirmada exitosamente.")
                                        }
                                        showTicket = true
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
                                            observations = "Reserva confirmada",
                                            storeId = storeId,
                                            pax = maxSelectableSeats,
                                            adults = adult,
                                            children = child,
                                            status = "paid",
                                            folio = folio
                                        )
                                    }
                                )
                            },
                            onDismiss = { showDialog = false },
                            onError = { errorMessage ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(errorMessage)
                                }
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                                selectedSeats.clear()
                            },
                            onSuccess = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Reserva confirmada exitosamente.")
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
                        navController.navigate("home") { popUpTo("home") }
                    }
                )
            }
        }
    }
}
