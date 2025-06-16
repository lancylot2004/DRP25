package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi

private const val SAVED_RECIPES_KEY = "saved_recipes_ids"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@Composable
fun rememberSavedRecipeIdsManager(): PersistenceManager<Set<String>> = remember {
    object : PersistenceManager<Set<String>> {
        private val _state = MutableStateFlow(
            settings
                .decodeValueOrNull<List<String>>(SAVED_RECIPES_KEY)
                ?.toSet()
                ?: emptySet()
        )
        override val state: StateFlow<Set<String>> = _state.asStateFlow()

        override fun update(transform: Set<String>.() -> Set<String>) {
            _state.update { currentSet ->
                val updatedSet = currentSet.transform()
                if (updatedSet != currentSet) {
                    settings.encodeValue(SAVED_RECIPES_KEY, updatedSet.toList())
                }
                updatedSet
            }
        }
    }
}