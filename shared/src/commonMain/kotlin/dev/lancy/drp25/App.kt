package dev.lancy.drp25

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

private val theme = darkColorScheme()

@Composable
@Preview
fun App() {
    MaterialTheme(colorScheme = theme) {
        Text("Yo let him cook")
    }
}
