package com.github.veselovalex.minesweeper

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.veselovalex.minesweeper.common.Game

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication
    ) {
        MaterialTheme {
            Game()
        }
    }
}