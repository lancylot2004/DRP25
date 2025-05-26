package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
actual fun getScreenSize(): DpSize = with(LocalConfiguration.current) {
    DpSize(this.screenWidthDp.dp, this.screenHeightDp.dp)
}
