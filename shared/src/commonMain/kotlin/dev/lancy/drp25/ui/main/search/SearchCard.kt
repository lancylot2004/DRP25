package dev.lancy.drp25.ui.main.search

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.Zap
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.ui.shared.components.StarRating
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Const
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import dev.lancy.drp25.utilities.ScreenSize

@Composable
fun SearchCard(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    tapCallback: () -> Unit
) {
    val hazeState = remember { HazeState() }
    val cardHeight = ScreenSize.height / 4

    Column(
        modifier = modifier
            .clickable(role = Role.Button) { tapCallback() }
            .padding(Size.Padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .clip(Shape.RoundedSmall)
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
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .hazeChild(hazeState, style = Const.HazeStyle)
                    .padding(Size.Padding)
            ) {
                StarRating(
                    rating = recipe.rating,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .height(20.dp)
                )
            }
        }

        Spacer(Modifier.height(Size.Spacing))

        Text(
            text = recipe.name,
            style = Typography.labelMedium,
            color = ColourScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Size.Spacing),
            maxLines = 2
        )

        Spacer(Modifier.height(Size.Spacing))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Size.Spacing)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Lucide.Clock,
                    contentDescription = "Cooking time",
                    tint = ColourScheme.onBackground,
                    modifier = Modifier.height(14.dp)
                )

                Spacer(Modifier.width(Size.Spacing))

                Text(
                    text = "${recipe.cookingTime} min",
                    style = Typography.labelSmall,
                    color = ColourScheme.onBackground
                )
            }
            recipe.calories?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Zap,
                        contentDescription = "Calories",
                        tint = ColourScheme.onBackground,
                        modifier = Modifier.height(14.dp)
                    )

                    Spacer(Modifier.width(Size.Spacing))

                    Text(
                        text = "$it kcal",
                        style = Typography.labelSmall,
                        color = ColourScheme.onBackground
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Lucide.Users,
                    contentDescription = "Servings",
                    tint = ColourScheme.onBackground,
                    modifier = Modifier.height(14.dp)
                )

                Spacer(Modifier.width(Size.Spacing))

                Text(
                    text = "${recipe.portions} servings",
                    style = Typography.labelSmall,
                    color = ColourScheme.onBackground
                )
            }
        }

        Spacer(Modifier.height(Size.Spacing))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Size.Padding),
            modifier = Modifier.fillMaxWidth()
        ) {
            recipe.keyIngredients.take(3).forEach { ingredient ->
                Box(
                    modifier = Modifier
                        .clip(Shape.RoundedSmall)
                        .border(1.dp, ColourScheme.outline, Shape.RoundedSmall)
                        .padding(horizontal = Size.Padding, vertical = Size.Spacing)
                ) {
                    Text(
                        text = ingredient,
                        style = Typography.labelSmall,
                        color = ColourScheme.onBackground
                    )
                }
            }
        }
    }
}