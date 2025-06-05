package dev.lancy.drp25.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import dev.lancy.drp25.data.example
import dev.lancy.drp25.ui.RootNode.RootTarget.LoggedOut
import dev.lancy.drp25.ui.RootNode.RootTarget.Main
import dev.lancy.drp25.ui.RootNode.RootTarget.Recipe
import dev.lancy.drp25.ui.loggedOut.LoggedOutNode
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.overlay.recipe.RecipeNode
import dev.lancy.drp25.ui.shared.NavProvider
import dev.lancy.drp25.ui.shared.NavTarget
import dev.lancy.drp25.utilities.currentTarget

class RootNode(
    nodeContext: NodeContext,
    private val backStack: BackStack<RootTarget> = BackStack(
        model = BackStackModel(
            // TODO: Update after authentication is finalised.
            initialTargets = listOf(Main),
            savedStateMap = nodeContext.savedStateMap,
        ),
        visualisation = { BackStackFader(it) },
    ),
) : Node<RootNode.RootTarget>(backStack, nodeContext),
    NavProvider<RootNode.RootTarget> {
    @Parcelize
    sealed class RootTarget :
        Parcelable,
        NavTarget {
        /**
         * [Main] contains all primary pages of the application, such as Search.
         */
        data object Main : RootTarget()

        /**
         * [LoggedOut] contains the authentication flows and onboarding.
         */
        data object LoggedOut : RootTarget()

        /**
         * [Recipe] is an overlay that appears above the [Main] page.
         */
        data object Recipe : RootTarget()
    }

    override fun buildChildNode(
        navTarget: RootTarget,
        nodeContext: NodeContext,
    ): Node<*> = when (navTarget) {
        Main -> MainNode(nodeContext, this)
        LoggedOut -> LoggedOutNode(nodeContext)
        Recipe -> RecipeNode(nodeContext, example, this) { backStack.pop() }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        AppyxNavigationContainer(
            appyxComponent = backStack,
            modifier = modifier,
        )
    }

    override suspend fun <C : NavTarget> navigate(target: RootTarget): Node<C> =
        attachChild<Node<C>> {
            when (backStack.currentTarget()) {
                // Destroy [LoggedOut] and [Overlay] since it never needs to be preserved.
                LoggedOut, Recipe -> backStack.replace(target)
                Main ->
                    when (target) {
                        // Destroy [Main] if the user logs out.
                        LoggedOut -> backStack.replace(target)
                        // Otherwise push overlay above [Main].
                        else -> backStack.push(target)
                    }
            }
        }
}
