package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.Carrot
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.math.round

class FeedCard(
    nodeContext: NodeContext,
    private val recipe: Recipe,
) : LeafNode(nodeContext) {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val hazeState = remember { HazeState() }
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(Size.BigPadding)
                .clip(Shape.RoundedLarge)
                .border(2.dp, ColourScheme.onBackground, Shape.RoundedLarge)
        ) {
            KamelImage(
                resource = asyncPainterResource(recipe.imageURL ?: "https://i.ytimg.com/vi/LOXyOlLUX_A/hqdefault.jpg"),
                contentDescription = recipe.name,
                modifier = Modifier
                    .fillMaxSize()
                    .haze(state = hazeState),
                contentScale = ContentScale.Crop,
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = 0.01f
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomStart)
                    .background(Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.6f to Color.Transparent,
                            0.85f to Color.Black
                        )
                    ))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f / 3.2f)
                    .align(Alignment.BottomStart)
                    .padding(Size.Padding),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(recipe.name, style = Typography.titleMedium, color = ColourScheme.onBackground)
                Row {
                    Icon(
                        Lucide.Clock,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(Size.CornerSmall),
                        tint = ColourScheme.onBackground
                    )
                    Text(
                        "${recipe.cookingTime} minutes",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        style = Typography.bodyMedium,
                        color = ColourScheme.onBackground
                    )
                }
                Row {
                    Icon(
                        Lucide.Carrot,
                        contentDescription = null,
                        modifier = Modifier.padding(Size.CornerSmall),
                        tint = ColourScheme.onBackground
                    )
                    Text(
                        recipe.tags.take(3).joinToString(),
                        modifier = Modifier.align(Alignment.CenterVertically),
                        style = Typography.bodyMedium,
                        color = ColourScheme.onBackground
                    )
                }
                Row {
                    Icon(
                        Lucide.Star,
                        contentDescription = null,
                        modifier = Modifier.padding(Size.CornerSmall),
                        tint = ColourScheme.onBackground
                    )
                    Text(
                        "${recipe.rating.round(1)} / 5",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        style = Typography.bodyMedium,
                        color = ColourScheme.onBackground
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(Shape.RoundedLarge)
                        .background(Color.Gray)
                        .hazeChild(hazeState)
                ) {
                    LazyRow {
                        items(recipe.tags.take(3)) {
                            Chip(
                                onClick = {},
                                modifier = Modifier.padding(Size.Padding)
                            ) {
                                Text(it.toString(), style = Typography.bodyMedium, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}