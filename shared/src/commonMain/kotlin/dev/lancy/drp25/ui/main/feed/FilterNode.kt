package dev.lancy.drp25.ui.main.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
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
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import dev.lancy.drp25.ui.main.MainNode
import dev.lancy.drp25.utilities.DishCuisine
import dev.lancy.drp25.utilities.DishDiet
import dev.lancy.drp25.utilities.DishType
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography

class FilterNode(nodeContext: NodeContext, parent: MainNode): LeafNode(nodeContext) {
    private val TIME_SLIDER_RANGE = 0f..100f

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier) {
        Column(modifier = Modifier.padding(Size.Padding)) {
            Text(
                "Filters",
                style = Typography.titleMedium
            )

            Text("Time")
            var timeSliderPosition by remember { mutableStateOf(TIME_SLIDER_RANGE) }
            RangeSlider(
                value = timeSliderPosition,
                onValueChange = { timeSliderPosition = it },
                valueRange = TIME_SLIDER_RANGE
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
                DishType.entries.forEach { dish ->
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
                DishCuisine.entries.forEach { cuisine ->
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
                DishDiet.entries.forEach { diet ->
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

        }

    }
}