package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize

@Composable
internal expect fun getScreenSize(): DpSize

val ScreenSize: DpSize
    @Composable get() = getScreenSize()
