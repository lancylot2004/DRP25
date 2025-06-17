package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.CircleX
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import dev.lancy.drp25.data.*
import dev.lancy.drp25.utilities.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.FilterContent(filtersManager: PersistenceManager<FilterValues>) {
    Text(
        "Filters",
        style = Typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Size.Padding)
            .align(Alignment.CenterHorizontally),
        textAlign = TextAlign.Center,
    )

    val filterValues by filtersManager.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Size.Padding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Size.BigPadding),
    ) {
        SliderSection(
            title = "Cook Time",
            value = filterValues.timeRange,
            range = FilterRanges.TIME_RANGE,
            format = { formatRange(it, FilterRanges.TIME_RANGE, "min", "any duration") },
        ) { newRange ->
            filtersManager.update { this.copy(timeRange = newRange) }
        }

        StarRatingSection(
            title = "Minimum rating",
            rating = filterValues.rating,
        ) { newRating ->
            filtersManager.update { this.copy(rating = newRating) }
        }

        ChipSelectionSection(
            "Type of meal",
            MealType.entries,
            filterValues.selectedMealTypes,
        ) { newSelection ->
            filtersManager.update { this.copy(selectedMealTypes = newSelection) }
        }

        ChipSelectionSection(
            "Cuisine",
            Cuisine.entries,
            filterValues.selectedCuisines,
        ) { newSelection ->
            filtersManager.update { this.copy(selectedCuisines = newSelection) }
        }

        ChipSelectionSection(
            "Dietary needs",
            Diet.entries,
            filterValues.selectedDiets,
        ) { newSelection ->
            filtersManager.update { this.copy(selectedDiets = newSelection) }
        }

        BinarySection(
            title = "Use only my equipment",
            value = filterValues.useMyEquipmentOnly,
            onValueChange = { newValue ->
                filtersManager.update { this.copy(useMyEquipmentOnly = newValue) }
            },
        )

        SliderSection(
            "Calories",
            filterValues.calorieRange,
            FilterRanges.CALORIE_RANGE,
            { formatRange(it, FilterRanges.CALORIE_RANGE, "cal", "any calories") },
        ) { newRange ->
            filtersManager.update { this.copy(calorieRange = newRange) }
        }

        SliderSection(
            "Protein",
            filterValues.proteinRange,
            FilterRanges.PROTEIN_RANGE,
            { formatRange(it, FilterRanges.PROTEIN_RANGE, "g", "any protein content") },
        ) { newRange ->
            filtersManager.update { this.copy(proteinRange = newRange) }
        }

        SliderSection(
            "Fat",
            filterValues.fatRange,
            FilterRanges.FAT_RANGE,
            { formatRange(it, FilterRanges.FAT_RANGE, "g", "any fat content") },
        ) { newRange ->
            filtersManager.update { this.copy(fatRange = newRange) }
        }

        SliderSection(
            "Carbohydrates",
            filterValues.carbsRange,
            FilterRanges.CARBS_RANGE,
            { formatRange(it, FilterRanges.CARBS_RANGE, "g", "any carb content") },
        ) { newRange ->
            filtersManager.update { this.copy(carbsRange = newRange) }
        }
    }
}

@Composable
private fun SliderSection(
    title: String,
    value: ClosedFloatingPointRange<Float>,
    range: ClosedFloatingPointRange<Float>,
    format: (ClosedFloatingPointRange<Float>) -> String,
    onChange: (ClosedFloatingPointRange<Float>) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(title, style = Typography.titleSmall)

            Text(format(value), style = Typography.titleSmall, color = ColourScheme.onBackground.copy(alpha = 0.7f))
        }

        RangeSlider(
            value,
            onValueChange = { onChange(it) },
            modifier = Modifier.fillMaxWidth(),
            valueRange = range,
            steps = (range.endInclusive - range.start).toInt().coerceAtLeast(0)
        )
    }
}

private fun formatRange(
    value: ClosedFloatingPointRange<Float>,
    range: ClosedFloatingPointRange<Float>,
    unit: String,
    anyMessage: String,
): String {
    val start = value.start
    val end = value.endInclusive

    return when {
        start == end -> "${start.toInt()} $unit"
        start == range.start && end == range.endInclusive -> anyMessage
        start == range.start -> "Up to ${end.toInt()} $unit"
        end == range.endInclusive -> "At least ${start.toInt()} $unit"
        else -> "${start.toInt()} to ${end.toInt()} $unit"
    }
}

@Composable
private fun <T> ChipSelectionSection(
    title: String,
    items: List<T>,
    selection: Set<T>,
    onSelectionChange: (Set<T>) -> Unit,
) {
    Column {
        Text(title, style = Typography.titleSmall)

        FlowRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(Size.Spacing),
            verticalArrangement = Arrangement.spacedBy(Size.Spacing),
        ) {
            items.forEach { item ->
                val selected = item in selection

                FilterChip(
                    selected = selected,
                    onClick = {
                        val newSelection = if (selected) selection - item else selection + item
                        onSelectionChange(newSelection)
                    },
                    label = { Text(item.toString(), style = Typography.bodyMedium) },
                    shape = Shape.RoundedMedium,
                )
            }
        }
    }
}

@Composable
private fun BinarySection(
    title: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = Typography.titleSmall)

        Switch(
            checked = value,
            onCheckedChange = onValueChange,
        )
    }
}

@Composable
private fun StarRatingSection(
    title: String,
    rating: Float,
    onRatingChanged: (Float) -> Unit,
) {
    Row(Modifier.fillMaxWidth()) {
        Text(title, style = Typography.titleSmall)

        Spacer(Modifier.weight(1f))

        StarRatingSelector(
            rating = rating,
            onRatingChanged = onRatingChanged,
        )

        Spacer(Modifier.width(Size.Spacing))

        IconButton(
            onClick = { onRatingChanged(0f) },
            modifier = Modifier.size(Size.IconSmall),
        ) {
            Icon(
                imageVector = Lucide.CircleX,
                contentDescription = "Clear rating",
                tint = Color.LightGray,
            )
        }
    }
}

@Composable
private fun StarRatingSelector(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
) {
    val clamped = rating.coerceIn(0f, 5f)

    Row(horizontalArrangement = Arrangement.spacedBy(Size.Spacing)) {
        repeat(5) { index ->
            val targetFill = (clamped - index).coerceIn(0f, 1f)
            val animatedFill by animateFloatAsState(
                targetValue = targetFill,
                label = "starFillAnimation",
            )

            Box(
                modifier = Modifier
                    .size(Size.IconSmall)
                    .pointerInput(Unit) {
                        detectTapGestures { onRatingChanged(index + 1f) }
                    },
            ) {
                Icon(
                    imageVector = Lucide.Star,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.fillMaxSize(),
                )

                Icon(
                    imageVector = Lucide.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.fillMaxSize()
                        .drawWithContent {
                            clipRect(right = size.width * animatedFill) {
                                this@drawWithContent.drawContent()
                            }
                        },
                )
            }
        }
    }
}