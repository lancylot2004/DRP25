package dev.lancy.drp25.data

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import dev.lancy.drp25.utilities.settings
import com.russhwolf.settings.serialization.decodeValueOrNull

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun getPreferredSystem(): MeasurementSystem =
    settings.decodeValueOrNull<MeasurementSystem>("measurement_system") ?: MeasurementSystem.METRIC

// INGREDIENTS
@Serializable
enum class Ingredients(val displayName: String) {
    ALCOHOL("Alcohol"),
    ALMOND("Almond"),
    ALMOND_MILK("Almond Milk"),
    APPLE("Apple"),
    APPLE_CIDER("Apple Cider"),
    AVOCADO("Avocado"),
    BACON("Bacon"),
    BAKING_SODA("Baking Soda"),
    BANANA("Banana"),
    BEEF_MEAT("Beef Meat"),
    BELL_PEPPER("Bell Pepper"),
    BISCUIT("Biscuit"),
    BLUEBERRIES("Blueberries"),
    BREAD("Bread"),
    BROCCOLI("Broccoli"),
    BUNS("Buns"),
    BUTTER("Butter"),
    CABBAGE("Cabbage"),
    CANNED_TUNA("Canned Tuna"),
    CARROT("Carrot"),
    CAULIFLOWER("Cauliflower"),
    CEREALS("Cereals"),
    CHEDDAR_CHEESE("Cheddar Cheese"),
    CHEESE_SLICES("Cheese Slices"),
    CHERRIES("Cherries"),
    CHERRY_TOMATO("Cherry Tomato"),
    CHICKEN_MEAT("Chicken Meat"),
    CHILI("Chili"),
    CHILI_SAUCE("Chili Sauce"),
    CHOCOLATE_BAR("Chocolate Bar"),
    CINNAMON("Cinnamon"),
    COCONUT("Coconut"),
    COFFEE_BEANS("Coffee Beans"),
    COGNAC("Cognac"),
    COOKIE("Cookie"),
    CORN("Corn"),
    CUCUMBER("Cucumber"),
    DOUBLE_CREAM("Double Cream"),
    DUCK_MEAT("Duck Meat"),
    DUMPLING("Dumpling"),
    EGG("Egg"),
    EGGPLANT("Eggplant"),
    FALAFEL("Falafel"),
    FISH("Fish"),
    FUSILI("Fusili"),
    GARLIC("Garlic"),
    GINGER("Ginger"),
    GRAPES("Grapes"),
    GREEN_SALAD("Green Salad"),
    HAM("Ham"),
    HAZELNUT("Hazelnut"),
    HONEY("Honey"),
    ICE_CREAM("Ice Cream"),
    JAM("Jam"),
    KETCHUP("Ketchup"),
    KIDNEY_BEANS("Kidney Beans"),
    KIWI("Kiwi"),
    LAMB("Lamb"),
    LEAFY_GREEN("Leafy Green"),
    LEMON("Lemon"),
    LEMON_JUICE("Lemon Juice"),
    LIME("Lime"),
    LOBSTER("Lobster"),
    MACARONI("Macaroni"),
    MANGO("Mango"),
    MAYONNAISE("Mayonnaise"),
    MEATBALLS("Meatballs"),
    MEAT_BURGER("Meat Burger"),
    MELON("Melon"),
    MILK("Milk"),
    MUSTARD("Mustard"),
    NOODLE("Noodle"),
    OAT("Oat"),
    OIL("Oil"),
    OLIVES("Olives"),
    ONION("Onion"),
    ORANGE("Orange"),
    OYSTER("Oyster"),
    PANCAKES("Pancakes"),
    PARMESAN_CHEESE("Parmesan Cheese"),
    PARSLEY("Parsley"),
    PEACH("Peach"),
    PEANUTS("Peanuts"),
    PEANUT_BUTTER("Peanut Butter"),
    PEAR("Pear"),
    PEAS("Peas"),
    PEPPER_BLACK("Black Pepper"),
    PINEAPPLE("Pineapple"),
    POPCORN("Popcorn"),
    PORK_MEAT("Pork Meat"),
    POTATO("Potato"),
    POULTRY_LEG("Poultry Leg"),
    PRETZEL("Pretzel"),
    PUDDING("Pudding"),
    PUMPKIN("Pumpkin"),
    RADISH("Radish"),
    RAMEN("Ramen"),
    RASPBERRY("Raspberry"),
    RICE("Rice"),
    SALAMI("Salami"),
    SALMON("Salmon"),
    SALT("Salt"),
    SAUSAGE("Sausage"),
    SEASONING("Seasoning"),
    SOYA("Soya"),
    SPAGHETTI("Spaghetti"),
    SPINACH("Spinach"),
    STARCH("Starch"),
    STEAK("Steak"),
    STRAWBERRY("Strawberry"),
    SUGAR_BROWN("Brown Sugar"),
    SUGAR_WHITE("White Sugar"),
    SWEET_POTATO("Sweet Potato"),
    TACO("Taco"),
    TOMATO("Tomato"),
    TORTILLA("Tortilla"),
    TURKEY_MEAT("Turkey Meat"),
    VANILLA("Vanilla"),
    VINEGAR("Vinegar"),
    WATERMELON("Watermelon"),
    WHEAT_FLOUR("Wheat Flour"),
    WINE("Wine"),
    YEAST("Yeast"),
    YOGURT("Yogurt");

    override fun toString() = displayName
}

@Serializable
@Parcelize
data class Ingredient(
    val name: String,
    val quantity: Double,
    val unit: IngredientUnit,
) : Parcelable

enum class MeasurementSystem { METRIC, IMPERIAL }

@Serializable(with = IngredientUnitSerialiser::class)
@Parcelize
sealed class IngredientUnit(
    open val displayName: String,
    open val shortName: String,
) : Parcelable {
    fun conversionFactorTo(other: IngredientUnit): Double = when {
        this is WeightUnit && other is WeightUnit -> this.gramFactor / other.gramFactor
        this is VolumeUnit && other is VolumeUnit -> this.mlFactor / other.mlFactor
        this::class == other::class -> 1.0
        else -> error("Incompatible unit types: ${this::class} and ${other::class}")
    }

    @Parcelize
    sealed class WeightUnit(
        override val displayName: String,
        override val shortName: String,
        val gramFactor: Double,
    ) : IngredientUnit(displayName, shortName) {
        data object Gram : WeightUnit("Gram", "g", 1.0)

        data object Kilogram : WeightUnit("Kilogram", "kg", 1000.0)

        data object Ounce : WeightUnit("Ounce", "oz", 28.3495)

        data object Pound : WeightUnit("Pound", "lb", 453.592)
    }

    @Parcelize
    sealed class VolumeUnit(
        override val displayName: String,
        override val shortName: String,
        val mlFactor: Double,
    ) : IngredientUnit(displayName, shortName) {
        data object Milliliter : VolumeUnit("Milliliter", "ml", 1.0)

        data object Liter : VolumeUnit("Liter", "L", 1000.0)

        data object Teaspoon : VolumeUnit("Teaspoon", "tsp", 4.92892)

        data object Tablespoon : VolumeUnit("Tablespoon", "tbsp", 14.7868)

        data object Cup : VolumeUnit("Cup", "cup", 240.0)
    }

    @Parcelize
    sealed class CountUnit(
        override val displayName: String,
        override val shortName: String,
    ) : IngredientUnit(displayName, shortName) {
        data object Piece : CountUnit("Piece", "")

        data object Slice : CountUnit("Slice", "slice")

        data object Pinch : CountUnit("Pinch", "pinch")

        data object Dash : CountUnit("Dash", "dash")
    }
}

object IngredientUnitSerialiser : KSerializer<IngredientUnit> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Ingredient", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): IngredientUnit = when (decoder.decodeString().lowercase()) {
        "gram" -> IngredientUnit.WeightUnit.Gram
        "kilogram" -> IngredientUnit.WeightUnit.Kilogram
        "ounce" -> IngredientUnit.WeightUnit.Ounce
        "pound" -> IngredientUnit.WeightUnit.Pound
        "milliliter" -> IngredientUnit.VolumeUnit.Milliliter
        "liter" -> IngredientUnit.VolumeUnit.Liter
        "teaspoon" -> IngredientUnit.VolumeUnit.Teaspoon
        "tablespoon" -> IngredientUnit.VolumeUnit.Tablespoon
        "cup" -> IngredientUnit.VolumeUnit.Cup
        "piece" -> IngredientUnit.CountUnit.Piece
        "slice" -> IngredientUnit.CountUnit.Slice
        "pinch" -> IngredientUnit.CountUnit.Pinch
        "dash" -> IngredientUnit.CountUnit.Dash
        else -> IngredientUnit.CountUnit.Piece  //error("Unknown unit.")
    }

    override fun serialize(
        encoder: Encoder,
        value: IngredientUnit,
    ) = encoder.encodeString(value.shortName)
}

fun Double.convert(from: IngredientUnit, to: IngredientUnit): Double =
    this * from.conversionFactorTo(to)

fun Int.convert(from: IngredientUnit, to: IngredientUnit): Double =
    toDouble().convert(from, to)

private fun getMeasurementSystem(unit: IngredientUnit): MeasurementSystem = when (unit) {
    is IngredientUnit.WeightUnit.Gram,
    is IngredientUnit.WeightUnit.Kilogram -> MeasurementSystem.METRIC
    is IngredientUnit.WeightUnit.Ounce,
    is IngredientUnit.WeightUnit.Pound -> MeasurementSystem.IMPERIAL
    else -> MeasurementSystem.METRIC
}

private fun preferredUnitFor(system: MeasurementSystem, fromUnit: IngredientUnit): IngredientUnit = when (system) {
    MeasurementSystem.METRIC -> when (fromUnit) {
        IngredientUnit.WeightUnit.Ounce -> IngredientUnit.WeightUnit.Gram
        IngredientUnit.WeightUnit.Pound -> IngredientUnit.WeightUnit.Kilogram
        else -> fromUnit
    }
    MeasurementSystem.IMPERIAL -> when (fromUnit) {
        IngredientUnit.WeightUnit.Gram -> IngredientUnit.WeightUnit.Ounce
        IngredientUnit.WeightUnit.Kilogram -> IngredientUnit.WeightUnit.Pound
        else -> fromUnit
    }
}

fun formatIngredientDisplay(ingredient: Ingredient): String {
    val preferredSystem = getPreferredSystem()
    val currentSystem = getMeasurementSystem(ingredient.unit)
    val displayUnit = if (currentSystem == preferredSystem) {
        ingredient.unit
    } else {
        preferredUnitFor(preferredSystem, ingredient.unit)
    }
    val displayQuantity = if (ingredient.unit == displayUnit) {
        ingredient.quantity
    } else {
        ingredient.quantity.convert(ingredient.unit, displayUnit)
    }
    val quantityFormatted = formatDouble(displayQuantity)
    val unitLabel = displayUnit.shortName

    return if (unitLabel.isNotEmpty()) {
        "$quantityFormatted $unitLabel ${ingredient.name}"
    } else {
        "$quantityFormatted ${ingredient.name}"
    }
}

fun formatDouble(quantity: Double): String = when {
    quantity % 1.0 == 0.0 -> quantity.toInt().toString()
    else -> {
        val s = quantity.toString()
        val i = s.indexOf('.')
        if (i == -1) {
            s
        } else {
            s
                .substring(0, (i + 3).coerceAtMost(s.length))
                .trimEnd('0')
                .trimEnd('.')
        }
    }
}
