package dev.lancy.drp25.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

// Filter data class
data class FilterValues(
    val timeRange: ClosedFloatingPointRange<Float> = FilterRanges.TIME_DEFAULT,
    val rating: Float = 3.0f,
    val selectedMealTypes: Set<MealType> = emptySet(),
    val selectedCuisines: Set<Cuisine> = emptySet(),
    val selectedDiets: Set<Diet> = emptySet(),
    val includedIngredients: Set<Ingredients> = emptySet(),
    val avoidedIngredients: Set<Ingredients> = emptySet(),
    val useMyEquipmentOnly: Boolean = true,
    val calorieRange: ClosedFloatingPointRange<Float> = FilterRanges.CALORIE_DEFAULT,
    val proteinRange: ClosedFloatingPointRange<Float> = FilterRanges.PROTEIN_DEFAULT,
    val fatRange: ClosedFloatingPointRange<Float> = FilterRanges.FAT_DEFAULT,
    val carbsRange: ClosedFloatingPointRange<Float> = FilterRanges.CARBS_DEFAULT
)

// Filter ranges
object FilterRanges {
    // Time in minutes
    val TIME_RANGE = 5f..180f
    val TIME_DEFAULT = 15f..60f

    // Rating from 0.0-5.0 stars with 0.1 precision
    val RATING_RANGE = 0.0f..5.0f
    val RATING_STEP = 0.1f
    val RATING_STEPS = ((RATING_RANGE.endInclusive - RATING_RANGE.start) / RATING_STEP).toInt() - 1

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

// Manage filter state persistence across app sessions
object FilterStateManager {
    // Persistent filter state
    var savedFilters by mutableStateOf(FilterValues())
        private set

    // Current working filters (used while editing)
    var currentFilters by mutableStateOf(FilterValues())
        private set

    // Update current filters while editing
    fun updateCurrentFilters(filters: FilterValues) {
        currentFilters = filters
    }

    // Save current filters as the new default
    fun saveFilters() {
        savedFilters = currentFilters
    }

    // Reset all filters to default values
    fun resetToDefaults() {
        currentFilters = FilterValues()
        savedFilters = FilterValues()
    }

    // Initialize filters (call this when app starts)
    fun initialize() {
        if (savedFilters == FilterValues()) {
            resetToDefaults()
        } else {
            currentFilters = savedFilters
        }
    }
}

// Filter formatters
object FilterFormatters {
    fun formatTime(minutes: Float): String {
        return when {
            minutes < 60f -> "${minutes.toInt()} min"
            minutes == 60f -> "1 hour"
            minutes < 120f -> "1h ${(minutes - 60).toInt()}min"
            else -> "${(minutes / 60).toInt()}h ${(minutes % 60).toInt()}m"
        }
    }

    fun formatRating(rating: Float): String {
        if (rating == 0f) return "Any rating"
        val rounded = (rating * 10).roundToInt() / 10f
        val integerPart = rounded.toInt()
        val fractionalDigit = ((rounded * 10).roundToInt() % 10)
        val oneDecimal = "$integerPart.$fractionalDigit"

        return when {
            rounded == 5.0f -> "5.0 stars only"
            else            -> "$oneDecimal+ stars"
        }
    }
}