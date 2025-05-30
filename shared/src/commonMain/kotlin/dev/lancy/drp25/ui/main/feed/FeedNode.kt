package dev.lancy.drp25.ui.main.feed

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.components.spotlight.ui.sliderscale.SpotlightSliderScale
import com.bumble.appyx.interactions.gesture.GestureFactory
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.data.example
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl

class FeedNode(
    nodeContext: NodeContext,
    parent: MainNode,
    private val spotlight: Spotlight<FeedTarget> = Spotlight(
        model = SpotlightModel(
            items = listOf(FeedTarget("1", example), FeedTarget("1", example), FeedTarget("1", example)),
            savedStateMap = mapOf()
        ),
        visualisation = {
            SpotlightSliderScale(
                uiContext = it,
                initialState = SpotlightModel.State(
                    positions = listOf(),
                    activeIndex = 0f,
                ),
            )
        },
        gestureFactory = { bounds -> SpotlightSlider.Gestures(bounds, orientation = Orientation.Horizontal) },
    ),
) : Node<FeedNode.FeedTarget>(spotlight, nodeContext),
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    data class FeedTarget(
        val id: String,
        val recipe: Recipe,
    )

    override fun buildChildNode(
        navTarget: FeedTarget,
        nodeContext: NodeContext,
    ): Node<*> = FeedCard(nodeContext, navTarget.recipe)

    @Composable
    override fun Content(modifier: Modifier) {
        AppyxNavigationContainer(spotlight)
    }
}
