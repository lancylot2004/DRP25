package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import dev.lancy.drp25.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.round

// Keys for Settings storage
private const val PANTRY_INGREDIENTS_KEY = "pantry_ingredients"
private const val PANTRY_UTENSILS_KEY = "pantry_utensils"

// Data class for persistent pantry ingredient
@Serializable
data class PersistentPantryIngredient(
    val name: String,
    val icon: String, // Store as string to serialize IngredientIcon
    val type: String, // Store as string to serialize IngredientType
    val location: String, // Store as string to serialize IngredientLocation
    val quantity: Double,
    val defaultUnit: String,
    val incrementAmount: Double,
    val expirationDate: String? = null, // ISO date string
    val addedDate: String = Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .toString(),
    val notes: String? = null,
)

// Data class for persistent utensil selection
@Serializable
data class PersistentUtensilSelection(
    val utensilName: String,
    val isAvailable: Boolean = true,
    val lastUsed: String = Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .toString(),
)

// Extension functions to convert between domain models and persistent models
fun IngredientItem.toPersistent(): PersistentPantryIngredient = PersistentPantryIngredient(
    name = name,
    icon = icon.name,
    type = type.name,
    location = location.name,
    quantity = quantity,
    defaultUnit = defaultUnit,
    incrementAmount = incrementAmount,
    expirationDate = null,
    notes = null,
)

fun PersistentPantryIngredient.toIngredientItem(): IngredientItem = IngredientItem(
    name = name,
    icon = IngredientIcon.valueOf(icon),
    type = IngredientType.valueOf(type),
    location = IngredientLocation.valueOf(location),
    quantity = quantity,
    defaultUnit = defaultUnit,
    incrementAmount = incrementAmount,
)

// Pantry Ingredients Manager (No changes needed)
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberPantryIngredientsManager(): PersistenceManager<List<IngredientItem>> = remember {
    object : PersistenceManager<List<IngredientItem>> {
        private val _state = MutableStateFlow(
            settings
                .decodeValueOrNull<List<PersistentPantryIngredient>>(PANTRY_INGREDIENTS_KEY)
                ?.map { it.toIngredientItem() }
                ?: getDefaultIngredients(),
        )
        override val state: StateFlow<List<IngredientItem>> = _state.asStateFlow()

        override fun update(transform: List<IngredientItem>.() -> List<IngredientItem>) {
            _state.update { currentList ->
                val updatedList = currentList.transform()
                if (updatedList != currentList) {
                    val persistentList = updatedList.map { it.toPersistent() }
                    settings.encodeValue(PANTRY_INGREDIENTS_KEY, persistentList)
                }
                updatedList
            }
        }
    }
}

// Utensils Manager (No changes needed)
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberPantryUtensilsManager(): PersistenceManager<List<PersistentUtensilSelection>> = remember {
    object : PersistenceManager<List<PersistentUtensilSelection>> {
        private val _state = MutableStateFlow(
            settings
                .decodeValueOrNull<List<PersistentUtensilSelection>>(PANTRY_UTENSILS_KEY)
                ?: emptyList(),
        )
        override val state: StateFlow<List<PersistentUtensilSelection>> = _state.asStateFlow()

        override fun update(transform: List<PersistentUtensilSelection>.() -> List<PersistentUtensilSelection>) {
            _state.update { currentList ->
                val updatedList = currentList.transform()
                if (updatedList != currentList) {
                    settings.encodeValue(PANTRY_UTENSILS_KEY, updatedList)
                }
                updatedList
            }
        }
    }
}

// Helper functions for common operations (Fixes for all of these)

// For IngredientItem manager:
fun PersistenceManager<List<IngredientItem>>.addIngredient(ingredient: IngredientItem) {
    update { this + ingredient } // 'this' refers to the List<IngredientItem>
}

fun PersistenceManager<List<IngredientItem>>.removeIngredient(ingredient: IngredientItem) {
    update { this.filter { it.name != ingredient.name } }
}

fun PersistenceManager<List<IngredientItem>>.updateIngredientQuantity(name: String, newQuantity: Double) {
    update {
        this.map { ingredient ->
            // 'this' refers to the List<IngredientItem>
            if (ingredient.name == name) {
                ingredient.copy(quantity = newQuantity)
            } else {
                ingredient
            }
        }
    }
}

fun PersistenceManager<List<IngredientItem>>.updateIngredient(updatedIngredient: IngredientItem) {
    update {
        this.map { ingredient ->
            // 'this' refers to the List<IngredientItem>
            if (ingredient.name == updatedIngredient.name) {
                updatedIngredient
            } else {
                ingredient
            }
        }
    }
}

// Extension to check if an ingredient is expired (if you add expiration dates)
fun PersistentPantryIngredient.isExpired(): Boolean {
    val expirationDate = this.expirationDate ?: return false
    return try {
        val expDate = LocalDate.parse(expirationDate)
        val today = Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        expDate < today
    } catch (e: Exception) {
        false
    }
}

// Extension to get days until expiration
fun PersistentPantryIngredient.daysUntilExpiration(): Int? {
    val expirationDate = this.expirationDate ?: return null
    return try {
        val expDate = LocalDate.parse(expirationDate)
        val today = Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        (expDate.toEpochDays() - today.toEpochDays()).toInt()
    } catch (e: Exception) {
        null
    }
}

// --- New Function to Subtract Recipe Ingredients from Pantry ---

/**
 * Updates the pantry by subtracting ingredients used in a cooked recipe.
 * For each ingredient in the recipe:
 * - It attempts to find a matching ingredient in the pantry (by name).
 * - If found, it converts the recipe's ingredient quantity to the pantry item's default unit.
 * - It then reduces the pantry item's quantity by the amount used.
 * - If an ingredient is not found in the pantry, or if there's insufficient quantity,
 * a warning is printed, and that specific ingredient's subtraction is skipped.
 *
 * @param recipe The cooked recipe containing the list of ingredients to subtract.
 */
fun PersistenceManager<List<IngredientItem>>.subtractIngredientsFromRecipe(recipe: Recipe) {
    // We update the state of the pantry manager
    update {
        // 'currentPantryList' is the List<IngredientItem> from the StateFlow
        val updatedPantryList = this.toMutableList()
        var anyInsufficient = false

        println("\nPantryManager: Attempting to subtract ingredients for recipe '${recipe.name}'.")

        recipe.ingredients.forEach { recipeIngredient ->
            // Iterate through ingredients in the cooked recipe
            // Find the matching ingredient in the current pantry list by name
            val pantryItemIndex = updatedPantryList.indexOfFirst { it.name == recipeIngredient.name }

            if (pantryItemIndex != -1) {
                val pantryItem = updatedPantryList[pantryItemIndex]

                // Convert the recipe's ingredient quantity to the pantry item's default unit
                val quantityUsedInPantryUnit = try {
                    recipeIngredient.quantity.convert(
                        from = recipeIngredient.unit,
                        to = when (pantryItem.defaultUnit) {
                            "g" -> IngredientUnit.WeightUnit.Gram
                            "kg" -> IngredientUnit.WeightUnit.Kilogram
                            "oz" -> IngredientUnit.WeightUnit.Ounce
                            "lb" -> IngredientUnit.WeightUnit.Pound
                            "ml" -> IngredientUnit.VolumeUnit.Milliliter
                            "L" -> IngredientUnit.VolumeUnit.Liter
                            "tsp" -> IngredientUnit.VolumeUnit.Teaspoon
                            "tbsp" -> IngredientUnit.VolumeUnit.Tablespoon
                            "cup" -> IngredientUnit.VolumeUnit.Cup
                            "piece" -> IngredientUnit.CountUnit.Piece
                            "slice" -> IngredientUnit.CountUnit.Slice
                            "pinch" -> IngredientUnit.CountUnit.Pinch
                            "dash" -> IngredientUnit.CountUnit.Dash
                            // Add more unit mappings as per your IngredientUnit definitions
                            else -> throw IllegalArgumentException("Unknown pantry default unit: ${pantryItem.defaultUnit}")
                        },
                    )
                } catch (e: Exception) {
                    println(
                        "PantryManager Warning: Could not convert ${recipeIngredient.quantity} ${recipeIngredient.unit.shortName} of ${recipeIngredient.name} to ${pantryItem.defaultUnit} for subtraction: ${e.message}. Skipping this ingredient.",
                    )
                    return@forEach // Skip to the next ingredient in the recipe
                }

                if (pantryItem.quantity >= quantityUsedInPantryUnit) {
                    // Enough stock in pantry, subtract the quantity
                    val newQuantity = pantryItem.quantity - quantityUsedInPantryUnit
                    updatedPantryList[pantryItemIndex] = pantryItem.copy(quantity = newQuantity)
                    println(
                        "PantryManager: Subtracted ${quantityUsedInPantryUnit.roundToDecimalPlaces(
                            2,
                        )} ${pantryItem.defaultUnit} of ${recipeIngredient.name}. New pantry stock: ${newQuantity.roundToDecimalPlaces(
                            2,
                        )} ${pantryItem.defaultUnit}.",
                    )
                } else {
                    // Not enough stock in pantry
                    println(
                        "PantryManager Warning: Insufficient stock for ${recipeIngredient.name}. Needed: ${quantityUsedInPantryUnit.roundToDecimalPlaces(
                            2,
                        )} ${pantryItem.defaultUnit}, Available: ${pantryItem.quantity.roundToDecimalPlaces(
                            2,
                        )} ${pantryItem.defaultUnit}. Skipping subtraction for this item.",
                    )
                    anyInsufficient = true
                }
            } else {
                // Ingredient not found in pantry
                println(
                    "PantryManager Warning: Ingredient '${recipeIngredient.name}' from recipe not found in pantry. Skipping subtraction for this item.",
                )
            }
        }

        if (anyInsufficient) {
            println(
                "PantryManager: Note: Some ingredients for recipe '${recipe.name}' could not be fully subtracted from pantry due to insufficient stock.",
            )
        }
        updatedPantryList // Return the modified list, which will then be saved by the manager
    }
    println("PantryManager: Finished processing ingredient subtraction for recipe '${recipe.name}'.")
    // Optionally, you could add a function to print pantry stock here for debugging
    // printCurrentPantryStock()
}

// Small helper for cleaner logging of Doubles
fun Double.roundToDecimalPlaces(places: Int): Double {
    val multiplier = 10.0.pow(places)
    return round(this * multiplier) / multiplier
}
