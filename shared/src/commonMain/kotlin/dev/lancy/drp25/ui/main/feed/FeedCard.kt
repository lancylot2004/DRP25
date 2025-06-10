package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.Carrot
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.Zap
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.ui.shared.components.IconText
import dev.lancy.drp25.ui.shared.components.StarRating
import dev.lancy.drp25.ui.main.feed.FeedNode.FeedTarget
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.getRecipe
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch

class FeedCard(
    nodeContext: NodeContext,
    parent: FeedNode,
    private val recipeId: String,
) : LeafNode(nodeContext),
    NavConsumer<FeedTarget, FeedNode> by NavConsumerImpl(parent){
    @Composable
    override fun Content(modifier: Modifier) {
        val hazeState = remember { HazeState() }
        val scope = rememberCoroutineScope()

        val recipe = getRecipe(recipeId)

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(Size.BigPadding)
                .border(1.dp, ColourScheme.secondary, Shape.RoundedLarge)
                .clip(Shape.RoundedLarge)
                .clickable(role = Role.Button) {
                    scope.launch {
                        this@FeedCard
                            .navParent.navParent
                            .superNavigate<RootNode.RootTarget>(RootNode.RootTarget.Recipe(recipe.id))
                    }
                }
        ) {
            // Background image
            KamelImage(
                resource = { asyncPainterResource(recipe.cardImage) },
                contentDescription = recipe.name,
                modifier = Modifier
                    .fillMaxSize()
                    .haze(state = hazeState),
                contentScale = ContentScale.Crop,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .hazeChild(hazeState, style = HazeDefaults.style(tint = ColourScheme.background.copy(alpha = 0.3f)))
                    .padding(Size.Padding),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = recipe.name,
                    style = Typography.titleMedium,
                    color = ColourScheme.onBackground
                )

                StarRating(recipe.rating)

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    horizontalArrangement = Arrangement.spacedBy(Size.Padding),
                    itemVerticalAlignment = Alignment.CenterVertically,
                ) {
                    IconText(Lucide.Clock, "Cooking Time", "${recipe.cookingTime} min")

                    recipe.calories?.let {
                        IconText(Lucide.Zap, "Calories", "$it kcal")
                    }

                    IconText(Lucide.Users, "Servings", "${recipe.portions} portions")

                    IconText(Lucide.Carrot, "Key Ingredients", recipe.keyIngredients.joinToString(", "))
                }

                // Chips
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
//                    recipe.tags.take(3).forEach { tag ->
//                        AssistChip(
//                            onClick = {},
//                            label = {
//                                Text(
//                                    tag.toString(),
//                                    style = Typography.bodySmall
//                                )
//                            },
//                            modifier = Modifier.defaultMinSize(minHeight = 28.dp),
//                            colors = AssistChipDefaults.assistChipColors(
//                                containerColor = Color.LightGray,
//                                labelColor = Color.Black,
//                            ),
//                            shape = Shape.RoundedLarge,
//                            elevation = AssistChipDefaults.assistChipElevation(),
//                        )
//                    }
                }

                // The navigation bar; counteract the padding of the outer box and the column.
                Spacer(Modifier.height(Size.BarLarge - Size.BigPadding - Size.Padding))
            }
        }
    }
}
