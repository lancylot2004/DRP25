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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.composables.icons.lucide.MessageCircle
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
import dev.lancy.drp25.utilities.PersistenceManager
import dev.lancy.drp25.data.formatStepDisplay
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.rememberDailyCookingActivityManager
import io.kamel.core.ExperimentalKamelApi
import io.kamel.image.KamelImageBox
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import dev.lancy.drp25.utilities.Client
import dev.lancy.drp25.data.Comment
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Surface
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.animateDpAsState
import dev.lancy.drp25.data.formatStepDisplay

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
        val coroutineScope = rememberCoroutineScope()

        var showFinishRecipeDialog by remember { mutableStateOf(false) }
        var userRating by remember { mutableStateOf(0) }
        var userComment by remember { mutableStateOf("") }
        var showSuccessAnimation by remember { mutableStateOf(false) }
        var animationScale by remember { mutableFloatStateOf(0f) }

        val commentsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        var showCommentsBottomSheet by remember { mutableStateOf(false) }
        var recipeComments by remember { mutableStateOf<List<Comment>>(emptyList()) }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .verticalScroll(scrollState)
                    .padding(bottom = 32.dp)
            ) {
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
                            )
                            .clip(Shape.RoundedMedium)
                            .padding(Size.Padding),
                        style = Typography.titleMedium,
                        color = ColourScheme.onBackground,
                    )
                }

                Column(
                    Modifier.padding(Size.Padding).animateContentSize(),
                ) {
                    ColumnContent(recipe)
                }

                Spacer(Modifier.height(Size.Padding))
                Button(
                    onClick = {
                        showFinishRecipeDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = Size.Padding, vertical = Size.Spacing)
                ) {
                    Text("Finish Recipe")
                }

                Spacer(Modifier.height(80.dp))
            }

            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        recipeComments = Client.fetchCommentsForRecipe(recipe.id)
                        showCommentsBottomSheet = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Size.Padding),
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            ) {
                Icon(
                    Lucide.MessageCircle,
                    contentDescription = "View Comments",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Success Animation Overlay
            if (showSuccessAnimation) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable(enabled = false) { },
                    contentAlignment = Alignment.Center
                ) {
                    val scale by animateFloatAsState(
                        targetValue = animationScale,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "Success Animation Scale"
                    )

                    Surface(
                        modifier = Modifier
                            .scale(scale)
                            .clip(RoundedCornerShape(24.dp)),
                        color = ColourScheme.surface,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(40.dp))
                                    .background(Color(0xFF4CAF50)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Lucide.ChefHat,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.White
                                )
                            }

                            Text(
                                "Delicious!",
                                style = Typography.titleLarge,
                                color = ColourScheme.primary
                            )

                            Text(
                                "Recipe completed successfully",
                                style = Typography.bodyMedium,
                                color = ColourScheme.onSurfaceVariant
                            )

                            if (userRating > 0) {
                                Row {
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = if (index < userRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                            contentDescription = null,
                                            tint = if (index < userRating) Color(0xFFFFD700) else Color.Gray,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    LaunchedEffect(showSuccessAnimation) {
                        if (showSuccessAnimation) {
                            animationScale = 1f
                        }
                    }
                }
            }
        }

        if (showFinishRecipeDialog) {
            AlertDialog(
                onDismissRequest = { showFinishRecipeDialog = false },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(24.dp),
                containerColor = ColourScheme.surface,
                text = {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = ColourScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Icon(
                                Lucide.ChefHat,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = ColourScheme.primary
                            )

                            Text(
                                "Recipe Completed!",
                                style = Typography.titleLarge,
                                color = ColourScheme.primary,
                            )

                            Text(
                                "How would you rate this recipe?",
                                style = Typography.bodyLarge,
                                color = ColourScheme.onSurface
                            )

                            InteractiveStarRating(
                                rating = userRating,
                                onRatingChange = { userRating = it },
                                starSize = 48.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
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
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(12.dp)
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
                                        commentText = userComment
                                    )
                                }

                                if (userRating > 0) {
                                    Client.updateRecipeRating(
                                        recipeId = recipe.id,
                                        newRating = userRating.toFloat()
                                    )
                                }

                                showFinishRecipeDialog = false
                                userRating = 0
                                userComment = ""
                                showSuccessAnimation = true
                                animationScale = 1f

                                delay(2000)
                                back()
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Submit", modifier = Modifier.padding(horizontal = 16.dp))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showFinishRecipeDialog = false },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = ColourScheme.surfaceVariant,
                            contentColor = ColourScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Cancel", modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            )
        }

        if (showCommentsBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCommentsBottomSheet = false },
                sheetState = commentsSheetState,
                containerColor = ColourScheme.surface,
                contentColor = ColourScheme.onSurface,
                modifier = Modifier.fillMaxHeight(0.8f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Size.Padding)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Size.Padding)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Lucide.MessageCircle,
                            contentDescription = null,
                            tint = ColourScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Comments",
                            style = Typography.titleLarge,
                            color = ColourScheme.primary
                        )
                    }

                    Divider(
                        color = ColourScheme.outlineVariant,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    if (recipeComments.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ColourScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Lucide.MessageCircle,
                                    contentDescription = null,
                                    tint = ColourScheme.outline,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "No comments yet",
                                    style = Typography.bodyLarge,
                                    color = ColourScheme.onSurfaceVariant
                                )
                                Text(
                                    "Be the first to leave one!",
                                    style = Typography.bodyMedium,
                                    color = ColourScheme.outline
                                )
                            }
                        }
                    } else {
                        recipeComments.forEach { comment ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = ColourScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            comment.user_name,
                                            style = Typography.labelLarge,
                                            color = ColourScheme.primary
                                        )
                                        comment.created_at?.let {
                                            val localDateTime = it.toLocalDateTime(TimeZone.currentSystemDefault())
                                            Text(
                                                "${localDateTime.date}",
                                                style = Typography.bodySmall,
                                                color = ColourScheme.outline
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        comment.comment_text,
                                        style = Typography.bodyMedium,
                                        color = ColourScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
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
            modifier = Modifier.padding(vertical = Size.Spacing)
        )

        Section("Ingredients") {
            recipe.ingredients.forEach { ingredient ->
                val formatted = formatIngredientDisplay(ingredient)
                var isChecked by remember { mutableStateOf(false) }

                val iconColor by animateColorAsState(
                    targetValue = if (isChecked) Color(0xFF4CAF50) else Color.Gray,
                    animationSpec = tween(durationMillis = 300)
                )

                IconTextClickable(
                    icon = if (isChecked) Lucide.Check else Lucide.Square,
                    text = formatted,
                    contentDescription = formatted,
                    iconTint = iconColor,
                    onClick = { isChecked = !isChecked }
                )
            }
        }

        val stepCheckedStates = remember {
            mutableStateListOf<Boolean>().apply {
                repeat(recipe.steps.size) { add(false) }
            }
        }

        Section("Steps") {
            recipe.steps.map { formatStepDisplay(it) }.forEachIndexed { index, step ->
                var visible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    delay(index * 100L)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
                    exit = slideOutVertically() + fadeOut(),
                ) {
                    val icon = if (stepCheckedStates[index]) Lucide.ChefHat else Lucide.Diamond
                    val iconColor by animateColorAsState(
                        targetValue = if (stepCheckedStates[index]) Color(0xFF4CAF50) else Color.Gray,
                        animationSpec = tween(durationMillis = 300)
                    )

                    IconTextClickable(
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
            modifier = Modifier.fillMaxWidth()
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

    @Composable
    private fun InteractiveStarRating(
        rating: Int,
        onRatingChange: (Int) -> Unit,
        modifier: Modifier = Modifier,
        starSize: Dp = 32.dp,
        selectedColor: Color = Color(0xFFFFD700),
        unselectedColor: Color = Color.Gray
    ) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Rate $i stars",
                    tint = if (i <= rating) selectedColor else unselectedColor,
                    modifier = Modifier
                        .size(starSize)
                        .clickable { onRatingChange(i) }
                )
            }
        }
    }
}