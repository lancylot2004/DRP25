package dev.lancy.drp25.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import dev.lancy.drp25.utilities.PersistenceManager
import dev.lancy.drp25.utilities.settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

private const val PREF_KEY_FILTERS = "filters_state"

/**
 * Global instance of the filters persistence manager.
 * This ensures that the same instance is used throughout the app lifecycle,
 * preventing state loss during recomposition.
 */
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
private val filtersManager = object : PersistenceManager<FilterValues> {
    private val _state = MutableStateFlow(
        try {
            val jsonString = settings.getStringOrNull(PREF_KEY_FILTERS)

            if (jsonString != null) {
                val decoded = Json.decodeFromString<FilterValues>(jsonString)
                decoded
            } else {
                FilterValues().also {
                    val jsonToSave = Json.encodeToString(it)
                    settings.putString(PREF_KEY_FILTERS, jsonToSave)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            FilterValues()
        }
    )

    override val state: StateFlow<FilterValues> = _state.asStateFlow()

    override fun update(transform: FilterValues.() -> FilterValues) {
        _state.update { current ->
            val updated = current.transform()
            if (updated != current) {
                try {
                    val jsonToSave = Json.encodeToString(updated)
                    settings.putString(PREF_KEY_FILTERS, jsonToSave)

                    // Verify the save worked
                    val verification = settings.getStringOrNull(PREF_KEY_FILTERS)
                    if (verification != null) {
                        val verificationDecoded = Json.decodeFromString<FilterValues>(verification)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            updated
        }
    }
}

/**
 * Provides a [PersistenceManager] for [FilterValues].
 * This Composable returns the global instance of the filters manager,
 * ensuring that the state is maintained across recompositions.
 */
@Composable
fun rememberFiltersManager(): PersistenceManager<FilterValues> = remember { filtersManager }