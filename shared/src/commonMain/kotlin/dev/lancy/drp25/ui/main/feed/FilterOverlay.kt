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
fun ColumnScope.FilterContent(filterValues: FilterValues, updateCallback: (FilterValues) -> Unit) {
    LaunchedEffect(filterValues) {
        updateCallback(filterValues)
    }

    Text(
        "Filters",
        style = Typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Size.Padding)
            .align(Alignment.CenterHorizontally),
        textAlign = TextAlign.Center,
    )

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
        ) { updateCallback(filterValues.copy(timeRange = it)) }

        StarRatingSection(
            title = "Minimum rating",
            rating = filterValues.rating,
        ) { updateCallback(filterValues.copy(rating = it)) }

        ChipSelectionSection(
            "Type of meal",
            MealType.entries,
            filterValues.selectedMealTypes,
        ) { updateCallback(filterValues.copy(selectedMealTypes = it)) }

        ChipSelectionSection(
            "Cuisine",
            Cuisine.entries,
            filterValues.selectedCuisines,
        ) { updateCallback(filterValues.copy(selectedCuisines = it)) }

        ChipSelectionSection(
            "Dietary needs",
            Diet.entries,
            filterValues.selectedDiets,
        ) { updateCallback(filterValues.copy(selectedDiets = it)) }

        BinarySection(
            title = "Use only my equipment",
            value = filterValues.useMyEquipmentOnly,
            onValueChange = { updateCallback(filterValues.copy(useMyEquipmentOnly = it)) },
        )

        SliderSection(
            "Calories",
            filterValues.calorieRange,
            FilterRanges.CALORIE_RANGE,
            { formatRange(it, FilterRanges.CALORIE_RANGE, "cal", "any calories") },
        ) { updateCallback(filterValues.copy(calorieRange = it)) }

        SliderSection(
            "Protein",
            filterValues.proteinRange,
            FilterRanges.PROTEIN_RANGE,
            { formatRange(it, FilterRanges.PROTEIN_RANGE, "g", "any protein content") },
        ) { updateCallback(filterValues.copy(proteinRange = it)) }

        SliderSection(
            "Fat",
            filterValues.fatRange,
            FilterRanges.FAT_RANGE,
            { formatRange(it, FilterRanges.FAT_RANGE, "g", "any fat content") },
        ) { updateCallback(filterValues.copy(fatRange = it)) }

        SliderSection(
            "Carbohydrates",
            filterValues.carbsRange,
            FilterRanges.CARBS_RANGE,
            { formatRange(it, FilterRanges.CARBS_RANGE, "g", "any carb content") },
        ) { updateCallback(filterValues.copy(carbsRange = it)) }
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

        // Could separate onValueChange with onValueChangeFinished to optimise.

        RangeSlider(
            value,
            onValueChange = { onChange(it) },
            modifier = Modifier.fillMaxWidth(),
            valueRange = range,
            steps = FilterRanges.TIME_RANGE.intSteps(),
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
        end == range.endInclusive -> "At least ${start.toInt()} min"
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
                    onClick = { onSelectionChange(if (selected) selection - item else selection + item) },
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
                // Empty background star
                Icon(
                    imageVector = Lucide.Star,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.fillMaxSize(),
                )

                // Filled foreground star
                Icon(
                    imageVector = Lucide.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier
                        .fillMaxSize()
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
