package dev.lancy.drp25.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.PI

object Rating {

    // Chef Hat Rating
    @Composable
    fun ChefHatRating(
        rating: Double,
        maxRating: Int = 5,
        textStyle: TextStyle = androidx.compose.ui.text.TextStyle(),
        textColor: Color = Color.White,
        modifier: Modifier = Modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            repeat(maxRating) { index ->
                ChefHat(
                    filled = index < rating.toInt(),
                    modifier = Modifier.size(16.dp)
                )
                if (index < maxRating - 1) Spacer(modifier = Modifier.width(2.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${rating.round(1)} / $maxRating",
                style = textStyle,
                color = textColor
            )
        }
    }

    @Composable
    private fun ChefHat(filled: Boolean, modifier: Modifier = Modifier) {
        Canvas(modifier = modifier) {
            val color = if (filled) Color(0xFFFF6B35) else Color(0x66FFFFFF)

            // Draw chef hat base (band)
            drawRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.65f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.7f, size.height * 0.35f)
            )

            // Draw chef hat top (puffy part)
            drawCircle(
                color = color,
                radius = size.width * 0.35f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.4f)
            )

            // Add small highlight for better visibility
            if (filled) {
                drawCircle(
                    color = Color(0x33FFFFFF),
                    radius = size.width * 0.15f,
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.35f, size.height * 0.25f)
                )
            }
        }
    }

    // Colored Star Rating
    @Composable
    fun ColoredStarRating(
        rating: Double,
        maxRating: Int = 5,
        textStyle: TextStyle = androidx.compose.ui.text.TextStyle(),
        textColor: Color = Color.White,
        modifier: Modifier = Modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            repeat(maxRating) { index ->
                ColoredStar(
                    filled = index < rating.toInt(),
                    halfFilled = index == rating.toInt() && rating % 1 >= 0.5,
                    modifier = Modifier.size(24.dp) // increased from 16.dp
                )
                if (index < maxRating - 1) Spacer(modifier = Modifier.width(2.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${rating.round(1)} / $maxRating",
                style = textStyle,
                color = textColor
            )
        }
    }

    @Composable
    private fun ColoredStar(filled: Boolean, halfFilled: Boolean = false, modifier: Modifier = Modifier) {
        Canvas(modifier = modifier) {
            val primaryColor = Color(0xFFFFD700) // Gold
            val secondaryColor = Color(0x66FFFFFF) // Semi-transparent white

            // Draw 5-pointed star
            val path = Path()
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val outerRadius = size.width * 0.4f
            val innerRadius = outerRadius * 0.4f

            for (i in 0 until 10) {
                val angle = (i * PI / 5.0).toFloat()
                val radius = if (i % 2 == 0) outerRadius else innerRadius
                val x = centerX + radius * cos(angle - PI.toFloat() / 2f)
                val y = centerY + radius * sin(angle - PI.toFloat() / 2f)

                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()

            // Draw based on fill state
            val color = when {
                filled -> primaryColor
                halfFilled -> primaryColor.copy(alpha = 0.7f)
                else -> secondaryColor
            }

            drawPath(path, color)
        }
    }

    // Flame Rating
    @Composable
    fun FlameRating(
        rating: Double,
        maxRating: Int = 5,
        textStyle: TextStyle = androidx.compose.ui.text.TextStyle(),
        textColor: Color = Color.White,
        modifier: Modifier = Modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            repeat(maxRating) { index ->
                Flame(
                    filled = index < rating.toInt(),
                    modifier = Modifier.size(16.dp)
                )
                if (index < maxRating - 1) Spacer(modifier = Modifier.width(2.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${rating.round(1)} / $maxRating",
                style = textStyle,
                color = textColor
            )
        }
    }

    @Composable
    private fun Flame(filled: Boolean, modifier: Modifier = Modifier) {
        Canvas(modifier = modifier) {
            val color = if (filled) Color(0xFFFF4444) else Color(0x66FFFFFF)

            // Draw flame shape
            val path = Path().apply {
                moveTo(size.width * 0.5f, size.height * 0.9f)
                cubicTo(
                    size.width * 0.2f, size.height * 0.7f,
                    size.width * 0.1f, size.height * 0.4f,
                    size.width * 0.3f, size.height * 0.2f
                )
                cubicTo(
                    size.width * 0.4f, size.height * 0.3f,
                    size.width * 0.5f, size.height * 0.1f,
                    size.width * 0.6f, size.height * 0.0f
                )
                cubicTo(
                    size.width * 0.8f, size.height * 0.2f,
                    size.width * 0.9f, size.height * 0.4f,
                    size.width * 0.7f, size.height * 0.7f
                )
                close()
            }
            drawPath(path, color)

            // Add inner flame for better visual effect when filled
            if (filled) {
                val innerPath = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.8f)
                    cubicTo(
                        size.width * 0.35f, size.height * 0.6f,
                        size.width * 0.3f, size.height * 0.4f,
                        size.width * 0.45f, size.height * 0.25f
                    )
                    cubicTo(
                        size.width * 0.5f, size.height * 0.35f,
                        size.width * 0.55f, size.height * 0.15f,
                        size.width * 0.6f, size.height * 0.1f
                    )
                    cubicTo(
                        size.width * 0.7f, size.height * 0.3f,
                        size.width * 0.75f, size.height * 0.5f,
                        size.width * 0.65f, size.height * 0.65f
                    )
                    close()
                }
                drawPath(innerPath, Color(0xFFFFAA00)) // Orange inner flame
            }
        }
    }

    // Extension function for rounding
    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }
}