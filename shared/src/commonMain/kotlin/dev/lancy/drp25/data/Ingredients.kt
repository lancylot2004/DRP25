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
enum class Ingredients(
    val displayName: String,
) {
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
    CREAM("Heavy Cream"),
    ;

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
        else -> error("Unknown unit.")
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
