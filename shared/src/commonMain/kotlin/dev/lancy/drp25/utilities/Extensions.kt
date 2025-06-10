package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isSpecified
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.spotlight.Spotlight
import dev.lancy.drp25.ui.shared.NavTarget
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun TextStyle.height(): Dp {
    val density = LocalDensity.current
    val lineHeight = lineHeight.takeIf { it.isSpecified } ?: fontSize
    return with(density) { lineHeight.toDp() }
}

fun <T> ClosedFloatingPointRange<T>.intSteps(): Int where T : Comparable<T>, T : Number =
    endInclusive.toDouble().toInt() - start.toDouble().toInt() - 1

fun <T : NavTarget> BackStack<T>.currentTarget(): T =
    this.model.output.value.currentTargetState.active.interactionTarget

@Composable
fun <T : NavTarget> Spotlight<T>.selectedIndex(): Int =
    this.activeIndex
        .collectAsState()
        .value
        .toInt()

fun ClosedFloatingPointRange<Float>.toIntString(): String = if (ceil(start).toInt() <= floor(endInclusive).toInt()) {
    (ceil(start).toInt()..floor(endInclusive).toInt()).joinToString(", ", "(", ")")
} else {
    "()"
}

fun <T> identity(it: T): T = it
