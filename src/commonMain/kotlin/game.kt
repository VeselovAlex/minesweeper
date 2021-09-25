package com.github.veselovalex.minesweeper.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.border
import org.jetbrains.compose.common.foundation.layout.*
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.background
import org.jetbrains.compose.common.ui.size
import org.jetbrains.compose.common.ui.unit.dp

@Composable
fun Game() = Column(Modifier.fillMaxWidth()) {
    val rows = 8
    val columns = 8

    Column {
        for (row in 1..rows) {
            Row {
                for (column in 1..columns) {
                    Box(
                        modifier = Modifier.size(32.dp, 32.dp)
                            .background(Color.DarkGray)
                            .border(1.dp, Color.White)
                    ) {
                        // No content
                    }
                }
            }
        }
    }
}