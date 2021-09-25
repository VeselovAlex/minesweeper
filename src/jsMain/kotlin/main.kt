package com.github.veselovalex.minesweeper

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.ui.Styles
import org.jetbrains.compose.web.dom.Text

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
            onClick { cell.open() }
            style {
                property("background", if (cell.isOpened) { "orange" } else { "steelblue" })
                property("border", "1px solid white")
                property("padding", 3.px)
                fontSize(32.px)
                lineHeight("1")
                fontWeight("bold")
                fontFamily("sans-serif")
                textAlign("center")
                cursor("pointer")
                width(40.px)
                height(40.px)
                boxSizing("border-box")
            }
        }
    ) {
        if (cell.isOpened) {
            if (cell.hasBomb) {
                Img(src="assets/mine.png", alt = "Bomb")
            } else if (cell.bombsNear > 0) {
                Text(cell.bombsNear.toString())
            }
        }
    }
}