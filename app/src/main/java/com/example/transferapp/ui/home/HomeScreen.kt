package com.example.transferapp.ui.home

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lint.kotlin.metadata.internal.metadata.jvm.JvmProtoBuf.flags
import androidx.navigation.NavController
import com.example.transferapp.MainActivity
import com.example.transferapp.data.model.*
import com.example.transferapp.ui.home.components.SideMenuContent
import com.example.transferapp.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import com.example.transferapp.data.model.Unit as ModelUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel, userId: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val isLoading by homeViewModel.isLoading.collectAsState()
    val homeData by homeViewModel.homeData.collectAsState(initial = null)
    val availabilityData by homeViewModel.availabilityData.collectAsState(initial = null)
    val userAgency by homeViewModel.userAgency.collectAsState() // Recuperamos la agencia del usuario
    val pendingReservations by homeViewModel.pendingReservations.collectAsState()

    // Estados para filtros
    var selectedZone by remember { mutableStateOf<Zone?>(null) }
    var selectedStore by remember { mutableStateOf<Store?>(null) }
    var selectedAgency by remember { mutableStateOf<Agency?>(null) }
    var selectedHotel by remember { mutableStateOf<Hotel?>(null) }
    var selectedPickup by remember { mutableStateOf<Pickup?>(null) }
    var selectedUnit by remember { mutableStateOf<ModelUnit?>(null) }

    // Otros estados
    var pax by remember { mutableStateOf("") }
    var adults by remember { mutableStateOf("") }
    var children by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }

    // Estado para almacenar el folio
    var reservationFolio by remember { mutableStateOf("") }

    var adultsEnabled by remember { mutableStateOf(false) }
    var clientNameEnabled by remember { mutableStateOf(false) }
    var showAvailability by remember { mutableStateOf(false) }


    // Función para manejar el cierre de sesión
    val context = LocalContext.current // Obtén el contexto de Compose
    val onLogout: () -> Unit = {
        homeViewModel.logout {
            Log.d("HomeScreen", "Logout exitoso. Reiniciando actividad y navegando a login...")

            // Reinicia la actividad completamente y redirige al login
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }





    LaunchedEffect(Unit) {
        if (userId.isNotEmpty()) {
            Log.d("HomeScreen", "Llamando a fetchUserAgency con userId=$userId")
            homeViewModel.fetchUserAgency(userId) // Recuperamos la agencia del usuario
        } else {
            Log.e("HomeScreen", "userId está vacío, no se llamará a fetchUserAgency")
        }
        // Inicializamos la data
        homeViewModel.initializeData(userId)
    }

    // Llenar/limpiar campos del formulario según pendientes
    LaunchedEffect(pendingReservations, homeData, userAgency) {
        if (pendingReservations.isNullOrEmpty()) {
            // No hay pendientes, limpiar todo
            selectedZone = null
            selectedStore = null
            selectedHotel = null
            selectedPickup = null
            selectedUnit = null
            pax = ""
            adults = ""
            children = ""
            clientName = ""
            selectedDate = ""
            reservationFolio = ""
            showAvailability = false
            // La agencia la fijamos desde userAgency si está disponible
            selectedAgency = userAgency
        } else {
            // Hay una pendiente, cargarla
            pendingReservations!!.firstOrNull()?.let { reservation ->
                if (homeData != null) {
                    selectedZone = homeData!!.zones.find { it.id == reservation.zoneId }
                    selectedStore = homeData!!.stores.find { it.id == reservation.storeId }
                    selectedHotel = homeData!!.hotels.find { it.id == reservation.hotelId }
                    // Si hay una reserva pendiente, usamos la agencia de la reserva pendiente
                    selectedAgency = homeData!!.agencies.find { it.id == reservation.agencyId }
                    selectedUnit = homeData!!.units.find { it.id == reservation.unitId }
                    selectedPickup = homeData!!.pickups.find { it.pickupTime == reservation.pickupTime }

                    pax = reservation.pax.toString()
                    adults = reservation.adults.toString()
                    children = reservation.children.toString()
                    clientName = reservation.clientName
                    selectedDate = reservation.reservationDate.substringBefore("T")
                    reservationFolio = reservation.folio
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideMenuContent(
                isLoadingReservations = homeViewModel.isLoadingReservations.collectAsState().value,
                reservations = homeViewModel.reservations.collectAsState().value,
                onFetchReservations = {
                    coroutineScope.launch {
                        homeViewModel.fetchUserReservations(userId)
                    }
                },
                onCloseMenu = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                onLogout = onLogout // Pasamos la función aquí
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Majestic Expedition") },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
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
                homeData?.let { homeInfo ->
                    HomeContent(
                        navController = navController,
                        homeData = homeInfo,
                        availabilityData = availabilityData,
                        paddingValues = paddingValues,
                        fetchAvailability = { unitId, reservationDate, zoneId ->
                            homeViewModel.fetchUnitAvailability(unitId, reservationDate, zoneId)
                            showAvailability = true
                        },
                        selectedZone = selectedZone,
                        onZoneSelected = { newZone ->
                            selectedZone = newZone
                            selectedStore = null
                            selectedHotel = null
                            selectedPickup = null
                            selectedUnit = null
                            // Si se cambia de zona y no hay pendientes, restablecemos la agencia desde userAgency
                            if (pendingReservations.isNullOrEmpty()) {
                                selectedAgency = userAgency
                            }
                        },
                        selectedStore = selectedStore,
                        onStoreSelected = { selectedStore = it },
                        selectedAgency = selectedAgency,
                        onAgencySelected = {},
                        selectedHotel = selectedHotel,
                        onHotelSelected = { selectedHotel = it },
                        selectedPickup = selectedPickup,
                        onPickupSelected = { selectedPickup = it },
                        selectedUnit = selectedUnit,
                        onUnitSelected = { selectedUnit = it },
                        pax = pax,
                        onPaxChange = { pax = it },
                        adults = adults,
                        onAdultsChange = { adults = it },
                        children = children,
                        onChildrenChange = { children = it },
                        clientName = clientName,
                        onClientNameChange = { clientName = it },
                        selectedDate = selectedDate,
                        onDateChange = { selectedDate = it },
                        adultsEnabled = adultsEnabled,
                        onAdultsEnabledChange = { adultsEnabled = it },
                        clientNameEnabled = clientNameEnabled,
                        onClientNameEnabledChange = { clientNameEnabled = it },
                        showAvailability = showAvailability,
                        reservationFolio = reservationFolio,
                        homeViewModel = homeViewModel,
                        userId = userId
                    )
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se pudieron cargar los datos.")
                    }
                }
            }
        }
    }
}
