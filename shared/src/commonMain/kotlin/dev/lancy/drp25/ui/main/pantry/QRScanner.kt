// File: MultiScanQRScannerView.kt
package dev.lancy.drp25.ui.main.pantry

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.*
import dev.lancy.drp25.data.IngredientItem
import dev.lancy.drp25.data.formatQuantity
import dev.lancy.drp25.utilities.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.publicvalue.multiplatform.qrcode.CameraPosition
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions

data class ScannedItem(
    val barcode: String,
    val productName: String,
    val ingredient: IngredientItem,
    val quantity: Double,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
)

@Composable
fun QRScannerView(
    ingredientsManager: PersistenceManager<List<IngredientItem>>,
    onClose: () -> Unit,
) {
    var scannedItems by remember { mutableStateOf(mutableListOf<ScannedItem>()) }
    var isScanning by remember { mutableStateOf(true) }
    var acceptResults by remember { mutableStateOf(true) }
    var scanMessage by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val currentIngredients by ingredientsManager.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(ColourScheme.surfaceVariant)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header with close button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .background(
                            color = ColourScheme.primaryContainer,
                            shape = CircleShape,
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close scanner",
                        tint = ColourScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Text(
                    text = "Scan Multiple Items",
                    style = MaterialTheme.typography.headlineSmall,
                    color = ColourScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                // Scanner toggle button
                IconButton(
                    onClick = { isScanning = !isScanning },
                    modifier = Modifier
                        .background(
                            color = if (isScanning) ColourScheme.primary else ColourScheme.surfaceVariant,
                            shape = CircleShape,
                        ),
                ) {
                    Icon(
                        imageVector = if (isScanning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isScanning) "Pause scanner" else "Resume scanner",
                        tint = if (isScanning) ColourScheme.onPrimary else ColourScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            // Scanner view
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .clip(Shape.RoundedMedium)
                    .clipToBounds()
                    .border(Size.Spacing, ColourScheme.outlineVariant, Shape.RoundedMedium),
                contentAlignment = Alignment.Center,
            ) {
                if (isScanning) {
                    ScannerWithPermissions(
                        modifier = Modifier.fillMaxSize(),
                        onScanned = onScanned@{ barcode ->
                            if (!acceptResults || isProcessing) return@onScanned true

                            // Prevent rapid successive scans of the same barcode
                            val recentScan = scannedItems.any {
                                it.barcode == barcode &&
                                        (Clock.System.now().toEpochMilliseconds() - it.timestamp) < 3000
                            }
                            if (recentScan) return@onScanned true

                            acceptResults = false
                            isProcessing = true
                            scanMessage = "Scanning..."

                            coroutineScope.launch {
                                try {
                                    fetchProductForMultiScan(
                                        barcode = barcode,
                                        httpClient = httpClient,
                                        currentIngredients = currentIngredients,
                                    ) { result ->
                                        scanMessage = when (result) {
                                            is ScanResult.Success -> {
                                                onScanSuccess(scannedItems, result, barcode)
                                            }
                                            is ScanResult.Error -> {
                                                result.message
                                            }
                                        }
                                    }
                                } finally {
                                    // Allow new scans after a short delay
                                    delay(1500)
                                    isProcessing = false
                                    delay(500)
                                    acceptResults = true
                                }
                            }
                            true
                        },
                        types = listOf(CodeType.EAN13, CodeType.EAN8),
                        cameraPosition = CameraPosition.BACK,
                    )
                } else {
                    // Paused state
                    Box(
                        modifier = Modifier.fillMaxSize().background(ColourScheme.surface.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = ColourScheme.onSurface.copy(alpha = 0.6f),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Scanner Paused",
                                style = MaterialTheme.typography.titleMedium,
                                color = ColourScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Review your items below\nTap play to continue scanning",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ColourScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                // Processing overlay
                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ColourScheme.surface.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = ColourScheme.primary,
                        )
                    }
                }
            }

            // Scan message
            AnimatedVisibility(
                visible = scanMessage != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
            ) {
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            scanMessage?.startsWith("✓") == true -> ColourScheme.primaryContainer
                            scanMessage?.startsWith("❌") == true -> ColourScheme.errorContainer
                            else -> ColourScheme.secondaryContainer
                        },
                    ),
                ) {
                    Text(
                        text = scanMessage ?: "",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            scanMessage?.startsWith("✓") == true -> ColourScheme.onPrimaryContainer
                            scanMessage?.startsWith("❌") == true -> ColourScheme.onErrorContainer
                            else -> ColourScheme.onSecondaryContainer
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scanned items list
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = ColourScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // List header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ColourScheme.surfaceVariant)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Scanned Items (${scannedItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )

                        if (scannedItems.isNotEmpty()) {
                            TextButton(onClick = {
                                scannedItems.clear()
                                scanMessage = "Cleared all items"
                            }) {
                                Text("Clear All")
                            }
                        }
                    }

                    // Items list
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(scannedItems.reversed(), key = { it.timestamp }) { item ->
                            ScannedItemRow(
                                item = item,
                                onRemove = {
                                    scannedItems.removeAll { it.timestamp == item.timestamp }
                                    scanMessage = "Removed ${item.ingredient.name}"
                                },
                                onQuantityChange = { newQuantity ->
                                    val index = scannedItems.indexOfFirst { it.timestamp == item.timestamp }
                                    if (index != -1) {
                                        scannedItems[index] = scannedItems[index].copy(quantity = newQuantity)
                                    }
                                },
                            )
                        }

                        if (scannedItems.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Icon(
                                            imageVector = Lucide.ScanLine,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = ColourScheme.onSurface.copy(alpha = 0.4f),
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "No items scanned yet",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = ColourScheme.onSurface.copy(alpha = 0.6f),
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Point camera at barcode to start",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ColourScheme.onSurface.copy(alpha = 0.5f),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        // Add all items to pantry
                        var addedCount = 0
                        scannedItems.forEach { item ->
                            val currentIngredient = currentIngredients.find {
                                it.name == item.ingredient.name
                            }
                            if (currentIngredient != null) {
                                val updatedIngredient = currentIngredient.copy(
                                    quantity = currentIngredient.quantity + item.quantity,
                                )
                                ingredientsManager.updateIngredient(updatedIngredient)
                                addedCount++
                            }
                        }

                        // Show success message and close
                        scanMessage = "✓ Added $addedCount items to pantry"
                        coroutineScope.launch {
                            delay(1000)
                            onClose()
                        }
                    },
                    enabled = scannedItems.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add ${scannedItems.size} Items")
                }
            }
        }

        // Auto-hide scan message
        LaunchedEffect(scanMessage) {
            if (scanMessage != null) {
                delay(3000)
                scanMessage = null
            }
        }
    }
}

private fun onScanSuccess(
    existingItems: MutableList<ScannedItem>,
    result: ScanResult.Success,
    barcode: String,
): String {
    val existingIndex = existingItems.indexOfFirst {
        it.ingredient.name == result.matchedIngredient.name
    }

    return if (existingIndex != -1) {
        // Update quantity for existing item
        val existingItem = existingItems[existingIndex]
        existingItems[existingIndex] = existingItem.copy(
            quantity = existingItem.quantity + result.quantity
        )
        "✓ Added more ${result.matchedIngredient.name}"
    } else {
        // Add new item
        existingItems.add(
            ScannedItem(
                barcode = barcode,
                productName = result.productName,
                ingredient = result.matchedIngredient,
                quantity = result.quantity,
            ),
        )
        "✓ Added ${result.productName}"
    }
}

@Composable
fun ScannedItemRow(
    item: ScannedItem,
    onRemove: () -> Unit,
    onQuantityChange: (Double) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ColourScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Ingredient icon
            IngredientIconView(
                icon = item.ingredient.icon,
                modifier = Modifier.size(48.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Item details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.ingredient.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.bodySmall,
                    color = ColourScheme.onSurface.copy(alpha = 0.7f),
                )
                Text(
                    text = "Barcode: ${item.barcode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColourScheme.onSurface.copy(alpha = 0.5f),
                )
            }

            // Quantity controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(
                    onClick = {
                        val newQty = (item.quantity - 0.5).coerceAtLeast(0.5)
                        onQuantityChange(newQty)
                    },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease quantity",
                        modifier = Modifier.size(18.dp),
                    )
                }

                Text(
                    text = "${item.quantity.formatQuantity()} ${item.ingredient.defaultUnit}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(80.dp),
                    textAlign = TextAlign.Center,
                )

                IconButton(
                    onClick = {
                        onQuantityChange(item.quantity + 0.5)
                    },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        modifier = Modifier.size(18.dp),
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove item",
                        modifier = Modifier.size(18.dp),
                        tint = ColourScheme.error,
                    )
                }
            }
        }
    }
}