package dev.lancy.drp25.ui.main.log

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.fetchSavedRecipes
import kotlinx.coroutines.launch

class LogNode(
    nodeContext: NodeContext,
    parent: MainNode
) : LeafNode(nodeContext) {
    @Composable
    override fun Content(modifier: Modifier) {
        // Fetch saved recipes.
        val scope = rememberCoroutineScope()
        val savedRecipes = mutableStateOf(listOf<String>())

        scope.launch {
            savedRecipes.value = fetchSavedRecipes()
        }

        LazyColumn(
            modifier = Modifier.padding(
                start = Size.Padding,
                end = Size.Padding,
                top = Size.Padding,
                bottom = Size.BarLarge
            )
        ) {
            items(savedRecipes.value) { recipeId ->
                logEntry(recipeId)
            }
        }
    }
}