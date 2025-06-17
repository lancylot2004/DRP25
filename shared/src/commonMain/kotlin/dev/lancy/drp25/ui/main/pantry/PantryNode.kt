// File: dev/lancy/drp25/ui/main/pantry/PantryNode.kt
package dev.lancy.drp25.ui.main.pantry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Scan
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.Client
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.rememberPantryIngredientsManager
import dev.lancy.drp25.utilities.httpClient
import kotlinx.coroutines.runBlocking

class PantryNode(
    nodeContext: NodeContext,
    parent: MainNode,
) : LeafNode(nodeContext),
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {

    @Composable
    override fun Content(modifier: Modifier) {
        // Remove the scanner state from here as it's now handled in IngredientsNode
        PantryScreen(modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showUtensilsScreen by remember { mutableStateOf(false) }
    val tabTitles = listOf("Fridge", "Freezer", "All")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = Size.BarLarge)
                    .background(Color.Transparent),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                IngredientsToolbar(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it },
                    tabTitles = tabTitles,
                    modifier = Modifier.weight(1f),
                )

                UtensilsToolbar(
                    onUtensilsClick = {
                        showUtensilsScreen = !showUtensilsScreen
                    },
                    isActive = showUtensilsScreen
                )
            }
        },
    ) { paddingValues ->
        if (showUtensilsScreen) {
            // Simplified UtensilsScreen call - no more manual state management needed
            UtensilsScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            // IngredientsNode now handles the scanner internally
            IngredientsNode(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
        }
    }
}