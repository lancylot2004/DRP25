package dev.lancy.drp25.ui.main.log

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import dev.lancy.drp25.data.*
import dev.lancy.drp25.utilities.Typography
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun RecipeForm(
    onSaveRecipe: (Recipe) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var portions by remember { mutableStateOf("1") }
    var cookingTime by remember { mutableStateOf("") }
    var cleanupTime by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var selectedDiet by remember { mutableStateOf<Diet?>(null) }
    var selectedCuisine by remember { mutableStateOf<Cuisine?>(null) }
    var selectedMealType by remember { mutableStateOf<MealType?>(null) }
    var selectedIngredients by remember { mutableStateOf<List<Ingredient>>(emptyList()) }
    var selectedUtensils by remember { mutableStateOf<List<Utensil>>(emptyList()) }
    var steps by remember { mutableStateOf<List<Step>>(listOf(Step())) }

    // Nutritional Values (Macros)
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }

    // Dropdown states
    var dietExpanded by remember { mutableStateOf(false) }
    var cuisineExpanded by remember { mutableStateOf(false) }
    var mealTypeExpanded by remember { mutableStateOf(false) }
    var ingredientsExpanded by remember { mutableStateOf(false) }
    var utensilsExpanded by remember { mutableStateOf(false) }

    // Ingredient form
    var selectedIngredientItem by remember { mutableStateOf<IngredientItem?>(null) }
    var ingredientSearchQuery by remember { mutableStateOf("") }
    var ingredientQuantity by remember { mutableStateOf("") }
    var ingredientUnit by remember { mutableStateOf<IngredientUnit>(IngredientUnit.CountUnit.Piece) }

    // Utensils form
    var utensilSearchQuery by remember { mutableStateOf("") }

    // Max dropdown height
    val maxDropDownHeight = 200.dp

    // Loading state for save operation
    var isSaving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }

    val sortedCuisines = remember { Cuisine.entries.sortedBy { it.displayName } }
    val defaultIngredients = remember { getDefaultIngredients() }

    // Filter ingredients based on search query
    val filteredIngredients = remember(ingredientSearchQuery) {
        if (ingredientSearchQuery.isBlank()) {
            defaultIngredients.sortedBy { it.name }
        } else {
            defaultIngredients.sortedBy {it.name }.filter {
                it.name.contains(ingredientSearchQuery, ignoreCase = true)
            }
        }
    }

    // Keyboard controller and focus manager for dismissing keyboard
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Helper function to get default unit for ingredient
    fun getDefaultUnitForIngredient(ingredientName: String): IngredientUnit {
        val defaultItem =
            defaultIngredients.find { it.name.equals(ingredientName, ignoreCase = true) }
        return when (defaultItem?.defaultUnit?.lowercase()) {
            "g" -> IngredientUnit.WeightUnit.Gram
            "kg" -> IngredientUnit.WeightUnit.Kilogram
            "ml" -> IngredientUnit.VolumeUnit.Milliliter
            "l" -> IngredientUnit.VolumeUnit.Liter
            "tsp" -> IngredientUnit.VolumeUnit.Teaspoon
            "tbsp" -> IngredientUnit.VolumeUnit.Tablespoon
            "cup" -> IngredientUnit.VolumeUnit.Cup
            "pcs" -> IngredientUnit.CountUnit.Piece
            "slice" -> IngredientUnit.CountUnit.Slice
            "pinch" -> IngredientUnit.CountUnit.Pinch
            "dash" -> IngredientUnit.CountUnit.Dash
            else -> IngredientUnit.CountUnit.Piece
        }
    }

    val isFormValid = name.isNotBlank() &&
            description.isNotBlank() &&
            portions.isNotBlank() &&
            cookingTime.isNotBlank() &&
            selectedIngredients.isNotEmpty() &&
            selectedUtensils.isNotEmpty() &&
            steps.any { it.description.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxHeight(0.9f) // Set maximum height to 90% of screen
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Create New Recipe",
            style = Typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        )

        // Show error if save failed
        saveError?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB00020))
            ) {
                Text(text = error, color = Color.White, modifier = Modifier.padding(16.dp))
            }
        }

        // Name (Required)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Recipe Name *", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF6200EE),
                unfocusedBorderColor = Color.Gray
            )
        )

        // Description (Required)
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description *", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            minLines = 3,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF6200EE),
                unfocusedBorderColor = Color.Gray
            )
        )

        // Cooking Time (Required)
        OutlinedTextField(
            value = cookingTime,
            onValueChange = { cookingTime = it },
            label = { Text("Cooking Time (min) *", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF6200EE),
                unfocusedBorderColor = Color.Gray
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Portions (Required)
            OutlinedTextField(
                value = portions,
                onValueChange = { portions = it },
                label = { Text("Portions *", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Right) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            // Cleanup Time (Optional)
            OutlinedTextField(
                value = cleanupTime,
                onValueChange = { cleanupTime = it },
                label = { Text("Cleanup Time (min)", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 17.sp)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )
        }

        // Nutritional Value Section (Optional)
        Text(
            text = "Nutritional Value (Optional)",
            style = Typography.titleSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
        )

        // Calories
        OutlinedTextField(
            value = calories,
            onValueChange = { calories = it },
            label = { Text("Calories", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF6200EE),
                unfocusedBorderColor = Color.Gray
            )
        )

        // Macros Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = protein,
                onValueChange = { protein = it },
                label = { Text("Protein (g)", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Right) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                ),
            )

            OutlinedTextField(
                value = fat,
                onValueChange = { fat = it },
                label = { Text("Fat (g)", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Right) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            OutlinedTextField(
                value = carbs,
                onValueChange = { carbs = it },
                label = { Text("Carbs (g)", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )
        }

        // Ingredients Section (Required)
        Text(
            text = "Ingredients *",
            style = Typography.titleSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Add Ingredient Form
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Ingredient Selection with Search
                ExposedDropdownMenuBox(
                    expanded = ingredientsExpanded,
                    onExpandedChange = { ingredientsExpanded = !ingredientsExpanded },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    OutlinedTextField(
                        value = if (selectedIngredientItem != null) selectedIngredientItem!!.name else ingredientSearchQuery,
                        onValueChange = { query ->
                            ingredientSearchQuery = query
                            selectedIngredientItem = null
                            ingredientsExpanded = true
                        },
                        label = { Text("Search or Select Ingredient", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ingredientsExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6200EE),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = ingredientsExpanded,
                        onDismissRequest = { ingredientsExpanded = false },
                        modifier = Modifier
                            .background(Color(0xFF2E2E2E))
                            .heightIn(max = maxDropDownHeight)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (filteredIngredients.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No ingredients found", color = Color.Gray, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {},
                                enabled = false
                            )
                        } else {
                            filteredIngredients.forEach { ingredientItem ->
                                DropdownMenuItem(
                                    text = { Text(ingredientItem.name, color = Color.White, style = MaterialTheme.typography.bodyMedium) },
                                    onClick = {
                                        selectedIngredientItem = ingredientItem
                                        ingredientSearchQuery = ""
                                        ingredientUnit =
                                            getDefaultUnitForIngredient(ingredientItem.name)
                                        ingredientsExpanded = false
                                        keyboardController?.hide()
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = ingredientQuantity,
                        onValueChange = { ingredientQuantity = it },
                        label = { Text("Quantity", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6200EE),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    // Unit Display (Read-only when ingredient is selected)
                    OutlinedTextField(
                        value = ingredientUnit.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6200EE),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                }

                Button(
                    onClick = {
                        if (selectedIngredientItem != null && ingredientQuantity.isNotBlank()) {
                            val ingredient = Ingredient(
                                name = selectedIngredientItem!!.name,
                                quantity = ingredientQuantity.toDoubleOrNull() ?: 0.0,
                                unit = ingredientUnit
                            )
                            selectedIngredients = selectedIngredients + ingredient
                            selectedIngredientItem = null
                            ingredientQuantity = ""
                            ingredientSearchQuery = ""
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    enabled = selectedIngredientItem != null && ingredientQuantity.isNotBlank()
                ) {
                    Icon(
                        imageVector = Lucide.Plus,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Ingredient")
                }
            }
        }

        // Selected Ingredients List
        selectedIngredients.forEach { ingredient ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3E3E3E))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatIngredientDisplay(ingredient),
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            selectedIngredients = selectedIngredients - ingredient
                        }
                    ) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Utensils Section (Required)
        Text(
            text = "Utensils *",
            style = Typography.titleSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = utensilsExpanded,
            onExpandedChange = { utensilsExpanded = !utensilsExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            OutlinedTextField(
                value = utensilSearchQuery,
                onValueChange = { query ->
                    utensilSearchQuery = query
                    utensilsExpanded = true
                },
                label = { Text("Search or Select Utensils", color = Color.Gray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = utensilsExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            // Filter utensils based on the search query
            val filteredUtensils = remember(utensilSearchQuery) {
                Utensil.entries.filter {
                    it.displayName.contains(utensilSearchQuery, ignoreCase = true)
                }
            }

            ExposedDropdownMenu(
                expanded = utensilsExpanded,
                onDismissRequest = { utensilsExpanded = false },
                modifier = Modifier
                    .background(Color(0xFF2E2E2E))
                    .heightIn(max = maxDropDownHeight)
                    .verticalScroll(rememberScrollState())
            ) {
                if (filteredUtensils.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No utensils found", color = Color.Gray, style = MaterialTheme.typography.bodyMedium) },
                        onClick = {},
                        enabled = false
                    )
                } else {
                    filteredUtensils.forEach { utensil ->
                        val isSelected = selectedUtensils.contains(utensil)
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = null,
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF6200EE),
                                            uncheckedColor = Color.Gray
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(utensil.displayName, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                }
                            },
                            onClick = {
                                selectedUtensils = if (isSelected) {
                                    selectedUtensils - utensil
                                } else {
                                    selectedUtensils + utensil
                                }
                                utensilSearchQuery = ""
                                utensilsExpanded = false
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        )
                    }
                }
            }
        }

        // Selected Utensils Display
        if (selectedUtensils.isNotEmpty()) {
            Text(
                text = "Selected: ${selectedUtensils.joinToString(", ") { it.displayName }}",
                color = Color.Gray,
                style = Typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Steps Section (Required)
        Text(
            text = "Cooking Steps *",
            style = Typography.titleSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        steps.forEachIndexed { index, step ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Step ${index + 1}",
                            color = Color.White,
                            style = Typography.titleSmall
                        )
                        if (steps.size > 1) {
                            IconButton(
                                onClick = {
                                    steps = steps.toMutableList().apply { removeAt(index) }
                                }
                            ) {
                                Icon(
                                    imageVector = Lucide.X,
                                    contentDescription = "Remove step",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = step.description,
                        onValueChange = { newDescription ->
                            steps = steps.toMutableList().apply {
                                this[index] = Step(description = newDescription)
                            }
                        },
                        label = { Text("Step description", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = if (index == steps.lastIndex) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (index < steps.lastIndex) {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            },
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.DarkGray,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                }
            }
        }

        Button(
            onClick = {
                steps = steps + Step()
            },
            modifier = Modifier.padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Icon(
                imageVector = Lucide.Plus,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Step")
        }

        // Diet Dropdown (Optional)
        ExposedDropdownMenuBox(
            expanded = dietExpanded,
            onExpandedChange = { dietExpanded = !dietExpanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedDiet?.displayName ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Diet", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dietExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )
            ExposedDropdownMenu(
                expanded = dietExpanded,
                onDismissRequest = { dietExpanded = false },
                modifier = Modifier.background(Color(0xFF2E2E2E)).heightIn(max = maxDropDownHeight).verticalScroll(rememberScrollState())
            ) {
                Diet.entries.forEach { diet ->
                    DropdownMenuItem(
                        text = { Text(diet.displayName, color = Color.White, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                        onClick = {
                            selectedDiet = diet
                            dietExpanded = false
                        }
                    )
                }
            }
        }

        // Cuisine Dropdown (Optional) - Sorted Alphabetically
        ExposedDropdownMenuBox(
            expanded = cuisineExpanded,
            onExpandedChange = { cuisineExpanded = !cuisineExpanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedCuisine?.displayName ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Cuisine", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cuisineExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )
            ExposedDropdownMenu(
                expanded = cuisineExpanded,
                onDismissRequest = { cuisineExpanded = false },
                modifier = Modifier.background(Color(0xFF2E2E2E)).heightIn(max = maxDropDownHeight).verticalScroll(rememberScrollState())
            ) {
                sortedCuisines.forEach { cuisine ->
                    DropdownMenuItem(
                        text = { Text(cuisine.displayName, color = Color.White, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                        onClick = {
                            selectedCuisine = cuisine
                            cuisineExpanded = false
                        }
                    )
                }
            }
        }

        // Meal Type Dropdown (Optional)
        ExposedDropdownMenuBox(
            expanded = mealTypeExpanded,
            onExpandedChange = { mealTypeExpanded = !mealTypeExpanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            OutlinedTextField(
                value = selectedMealType?.displayName ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Meal Type", color = Color.Gray, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealTypeExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )
            ExposedDropdownMenu(
                expanded = mealTypeExpanded,
                onDismissRequest = { mealTypeExpanded = false },
                modifier = Modifier.background(Color(0xFF2E2E2E)).heightIn(max = maxDropDownHeight).verticalScroll(rememberScrollState())
            ) {
                MealType.entries.forEach { mealType ->
                    DropdownMenuItem(
                        text = { Text(mealType.displayName, color = Color.White, style = Typography.bodyLarge.copy(fontSize = 18.sp)) },
                        onClick = {
                            selectedMealType = mealType
                            mealTypeExpanded = false
                        }
                    )
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (isFormValid) {
                        val macros = mutableMapOf<String, Double>()
                        protein.toDoubleOrNull()?.let { macros["protein"] = it }
                        fat.toDoubleOrNull()?.let { macros["fat"] = it }
                        carbs.toDoubleOrNull()?.let { macros["carbs"] = it }

                        val recipe = Recipe(
                            id = Uuid.random().toString(),
                            name = name,
                            description = description,
                            portions = portions.toIntOrNull() ?: 1,
                            cookingTime = cookingTime.toIntOrNull() ?: 0,
                            cleanupTime = cleanupTime.toIntOrNull(),
                            calories = calories.toIntOrNull(),
                            macros = macros,
                            ingredients = selectedIngredients,
                            keyIngredients = selectedIngredients.take(3).map { it.name.lowercase() },
                            diet = selectedDiet,
                            cuisine = selectedCuisine,
                            mealType = selectedMealType,
                            utensils = selectedUtensils,
                            steps = steps.filter { it.description.isNotBlank() },
                            cardImage = "",
                            smallImage = ""
                        )
                        onSaveRecipe(recipe)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                ),
                enabled = isFormValid
            ) {
                Text("Save Recipe")
            }
        }
    }
}