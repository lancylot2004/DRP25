package dev.lancy.drp25.ui.main.me

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import dev.lancy.drp25.data.Diet
import dev.lancy.drp25.data.Allergens
import dev.lancy.drp25.data.MeasurementSystem
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.rememberAllergenAvoidancesManager
import dev.lancy.drp25.utilities.rememberPreferredWeightSystemManager
import dev.lancy.drp25.utilities.rememberPreferredVolumeSystemManager
import dev.lancy.drp25.utilities.rememberPreferredTemperatureSystemManager
import dev.lancy.drp25.utilities.rememberDietaryPreferencesManager


class SettingsNode(
    nodeContext: NodeContext,
    parent: RootNode,
    private val back: () -> Unit
) : LeafNode(nodeContext),
    NavConsumer<RootNode.RootTarget, RootNode> by NavConsumerImpl(parent) {

    @Composable
    override fun Content(modifier: Modifier) {
        val weightSystemManager = rememberPreferredWeightSystemManager()
        val volumeSystemManager = rememberPreferredVolumeSystemManager()
        val temperatureSystemManager = rememberPreferredTemperatureSystemManager()

        val preferredWeightSystem by weightSystemManager.state.collectAsState()
        val preferredVolumeSystem by volumeSystemManager.state.collectAsState()
        val preferredTemperatureSystem by temperatureSystemManager.state.collectAsState()

        val dietaryPreferencesManager = rememberDietaryPreferencesManager()
        val selectedDiets by dietaryPreferencesManager.state.collectAsState()

        val allergenPreferencesManager = rememberAllergenAvoidancesManager()
        val selectedAllergens by allergenPreferencesManager.state.collectAsState()

        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier.fillMaxSize().background(ColourScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(Size.BigPadding).verticalScroll(scrollState)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = Size.Padding)
                ) {
                    IconButton(
                        onClick = back,
                        modifier = Modifier
                            .size(Size.IconLarge)
                            .clip(Shape.RoundedMedium)
                            .background(ColourScheme.surfaceVariant)
                            .padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ChevronLeft,
                            contentDescription = "Back",
                            tint = ColourScheme.onBackground
                        )
                    }
                    Spacer(Modifier.width(Size.Padding))
                    Text(
                        text = "Settings",
                        color = ColourScheme.onBackground,
                        style = Typography.headlineMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(
                    color = ColourScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = Size.Padding)
                )

                Text(
                    text = "Unit System Preferences",
                    color = ColourScheme.onBackground,
                    style = Typography.titleSmall,
                    modifier = Modifier.padding(vertical = Size.Padding)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Shape.RoundedLarge)
                        .background(ColourScheme.surfaceVariant)
                        .padding(start = 30.dp, end = 30.dp, top = 20.dp, bottom = 20.dp)
                ) {
                    // "Metric / Imperial" in the top-right corner above switches
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            "Metric / Imperial",
                            style = Typography.labelSmall,
                            color = ColourScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp, end = 2.dp)
                        )
                    }

                    // Weight Units Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Weight",
                            color = ColourScheme.onBackground,
                            style = Typography.labelLarge,
                            modifier = Modifier.width(90.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Unit text left of the switch, with color matching active state
                        Text(
                            text = if (preferredWeightSystem == MeasurementSystem.METRIC) "g / kg" else "oz / lb",
                            color = if (preferredWeightSystem == MeasurementSystem.METRIC) ColourScheme.primary else ColourScheme.onSurfaceVariant,
                            style = Typography.bodyMedium,
                            textAlign = TextAlign.End,
                            modifier = Modifier.width(100.dp).padding(end = Size.Spacing)
                        )

                        Switch(
                            checked = (preferredWeightSystem == MeasurementSystem.IMPERIAL),
                            onCheckedChange = { isChecked ->
                                val newSystem = if (isChecked) MeasurementSystem.IMPERIAL else MeasurementSystem.METRIC
                                weightSystemManager.update { newSystem }
                            },
                        )
                    }
                    Spacer(Modifier.height(8.dp))

                    // Volume Units Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Volume",
                            color = ColourScheme.onBackground,
                            style = Typography.labelLarge,
                            modifier = Modifier.width(90.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = if (preferredVolumeSystem == MeasurementSystem.METRIC) "ml / L" else "t(b)sp / cup",
                            color = if (preferredVolumeSystem == MeasurementSystem.METRIC) ColourScheme.primary else ColourScheme.onSurfaceVariant,
                            style = Typography.bodyMedium,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .width(100.dp)
                                .padding(end = Size.Spacing)
                        )

                        Switch(
                            checked = (preferredVolumeSystem == MeasurementSystem.IMPERIAL),
                            onCheckedChange = { isChecked ->
                                val newSystem = if (isChecked) MeasurementSystem.IMPERIAL else MeasurementSystem.METRIC
                                volumeSystemManager.update { newSystem }
                            },
                        )
                    }
                    Spacer(Modifier.height(8.dp))

                    // Count Units Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Temperature",
                            color = ColourScheme.onBackground,
                            style = Typography.labelLarge,
                            modifier = Modifier.width(120.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = if (preferredTemperatureSystem == MeasurementSystem.METRIC) "°C" else "°F",
                            color = if (preferredTemperatureSystem == MeasurementSystem.METRIC) ColourScheme.primary else ColourScheme.onSurfaceVariant,
                            style = Typography.bodyMedium,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .width(100.dp)
                                .padding(end = Size.Spacing)
                        )

                        Switch(
                            checked = (preferredTemperatureSystem == MeasurementSystem.IMPERIAL),
                            onCheckedChange = { isChecked ->
                                val newSystem = if (isChecked) MeasurementSystem.IMPERIAL else MeasurementSystem.METRIC
                                temperatureSystemManager.update { newSystem }
                            },
                        )
                    }
                }

                Spacer(Modifier.height(Size.BigPadding))

                Text(
                    text = "Dietary Preferences",
                    color = ColourScheme.onBackground,
                    style = Typography.titleSmall,
                    modifier = Modifier.padding(vertical = Size.Padding)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Shape.RoundedLarge)
                        .background(ColourScheme.surfaceVariant)
                        .padding(vertical = Size.Padding)
                        .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp)
                ) {
                    DietPreferenceGrid(
                        allDiets = Diet.entries.toList(),
                        selected = selectedDiets,
                        onToggle = { diet ->
                            dietaryPreferencesManager.update {
                                if (diet in this) this - diet else this + diet
                            }
                        }
                    )
                }

                Spacer(Modifier.height(Size.Padding))

                Text(
                    text = "Allergens To Avoid",
                    color = ColourScheme.onBackground,
                    style = Typography.titleSmall,
                    modifier = Modifier.padding(vertical = Size.Padding)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Shape.RoundedLarge)
                        .background(ColourScheme.surfaceVariant)
                        .padding(vertical = Size.Padding)
                        .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp)
                ) {
                    AllergenPreferenceGrid(
                        allAllergens = Allergens.entries.toList(),
                        selected = selectedAllergens,
                        onToggle = { allergen ->
                            allergenPreferencesManager.update {
                                if (allergen in this) this - allergen else this + allergen
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DietPreferenceGrid(
    allDiets: List<Diet>,
    selected: Set<Diet>,
    onToggle: (Diet) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in allDiets.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val diet1 = allDiets[i]
                DietCheckboxItem(diet = diet1, isSelected = diet1 in selected, onToggle = onToggle, modifier = Modifier.weight(1f))

                if (i + 1 < allDiets.size) {
                    val diet2 = allDiets[i + 1]
                    DietCheckboxItem(diet = diet2, isSelected = diet2 in selected, onToggle = onToggle, modifier = Modifier.weight(1f))
                } else {
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun DietCheckboxItem(
    diet: Diet,
    isSelected: Boolean,
    onToggle: (Diet) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onToggle(diet) }
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle(diet) }
        )
        Text(diet.displayName, color = ColourScheme.onBackground, style = Typography.bodyMedium)
    }
}

@Composable
private fun AllergenPreferenceGrid(
    allAllergens: List<Allergens>,
    selected: Set<Allergens>,
    onToggle: (Allergens) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in allAllergens.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val allergen1 = allAllergens[i]
                AllergenCheckboxItem(allergen = allergen1, isSelected = allergen1 in selected, onToggle = onToggle, modifier = Modifier.weight(1f))

                if (i + 1 < allAllergens.size) {
                    val allergen2 = allAllergens[i + 1]
                    AllergenCheckboxItem(allergen = allergen2, isSelected = allergen2 in selected, onToggle = onToggle, modifier = Modifier.weight(1f))
                } else {
                     Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun AllergenCheckboxItem(
    allergen: Allergens,
    isSelected: Boolean,
    onToggle: (Allergens) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onToggle(allergen) }
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle(allergen) }
        )
        Text(allergen.displayName, color = ColourScheme.onBackground, style = Typography.bodyMedium)
    }
}