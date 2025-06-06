package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.spotlight.Spotlight
import dev.lancy.drp25.data.Ingredient
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.data.Step
import dev.lancy.drp25.ui.shared.NavTarget
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

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
