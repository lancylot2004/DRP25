package dev.lancy.drp25.ui.overlay.recipe

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalRuler
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.Carrot
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.ui.main.feed.round
import dev.lancy.drp25.ui.shared.components.IconText
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import io.kamel.core.ExperimentalKamelApi
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.KamelImageBox
import io.kamel.image.asyncPainterResource

class RecipeNode(
    nodeContext: NodeContext,
    private val recipe: Recipe,
) : LeafNode(nodeContext) {
    @OptIn(ExperimentalKamelApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        Column {
            val hazeState = remember { HazeState() }

            KamelImageBox(
                resource = {
                    asyncPainterResource(
                        "https://www.halfbakedharvest.com/wp-content/uploads/2019/07/Bucatini-Amatriciana-1-700x1050.jpg",
                        filterQuality = FilterQuality.High
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .clip(RoundedCornerShape(bottomStart = Size.CornerLarge, bottomEnd = Size.CornerLarge)),
                contentAlignment = Alignment.BottomStart,
            ) { painter ->
                Image(
                    painter = painter,
                    contentDescription = "description",
                    modifier = Modifier
                        .fillMaxSize()
                        .haze(hazeState),
                    contentScale = ContentScale.Crop,
                )

                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(
                            .3f to Color.Transparent,
                            1f to ColourScheme.background.copy(alpha = 0.8f),
                        ))
                )

                Text(
                    "Pork and Century Egg Congee",
                    modifier = Modifier
                        .hazeChild(hazeState, style = HazeStyle(blurRadius = 2.dp, noiseFactor = 10f))
                        .clip(Shape.RoundedMedium)
                        .padding(Size.Padding),
                    style = Typography.titleMedium,
                    color = ColourScheme.onBackground,
                )
            }

            Column(Modifier.padding(Size.Padding)) { ColumnContent() }
        }
    }

    @Composable
    private fun ColumnScope.ColumnContent() {
        IconText(
            Lucide.Clock,
            "Cooking Time",
            "${recipe.cookingTime} minutes",
        )

        IconText(
            Lucide.Carrot,
            "Tags",
            recipe.tags.take(3).joinToString(),
        )

        HorizontalRuler()


    }
}