package com.github.veselovalex.minesweeper

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.web.renderComposable

@Composable
actual fun CellImage(cell: Cell): Unit {
    Text("*")
}

fun main() {
    val root = document.getElementById("app-root") ?: throw RuntimeException("#app-root is missing in index.html")

    renderComposable(root) {
        Game()
    }
}