package dev.lancy.drp25.ui.main.pantry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class IngredientItem(
    val icon: IngredientIcon,
    val name: String,
    val defaultUnit: String,
    val incrementAmount: Double = 1.0,
    var quantity: Double = 0.0
)

// Default ingredient data with reasonable units and increments - ALL ingredients alphabetically
private fun getDefaultIngredients(): List<IngredientItem> = listOf(
    IngredientItem(IngredientIcon.ALCOHOL, "Alcohol", "ml", 50.0),
    IngredientItem(IngredientIcon.ALMOND, "Almond", "g", 25.0),
    IngredientItem(IngredientIcon.ALMOND_MILK, "Almond Milk", "ml", 250.0),
    IngredientItem(IngredientIcon.APPLE, "Apple", "pcs", 1.0),
    IngredientItem(IngredientIcon.APPLE_CIDER, "Apple Cider", "ml", 250.0),
    IngredientItem(IngredientIcon.AVOCADO, "Avocado", "pcs", 1.0),
    IngredientItem(IngredientIcon.BACON, "Bacon", "g", 1.0),
    IngredientItem(IngredientIcon.BAKING_SODA, "Baking Soda", "g", 0.5),
    IngredientItem(IngredientIcon.BANANA, "Banana", "pcs", 1.0),
    IngredientItem(IngredientIcon.BEEF_MEAT, "Beef", "kg", 0.25),
    IngredientItem(IngredientIcon.BELL_PEPPER, "Bell Pepper", "pcs", 1.0),
    IngredientItem(IngredientIcon.BISCUIT, "Biscuit", "pcs", 1.0),
    IngredientItem(IngredientIcon.BLUEBERRIES, "Blueberries", "g", 50.0),
    IngredientItem(IngredientIcon.BREAD, "Bread", "g", 1.0),
    IngredientItem(IngredientIcon.BROCCOLI, "Broccoli", "g", 100.0),
    IngredientItem(IngredientIcon.BUNS, "Buns", "pcs", 1.0),
    IngredientItem(IngredientIcon.BUTTER, "Butter", "g", 25.0),
    IngredientItem(IngredientIcon.CABBAGE, "Cabbage", "g", 100.0),
    IngredientItem(IngredientIcon.CANNED_TUNA, "Canned Tuna", "pcs", 1.0),
    IngredientItem(IngredientIcon.CARROT, "Carrot", "pcs", 1.0),
    IngredientItem(IngredientIcon.CAULIFLOWER, "Cauliflower", "g", 100.0),
    IngredientItem(IngredientIcon.CEREALS, "Cereals", "g", 50.0),
    IngredientItem(IngredientIcon.CHEDDAR_CHEESE, "Cheddar Cheese", "g", 50.0),
    IngredientItem(IngredientIcon.CHEESE_SLICES, "Cheese Slices", "g", 1.0),
    IngredientItem(IngredientIcon.CHERRIES, "Cherries", "g", 50.0),
    IngredientItem(IngredientIcon.CHERRY_TOMATO, "Cherry Tomato", "g", 50.0),
    IngredientItem(IngredientIcon.CHICKEN_MEAT, "Chicken", "kg", 0.25),
    IngredientItem(IngredientIcon.CHILI, "Chili", "pcs", 1.0),
    IngredientItem(IngredientIcon.CHILI_SAUCE, "Chili Sauce", "ml", 25.0),
    IngredientItem(IngredientIcon.CHOCOLATE_BAR, "Chocolate Bar", "g", 1.0),
    IngredientItem(IngredientIcon.CINNAMON, "Cinnamon", "tsp", 0.25),
    IngredientItem(IngredientIcon.COCONUT, "Coconut", "pcs", 1.0),
    IngredientItem(IngredientIcon.COFFEE_BEANS, "Coffee Beans", "g", 25.0),
    IngredientItem(IngredientIcon.COGNAC, "Cognac", "ml", 50.0),
    IngredientItem(IngredientIcon.COOKIE, "Cookie", "pcs", 1.0),
    IngredientItem(IngredientIcon.CORN, "Corn", "pcs", 1.0),
    IngredientItem(IngredientIcon.CUCUMBER, "Cucumber", "pcs", 1.0),
    IngredientItem(IngredientIcon.DOUBLE_CREAM, "Double Cream", "ml", 100.0),
    IngredientItem(IngredientIcon.DUCK_MEAT, "Duck", "kg", 0.25),
    IngredientItem(IngredientIcon.DUMPLING, "Dumpling", "pcs", 1.0),
    IngredientItem(IngredientIcon.EGG, "Eggs", "pcs", 1.0),
    IngredientItem(IngredientIcon.EGGPLANT, "Eggplant", "pcs", 1.0),
    IngredientItem(IngredientIcon.FALAFEL, "Falafel", "pcs", 1.0),
    IngredientItem(IngredientIcon.FISH, "Fish", "kg", 0.25),
    IngredientItem(IngredientIcon.FUSILI, "Fusili", "g", 100.0),
    IngredientItem(IngredientIcon.GARLIC, "Garlic", "pcs", 1.0),
    IngredientItem(IngredientIcon.GINGER, "Ginger", "g", 10.0),
    IngredientItem(IngredientIcon.GRAPES, "Grapes", "g", 100.0),
    IngredientItem(IngredientIcon.GREEN_SALAD, "Green Salad", "g", 50.0),
    IngredientItem(IngredientIcon.HAM, "Ham", "g", 1.0),
    IngredientItem(IngredientIcon.HAZELNUT, "Hazelnut", "g", 25.0),
    IngredientItem(IngredientIcon.HONEY, "Honey", "ml", 1.0),
    IngredientItem(IngredientIcon.ICE_CREAM, "Ice Cream", "ml", 1.0),
    IngredientItem(IngredientIcon.JAM, "Jam", "ml", 1.0),
    IngredientItem(IngredientIcon.KETCHUP, "Ketchup", "ml", 1.0),
    IngredientItem(IngredientIcon.KIDNEY_BEANS, "Kidney Beans", "g", 100.0),
    IngredientItem(IngredientIcon.KIWI, "Kiwi", "pcs", 1.0),
    IngredientItem(IngredientIcon.LAMB, "Lamb", "kg", 0.25),
    IngredientItem(IngredientIcon.LEAFY_GREEN, "Leafy Green", "g", 50.0),
    IngredientItem(IngredientIcon.LEMON, "Lemon", "pcs", 1.0),
    IngredientItem(IngredientIcon.LEMON_JUICE, "Lemon Juice", "ml", 25.0),
    IngredientItem(IngredientIcon.LIME, "Lime", "pcs", 1.0),
    IngredientItem(IngredientIcon.LOBSTER, "Lobster", "pcs", 1.0),
    IngredientItem(IngredientIcon.MACARONI, "Macaroni", "g", 100.0),
    IngredientItem(IngredientIcon.MANGO, "Mango", "pcs", 1.0),
    IngredientItem(IngredientIcon.MAYONNAISE, "Mayonnaise", "ml", 1.0),
    IngredientItem(IngredientIcon.MEATBALLS, "Meatballs", "pcs", 1.0),
    IngredientItem(IngredientIcon.MEAT_BURGER, "Meat Burger", "pcs", 1.0),
    IngredientItem(IngredientIcon.MELON, "Melon", "pcs", 1.0),
    IngredientItem(IngredientIcon.MILK, "Milk", "ml", 250.0),
    IngredientItem(IngredientIcon.MUSTARD, "Mustard", "ml", 1.0),
    IngredientItem(IngredientIcon.NOODLE, "Noodle", "g", 100.0),
    IngredientItem(IngredientIcon.OAT, "Oat", "g", 50.0),
    IngredientItem(IngredientIcon.OIL, "Oil", "ml", 25.0),
    IngredientItem(IngredientIcon.OLIVES, "Olives", "pcs", 5.0),
    IngredientItem(IngredientIcon.ONION, "Onion", "pcs", 1.0),
    IngredientItem(IngredientIcon.OYSTER, "Oyster", "pcs", 1.0),
    IngredientItem(IngredientIcon.PANCAKES, "Pancakes", "pcs", 1.0),
    IngredientItem(IngredientIcon.PARMESAN_CHEESE, "Parmesan Cheese", "g", 25.0),
    IngredientItem(IngredientIcon.PARSLEY, "Parsley", "g", 10.0),
    IngredientItem(IngredientIcon.PEACH, "Peach", "pcs", 1.0),
    IngredientItem(IngredientIcon.PEANUTS, "Peanuts", "g", 25.0),
    IngredientItem(IngredientIcon.PEANUT_BUTTER, "Peanut Butter", "g", 1.0),
    IngredientItem(IngredientIcon.PEAR, "Pear", "pcs", 1.0),
    IngredientItem(IngredientIcon.PEAS, "Peas", "g", 50.0),
    IngredientItem(IngredientIcon.PEPPER_BLACK, "Black Pepper", "tsp", 0.25),
    IngredientItem(IngredientIcon.PINEAPPLE, "Pineapple", "pcs", 1.0),
    IngredientItem(IngredientIcon.POPCORN, "Popcorn", "g", 50.0),
    IngredientItem(IngredientIcon.PORK_MEAT, "Pork", "kg", 0.25),
    IngredientItem(IngredientIcon.POTATO, "Potato", "pcs", 1.0),
    IngredientItem(IngredientIcon.POULTRY_LEG, "Poultry Leg", "pcs", 1.0),
    IngredientItem(IngredientIcon.PRETZEL, "Pretzel", "pcs", 1.0),
    IngredientItem(IngredientIcon.PUDDING, "Pudding", "ml", 1.0),
    IngredientItem(IngredientIcon.PUMPKIN, "Pumpkin", "g", 100.0),
    IngredientItem(IngredientIcon.RADISH, "Radish", "pcs", 1.0),
    IngredientItem(IngredientIcon.RAMEN, "Ramen", "g", 1.0),
    IngredientItem(IngredientIcon.RASPBERRY, "Raspberry", "g", 50.0),
    IngredientItem(IngredientIcon.RICE, "Rice", "g", 100.0),
    IngredientItem(IngredientIcon.SALAMI, "Salami", "g", 1.0),
    IngredientItem(IngredientIcon.SALMON, "Salmon", "kg", 0.25),
    IngredientItem(IngredientIcon.SALT, "Salt", "tsp", 0.25),
    IngredientItem(IngredientIcon.SAUSAGE, "Sausage", "pcs", 1.0),
    IngredientItem(IngredientIcon.SEASONING, "Seasoning", "tsp", 0.25),
    IngredientItem(IngredientIcon.SOYA, "Soya", "ml", 25.0),
    IngredientItem(IngredientIcon.SPAGHETTI, "Spaghetti", "g", 100.0),
    IngredientItem(IngredientIcon.SPINACH, "Spinach", "g", 50.0),
    IngredientItem(IngredientIcon.STARCH, "Starch", "g", 1.0),
    IngredientItem(IngredientIcon.STEAK, "Steak", "pcs", 1.0),
    IngredientItem(IngredientIcon.STRAWBERRY, "Strawberry", "g", 50.0),
    IngredientItem(IngredientIcon.SUGAR_BROWN, "Brown Sugar", "kg", 1.0),
    IngredientItem(IngredientIcon.SUGAR_WHITE, "White Sugar", "kg", 1.0),
    IngredientItem(IngredientIcon.SWEET_POTATO, "Sweet Potato", "pcs", 1.0),
    IngredientItem(IngredientIcon.TACO, "Taco", "pcs", 1.0),
    IngredientItem(IngredientIcon.TANGERINE, "Tangerine", "pcs", 1.0),
    IngredientItem(IngredientIcon.TOMATO, "Tomato", "pcs", 1.0),
    IngredientItem(IngredientIcon.TORTILLA, "Tortilla", "pcs", 1.0),
    IngredientItem(IngredientIcon.TURKEY_MEAT, "Turkey", "kg", 0.25),
    IngredientItem(IngredientIcon.VANILLA, "Vanilla", "tsp", 0.25),
    IngredientItem(IngredientIcon.VINEGAR, "Vinegar", "ml", 1.0),
    IngredientItem(IngredientIcon.WATERMELON, "Watermelon", "pcs", 1.0),
    IngredientItem(IngredientIcon.WHEAT_FLOUR, "Wheat Flour", "g", 100.0),
    IngredientItem(IngredientIcon.WINE, "Wine", "ml", 100.0),
    IngredientItem(IngredientIcon.YEAST, "Yeast", "tsp", 0.25),
    IngredientItem(IngredientIcon.YOGURT, "Yogurt", "g", 100.0)
)

@Composable
fun IngredientsNode(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier
) {
    var ingredients by remember { mutableStateOf(getDefaultIngredients()) }

    // Show all ingredients since we only have one tab
    val filteredIngredients = ingredients

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filteredIngredients) { ingredient ->
            IngredientCard(
                ingredient = ingredient,
                onQuantityChange = { newQuantity ->
                    ingredients = ingredients.map {
                        if (it.icon == ingredient.icon) it.copy(quantity = newQuantity) else it
                    }
                }
            )
        }
    }
}

@Composable
private fun IngredientCard(
    ingredient: IngredientItem,
    onQuantityChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Icon and name below it - fixed width for alignment
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(120.dp)
            ) {
                IngredientIconView(
                    icon = ingredient.icon,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Right side: Quantity controls with precise alignment
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(200.dp)
            ) {
                // Quantity display with underline and controls
                var quantityText by remember(ingredient.quantity) {
                    mutableStateOf(
                        if (ingredient.quantity == 0.0) "0"
                        else if (ingredient.quantity % 1.0 == 0.0) ingredient.quantity.toInt().toString()
                        else ingredient.quantity.toString()
                    )
                }

                var isEditing by remember { mutableStateOf(false) }

                // Decrease button
                IconButton(
                    onClick = {
                        val newQuantity = maxOf(0.0, ingredient.quantity - ingredient.incrementAmount)
                        onQuantityChange(newQuantity)
                        quantityText = if (newQuantity == 0.0) "0"
                        else if (newQuantity % 1.0 == 0.0) newQuantity.toInt().toString()
                        else newQuantity.toString()
                    },
                    enabled = ingredient.quantity > 0,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease quantity",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Quantity with underline - editable
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(60.dp)
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { newText ->
                                // Only allow numbers and decimal points
                                if (newText.isEmpty() || newText.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    quantityText = newText
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                            modifier = Modifier
                                .width(60.dp)
                                .height(56.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                                onDone = {
                                    val newQuantity = quantityText.toDoubleOrNull() ?: 0.0
                                    val finalQuantity = maxOf(0.0, newQuantity)
                                    onQuantityChange(finalQuantity)
                                    quantityText = if (finalQuantity == 0.0) "0"
                                    else if (finalQuantity % 1.0 == 0.0) finalQuantity.toInt().toString()
                                    else finalQuantity.toString()
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
                                textAlign = TextAlign.Center
                            )

                            // Underline
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outline,
                                thickness = 2.dp
                            )
                        }
                    }
                }

                // Increase button
                IconButton(
                    onClick = {
                        val newQuantity = ingredient.quantity + ingredient.incrementAmount
                        onQuantityChange(newQuantity)
                        quantityText = if (newQuantity % 1.0 == 0.0) newQuantity.toInt().toString()
                        else newQuantity.toString()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Unit - now guaranteed to stay on same line
                Text(
                    text = ingredient.defaultUnit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
    }
}

// Simplified toolbar with single "Ingredients" tab
@Composable
fun IngredientsToolbar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.height(56.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Complete ingredients screen
@Composable
fun IngredientsScreen(
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        IngredientsToolbar(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        IngredientsNode(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxSize()
        )
    }
}