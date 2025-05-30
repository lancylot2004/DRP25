package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.ui.fader.SpotlightFader
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.ui.main.MainNode.MainTarget
import dev.lancy.drp25.utilities.Shape

class FeedCard(
    nodeContext: NodeContext,
    recipe: Recipe,
) : LeafNode(nodeContext) {
    @Composable
    override fun Content(modifier: Modifier) {
        Box(modifier.clip(Shape.RoundedLarge)) {

        }
    }
}