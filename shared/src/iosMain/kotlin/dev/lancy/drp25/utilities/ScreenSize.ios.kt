package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectGetWidth
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun getScreenSize(): DpSize = remember {
    val screen = UIScreen.mainScreen
    DpSize(
        width = CGRectGetWidth(screen.bounds).dp,
        height = CGRectGetHeight(screen.bounds).dp
    )
}
