package dev.lancy.drp25.ui.main.pantry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.lancy.drp25.utilities.Size
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import dev.lancy.drp25.ui.main.MainNode

class PantryNode(
    nodeContext: NodeContext,
    parent: MainNode
) : LeafNode(nodeContext) {

    @Composable
    override fun Content(modifier: Modifier) {
        PantryScreen(modifier)
    }
}

@Composable
fun PantryScreen(modifier: Modifier = Modifier) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showUtensilsScreen by remember { mutableStateOf(false) }
    val tabTitles = listOf("Ingredients") // listOf("Fridge", "Freezer", "All")

    Scaffold(
        modifier = modifier,
        bottomBar = {
            // Arrange the ingredients toolbar and utensils button in one Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = Size.BarLarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ingredients Toolbar Card
                IngredientsToolbar(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it },
                    modifier = Modifier.weight(1f)
                )

                // Utensils Toolbar Card - now toggles instead of just opening
                UtensilsToolbar(
                    onUtensilsClick = {
                        showUtensilsScreen = !showUtensilsScreen
                    }
                )
            }
        }
    ) { innerPadding ->
        // Main screen content or Utensils overlay
        if (showUtensilsScreen) {
            // Show utensils screen as overlay
            UtensilsScreen(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Main pantry content - now shows the actual ingredients
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                IngredientsNode(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}