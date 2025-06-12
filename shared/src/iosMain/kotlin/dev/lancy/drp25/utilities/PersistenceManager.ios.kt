package dev.lancy.drp25.utilities

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings

@OptIn(ExperimentalSettingsImplementation::class)
actual fun provideSettings(): Settings = KeychainSettings("drp25")
