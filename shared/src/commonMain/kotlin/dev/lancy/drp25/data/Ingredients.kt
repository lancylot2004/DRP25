package dev.lancy.drp25.data

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable
data class Ingredient(
    val name: String,
    val quantity: Double,
    val unit: Unit,
    val system: MeasurementSystem = MeasurementSystem.METRIC
)

enum class MeasurementSystem { METRIC, IMPERIAL }

@Serializable(with = UnitSerializer::class)
sealed class Unit(val type: UnitType) {
    enum class UnitType { WEIGHT, VOLUME, COUNT, OTHER }

    data object Gram : Unit(UnitType.WEIGHT)
    data object Kilogram : Unit(UnitType.WEIGHT)
    data object Ounce : Unit(UnitType.WEIGHT)
    data object Pound : Unit(UnitType.WEIGHT)

    data object Milliliter : Unit(UnitType.VOLUME)
    data object Liter : Unit(UnitType.VOLUME)
    data object Teaspoon : Unit(UnitType.VOLUME)
    data object Tablespoon : Unit(UnitType.VOLUME)
    data object Cup : Unit(UnitType.VOLUME)

    data object Piece : Unit(UnitType.COUNT)
    data object Slice : Unit(UnitType.COUNT)
    data object Pinch : Unit(UnitType.OTHER)
    data object Dash : Unit(UnitType.OTHER)

    data class Other(val name: String) : Unit(UnitType.OTHER)
}

object UnitSerializer : KSerializer<Unit> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Unit", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Unit {
        return when (val name = decoder.decodeString()) {
            "Gram" -> Unit.Gram
            "Kilogram" -> Unit.Kilogram
            "Ounce" -> Unit.Ounce
            "Pound" -> Unit.Pound
            "Milliliter" -> Unit.Milliliter
            "Liter" -> Unit.Liter
            "Teaspoon" -> Unit.Teaspoon
            "Tablespoon" -> Unit.Tablespoon
            "Cup" -> Unit.Cup
            "Piece" -> Unit.Piece
            "Slice" -> Unit.Slice
            "Pinch" -> Unit.Pinch
            "Dash" -> Unit.Dash
            else -> Unit.Other(name)
        }
    }

    override fun serialize(encoder: Encoder, value: Unit) {
        val name = when (value) {
            is Unit.Gram -> "Gram"
            is Unit.Kilogram -> "Kilogram"
            is Unit.Ounce -> "Ounce"
            is Unit.Pound -> "Pound"
            is Unit.Milliliter -> "Milliliter"
            is Unit.Liter -> "Liter"
            is Unit.Teaspoon -> "Teaspoon"
            is Unit.Tablespoon -> "Tablespoon"
            is Unit.Cup -> "Cup"
            is Unit.Piece -> "Piece"
            is Unit.Slice -> "Slice"
            is Unit.Pinch -> "Pinch"
            is Unit.Dash -> "Dash"
            is Unit.Other -> value.name
        }
        encoder.encodeString(name)
    }
}


object UnitConverter {
    private val weightToGram = mapOf(
        Unit.Gram to 1.0,
        Unit.Kilogram to 1000.0,
        Unit.Ounce to 28.3495,
        Unit.Pound to 453.592
    )

    private val volumeToMilliliter = mapOf(
        Unit.Milliliter to 1.0,
        Unit.Liter to 1000.0,
        Unit.Teaspoon to 4.92892,
        Unit.Tablespoon to 14.7868,
        Unit.Cup to 240.0
    )

    fun convert(quantity: Double, fromUnit: Unit, toUnit: Unit): Double {
        if (fromUnit::class != toUnit::class || fromUnit.type != toUnit.type) {
            throw IllegalArgumentException("Cannot convert between different unit types")
        }

        return when (fromUnit.type) {
            Unit.UnitType.WEIGHT -> {
                val qtyInGrams = quantity * (weightToGram[fromUnit] ?: error("Unknown fromUnit"))
                qtyInGrams / (weightToGram[toUnit] ?: error("Unknown toUnit"))
            }
            Unit.UnitType.VOLUME -> {
                val qtyInMl = quantity * (volumeToMilliliter[fromUnit] ?: error("Unknown fromUnit"))
                qtyInMl / (volumeToMilliliter[toUnit] ?: error("Unknown toUnit"))
            }
            else -> quantity
        }
    }
}