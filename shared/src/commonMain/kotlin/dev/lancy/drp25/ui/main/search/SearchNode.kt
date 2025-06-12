package dev.lancy.drp25.ui.main.search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import dev.lancy.drp25.ui.main.MainNode
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Settings
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.ColourScheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.utilities.fetchRecipes
import dev.lancy.drp25.ui.main.feed.FeedCard
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.Shape
import kotlinx.coroutines.launch
import dev.lancy.drp25.utilities.Typography

class SearchNode(nodeContext: NodeContext, parent: MainNode): LeafNode(nodeContext),
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            recipes = fetchRecipes()
        }

        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                query = "",
                onQueryChange = {},
                onSearch = {},
                content = {},
                active = false,
                onActiveChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Size.Padding)
                    .clip(Shape.RoundedSmall)
                    .background(ColourScheme.surface)
                    .padding(Size.Padding),
                placeholder = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = null,
                            modifier = Modifier.padding(end = Size.Padding),
                            tint = ColourScheme.onSurface
                        )
                        Text(
                            "Search recipes",
                            color = ColourScheme.onSurface,
                            style = Typography.bodyLarge
                        )
                    }
                }
            )

            Spacer(Modifier.height(Size.Spacing))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Size.Padding),
                verticalArrangement = Arrangement.spacedBy(Size.Padding),
                horizontalArrangement = Arrangement.spacedBy(Size.Padding)
            ) {
                items(recipes) { recipe ->
                    SearchCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        recipe = recipe,
                        tapCallback = {
                            scope.launch {
                                this@SearchNode
                                    .navParent
                                    .superNavigate<RootNode.RootTarget>(RootNode.RootTarget.Recipe(recipe))
                            }
                        }
                    )
                }
            }
        }
    }
}