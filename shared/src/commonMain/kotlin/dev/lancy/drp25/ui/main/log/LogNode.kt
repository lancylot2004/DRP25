package dev.lancy.drp25.ui.main.log

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.fetchSavedRecipes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogNode(
    nodeContext: NodeContext,
    parent: MainNode,
) : LeafNode(nodeContext),
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    private fun CoroutineScope.updateRecipes(onUpdate: (List<Recipe>) -> Unit) {
        launch {
            val newRecipes = fetchSavedRecipes()
            withContext(Dispatchers.Main) { onUpdate(newRecipes) }
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        // Fetch saved recipes.
        val scope = rememberCoroutineScope()
        var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }

        LaunchedEffect(this.lifecycleScope) {
            scope.updateRecipes { recipes = it }
        }

        LazyColumn(
            modifier = Modifier.padding(
                start = Size.Padding,
                end = Size.Padding,
                top = Size.Padding,
                bottom = Size.BarLarge,
            ),
        ) {
            items(recipes) { recipeId -> logEntry(recipeId) }
        }
    }
}
