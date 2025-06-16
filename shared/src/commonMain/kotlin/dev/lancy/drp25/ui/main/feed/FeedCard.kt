package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Carrot
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.Zap
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.ui.shared.components.IconText
import dev.lancy.drp25.ui.shared.components.StarRating
import dev.lancy.drp25.utilities.Client
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Const
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.rememberSavedRecipeIdsManager
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FeedCard(modifier: Modifier = Modifier, recipe: Recipe, tapCallback: () -> Unit) {
    val hazeState = remember { HazeState() }
    val scope = rememberCoroutineScope()
    val manager = rememberSavedRecipeIdsManager()
    val savedIds by manager.state.collectAsState(initial = emptySet())
    val isSaved = recipe.id in savedIds

    // 1️Local flag for the pop animation
    var showPopHeart by remember { mutableStateOf(false) }
    val popScale by animateFloatAsState(
        targetValue = if (showPopHeart) 1.4f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
    val popAlpha by animateFloatAsState(
        targetValue = if (showPopHeart) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(Size.BigPadding)
            .border(1.dp, ColourScheme.secondary, Shape.RoundedLarge)
            .clip(Shape.RoundedLarge)
            //.clickable(role = Role.Button) { tapCallback() },
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { tapCallback() },
                onDoubleClick = {
                    showPopHeart = true
                    scope.launch {
                        manager.update {
                            if (contains(recipe.id)) this - recipe.id
                            else this + recipe.id
                        }
                        delay(400)
                        showPopHeart = false
                    }
                }
            )
    ) {
        KamelImage(
            resource = { asyncPainterResource(recipe.cardImage) },
            contentDescription = recipe.name,
            modifier = Modifier
                .fillMaxSize()
                .haze(state = hazeState),
            contentScale = ContentScale.Crop,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        )

        Column(
            modifier = Modifier
                .hazeChild(
                    state = hazeState,
                    shape = Shape.RoundedMedium,
                    style = Const.HazeStyle,
                ).align(Alignment.CenterEnd),
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        manager.update {
                            if (recipe.id in this) this - recipe.id
                            else this + recipe.id
                        }
                    }
                },
                modifier = Modifier
                    .shadow(
                        elevation = Size.IconMedium,
                        ambientColor = Color.Black,
                        spotColor = Color.Black,
                    ),
            ) {
                AnimatedContent(isSaved) {
                    Icon(
                        imageVector = Lucide.Heart,
                        contentDescription = null,
                        tint = if (it) Color(0xFFFF6767) else Color.White,
                        modifier = Modifier.size(Size.IconMedium),
                    )
                }
            }
        }

        // Heart icon when double-tap ??
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = Color(0xFFFF6767),
            modifier = Modifier.align(Alignment.Center)
                .graphicsLayer {
                    scaleX = popScale
                    scaleY = popScale
                    alpha = popAlpha
                }.size(64.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .hazeChild(hazeState, style = Const.HazeStyle)
                .padding(Size.Padding),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = recipe.name,
                style = Typography.titleMedium,
                color = ColourScheme.onBackground,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                StarRating(recipe.rating)
                Text(
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
                IconText(Lucide.Clock, "Cooking Time", "${recipe.cookingTime} min")

                recipe.calories?.let {
                    IconText(Lucide.Zap, "Calories", "$it kcal")
                }

                IconText(Lucide.Users, "Servings", "${recipe.portions} portions")

                IconText(Lucide.Carrot, "Key Ingredients", recipe.keyIngredients.joinToString(", "))
            }

            // The navigation bar; counteract the padding of the outer box and the column.
            Spacer(Modifier.height(Size.BarLarge - Size.BigPadding - Size.Padding))
        }
    }
}
