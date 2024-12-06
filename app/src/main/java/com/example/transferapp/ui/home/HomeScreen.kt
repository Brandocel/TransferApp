package com.example.transferapp.ui.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.transferapp.data.model.*
import com.example.transferapp.ui.home.components.SideMenuContent
import com.example.transferapp.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import com.example.transferapp.data.model.Unit as ModelUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel, token: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed) // Estado inicial cerrado
    val coroutineScope = rememberCoroutineScope()

    val isLoading by homeViewModel.isLoading.collectAsState()
    val homeData by homeViewModel.homeData.collectAsState(initial = null)
    val availabilityData by homeViewModel.availabilityData.collectAsState(initial = null)

    // Estados para campos
    var selectedZone by remember { mutableStateOf<Zone?>(null) }
    var selectedStore by remember { mutableStateOf<Store?>(null) }
    var selectedAgency by remember { mutableStateOf<Agency?>(null) }
    var selectedHotel by remember { mutableStateOf<Hotel?>(null) }
    var selectedPickup by remember { mutableStateOf<Pickup?>(null) }
    var selectedUnit by remember { mutableStateOf<ModelUnit?>(null) }

    var pax by remember { mutableStateOf("") }
    var adults by remember { mutableStateOf("") }
    var children by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }

    var adultsEnabled by remember { mutableStateOf(false) }
    var clientNameEnabled by remember { mutableStateOf(false) }
    var showAvailability by remember { mutableStateOf(false) }



    LaunchedEffect(Unit) {
        homeViewModel.fetchHomeData()
    }


    // Drawer con contenido condicional
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideMenuContent(
                isLoadingReservations = homeViewModel.isLoadingReservations.collectAsState().value,
                reservations = homeViewModel.reservations.collectAsState().value,
                onFetchReservations = {
                    coroutineScope.launch {
                        if (token.isNotEmpty()) {
                            Log.d("HomeScreen", "Token no está vacío, llamando a fetchUserReservations con token: $token")
                            homeViewModel.fetchUserReservations(token) // Asegúrate de que 'token' es realmente el 'userId'
                        } else {
                            Log.e("HomeScreen", "Token está vacío, no se llamará a fetchUserReservations")
                        }
                    }
                },

                onCloseMenu = {
                    coroutineScope.launch {
                        drawerState.close() // Cierra el menú cuando se llama a onCloseMenu
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Majestic Expedidition") },
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
                homeData?.let {
                    HomeContent(
                        navController = navController,
                        homeData = it,
                        availabilityData = availabilityData,
                        paddingValues = paddingValues,
                        fetchAvailability = { unitId, pickupTime, reservationDate, hotelId ->
                            homeViewModel.fetchUnitAvailability(
                                unitId,
                                pickupTime,
                                reservationDate,
                                hotelId
                            )
                            showAvailability = true
                        },
                        selectedZone = selectedZone,
                        onZoneSelected = { selectedZone = it },
                        selectedStore = selectedStore,
                        onStoreSelected = { selectedStore = it },
                        selectedAgency = selectedAgency,
                        onAgencySelected = { selectedAgency = it },
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
                        showAvailability = showAvailability
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

