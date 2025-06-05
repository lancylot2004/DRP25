package dev.lancy.drp25.data

import kotlinx.serialization.Serializable

@Serializable
sealed class RecipeTag

sealed class Diet : RecipeTag() {
    data object Vegan : Diet()
    data object Vegetarian : Diet()
    data object Halal : Diet()
    data object Kosher : Diet()
    data object GlutenFree : Diet()
    data object DairyFree : Diet()
    data object NutFree : Diet()
    data object LowCarb : Diet()
    data object Keto : Diet()
    data object Paleo : Diet()
    data object Pescatarian : Diet()
    data class Other(val name: String) : Diet()
}

sealed class Cuisine : RecipeTag() {
    data object Italian : Cuisine()
    data object Chinese : Cuisine()
    data object Indian : Cuisine()
    data object Mexican : Cuisine()
    data object American : Cuisine()
    data object French : Cuisine()
    data object Japanese : Cuisine()
    data object Thai : Cuisine()
    data object Spanish : Cuisine()
    data object MiddleEastern : Cuisine()
    data object Korean : Cuisine()
    data object Greek : Cuisine()
    data object African : Cuisine()
    data object German : Cuisine()
    data object Nordic : Cuisine()
    data class Other(val name: String) : Cuisine()
}

sealed class MealType : RecipeTag() {
    data object Breakfast : MealType()
    data object Brunch : MealType()
    data object Lunch : MealType()
    data object Dinner : MealType()
    data object Snack : MealType()
    data object Dessert : MealType()
    data object Supper : MealType()
    data class Other(val name: String) : MealType()
}

@Serializable
sealed class Utensil : RecipeTag() {
    data class Oven(val temperature: Int) : Utensil()
    data object AirFryer : Utensil()
    data object InstantPot : Utensil()
    data object PressureCooker : Utensil()
    data object SlowCooker : Utensil()
    data object Blender : Utensil()
    data object Steamer : Utensil()
    data object Whisk : Utensil()
    data object Scale : Utensil()
    data object GarlicPress : Utensil()
    data object PastryBag : Utensil()
    data object MuffinTin : Utensil()
    data object BakingDish : Utensil()
    data object Foil : Utensil()
    data class Other(val name: String) : Utensil()
}

enum class RecipeEffortLevel(val displayName: String) {
    LOW_EFFORT("low effort"),
    MEDIUM_EFFORT("medium effort"),
    HIGH_EFFORT("high effort"),
    CHALLENGING("challenging"),
}

