package dev.lancy.drp25.ui.main.feed

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.*
import dev.lancy.drp25.data.*
import dev.lancy.drp25.data.FilterFormatters.formatTime
import dev.lancy.drp25.data.Unit as DataUnit
import dev.lancy.drp25.utilities.*
import kotlin.math.roundToInt

@Composable
fun FilterNode(isVisible: Boolean, onVisibilityChange: (Boolean) -> Unit) {
    if (!isVisible) return

    val density = LocalDensity.current
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val dismissThreshold = with(density) { 120.dp.toPx() }
    val dragHandleHeight = with(density) { 60.dp.toPx() }

    val animatedOffset by animateFloatAsState(
        targetValue = if (isDragging) offsetY else 0f,
        animationSpec = tween(durationMillis = if (isDragging) 0 else 300)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .pointerInput(Unit) { detectDragGestures { _, _ -> onVisibilityChange(false) } }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset { IntOffset(0, animatedOffset.roundToInt()) }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = { if (it.y <= dragHandleHeight) { isDragging = true; offsetY = 0f } },
                        onDragEnd = {
                            if (isDragging) {
                                if (offsetY > dismissThreshold) onVisibilityChange(false) else offsetY = 0f
                                isDragging = false
                            }
                        }
                    ) { _, dragAmount ->
                        if (isDragging && dragAmount > 0) offsetY = (offsetY + dragAmount).coerceAtLeast(0f)
                    }
                },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().height(60.dp).padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (isDragging) 50.dp else 40.dp)
                            .height(if (isDragging) 5.dp else 4.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isDragging) Color.White else Color.Gray)
                    )
                }
                Box(modifier = Modifier.pointerInput(Unit) { detectVerticalDragGestures { _, _ -> } }) {
                    FilterContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContent() {
    LaunchedEffect(Unit) { FilterStateManager.saveFilters() }
    var currentFilters by remember { mutableStateOf(FilterStateManager.currentFilters) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Size.Padding, bottom = Size.BarLarge, start = Size.Padding, end = Size.Padding)
    ) {
        Text("Filters", style = Typography.titleMedium, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            SliderSection("Time", currentFilters.timeRange, FilterRanges.TIME_RANGE,
                { "${formatTime(it.start)} - ${formatTime(it.endInclusive)}" }) {
                currentFilters = currentFilters.copy(timeRange = it)
                FilterStateManager.updateCurrentFilters(currentFilters)
            }

            SingleSliderSection("Minimum Rating", currentFilters.rating, FilterRanges.RATING_RANGE,
                FilterRanges.RATING_STEPS, FilterFormatters::formatRating) {
                currentFilters = currentFilters.copy(rating = it)
                FilterStateManager.updateCurrentFilters(currentFilters)
            }

            ChipSelectionSection("Type of meal", MealType.entries, currentFilters.selectedMealTypes,
                { it.displayName }, Color(0xFF2196F3)) {
                currentFilters = currentFilters.copy(selectedMealTypes = it)
                FilterStateManager.updateCurrentFilters(currentFilters)
            }

            ChipSelectionSection("Cuisine", Cuisine.entries, currentFilters.selectedCuisines,
                { it.displayName }, Color(0xFF9C27B0)) {
                currentFilters = currentFilters.copy(selectedCuisines = it)
                FilterStateManager.updateCurrentFilters(currentFilters)
            }

            ChipSelectionSection("Dietary needs", Diet.entries, currentFilters.selectedDiets,
                { it.displayName }, Color(0xFF4CAF50)) {
                currentFilters = currentFilters.copy(selectedDiets = it)
                FilterStateManager.updateCurrentFilters(currentFilters)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Only use my equipment", style = Typography.bodyMedium)
                Switch(
                    checked = currentFilters.useMyEquipmentOnly,
                    onCheckedChange = {
                        currentFilters = currentFilters.copy(useMyEquipmentOnly = it)
                        FilterStateManager.updateCurrentFilters(currentFilters)
                    }
                )
            }

            SliderSection("Calories", currentFilters.calorieRange, FilterRanges.CALORIE_RANGE,
                { "${it.start.toInt()} - ${it.endInclusive.toInt()} cal" }) {
                currentFilters = currentFilters.copy(calorieRange = it)
                FilterStateManager.updateCurrentFilters(currentFilters)
            }

            SliderSection("Protein", currentFilters.proteinRange, FilterRanges.PROTEIN_RANGE,
                { "${it.start.toInt()} - ${it.endInclusive.toInt()}g" }) {
                currentFilters = currentFilters.copy(proteinRange = it)
                FilterStateManager.updateCurrentFilters(currentFilters)
            }

            SliderSection("Fat", currentFilters.fatRange, FilterRanges.FAT_RANGE,
                { "${it.start.toInt()} - ${it.endInclusive.toInt()}g" }) {
                currentFilters = currentFilters.copy(fatRange = it)
                FilterStateManager.updateCurrentFilters(currentFilters)
            }

            SliderSection("Carbohydrates", currentFilters.carbsRange, FilterRanges.CARBS_RANGE,
                { "${it.start.toInt()} - ${it.endInclusive.toInt()}g" }) {
                currentFilters = currentFilters.copy(carbsRange = it)
                FilterStateManager.updateCurrentFilters(currentFilters)
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
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    Text(title, style = Typography.bodyMedium, modifier = Modifier.padding(top = 16.dp))
    Column {
        RangeSlider(value = value, onValueChange = onValueChange, valueRange = valueRange, modifier = Modifier.fillMaxWidth())
        Text(formatValue(value), style = Typography.bodySmall, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun SingleSliderSection(
    title: String, value: Float, valueRange: ClosedFloatingPointRange<Float>,
    steps: Int, formatValue: (Float) -> String, onValueChange: (Float) -> Unit
) {
    Text(title, style = Typography.bodyMedium, modifier = Modifier.padding(top = 16.dp))
    Column {
        Slider(value = value, onValueChange = onValueChange, valueRange = valueRange, steps = steps, modifier = Modifier.fillMaxWidth())
        Text(formatValue(value), style = Typography.bodySmall, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun <T> ChipSelectionSection(
    title: String, items: List<T>, selectedItems: Set<T>,
    getDisplayName: (T) -> String, chipColor: Color, onSelectionChange: (Set<T>) -> Unit
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
                    labelColor = if (isSelected) Color.White else Color.Black
                ),
                shape = Shape.RoundedLarge,
                elevation = AssistChipDefaults.assistChipElevation(elevation = if (isSelected) 6.dp else 2.dp)
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