package dev.lancy.drp25.data

import dev.lancy.drp25.utilities.IngredientIcon
import kotlinx.serialization.Serializable
import kotlin.math.round
import kotlin.math.roundToInt

@Serializable
enum class IngredientLocation {
    Fridge,
    Freezer,
    Pantry
}

// Compacted and reordered categories by common usage
@Serializable
enum class IngredientType(val displayName: String, val order: Int) {
    ESSENTIALS("Essentials", 1),
    VEGETABLES("Vegetables", 2),
    MEAT("Meat", 3),
    DAIRY("Dairy", 4),
    FRUIT("Fruit", 5),
    BAKING_SNACKS("Baking & Snacks", 6),
    CONDIMENTS("Condiments", 7),
    LIQUIDS("Liquids", 8)
}

@Serializable
data class IngredientItem(
    val icon: IngredientIcon,
    val name: String,
    val defaultUnit: String,
    val incrementAmount: Double = 1.0,
    var quantity: Double = 0.0,
    val location: IngredientLocation,
    val type: IngredientType
)

fun getDefaultIngredients(): List<IngredientItem> = listOf(
    // ESSENTIALS (Order: 1)
    IngredientItem(IngredientIcon.EGG, "Eggs", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.ESSENTIALS),
    IngredientItem(IngredientIcon.SALT, "Salt", "g", 1.0, location = IngredientLocation.Pantry, type = IngredientType.ESSENTIALS),
    IngredientItem(IngredientIcon.PEPPER_BLACK, "Black Pepper", "g", 1.0, location = IngredientLocation.Pantry, type = IngredientType.ESSENTIALS),
    IngredientItem(IngredientIcon.WHEAT_FLOUR, "Wheat Flour", "g", 100.0, location = IngredientLocation.Pantry, type = IngredientType.ESSENTIALS),
    IngredientItem(IngredientIcon.SUGAR_WHITE, "White Sugar", "kg", 1.0, location = IngredientLocation.Pantry, type = IngredientType.ESSENTIALS),
    IngredientItem(IngredientIcon.SUGAR_BROWN, "Brown Sugar", "kg", 1.0, location = IngredientLocation.Pantry, type = IngredientType.ESSENTIALS),
    IngredientItem(IngredientIcon.OIL, "Oil", "ml", 25.0, location = IngredientLocation.Pantry, type = IngredientType.ESSENTIALS),
    IngredientItem(IngredientIcon.BAKING_SODA, "Baking Soda", "g", 0.5, location = IngredientLocation.Pantry, type = IngredientType.ESSENTIALS),
    IngredientItem(IngredientIcon.BUTTER, "Butter", "g", 25.0, location = IngredientLocation.Fridge, type = IngredientType.ESSENTIALS),

    // VEGETABLES (Order: 2)
    IngredientItem(IngredientIcon.TOMATO, "Tomato", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.CHERRY_TOMATO, "Cherry Tomato", "g", 50.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.ONION, "Onion", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.GARLIC, "Garlic", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.CARROT, "Carrot", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.POTATO, "Potato", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.SWEET_POTATO, "Sweet Potato", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.BROCCOLI, "Broccoli", "g", 100.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.CAULIFLOWER, "Cauliflower", "g", 100.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.BELL_PEPPER, "Bell Pepper", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.CHILI, "Chili", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.CUCUMBER, "Cucumber", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.SPINACH, "Spinach", "g", 50.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.LEAFY_GREEN, "Leafy Green", "g", 50.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.GREEN_SALAD, "Green Salad", "g", 50.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.CABBAGE, "Cabbage", "g", 100.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.EGGPLANT, "Eggplant", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.CORN, "Corn", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.PEAS, "Peas", "g", 50.0, location = IngredientLocation.Freezer, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.RADISH, "Radish", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.GINGER, "Ginger", "g", 10.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.PARSLEY, "Parsley", "g", 10.0, location = IngredientLocation.Fridge, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.PUMPKIN, "Pumpkin", "g", 100.0, location = IngredientLocation.Pantry, type = IngredientType.VEGETABLES),
    IngredientItem(IngredientIcon.KIDNEY_BEANS, "Kidney Beans", "g", 100.0, location = IngredientLocation.Pantry, type = IngredientType.VEGETABLES),

    // MEAT (Order: 3)
    IngredientItem(IngredientIcon.CHICKEN_MEAT, "Chicken", "kg", 0.25, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.BEEF_MEAT, "Beef", "kg", 0.25, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.PORK_MEAT, "Pork", "kg", 0.25, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.TURKEY_MEAT, "Turkey", "kg", 0.25, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.LAMB, "Lamb", "kg", 0.25, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.DUCK_MEAT, "Duck", "kg", 0.25, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.STEAK, "Steak", "pcs", 1.0, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.BACON, "Bacon", "g", 1.0, location = IngredientLocation.Fridge, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.HAM, "Ham", "g", 1.0, location = IngredientLocation.Fridge, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.SALAMI, "Salami", "g", 1.0, location = IngredientLocation.Fridge, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.SAUSAGE, "Sausage", "pcs", 1.0, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.POULTRY_LEG, "Poultry Leg", "pcs", 1.0, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.MEATBALLS, "Meatballs", "pcs", 1.0, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.MEAT_BURGER, "Meat Burger", "pcs", 1.0, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.FISH, "Fish", "kg", 0.25, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.SALMON, "Salmon", "kg", 0.25, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.CANNED_TUNA, "Canned Tuna", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.LOBSTER, "Lobster", "pcs", 1.0, location = IngredientLocation.Freezer, type = IngredientType.MEAT),
    IngredientItem(IngredientIcon.OYSTER, "Oyster", "pcs", 1.0, location = IngredientLocation.Freezer, type = IngredientType.MEAT),

    // DAIRY (Order: 4)
    IngredientItem(IngredientIcon.MILK, "Milk", "ml", 250.0, location = IngredientLocation.Fridge, type = IngredientType.DAIRY),
    IngredientItem(IngredientIcon.ALMOND_MILK, "Almond Milk", "ml", 250.0, location = IngredientLocation.Fridge, type = IngredientType.DAIRY),
    IngredientItem(IngredientIcon.CHEDDAR_CHEESE, "Cheddar Cheese", "g", 50.0, location = IngredientLocation.Fridge, type = IngredientType.DAIRY),
    IngredientItem(IngredientIcon.PARMESAN_CHEESE, "Parmesan Cheese", "g", 25.0, location = IngredientLocation.Fridge, type = IngredientType.DAIRY),
    IngredientItem(IngredientIcon.CHEESE_SLICES, "Cheese Slices", "g", 1.0, location = IngredientLocation.Fridge, type = IngredientType.DAIRY),
    IngredientItem(IngredientIcon.YOGURT, "Yogurt", "g", 100.0, location = IngredientLocation.Fridge, type = IngredientType.DAIRY),
    IngredientItem(IngredientIcon.DOUBLE_CREAM, "Double Cream", "ml", 100.0, location = IngredientLocation.Fridge, type = IngredientType.DAIRY),
    IngredientItem(IngredientIcon.ICE_CREAM, "Ice Cream", "ml", 1.0, location = IngredientLocation.Freezer, type = IngredientType.DAIRY),

    // FRUIT (Order: 5)
    IngredientItem(IngredientIcon.APPLE, "Apple", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.BANANA, "Banana", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.LEMON, "Lemon", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.LIME, "Lime", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.STRAWBERRY, "Strawberry", "g", 50.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.BLUEBERRIES, "Blueberries", "g", 50.0, location = IngredientLocation.Freezer, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.RASPBERRY, "Raspberry", "g", 50.0, location = IngredientLocation.Freezer, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.GRAPES, "Grapes", "g", 100.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.ORANGE, "Orange", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.MANGO, "Mango", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.PINEAPPLE, "Pineapple", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.WATERMELON, "Watermelon", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.MELON, "Melon", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.PEACH, "Peach", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.PEAR, "Pear", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.CHERRIES, "Cherries", "g", 50.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.KIWI, "Kiwi", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),
    IngredientItem(IngredientIcon.AVOCADO, "Avocado", "pcs", 1.0, location = IngredientLocation.Fridge, type = IngredientType.FRUIT),

    // BAKING & SNACKS (Order: 6) - Combines Grains and Snacks
    IngredientItem(IngredientIcon.BREAD, "Bread", "g", 1.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.RICE, "Rice", "g", 100.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.SPAGHETTI, "Spaghetti", "g", 100.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.FUSILLI, "Fusilli", "g", 100.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.MACARONI, "Macaroni", "g", 100.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.NOODLE, "Noodle", "g", 100.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.RAMEN, "Ramen", "g", 1.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.OAT, "Oat", "g", 50.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.CEREALS, "Cereals", "g", 50.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.BUNS, "Buns", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.TORTILLA, "Tortilla", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.DUMPLING, "Dumpling", "pcs", 1.0, location = IngredientLocation.Freezer, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.TACO, "Taco", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.CHOCOLATE_BAR, "Chocolate Bar", "g", 1.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.COOKIE, "Cookie", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.BISCUIT, "Biscuit", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.PRETZEL, "Pretzel", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.POPCORN, "Popcorn", "g", 50.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.ALMOND, "Almond", "g", 25.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.HAZELNUT, "Hazelnut", "g", 25.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.PEANUTS, "Peanuts", "g", 25.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.COCONUT, "Coconut", "pcs", 1.0, location = IngredientLocation.Pantry, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.PANCAKES, "Pancakes", "pcs", 1.0, location = IngredientLocation.Freezer, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.FALAFEL, "Falafel", "pcs", 1.0, location = IngredientLocation.Freezer, type = IngredientType.BAKING_SNACKS),
    IngredientItem(IngredientIcon.PUDDING, "Pudding", "ml", 1.0, location = IngredientLocation.Fridge, type = IngredientType.BAKING_SNACKS),

    // CONDIMENTS (Order: 7) - Combines Sauces, Spices, and traditional Condiments
    IngredientItem(IngredientIcon.CINNAMON, "Cinnamon", "tsp", 0.25, location = IngredientLocation.Pantry, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.VANILLA, "Vanilla", "tsp", 0.25, location = IngredientLocation.Pantry, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.SEASONING, "Seasoning", "tsp", 0.25, location = IngredientLocation.Pantry, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.YEAST, "Yeast", "tsp", 0.25, location = IngredientLocation.Fridge, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.MAYONNAISE, "Mayonnaise", "ml", 1.0, location = IngredientLocation.Fridge, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.KETCHUP, "Ketchup", "ml", 1.0, location = IngredientLocation.Fridge, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.MUSTARD, "Mustard", "ml", 1.0, location = IngredientLocation.Fridge, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.CHILI_SAUCE, "Chili Sauce", "ml", 25.0, location = IngredientLocation.Fridge, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.SOY, "Soy", "ml", 25.0, location = IngredientLocation.Fridge, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.VINEGAR, "Vinegar", "ml", 1.0, location = IngredientLocation.Pantry, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.HONEY, "Honey", "ml", 1.0, location = IngredientLocation.Pantry, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.JAM, "Jam", "ml", 1.0, location = IngredientLocation.Fridge, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.PEANUT_BUTTER, "Peanut Butter", "g", 1.0, location = IngredientLocation.Pantry, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.OLIVES, "Olives", "pcs", 5.0, location = IngredientLocation.Fridge, type = IngredientType.CONDIMENTS),
    IngredientItem(IngredientIcon.STARCH, "Starch", "g", 1.0, location = IngredientLocation.Pantry, type = IngredientType.CONDIMENTS),

    // LIQUIDS (Order: 8) - Renamed from Beverages
    IngredientItem(IngredientIcon.APPLE_CIDER, "Apple Cider", "ml", 250.0, location = IngredientLocation.Fridge, type = IngredientType.LIQUIDS),
    IngredientItem(IngredientIcon.WINE, "Wine", "ml", 100.0, location = IngredientLocation.Pantry, type = IngredientType.LIQUIDS),
    IngredientItem(IngredientIcon.ALCOHOL, "Alcohol", "ml", 50.0, location = IngredientLocation.Pantry, type = IngredientType.LIQUIDS),
    IngredientItem(IngredientIcon.COGNAC, "Cognac", "ml", 50.0, location = IngredientLocation.Pantry, type = IngredientType.LIQUIDS),
    IngredientItem(IngredientIcon.COFFEE_BEANS, "Coffee Beans", "g", 25.0, location = IngredientLocation.Pantry, type = IngredientType.LIQUIDS),
    IngredientItem(IngredientIcon.LEMON_JUICE, "Lemon Juice", "ml", 25.0, location = IngredientLocation.Fridge, type = IngredientType.LIQUIDS)
)

fun Double.formatQuantity(): String {
    return when {
        this == 0.0 -> "0"
        this % 1.0 == 0.0 -> this.toInt().toString()
        else -> {
            // Multiply by 10, round, and then divide by 10 to get one decimal place
            val rounded = round(this * 10) / 10
            // Convert to string and remove unnecessary trailing zeros and a trailing dot
            val str = rounded.toString()
            if (str.contains('.')) {
                str.trimEnd('0').trimEnd('.')
            } else {
                str
            }
        }
    }
}