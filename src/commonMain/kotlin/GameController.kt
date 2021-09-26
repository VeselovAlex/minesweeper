package com.github.veselovalex.minesweeper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

class GameController(private val options: GameSettings, private val onWin: (() -> Unit)? = null, private val onLose: (() -> Unit)? = null) {
    val rows: Int
        get() = options.rows
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

    constructor(
        rows: Int,
        columns: Int,
        mines: Collection<Pair<Int, Int>>,
        onWin: (() -> Unit)? = null,
        onLose: (() -> Unit)? = null
    ) : this(GameSettings(rows, columns, mines.size), onWin, onLose)  {
        for (row in cells) {
            for (cell in row) {
                cell.hasBomb = false
                cell.bombsNear = 0
            }
        }

        for ((row, column) in mines) {
            cellAt(row, column)?.apply {
                hasBomb = true
                neighborsOf(this).forEach {
                    it.bombsNear += 1
                }
            }
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
        if (cellsToOpen == 0) {
            win()
            return
        }

        if (cell.bombsNear == 0) {
            neighborsOf(cell).forEach {
                openCell(it)
            }
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
        val result = mutableListOf<Cell>()
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
        onWin?.invoke()
    }

    private fun lose() {
        endGame()
        openAllBombs()
        onLose?.invoke()
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

    override fun toString(): String {
        return buildString {
            for (row in cells) {
                for (cell in row) {
                    if (cell.hasBomb) {
                        append('*')
                    } else if (cell.isFlagged) {
                        append('!')
                    } else if (cell.bombsNear > 0) {
                        append(cell.bombsNear)
                    } else {
                        append(' ')
                    }
                }
                append('\n')
            }
            deleteAt(length - 1)
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