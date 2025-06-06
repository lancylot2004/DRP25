package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.components.spotlight.operation.updateElements
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.gesture.GestureSettleConfig
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.data.fetchRecipes
import dev.lancy.drp25.data.allRecipes
import dev.lancy.drp25.data.example
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.ui.shared.NavProvider
import dev.lancy.drp25.ui.shared.NavTarget

class FeedNode(
    nodeContext: NodeContext,
    parent: MainNode,
    private val spotlight: Spotlight<Recipe> = createSpotlight(),
) : Node<Recipe>(spotlight, nodeContext),
    NavProvider<Recipe>,
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {

    private var dataLoaded = mutableStateOf(allRecipes.isNotEmpty())

    companion object {
        private fun createSpotlight(): Spotlight<Recipe> {
            // Start with example data to prevent crashes
            val initialRecipes = if (allRecipes.isNotEmpty()) {
                allRecipes
            } else {
                listOf() //listOf(example, example, example)
            }

            return Spotlight(
                model = SpotlightModel(
                    items = initialRecipes,
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
                animationSpec = tween(
                    durationMillis = 40,
                    easing = FastOutSlowInEasing
                ),
                gestureFactory = { bounds -> SpotlightSlider.Gestures(
                    bounds,
                    orientation = Orientation.Horizontal
                )},
                gestureSettleConfig = GestureSettleConfig(
                    completionThreshold = 0.3f,
                    completeGestureSpec = spring(),
                    revertGestureSpec = spring(),
                ),
            )
        }
    }

    override fun buildChildNode(
        navTarget: Recipe,
        nodeContext: NodeContext,
    ): Node<*> = FeedCard(nodeContext, this, navTarget)

    @Composable
    override fun Content(modifier: Modifier) {
        val isDataLoaded by dataLoaded

        LaunchedEffect(Unit) {
            if (!isDataLoaded && allRecipes.isEmpty()) {
                fetchRecipes()

                // Update the spotlight with the new data
                if (allRecipes.isNotEmpty()) {
                    try {
                        spotlight.updateElements(allRecipes)
                        dataLoaded.value = true
                    } catch (e: Exception) {
                        println("Error updating spotlight: ${e.message}")
                    }
                }
            }
        }

        AppyxNavigationContainer(spotlight)
    }

    override suspend fun <C : NavTarget> navigate(target: Recipe): Node<C> = attachChild {
        val ind = spotlight.elements.value.onScreen?.indexOfFirst { it.interactionTarget == target }
        if (ind == null || ind < 0) {
            throw IllegalArgumentException("Recipe not found in current spotlight")
        }
        spotlight.activate(ind.toFloat())
    }
}