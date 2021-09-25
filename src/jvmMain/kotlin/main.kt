package com.github.veselovalex.minesweeper

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


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