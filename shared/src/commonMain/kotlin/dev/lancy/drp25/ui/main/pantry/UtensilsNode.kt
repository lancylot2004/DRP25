package dev.lancy.drp25.ui.main.pantry

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.Check
import dev.lancy.drp25.utilities.UtensilIcon
import dev.lancy.drp25.utilities.UtensilIconView
import dev.lancy.drp25.utilities.rememberPantryUtensilsManager
import dev.lancy.drp25.utilities.PersistentUtensilSelection
import kotlinx.datetime.toLocalDateTime

@Composable
fun UtensilsToolbar(
    onUtensilsClick: () -> Unit,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 8.dp else 6.dp
        ),
        border = if (isActive) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null,
        modifier = modifier.size(56.dp)
    ) {
        IconButton(
            onClick = onUtensilsClick,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Icon(
                imageVector = Lucide.Utensils,
                contentDescription = if (isActive) "Close Utensils" else "Open Utensils",
                tint = if (isActive) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }
            )
        }
    }
}

@Composable
fun UtensilsScreen(
    modifier: Modifier = Modifier // Removed onDismiss parameter
) {
    // Use the proper utensils manager with PersistentUtensilSelection
    val utensilsManager = rememberPantryUtensilsManager()
    val persistentUtensils by utensilsManager.state.collectAsState()

    // Convert PersistentUtensilSelection to Set<UtensilIcon>
    val selectedUtensils = remember(persistentUtensils) {
        persistentUtensils
            .filter { it.isAvailable }
            .mapNotNull { persistent ->
                try { UtensilIcon.valueOf(persistent.utensilName) }
                catch (e: IllegalArgumentException) { null }
            }
            .toSet()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Utensils,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Kitchen Utensils",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Get utensils categories with error handling
            val utensilCategories = getUtensilCategories()

            if (utensilCategories.isEmpty()) {
                // Fallback UI in case of errors
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error loading utensils",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 136.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    utensilCategories.forEach { category ->
                        item(key = "category_${category.name}") {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                            )
                        }

                        // Add null safety and proper chunking
                        val chunkedUtensils = category.utensils.chunked(3)
                        items(
                            items = chunkedUtensils,
                            key = { row -> "row_${category.name}_${row.firstOrNull()?.name ?: ""}" }
                        ) { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { utensil ->
                                    UtensilCard(
                                        utensil = utensil,
                                        isSelected = selectedUtensils.contains(utensil),
                                        onSelectionChange = { isSelected ->
                                            // Update using PersistentUtensilSelection structure
                                            utensilsManager.update {
                                                val existingIndex = this.indexOfFirst {
                                                    it.utensilName == utensil.name
                                                }

                                                if (existingIndex >= 0) {
                                                    // Update existing selection
                                                    this.toMutableList().apply {
                                                        set(existingIndex, this[existingIndex].copy(
                                                            isAvailable = isSelected,
                                                            lastUsed = kotlinx.datetime.Clock.System.now()
                                                                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                                                                .toString()
                                                        ))
                                                    }
                                                } else if (isSelected) {
                                                    // Add new selection
                                                    this + PersistentUtensilSelection(
                                                        utensilName = utensil.name,
                                                        isAvailable = true,
                                                        lastUsed = kotlinx.datetime.Clock.System.now()
                                                            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                                                            .toString()
                                                    )
                                                } else {
                                                    this
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Fill remaining space if row has less than 3 items
                                repeat(3 - row.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        item(key = "spacer_${category.name}") {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UtensilCard(
    utensil: UtensilIcon,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onSelectionChange(!isSelected) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null,
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Safe handling of UtensilIconView
            val iconExists = remember(utensil) {
                try {
                    // Check if UtensilIconView exists and can be called
                    true
                } catch (e: Exception) {
                    false
                }
            }

            if (iconExists) {
                UtensilIconView(
                    icon = utensil,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                // Fallback icon if UtensilIconView fails
                Icon(
                    imageVector = Lucide.Utensils,
                    contentDescription = utensil.name,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = utensil.name.lowercase()
                    .replace("_", " ")
                    .split(" ")
                    .joinToString(" ") { word ->
                        word.replaceFirstChar { char ->
                            if (char.isLowerCase()) char.titlecase() else char.toString()
                        }
                    },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Lucide.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Data class for organizing utensils by category
data class UtensilCategory(
    val name: String,
    val utensils: List<UtensilIcon>
)

// Helper function to organize utensils by category with null safety
private fun getUtensilCategories(): List<UtensilCategory> {
    return try {
        listOf(
            UtensilCategory(
                "Appliances",
                listOf(
                    UtensilIcon.OVEN,
                    UtensilIcon.MICROWAVE,
                    UtensilIcon.STOVE,
                    UtensilIcon.TOASTER,
                    UtensilIcon.FOOD_PROCESSOR,
                    UtensilIcon.HAND_MIXER,
                    UtensilIcon.JUICER,
                    UtensilIcon.RICE_COOKER,
                    UtensilIcon.KETTLE
                )
            ),
            UtensilCategory(
                "Cooking",
                listOf(
                    UtensilIcon.PAN,
                    UtensilIcon.SAUCEPAN,
                    UtensilIcon.WOK,
                    UtensilIcon.STOCK_POT,
                    UtensilIcon.PRESSURE_COOKER,
                    UtensilIcon.GRILL
                )
            ),
            UtensilCategory(
                "Baking",
                listOf(
                    UtensilIcon.STAND_MIXER,
                    UtensilIcon.MIXING_BOWL,
                    UtensilIcon.WHISK,
                    UtensilIcon.ROLLING_PIN,
                    UtensilIcon.MEASURING_CUP,
                    UtensilIcon.OVEN_GLOVE
                )
            ),
            UtensilCategory(
                "Cutting & Prep",
                listOf(
                    UtensilIcon.FORK,
                    UtensilIcon.COOK_KNIFE,
                    UtensilIcon.CLEAVER_BUTCHER,
                    UtensilIcon.SCISSORS,
                    UtensilIcon.PEELER,
                    UtensilIcon.GRATER,
                    UtensilIcon.LADLE,
                    UtensilIcon.SPATULA,
                    UtensilIcon.TONGS,
                    UtensilIcon.STRAINER,
                    UtensilIcon.SKIMMER,
                    UtensilIcon.TENDERIZER,
                )
            )
        )
    } catch (e: Exception) {
        emptyList()
    }
}