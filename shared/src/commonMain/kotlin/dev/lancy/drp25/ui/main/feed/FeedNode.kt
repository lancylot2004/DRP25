package dev.lancy.drp25.ui.main.feed

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.ui.stack3d.BackStack3D
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.ui.sliderscale.SpotlightSliderScale
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl

class FeedNode(
    nodeContext: NodeContext,
    parent: MainNode,
    val spotlight: Spotlight<Unit> = Spotlight(
        model = SpotlightModel(items = listOf(Unit), savedStateMap = mapOf()),
        visualisation = { SpotlightSliderScale(
            uiContext = it,
            initialState = SpotlightModel.State(
                positions = listOf(),
                activeIndex = 0f,
            )
        )}
    )
): LeafNode(nodeContext),
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    @Composable
    override fun Content(modifier: Modifier) {
        AppyxNavigationContainer(spotlight)
    }
}
