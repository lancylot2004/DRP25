package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.ui.components.Rating
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.math.round

// Lucide icons
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Carrot
import com.composables.icons.lucide.ChefHat
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.Zap

import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode

class FeedCard(
    nodeContext: NodeContext,
    private val recipe: Recipe,
) : LeafNode(nodeContext) {

    @Composable
    override fun Content(modifier: Modifier) {
        val hazeState = remember { HazeState() }

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    start = Size.BigPadding,
                    top = Size.BigPadding,
                    end = Size.BigPadding,
                    bottom = Size.BigPadding + 56.dp
                )
                .clip(Shape.RoundedLarge)
                .border(2.dp, ColourScheme.onBackground, Shape.RoundedLarge)
        ) {
            // Background image
            KamelImage(
                resource = asyncPainterResource(recipe.imageURL ?: "https://i.ytimg.com/vi/LOXyOlLUX_A/hqdefault.jpg"),
                contentDescription = recipe.name,
                modifier = Modifier.fillMaxSize().haze(state = hazeState),
                contentScale = ContentScale.Crop,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.6f to Color.Transparent,
                                0.85f to Color.Black
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f / 2.5f)
                    .align(Alignment.BottomStart)
                    .padding(horizontal = Size.Padding, vertical = Size.BarLarge),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    recipe.name,
                    style = Typography.titleMedium,
                    color = ColourScheme.onBackground
                )

                Rating.ColoredStarRating(
                    rating = recipe.rating,
                    textStyle = Typography.titleSmall,
                    textColor = ColourScheme.onBackground,
                    modifier = Modifier
                        .padding(vertical = Size.CornerSmall)
                        .height(24.dp)
                )

                val keyInfo: @Composable (ImageVector, String, String) -> Unit =
                    { icon, desc, text ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                icon,
                                contentDescription = desc,
                                modifier = Modifier.padding(end = Size.CornerSmall),
                                tint = ColourScheme.onBackground
                            )
                            Text(
                                text,
                                style = Typography.bodyMedium,
                                color = ColourScheme.onBackground,
                            )
                        }
                    }

                // First line: time, calories, servings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    keyInfo(Lucide.Clock, "Cooking Time", "${recipe.cookingTime} min")
                    recipe.calories?.let {
                        keyInfo(Lucide.Zap, "Calories", "$it cal")
                    }
                    keyInfo(Lucide.Users, "Servings", "${recipe.portions} portions")
                }

                Spacer(Modifier.height(6.dp))

                // Second line: key ingredients, effort level
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    keyInfo(Lucide.Carrot, "Key Ingredients", recipe.keyIngredients.joinToString(", "))
                    keyInfo(Lucide.ChefHat, "Effort", recipe.effortLevel.displayName)
                }
            }

            // Tag Chips at bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(Size.Padding)
                    .hazeChild(hazeState)
            ) {
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recipe.tags.take(3).forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    tag.toString(),
                                    style = Typography.bodySmall
                                )
                            },
                            modifier = Modifier.defaultMinSize(minHeight = 28.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.LightGray,
                                labelColor = Color.Black
                            ),
                            shape = Shape.RoundedLarge,
                            elevation = AssistChipDefaults.assistChipElevation()
                        )
                    }
                }
            }
        }
    }
}

// Utility rounding function
fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}
