package com.github.veselovalex.minesweeper

import androidx.compose.runtime.*
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.border
import org.jetbrains.compose.common.foundation.clickable
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.background
import org.jetbrains.compose.common.ui.size
import org.jetbrains.compose.common.ui.unit.dp
import kotlin.random.Random

data class BoardOptions(val rows: Int, val columns: Int, val mines: Int)

enum class OpenResultKind { SUCCESS, BOMB_EXPLODED, NOTHING }

data class OpenResult(val kind: OpenResultKind, val cellsOpened: Int = 0) {
    companion object {
        fun nothing() =  OpenResult(OpenResultKind.NOTHING)
        fun bombExploded() =  OpenResult(OpenResultKind.BOMB_EXPLODED, 1)
        fun opened(count: Int) =  OpenResult(OpenResultKind.SUCCESS, count)
    }
}



class Board(private val options: BoardOptions) {
    private val cells = Array(options.rows) { row ->
        Array(options.columns) { column ->
            Cell(row, column, this, false)
        }
    }

    init {
        for (i in 1..options.mines) {
            putBomb()
        }
    }

    private fun putBomb() {
        var cell: Cell
        do {
            // This strategy may create infinite loop, but for simplicity we can assume
            // that mine count is small enough
            val random = Random.nextInt(options.rows * options.columns)
            cell = cells[random % columns][random / columns]
        } while (cell.hasBomb)
        cell.hasBomb = true
    }

    val rows: Int
        get() = options.rows;

    val columns: Int
        get() = options.columns

    fun openCell(cell: Cell): OpenResult {
        if (cell.isOpened || cell.isFlagged) return OpenResult.nothing()

        cell.isOpened = true
        if (cell.hasBomb) return OpenResult.bombExploded()

        var openedCells = 1;
        if (cell.bombsNear == 0) {
            neighborsOf(cell).forEach {
                if (!it.hasBomb) {
                    openedCells += openCell(it).cellsOpened
                }
            }
        }

        return OpenResult.opened(openedCells)
    }

    fun flagAllClosedCells() {
        cells.forEach { row ->
            row.forEach { cell ->
                if (!cell.isOpened) {
                    cell.isFlagged = true
                }
            }
        }
    }

    fun openAllBombs() {
        cells.forEach { row ->
            row.forEach { cell ->
                if (cell.hasBomb && !cell.isFlagged) {
                    cell.isOpened = true
                }
            }
        }
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

    fun open() = board.openCell(this)
}

@Composable
expect fun OpenedCell(cell: Cell)

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
fun BoardView(
    settings: BoardOptions,
    onWin: () -> Unit,
    onLose: () -> Unit,
) {
    val board = remember { Board(settings) }
    var active = true
    var cellsToOpen = settings.rows * settings.columns - settings.mines
    var explodedCell by mutableStateOf<Cell?>(null);

    Column  {
        for (row in 0 until board.rows) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (column in 0 until board.columns) {
                    val cell = board.cellAt(row, column)!!

                    val closedCellColor = Color.DarkGray
                    val openedCellColor = Color.White
                    val explodedColor = Color.Red
                    val color = if (explodedCell == cell) {
                        explodedColor
                    } else if (cell.isOpened) {
                        openedCellColor
                    } else {
                        closedCellColor
                    }

                    Box(
                        modifier = Modifier.size(40.dp, 40.dp)
                            .background(color)
                            .border(1.dp, Color(0xDD, 0xDD, 0xDD))
                            .clickable {
                                // TODO Handle flag clicks
                                if (active) {
                                    val (kind, cellsOpened) = cell.open()
                                    when (kind) {
                                        OpenResultKind.BOMB_EXPLODED -> {
                                            active = false
                                            board.openAllBombs()
                                            explodedCell = cell
                                            onLose()
                                        }
                                        OpenResultKind.SUCCESS -> {
                                            cellsToOpen -= cellsOpened
                                            if (cellsToOpen == 0) {
                                                active = false
                                                board.flagAllClosedCells()
                                                onWin()
                                            }
                                        }
                                        else -> {}
                                    }
                                }
                            }
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


@Composable
fun Game() = Column(Modifier.fillMaxWidth()) {
    var message by remember { mutableStateOf<String?>(null) }
    val boardSettings = BoardOptions(
        rows = 8,
        columns = 8,
        mines = 10,
    )

    val onWin = { message = "You win!" }
    val onLose = { message = "Bad luck" }

    Column {
        BoardView(boardSettings, onWin, onLose)
        message?.let {
            Text(it)
        }
    }
}