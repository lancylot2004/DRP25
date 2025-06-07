package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.components.spotlight.operation.updateElements
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.gesture.GestureSettleConfig
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.utilities.allRecipes
import dev.lancy.drp25.ui.main.feed.FeedNode.FeedTarget
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import kotlinx.coroutines.launch
import dev.lancy.drp25.ui.shared.NavProvider
import dev.lancy.drp25.ui.shared.NavTarget
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.fetchAllRecipes
import kotlin.jvm.JvmInline

class FeedNode(
    nodeContext: NodeContext,
    parent: MainNode,
    private val spotlight: Spotlight<FeedTarget> = Spotlight(
        model = SpotlightModel(
            items = if (allRecipes.value.isNotEmpty()) allRecipes.value.map { FeedTarget(it.id) } else listOf(),
            initialActiveIndex = 0f,
            savedStateMap = nodeContext.savedStateMap,
        ),
        visualisation = {
            SpotlightSlider(
                uiContext = it,
                initialState = SpotlightModel.State(
                    positions = listOf(),
                    activeIndex = 0f,
                ),
            )
        },

        // Animations
        animationSpec = tween(
            durationMillis = 40,
            easing = FastOutSlowInEasing
        ),

        gestureFactory = { bounds -> SpotlightSlider.Gestures(
            bounds,
            orientation = Orientation.Horizontal
        )},

        // Incomplete gesture configuration
        gestureSettleConfig = GestureSettleConfig(
            completionThreshold = 0.3f,
            completeGestureSpec = spring(),
            revertGestureSpec = spring(),
        ),
    )
) : Node<FeedTarget>(spotlight, nodeContext),
    NavProvider<FeedTarget>,
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    @JvmInline
    value class FeedTarget(val id: String) : NavTarget

    override fun buildChildNode(
        navTarget: FeedTarget,
        nodeContext: NodeContext,
    ): Node<*> = FeedCard(nodeContext, this, navTarget.id) // TODO

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        // Ensure recipes are loaded and spotlight is updated
        LaunchedEffect(allRecipes.value.size) {
            if (allRecipes.value.isNotEmpty()) {
                try {
                    val feedTargets = allRecipes.value.map { FeedTarget(it.id) }
                    spotlight.updateElements(feedTargets)
                } catch (e: Exception) {
                    println("Error updating spotlight: ${e.message}")
                    e.printStackTrace()
                }
            }
        }

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = Size.BigPadding, end = Size.BigPadding, top = Size.Padding)
                    .fillMaxWidth()
            ) {
                Text("Feed", color = Color.White, style = Typography.titleMedium)

                IconButton(onClick = { scope.launch { sheetState.expand() } }) {
                    Icon(
                        imageVector = Lucide.Settings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(Size.IconMedium)
                    )
                }
            }

            AppyxNavigationContainer(spotlight)
        }

        if (sheetState.isVisible) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(Size.ModalSheetHeight),
                shape = Shape.RoundedLarge,
                sheetState = sheetState,
                onDismissRequest = { scope.launch { sheetState.hide() } },
            ) { FilterContent() }
        }
    }

    override suspend fun <C : NavTarget> navigate(target: FeedTarget): Node<C> = attachChild {
        val ind = spotlight.elements.value.onScreen?.indexOfFirst { it.interactionTarget == target }
        if (ind == null || ind < 0) { TODO() }
        spotlight.activate(ind.toFloat())
    }
}
