package com.github.veselovalex.minesweeper

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.border
import org.jetbrains.compose.common.foundation.clickable
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.ui.background
import org.jetbrains.compose.common.ui.size
import org.jetbrains.compose.common.ui.unit.dp

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

    return Box(
        modifier = org.jetbrains.compose.common.ui.Modifier.size(40.dp, 40.dp)
            .background(color)
            .border(1.dp, Color(0xDD, 0xDD, 0xDD))
            .clickable { cell.open() }
    ) {
        if (cell.isOpened) {
            if (cell.hasBomb) {
                Image(
                    painter = painterResource("assets/mine.png"),
                    contentDescription = "Bomb",
                    modifier = Modifier.fillMaxSize().padding(all = Dp(4.0f))
                )
            } else if (cell.bombsNear > 0) {
                Text(
                    text = cell.bombsNear.toString(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}