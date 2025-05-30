package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import com.bumble.appyx.components.backstack.BackStack

@Composable
fun textWidth(
    text: String,
    style: TextStyle,
): Dp {
    val textMeasurer = rememberTextMeasurer()
    val widthInPixels = textMeasurer.measure(text, style).size.width
    return with(LocalDensity.current) { widthInPixels.toDp() }
}

fun <NavTarget : Any> BackStack<NavTarget>.currentTarget(): NavTarget =
    this.model.output.value.currentTargetState.active.interactionTarget
