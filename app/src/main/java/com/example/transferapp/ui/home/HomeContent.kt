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
    onUnitSelected: (com.example.transferapp.data.model.Unit?) -> Unit,
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
    showAvailability: Boolean
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Selector de Zonas
        FilterDropdown(
            label = "Selecciona una Zona",
            options = homeData.zones.map { it.name },
            selectedOption = selectedZone?.name,
            onOptionSelected = { zoneName ->
                val zone = homeData.zones.firstOrNull { it.name == zoneName }
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
        FilterDropdown(
            label = "Selecciona un Shopping",
            options = if (selectedZone != null) {
                homeData.stores.filter { it.zoneId == selectedZone!!.id }.map { it.name }
            } else emptyList(),
            selectedOption = selectedStore?.name,
            onOptionSelected = { storeName ->
                val store = homeData.stores.firstOrNull { it.name == storeName }
                onStoreSelected(store)
            },
            enabled = selectedZone != null
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Agencias
        FilterDropdown(
            label = "Selecciona una Agencia",
            options = homeData.agencies.map { it.name },
            selectedOption = selectedAgency?.name,
            onOptionSelected = { agencyName ->
                val agency = homeData.agencies.firstOrNull { it.name == agencyName }
                onAgencySelected(agency)
                onUnitSelected(null)
            },
            enabled = selectedZone != null
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Hoteles
        FilterDropdown(
            label = "Selecciona un Hotel",
            options = if (selectedZone != null) {
                homeData.hotels.filter { it.zoneId == selectedZone!!.id }.map { it.name }
            } else emptyList(),
            selectedOption = selectedHotel?.name,
            onOptionSelected = { hotelName ->
                val hotel = homeData.hotels.firstOrNull { it.name == hotelName }
                onHotelSelected(hotel)
                val pickup = homeData.pickups.firstOrNull { it.hotelId == hotel?.id }
                onPickupSelected(pickup)
            },
            enabled = selectedZone != null
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Horario de Recogida
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
        // Selector de Unidades
        FilterDropdown(
            label = "Selecciona una Unidad",
            options = homeData.units.map { "${it.name} (${it.seatCount ?: "N/A"} asientos)" }, // Mostrar nombre y asientos disponibles
            selectedOption = selectedUnit?.let { "${it.name} (${it.seatCount ?: "N/A"} asientos)" },
            onOptionSelected = { option ->
                val unitName = option.substringBefore(" (") // Extraer solo el nombre de la unidad seleccionada
                val unitn = homeData.units.firstOrNull { it.name == unitName }
                onUnitSelected(unitn)
            },
            enabled = true // Siempre habilitado
        )



// Mostrar información de la unidad seleccionada (nombre y cantidad de asientos)
        selectedUnit?.let { unit ->
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
        }


        Spacer(modifier = Modifier.height(16.dp))

        // DatePicker
        DatePickerBox(
            selectedDate = selectedDate,
            onDateSelected = onDateChange
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Input de Pax
        OutlinedTextFieldPax(
            value = pax,
            onValueChange = { value ->
                onPaxChange(value)
                onAdultsEnabledChange(value.isNotEmpty())
                if (value.isEmpty()) {
                    onAdultsChange("")
                    onChildrenChange("")
                    onClientNameEnabledChange(false)
                } else {
                    val paxValue = value.toIntOrNull() ?: 0
                    val adultsValue = adults.toIntOrNull() ?: 0
                    onChildrenChange(calculateChildren(paxValue, adultsValue).toString())
                }
            },
            label = "Número de Asientos (Pax)",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Inputs de Adultos y Niños
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextFieldPax(
                value = adults,
                onValueChange = { value ->
                    onAdultsChange(value)
                    val paxValue = pax.toIntOrNull() ?: 0
                    val adultsValue = value.toIntOrNull() ?: 0
                    onChildrenChange(calculateChildren(paxValue, adultsValue).toString())
                    onClientNameEnabledChange(validatePax(adults, children, pax))
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

        // Input de Nombre del Cliente
        OutlinedTextFieldPax(
            value = clientName,
            onValueChange = onClientNameChange,
            label = "Nombre del Cliente",
            enabled = clientNameEnabled,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Botón para Buscar Disponibilidad
        Button(
            onClick = {
                if (selectedUnit != null && selectedPickup != null && selectedDate.isNotEmpty()) {
                    fetchAvailability(
                        selectedUnit!!.id,
                        selectedPickup!!.pickupTime,
                        selectedDate,
                        selectedHotel!!.id
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar Disponibilidad")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar Disponibilidad
        if (showAvailability && availabilityData != null) {
            availabilityData.data?.let { data ->
                AvailabilityCard(
                    unitName = data.unit.name,
                    totalSeats = data.totalSeats,
                    occupiedSeats = data.occupiedSeats,
                    pendingSeats = data.pendingSeats,
                    availableSeats = data.availableSeats
                )
                if (availabilityData?.data?.availableSeats ?: 0 > 0) {
                Button(
                    onClick = {
                        if (clientName.isNotEmpty() && selectedUnit != null && selectedPickup != null && selectedDate.isNotEmpty() && selectedHotel != null) {
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
                                    storeId = selectedStore!!.id
                                )
                            )
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
                                storeId = selectedStore!!.id
                            )

                        }else{
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se pudieron cargar los datos.")
                }
            }
    }
}}



// Función para calcular niños
fun calculateChildren(pax: Int, adults: Int): Int {
    return (pax - adults).coerceAtLeast(0)
}

// Función para validar Pax
fun validatePax(adults: String, children: String, pax: String): Boolean {
    val total = (adults.toIntOrNull() ?: 0) + (children.toIntOrNull() ?: 0)
    return total == (pax.toIntOrNull() ?: 0)
}


