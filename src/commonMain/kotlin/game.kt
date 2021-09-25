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

data class BoardOptions(val rows: Int, val columns: Int, val mines: Int)

class Board(val options: BoardOptions) {
    val cells = Array(options.rows) { row ->
        Array(options.columns) { column ->
            val hasBomb = row == column
            val isOpened = hasBomb
            Cell(hasBomb, isOpened)
        }
    }

    val rows: Int
        get() = options.rows;

    val columns: Int
        get() = options.columns

    fun cellAt(row: Int, column: Int) = cells.getOrNull(row)?.getOrNull(column)
}

class Cell(hasBomb: Boolean = false, isOpened: Boolean = false) {
    var hasBomb by mutableStateOf(hasBomb)
    var isOpened by mutableStateOf(isOpened)

    fun open() {
        isOpened = true
    }
}


@Composable
expect fun CellView(cell: Cell): Unit

@Composable
fun Game() = Column(Modifier.fillMaxWidth()) {
    val boardSettings = BoardOptions(
        rows = 8,
        columns = 8,
        mines = 10,
    )

    val board = Board(boardSettings)

    Column {
        for (row in 0 until board.rows) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (column in 0 until board.columns) {
                    val cell = board.cellAt(row, column)!!
                    CellView(cell)
                }
            }
        }
    }
}

