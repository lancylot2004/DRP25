// File: IngredientsNode.kt
package dev.lancy.drp25.ui.main.pantry

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Cookie
import dev.lancy.drp25.data.IngredientItem
import dev.lancy.drp25.data.IngredientLocation
import dev.lancy.drp25.data.IngredientType
import dev.lancy.drp25.data.formatQuantity
import dev.lancy.drp25.utilities.IngredientIconView
import dev.lancy.drp25.utilities.rememberPantryIngredientsManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import androidx.compose.material3.OutlinedTextFieldDefaults
import dev.lancy.drp25.utilities.updateIngredient
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.foundation.shape.CircleShape
import com.composables.icons.lucide.ScanLine
import dev.lancy.drp25.utilities.fetchProductAndUpdatePantry
import dev.lancy.drp25.utilities.httpClient
// Updated portion of IngredientsNode.kt - replace the scanner section in your existing file

@Composable
fun IngredientsNode(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier
) {
    val ingredientsManager = rememberPantryIngredientsManager()
    val persistentIngredients by ingredientsManager.state.collectAsState()

    // State for showing QR scanner
    var showScanner by remember { mutableStateOf(false) }

    val saveIngredient = remember {
        { ingredient: IngredientItem ->
            ingredientsManager.updateIngredient(ingredient)
        }
    }

    val filteredIngredients = remember(selectedTabIndex, persistentIngredients) {
        persistentIngredients.filter {
            when (selectedTabIndex) {
                0 -> it.location == IngredientLocation.Fridge
                1 -> it.location == IngredientLocation.Freezer
                else -> true
            }
        }
    }

    val groupedByType = remember(filteredIngredients) {
        filteredIngredients
            .groupBy { it.type }
            .entries
            .sortedBy { it.key.order }
    }

    val headerPositions = remember(groupedByType) {
        mutableListOf<Pair<IngredientType, Int>>().apply {
            var index = 0
            groupedByType.forEach { (type, items) ->
                add(type to index)
                index += 1 + items.size
            }
        }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Cookie,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Pantry",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Scan Items Button (updated for multi-scan)
                OutlinedButton(
                    onClick = { showScanner = true },
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = Lucide.ScanLine,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scan Items",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                headerPositions.forEach { (type, position) ->
                    ElevatedButton(
                        onClick = {
                            coroutineScope.launch { listState.animateScrollToItem(position) }
                        },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = type.displayName,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 144.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                groupedByType.forEach { (type, ingredients) ->
                    if (ingredients.isNotEmpty()) {
                        item(key = type.displayName) {
                            Surface(
                                tonalElevation = 2.dp,
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = type.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 20.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(
                                        vertical = 12.dp,
                                        horizontal = 16.dp
                                    )
                                )
                            }
                        }
                        items(items = ingredients, key = { it.name }) { ingredient ->
                            IngredientRow(
                                ingredient = ingredient,
                                onSave = saveIngredient
                            )
                            if (ingredient != ingredients.last()) HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }

        // Multi-Scan QR Scanner Overlay
        if (showScanner) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            ) {
                QRScannerView(
                    ingredientsManager = ingredientsManager,
                    onClose = { showScanner = false }
                )
            }
        }
    }
}

@Composable
fun IngredientRow(
    ingredient: IngredientItem,
    onSave: (IngredientItem) -> Unit,
    modifier: Modifier = Modifier
) {
    // Local state for this ingredient's UI display - initialized once and then independent
    var currentQuantity by remember { mutableStateOf(ingredient.quantity) }
    var quantityText by remember { mutableStateOf(ingredient.quantity.formatQuantity()) }

    var isTextFieldFocused by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val saveDebounceScope = rememberCoroutineScope()
    var saveJob: Job? by remember { mutableStateOf(null) }

    fun debounceAndSave(newQuantity: Double) {
        saveJob?.cancel()
        saveJob = saveDebounceScope.launch {
            delay(150) // Debounce delay for saving to persistence
            val updatedIngredient = ingredient.copy(quantity = newQuantity)
            onSave(updatedIngredient)
        }
    }

    fun updateQuantity(newQuantity: Double) {
        currentQuantity = newQuantity
        quantityText = newQuantity.formatQuantity()
        debounceAndSave(newQuantity)
    }

    fun commitTextChange() {
        saveJob?.cancel()
        val newQty = quantityText.toDoubleOrNull()?.coerceAtLeast(0.0) ?: currentQuantity
        currentQuantity = newQty
        quantityText = newQty.formatQuantity()
        val updatedIngredient = ingredient.copy(quantity = currentQuantity)
        onSave(updatedIngredient)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(start = 36.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(120.dp)
        ) {
            IngredientIconView(icon = ingredient.icon, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(240.dp)
        ) {
            IconButton(
                onClick = {
                    // Direct update on click for instant UI feedback
                    val newQty = (currentQuantity - ingredient.incrementAmount).coerceAtLeast(0.0)
                    updateQuantity(newQty)
                },
                enabled = currentQuantity > 0,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(imageVector = Icons.Filled.Remove, contentDescription = null)
            }

            Box(modifier = Modifier.width(120.dp)) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { text ->
                        // Only update text while typing, don't update currentQuantity until valid
                        if (text.matches(Regex("^\\d*\\.?\\d*$"))) {
                            quantityText = text
                            val parsedValue = text.toDoubleOrNull()
                            if (parsedValue != null) {
                                currentQuantity = parsedValue
                                debounceAndSave(parsedValue)
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            commitTextChange()
                            focusManager.clearFocus()
                        }
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .onFocusChanged { focusState ->
                            isTextFieldFocused = focusState.isFocused
                            if (!focusState.isFocused) {
                                commitTextChange()
                            }
                        }
                )
            }

            IconButton(
                onClick = {
                    // Direct update on click for instant UI feedback
                    val newQty = currentQuantity + ingredient.incrementAmount
                    updateQuantity(newQty)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }

            Text(
                text = ingredient.defaultUnit,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.width(36.dp)
            )
        }
    }
}

@Composable
fun IngredientsToolbar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabTitles: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.height(56.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { }
        ) {
            tabTitles.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Tab(
                    selected = isSelected,
                    selectedContentColor = Color.Transparent,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }
    }
}