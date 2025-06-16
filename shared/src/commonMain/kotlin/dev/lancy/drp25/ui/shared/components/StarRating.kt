package dev.lancy.drp25.ui.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import dev.lancy.drp25.utilities.Size

@Composable
fun StarRating(rating: Float, modifier: Modifier = Modifier, starSize: Dp = Size.IconSmall) {
    val clamped = rating.coerceIn(0f, 5f)

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { index ->
            val fillRatio = (clamped - index).coerceIn(0f, 1f)

            Box(modifier = Modifier.size(starSize)) {
                // Background empty star
                if (fillRatio == 1f) {
                    Icon(
                        imageVector = Lucide.Star,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // Foreground gold star clipped to fillRatio
                Icon(
                    imageVector = Lucide.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RectangleShape)
                        .graphicsLayer {
                            clip = true
                            shape = RectangleShape
                        }.drawWithContent {
                            clipRect(right = size.width * fillRatio) {
                                this@drawWithContent.drawContent()
                            }
                        },
                )
            }
        }
    }
}
