package com.github.veselovalex.minesweeper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

class GameController(private val options: GameSettings, private val onWin: () -> Unit, private val onLose: () -> Unit) {
    val rows: Int
        get() = options.rows;
    val columns: Int
        get() = options.columns
    val bombs: Int
        get() = options.mines
    var running by mutableStateOf(false)
        private set
    var finished by mutableStateOf(false)
        private set
    var flagsSet by mutableStateOf(0)
        private set
    var cellsToOpen by mutableStateOf(options.rows * options.columns - options.mines)
        private set
    var seconds by mutableStateOf(0)
        private set

    private var time = 0L
    private var startTime = 0L

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
        if (finished || cell.isOpened || cell.isFlagged) return
        if (!running) {
            startGame()
        }

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
        if (finished || cell.isOpened) return
        if (!running) {
            startGame()
        }

        cell.isFlagged = !cell.isFlagged
        if (cell.isFlagged) {
            flagsSet += 1
        } else {
            flagsSet -= 1
        }
    }

    fun onTimeTick(timeMillis: Long) {
        time = timeMillis
        if (running) {
            seconds = ((time - startTime) / 1000L).toInt()
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
        finished = true
        running = false
    }

    private fun startGame() {
        if (!finished) {
            seconds = 0
            startTime = time
            running = true
        }
    }
}

data class GameSettings(val rows: Int, val columns: Int, val mines: Int)

class Cell(val row: Int, val column: Int) {
    var hasBomb = false
    var isOpened by mutableStateOf(false)
    var isFlagged by mutableStateOf(false)
    var bombsNear = 0
}