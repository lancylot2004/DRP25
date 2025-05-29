package dev.lancy.drp25.data

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
