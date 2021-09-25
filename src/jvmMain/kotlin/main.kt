package com.github.veselovalex.minesweeper

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.border
import org.jetbrains.compose.common.foundation.clickable
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.ui.background
import org.jetbrains.compose.common.ui.size
import org.jetbrains.compose.common.ui.unit.dp


@Composable
actual fun CellImage(cell: Cell): Unit {
    Image(
        painter = painterResource("mine.png"),
        contentDescription = "Bomb",
        modifier = Modifier.fillMaxSize()
    )
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication
    ) {
        MaterialTheme {
            Game()
        }
    }
}

@Composable
actual fun CellView(cell: Cell) {
    val closedCellColor = Color.DarkGray
    val openedCellColor = Color.White
    val color = if (cell.isOpened) { openedCellColor } else { closedCellColor }


    @OptIn(ExperimentalFoundationApi::class)
    return Box(
        modifier = org.jetbrains.compose.common.ui.Modifier.size(32.dp, 32.dp)
            .background(color)
            .border(1.dp, Color.White)
            .clickable {
                cell.isOpened = true
            }
    ) {
        if (cell.isOpened) {
            if (cell.hasBomb) {
                Image(
                    painter = painterResource("mine.png"),
                    contentDescription = "Bomb",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}