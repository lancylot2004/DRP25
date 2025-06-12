package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import dev.lancy.drp25.data.FilterValues
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.Client
import dev.lancy.drp25.utilities.ScreenSize
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.rememberPersisted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class FeedNode(
    nodeContext: NodeContext,
    parent: MainNode,
) : LeafNode(nodeContext),
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    private fun CoroutineScope.updateRecipes(
        filterValues: FilterValues,
        onUpdate: (List<Recipe>) -> Unit,
    ) {
        launch {
            val newRecipes = Client.fetchRecipes(filterValues)
            withContext(Dispatchers.Main) { onUpdate(newRecipes) }
        }
    }

    private fun CoroutineScope.detailsFor(recipe: Recipe) {
        launch {
            this@FeedNode
                .navParent
                .superNavigate<RootNode.RootTarget>(RootNode.RootTarget.Recipe(recipe))
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scrollState = rememberLazyListState()
        val scope = rememberCoroutineScope()

        var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }

        val filterPersistence = rememberPersisted("filters") { FilterValues() }
        val filterValues by filterPersistence.state.collectAsState()

        LaunchedEffect(this.lifecycleScope) {
            scope.updateRecipes(filterValues) { recipes = it }
        }

        Box(Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = Size.BigPadding, end = Size.BigPadding, top = Size.Padding)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
            ) {
                Text("Feed", color = Color.White, style = Typography.titleMedium)

                IconButton(onClick = { scope.launch { sheetState.expand() } }) {
                    Icon(
                        imageVector = Lucide.Settings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(Size.IconMedium),
                    )
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
                    // Take into account header.
                    .padding(top = Size.IconMedium + Size.Padding),
                state = scrollState,
                flingBehavior = rememberSnapFlingBehavior(scrollState),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(recipes) { recipe ->
                    FeedCard(
                        modifier = Modifier
                            .size(ScreenSize.width, ScreenSize.height)
                            .padding(Size.Padding),
                        recipe = recipe,
                        tapCallback = { scope.detailsFor(recipe) },
                    )
                }

                if (recipes.isEmpty()) {
                    item {
                        Text(
                            text = "No recipes available",
                            color = Color.White,
                            style = Typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            AnimatedVisibility(sheetState.isVisible) {
                ModalBottomSheet(
                    modifier = Modifier
                        .fillMaxHeight(Size.ModalSheetHeight)
                        .align(Alignment.BottomCenter),
                    shape = Shape.RoundedLarge,
                    sheetState = sheetState,
                    onDismissRequest = {
                        scope.launch {
                            sheetState.hide()
                            scope.updateRecipes(filterPersistence.state.value) { recipes = it }
                        }
                    },
                    dragHandle = {},
                ) {
                    Column {
                        FilterContent(filterPersistence)
                    }
                }
            }
        }
    }
}
