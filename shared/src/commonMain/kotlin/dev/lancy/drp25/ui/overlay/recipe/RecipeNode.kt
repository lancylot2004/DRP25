package dev.lancy.drp25.ui.overlay.recipe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.Carrot
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChefHat
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Diamond
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Square
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.Zap
import dev.chrisbanes.haze.HazeState
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
import dev.lancy.drp25.utilities.Const
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import io.kamel.core.ExperimentalKamelApi
import io.kamel.image.KamelImageBox
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay

class RecipeNode(
    nodeContext: NodeContext,
    private val recipe: Recipe,
    parent: RootNode,
    private val back: () -> Unit,
) : LeafNode(nodeContext),
    NavConsumer<RootNode.RootTarget, RootNode> by NavConsumerImpl(parent) {
    @OptIn(ExperimentalKamelApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            val hazeState = remember { HazeState() }

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
                        .background(
                            Brush.verticalGradient(
                                .3f to Color.Transparent,
                                1f to ColourScheme.background.copy(alpha = 0.8f),
                            ),
                        ),
                )

                IconButton(
                    modifier = Modifier
                        .padding(Size.Padding)
                        .align(Alignment.TopStart)
                        .clip(Shape.RoundedMedium)
                        .hazeChild(hazeState, shape = Shape.RoundedMedium, style = Const.HazeStyle),
                    onClick = back,
                ) {
                    Icon(
                        Lucide.ChevronLeft,
                        contentDescription = "Back",
                        tint = ColourScheme.onBackground,
                    )
                }

                Text(
                    text = recipe.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .hazeChild(
                            hazeState,
                            shape = Shape.RoundedMedium.copy(topStart = CornerSize(0), topEnd = CornerSize(0)),
                            style = Const.HazeStyle,
                        ).clip(Shape.RoundedMedium)
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            StarRating(recipe.rating)
            androidx.compose.material.Text(
                text = "${recipe.rating} / 5",
                style = Typography.bodyMedium,
                color = ColourScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 16.sp
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.spacedBy(Size.Padding),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            IconText(Lucide.Clock, "Cooking Time", "${recipe.cookingTime} min",)

            recipe.calories?.let {
                IconText(Lucide.Zap, "Calories", "$it kcal")
            }

            IconText(Lucide.Users, "Servings", "${recipe.portions} portions")

            IconText(Lucide.Carrot, "Tags", listOfNotNull(recipe.mealType, recipe.diet, recipe.cuisine).joinToString(),)
        }

        Section("Ingredients") {
            recipe.ingredients.forEach { ingredient ->
                val formatted = formatIngredientDisplay(ingredient)
                var isChecked by remember { mutableStateOf(false) }

                val iconColor by animateColorAsState(
                    targetValue = if (isChecked) Color(0xFF4CAF50) else Color.Gray,
                    animationSpec = tween(durationMillis = 300)
                )

                IconText(
                    icon = if (isChecked) Lucide.Check else Lucide.Square,
                    text = formatted,
                    contentDescription = formatted,
                    iconTint = iconColor,
                    onClick = { isChecked = !isChecked }
                )
            }
        }

        // Track which steps are checked
        val stepCheckedStates = remember {
            mutableStateListOf<Boolean>().apply {
                repeat(recipe.steps.size) { add(false) }
            }
        }

        Section("Steps") {
            recipe.steps.forEachIndexed { index, step ->
                var visible by remember { mutableStateOf(false) }

                // Animate step entrance
                LaunchedEffect(Unit) {
                    delay(index * 100L)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300))
                ) {
                    // Smoothly animate icon change
                    val icon = if (stepCheckedStates[index]) Lucide.ChefHat else Lucide.Diamond
                    val iconColor by animateColorAsState(
                        targetValue = if (stepCheckedStates[index]) Color(0xFF4CAF50) else Color.Gray,
                        animationSpec = tween(durationMillis = 300)
                    )

                    IconText(
                        icon = icon,
                        text = step.description,
                        contentDescription = step.description,
                        iconTint = iconColor,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            textDecoration = if (stepCheckedStates[index]) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        onClick = {
                            stepCheckedStates[index] = !stepCheckedStates[index]
                        }
                    )
                }
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


// ICON TEXT
@Composable
fun IconText(
    icon: ImageVector,
    text: String,
    contentDescription: String,
    iconTint: Color = Color.Gray,
    textStyle: TextStyle = TextStyle.Default,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = textStyle,
            fontSize = 18.sp
        )
    }
}
