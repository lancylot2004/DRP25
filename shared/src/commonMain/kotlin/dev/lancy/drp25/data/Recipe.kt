package dev.lancy.drp25.data

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import dev.lancy.drp25.ui.shared.NavTarget
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Recipe(
    val id: String = "",
    val name: String,
    val description: String,
    val rating: Float = 0f,
    val numRatings: Int = 0,
    val portions: Int = 1,
    val cookingTime: Int,
    val cleanupTime: Int? = null,
    val calories: Int? = null,
    val macros: Map<String, Double> = mapOf(),
    val ingredients: List<Ingredient>,
    val keyIngredients: List<String>,
    val diet: Diet? = null,
    val cuisine: Cuisine? = null,
    val mealType: MealType? = null,
    val utensils: List<Utensil>,
    val steps: List<Step>,
    val cardImage: String = "",
    val smallImage: String = "",
    val video: String? = null,
) : NavTarget,
    Parcelable

@Parcelize
@Serializable
class Step(
    var description: String = "",
    var videoTimestamp: Int? = null,
) : Parcelable
