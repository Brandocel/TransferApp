package com.example.transferapp.ui.selection.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SeatGrid(
    totalSeats: Int,
    occupiedSeats: List<Int>?,
    pendingSeats: List<Int>?,
    selectedSeats: MutableList<Int>,
    maxSelectableSeats: Int,
    onUpdateSeat: () -> Unit
) {
    val seatsPerRow = 4
    val rows = (totalSeats / seatsPerRow) + if (totalSeats % seatsPerRow != 0) 1 else 0

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (rowIndex in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (colIndex in 0 until seatsPerRow) {
                    val seatNumber = rowIndex * seatsPerRow + colIndex + 1
                    if (seatNumber <= totalSeats) {
                        val seatLabel = "${('A' + rowIndex)}${colIndex + 1}"
                        SeatButton(
                            seatLabel = seatLabel,
                            seatNumber = seatNumber,
                            isOccupied = occupiedSeats?.contains(seatNumber) == true,
                            isPending = pendingSeats?.contains(seatNumber) == true,
                            isSelected = seatNumber in selectedSeats,
                            onSeatSelected = { selected ->
                                if (selected) {
                                    if (selectedSeats.size < maxSelectableSeats) {
                                        selectedSeats.add(seatNumber)
                                    }
                                } else {
                                    selectedSeats.remove(seatNumber)
                                }
                                onUpdateSeat()
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(50.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
