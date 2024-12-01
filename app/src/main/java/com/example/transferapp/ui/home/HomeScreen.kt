package com.example.transferapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.transferapp.data.model.Agency
import com.example.transferapp.data.model.Hotel
import com.example.transferapp.data.model.Zone
import com.example.transferapp.data.model.Unit as ModelUnit // Alias para evitar conflictos
import com.example.transferapp.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel) {
    val isLoading by homeViewModel.isLoading.collectAsState()
    val homeData by homeViewModel.homeData.collectAsState()

    var selectedZone by remember { mutableStateOf<Zone?>(null) }
    var selectedAgency by remember { mutableStateOf<Agency?>(null) }
    var selectedHotel by remember { mutableStateOf<Hotel?>(null) }
    var selectedUnit by remember { mutableStateOf<ModelUnit?>(null) } // Cambio aquí

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
                DropdownMenuSelection(
                    label = "Selecciona una Zona",
                    items = data.zones,
                    selectedItem = selectedZone,
                    onItemSelected = { selectedZone = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                DropdownMenuSelection(
                    label = "Selecciona una Agencia",
                    items = if (selectedZone != null) data.agencies else emptyList(),
                    selectedItem = selectedAgency,
                    onItemSelected = { selectedAgency = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                DropdownMenuSelection(
                    label = "Selecciona un Hotel",
                    items = if (selectedZone != null) {
                        data.hotels.filter { it.zoneId == selectedZone!!.id }
                    } else emptyList(),
                    selectedItem = selectedHotel,
                    onItemSelected = { selectedHotel = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                DropdownMenuSelection(
                    label = "Selecciona una Unidad",
                    items = if (selectedAgency != null) {
                        data.units.filter { it.agencyId == selectedAgency!!.id }
                    } else emptyList(),
                    selectedItem = selectedUnit,
                    onItemSelected = { selectedUnit = it }
                )
            }
        }
    }
}

@Composable
fun <T> DropdownMenuSelection(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit
) where T : Any {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedItem?.toString() ?: "",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.toString()) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
