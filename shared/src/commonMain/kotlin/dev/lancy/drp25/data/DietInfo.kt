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
enum class Allergens(val displayName: String) {
    PEANUTS("Peanuts"),
    TREE_NUTS("Tree Nuts"),
    DAIRY("Dairy"),
    EGGS("Eggs"),
    SOY("Soy"),
    WHEAT("Wheat"),
    GLUTEN("Gluten"),
    FISH("Fish"),
    SHELLFISH("Shellfish"),
    SESAME("Sesame"),
    MUSTARD("Mustard"),
    CELERY("Celery"),
    SULFITES("Sulfites"),
    LUPIN("Lupin"),
    MOLLUSCS("Molluscs");

    override fun toString() = displayName
}

@Serializable
enum class Cuisine(val displayName: String) {
    ASIAN("Asian"),
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
    CLEAVER_BUTCHER("Cleaver Butcher"),
    COOK_KNIFE("Cook Knife"),
    FOOD_PROCESSOR("Food Processor"),
    FOOD_SCALE("Food Scale"),
    FORK("Fork"),
    FREEZER("Freezer"),
    FRIDGE("Fridge"),
    FUNNEL("Funnel"),
    GRATER("Grater"),
    GRILL("Grill"),
    HAND_MIXER("Hand Mixer"),
    JUICER("Juicer"),
    KETTLE("Kettle"),
    LADLE("Ladle"),
    MEASURING_CUP("Measuring Cup"),
    MICROWAVE("Microwave"),
    MIXING_BOWL("Mixing Bowl"),
    OVEN("Oven"),
    OVEN_GLOVE("Oven Glove"),
    PAN("Pan"),
    PEELER("Peeler"),
    PRESSURE_COOKER("Pressure Cooker"),
    RICE_COOKER("Rice Cooker"),
    ROLLING_PIN("Rolling Pin"),
    SAUCEPAN("Saucepan"),
    SCISSORS("Scissors"),
    SKIMMER("Skimmer"),
    SPATULA("Spatula"),
    STAND_MIXER("Stand Mixer"),
    STOCK_POT("Stock Pot"),
    STOVE("Stove"),
    STRAINER("Strainer"),
    TENDERIZER("Tenderizer"),
    TOASTER("Toaster"),
    TONGS("Tongs"),
    WHISK("Whisk"),
    WOK("Wok");

    override fun toString() = displayName
}