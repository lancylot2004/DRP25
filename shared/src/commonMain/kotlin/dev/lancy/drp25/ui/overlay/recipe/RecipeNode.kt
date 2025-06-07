package dev.lancy.drp25.ui.overlay.recipe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.Carrot
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Diamond
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Square
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.data.formatIngredientDisplay
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.ui.shared.components.IconText
import dev.lancy.drp25.ui.shared.components.StarRating
import dev.lancy.drp25.utilities.Animation
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.allRecipes
import io.kamel.core.ExperimentalKamelApi
import io.kamel.image.KamelImageBox
import io.kamel.image.asyncPainterResource

class RecipeNode(
    nodeContext: NodeContext,
    private val recipeID: String,
    parent: RootNode,
    private val back: () -> Unit,
) : LeafNode(nodeContext),
    NavConsumer<RootNode.RootTarget, RootNode> by NavConsumerImpl(parent) {

    @OptIn(ExperimentalKamelApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            val hazeState = remember { HazeState() }
            val recipe = remember(recipeID) {
                allRecipes.value.find { it.id == recipeID }
            } ?: throw IllegalStateException("Recipe with id $recipeID not found")

            KamelImageBox(
                resource = {
                    asyncPainterResource(recipe.smallImage, filterQuality = FilterQuality.High)
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
                        )),
                )

                IconButton(
                    modifier = Modifier.align(Alignment.TopStart),
                    onClick = back
                ) {
                    Icon(
                        Lucide.ChevronLeft,
                        contentDescription = "Back",
                        tint = ColourScheme.onBackground
                    )
                }

                Text(
                    text = recipe.name,
                    modifier = Modifier
                        .hazeChild(hazeState, style = HazeStyle(blurRadius = 2.dp, noiseFactor = 10f))
                        .clip(Shape.RoundedMedium)
                        .padding(Size.Padding),
                    style = Typography.titleMedium,
                    color = ColourScheme.onBackground,
                )
            }

            Column(
                Modifier
                    .padding(Size.Padding)
                    .animateContentSize(),
            ) { ColumnContent(recipe) }
        }
    }

    @Composable
    private fun ColumnScope.ColumnContent(recipe: Recipe) {
        StarRating(3.5f)

        IconText(
            Lucide.Clock,
            "Cooking Time",
            "${recipe.cookingTime} min",
        )

        IconText(
            Lucide.Carrot,
            "Tags",
            listOf(recipe.mealType, recipe.diet, recipe.cuisine).joinToString()
        )


        Section("Ingredients") {
            recipe.ingredients.forEach { ingredient ->
                val formatted = formatIngredientDisplay(ingredient)
                IconText(
                    Lucide.Square,
                    formatted,
                    formatted
                )
            }
        }

        Section("Steps") {
            recipe.steps.forEachIndexed { index, step ->
                IconText(
                    Lucide.Diamond,
                    step.description,
                    step.description,
                )
            }
        }

        Section("About This Recipe") {
            Text(
                text = recipe.description,
                style = Typography.bodyMedium,
                color = ColourScheme.onBackground,
            )
        }
    }

    @Composable
    fun ColumnScope.Section(
        title: String,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        var expanded by remember { mutableStateOf(true) }

        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    title,
                    style = Typography.titleSmall,
                    color = ColourScheme.onBackground,
                )

                IconButton(onClick = { expanded = !expanded }) {
                    val rotationAngle by animateFloatAsState(
                        targetValue = if (expanded) 180f else 0f,
                        animationSpec = Animation.medium(),
                        label = "\"$title\" Chevron Rotation",
                    )

                    Icon(
                        Lucide.ChevronUp,
                        contentDescription = "Expand/Collapse",
                        modifier = Modifier.rotate(rotationAngle),
                        tint = ColourScheme.onBackground,
                    )
                }
            }

            Divider(color = ColourScheme.outlineVariant)

            Spacer(Modifier.height(Size.Padding))
        }

        AnimatedVisibility(
            visible = expanded,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut(),
        ) { Column(verticalArrangement = Arrangement.spacedBy(Size.Spacing)) { content() } }
    }
}