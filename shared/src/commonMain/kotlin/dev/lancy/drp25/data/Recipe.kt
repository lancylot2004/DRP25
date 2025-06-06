package dev.lancy.drp25.data

import arrow.core.toNonEmptyListOrNull
import dev.lancy.drp25.ui.shared.NavTarget
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val name: String,
    val description: String,
    val rating: Double,
    val portions: Int,
    val cookingTime: Int,
    val cleanupTime: Int?,
    val calories: Int?,
    val macros: Map<String, Double> = mapOf(),
    val keyIngredients: List<String>,
    val sections: List<RecipeSection>,
    val effortLevel: RecipeEffortLevel,
    val tags: List<RecipeTag>,
    val imageURL: String? = null,
): NavTarget

enum class RecipeEffortLevel(val displayName: String) {
    LOW_EFFORT("Low effort"),
    QUICK_EFFORT("Quick prep"),
    MEDIUM_EFFORT("Medium effort"),
    HIGH_EFFORT("High effort"),
    FATALITY("Fatality"),
}

val example = Recipe(
    name = "Example Recipe",
    description = "This is an example recipe for demonstration purposes.",
    rating = 4.5,
    portions = 4,
    cookingTime = 30,
    cleanupTime = 15,
    calories = 500,
    macros = mapOf("Protein" to 20.0, "Carbs" to 60.0, "Fats" to 10.0),
    keyIngredients = listOf("chicken, potatoes"),
    sections = listOf(
        RecipeSection(
            title = "Preparation",
            steps = listOf(RecipeStep("Chop the vegetables."), RecipeStep("Marinate the meat.")).toNonEmptyListOrNull()!!
        ),
        RecipeSection(
            title = "Cooking",
            steps = listOf(RecipeStep("Cook the meat in a pan."), RecipeStep("Add vegetables and stir-fry.")).toNonEmptyListOrNull()!!
        )
    ),
    effortLevel = RecipeEffortLevel.LOW_EFFORT,
    tags = listOf(
        RecipeTag.Diet.Vegan,
        RecipeTag.Cuisine.Italian,
        RecipeTag.MealType.Dinner
    ),
    imageURL = "https://www.halfbakedharvest.com/wp-content/uploads/2019/07/Bucatini-Amatriciana-1-700x1050.jpg"
)
