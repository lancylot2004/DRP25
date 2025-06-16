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
import dev.lancy.drp25.data.IngredientItem
import dev.lancy.drp25.data.getDefaultIngredients
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.Client
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import kotlinx.coroutines.runBlocking

class PantryNode(
    nodeContext: NodeContext,
    parent: MainNode,
) : LeafNode(nodeContext),
    NavConsumer<MainNode.MainTarget, MainNode> by NavConsumerImpl(parent) {
    @Composable
    override fun Content(modifier: Modifier) {
        var showScanner by remember { mutableStateOf(false) }

        if (showScanner) {
            QRScannerView { scannedCode ->
                // Handle the scanned barcode
                println("Scanned barcode: $scannedCode")
                showScanner = false
                val product = runBlocking { Client.fetchProduct(scannedCode) }
                print(product)
            }
        } else {
            PantryScreen(
                modifier = modifier,
                onScanClick = { showScanner = true },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    modifier: Modifier = Modifier,
    onScanClick: () -> Unit = {},
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showUtensilsScreen by remember { mutableStateOf(false) }
    val tabTitles = listOf("Fridge", "Freezer", "All")

    // Centralized state for all ingredients
    val allIngredients = remember { mutableStateListOf<IngredientItem>().apply { addAll(getDefaultIngredients()) } }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pantry",
                        style = Typography.titleMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                actions = {
                    IconButton(
                        onClick = onScanClick,
                    ) {
                        Icon(
                            imageVector = Lucide.Scan,
                            contentDescription = "Scan ingredient",
                            tint = Color.White,
                            modifier = Modifier.size(Size.IconMedium),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
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
                )
            }
        },
    ) { paddingValues ->
        if (showUtensilsScreen) {
            UtensilsScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
        } else {
            IngredientsNode(
                selectedTabIndex = selectedTabIndex,
                allIngredients = allIngredients,
                onQuantityChange = { updatedIngredient ->
                    val index = allIngredients.indexOfFirst { it.name == updatedIngredient.name }
                    if (index != -1) {
                        allIngredients[index] = updatedIngredient
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
        }
    }
}
