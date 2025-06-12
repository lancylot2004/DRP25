package dev.lancy.drp25.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import dev.lancy.drp25.data.Diet
import dev.lancy.drp25.data.MeasurementSystem
import dev.lancy.drp25.ui.RootNode
import dev.lancy.drp25.ui.shared.NavConsumer
import dev.lancy.drp25.ui.shared.NavConsumerImpl
import dev.lancy.drp25.utilities.ColourScheme
import dev.lancy.drp25.utilities.Shape
import dev.lancy.drp25.utilities.Size
import dev.lancy.drp25.utilities.Typography
import dev.lancy.drp25.utilities.rememberPersisted

class SettingsNode(
    nodeContext: NodeContext,
    parent: RootNode,
    private val back: () -> Unit
) : LeafNode(nodeContext),
    NavConsumer<RootNode.RootTarget, RootNode> by NavConsumerImpl(parent) {

    @Composable
    override fun Content(modifier: Modifier) {
        val systemPersistence = rememberPersisted("measurement_system") { MeasurementSystem.METRIC }
        val selectedSystem = systemPersistence.state.collectAsState().value

        val dietPersistence = rememberPersisted("dietary_preferences") { emptySet<Diet>() }
        val selectedDiets = dietPersistence.state.collectAsState().value

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ColourScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Size.BigPadding)
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
                        style = Typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(
                    color = ColourScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = Size.Padding)
                )

                Text(
                    text = "System Preference",
                    color = ColourScheme.onBackground,
                    style = Typography.labelMedium,
                    modifier = Modifier.padding(vertical = Size.Padding)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Shape.RoundedLarge)
                        .background(ColourScheme.surfaceVariant)
                        .padding(Size.Padding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MeasurementSystem.entries.forEach { system ->
                        val selected = system == selectedSystem
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(Shape.RoundedLarge)
                                .background(if (selected) ColourScheme.primaryContainer else ColourScheme.surfaceVariant)
                                .clickable {
                                    if (!selected) {
                                        systemPersistence.update { system }
                                    }
                                }
                                .padding(vertical = Size.Padding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = system.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (selected) ColourScheme.onPrimaryContainer else ColourScheme.onSurfaceVariant,
                                style = Typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(Size.BigPadding))

                Text(
                    text = "Dietary Preferences",
                    color = ColourScheme.onBackground,
                    style = Typography.labelMedium,
                    modifier = Modifier.padding(vertical = Size.Padding)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Shape.RoundedLarge)
                        .background(ColourScheme.surfaceVariant)
                        .padding(vertical = Size.Padding)
                        .weight(1f, fill = false)
                ) {
                    DietPreferenceList(
                        allDiets = Diet.entries,
                        selected = selectedDiets,
                        onToggle = { diet ->
                            dietPersistence.update {
                                if (diet in this) this - diet else this + diet
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DietPreferenceList(
    allDiets: List<Diet>,
    selected: Set<Diet>,
    onToggle: (Diet) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Size.Spacing),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 260.dp)
    ) {
        items(allDiets) { diet ->
            val isSelected = diet in selected
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(diet) }
                    .padding(vertical = Size.Padding, horizontal = Size.BigPadding)
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggle(diet) }
                )
                Spacer(Modifier.width(Size.Padding))
                Text(diet.displayName, color = ColourScheme.onBackground, style = Typography.bodyMedium)
            }
        }
    }
}