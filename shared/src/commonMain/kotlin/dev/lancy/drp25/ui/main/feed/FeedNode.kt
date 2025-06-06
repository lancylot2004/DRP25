package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.gesture.GestureSettleConfig
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.utils.multiplatform.Parcelize
import dev.lancy.drp25.data.Ingredient
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.data.Step
import dev.lancy.drp25.ui.RootNode.RootTarget
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.main.feed.FeedNode.FeedTarget
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.ui.shared.NavProvider
import dev.lancy.drp25.ui.shared.NavTarget
import dev.lancy.drp25.utilities.realm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.mongodb.kbson.ObjectId
import kotlin.jvm.JvmInline

import io.realm.kotlin.ext.query

class FeedNode(
    nodeContext: NodeContext,
    parent: MainNode,
    private val spotlight: Spotlight<FeedTarget> = Spotlight(
        model = SpotlightModel(
            items = realm.query<Recipe>().find().map { FeedTarget(it.id) },
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
) : Node<FeedTarget>(spotlight, nodeContext),
    NavProvider<FeedTarget>,
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    @JvmInline
    value class FeedTarget(val id: ObjectId) : NavTarget

    override fun buildChildNode(
        navTarget: FeedTarget,
        nodeContext: NodeContext,
    ): Node<*> = FeedCard(nodeContext, this, navTarget.id) // TODO

    @Composable
    override fun Content(modifier: Modifier) {
        val result = realm.query<Recipe>().find().map { FeedTarget(it.id) }

        AppyxNavigationContainer(spotlight)
    }

    override suspend fun <C : NavTarget> navigate(target: FeedTarget): Node<C> = attachChild {
        val ind = spotlight.elements.value.onScreen?.indexOfFirst { it.interactionTarget == target }
        if (ind == null || ind < 0) { TODO() }
        spotlight.activate(ind.toFloat())
    }
}
