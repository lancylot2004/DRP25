package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

class FeedCard(
    nodeContext: NodeContext,
    private val recipe: Recipe,
) : LeafNode(nodeContext) {
    @Composable
    override fun Content(modifier: Modifier) {
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(Size.BigPadding)
                    .clip(Shape.RoundedLarge)
                    .border(2.dp, ColourScheme.onBackground, Shape.RoundedLarge),
        ) {
            KamelImage(
                resource = asyncPainterResource(recipe.imageURL ?: "https://i.ytimg.com/vi/LOXyOlLUX_A/hqdefault.jpg"),
                contentDescription = recipe.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                animationSpec =
                    spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = 0.01f,
                    ),
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(1f / 3)
                        .align(Alignment.BottomStart)
                        .padding(Size.Padding),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(recipe.name, style = Typography.titleMedium, color = ColourScheme.onBackground)
            }
        }
    }
}
