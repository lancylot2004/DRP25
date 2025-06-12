package dev.lancy.drp25.ui.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.components.spotlight.ui.fader.SpotlightFader
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.Cookie
import com.composables.icons.lucide.Logs
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Telescope
import com.composables.icons.lucide.Search
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.main.feed.FeedNode
import dev.lancy.drp25.ui.main.log.LogNode
import dev.lancy.drp25.ui.main.me.MeNode
import dev.lancy.drp25.ui.main.pantry.PantryNode
import dev.lancy.drp25.ui.main.search.SearchNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.ui.shared.NavProvider
import dev.lancy.drp25.ui.shared.NavTarget
import dev.lancy.drp25.ui.shared.StaticNavTarget
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Const
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.selectedIndex

class MainNode(
    nodeContext: NodeContext,
    parent: RootNode,
    private val spotlight: Spotlight<MainTarget> =
        Spotlight(
            model =
                SpotlightModel(
                    items = MainTarget.entries,
                    initialActiveIndex = MainTarget.entries.indexOf(MainTarget.default).toFloat(),
                    savedStateMap = nodeContext.savedStateMap,
                ),
            visualisation = {
                SpotlightFader(
                    uiContext = it,
                    defaultAnimationSpec = spring(stiffness = Spring.StiffnessHigh),
                )
            },
        ),
) : Node<MainNode.MainTarget>(spotlight, nodeContext),
    NavProvider<MainNode.MainTarget>,
    NavConsumer<RootNode.RootTarget, RootNode> by NavConsumerImpl(parent) {
    @Parcelize
    sealed class MainTarget(
        val title: String,
        val icon: @Composable () -> ImageVector,
        val nodeFactory: (NodeContext, MainNode) -> Node<*>,
    ) : Parcelable,
        StaticNavTarget by Companion {
        /**
         * [Feed] contains the infinite scrolling list of events.
         */
        data object Feed : MainTarget(
            "Feed",
            { Lucide.Telescope },
            { context, parent -> FeedNode(context, parent) },
        )

        /**
         * [Search] contains the infinite scrolling list of events.
         */
        data object Search : MainTarget(
            "Search",
            { Lucide.Search },
            { context, parent -> SearchNode(context, parent) },
        )

        /**
         * [Pantry] contains the ingredients and utensils in the kitchen.
         */
        data object Pantry : MainTarget(
            "Pantry",
            { Lucide.Cookie },
            { context, parent -> PantryNode(context, parent) },
        )

        /**
         * [Log] contains previously cooked and saved recipes.
         */
        data object Log : MainTarget(
            "Log",
            { Lucide.Logs },
            { context, parent -> LogNode(context, parent) }
        )

        /**
         * [Me] contains the user's information and settings.
         */
        data object Me : MainTarget(
            "Me",
            { Lucide.CircleUserRound },
            { context, parent -> MeNode(context, parent) },
        )

        companion object : StaticNavTarget {
            override val default: MainTarget = Feed

            override val entries: List<MainTarget> = listOf(Feed, Search, Pantry, Log, Me)
        }
    }

    override fun buildChildNode(
        navTarget: MainTarget,
        nodeContext: NodeContext,
    ): Node<*> = navTarget.nodeFactory(nodeContext, this)

    override fun onChildFinished(child: Node<*>) {
        // TODO: Navigate to [LoggedOut] once the user log out.
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val hazeState = remember { HazeState() }

        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(1f)
                    .fillMaxWidth()
                    .height(Size.BarLarge)
                    .background(Color.Transparent)
                    // Sneaky fix for glitter at bottom of screen.
                    .offset(y = 2.dp)
                    .hazeChild(hazeState, style = Const.HazeStyle),
            ) {
                MainTarget.entries.forEach {
                    val index = MainTarget.entries.indexOf(it)
                    NavigationItem(
                        title = it.title,
                        selected = spotlight.selectedIndex() == index,
                        icon = it.icon,
                    ) { spotlight.activate(index.toFloat()) }
                }
            }

            AppyxNavigationContainer(
                modifier = Modifier.haze(hazeState, Const.HazeStyle),
                appyxComponent = spotlight,
            )
        }
    }

    @Composable
    private fun NavigationItem(
        title: String,
        selected: Boolean,
        icon: @Composable () -> ImageVector,
        onClick: () -> Unit,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(Shape.RoundedSmall)
                .clickable { onClick() },
        ) {
            Icon(
                imageVector = icon(),
                contentDescription = title,
                modifier = Modifier
                    .padding(start = Size.Padding, end = Size.Padding, top = Size.Padding)
                    .size(Size.IconSmall),
                tint = if (!selected) ColourScheme.onBackground else ColourScheme.primary,
            )

            Spacer(Modifier.height(Size.Padding))

            Text(
                title,
                color = if (!selected) ColourScheme.onBackground else ColourScheme.primary,
                style = Typography.labelSmall,
                modifier = Modifier
                    .padding(start = Size.Padding, end = Size.Padding, bottom = Size.Padding),
            )
        }
    }

    override suspend fun <U : NavTarget> navigate(target: MainTarget): Node<U> =
        attachChild { spotlight.activate(MainTarget.entries.indexOf(target).toFloat()) }
}
