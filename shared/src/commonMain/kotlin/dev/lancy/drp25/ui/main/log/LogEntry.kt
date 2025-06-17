package dev.lancy.drp25.ui.main.log

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.composables.icons.lucide.Carrot
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.Zap
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.ui.shared.components.IconText
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun logEntry(recipe: Recipe, navCallback: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(Size.Padding)
            .clip(Shape.RoundedMedium)
            .clickable(role = Role.Button) { navCallback() },
    ) {
        KamelImage(
            resource = { asyncPainterResource(recipe.cardImage) },
            contentDescription = recipe.name,
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .aspectRatio(1f)
                .padding(Size.Padding)
                .clip(Shape.RoundedMedium),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier.align(Alignment.CenterVertically),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().width(IntrinsicSize.Max),
            ) {
                Text(
                    text = recipe.name,
                    style = Typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Size.CornerSmall),
                    modifier = Modifier.padding(end = Size.Padding),
                ) {
                    if (recipe.rating == 0.0f) {
                        Icon(
                            imageVector = Lucide.Star,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterVertically),
                        )
                    } else {
                        Icon(
                            imageVector = Lucide.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.align(Alignment.CenterVertically),
                        )
                        Text(
                            text = "${recipe.rating} / 5",
                            style = Typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterVertically),
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Size.Padding),
            ) {
                recipe.calories?.let {
                    IconText(Lucide.Zap, "Calories", "$it kcal")
                }
                IconText(Lucide.Users, "Servings", "${recipe.portions} portions")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Size.CornerSmall),
                modifier = Modifier.padding(end = Size.Padding),
            ) {
                Icon(
                    imageVector = Lucide.Carrot,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                )
                Text(
                    text = recipe.keyIngredients.joinToString(", "),
                    style = Typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
