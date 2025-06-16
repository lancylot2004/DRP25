package dev.lancy.drp25.ui.main.log

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.*
import dev.lancy.drp25.data.*
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.Client
import dev.lancy.drp25.utilities.Client.saveNewRecipe
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.rememberSavedRecipeIdsManager
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
            val newRecipes = Client.fetchSavedRecipes()
            withContext(Dispatchers.Main) { onUpdate(newRecipes) }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val scope = rememberCoroutineScope()
        val manager = rememberSavedRecipeIdsManager()
        val savedIds by manager.state.collectAsState(initial = emptySet())
        var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var showBottomSheet by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            recipes = Client.fetchRecipes().filter {it.id in savedIds}
        }

        LaunchedEffect(savedIds) {
            recipes = Client.fetchRecipes().filter { it.id in savedIds }
        }

        Box(Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(start = Size.BigPadding, end = Size.BigPadding, top = Size.Padding)
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = "Saved meals",
                        color = Color.White,
                        style = Typography.titleMedium
                    )

                    Button(
                        onClick = { showBottomSheet = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE)
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Plus,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Recipe", color = Color.White)
                    }
                }

                // Recipe List
                LazyColumn(
                    modifier = Modifier.padding(
                        start = Size.Padding,
                        end = Size.Padding,
                        top = Size.Padding,
                        bottom = Size.BarLarge,
                    ),
                ) {
                    items(recipes.size) { index ->
                        val recipe = recipes[index]
                        Column {
                            logEntry(recipe) {
                                scope.launch {
                                    navParent.superNavigate<Nothing>(RootNode.RootTarget.Recipe(recipe))
                                }
                            }
                            if (index < recipes.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Sheet - Fixed to bottom of screen
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState,
                containerColor = Color(0xFF1E1E1E),
                contentColor = Color.White,
                dragHandle = {
                    Surface(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.Gray,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            Modifier.size(width = 32.dp, height = 4.dp)
                        )
                    }
                }
            ) {
                // Wrapping in a Column
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RecipeForm(
                        onSaveRecipe = { recipe ->
                            scope.launch {
                                val success = Client.saveNewRecipe(recipe, manager)
                                if (success) {
                                    showBottomSheet = false
                                    //scope.updateRecipes { recipes = it }
                                }
                            }
                        },
                        onCancel = { showBottomSheet = false }
                    )
                }
            }
        }
    }
}