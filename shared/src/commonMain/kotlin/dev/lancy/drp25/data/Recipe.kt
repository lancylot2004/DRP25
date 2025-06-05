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
    val ingredients: List<Ingredient>,
    val keyIngredients: List<String>,
    val sections: List<RecipeSection>,
    val effortLevel: RecipeEffortLevel,
    val tags: List<RecipeTag>,
    val imageURL: String? = null,
)

// Recipe Card
data class RecipeCard(
    val name: String,
    val rating: Double,
    val portions: Int,
    val cookingTime: Int,
    val cleanupTime: Int?,
    val calories: Int?,
    val macros: Map<String, Double> = mapOf(),
    val keyIngredients: List<String>,
    val effortLevel: RecipeEffortLevel,
    val tags: List<RecipeTag>,
    val imageURL: String?
) {
    companion object {
        fun fromRecipe(recipe: Recipe): RecipeCard = RecipeCard(
            name = recipe.name,
            rating = recipe.rating,
            portions = recipe.portions,
            cookingTime = recipe.cookingTime,
            cleanupTime = recipe.cleanupTime,
            calories = recipe.calories,
            macros = recipe.macros,
            keyIngredients = recipe.keyIngredients,
            effortLevel = recipe.effortLevel,
            tags = recipe.tags,
            imageURL = recipe.imageURL
        )
    }
}