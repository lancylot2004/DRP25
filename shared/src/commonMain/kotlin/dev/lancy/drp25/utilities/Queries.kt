package dev.lancy.drp25.utilities

import androidx.compose.runtime.mutableStateOf
import dev.lancy.drp25.data.Cuisine
import dev.lancy.drp25.data.Diet
import dev.lancy.drp25.data.MealType
import dev.lancy.drp25.data.Recipe
import dev.lancy.drp25.data.Utensil
import dev.lancy.drp25.data.client
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator.*
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder

// Global variable to store all recipes
val allRecipes = mutableStateOf(listOf<Recipe>())

suspend fun fetchAllRecipes() {
    runCatching {
        client.from("recipes").select().decodeList<Recipe>()
    }.onSuccess {
        allRecipes.value += it
        println("Fetched ${it.size} recipes")
    }.onFailure {
        println("Failed to fetch recipes: ${it.message}")
    }
}

// Recipe filter functions for Supabase-kt
object RecipeFilters {

    // Name filters
    fun PostgrestFilterBuilder.filterByName(name: String) = apply {
        filter("name", EQ, name)
    }

    fun PostgrestFilterBuilder.filterByNameContains(searchTerm: String) = apply {
        filter("name", ILIKE, "%$searchTerm%")
    }

    fun PostgrestFilterBuilder.filterByNameStartsWith(prefix: String) = apply {
        filter("name", LIKE, "$prefix%")
    }

    // Description filters
    fun PostgrestFilterBuilder.filterByDescriptionContains(searchTerm: String) = apply {
        filter("description", ILIKE, "%$searchTerm%")
    }

    fun PostgrestFilterBuilder.filterByDescriptionNotEmpty() = apply {
        filter("description", NEQ, "")
        filter("description", IS, "NOT NULL")
    }

    // Rating filters
    fun PostgrestFilterBuilder.filterByRating(rating: Double) = apply {
        filter("rating", EQ, rating)
    }

    fun PostgrestFilterBuilder.filterByMinRating(minRating: Double) = apply {
        filter("rating", GTE, minRating)
    }

    fun PostgrestFilterBuilder.filterByMaxRating(maxRating: Double) = apply {
        filter("rating", LTE, maxRating)
    }

    fun PostgrestFilterBuilder.filterByRatingRange(minRating: Double, maxRating: Double) = apply {
        filter("rating", GTE, minRating)
        filter("rating", LTE, maxRating)
    }

    // Portions filters
    fun PostgrestFilterBuilder.filterByPortions(portions: Int) = apply {
        filter("portions", EQ, portions)
    }

    fun PostgrestFilterBuilder.filterByMinPortions(minPortions: Int) = apply {
        filter("portions", GTE, minPortions)
    }

    fun PostgrestFilterBuilder.filterByMaxPortions(maxPortions: Int) = apply {
        filter("portions", LTE, maxPortions)
    }

    fun PostgrestFilterBuilder.filterByPortionsRange(minPortions: Int, maxPortions: Int) = apply {
        filter("portions", GTE, minPortions)
        filter("portions", LTE, maxPortions)
    }

    // Cooking time filters
    fun PostgrestFilterBuilder.filterByCookingTime(cookingTime: Int) = apply {
        filter("cookingTime", EQ, cookingTime)
    }

    fun PostgrestFilterBuilder.filterByMinCookingTime(minTime: Int) = apply {
        filter("cookingTime", GTE, minTime)
    }

    fun PostgrestFilterBuilder.filterByMaxCookingTime(maxTime: Int) = apply {
        filter("cookingTime", LTE, maxTime)
    }

    fun PostgrestFilterBuilder.filterByCookingTimeRange(minTime: Int, maxTime: Int) = apply {
        filter("cookingTime", GTE, minTime)
        filter("cookingTime", LTE, maxTime)
    }

    // Cleanup time filters (nullable field)
    fun PostgrestFilterBuilder.filterByCleanupTime(cleanupTime: Int) = apply {
        filter("cleanupTime", EQ, cleanupTime)
    }

    fun PostgrestFilterBuilder.filterByMinCleanupTime(minTime: Int) = apply {
        filter("cleanupTime", GTE, minTime)
    }

    fun PostgrestFilterBuilder.filterByMaxCleanupTime(maxTime: Int) = apply {
        filter("cleanupTime", LTE, maxTime)
    }

    fun PostgrestFilterBuilder.filterByCleanupTimeRange(minTime: Int, maxTime: Int) = apply {
        filter("cleanupTime", GTE, minTime)
        filter("cleanupTime", LTE, maxTime)
    }

    fun PostgrestFilterBuilder.filterByCleanupTimeNotNull() = apply {
        filter("cleanupTime", IS, "NOT NULL")
    }

    fun PostgrestFilterBuilder.filterByCleanupTimeIsNull() = apply {
        filter("cleanupTime", IS, "NULL")
    }

    // Calories filters (nullable field)
    fun PostgrestFilterBuilder.filterByCalories(calories: Int) = apply {
        filter("calories", EQ, calories)
    }

    fun PostgrestFilterBuilder.filterByMinCalories(minCalories: Int) = apply {
        filter("calories", GTE, minCalories)
    }

    fun PostgrestFilterBuilder.filterByMaxCalories(maxCalories: Int) = apply {
        filter("calories", LTE, maxCalories)
    }

    fun PostgrestFilterBuilder.filterByCaloriesRange(minCalories: Int, maxCalories: Int) = apply {
        filter("calories", GTE, minCalories)
        filter("calories", LTE, maxCalories)
    }

    fun PostgrestFilterBuilder.filterByCaloriesNotNull() = apply {
        filter("calories", IS, "NOT NULL")
    }

    // Macros filters (assuming JSONB column in database)
    fun PostgrestFilterBuilder.filterByMacroValue(macroName: String, value: Double) = apply {
        filter("macros->>$macroName", EQ, value)
    }

    fun PostgrestFilterBuilder.filterByMinMacroValue(macroName: String, minValue: Double) = apply {
        filter("macros->>$macroName", GTE, minValue)
    }

    fun PostgrestFilterBuilder.filterByMacroExists(macroName: String) = apply {
        filter("macros", CS, "{\"$macroName\"}")
    }

    // Key ingredients filters
    fun PostgrestFilterBuilder.filterByKeyIngredient(ingredient: String) = apply {
        filter("keyIngredients", CS, "[\"$ingredient\"]")
    }

    fun PostgrestFilterBuilder.filterByAnyKeyIngredients(ingredients: List<String>) = apply {
        or {
            ingredients.forEach { ingredient ->
                filter("keyIngredients", CS, "[\"$ingredient\"]")
            }
        }
    }

    fun PostgrestFilterBuilder.filterByAllKeyIngredients(ingredients: List<String>) = apply {
        ingredients.forEach { ingredient ->
            filter("keyIngredients", CS, "[\"$ingredient\"]")
        }
    }

    // Enum-based tag filters
    fun PostgrestFilterBuilder.filterByDiet(diet: Diet) = apply {
        filter("diets", CS, "[\"$diet\"]")
    }

    fun PostgrestFilterBuilder.filterByAnyDiets(diets: List<Diet>) = apply {
        or {
            diets.forEach { diet ->
                filter("diets", CS, "[\"$diet\"]")
            }
        }
    }

    fun PostgrestFilterBuilder.filterByAllDiets(diets: List<Diet>) = apply {
        diets.forEach { diet ->
            filter("diets", CS, "[\"$diet\"]")
        }
    }

    fun PostgrestFilterBuilder.filterByCuisine(cuisine: Cuisine) = apply {
        filter("cuisines", CS, "[\"$cuisine\"]")
    }

    fun PostgrestFilterBuilder.filterByAnyCuisines(cuisines: List<Cuisine>) = apply {
        or {
            cuisines.forEach { cuisine ->
                filter("cuisines", CS, "[\"$cuisine\"]")
            }
        }
    }

    fun PostgrestFilterBuilder.filterByAllCuisines(cuisines: List<Cuisine>) = apply {
        cuisines.forEach { cuisine ->
            filter("cuisines", CS, "[\"$cuisine\"]")
        }
    }

    fun PostgrestFilterBuilder.filterByMealType(mealType: MealType) = apply {
        filter("mealTypes", CS, "[\"$mealType\"]")
    }

    fun PostgrestFilterBuilder.filterByAnyMealTypes(mealTypes: List<MealType>) = apply {
        or {
            mealTypes.forEach { mealType ->
                filter("mealTypes", CS, "[\"$mealType\"]")
            }
        }
    }

    fun PostgrestFilterBuilder.filterByAllMealTypes(mealTypes: List<MealType>) = apply {
        mealTypes.forEach { mealType ->
            filter("mealTypes", CS, "[\"$mealType\"]")
        }
    }

    fun PostgrestFilterBuilder.filterByUtensil(utensil: Utensil) = apply {
        filter("utensils", CS, "[\"$utensil\"]")
    }

    fun PostgrestFilterBuilder.filterByAnyUtensils(utensils: List<Utensil>) = apply {
        or {
            utensils.forEach { utensil ->
                filter("utensils", CS, "[\"$utensil\"]")
            }
        }
    }

    fun PostgrestFilterBuilder.filterByAllUtensils(utensils: List<Utensil>) = apply {
        utensils.forEach { utensil ->
            filter("utensils", CS, "[\"$utensil\"]")
        }
    }

    // Convenience filters
    fun PostgrestFilterBuilder.filterVegetarianFriendly() = apply {
        or {
            filter("diets", CS, "[\"${Diet.VEGAN}\"]")
            filter("diets", CS, "[\"${Diet.VEGETARIAN}\"]")
        }
    }

    fun PostgrestFilterBuilder.filterGlutenFree() = apply {
        filter("diets", CS, "[\"${Diet.GLUTEN_FREE}\"]")
    }

    fun PostgrestFilterBuilder.filterQuickCookingMethods() = apply {
        or {
            filter("utensils", CS, "[\"${Utensil.AIR_FRYER}\"]")
            filter("utensils", CS, "[\"${Utensil.INSTANT_POT}\"]")
            filter("utensils", CS, "[\"${Utensil.PRESSURE_COOKER}\"]")
        }
    }

    // Image/Video URL filters
    fun PostgrestFilterBuilder.filterByHasImage() = apply {
        filter("imageURL", IS, "NOT NULL")
        filter("imageURL", NEQ, "")
    }

    fun PostgrestFilterBuilder.filterByHasVideo() = apply {
        filter("videoURL", IS, "NOT NULL")
        filter("videoURL", NEQ, "")
    }

    fun PostgrestFilterBuilder.filterByHasMedia() = apply {
        or {
            and {
                filter("imageURL", IS, "NOT NULL")
                filter("imageURL", NEQ, "")
            }
            and {
                filter("videoURL", IS, "NOT NULL")
                filter("videoURL", NEQ, "")
            }
        }
    }

    fun PostgrestFilterBuilder.filterByNoMedia() = apply {
        and {
            or {
                filter("imageURL", IS, "NULL")
                filter("imageURL", EQ, "")
            }
            or {
                filter("videoURL", IS, "NULL")
                filter("videoURL", EQ, "")
            }
        }
    }

    // Combined time filters
    fun PostgrestFilterBuilder.filterByMaxTotalTime(maxTotalTime: Int) = apply {
        filter("(cookingTime + COALESCE(cleanupTime, 0))", LTE, maxTotalTime)
    }

    fun PostgrestFilterBuilder.filterHealthy(maxCalories: Int? = null, minRating: Double = 4.0) = apply {
        maxCalories?.let { filterByMaxCalories(it) }
        filterByMinRating(minRating)
        filterByCaloriesNotNull()
    }
}

// Usage examples:
/*
// Basic usage with enum filters
val recipes = supabase.from("recipes").select {
    // Single enum filters
    filterByDiet(Diet.VEGAN)
    filterByCuisine(Cuisine.ITALIAN)
    filterByMealType(MealType.DINNER)
    filterByUtensil(Utensil.AIR_FRYER)

    // Multiple enum filters
    filterByAnyDiets(listOf(Diet.VEGAN, Diet.VEGETARIAN))
    filterByAnyCuisines(listOf(Cuisine.ITALIAN, Cuisine.FRENCH))

    // Combine with other filters
    filterByMaxCookingTime(45)
    filterByMinRating(4.0)
    filterByHasImage()
}.decodeList<Recipe>()

// Convenience filters
val healthyQuickRecipes = supabase.from("recipes").select {
    filterVegetarianFriendly()
    filterQuickCookingMethods()
    filterByMaxCookingTime(30)
    filterByMinRating(4.0)
}.decodeList<Recipe>()
*/