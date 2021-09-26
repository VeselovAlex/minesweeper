package com.github.veselovalex.minesweeper

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.ui.Styles

fun main() {
    val root = document.getElementById("app-root") ?: throw RuntimeException("#app-root is missing in index.html")

    renderComposable(root) {
        Style(Styles)
        Game()
    }
}


@Composable
actual fun CellWithIcon(src: String, alt: String) {
    Img(src, alt, attrs = {
        style {
            property("user-select", "none")
            margin(4.px)
        }
    })
}

@Composable
actual fun OpenedCell(cell: Cell) {
    Div (
        attrs = {
            style {
                property("user-select", "none")
                fontSize(32.px)
                lineHeight("1")
                fontWeight("bold")
                fontFamily("sans-serif")
                textAlign("center")
                width(32.px)
                height(32.px)
                boxSizing("border-box")
                margin(4.px)
            }
        }
    ) {
        Text(cell.bombsNear.toString())
    }
}

@Composable
actual fun ClickableCell(
    onLeftMouseButtonClick: (isShiftPressed: Boolean) -> Unit,
    onRightMouseButtonClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Div (
        attrs = {
            onClick {
                it.preventDefault()
                onLeftMouseButtonClick(it.shiftKey)
            }
            onContextMenu {
                // Handle right mouse button click
                // Disable default context menu
                it.preventDefault()
                onRightMouseButtonClick()
            }
            style {
                cursor("pointer")
            }
        }
    ) {
        content()
    }
}