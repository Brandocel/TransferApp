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
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel, userId: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val isLoading by homeViewModel.isLoading.collectAsState()
    val homeData by homeViewModel.homeData.collectAsState(initial = null)
    val availabilityData by homeViewModel.availabilityData.collectAsState(initial = null)
    val userAgency by homeViewModel.userAgency.collectAsState()

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

    var adultsEnabled by remember { mutableStateOf(false) }
    var clientNameEnabled by remember { mutableStateOf(false) }
    var showAvailability by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Obtenemos la data inicial
        homeViewModel.fetchHomeData()

        // Obtenemos la agencia del usuario solo si userId no está vacío
        if (userId.isNotEmpty()) {
            Log.d("HomeScreen", "Llamando a fetchUserAgency con userId=$userId")
            homeViewModel.fetchUserAgency(userId)
        } else {
            Log.e("HomeScreen", "userId está vacío, no se llamará a fetchUserAgency")
        }
    }

    // Cuando obtengas userAgency, si selectedAgency todavía es null, asígnala solo una vez.
    if (userAgency != null && selectedAgency == null) {
        Log.d("HomeScreen", "Agencia del usuario cargada por primera vez: ${    userAgency!!.name}")
        selectedAgency = userAgency
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideMenuContent(
                isLoadingReservations = homeViewModel.isLoadingReservations.collectAsState().value,
                reservations = homeViewModel.reservations.collectAsState().value,
                onFetchReservations = {
                    coroutineScope.launch {
                        if (userId.isNotEmpty()) {
                            Log.d("HomeScreen", "Obteniendo reservas del usuario con userId=$userId")
                            homeViewModel.fetchUserReservations(userId)
                        } else {
                            Log.e("HomeScreen", "userId está vacío, no se obtendrán reservas")
                        }
                    }
                },
                onCloseMenu = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
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
                homeData?.let {
                    HomeContent(
                        navController = navController,
                        homeData = it,
                        availabilityData = availabilityData,
                        paddingValues = paddingValues,
                        fetchAvailability = { unitId, pickupTime, reservationDate, hotelId ->
                            homeViewModel.fetchUnitAvailability(unitId, pickupTime, reservationDate, hotelId)
                            showAvailability = true
                        },
                        selectedZone = selectedZone,
                        onZoneSelected = { newZone ->
                            selectedZone = newZone
                            selectedStore = null
                            // IMPORTANTE: Ya no ponemos onAgencySelected(null) aquí
                            selectedHotel = null
                            selectedPickup = null
                            selectedUnit = null
                        },
                        selectedStore = selectedStore,
                        onStoreSelected = { selectedStore = it },
                        selectedAgency = selectedAgency,
                        onAgencySelected = {
                            // Si no quieres que se cambie una vez obtenida, no hagas nada aquí.
                            // selectedAgency = it
                        },
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



