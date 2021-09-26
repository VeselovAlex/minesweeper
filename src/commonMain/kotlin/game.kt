package com.github.veselovalex.minesweeper

import androidx.compose.runtime.*
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.border
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

@Composable
expect fun OpenedCell(cell: Cell)

@Composable
expect fun CellWithIcon(src: String, alt: String)

@Composable
fun Mine() {
    CellWithIcon(src="assets/mine.png", alt = "Bomb")
}

@Composable
fun Flag() {
    CellWithIcon(src="assets/flag.png", alt = "Flag")
}

class GameStyles(
    val closedCellColor: Color,
    val openedCellColor: Color,
    val borderColor: Color
) {
    fun getCellColor(cell: Cell): Color {
        return if (cell.isOpened) {
            openedCellColor
        } else {
            closedCellColor
        }
    }
}

@Composable
expect fun ClickableCell(
    onLeftMouseButtonClick: () -> Unit,
    onRightMouseButtonClick: () -> Unit,
    content: @Composable () -> Unit
)

@Composable
fun BoardView(game: GameController, styles: GameStyles) {
    Column  {
        for (row in 0 until game.rows) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (column in 0 until game.columns) {
                    val cell = game.cellAt(row, column)!!

                    ClickableCell(
                        onLeftMouseButtonClick = { game.openCell(cell) },
                        onRightMouseButtonClick = { game.toggleFlag(cell) }
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp, 40.dp)
                                .background(styles.getCellColor(cell))
                                .border(1.dp, styles.borderColor)
                        ) {
                            if (cell.isOpened) {
                                if (cell.hasBomb) {
                                    Mine()
                                } else if (cell.bombsNear > 0) {
                                    OpenedCell(cell)
                                }
                            } else if (cell.isFlagged) {
                                Flag()
                            }
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

    val styles = GameStyles(
        openedCellColor = Color.White,
        closedCellColor = Color.DarkGray,
        borderColor = Color.LightGray
    )

    fun newGame(rows: Int, columns: Int, mines: Int) {
        game = GameController(
            options = GameSettings(rows, columns, mines),
            onWin,
            onLose
        )
        message = null
    }

    Column {
        Column {
            Box { Text("New Game") }
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
        }
        if (game != null) {
            Row {
                val bombsLeft = game?.let {
                    max(it.bombs - it.flagsSet, 0)
                }
                Box {
                    Text("Bombs: $bombsLeft")
                }
                Box {
                    Text("Seconds: ${game!!.seconds}")
                }
            }

            BoardView(game!!, styles)
            if (message != null) {
                Box {
                    Text(message!!)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                game?.apply {
                    onTimeTick(it)
                }
            }
        }
    }
}