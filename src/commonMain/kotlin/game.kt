package com.github.veselovalex.minesweeper

import androidx.compose.runtime.*
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.border
import org.jetbrains.compose.common.foundation.clickable
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.material.Button
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.background
import org.jetbrains.compose.common.ui.size
import org.jetbrains.compose.common.ui.unit.dp
import kotlin.math.max
import kotlin.random.Random

data class GameSettings(val rows: Int, val columns: Int, val mines: Int)

class Cell(val row: Int, val column: Int) {
    var hasBomb = false
    var isOpened by mutableStateOf(false)
    var isFlagged by mutableStateOf(false)
    var bombsNear = 0
}

class GameController(private val options: GameSettings, private val onWin: () -> Unit, private val onLose: () -> Unit) {
    val rows: Int
        get() = options.rows;
    val columns: Int
        get() = options.columns
    val bombs: Int
        get() = options.mines
    var running by mutableStateOf(true)
        private set
    var flagsSet by mutableStateOf(0)
        private set
    var cellsToOpen by mutableStateOf(options.rows * options.columns - options.mines)
        private set

    private val cells = Array(options.rows) { row ->
        Array(options.columns) { column ->
            Cell(row, column)
        }
    }

    init {
        for (i in 1..options.mines) {
            putBomb()
        }
    }

    fun cellAt(row: Int, column: Int) = cells.getOrNull(row)?.getOrNull(column)

    fun openCell(cell: Cell) {
        if (!running || cell.isOpened || cell.isFlagged) return

        cell.isOpened = true
        if (cell.hasBomb) {
            lose()
            return
        }
        cellsToOpen -= 1

        if (cell.bombsNear == 0) {
            neighborsOf(cell).forEach {
                openCell(it)
            }
        }

        if (cellsToOpen == 0) {
            win()
        }
    }

    fun toggleFlag(cell: Cell) {
        if (!running || cell.isOpened) return

        cell.isFlagged = !cell.isFlagged
        if (cell.isFlagged) {
            flagsSet += 1
        } else {
            flagsSet -= 1
        }
    }

    private fun putBomb() {
        var cell: Cell
        do {
            // This strategy may create infinite loop, but for simplicity we can assume
            // that mine count is small enough
            val random = Random.nextInt(options.rows * options.columns)
            cell = cells[random / columns][random % columns]
        } while (cell.hasBomb)

        cell.hasBomb = true
        neighborsOf(cell).forEach {
            it.bombsNear += 1
        }
    }

    private fun flagAllBombs() {
        cells.forEach { row ->
            row.forEach { cell ->
                if (!cell.isOpened) {
                    cell.isFlagged = true
                }
            }
        }
    }

    private fun openAllBombs() {
        cells.forEach { row ->
            row.forEach { cell ->
                if (cell.hasBomb && !cell.isFlagged) {
                    cell.isOpened = true
                }
            }
        }
    }

    private fun neighborsOf(cell: Cell): List<Cell> = neighborsOf(cell.row, cell.column)

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

    private fun win() {
        endGame()
        flagAllBombs()
        onWin()
    }

    private fun lose() {
        endGame()
        openAllBombs()
        onLose()
    }

    private fun endGame() {
        running = false
    }
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
fun BoardView(game: GameController) {
    Column  {
        for (row in 0 until game.rows) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (column in 0 until game.columns) {
                    val cell = game.cellAt(row, column)!!

                    val closedCellColor = Color.DarkGray
                    val openedCellColor = Color.White
                    val color = if (cell.isOpened) {
                        openedCellColor
                    } else {
                        closedCellColor
                    }

                    Box(
                        modifier = Modifier.size(40.dp, 40.dp)
                            .background(color)
                            .border(1.dp, Color(0xDD, 0xDD, 0xDD))
                            .clickable {
                                game.openCell(cell)
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
    var game by remember { mutableStateOf<GameController?>(null) }

    val onWin = { message = "You win!" }
    val onLose = { message = "Try again" }


    fun newGame(rows: Int, columns: Int, mines: Int) {
        game = GameController(
            options = GameSettings(rows, columns, mines),
            onWin,
            onLose
        )
    }

    Column {
        Row {
            val bombsLeft = game?.let {
                max(it.bombs - it.flagsSet, 0)
            }
            Box {
                Text("Bombs: ${bombsLeft}")
            }
            Row {
                Button(onClick = { newGame(9, 9, 10) }) {
                    Text("Easy")
                }
                Button(onClick = { newGame(16, 16, 40) }) {
                    Text("Medium")
                }
                Button(onClick = { newGame(16, 30, 99) }) {
                    Text("Expert")
                }
            }
            Box {
                Text("Seconds: ${0}")
            }
        }
        if (game != null) {
            BoardView(game!!)
        }
        if (message != null) {
            Text(message!!)
        }
    }
}