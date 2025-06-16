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
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
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
import dev.lancy.drp25.data.Comment
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.data.formatIngredientDisplay
import dev.lancy.drp25.data.formatStepDisplay
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.ui.shared.components.IconText
import dev.lancy.drp25.ui.shared.components.StarRating
import dev.lancy.drp25.utilities.Animation
import dev.lancy.drp25.utilities.Client
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Const
import dev.lancy.drp25.utilities.PersistenceManager
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.identity
import dev.lancy.drp25.utilities.rememberDailyCookingActivityManager
import io.kamel.core.ExperimentalKamelApi
import io.kamel.image.KamelImageBox
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Duration.Companion.seconds

class RecipeNode(
    nodeContext: NodeContext,
    private val recipe: Recipe,
    parent: RootNode,
    private val back: () -> Unit,
) : LeafNode(nodeContext),
    NavConsumer<RootNode.RootTarget, RootNode> by NavConsumerImpl(parent) {
    @OptIn(ExperimentalKamelApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val dailyCookingActivityManager = rememberDailyCookingActivityManager()
        val scrollState = rememberScrollState()
        val scope = rememberCoroutineScope()

        var showReviewDialog by remember { mutableStateOf(false) }
        var userRating by remember { mutableStateOf(0) }
        var showSuccessAnimation by remember { mutableStateOf(false) }

        val commentsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var showComments by remember { mutableStateOf(false) }
        var recipeComments by remember { mutableStateOf(emptyList<Comment>()) }

        LaunchedEffect(Unit) {
            scope.launch { recipeComments = getComments() }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(bottom = Size.Padding),
                verticalArrangement = Arrangement.spacedBy(Size.Padding),
            ) {
                ImageHeader()

                Column(
                    Modifier
                        .padding(Size.Padding)
                        .animateContentSize(),
                ) { ColumnContent(recipe) }

                Button(
                    onClick = { showReviewDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = Size.Padding, vertical = Size.Spacing),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColourScheme.primaryContainer.copy(alpha = 0.4f),
                        contentColor = ColourScheme.onPrimaryContainer,
                    ),
                ) { Text("Leave a Review") }

                Button(
                    onClick = { showComments = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = Size.Padding, vertical = Size.Spacing),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColourScheme.secondaryContainer.copy(alpha = 0.2f),
                        contentColor = ColourScheme.onSecondaryContainer,
                    ),
                ) { Text("See Reviews") }
            }

            AnimatedVisibility(
                showSuccessAnimation,
                modifier = Modifier.align(Alignment.BottomCenter),
            ) { SuccessDialog(userRating) { showSuccessAnimation = false } }

            AnimatedVisibility(
                showReviewDialog,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .border(3.dp, Color.Red),
            ) {
                ReviewDialog(
                    dailyCookingActivityManager = dailyCookingActivityManager,
                    onDismiss = { showReviewDialog = false },
                    onSuccess = {
                        showSuccessAnimation = true
                        showReviewDialog = false
                        userRating = it

                        scope.launch { recipeComments = getComments().toMutableStateList() }
                    },
                )
            }

            AnimatedVisibility(
                showComments,
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                CommentSheet(commentsSheetState, recipeComments) { showComments = false }
            }
        }
    }

    @Composable
    private fun SuccessDialog(
        userRating: Int,
        onDismiss: () -> Unit,
    ) {
        LaunchedEffect(Unit) {
            // Dismiss after a short delay.
            delay(2.seconds)
            onDismiss()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier.clip(Shape.RoundedMedium),
                color = ColourScheme.surface,
                shadowElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(Color(0xFF4CAF50)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Lucide.ChefHat,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.White,
                        )
                    }

                    Text(
                        "Delicious!",
                        style = Typography.titleLarge,
                        color = ColourScheme.primary,
                    )

                    Text(
                        "Review submitted successfully!",
                        style = Typography.bodyMedium,
                        color = ColourScheme.onSurfaceVariant,
                    )

                    Row {
                        repeat(5) { index ->
                            val icon = if (index < userRating) Icons.Filled.Star else Icons.Outlined.StarBorder

                            Icon(
                                imageVector = icon,
                                contentDescription = "Star $index",
                                tint = if (index < userRating) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ReviewDialog(
        dailyCookingActivityManager: PersistenceManager<Map<String, Int>>,
        onDismiss: () -> Unit,
        onSuccess: (Int) -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()
        var userRating by remember { mutableStateOf(0) }
        var userComment by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = Shape.RoundedMedium,
            containerColor = ColourScheme.surface,
            text = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ColourScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        Icon(
                            Lucide.ChefHat,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = ColourScheme.primary,
                        )

                        Text(
                            "Recipe Completed!",
                            style = Typography.titleLarge,
                            color = ColourScheme.primary,
                        )

                        Text(
                            "How would you rate this recipe?",
                            style = Typography.bodyLarge,
                            color = ColourScheme.onSurface,
                        )

                        Text(
                            "5 stars: One of the best recipes I've ever tried! A must!\n" +
                                "4 stars: Great recipe, but could use a few tweaks. Would cook again.\n" +
                                "3 stars: Nothing special, but decent.\n" +
                                "2 stars: Did not like it. Would not recommend.\n" +
                                "1 star: Avoid at all costs!\n",
                            color = ColourScheme.onSurfaceVariant,
                        )

                        InteractiveStarRating(
                            rating = userRating,
                            onRatingChange = { userRating = it },
                            starSize = 48.dp,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )

                        OutlinedTextField(
                            value = userComment,
                            onValueChange = { userComment = it },
                            label = { Text("Share your thoughts (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            textStyle = TextStyle(color = ColourScheme.onSurface),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = ColourScheme.primary,
                                unfocusedIndicatorColor = ColourScheme.outline,
                                focusedLabelColor = ColourScheme.primary,
                                unfocusedLabelColor = ColourScheme.onSurfaceVariant,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                            ),
                            shape = RoundedCornerShape(12.dp),
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                            dailyCookingActivityManager.update {
                                val currentCount = this[today.toString()] ?: 0
                                toMutableMap().apply { put(today.toString(), currentCount + 1) }
                            }

                            if (userComment.isNotBlank()) {
                                Client.submitComment(
                                    recipeId = recipe.id,
                                    userName = "Guest User",
                                    commentText = userComment,
                                    rating = userRating,
                                )
                            }

                            onSuccess(userRating)
                        }
                    },
                    modifier = Modifier.padding(end = Size.Padding),
                ) { Text("Submit", modifier = Modifier.padding(horizontal = 16.dp)) }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColourScheme.surfaceVariant,
                        contentColor = ColourScheme.onSurfaceVariant,
                    ),
                ) { Text("Cancel", modifier = Modifier.padding(horizontal = 16.dp)) }
            },
        )
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun CommentSheet(
        commentsSheetState: SheetState,
        recipeComments: List<Comment>,
        onDismiss: () -> Unit,
    ) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = commentsSheetState,
            containerColor = ColourScheme.surface,
            contentColor = ColourScheme.onSurface,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Size.Padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Size.Padding),
            ) {
                Text(
                    "Reviews",
                    style = Typography.titleLarge,
                    color = ColourScheme.primary,
                )

                Divider(
                    color = ColourScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = Size.Padding),
                )

                recipeComments
                    .ifEmpty {
                        Text(
                            "No comments yet. Be the first!",
                            style = Typography.bodyLarge,
                            color = ColourScheme.onSurfaceVariant,
                        )

                        emptyList()
                    }.forEach { comment -> CommentCard(comment) }
            }
        }
    }

    @Composable
    private fun CommentCard(comment: Comment) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = ColourScheme.surfaceVariant,
            ),
            shape = Shape.RoundedMedium,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Size.Padding, vertical = Size.Spacing),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    comment.user_name,
                    style = Typography.labelLarge,
                    color = ColourScheme.primary,
                )

                comment.created_at?.let {
                    Text(
                        "${it.toLocalDateTime(TimeZone.currentSystemDefault()).date}",
                        style = Typography.bodySmall,
                        color = ColourScheme.outline,
                    )
                }
            }

            StarRating(
                comment.rating.toFloat(),
                modifier = Modifier.padding(horizontal = Size.Padding, vertical = Size.Spacing),
            )

            Text(
                comment.comment_text,
                modifier = Modifier.padding(horizontal = Size.Padding, vertical = Size.Spacing),
                style = Typography.bodyMedium,
                color = ColourScheme.onSurfaceVariant,
            )
        }
    }

    @Composable
    private fun ImageHeader() {
        val hazeState = remember { HazeState() }

        KamelImageBox(
            resource = {
                asyncPainterResource(recipe.smallImage, filterQuality = FilterQuality.High)
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .clip(
                    RoundedCornerShape(
                        bottomStart = Size.CornerLarge,
                        bottomEnd = Size.CornerLarge,
                    ),
                ),
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
                        shape = Shape.RoundedMedium.copy(
                            topStart = CornerSize(0),
                            topEnd = CornerSize(0),
                        ),
                        style = Const.HazeStyle,
                    ).clip(Shape.RoundedMedium)
                    .padding(Size.Padding),
                style = Typography.titleMedium,
                color = ColourScheme.onBackground,
            )
        }
    }

    @Composable
    private fun ColumnScope.ColumnContent(recipe: Recipe) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StarRating(recipe.rating)

            val ratingText = buildString {
                if (recipe.numRatings == 0) {
                    append("No ratings yet")
                    return@buildString
                }

                val numericValue = recipe
                    .rating
                    .toString()
                    .runCatching { slice(0..3) }
                    .fold(
                        onSuccess = ::identity,
                        onFailure = { recipe.rating.toString() },
                    )
                append(numericValue)
                append(" from ${recipe.numRatings} ratings")
            }

            Text(
                text = ratingText,
                style = Typography.bodyMedium,
                color = ColourScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 16.sp,
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

            IconText(Lucide.Carrot, "Tags", listOfNotNull(recipe.mealType, recipe.diet, recipe.cuisine).joinToString())
        }

        Spacer(Modifier.height(Size.Spacing))

        Divider(
            color = ColourScheme.outlineVariant,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = Size.Spacing),
        )

        Section("Ingredients") {
            recipe.ingredients.forEach { ingredient ->
                val formatted = formatIngredientDisplay(ingredient)
                var isChecked by remember { mutableStateOf(false) }

                val iconColor by animateColorAsState(
                    targetValue = if (isChecked) Color(0xFF4CAF50) else Color.Gray,
                    animationSpec = tween(durationMillis = 300),
                )

                IconTextClickable(
                    icon = if (isChecked) Lucide.Check else Lucide.Square,
                    text = formatted,
                    contentDescription = formatted,
                    iconTint = iconColor,
                    onClick = { isChecked = !isChecked },
                )
            }
        }

        Section("Steps") {
            recipe.steps.map { formatStepDisplay(it) }.forEachIndexed { index, step ->
                var visible by remember { mutableStateOf(false) }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
                    exit = slideOutVertically() + fadeOut(),
                ) {
                    var checked by remember { mutableStateOf(false) }

                    val icon = if (checked) Lucide.ChefHat else Lucide.Diamond
                    val iconColor by animateColorAsState(
                        targetValue = if (checked) Color(0xFF4CAF50) else Color.Gray,
                        animationSpec = tween(durationMillis = 300),
                    )

                    IconTextClickable(
                        icon = icon,
                        text = step.description,
                        contentDescription = step.description,
                        iconTint = iconColor,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
                        ),
                        onClick = { checked = !checked },
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
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
            exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Size.Spacing)) { content() }
        }
    }

    @Composable
    private fun IconTextClickable(
        icon: ImageVector,
        text: String,
        contentDescription: String,
        iconTint: Color = Color.Gray,
        textStyle: TextStyle = TextStyle.Default,
        onClick: () -> Unit = {},
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = textStyle,
                fontSize = 18.sp,
            )
        }
    }

    @Composable
    private fun InteractiveStarRating(
        rating: Int,
        onRatingChange: (Int) -> Unit,
        modifier: Modifier = Modifier,
        starSize: Dp = 32.dp,
        selectedColor: Color = Color(0xFFFFD700),
        unselectedColor: Color = Color.Gray,
    ) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Rate $i stars",
                    tint = if (i <= rating) selectedColor else unselectedColor,
                    modifier = Modifier
                        .size(starSize)
                        .clickable { onRatingChange(i) },
                )
            }
        }
    }

    private suspend fun getComments(): List<Comment> {
        val result = Client.fetchCommentsForRecipe(recipe.id)
        print(result)
        return result
    }
}
