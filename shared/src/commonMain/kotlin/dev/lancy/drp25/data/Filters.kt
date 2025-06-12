package dev.lancy.drp25.data

import dev.lancy.drp25.utilities.ClosedFloatRangeSerializer
import kotlinx.serialization.Serializable

// Filter data class
@Serializable
data class FilterValues(
    @Serializable(ClosedFloatRangeSerializer::class)
    val timeRange: ClosedFloatingPointRange<Float> = FilterRanges.TIME_DEFAULT,
    val rating: Float = 3.0f,
    val selectedMealTypes: Set<MealType> = emptySet(),
    val selectedCuisines: Set<Cuisine> = emptySet(),
    val selectedDiets: Set<Diet> = emptySet(),
    val includedIngredients: Set<Ingredients> = emptySet(),
    val avoidedIngredients: Set<Ingredients> = emptySet(),
    val useMyEquipmentOnly: Boolean = true,
    @Serializable(ClosedFloatRangeSerializer::class)
    val calorieRange: ClosedFloatingPointRange<Float> = FilterRanges.CALORIE_DEFAULT,
    @Serializable(ClosedFloatRangeSerializer::class)
    val proteinRange: ClosedFloatingPointRange<Float> = FilterRanges.PROTEIN_DEFAULT,
    @Serializable(ClosedFloatRangeSerializer::class)
    val fatRange: ClosedFloatingPointRange<Float> = FilterRanges.FAT_DEFAULT,
    @Serializable(ClosedFloatRangeSerializer::class)
    val carbsRange: ClosedFloatingPointRange<Float> = FilterRanges.CARBS_DEFAULT,
)

// Filter ranges
object FilterRanges {
    // Time in minutes
    val TIME_RANGE = 5f..180f
    val TIME_DEFAULT = 15f..60f

    // Nutrition ranges - consolidated defaults
    val CALORIE_RANGE = 50f..1500f
    val CALORIE_DEFAULT = 200f..800f

    val PROTEIN_RANGE = 0f..100f
    val PROTEIN_DEFAULT = 10f..50f

    val FAT_RANGE = 0f..80f
    val FAT_DEFAULT = 5f..30f

    val CARBS_RANGE = 0f..150f
    val CARBS_DEFAULT = 20f..80f
}
