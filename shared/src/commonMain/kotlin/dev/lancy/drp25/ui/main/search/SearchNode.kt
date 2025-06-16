package dev.lancy.drp25.ui.main.search
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.CircleX
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.Client
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.rememberPersisted
import kotlinx.coroutines.launch

class SearchNode(
    nodeContext: NodeContext,
    parent: MainNode,
) : LeafNode(nodeContext),
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val scope = rememberCoroutineScope()
        var recipes by rememberSaveable { mutableStateOf<List<Recipe>>(emptyList()) }
        val queryState = rememberSaveable { mutableStateOf("") }
        var searchPerformed by rememberSaveable { mutableStateOf(false) }

        val previousSearchesPersistence = rememberPersisted("previous_searches") { emptyList<String>() }
        val previousSearches by previousSearchesPersistence.state.collectAsState()

        val keyboardController = LocalSoftwareKeyboardController.current
        val hideKeyboardModifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { keyboardController?.hide() })
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .then(hideKeyboardModifier)
        ) {
            SearchBar(
                query = queryState.value,
                onQueryChange = { queryState.value = it },
                onSearch = {
                    keyboardController?.hide()
                    val query = queryState.value.trim()
                    if (query.isEmpty()) {
                        searchPerformed = false
                        recipes = emptyList()
                    } else {
                        if (!previousSearches.contains(query)) {
                            previousSearchesPersistence.update {
                                listOf(query) + filterNot { it == query }.take(9)
                            }
                        }
                        searchPerformed = true
                        scope.launch {
                            recipes = if (query.equals("all", ignoreCase = true)) {
                                Client.fetchRecipes()
                            } else {
                                Client.searchRecipes(query)
                            }
                        }
                    }
                },
                active = false,
                onActiveChange = {},
                placeholder = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = null,
                            modifier = Modifier.padding(end = Size.Padding),
                            tint = ColourScheme.onSurface,
                        )
                        Text(
                            "Search recipes",
                            color = ColourScheme.onSurface,
                            style = Typography.bodyLarge,
                        )
                    }
                },
                trailingIcon = {
                    if (queryState.value.isNotEmpty()) {
                        Icon(
                            imageVector = Lucide.CircleX,
                            contentDescription = "Clear",
                            tint = ColourScheme.onSurface,
                            modifier = Modifier
                                .clickable {
                                    queryState.value = ""
                                    searchPerformed = false
                                },
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Size.Padding)
                    .clip(Shape.RoundedSmall)
                    .background(ColourScheme.surface)
                    .padding(Size.Padding),
                content = {},
            )

            Spacer(Modifier.height(Size.Spacing))

            if (!searchPerformed) {
                Column(modifier = Modifier.padding(horizontal = Size.BigPadding)) {
                    Text(
                        "Recent",
                        style = Typography.titleSmall,
                        color = ColourScheme.onSurface,
                        modifier = Modifier.padding(bottom = Size.Padding),
                    )
                    previousSearches.forEach { search ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Size.Padding),
                        ) {
                            Icon(
                                imageVector = Lucide.History,
                                contentDescription = null,
                                tint = ColourScheme.primary,
                                modifier = Modifier.size(Size.IconMedium),
                            )
                            Text(
                                text = search,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = Size.Padding)
                                    .clickable {
                                        queryState.value = search
                                        searchPerformed = true
                                        scope.launch {
                                            recipes = if (search.equals("all", ignoreCase = true)) {
                                                Client.fetchRecipes()
                                            } else {
                                                Client.searchRecipes(search)
                                            }
                                        }
                                    },
                                color = ColourScheme.onSurface,
                                style = Typography.bodyLarge,
                            )
                            Icon(
                                imageVector = Lucide.CircleX,
                                contentDescription = "Remove",
                                tint = ColourScheme.onSurface,
                                modifier = Modifier
                                    .size(Size.IconMedium)
                                    .clickable {
                                        previousSearchesPersistence.update {
                                            filter { it != search }
                                        }
                                    },
                            )
                        }
                    }
                    Divider(
                        color = ColourScheme.outline,
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = Size.Padding),
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Size.Padding),
                    verticalArrangement = Arrangement.spacedBy(Size.Padding),
                    horizontalArrangement = Arrangement.spacedBy(Size.Padding),
                ) {
                    items(recipes) { recipe ->
                        SearchCard(
                            modifier = Modifier.fillMaxWidth(),
                            recipe = recipe,
                            tapCallback = {
                                scope.launch {
                                    this@SearchNode
                                        .navParent
                                        .superNavigate<RootNode.RootTarget>(
                                            RootNode.RootTarget.Recipe(
                                                recipe,
                                            ),
                                        )
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
