package dev.lancy.drp25.utilities


enum class DishType(val displayName: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    DESSERT("Dessert")
}

enum class DishCuisine(val displayName: String) {
    ITALIAN("Italian"),
    MEDITERRANEAN("Mediterranean"),
    CHINESE("Chinese"),
    INDIAN("Indian")
}

enum class DishDiet(val displayName: String) {
    VEGAN("Vegan"),
    VEGETARIAN("Vegetarian"),
    GLUTENFREE("Gluten-free")
}