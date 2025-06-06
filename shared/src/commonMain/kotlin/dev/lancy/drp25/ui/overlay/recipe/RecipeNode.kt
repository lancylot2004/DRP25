package dev.lancy.drp25.ui.overlay.recipe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.Carrot
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Square
import com.composables.icons.lucide.SquareCheckBig
import com.composables.icons.lucide.SquareDashedBottom
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.lancy.drp25.data.Recipe
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
import dev.lancy.drp25.utilities.realm
import io.kamel.core.ExperimentalKamelApi
import io.kamel.image.KamelImageBox
import io.kamel.image.asyncPainterResource
import org.mongodb.kbson.ObjectId
import io.realm.kotlin.ext.query

class RecipeNode(
    nodeContext: NodeContext,
    private val recipeID: ObjectId,
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
                realm.query<Recipe>("id == $0", recipeID).first().find()
            } ?: throw IllegalStateException("Recipe with id $recipeID not found")

            KamelImageBox(
                resource = {
                    asyncPainterResource(recipe.smallImage)
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

        Section("Ingredients") {
            recipe.ingredients.forEach {
                IconText(
                    Lucide.Square,
                    "${it.name} (${it.amount ?: ""})",
                    "${it.name} (${it.amount ?: ""})",
                )
            }
        }

        Section("Steps") {
            recipe.steps.forEachIndexed { index, step ->
                IconText(
                    Lucide.SquareCheckBig,
                    step.description,
                    step.description,
                )
            }
        }

        Section("About This Recipe") {
            Text(
                "The chef is from this tiny little village in France, and has refined his culinary skills by eating grapes in the vineyard every single morning. This dish is inspired by Martians who invited the young chef to Mars for a taster session in potato growing, and his memories thereof.",
                style = Typography.bodyMedium,
                color = ColourScheme.onBackground,
            )
        }
    }

    @Composable
    private fun ColumnScope.Section(
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
