package com.example.transferapp.ui.home.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OutlinedTextFieldPax(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isNumeric: Boolean = false // Nuevo parámetro opcional para restringir entrada a números
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            onValueChange(if (isNumeric) input.filter { it.isDigit() } else input)
        },
        label = { Text(label) },
        enabled = enabled,
        readOnly = readOnly,
        modifier = modifier
    )
}
