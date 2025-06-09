package dev.lancy.drp25.utilities

import androidx.compose.runtime.mutableStateOf
import dev.lancy.drp25.data.Cuisine
import dev.lancy.drp25.data.Diet
import dev.lancy.drp25.data.FilterValues
import dev.lancy.drp25.data.MealType
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.data.client
import dev.lancy.drp25.data.Ingredients
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import kotlin.math.ceil
import kotlin.math.floor


// Global variable to store all recipes
val allRecipes = mutableStateOf(listOf<Recipe>())

// Global variable to store filtered recipes
val filteredRecipes = mutableStateOf(listOf<Recipe>())

// Fetch all recipes in the database
suspend fun fetchAllRecipes() {
    runCatching {
        client.from("recipes").select().decodeList<Recipe>()
    }.onSuccess {
        allRecipes.value = it
        println("Fetched ${it.size} recipes")
    }.onFailure {
        println("Failed to fetch recipes: ${it.message}")
    }
}

// Filter all recipes with the given filters
suspend fun applyFilters(filters: FilterValues) {
    if (allRecipes.value.isEmpty()) {
        fetchAllRecipes()
        return
    }
    try {
        val filtered = fetchFilteredRecipes(filters)
        filteredRecipes.value = filtered
        println("With filters: $filters")
        println("Applied filters: ${filteredRecipes.value.size} recipes match: ${filteredRecipes.value}")
    } catch (e: Exception) {
        println("Error applying filters: ${e.message}")
    }
}

// Fetch filtered recipes from the queried database
suspend fun fetchFilteredRecipes(filters: FilterValues): List<Recipe> {
    return client.from("recipes").select {
        filter {
            applyRecipeFilters(filters)
        }
    }.decodeList<Recipe>()
}

// Extension function to apply all filters within a filter block
fun PostgrestFilterBuilder.applyRecipeFilters(filters: FilterValues) {
    // Apply time range filter
    filter("cookingTime", FilterOperator.IN, filters.timeRange.toIntString())

    // Apply minimum rating filter
    filter("rating", FilterOperator.GTE, filters.rating)

//    // Apply meal type filter
//    if (filters.selectedMealTypes.isNotEmpty()) {
//        val mealTypeStrings = filters.selectedMealTypes.map { it.name.lowercase() }
//        val mealTypeValues = mealTypeStrings.joinToString(",") { "'$it'" }
//        filter("mealType", FilterOperator.IN, "($mealTypeValues)")
//    }
//
//    // Apply cuisine filter
//    if (filters.selectedCuisines.isNotEmpty()) {
//        val cuisineStrings = filters.selectedCuisines.map { it.name.lowercase() }
//        val cuisineValues = cuisineStrings.joinToString(",") { "'$it'" }
//        filter("cuisine", FilterOperator.IN, "($cuisineValues)")
//    }
//
//    // Apply diet filter
//    if (filters.selectedDiets.isNotEmpty()) {
//        val dietStrings = filters.selectedDiets.map { it.name.lowercase() }
//        val dietValues = dietStrings.joinToString(",") { "'$it'" }
//        filter("diet", FilterOperator.IN, "($dietValues)")
//    }
//
//    // Apply ingredient filters
//    applyIngredientFilters(filters.includedIngredients, filters.avoidedIngredients)
//
//    // Apply equipment filter
//    if (filters.useMyEquipmentOnly) {
//        applyEquipmentFilter()
//    }

    // Apply calorie range filter
    filter("calories", FilterOperator.IN, filters.calorieRange.toIntString())

    // Apply macro filters (protein, fat, carbs) - accessing JSON fields
    filter("macros->protein", FilterOperator.IN, filters.proteinRange.toIntString())
    filter("macros->fat", FilterOperator.IN, filters.fatRange.toIntString())
    filter("macros->carbs", FilterOperator.IN, filters.carbsRange.toIntString())
}


// Apply ingredient filters
private fun PostgrestFilterBuilder.applyIngredientFilters(
    includedIngredients: Set<Ingredients>,
    avoidedIngredients: Set<Ingredients>
) {
    // Include recipes that contain ALL specified ingredients
    if (includedIngredients.isNotEmpty()) {
        includedIngredients.forEach { ingredient ->
            filter("keyIngredients", FilterOperator.CS, "[\"${ingredient.name.lowercase()}\"]")
        }
    }

    // Exclude recipes that contain ANY avoided ingredients
    if (avoidedIngredients.isNotEmpty()) {
        avoidedIngredients.forEach { ingredient ->
            filterNot("keyIngredients", FilterOperator.CS, "[\"${ingredient.name.lowercase()}\"]")
        }
    }
}

// Apply equipment filter
private fun PostgrestFilterBuilder.applyEquipmentFilter() {
    // Filter by basic utensils only
    val basicUtensils = listOf<String>()
    val utensilsJson = "[${basicUtensils.joinToString(",") { "\"$it\"" }}]"
    filter("utensils", FilterOperator.CS, utensilsJson)
}


/** Function to create a list of values from a range */
fun ClosedFloatingPointRange<Float>.toIntString(): String {
    return if (ceil(start).toInt() <= floor(endInclusive).toInt()) {
        (ceil(start).toInt()..floor(endInclusive).toInt()).joinToString(", ", "(", ")")
    } else { "()" }
}
