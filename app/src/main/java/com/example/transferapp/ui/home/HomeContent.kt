package com.example.transferapp.ui.home

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.transferapp.data.model.*
import com.example.transferapp.ui.Screen
import com.example.transferapp.data.model.Unit as ModelUnit
import com.example.transferapp.ui.home.components.FilterDropdown
import com.example.transferapp.ui.home.components.OutlinedTextFieldPax
import com.example.transferapp.ui.home.components.AvailabilityCard
import com.example.transferapp.ui.home.components.DatePickerBox

@OptIn(UnstableApi::class)
@Composable
fun HomeContent(
    navController: NavController,
    homeData: HomeData,
    availabilityData: AvailabilityResponse?,
    paddingValues: PaddingValues,
    fetchAvailability: (String, String, String, String) -> Unit,
    selectedZone: Zone?,
    onZoneSelected: (Zone?) -> Unit,
    selectedStore: Store?,
    onStoreSelected: (Store?) -> Unit,
    selectedAgency: Agency?,
    onAgencySelected: (Agency?) -> Unit,
    selectedHotel: Hotel?,
    onHotelSelected: (Hotel?) -> Unit,
    selectedPickup: Pickup?,
    onPickupSelected: (Pickup?) -> Unit,
    selectedUnit: ModelUnit?,
    onUnitSelected: (ModelUnit?) -> Unit, // Aquí usamos ModelUnit en vez de Unit del data model
    pax: String,
    onPaxChange: (String) -> Unit,
    adults: String,
    onAdultsChange: (String) -> Unit,
    children: String,
    onChildrenChange: (String) -> Unit,
    clientName: String,
    onClientNameChange: (String) -> Unit,
    selectedDate: String,
    onDateChange: (String) -> Unit,
    adultsEnabled: Boolean,
    onAdultsEnabledChange: (Boolean) -> Unit,
    clientNameEnabled: Boolean,
    onClientNameEnabledChange: (Boolean) -> Unit,
    showAvailability: Boolean,
    reservationFolio: String
) {
    // Logs para debug
    Log.d("HomeContent", "Agencias disponibles: ${homeData.agencies.joinToString { it.name }}")
    Log.d("HomeContent", "Agencia seleccionada: ${selectedAgency?.name ?: "Ninguna"}")

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (reservationFolio.isNotEmpty()) {
            Text(
                text = "Folio de la Reserva: $reservationFolio",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        // Selector de Zonas
        Log.d("HomeContent", "Zonas disponibles: ${homeData.zones.joinToString { it.name }}")
        FilterDropdown(
            label = "Selecciona una Zona",
            options = homeData.zones.map { it.name },
            selectedOption = selectedZone?.name,
            onOptionSelected = { zoneName ->
                val zone = homeData.zones.firstOrNull { it.name == zoneName }
                Log.d("HomeContent", "Zona seleccionada: ${zone?.name ?: "Ninguna"}")
                onZoneSelected(zone)
                onStoreSelected(null)
                onAgencySelected(null)
                onHotelSelected(null)
                onPickupSelected(null)
                onUnitSelected(null)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Tiendas
        val storeOptions = if (selectedZone != null) {
            homeData.stores.filter { it.zoneId == selectedZone.id }.map { it.name }
        } else emptyList()
        Log.d("HomeContent", "Tiendas filtradas por Zona: $storeOptions")
        FilterDropdown(
            label = "Selecciona un Shopping",
            options = storeOptions,
            selectedOption = selectedStore?.name,
            onOptionSelected = { storeName ->
                val store = homeData.stores.firstOrNull { it.name == storeName }
                Log.d("HomeContent", "Tienda seleccionada: ${store?.name ?: "Ninguna"}")
                onStoreSelected(store)
            },
            enabled = selectedZone != null
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Agencias - Solo la agencia del usuario
        selectedAgency?.let { agency ->
            Log.d("HomeContent", "Mostrando agencia del usuario: ${agency.name}")
            FilterDropdown(
                label = "Selecciona una Agencia",
                options = listOf(agency.name),
                selectedOption = agency.name,
                onOptionSelected = {
                    // No permitir selección, ya está preseleccionada
                    Log.d("HomeContent", "Intento de cambiar agencia, se ignora porque está deshabilitado.")
                },
                enabled = false
            )
        } ?: run {
            // Mostrar un mensaje o indicador de carga si la agencia no está disponible
            Log.d("HomeContent", "Agencia del usuario aún no disponible, mostrando mensaje de carga...")
            Text(
                text = "Cargando agencia del usuario...",
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Hoteles
        val hotelOptions = if (selectedZone != null) {
            homeData.hotels.filter { it.zoneId == selectedZone.id }.map { it.name }
        } else emptyList()
        Log.d("HomeContent", "Hoteles filtrados por Zona: $hotelOptions")
        FilterDropdown(
            label = "Selecciona un Hotel",
            options = hotelOptions,
            selectedOption = selectedHotel?.name,
            onOptionSelected = { hotelName ->
                val hotel = homeData.hotels.firstOrNull { it.name == hotelName }
                Log.d("HomeContent", "Hotel seleccionado: ${hotel?.name ?: "Ninguno"}")
                onHotelSelected(hotel)
                val pickup = homeData.pickups.firstOrNull { it.hotelId == hotel?.id }
                Log.d("HomeContent", "Pickup seleccionado automáticamente: ${pickup?.pickupTime ?: "Ninguno"}")
                onPickupSelected(pickup)
            },
            enabled = selectedZone != null
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Horario de Recogida
        Log.d("HomeContent", "Pickup seleccionado: ${selectedPickup?.pickupTime ?: "Ninguno"}")
        OutlinedTextFieldPax(
            value = selectedPickup?.pickupTime ?: "Sin horario de recogida",
            onValueChange = {},
            label = "Horario de Recogida",
            enabled = false,
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Unidades
        val unitOptions = homeData.units.map { "${it.name} (${it.seatCount ?: "N/A"} asientos)" }
        Log.d("HomeContent", "Unidades disponibles: $unitOptions")
        FilterDropdown(
            label = "Selecciona una Unidad",
            options = unitOptions,
            selectedOption = selectedUnit?.let { "${it.name} (${it.seatCount ?: "N/A"} asientos)" },
            onOptionSelected = { option ->
                val unitName = option.substringBefore(" (")
                val unitn = homeData.units.firstOrNull { it.name == unitName }
                Log.d("HomeContent", "Unidad seleccionada: ${unitn?.name ?: "Ninguna"}")
                onUnitSelected(unitn)
            },
            enabled = true
        )

        // Información de la unidad seleccionada
        selectedUnit?.let { unit ->
            Log.d("HomeContent", "Mostrando datos de la Unidad seleccionada: ${unit.name}")
            Text(
                text = "Unidad Seleccionada: ${unit.name}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            unit.seatCount?.let { seatCount ->
                Text(
                    text = "Asientos Totales: $seatCount",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } ?: Text(
                text = "Asientos Totales: Información no disponible",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.error
            )
        } ?: run {
            Log.d("HomeContent", "No hay Unidad seleccionada aún.")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // DatePicker
        Log.d("HomeContent", "Fecha seleccionada: $selectedDate")
        DatePickerBox(
            selectedDate = selectedDate,
            onDateSelected = onDateChange
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Input de Pax
        Log.d("HomeContent", "Pax: $pax, Adults: $adults, Children: $children, ClientName: $clientName")
        OutlinedTextFieldPax(
            value = pax,
            onValueChange = { value ->
                Log.d("HomeContent", "Pax cambiado a: $value")
                onPaxChange(value)
                onAdultsEnabledChange(value.isNotEmpty())
                if (value.isEmpty()) {
                    onAdultsChange("")
                    onChildrenChange("")
                    onClientNameEnabledChange(false)
                    Log.d("HomeContent", "Pax vacío, reseteando Adults, Children, ClientNameEnabled.")
                } else {
                    val paxValue = value.toIntOrNull() ?: 0
                    val adultsValue = adults.toIntOrNull() ?: 0
                    val calculatedChildren = calculateChildren(paxValue, adultsValue).toString()
                    onChildrenChange(calculatedChildren)
                    Log.d("HomeContent", "Niños calculados: $calculatedChildren")
                }
            },
            label = "Número de Asientos (Pax)",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextFieldPax(
                value = adults,
                onValueChange = { value ->
                    Log.d("HomeContent", "Adults cambiado a: $value")
                    onAdultsChange(value)
                    val paxValue = pax.toIntOrNull() ?: 0
                    val adultsValue = value.toIntOrNull() ?: 0
                    val calculatedChildren = calculateChildren(paxValue, adultsValue).toString()
                    onChildrenChange(calculatedChildren)
                    onClientNameEnabledChange(validatePax(adults, children, pax))
                    Log.d("HomeContent", "Niños recalculados: $calculatedChildren, ClientNameEnabled: $clientNameEnabled")
                },
                label = "Adultos",
                enabled = adultsEnabled,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextFieldPax(
                value = children,
                onValueChange = {},
                label = "Niños (Automático)",
                readOnly = true,
                enabled = false,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Log.d("HomeContent", "ClientName: $clientName, ClientNameEnabled: $clientNameEnabled")
        OutlinedTextFieldPax(
            value = clientName,
            onValueChange = {
                Log.d("HomeContent", "ClientName cambiado a: $it")
                onClientNameChange(it)
            },
            label = "Nombre del Cliente",
            enabled = clientNameEnabled,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Botón para Buscar Disponibilidad
        Button(
            onClick = {
                Log.d("HomeContent", "Botón Buscar Disponibilidad presionado")
                if (selectedUnit != null && selectedPickup != null && selectedDate.isNotEmpty()) {
                    Log.d("HomeContent", "Parametros para fetchAvailability: unitId=${selectedUnit!!.id}, pickupTime=${selectedPickup!!.pickupTime}, reservationDate=$selectedDate, hotelId=${selectedHotel?.id ?: ""}")
                    fetchAvailability(
                        selectedUnit!!.id,
                        selectedPickup!!.pickupTime,
                        selectedDate,
                        selectedHotel!!.id
                    )
                } else {
                    Log.d("HomeContent", "No se cumplen las condiciones para fetchAvailability")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar Disponibilidad")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar Disponibilidad
        if (showAvailability && availabilityData != null) {
            Log.d("HomeContent", "Mostrando disponibilidad: ${availabilityData.data}")
            availabilityData.data?.let { data ->
                AvailabilityCard(
                    unitName = data.unit.name,
                    totalSeats = data.totalSeats,
                    occupiedSeats = data.occupiedSeats,
                    pendingSeats = data.pendingSeats,
                    availableSeats = data.availableSeats
                )
                if (data.availableSeats > 0) {
                    Button(
                        onClick = {
                            Log.d("HomeContent", "Botón Reservar presionado")
                            if (clientName.isNotEmpty() && selectedUnit != null && selectedPickup != null && selectedDate.isNotEmpty() && selectedHotel != null && selectedAgency != null) {
                                Log.d("HomeContent", "Navegando a SeatSelection con la siguiente información: " +
                                        "unitId=${selectedUnit!!.id}, pickupTime=${selectedPickup!!.pickupTime}, " +
                                        "reservationDate=$selectedDate, hotelId=${selectedHotel!!.id}, agencyId=${selectedAgency!!.id}, " +
                                        "client=$clientName, adult=${adults.toInt()}, child=${children.toInt()}, " +
                                        "zoneId=${selectedZone!!.id}, storeId=${selectedStore!!.id}")
                                navController.navigate(
                                    Screen.SeatSelection.createRoute(
                                        unitId = selectedUnit!!.id,
                                        pickupTime = selectedPickup!!.pickupTime,
                                        reservationDate = selectedDate,
                                        hotelId = selectedHotel!!.id,
                                        agencyId = selectedAgency!!.id,
                                        client = clientName,
                                        adult = adults.toInt(),
                                        child = children.toInt(),
                                        zoneId = selectedZone!!.id,
                                        storeId = selectedStore!!.id,
                                        folio = reservationFolio
                                    )
                                )
                            } else {
                                Log.e("Navigation", "Error: argumentos nulos o vacíos")
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Reservar")
                    }
                }
            } ?: run {
                Log.e("HomeContent", "No se pudieron cargar los datos de disponibilidad.")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se pudieron cargar los datos.")
                }
            }
        } else {
            Log.d("HomeContent", "showAvailability=$showAvailability o availabilityData=$availabilityData, no se muestra disponibilidad.")
        }
    }
}

// Función para calcular niños
fun calculateChildren(pax: Int, adults: Int): Int {
    return (pax - adults).coerceAtLeast(0)
}

// Función para validar Pax
fun validatePax(adults: String, children: String, pax: String): Boolean {
    val total = (adults.toIntOrNull() ?: 0) + (children.toIntOrNull() ?: 0)
    return total == (pax.toIntOrNull() ?: 0)
}
