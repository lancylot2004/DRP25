package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi

expect fun provideSettings(): Settings

val settings: Settings by lazy { provideSettings() }

interface PersistenceManager<T> {
    val state: StateFlow<T>

    fun update(transform: T.() -> T)
}

@Composable
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
inline fun <reified T : Any> rememberPersisted(
    key: String,
    crossinline fallback: () -> T,
): PersistenceManager<T> = object : PersistenceManager<T> {
    private val _state = MutableStateFlow(
        settings.decodeValueOrNull(key) ?: fallback().also { settings.encodeValue(key, it) },
    )

    override val state: StateFlow<T> = _state.asStateFlow()

    override fun update(transform: T.() -> T) {
        _state.update {
            val updated = it.transform()
            if (updated != it) {
                settings.encodeValue(key, updated)
            }
            updated
        }
    }
}.let { remember { it } }
