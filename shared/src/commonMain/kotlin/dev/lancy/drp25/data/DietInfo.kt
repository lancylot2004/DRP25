package dev.lancy.drp25.data

import kotlinx.serialization.*

/* DIET TYPE, CUISINE, MEAL TYPE, UTENSILS */

@Serializable
enum class Diet(val displayName: String) {
    VEGAN("Vegan"),
    VEGETARIAN("Vegetarian"),
    HALAL("Halal"),
    KOSHER("Kosher"),
    GLUTEN_FREE("Gluten-free"),
    DAIRY_FREE("Dairy-free"),
    NUT_FREE("Nut-free"),
    LOW_CARB("Low carb"),
    KETO("Keto"),
    PALEO("Paleo"),
    PESCATARIAN("Pescatarian");

    override fun toString() = displayName
}

@Serializable
enum class Cuisine(val displayName: String) {
    ITALIAN("Italian"),
    CHINESE("Chinese"),
    MEDITERRANEAN("Mediterranean"),
    INDIAN("Indian"),
    MEXICAN("Mexican"),
    AMERICAN("American"),
    FRENCH("French"),
    JAPANESE("Japanese"),
    THAI("Thai"),
    SPANISH("Spanish"),
    MIDDLE_EASTERN("Middle Eastern"),
    KOREAN("Korean"),
    GREEK("Greek"),
    AFRICAN("African"),
    GERMAN("German"),
    NORDIC("Nordic");

    override fun toString() = displayName
}

@Serializable
enum class MealType(val displayName: String) {
    BREAKFAST("Breakfast"),
    BRUNCH("Brunch"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACK("Snack"),
    DESSERT("Dessert"),
    SUPPER("Supper");

    override fun toString() = displayName
}

@Serializable
enum class Utensil(val displayName: String) {
    AIR_FRYER("Air Fryer"),
    INSTANT_POT("Instant Pot"),
    PRESSURE_COOKER("Pressure Cooker"),
    SLOW_COOKER("Slow Cooker"),
    BLENDER("Blender"),
    STEAMER("Steamer"),
    WHISK("Whisk"),
    SCALE("Scale"),
    GARLIC_PRESS("Garlic Press"),
    PASTRY_BAG("Pastry Bag"),
    MUFFIN_TIN("Muffin Tin"),
    BAKING_DISH("Baking Dish"),
    POT("Pot"),
    PAN("Pan"),
    SPATULA("Spatula"),
    GLASS("Glass"),
    TOASTER("Toaster"),
    BOWL("Bowl"),
    FOIL("Foil");

    override fun toString() = displayName
}