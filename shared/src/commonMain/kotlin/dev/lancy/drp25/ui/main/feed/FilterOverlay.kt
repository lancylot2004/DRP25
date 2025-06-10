package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.*
import dev.lancy.drp25.data.*
import dev.lancy.drp25.data.FilterFormatters.formatTime
import dev.lancy.drp25.utilities.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContent(updateCallback: (FilterValues) -> Unit) {
    var filterValues by remember { mutableStateOf(FilterValues()) }
    LaunchedEffect(filterValues) {
        updateCallback(filterValues)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Size.Padding, bottom = Size.BarLarge, start = Size.Padding, end = Size.Padding),
    ) {
        Text("Filters", style = Typography.titleMedium, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            SliderSection(
                "Time",
                filterValues.timeRange,
                FilterRanges.TIME_RANGE,
                { "${formatTime(it.start)} - ${formatTime(it.endInclusive)}" },
            ) {
                filterValues = filterValues.copy(timeRange = it)
            }

            SingleSliderSection(
                "Minimum Rating",
                filterValues.rating,
                FilterRanges.RATING_RANGE,
                FilterRanges.RATING_STEPS,
                FilterFormatters::formatRating,
            ) {
                filterValues = filterValues.copy(rating = it)
            }

            ChipSelectionSection(
                "Type of meal",
                MealType.entries,
                filterValues.selectedMealTypes,
                { it.displayName },
                Color(0xFF2196F3),
            ) {
                filterValues = filterValues.copy(selectedMealTypes = it)
            }

            ChipSelectionSection(
                "Cuisine",
                Cuisine.entries,
                filterValues.selectedCuisines,
                { it.displayName },
                Color(0xFF9C27B0),
            ) {
                filterValues = filterValues.copy(selectedCuisines = it)
            }

            ChipSelectionSection(
                "Dietary needs",
                Diet.entries,
                filterValues.selectedDiets,
                { it.displayName },
                Color(0xFF4CAF50),
            ) {
                filterValues = filterValues.copy(selectedDiets = it)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Only use my equipment", style = Typography.bodyMedium)
                Switch(
                    checked = filterValues.useMyEquipmentOnly,
                    onCheckedChange = { filterValues = filterValues.copy(useMyEquipmentOnly = it) },
                )
            }

            SliderSection(
                "Calories",
                filterValues.calorieRange,
                FilterRanges.CALORIE_RANGE,
                { "${it.start.toInt()} - ${it.endInclusive.toInt()} cal" },
            ) {
                filterValues = filterValues.copy(calorieRange = it)
            }

            SliderSection(
                "Protein",
                filterValues.proteinRange,
                FilterRanges.PROTEIN_RANGE,
                { "${it.start.toInt()} - ${it.endInclusive.toInt()}g" },
            ) {
                filterValues = filterValues.copy(proteinRange = it)
            }

            SliderSection(
                "Fat",
                filterValues.fatRange,
                FilterRanges.FAT_RANGE,
                { "${it.start.toInt()} - ${it.endInclusive.toInt()}g" },
            ) {
                filterValues = filterValues.copy(fatRange = it)
            }

            SliderSection(
                "Carbohydrates",
                filterValues.carbsRange,
                FilterRanges.CARBS_RANGE,
                { "${it.start.toInt()} - ${it.endInclusive.toInt()}g" },
            ) {
                filterValues = filterValues.copy(carbsRange = it)
            }
        }
    }
}

@Composable
private fun SliderSection(
    title: String,
    value: ClosedFloatingPointRange<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    formatValue: (ClosedFloatingPointRange<Float>) -> String,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
) {
    Text(title, style = Typography.bodyMedium, modifier = Modifier.padding(top = 16.dp))
    Column {
        RangeSlider(value = value, onValueChange = onValueChange, valueRange = valueRange, modifier = Modifier.fillMaxWidth())
        Text(formatValue(value), style = Typography.bodySmall, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun SingleSliderSection(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    formatValue: (Float) -> String,
    onValueChange: (Float) -> Unit,
) {
    Text(title, style = Typography.bodyMedium, modifier = Modifier.padding(top = 16.dp))
    Column {
        Slider(value = value, onValueChange = onValueChange, valueRange = valueRange, steps = steps, modifier = Modifier.fillMaxWidth())
        Text(formatValue(value), style = Typography.bodySmall, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun <T> ChipSelectionSection(
    title: String,
    items: List<T>,
    selectedItems: Set<T>,
    getDisplayName: (T) -> String,
    chipColor: Color,
    onSelectionChange: (Set<T>) -> Unit,
) {
    Text(title, style = Typography.bodyMedium, modifier = Modifier.padding(top = 16.dp))
    FlowRow(modifier = Modifier.padding(top = 8.dp)) {
        items.forEach { item ->
            val isSelected = selectedItems.contains(item)
            var isPressed by remember { mutableStateOf(false) }

            val animatedScale by animateFloatAsState(if (isPressed) 0.95f else 1f, tween(100))
            val animatedColor by animateColorAsState(if (isSelected) chipColor else Color.LightGray, tween(200))

            AssistChip(
                onClick = {
                    isPressed = true
                    onSelectionChange(if (isSelected) selectedItems - item else selectedItems + item)
                },
                label = { androidx.compose.material.Text(getDisplayName(item), style = Typography.bodySmall) },
                modifier = Modifier
                    .defaultMinSize(minHeight = 28.dp)
                    .padding(end = 4.dp, bottom = 4.dp)
                    .scale(animatedScale)
                    .pointerInput(Unit) { detectDragGestures(onDragEnd = { isPressed = false }) { _, _ -> } },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = animatedColor,
                    labelColor = if (isSelected) Color.White else Color.Black,
                ),
                shape = Shape.RoundedLarge,
                elevation = AssistChipDefaults.assistChipElevation(elevation = if (isSelected) 6.dp else 2.dp),
            )

            LaunchedEffect(isPressed) {
                if (isPressed) {
                    kotlinx.coroutines.delay(100)
                    isPressed = false
                }
            }
        }
    }
}
