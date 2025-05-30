package dev.lancy.drp25.data

import arrow.core.toNonEmptyListOrNull
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
    val sections: List<RecipeSection>,
    val tags: List<RecipeTag>,
    val imageURL: String? = null,
)

val example = Recipe(
    name = "Example Recipe",
    description = "This is an example recipe for demonstration purposes.",
    rating = 4.5,
    portions = 4,
    cookingTime = 30,
    cleanupTime = 15,
    calories = 500,
    macros = mapOf("Protein" to 20.0, "Carbs" to 60.0, "Fats" to 10.0),
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
    tags = listOf(
        RecipeTag.Diet.Vegan,
        RecipeTag.Cuisine.Italian,
        RecipeTag.MealType.Dinner
    ),
    imageURL = "https://i.ytimg.com/vi/LOXyOlLUX_A/hqdefault.jpg"
)
