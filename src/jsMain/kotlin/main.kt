package com.github.veselovalex.minesweeper

import com.github.veselovalex.minesweeper.common.Game
import kotlinx.browser.document
import org.jetbrains.compose.web.renderComposable

fun main() {
    val root = document.getElementById("app-root") ?: throw RuntimeException("#app-root is missing in index.html")

    renderComposable(root) {
        Game()
    }
}