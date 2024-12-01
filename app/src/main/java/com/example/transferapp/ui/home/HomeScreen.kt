package com.example.transferapp.ui.home

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
import com.example.transferapp.data.model.Agency
import com.example.transferapp.data.model.Hotel
import com.example.transferapp.data.model.Zone
import com.example.transferapp.data.model.Unit as ModelUnit
import com.example.transferapp.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel) {
    val isLoading by homeViewModel.isLoading.collectAsState()
    val homeData by homeViewModel.homeData.collectAsState()

    var selectedZone by remember { mutableStateOf<Zone?>(null) }
    var selectedAgency by remember { mutableStateOf<Agency?>(null) }
    var selectedHotel by remember { mutableStateOf<Hotel?>(null) }
    var selectedUnit by remember { mutableStateOf<ModelUnit?>(null) }

    LaunchedEffect(Unit) {
        homeViewModel.fetchHomeData()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        homeData?.let { data ->
            Column(modifier = Modifier.padding(16.dp)) {
                // Selector de zonas
                FilterDropdown(
                    label = "Selecciona una Zona",
                    options = data.zones.map { it.name },
                    selectedOption = selectedZone?.name,
                    onOptionSelected = { zoneName ->
                        selectedZone = data.zones.firstOrNull { it.name == zoneName }
                        selectedAgency = null
                        selectedHotel = null
                        selectedUnit = null
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de agencias
                FilterDropdown(
                    label = "Selecciona una Agencia",
                    options = if (selectedZone != null) data.agencies.map { it.name } else emptyList(),
                    selectedOption = selectedAgency?.name,
                    onOptionSelected = { agencyName ->
                        selectedAgency = data.agencies.firstOrNull { it.name == agencyName }
                        selectedHotel = null
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
                        selectedUnit = null
                    },
                    enabled = selectedZone != null
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

                Spacer(modifier = Modifier.height(32.dp))

                // Mostrar datos seleccionados
                Text(
                    text = "Datos seleccionados:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = "Zona: ${selectedZone?.name ?: "No seleccionada"}")
                Text(text = "Agencia: ${selectedAgency?.name ?: "No seleccionada"}")
                Text(text = "Hotel: ${selectedHotel?.name ?: "No seleccionado"}")
                Text(text = "Unidad: ${selectedUnit?.name ?: "No seleccionada"}")
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
