package com.github.veselovalex.minesweeper

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.ui.Styles
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width

@Composable
actual fun CellImage(cell: Cell): Unit {
    Text("*")
}

fun main() {
    val root = document.getElementById("app-root") ?: throw RuntimeException("#app-root is missing in index.html")

    renderComposable(root) {
        Style(Styles)
        Game()
    }
}

@Composable
actual fun CellView(cell: Cell) {
    Div (
        attrs = {
            onClick { cell.isOpened = !cell.isOpened }
            style {
                property("background", if (cell.isOpened) { "orange" } else { "steelblue" })
                property("border", "1px solid white")
                width(32.px)
                height(32.px)
            }
        }
    ) {
        if (cell.isOpened) {
            if (cell.hasBomb) {
                Img(src="mine.png", alt = "Bomb")
            }
        }
    }
}