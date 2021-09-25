package com.github.veselovalex.minesweeper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.border
import org.jetbrains.compose.common.foundation.clickable
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.background
import org.jetbrains.compose.common.ui.size
import org.jetbrains.compose.common.ui.unit.dp

data class BoardOptions(val rows: Int, val columns: Int, val mines: Int)

enum class OpenResult { SUCCESS, BOMB_EXPLODED, NOTHING }

class Board(private val options: BoardOptions) {
    private val cells = Array(options.rows) { row ->
        Array(options.columns) { column ->
            val hasBomb = row == column
            Cell(row, column, this, hasBomb).apply {
                isFlagged = row == column + 1
                isOpened = hasBomb
            }
        }
    }

    val rows: Int
        get() = options.rows;

    val columns: Int
        get() = options.columns

    fun openCell(cell: Cell): OpenResult {
        if (cell.isOpened || cell.isFlagged) return OpenResult.NOTHING
        if (cell.hasBomb) return OpenResult.BOMB_EXPLODED

        cell.isOpened = true
        if (cell.bombsNear == 0) {
            neighborsOf(cell).forEach { if (!it.hasBomb) openCell(it) }
        }

        return OpenResult.SUCCESS
    }

    fun cellAt(row: Int, column: Int) = cells.getOrNull(row)?.getOrNull(column)

    fun neighborsOf(cell: Cell): List<Cell> = neighborsOf(cell.row, cell.column)

    private fun neighborsOf(row: Int, column: Int): List<Cell> {
        var result = mutableListOf<Cell>();
        cellAt(row - 1, column - 1)?.let { result.add(it) }
        cellAt(row - 1, column)?.let { result.add(it) }
        cellAt(row - 1, column + 1)?.let { result.add(it) }
        cellAt(row, column - 1)?.let { result.add(it) }
        cellAt(row, column + 1)?.let { result.add(it) }
        cellAt(row + 1, column - 1)?.let { result.add(it) }
        cellAt(row + 1, column)?.let { result.add(it) }
        cellAt(row + 1, column + 1)?.let { result.add(it) }

        return result
    }
}

class Cell(
    val row: Int,
    val column: Int,
    val board: Board,
    hasBomb: Boolean = false,
) {
    var hasBomb by mutableStateOf(hasBomb)
    var isOpened by mutableStateOf(false)
    var isFlagged by mutableStateOf(false)

    val bombsNear by lazy {
        board.neighborsOf(this).count { it.hasBomb }
    }

    fun open() {
        board.openCell(this)
    }
}

@Composable
expect fun OpenedCell(cell: Cell): Unit

@Composable
expect fun CellWithIcon(src: String, alt: String)


@Composable
fun Mine(cell: Cell) {
    CellWithIcon(src="assets/mine.png", alt = "Bomb")
}

@Composable
fun Flag(cell: Cell) {
    CellWithIcon(src="assets/flag.png", alt = "Flag")
}


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

                    val closedCellColor = Color.DarkGray
                    val openedCellColor = Color.White
                    val color = if (cell.isOpened) { openedCellColor } else { closedCellColor }

                    Box(
                        modifier = Modifier.size(40.dp, 40.dp)
                            .background(color)
                            .border(1.dp, Color(0xDD, 0xDD, 0xDD))
                            .clickable { cell.open() } // TODO Handle flag clicks
                    ) {
                        if (cell.isOpened) {
                            if (cell.hasBomb) {
                                Mine(cell)
                            } else if (cell.bombsNear > 0) {
                                OpenedCell(cell)
                            }
                        } else if (cell.isFlagged) {
                            Flag(cell)
                        }
                    }

                }
            }
        }
    }
}