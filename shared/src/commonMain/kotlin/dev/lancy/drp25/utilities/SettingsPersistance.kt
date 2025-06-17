package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import dev.lancy.drp25.data.MeasurementSystem
import dev.lancy.drp25.data.Allergens
import dev.lancy.drp25.data.Diet
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


// User Account Preference Keys
private const val PREF_KEY_USER_NAME = "user_name"
private const val PREF_KEY_RECIPES_COOKED = "recipes_cooked_count"
private const val PREF_KEY_SAVED_PREFERENCES = "saved_preferences_count"
private const val PREF_KEY_CREATED_RECIPES = "created_recipes_count"

// Unit Preference Keys
private const val PREF_KEY_WEIGHT_UNIT_SYSTEM = "unit_system_weight"
private const val PREF_KEY_VOLUME_UNIT_SYSTEM = "unit_system_volume"
private const val PREF_KEY_TEMPERATURE_UNIT_SYSTEM = "unit_system_temperature"

// Dietary Preference Key
private const val PREF_KEY_DIETARY_PREFERENCES = "dietary_preferences"

// Allergen Avoidance Key
private const val PREF_KEY_ALLERGEN_AVOIDANCES = "allergen_avoidances"

// Daily Cooking Activity Key
private const val PREF_KEY_DAILY_COOKING_ACTIVITY = "daily_cooking_activity"


// Composable Persistence Managers for User Account Data
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberUserNameManager(): PersistenceManager<String> = remember {
    object : PersistenceManager<String> {
        private val _state = MutableStateFlow(
            settings.decodeValueOrNull<String>(PREF_KEY_USER_NAME)
                ?: "User".also { settings.encodeValue(PREF_KEY_USER_NAME, it) }
        )
        override val state: StateFlow<String> = _state.asStateFlow()

        override fun update(transform: String.() -> String) {
            _state.update { current ->
                val updated = current.transform()
                if (updated != current) {
                    settings.encodeValue(PREF_KEY_USER_NAME, updated)
                }
                updated
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberRecipesCookedManager(): PersistenceManager<Int> = remember {
    object : PersistenceManager<Int> {
        private val _state = MutableStateFlow(
            settings.decodeValueOrNull<Int>(PREF_KEY_RECIPES_COOKED)
                ?: 0.also { settings.encodeValue(PREF_KEY_RECIPES_COOKED, it) }
        )
        override val state: StateFlow<Int> = _state.asStateFlow()

        override fun update(transform: Int.() -> Int) {
            _state.update { current ->
                val updated = current.transform()
                if (updated != current) {
                    settings.encodeValue(PREF_KEY_RECIPES_COOKED, updated)
                }
                updated
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberSavedPreferencesManager(): PersistenceManager<Int> = remember {
    object : PersistenceManager<Int> {
        private val _state = MutableStateFlow(
            settings.decodeValueOrNull<Int>(PREF_KEY_SAVED_PREFERENCES)
                ?: 0.also { settings.encodeValue(PREF_KEY_SAVED_PREFERENCES, it) }
        )
        override val state: StateFlow<Int> = _state.asStateFlow()

        override fun update(transform: Int.() -> Int) {
            _state.update { current ->
                val updated = current.transform()
                if (updated != current) {
                    settings.encodeValue(PREF_KEY_SAVED_PREFERENCES, updated)
                }
                updated
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberCreatedRecipesManager(): PersistenceManager<Int> = remember {
    object : PersistenceManager<Int> {
        private val _state = MutableStateFlow(
            settings.decodeValueOrNull<Int>(PREF_KEY_CREATED_RECIPES)
                ?: 0.also { settings.encodeValue(PREF_KEY_CREATED_RECIPES, it) }
        )
        override val state: StateFlow<Int> = _state.asStateFlow()

        override fun update(transform: Int.() -> Int) {
            _state.update { current ->
                val updated = current.transform()
                if (updated != current) {
                    settings.encodeValue(PREF_KEY_CREATED_RECIPES, updated)
                }
                updated
            }
        }
    }
}

// Composable Persistence Managers for Unit Preference Data
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberPreferredWeightSystemManager(): PersistenceManager<MeasurementSystem> = remember {
    object : PersistenceManager<MeasurementSystem> {
        private val _state = MutableStateFlow(
            settings.decodeValueOrNull<MeasurementSystem>(PREF_KEY_WEIGHT_UNIT_SYSTEM)
                ?: MeasurementSystem.METRIC.also { settings.encodeValue(PREF_KEY_WEIGHT_UNIT_SYSTEM, it) }
        )
        override val state: StateFlow<MeasurementSystem> = _state.asStateFlow()

        override fun update(transform: MeasurementSystem.() -> MeasurementSystem) {
            _state.update { current ->
                val updated = current.transform()
                if (updated != current) {
                    settings.encodeValue(PREF_KEY_WEIGHT_UNIT_SYSTEM, updated)
                }
                updated
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberPreferredVolumeSystemManager(): PersistenceManager<MeasurementSystem> = remember {
    object : PersistenceManager<MeasurementSystem> {
        private val _state = MutableStateFlow(
            settings.decodeValueOrNull<MeasurementSystem>(PREF_KEY_VOLUME_UNIT_SYSTEM)
                ?: MeasurementSystem.METRIC.also { settings.encodeValue(PREF_KEY_VOLUME_UNIT_SYSTEM, it) }
        )
        override val state: StateFlow<MeasurementSystem> = _state.asStateFlow()

        override fun update(transform: MeasurementSystem.() -> MeasurementSystem) {
            _state.update { current ->
                val updated = current.transform()
                if (updated != current) {
                    settings.encodeValue(PREF_KEY_VOLUME_UNIT_SYSTEM, updated)
                }
                updated
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberPreferredTemperatureSystemManager(): PersistenceManager<MeasurementSystem> = remember {
    object : PersistenceManager<MeasurementSystem> {
        private val _state = MutableStateFlow(
            settings.decodeValueOrNull<MeasurementSystem>(PREF_KEY_TEMPERATURE_UNIT_SYSTEM)
                ?: MeasurementSystem.METRIC.also { settings.encodeValue(PREF_KEY_TEMPERATURE_UNIT_SYSTEM, it) }
        )
        override val state: StateFlow<MeasurementSystem> = _state.asStateFlow()

        override fun update(transform: MeasurementSystem.() -> MeasurementSystem) {
            _state.update { current ->
                val updated = current.transform()
                if (updated != current) {
                    settings.encodeValue(PREF_KEY_TEMPERATURE_UNIT_SYSTEM, updated)
                }
                updated
            }
        }
    }
}

// Composable Persistence Manager for Dietary Preferences
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberDietaryPreferencesManager(): PersistenceManager<Set<Diet>> = remember {
    object : PersistenceManager<Set<Diet>> {
        private val _state = MutableStateFlow(
            settings
                .decodeValueOrNull<List<Diet>>(PREF_KEY_DIETARY_PREFERENCES)
                ?.toSet()
                ?: emptySet<Diet>().also { settings.encodeValue(PREF_KEY_DIETARY_PREFERENCES, it.toList()) }
        )
        override val state: StateFlow<Set<Diet>> = _state.asStateFlow()

        override fun update(transform: Set<Diet>.() -> Set<Diet>) {
            _state.update { currentSet ->
                val updatedSet = currentSet.transform()
                if (updatedSet != currentSet) {
                    settings.encodeValue(PREF_KEY_DIETARY_PREFERENCES, updatedSet.toList())
                }
                updatedSet
            }
        }
    }
}

// Composable Persistence Manager for Allergen Avoidances
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberAllergenAvoidancesManager(): PersistenceManager<Set<Allergens>> = remember {
    object : PersistenceManager<Set<Allergens>> {
        private val _state = MutableStateFlow(
            settings
                .decodeValueOrNull<List<Allergens>>(PREF_KEY_ALLERGEN_AVOIDANCES)
                ?.toSet()
                ?: emptySet<Allergens>().also { settings.encodeValue(PREF_KEY_ALLERGEN_AVOIDANCES, it.toList()) }
        )
        override val state: StateFlow<Set<Allergens>> = _state.asStateFlow()

        override fun update(transform: Set<Allergens>.() -> Set<Allergens>) {
            _state.update { currentSet ->
                val updatedSet = currentSet.transform()
                if (updatedSet != currentSet) {
                    settings.encodeValue(PREF_KEY_ALLERGEN_AVOIDANCES, updatedSet.toList())
                }
                updatedSet
            }
        }
    }
}

// Helper function to manage state for UI toggles
@Composable
fun <T : Any> rememberPreferenceState(manager: PersistenceManager<T>): Pair<T, (T) -> Unit> {
    val stateValue by manager.state.collectAsState()
    var mutableState by remember(stateValue) { mutableStateOf(stateValue) }

    val setter: (T) -> Unit = { newValue ->
        manager.update { newValue }
        mutableState = newValue
    }
    return Pair(mutableState, setter)
}

// Non-Composable Helper Functions for Data Layer Access
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.getPreferredWeightSystem(): MeasurementSystem =
    decodeValueOrNull<MeasurementSystem>(PREF_KEY_WEIGHT_UNIT_SYSTEM) ?: MeasurementSystem.METRIC

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.setPreferredWeightSystem(system: MeasurementSystem) {
    encodeValue(PREF_KEY_WEIGHT_UNIT_SYSTEM, system)
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.getPreferredVolumeSystem(): MeasurementSystem =
    decodeValueOrNull<MeasurementSystem>(PREF_KEY_VOLUME_UNIT_SYSTEM) ?: MeasurementSystem.METRIC

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.setPreferredVolumeSystem(system: MeasurementSystem) {
    encodeValue(PREF_KEY_VOLUME_UNIT_SYSTEM, system)
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.getPreferredCountSystem(): MeasurementSystem =
    decodeValueOrNull<MeasurementSystem>(PREF_KEY_TEMPERATURE_UNIT_SYSTEM) ?: MeasurementSystem.METRIC

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.setPreferredCountSystem(system: MeasurementSystem) {
    encodeValue(PREF_KEY_TEMPERATURE_UNIT_SYSTEM, system)
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.getUserName(): String =
    decodeValueOrNull<String>(PREF_KEY_USER_NAME) ?: "User"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.setUserName(name: String) {
    encodeValue(PREF_KEY_USER_NAME, name)
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.getRecipesCookedCount(): Int =
    decodeValueOrNull<Int>(PREF_KEY_RECIPES_COOKED) ?: 0

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.setRecipesCookedCount(count: Int) {
    encodeValue(PREF_KEY_RECIPES_COOKED, count)
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.getSavedPreferencesCount(): Int =
    decodeValueOrNull<Int>(PREF_KEY_SAVED_PREFERENCES) ?: 0

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.setSavedPreferencesCount(count: Int) {
    encodeValue(PREF_KEY_SAVED_PREFERENCES, count)
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.getCreatedRecipesCount(): Int =
    decodeValueOrNull<Int>(PREF_KEY_CREATED_RECIPES) ?: 0

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.setCreatedRecipesCount(count: Int) {
    encodeValue(PREF_KEY_CREATED_RECIPES, count)
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.getDietaryPreferences(): Set<Diet> =
    decodeValueOrNull<List<Diet>>(PREF_KEY_DIETARY_PREFERENCES)?.toSet() ?: emptySet()

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.setDietaryPreferences(diets: Set<Diet>) {
    encodeValue(PREF_KEY_DIETARY_PREFERENCES, diets.toList())
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.getAllergenAvoidances(): Set<Allergens> =
    decodeValueOrNull<List<Allergens>>(PREF_KEY_ALLERGEN_AVOIDANCES)?.toSet() ?: emptySet()

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
fun Settings.setAllergenAvoidances(allergens: Set<Allergens>) {
    encodeValue(PREF_KEY_ALLERGEN_AVOIDANCES, allergens.toList())
}
