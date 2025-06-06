package dev.lancy.drp25.utilities

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeStyle

object Size {
    /** *Final* Typical padding value. */
    val Padding = 8.dp

    /** *Final* Big padding value. Usually used between prominent containers or against the edge of the screen. */
    val BigPadding = 16.dp

    /** *Final* Typical spacing value. */
    val Spacing = 4.dp

    /** Thickness of larger bars, such as the bottom navigation bar. */
    val BarLarge = 70.dp

    /** Thickness of medium bars, such as the trailing action bar. */
    val BarMedium = 50.dp

    /** Thickness of small bars, such as the hazed navigation bar. */
    val BarSmall = 35.dp

    /** Height of medium buttons, such as those in the onboarding process. */
    val ButtonMedium = 50.dp

    /** Typical corner radius, small. */
    val CornerSmall = 4.dp

    /** Typical corner radius, small. */
    val CornerMedium = 8.dp

    /** *Final* Typical corner radius, large. */
    val CornerLarge = 20.dp

    /** Typical icon size, small. */
    val IconSmall = 23.dp

    /** Typical icon size, medium. */
    val IconMedium = 30.dp

    /** Typical icon size, large. */
    val IconLarge = 40.dp

    /** The percentage of screen height modal sheets should be. */
    const val ModalSheetHeight = 0.8f
}

object Shape {
    /** Typical rounded-corner shape, small. */
    val RoundedSmall = RoundedCornerShape(Size.CornerSmall)

    /** Typical rounded-corner shape, large. */
    val RoundedMedium = RoundedCornerShape(Size.CornerMedium)

    /** Typical rounded-corner shape, large. */
    val RoundedLarge = RoundedCornerShape(Size.CornerLarge)

    /** Full-corner shape. */
    val RoundedFull = RoundedCornerShape(100)
}

object Animation {
    fun <T> short(): AnimationSpec<T> = tween(50, 100)

    fun <T> medium(): AnimationSpec<T> = tween(300, 100)
}

object Const {
    val HazeStyle
        @Composable get() = HazeStyle(ColourScheme.background.copy(alpha = 0.5f), 5.dp, 0.2f)
}

val Typography: Typography
    @Composable get() = MaterialTheme.typography

val ColourScheme: ColorScheme
    @Composable get() = MaterialTheme.colorScheme
