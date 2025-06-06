package dev.lancy.drp25.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator.*
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder


// Global variable to store all recipes
var allRecipes: List<Recipe> = emptyList()

suspend fun fetchRecipes() {
    runCatching {
        client.from("recipes").select().decodeList<Recipe>()
    }.onSuccess {
        allRecipes = it
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

    // Cooking time filters (with upper and lower bounds)
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

    // Key ingredients filters (using CS for contains and OR logic for multiple)
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

    // Effort level filters
    fun PostgrestFilterBuilder.filterByEffortLevel(effortLevel: RecipeEffortLevel) = apply {
        filter("effortLevel", EQ, effortLevel.name)
    }

    fun PostgrestFilterBuilder.filterByEffortLevels(effortLevels: List<RecipeEffortLevel>) = apply {
        filter("effortLevel", IN, effortLevels.map { it.name })
    }

    // Helper function to convert RecipeTag to searchable string
    private fun RecipeTag.toSearchString(): String = when (this) {
        // Diet tags
        is Diet.Vegan -> "Diet.Vegan"
        is Diet.Vegetarian -> "Diet.Vegetarian"
        is Diet.Halal -> "Diet.Halal"
        is Diet.Kosher -> "Diet.Kosher"
        is Diet.GlutenFree -> "Diet.GlutenFree"
        is Diet.DairyFree -> "Diet.DairyFree"
        is Diet.NutFree -> "Diet.NutFree"
        is Diet.LowCarb -> "Diet.LowCarb"
        is Diet.Keto -> "Diet.Keto"
        is Diet.Paleo -> "Diet.Paleo"
        is Diet.Pescatarian -> "Diet.Pescatarian"
        is Diet.Other -> "Diet.Other:${this.name}"

        // Cuisine tags
        is Cuisine.Italian -> "Cuisine.Italian"
        is Cuisine.Chinese -> "Cuisine.Chinese"
        is Cuisine.Indian -> "Cuisine.Indian"
        is Cuisine.Mexican -> "Cuisine.Mexican"
        is Cuisine.American -> "Cuisine.American"
        is Cuisine.French -> "Cuisine.French"
        is Cuisine.Japanese -> "Cuisine.Japanese"
        is Cuisine.Thai -> "Cuisine.Thai"
        is Cuisine.Spanish -> "Cuisine.Spanish"
        is Cuisine.MiddleEastern -> "Cuisine.MiddleEastern"
        is Cuisine.Korean -> "Cuisine.Korean"
        is Cuisine.Greek -> "Cuisine.Greek"
        is Cuisine.African -> "Cuisine.African"
        is Cuisine.German -> "Cuisine.German"
        is Cuisine.Nordic -> "Cuisine.Nordic"
        is Cuisine.Other -> "Cuisine.Other:${this.name}"

        // MealType tags
        is MealType.Breakfast -> "MealType.Breakfast"
        is MealType.Brunch -> "MealType.Brunch"
        is MealType.Lunch -> "MealType.Lunch"
        is MealType.Dinner -> "MealType.Dinner"
        is MealType.Snack -> "MealType.Snack"
        is MealType.Dessert -> "MealType.Dessert"
        is MealType.Supper -> "MealType.Supper"
        is MealType.Other -> "MealType.Other:${this.name}"

        // Utensil tags
        is Utensil.Oven -> "Utensil.Oven:${this.temperature}"
        is Utensil.AirFryer -> "Utensil.AirFryer"
        is Utensil.InstantPot -> "Utensil.InstantPot"
        is Utensil.PressureCooker -> "Utensil.PressureCooker"
        is Utensil.SlowCooker -> "Utensil.SlowCooker"
        is Utensil.Blender -> "Utensil.Blender"
        is Utensil.Steamer -> "Utensil.Steamer"
        is Utensil.Whisk -> "Utensil.Whisk"
        is Utensil.Scale -> "Utensil.Scale"
        is Utensil.GarlicPress -> "Utensil.GarlicPress"
        is Utensil.PastryBag -> "Utensil.PastryBag"
        is Utensil.MuffinTin -> "Utensil.MuffinTin"
        is Utensil.BakingDish -> "Utensil.BakingDish"
        is Utensil.Foil -> "Utensil.Foil"
        is Utensil.Other -> "Utensil.Other:${this.name}"
    }

    // Tags filters (using CS for contains and OR logic for multiple)
    fun PostgrestFilterBuilder.filterByTag(tag: RecipeTag) = apply {
        filter("tags", CS, "[\"${tag.toSearchString()}\"]")
    }

    fun PostgrestFilterBuilder.filterByAnyTags(tags: List<RecipeTag>) = apply {
        or {
            tags.forEach { tag ->
                filter("tags", CS, "[\"${tag.toSearchString()}\"]")
            }
        }
    }

    fun PostgrestFilterBuilder.filterByAllTags(tags: List<RecipeTag>) = apply {
        tags.forEach { tag ->
            filter("tags", CS, "[\"${tag.toSearchString()}\"]")
        }
    }

    // Specific tag type filters
    fun PostgrestFilterBuilder.filterByDiet(diet: Diet) = apply {
        filterByTag(diet)
    }

    fun PostgrestFilterBuilder.filterByAnyDiets(diets: List<Diet>) = apply {
        or {
            diets.forEach { diet ->
                filter("tags", CS, "[\"${diet.toSearchString()}\"]")
            }
        }
    }

    fun PostgrestFilterBuilder.filterByCuisine(cuisine: Cuisine) = apply {
        filterByTag(cuisine)
    }

    fun PostgrestFilterBuilder.filterByAnyCuisines(cuisines: List<Cuisine>) = apply {
        or {
            cuisines.forEach { cuisine ->
                filter("tags", CS, "[\"${cuisine.toSearchString()}\"]")
            }
        }
    }

    fun PostgrestFilterBuilder.filterByMealType(mealType: MealType) = apply {
        filterByTag(mealType)
    }

    fun PostgrestFilterBuilder.filterByAnyMealTypes(mealTypes: List<MealType>) = apply {
        or {
            mealTypes.forEach { mealType ->
                filter("tags", CS, "[\"${mealType.toSearchString()}\"]")
            }
        }
    }

    fun PostgrestFilterBuilder.filterByUtensil(utensil: Utensil) = apply {
        filterByTag(utensil)
    }

    fun PostgrestFilterBuilder.filterByAnyUtensils(utensils: List<Utensil>) = apply {
        or {
            utensils.forEach { utensil ->
                filter("tags", CS, "[\"${utensil.toSearchString()}\"]")
            }
        }
    }

    // Specialized utensil filters
    fun PostgrestFilterBuilder.filterByOvenTemperature(minTemp: Int, maxTemp: Int) = apply {
        // This would require custom logic or storing oven temp separately
        // For now, filter by any oven and handle temperature filtering in app
        filter("tags", LIKE, "%Utensil.Oven:%")
    }

    fun PostgrestFilterBuilder.filterByRequiresOven() = apply {
        filter("tags", LIKE, "%Utensil.Oven:%")
    }

    // Convenience filters for common combinations
    fun PostgrestFilterBuilder.filterVegetarianFriendly() = apply {
        or {
            filter("tags", CS, "[\"${Diet.Vegan.toSearchString()}\"]")
            filter("tags", CS, "[\"${Diet.Vegetarian.toSearchString()}\"]")
        }
    }

    fun PostgrestFilterBuilder.filterGlutenFriendly() = apply {
        filter("tags", CS, "[\"${Diet.GlutenFree.toSearchString()}\"]")
    }

    fun PostgrestFilterBuilder.filterQuickCookingMethods() = apply {
        or {
            filter("tags", CS, "[\"${Utensil.AirFryer.toSearchString()}\"]")
            filter("tags", CS, "[\"${Utensil.InstantPot.toSearchString()}\"]")
            filter("tags", CS, "[\"${Utensil.PressureCooker.toSearchString()}\"]")
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

    // Combined time filters (total time = cooking + cleanup)
    fun PostgrestFilterBuilder.filterByMaxTotalTime(maxTotalTime: Int) = apply {
        // This assumes you have a computed column or use raw SQL
        // Alternative: filter in application code after fetching
        filter("(cookingTime + COALESCE(cleanupTime, 0))", LTE, maxTotalTime)
    }

    // Complex combo filters
    fun PostgrestFilterBuilder.filterQuickAndEasy(maxCookingTime: Int = 30, maxEffortLevel: RecipeEffortLevel) = apply {
        filterByMaxCookingTime(maxCookingTime)
        filterByEffortLevel(maxEffortLevel)
    }

    fun PostgrestFilterBuilder.filterHealthy(maxCalories: Int? = null, minRating: Double = 4.0) = apply {
        maxCalories?.let { filterByMaxCalories(it) }
        filterByMinRating(minRating)
        filterByCaloriesNotNull()
    }
}

// Usage examples:
/*
// Basic usage in your repository/service
val recipes = supabase.from("recipes").select {
    // Apply single filter
    filterByMinRating(4.0)

    // Apply multiple filters
    filterByMaxCookingTime(45)
    filterByHasImage()
    filterByAnyKeyIngredients(listOf("chicken", "vegetables"))

    // Use range filters
    filterByCookingTimeRange(15, 60)
    filterByCaloriesRange(200, 800)

    // Tag-specific filters
    filterByDiet(Diet.Vegan)
    filterByCuisine(Cuisine.Italian)
    filterByMealType(MealType.Dinner)
    filterByUtensil(Utensil.Oven(375))

    // Multiple tag filters
    filterByAnyDiets(listOf(Diet.Vegan, Diet.Vegetarian))
    filterByAnyCuisines(listOf(Cuisine.Italian, Cuisine.French))

    // Complex filters
    filterQuickAndEasy(30, RecipeEffortLevel.LOW_EFFORT)
    filterVegetarianFriendly()
    filterQuickCookingMethods()
}.decodeList<Recipe>()

// Advanced filtering with OR conditions
val advancedRecipes = supabase.from("recipes").select {
    or {
        filterByMinRating(4.5)
        and {
            filterByMaxCookingTime(20)
            filterByEffortLevel(RecipeEffortLevel.LOW_EFFORT)
            filterVegetarianFriendly()
        }
    }
}.decodeList<Recipe>()

// Filter by multiple criteria
val healthyItalianRecipes = supabase.from("recipes").select {
    filterByCuisine(Cuisine.Italian)
    filterByMaxCalories(600)
    filterByMinRating(4.0)
    filterByEffortLevel(RecipeEffortLevel.MEDIUM_EFFORT)
}.decodeList<Recipe>()
*/