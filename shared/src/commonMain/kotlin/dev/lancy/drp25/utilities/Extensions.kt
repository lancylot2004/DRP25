package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.spotlight.Spotlight
import dev.lancy.drp25.ui.shared.NavTarget
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun textWidth(
    text: String,
    style: TextStyle,
): Dp {
    val textMeasurer = rememberTextMeasurer()
    val widthInPixels = textMeasurer.measure(text, style).size.width
    return with(LocalDensity.current) { widthInPixels.toDp() }
}

fun <T : NavTarget> BackStack<T>.currentTarget(): T =
    this.model.output.value.currentTargetState.active.interactionTarget

@Composable
fun <T: NavTarget> Spotlight<T>.selectedIndex(): Int =
    this.activeIndex.collectAsState().value.toInt()

fun ClosedFloatingPointRange<Float>.toIntString(): String {
    return if (ceil(start).toInt() <= floor(endInclusive).toInt()) {
        (ceil(start).toInt()..floor(endInclusive).toInt()).joinToString(", ", "(", ")")
    } else { "()" }
}
