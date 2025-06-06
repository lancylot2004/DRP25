package dev.lancy.drp25.ui.main.feed

import androidx.compose.foundation.clickable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.gesture.GestureSettleConfig
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.data.example
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import kotlinx.coroutines.launch
import dev.lancy.drp25.ui.shared.NavProvider
import dev.lancy.drp25.ui.shared.NavTarget

class FeedNode(
    nodeContext: NodeContext,
    parent: MainNode,
    private val spotlight: Spotlight<Recipe> = Spotlight(
        model = SpotlightModel(
            items = listOf(example, example, example),
            savedStateMap = mapOf(),
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
    ),
) : Node<Recipe>(spotlight, nodeContext),
    NavProvider<Recipe>,
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    override fun buildChildNode(
        navTarget: Recipe,
        nodeContext: NodeContext,
    ): Node<*> = FeedCard(nodeContext, this, navTarget)

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val sheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
        val scope = rememberCoroutineScope()

        ModalBottomSheetLayout(
            sheetContent = { FilterContent() },
            sheetState = sheetState
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(
                            start = Size.BigPadding,
                            end = Size.BigPadding,
                            top = Size.Padding
                        )
                        .fillMaxWidth()
                ) {
                    Text(
                        "Feed",
                        color = Color.White,
                        style = Typography.titleMedium,
                    )

                    Icon(
                        imageVector = Lucide.Settings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(Size.IconMedium).clickable { scope.launch { sheetState.show() } }
                    )
                }

                AppyxNavigationContainer(spotlight)
            }
        }
    }

    override suspend fun <C : NavTarget> navigate(target: Recipe): Node<C> = attachChild {
        val ind = spotlight.elements.value.onScreen?.indexOfFirst { it.interactionTarget == target }
        if (ind == null || ind < 0) { TODO() }
        spotlight.activate(ind.toFloat())
    }
}
