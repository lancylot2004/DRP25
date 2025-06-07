package dev.lancy.drp25.ui.main.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import dev.lancy.drp25.data.Cuisine
import dev.lancy.drp25.data.Diet
import dev.lancy.drp25.data.Ingredients
import dev.lancy.drp25.data.MealType
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography

private val PLACEHOLDER_SLIDER_RANGE = 0f..100f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContent() {
    Column(
        modifier = Modifier.background(color = Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(
                top = Size.Padding,
                bottom = Size.BarLarge,
                start = Size.Padding,
                end = Size.Padding
            )
    ) {
        Text("Filters", style = Typography.titleMedium)

        Text("Time")
        var timeSliderPosition by remember { mutableStateOf(PLACEHOLDER_SLIDER_RANGE) }
        RangeSlider(
            value = timeSliderPosition,
            onValueChange = { timeSliderPosition = it },
            valueRange = PLACEHOLDER_SLIDER_RANGE
        )

        Text("Rating")
        var ratingSliderPosition by remember { mutableFloatStateOf(0f) }
        Slider(
            value = ratingSliderPosition,
            onValueChange = { ratingSliderPosition = it },
            valueRange = 0f..5f,
            steps = 3
        )

        Text("Type of meal")
        FlowRow {
            MealType.entries.forEach { dish ->
                AssistChip(
                    onClick = {},
                    label = {
                        androidx.compose.material.Text(
                            dish.displayName,
                            style = Typography.bodySmall
                        )
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 28.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.LightGray,
                        labelColor = Color.Black,
                    ),
                    shape = Shape.RoundedLarge,
                    elevation = AssistChipDefaults.assistChipElevation(),
                )
            }
        }

        Text("Cuisine")
        FlowRow {
            Cuisine.entries.forEach { cuisine ->
                AssistChip(
                    onClick = {},
                    label = {
                        androidx.compose.material.Text(
                            cuisine.displayName,
                            style = Typography.bodySmall
                        )
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 28.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.LightGray,
                        labelColor = Color.Black,
                    ),
                    shape = Shape.RoundedLarge,
                    elevation = AssistChipDefaults.assistChipElevation(),
                )
            }
        }

        Text("Dietary needs")
        FlowRow {
            Diet.entries.forEach { diet ->
                AssistChip(
                    onClick = {},
                    label = {
                        androidx.compose.material.Text(
                            diet.displayName,
                            style = Typography.bodySmall
                        )
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 28.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.LightGray,
                        labelColor = Color.Black,
                    ),
                    shape = Shape.RoundedLarge,
                    elevation = AssistChipDefaults.assistChipElevation(),
                )
            }
        }

        Text("Must include")
        var includeTextFieldState by remember { mutableStateOf("") }
        var includeActive by remember { mutableStateOf(false) }
        SearchBar(
            query = includeTextFieldState,
            onQueryChange = { includeTextFieldState = it },
            onSearch = { includeActive = false },
            content = {},
            active = includeActive,
            onActiveChange = {},
            placeholder = {
                Row {
                    Icon(
                        imageVector = Lucide.Search, null, modifier = Modifier.padding(
                            end = Size.Padding
                        )
                    )
                    Text("Search ingredients", color = Color.LightGray)
                }
            },
        )
        FlowRow {
            Ingredients.entries.take(3).forEach { ingredient ->
                AssistChip(
                    onClick = {},
                    label = {
                        androidx.compose.material.Text(
                            ingredient.displayName,
                            style = Typography.bodySmall
                        )
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 28.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.LightGray,
                        labelColor = Color.Black,
                    ),
                    shape = Shape.RoundedLarge,
                    elevation = AssistChipDefaults.assistChipElevation(),
                )
            }
        }

        Text("Must avoid")
        var avoidTextFieldState by remember { mutableStateOf("") }
        var avoidActive by remember { mutableStateOf(false) }
        SearchBar(
            query = avoidTextFieldState,
            onQueryChange = { avoidTextFieldState = it },
            onSearch = { avoidActive = false },
            content = {},
            active = avoidActive,
            onActiveChange = {},
            placeholder = {
                Row {
                    Icon(
                        imageVector = Lucide.Search, null, modifier = Modifier.padding(
                            end = Size.Padding
                        )
                    )
                    Text("Search ingredients", color = Color.LightGray)
                }
            },
        )
        FlowRow {
            Ingredients.entries.take(3).forEach { ingredient ->
                AssistChip(
                    onClick = {},
                    label = {
                        androidx.compose.material.Text(
                            ingredient.displayName,
                            style = Typography.bodySmall
                        )
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 28.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.LightGray,
                        labelColor = Color.Black,
                    ),
                    shape = Shape.RoundedLarge,
                    elevation = AssistChipDefaults.assistChipElevation(),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Only use my equipment")
            var equipmentChecked by remember { mutableStateOf(true) }
            Switch(
                checked = equipmentChecked,
                onCheckedChange = {
                    equipmentChecked = it
                },
            )
        }

        Text("Calories")
        var calorieSliderPosition by remember { mutableStateOf(PLACEHOLDER_SLIDER_RANGE) }
        RangeSlider(
            value = calorieSliderPosition,
            onValueChange = { calorieSliderPosition = it },
            valueRange = PLACEHOLDER_SLIDER_RANGE
        )

        Text("Protein")
        var proteinSliderPosition by remember { mutableStateOf(PLACEHOLDER_SLIDER_RANGE) }
        RangeSlider(
            value = proteinSliderPosition,
            onValueChange = { proteinSliderPosition = it },
            valueRange = PLACEHOLDER_SLIDER_RANGE
        )

        Text("Fat")
        var fatSliderPosition by remember { mutableStateOf(PLACEHOLDER_SLIDER_RANGE) }
        RangeSlider(
            value = fatSliderPosition,
            onValueChange = { fatSliderPosition = it },
            valueRange = PLACEHOLDER_SLIDER_RANGE
        )

        Text("Carbohydrates")
        var carbsSliderPosition by remember { mutableStateOf(PLACEHOLDER_SLIDER_RANGE) }
        RangeSlider(
            value = carbsSliderPosition,
            onValueChange = { carbsSliderPosition = it },
            valueRange = PLACEHOLDER_SLIDER_RANGE
        )

    }

}