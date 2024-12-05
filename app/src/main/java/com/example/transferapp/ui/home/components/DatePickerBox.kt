package com.example.transferapp.ui.home.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerBox(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current // Obtiene el contexto dentro de un @Composable
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val calendar = remember { Calendar.getInstance() }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .clickable {
                // Crea el DatePickerDialog
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        onDateSelected(dateFormatter.format(calendar.time))
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.datePicker.minDate = calendar.timeInMillis + (24 * 60 * 60 * 1000) // Mínimo un día después
                datePickerDialog.show()
            }
            .padding(16.dp)
    ) {
        Text(
            text = if (selectedDate.isEmpty()) "Selecciona una Fecha" else selectedDate,
            color = if (selectedDate.isEmpty()) Color.Gray else Color.Black
        )
    }
}
