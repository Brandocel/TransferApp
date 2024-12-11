package com.example.transferapp.ui.selection.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transferapp.R

@Composable
fun SeatButton(
    seatLabel: String,
    seatNumber: Int,
    isOccupied: Boolean,
    isPending: Boolean,
    isSelected: Boolean,
    onSeatSelected: (Boolean) -> Unit
) {
    val seatState = when {
        isOccupied -> R.drawable.reservado
        isPending -> R.drawable.pendiente
        isSelected -> R.drawable.seleccionado
        else -> R.drawable.disponible
    }

    val isClickable = !isOccupied && !isPending

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(70.dp)
            .clickable(enabled = isClickable) {
                if (isClickable) {
                    onSeatSelected(!isSelected)
                }
            }
    ) {
        Image(
            painter = painterResource(id = seatState),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Text(
            text = seatLabel,
            fontSize = 14.sp
        )
    }
}








