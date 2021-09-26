package com.github.veselovalex.minesweeper

import androidx.compose.foundation.ExperimentalDesktopApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.mouseClickable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
    ) {
        MaterialTheme {
            Game()
        }
    }
}

@Composable
actual fun CellWithIcon(src: String, alt: String) {
    Image(
        painter = painterResource(src),
        contentDescription = alt,
        modifier = Modifier.fillMaxSize().padding(Dp(4.0f))
    )
}

@Composable
actual fun OpenedCell(cell: Cell) {
    Text(
        text = cell.bombsNear.toString(),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalDesktopApi::class)
@Composable
actual fun ClickableCell(
    onLeftMouseButtonClick: () -> Unit,
    onRightMouseButtonClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .mouseClickable {
                val lmb = buttons.isPrimaryPressed
                val rmb = buttons.isSecondaryPressed

                if (lmb && !rmb) {
                    onLeftMouseButtonClick()
                } else if (rmb && !lmb) {
                    onRightMouseButtonClick()
                }
            }
    ) {
        content()
    }
}