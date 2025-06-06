package dev.lancy.drp25.data

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable(with = RecipeTagSerializer::class)
sealed class RecipeTag {
    abstract override fun toString(): String
}

sealed class Diet : RecipeTag() {
    @Serializable object Vegan : Diet() { override fun toString() = "Vegan" }
    @Serializable object Vegetarian : Diet() { override fun toString() = "Vegetarian" }
    @Serializable object Halal : Diet() { override fun toString() = "Halal" }
    @Serializable object Kosher : Diet() { override fun toString() = "Kosher" }
    @Serializable object GlutenFree : Diet() { override fun toString() = "Gluten free" }
    @Serializable object DairyFree : Diet() { override fun toString() = "Dairy free" }
    @Serializable object NutFree : Diet() { override fun toString() = "Nut free" }
    @Serializable object LowCarb : Diet() { override fun toString() = "Low carb" }
    @Serializable object Keto : Diet() { override fun toString() = "Keto" }
    @Serializable object Paleo : Diet() { override fun toString() = "Paleo" }
    @Serializable object Pescatarian : Diet() { override fun toString() = "Pescatarian" }
    @Serializable data class Other(val name: String) : Diet() { override fun toString() = name }
}

sealed class Cuisine : RecipeTag() {
    @Serializable object Italian : Cuisine() { override fun toString() = "Italian" }
    @Serializable object Chinese : Cuisine() { override fun toString() = "Chinese" }
    @Serializable object Indian : Cuisine() { override fun toString() = "Indian" }
    @Serializable object Mexican : Cuisine() { override fun toString() = "Mexican" }
    @Serializable object American : Cuisine() { override fun toString() = "American" }
    @Serializable object French : Cuisine() { override fun toString() = "French" }
    @Serializable object Japanese : Cuisine() { override fun toString() = "Japanese" }
    @Serializable object Thai : Cuisine() { override fun toString() = "Thai" }
    @Serializable object Spanish : Cuisine() { override fun toString() = "Spanish" }
    @Serializable object MiddleEastern : Cuisine() { override fun toString() = "Middle Eastern" }
    @Serializable object Korean : Cuisine() { override fun toString() = "Korean" }
    @Serializable object Greek : Cuisine() { override fun toString() = "Greek" }
    @Serializable object African : Cuisine() { override fun toString() = "African" }
    @Serializable object German : Cuisine() { override fun toString() = "German" }
    @Serializable object Nordic : Cuisine() { override fun toString() = "Nordic" }
    @Serializable data class Other(val name: String) : Cuisine() { override fun toString() = name }
}

sealed class MealType : RecipeTag() {
    @Serializable object Breakfast : MealType() { override fun toString() = "Breakfast" }
    @Serializable object Brunch : MealType() { override fun toString() = "Brunch" }
    @Serializable object Lunch : MealType() { override fun toString() = "Lunch" }
    @Serializable object Dinner : MealType() { override fun toString() = "Dinner" }
    @Serializable object Snack : MealType() { override fun toString() = "Snack" }
    @Serializable object Dessert : MealType() { override fun toString() = "Dessert" }
    @Serializable object Supper : MealType() { override fun toString() = "Supper" }
    @Serializable data class Other(val name: String) : MealType() { override fun toString() = name }
}

@Serializable
sealed class Utensil : RecipeTag() {
    @Serializable data class Oven(val temperature: Int) : Utensil() {
        override fun toString() = "Oven($temperature°C)"
    }
    @Serializable object AirFryer : Utensil() { override fun toString() = "Air Fryer" }
    @Serializable object InstantPot : Utensil() { override fun toString() = "Instant Pot" }
    @Serializable object PressureCooker : Utensil() { override fun toString() = "Pressure Cooker" }
    @Serializable object SlowCooker : Utensil() { override fun toString() = "Slow Cooker" }
    @Serializable object Blender : Utensil() { override fun toString() = "Blender" }
    @Serializable object Steamer : Utensil() { override fun toString() = "Steamer" }
    @Serializable object Whisk : Utensil() { override fun toString() = "Whisk" }
    @Serializable object Scale : Utensil() { override fun toString() = "Scale" }
    @Serializable object GarlicPress : Utensil() { override fun toString() = "Garlic Press" }
    @Serializable object PastryBag : Utensil() { override fun toString() = "Pastry Bag" }
    @Serializable object MuffinTin : Utensil() { override fun toString() = "Muffin Tin" }
    @Serializable object BakingDish : Utensil() { override fun toString() = "Baking Dish" }
    @Serializable object Foil : Utensil() { override fun toString() = "Foil" }
    @Serializable data class Other(val name: String) : Utensil() { override fun toString() = name }
}

enum class RecipeEffortLevel(val displayName: String) {
    LOW_EFFORT("low effort"),
    MEDIUM_EFFORT("medium effort"),
    HIGH_EFFORT("high effort"),
    CHALLENGING("challenging");
    override fun toString() = displayName
}

object RecipeTagSerializer : KSerializer<RecipeTag> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RecipeTag", PrimitiveKind.STRING)

    private val dietMap = listOf(
        Diet.Vegan, Diet.Vegetarian, Diet.Halal, Diet.Kosher, Diet.GlutenFree,
        Diet.DairyFree, Diet.NutFree, Diet.LowCarb, Diet.Keto, Diet.Paleo, Diet.Pescatarian
    ).associateBy { it.toString().lowercase() }

    private val cuisineMap = listOf(
        Cuisine.Italian, Cuisine.Chinese, Cuisine.Indian, Cuisine.Mexican, Cuisine.American,
        Cuisine.French, Cuisine.Japanese, Cuisine.Thai, Cuisine.Spanish, Cuisine.MiddleEastern,
        Cuisine.Korean, Cuisine.Greek, Cuisine.African, Cuisine.German, Cuisine.Nordic
    ).associateBy { it.toString().lowercase() }

    private val mealTypeMap = listOf(
        MealType.Breakfast, MealType.Brunch, MealType.Lunch, MealType.Dinner,
        MealType.Snack, MealType.Dessert, MealType.Supper
    ).associateBy { it.toString().lowercase() }

    private val utensilMap = listOf(
        Utensil.AirFryer, Utensil.InstantPot, Utensil.PressureCooker, Utensil.SlowCooker,
        Utensil.Blender, Utensil.Steamer, Utensil.Whisk, Utensil.Scale, Utensil.GarlicPress,
        Utensil.PastryBag, Utensil.MuffinTin, Utensil.BakingDish, Utensil.Foil
    ).associateBy { it.toString().lowercase() }

    override fun deserialize(decoder: Decoder): RecipeTag {
        val raw = decoder.decodeString().lowercase()
        return dietMap[raw] ?: cuisineMap[raw] ?: mealTypeMap[raw] ?: utensilMap[raw] ?: Diet.Other(raw)
    }

    override fun serialize(encoder: Encoder, value: RecipeTag) {
        encoder.encodeString(value.toString())
    }
}
