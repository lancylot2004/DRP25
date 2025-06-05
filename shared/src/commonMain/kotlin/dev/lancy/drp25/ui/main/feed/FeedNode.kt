package dev.lancy.drp25.ui.main.feed

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.components.spotlight.ui.sliderscale.SpotlightSliderScale
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.data.example
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
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
        gestureFactory = { bounds -> SpotlightSlider.Gestures(bounds, orientation = Orientation.Horizontal) },
    ),
) : Node<Recipe>(spotlight, nodeContext),
    NavProvider<Recipe>,
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    override fun buildChildNode(
        navTarget: Recipe,
        nodeContext: NodeContext,
    ): Node<*> = FeedCard(nodeContext, this, navTarget)

    @Composable
    override fun Content(modifier: Modifier) {
        AppyxNavigationContainer(spotlight)
    }

    override suspend fun <C : NavTarget> navigate(target: Recipe): Node<C> = attachChild {
        val ind = spotlight.elements.value.onScreen?.indexOfFirst { it.interactionTarget == target }
        if (ind == null || ind < 0) { TODO() }
        spotlight.activate(ind.toFloat())
    }
}
