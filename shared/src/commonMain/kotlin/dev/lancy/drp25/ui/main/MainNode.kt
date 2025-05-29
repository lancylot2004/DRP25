package dev.lancy.drp25.ui.main

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.times
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
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Telescope
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.main.feed.FeedNode
import dev.lancy.drp25.ui.main.me.MeNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.ui.shared.NavProvider
import dev.lancy.drp25.ui.shared.NavTarget
import dev.lancy.drp25.ui.shared.StaticNavTarget
import dev.lancy.drp25.utilities.Animation
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Const
import dev.lancy.drp25.utilities.ScreenSize
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.textWidth
import kotlin.math.roundToInt

class MainNode(
    nodeContext: NodeContext,
    parent: RootNode,
    private val spotlight: Spotlight<MainTarget> = Spotlight(
        model = SpotlightModel(
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
    ) : Parcelable, StaticNavTarget by Companion {
        /**
         * [Feed] contains the infinite scrolling list of events.
         */
        data object Feed : MainTarget(
            "Feed",
            { Lucide.Telescope },
            { context, parent -> FeedNode(context, parent) },
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
            override val default: MainTarget = Me

            override val entries: List<MainTarget> = listOf(Feed, Me)
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
        val selectedIndex = spotlight.activeIndex
            .collectAsState().value.toInt()
        val hazeState = remember { HazeState() }

        val overlayWidth = 2 * Size.Padding + max(
            textWidth(
                MainTarget.entries[selectedIndex].title,
                Typography.labelSmall
            ),
            Size.IconSmall
        )

        var overlayOffsetX by remember { mutableStateOf(0f) }
        val overlayAnimatedX by animateFloatAsState(
            overlayOffsetX,
            label = "overlayOffsetX",
            animationSpec = Animation.EnterLong
        )

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
                    .hazeChild(hazeState),
            ) {
                MainTarget.entries.forEach {
                    val index = MainTarget.entries.indexOf(it)
                    NavigationItem(
                        title = it.title,
                        selected = selectedIndex == index,
                        icon = it.icon,
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            if (selectedIndex == index) {
                                // Rely on navigation bar occupying width of screen.
                                overlayOffsetX = coordinates.boundsInRoot().left
                            }
                        }
                    ) { spotlight.activate(index.toFloat()) }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset { IntOffset(overlayAnimatedX.roundToInt(), (-Size.Padding).roundToPx()) }
                    .width(overlayWidth)
                    .height(Size.BarLarge - 2 * Size.Padding)
                    .clip(Shape.RoundedMedium)
                    .background(ColourScheme.onBackground)
                    .alpha(0.5f)
            )

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
        modifier: Modifier,
        onClick: () -> Unit
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .clip(Shape.RoundedSmall)
                .clickable { onClick() },
        ) {
            Icon(
                imageVector = icon(),
                contentDescription = title,
                modifier = Modifier.padding(
                    top = Size.Padding,
                    start = Size.Padding,
                    end = Size.Padding
                ).size(Size.IconSmall),
            )

            Spacer(Modifier.height(Size.Padding))

            Text(
                title,
                style = Typography.labelSmall,
                modifier = Modifier.padding(
                    bottom = Size.Padding,
                    start = Size.Padding,
                    end = Size.Padding
                ),
            )
        }
    }

    override suspend fun <U : NavTarget> navigate(target: MainTarget): Node<U> =
        attachChild { spotlight.activate(MainTarget.entries.indexOf(target).toFloat()) }
}
