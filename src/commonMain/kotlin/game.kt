package com.github.veselovalex.minesweeper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.border
import org.jetbrains.compose.common.foundation.layout.*
import org.jetbrains.compose.common.material.Button
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.background
import org.jetbrains.compose.common.ui.size
import org.jetbrains.compose.common.ui.unit.dp

class Cell(hasBomb: Boolean = false, isOpened: Boolean = false) {
    var hasBomb by mutableStateOf(hasBomb)
    var isOpened by mutableStateOf(isOpened)
}


@Composable
expect fun CellImage(cell: Cell): Unit

@Composable
expect fun CellView(cell: Cell): Unit

@Composable
fun Game() = Column(Modifier.fillMaxWidth()) {
    val rows = 8
    val columns = 8

    val cells = Array(rows) { row ->
        Array(columns) { column ->
            val hasBomb = row == column
            val isOpened = hasBomb
            Cell(hasBomb, isOpened)
        }
    }

    Column {
        for (row in 0 until rows) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (column in 0 until columns) {
                    val cell = cells[row][column]
                    CellView(cell)
                }
            }
        }
    }
}

