package dev.lancy.drp25.data

import kotlinx.serialization.Serializable

@Serializable
sealed class RecipeTag {
    sealed class Diet : RecipeTag() {
        data object Vegan : Diet()

        data object Vegetarian : Diet()

        data object Halal : Diet()

        data object GlutenFree : Diet()

        data object Pescatarian : Diet()

        data class Other(
            val name: String,
        ) : Diet()
    }

    sealed class Cuisine : RecipeTag() {
        data object Italian : Cuisine()

        data object Chinese : Cuisine()

        data object Indian : Cuisine()

        data object Mexican : Cuisine()

        data object American : Cuisine()

        data class Other(
            val name: String,
        ) : Cuisine()
    }

    sealed class MealType : RecipeTag() {
        data object Breakfast : MealType()

        data object Lunch : MealType()

        data object Dinner : MealType()

        data object Snack : MealType()

        data object Dessert : MealType()

        data class Other(
            val name: String,
        ) : MealType()
    }

    sealed class Utensil : RecipeTag() {
        data class Oven(
            val temperature: Int,
        ) : Utensil()
    }
}
