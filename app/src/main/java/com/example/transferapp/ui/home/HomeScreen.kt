package com.example.transferapp.ui.home

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.transferapp.data.model.*
import com.example.transferapp.data.model.Unit as ModelUnit
import com.example.transferapp.viewmodel.HomeViewModel
import java.util.Calendar
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel) {
    val isLoading by homeViewModel.isLoading.collectAsState()
    val homeData by homeViewModel.homeData.collectAsState()

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

    val calendar = Calendar.getInstance()

    LaunchedEffect(Unit) {
        homeViewModel.fetchHomeData()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        homeData?.let { data ->
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Habilitar desplazamiento
            ) {
                // Selector de zonas
                FilterDropdown(
                    label = "Selecciona una Zona",
                    options = data.zones.map { it.name },
                    selectedOption = selectedZone?.name,
                    onOptionSelected = { zoneName ->
                        selectedZone = data.zones.firstOrNull { it.name == zoneName }
                        selectedStore = null
                        selectedAgency = null
                        selectedHotel = null
                        selectedPickup = null
                        selectedUnit = null
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de tiendas
                FilterDropdown(
                    label = "Selecciona una Tienda",
                    options = if (selectedZone != null) {
                        data.stores.filter { it.zoneId == selectedZone!!.id }.map { it.name }
                    } else emptyList(),
                    selectedOption = selectedStore?.name,
                    onOptionSelected = { storeName ->
                        selectedStore = data.stores.firstOrNull { it.name == storeName }
                    },
                    enabled = selectedZone != null
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de agencias
                FilterDropdown(
                    label = "Selecciona una Agencia",
                    options = data.agencies.map { it.name },
                    selectedOption = selectedAgency?.name,
                    onOptionSelected = { agencyName ->
                        selectedAgency = data.agencies.firstOrNull { it.name == agencyName }
                        selectedUnit = null
                    },
                    enabled = selectedZone != null
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de hoteles
                FilterDropdown(
                    label = "Selecciona un Hotel",
                    options = if (selectedZone != null) {
                        data.hotels.filter { it.zoneId == selectedZone!!.id }.map { it.name }
                    } else emptyList(),
                    selectedOption = selectedHotel?.name,
                    onOptionSelected = { hotelName ->
                        selectedHotel = data.hotels.firstOrNull { it.name == hotelName }
                        selectedPickup = data.pickups.firstOrNull { it.hotelId == selectedHotel?.id }
                    },
                    enabled = selectedZone != null
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar el horario de recogida (Pickup)
                OutlinedTextField(
                    value = selectedPickup?.pickupTime ?: "Sin horario de recogida",
                    onValueChange = {}, // No se permite edición
                    label = { Text("Horario de Recogida") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de unidades
                FilterDropdown(
                    label = "Selecciona una Unidad",
                    options = if (selectedAgency != null) {
                        data.units.filter { it.agencyId == selectedAgency!!.id }.map { it.name }
                    } else emptyList(),
                    selectedOption = selectedUnit?.name,
                    onOptionSelected = { unitName ->
                        selectedUnit = data.units.firstOrNull { it.name == unitName }
                    },
                    enabled = selectedAgency != null
                )

                Spacer(modifier = Modifier.height(16.dp))

                // DatePicker para seleccionar fecha
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val datePickerDialog = DatePickerDialog(
                                navController.context,
                                { _, year, month, dayOfMonth ->
                                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            // Configura la fecha mínima (día siguiente)
                            datePickerDialog.datePicker.minDate = calendar.timeInMillis + (24 * 60 * 60 * 1000) // Añade 1 día
                            datePickerDialog.show()
                        }
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (selectedDate.isEmpty()) "Selecciona una Fecha" else selectedDate,
                        color = if (selectedDate.isEmpty()) Color.Gray else Color.Black
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Input de Pax
                OutlinedTextField(
                    value = pax,
                    onValueChange = {
                        pax = it.filter { char -> char.isDigit() }
                        adultsEnabled = pax.isNotEmpty()
                        if (pax.isEmpty()) {
                            adults = ""
                            children = ""
                            clientNameEnabled = false
                        } else {
                            val paxValue = pax.toIntOrNull() ?: 0
                            val adultsValue = adults.toIntOrNull() ?: 0
                            children = calculateChildren(paxValue, adultsValue).toString()
                        }
                    },
                    label = { Text("Número de Asientos (Pax)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Inputs de adultos y niños
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = adults,
                        onValueChange = {
                            adults = it.filter { char -> char.isDigit() }
                            val paxValue = pax.toIntOrNull() ?: 0
                            val adultsValue = adults.toIntOrNull() ?: 0
                            children = calculateChildren(paxValue, adultsValue).toString()
                            clientNameEnabled = validatePax(adults, children, pax)
                        },
                        label = { Text("Adultos") },
                        enabled = adultsEnabled,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = children,
                        onValueChange = {}, // No permite edición
                        label = { Text("Niños (Automático)") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input de nombre del cliente
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nombre del Cliente") },
                    enabled = clientNameEnabled,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Mostrar datos seleccionados
                Text(
                    text = "Datos seleccionados:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = "Zona: ${selectedZone?.name ?: "No seleccionada"}")
                Text(text = "Tienda: ${selectedStore?.name ?: "No seleccionada"}")
                Text(text = "Agencia: ${selectedAgency?.name ?: "No seleccionada"}")
                Text(text = "Hotel: ${selectedHotel?.name ?: "No seleccionado"}")
                Text(text = "Horario de Recogida: ${selectedPickup?.pickupTime ?: "No seleccionado"}")
                Text(text = "Unidad: ${selectedUnit?.name ?: "No seleccionada"}")
                Text(text = "Fecha: ${if (selectedDate.isEmpty()) "No seleccionada" else selectedDate}")
            }
        } ?: run {
            Text(
                text = "No se pudieron cargar los datos.",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// Función de validación
fun validatePax(adults: String, children: String, pax: String): Boolean {
    val total = (adults.toIntOrNull() ?: 0) + (children.toIntOrNull() ?: 0)
    return total == (pax.toIntOrNull() ?: 0)
}

// Función para calcular el número de niños
fun calculateChildren(pax: Int, adults: Int): Int {
    return (pax - adults).coerceAtLeast(0) // Asegura que no sea negativo
}

@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .clickable(enabled = enabled) { expanded = true }
                .padding(8.dp)
        ) {
            Text(
                text = selectedOption ?: "Seleccionar $label",
                color = if (enabled) Color.Black else Color.Gray
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onOptionSelected(option)
                    },
                    text = { Text(option) }
                )
            }
        }
    }
}
