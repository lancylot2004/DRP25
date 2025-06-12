// File: IngredientsNode.kt
package dev.lancy.drp25.ui.main.pantry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Cookie
import com.composables.icons.lucide.Utensils
import dev.lancy.drp25.data.IngredientItem
import dev.lancy.drp25.data.IngredientLocation
import dev.lancy.drp25.data.IngredientType
import dev.lancy.drp25.data.formatQuantity
import dev.lancy.drp25.utilities.IngredientIconView
import kotlinx.coroutines.launch

@Composable
fun IngredientsNode(
    selectedTabIndex: Int,
    allIngredients: SnapshotStateList<IngredientItem>,
    onQuantityChange: (IngredientItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredIngredients = when (selectedTabIndex) {
        0 -> allIngredients.filter { it.location == IngredientLocation.Fridge }
        1 -> allIngredients.filter { it.location == IngredientLocation.Freezer }
        else -> allIngredients.toList()
    }

    val groupedByType = filteredIngredients
        .groupBy { it.type }
        .entries
        .sortedBy { it.key.order }

    val headerPositions = remember(filteredIngredients) {
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

    LaunchedEffect(selectedTabIndex) {
        listState.animateScrollToItem(0)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Start,
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
        }

        // Category Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            headerPositions.forEach { (type, position) ->
                TextButton(onClick = {
                    coroutineScope.launch { listState.animateScrollToItem(position) }
                }) {
                    Text(text = type.displayName)
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 164.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            groupedByType.forEach { (type, ingredients) ->
                if (ingredients.isNotEmpty()) {
                    item {
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
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                            )
                        }
                    }
                    itemsIndexed(ingredients) { index, ingredient ->
                        IngredientRow(
                            ingredient = ingredient,
                            onQuantityChange = { newQty ->
                                onQuantityChange(ingredient.copy(quantity = newQty))
                            }
                        )
                        if (index < ingredients.lastIndex) HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IngredientRow(
    ingredient: IngredientItem,
    onQuantityChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var quantityText by remember { mutableStateOf(ingredient.quantity.formatQuantity()) }
    var isEditing by remember { mutableStateOf(false) }

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
            modifier = Modifier.width(180.dp)
        ) {
            IconButton(
                onClick = {
                    val newQty = (ingredient.quantity - ingredient.incrementAmount).coerceAtLeast(0.0)
                    onQuantityChange(newQty)
                    quantityText = newQty.formatQuantity()
                }, enabled = ingredient.quantity > 0, modifier = Modifier.size(40.dp)
            ) { Icon(imageVector = Icons.Filled.Remove, contentDescription = null) }
            Box(modifier = Modifier.width(60.dp)) {
                if (isEditing) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { text -> if (text.matches(Regex("^\\d*\\.?\\d*$"))) quantityText = text },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        keyboardActions = KeyboardActions(onDone = {
                            val newQty = quantityText.toDoubleOrNull() ?: 0.0
                            onQuantityChange(newQty)
                            quantityText = newQty.formatQuantity()
                            isEditing = false
                        }),
                        //colors = TextFieldDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = quantityText,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize().clickable { isEditing = true }
                    )
                }
            }
            IconButton(
                onClick = {
                    val newQty = ingredient.quantity + ingredient.incrementAmount
                    onQuantityChange(newQty)
                    quantityText = newQty.formatQuantity()
                }, modifier = Modifier.size(40.dp)
            ) { Icon(imageVector = Icons.Filled.Add, contentDescription = null) }
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
