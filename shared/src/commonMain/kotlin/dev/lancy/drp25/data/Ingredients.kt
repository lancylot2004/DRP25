package dev.lancy.drp25.data

import kotlinx.serialization.*

/* INGREDIENTS */
@Serializable
enum class Ingredients(val displayName: String) {
    SALT("Salt"),
    PEPPER("Black Pepper"),
    SUGAR("Sugar"),
    FLOUR("Flour"),
    RICE("Rice"),
    PASTA("Pasta"),
    OLIVE_OIL("Olive Oil"),
    BUTTER("Butter"),
    EGGS("Eggs"),
    MILK("Milk"),
    CHEESE("Cheese"),
    GARLIC("Garlic"),
    ONION("Onion"),
    POTATO("Potato"),
    TOMATO("Tomato"),
    CARROT("Carrot"),
    CHICKEN("Chicken"),
    BEEF("Beef"),
    FISH("Fish"),
    APPLE("Apple"),
    BANANA("Banana"),
    LEMON("Lemon"),
    LETTUCE("Lettuce"),
    PEANUT_BUTTER("Peanut Butter"),
    HONEY("Honey"),
    VANILLA("Vanilla Extract"),
    BAKING_SODA("Baking Soda"),
    YEAST("Yeast"),
    MUSTARD("Mustard"),
    KETCHUP("Ketchup"),
    SOY_SAUCE("Soy Sauce"),
    VINEGAR("Vinegar"),
    COCONUT_MILK("Coconut Milk"),
    CREAM("Heavy Cream");

    override fun toString() = displayName
}

@Serializable
data class Ingredient(
    val name: String,
    val quantity: Double,
    val unit: Unit,
    val system: MeasurementSystem = MeasurementSystem.METRIC
)

enum class MeasurementSystem { METRIC, IMPERIAL }

@Serializable
enum class Unit(val type: UnitType, val displayName: String, val shortName: String, val gramFactor: Double? = null, val mlFactor: Double? = null) {
    // Weight units
    GRAM(UnitType.WEIGHT, "Gram", "g", gramFactor = 1.0),
    KILOGRAM(UnitType.WEIGHT, "Kilogram", "kg", gramFactor = 1000.0),
    OUNCE(UnitType.WEIGHT, "Ounce", "oz", gramFactor = 28.3495),
    POUND(UnitType.WEIGHT, "Pound", "lb", gramFactor = 453.592),

    // Volume units
    MILLILITER(UnitType.VOLUME, "Milliliter", "ml", mlFactor = 1.0),
    LITER(UnitType.VOLUME, "Liter", "L", mlFactor = 1000.0),
    TEASPOON(UnitType.VOLUME, "Teaspoon", "tsp", mlFactor = 4.92892),
    TABLESPOON(UnitType.VOLUME, "Tablespoon", "tbsp", mlFactor = 14.7868),
    CUP(UnitType.VOLUME, "Cup", "cup", mlFactor = 240.0),

    // Count and other units
    PIECE(UnitType.COUNT, "Piece", ""),
    SLICE(UnitType.COUNT, "Slice", "slice"),
    PINCH(UnitType.OTHER, "Pinch", "pinch"),
    DASH(UnitType.OTHER, "Dash", "dash");

    enum class UnitType { WEIGHT, VOLUME, COUNT, OTHER }
}

object UnitConverter {
    fun convert(quantity: Double, fromUnit: Unit, toUnit: Unit): Double {
        require(fromUnit.type == toUnit.type) { "Cannot convert between different unit types" }

        return when (fromUnit.type) {
            Unit.UnitType.WEIGHT -> {
                val qtyInGrams = quantity * (fromUnit.gramFactor ?: error("Unknown fromUnit"))
                qtyInGrams / (toUnit.gramFactor ?: error("Unknown toUnit"))
            }
            Unit.UnitType.VOLUME -> {
                val qtyInMl = quantity * (fromUnit.mlFactor ?: error("Unknown fromUnit"))
                qtyInMl / (toUnit.mlFactor ?: error("Unknown toUnit"))
            }
            else -> quantity
        }
    }
}

fun formatIngredientDisplay(ingredient: Ingredient): String {
    val quantityFormatted = formatDouble(ingredient.quantity)
    val unitLabel = ingredient.unit.shortName

    return if (unitLabel.isNotEmpty()) {
        "$quantityFormatted $unitLabel ${ingredient.name.lowercase()}"
    } else {
        "$quantityFormatted ${ingredient.name.lowercase()}"
    }
}

fun formatDouble(quantity: Double): String {
    val intPart = quantity.toInt()
    return if (quantity == intPart.toDouble()) {
        intPart.toString()
    } else {
        val str = quantity.toString()
        val dotIndex = str.indexOf('.')
        if (dotIndex == -1) return str
        val endIndex = (dotIndex + 3).coerceAtMost(str.length)
        str.substring(0, endIndex).trimEnd('0').trimEnd('.')
    }
}