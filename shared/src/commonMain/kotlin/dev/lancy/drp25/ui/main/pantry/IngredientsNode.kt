package dev.lancy.drp25.ui.main.pantry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
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
import dev.lancy.drp25.data.IngredientItem
import dev.lancy.drp25.data.IngredientLocation
import dev.lancy.drp25.data.formatQuantity
import dev.lancy.drp25.utilities.IngredientIcon
import dev.lancy.drp25.utilities.IngredientIconView


@Composable
fun IngredientsNode(
    selectedTabIndex: Int,
    allIngredients: SnapshotStateList<dev.lancy.drp25.data.IngredientItem>,
    onQuantityChange: (IngredientItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredIngredients = remember(selectedTabIndex, allIngredients) {
        when (selectedTabIndex) {
            0 -> allIngredients.filter { it.location == IngredientLocation.Fridge }
            1 -> allIngredients.filter { it.location == IngredientLocation.Freezer }
            2 -> allIngredients
            else -> allIngredients
        }.sortedBy { it.name }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = 8.dp + 56.dp + 16.dp + 64.dp
        )
    ) {
        itemsIndexed(filteredIngredients) { index, ingredient ->
            IngredientRow(
                ingredient = ingredient,
                onQuantityChange = { newQuantity ->
                    val updatedIngredient = ingredient.copy(quantity = newQuantity)
                    onQuantityChange(updatedIngredient)
                }
            )

            if (index < filteredIngredients.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
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
    var quantityText by remember(ingredient.quantity) {
        mutableStateOf(
            if (ingredient.quantity == 0.0) "0"
            else ingredient.quantity.formatQuantity()
        )
    }

    var isEditing by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .padding(start = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(120.dp)
        ) {
            IngredientIconView(
                icon = ingredient.icon,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(210.dp)
        ) {
            IconButton(
                onClick = {
                    val newQuantity = maxOf(0.0, ingredient.quantity - ingredient.incrementAmount)
                    onQuantityChange(newQuantity)
                    quantityText = if (newQuantity == 0.0) "0"
                    else newQuantity.formatQuantity()
                },
                enabled = ingredient.quantity > 0,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease quantity",
                    tint = if (ingredient.quantity > 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(70.dp)
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { newText ->
                            if (newText.isEmpty() || newText.matches(Regex("^\\d*\\.?\\d*$"))) {
                                quantityText = newText
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                        modifier = Modifier
                            .width(70.dp)
                            .height(56.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val newQuantity = quantityText.toDoubleOrNull() ?: 0.0
                                val finalQuantity = maxOf(0.0, newQuantity)
                                onQuantityChange(finalQuantity)
                                quantityText = if (finalQuantity == 0.0) "0"
                                else finalQuantity.formatQuantity()
                                isEditing = false
                            }
                        )
                    )

                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isEditing = true
                            }
                    ) {
                        Text(
                            text = quantityText,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 1.dp
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    val newQuantity = ingredient.quantity + ingredient.incrementAmount
                    onQuantityChange(newQuantity)
                    quantityText = if (newQuantity % 1.0 == 0.0) newQuantity.toInt().toString()
                    else newQuantity.formatQuantity()
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase quantity",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = ingredient.defaultUnit,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(44.dp)
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
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }
    }
}