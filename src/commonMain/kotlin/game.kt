package com.github.veselovalex.minesweeper

import androidx.compose.runtime.Composable
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.border
import org.jetbrains.compose.common.foundation.layout.*
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.background
import org.jetbrains.compose.common.ui.size
import org.jetbrains.compose.common.ui.unit.dp

data class Cell(val hasBomb: Boolean = false, var isOpened: Boolean = false)

@Composable
expect fun CellImage(cell: Cell): Unit

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

    val closedCellColor = Color.DarkGray
    val openedCellColor = Color.White

    Column {
        for (row in 0 until rows) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (column in 0 until columns) {
                    val cell = cells[row][column]
                    val color = if (cell.isOpened) { openedCellColor } else { closedCellColor }
                    Box(
                        modifier = Modifier.size(32.dp, 32.dp)
                            .background(color)
                            .border(1.dp, Color.White)
                    ) {
                        if (cell.isOpened) {
                            CellImage(cell)
                        }
                    }
                }
            }
        }
    }
}