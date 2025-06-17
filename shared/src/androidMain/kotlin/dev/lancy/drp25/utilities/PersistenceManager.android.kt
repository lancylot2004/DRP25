package dev.lancy.drp25.utilities

import android.content.Context
import androidx.startup.Initializer
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

internal class SettingsInitializer : Initializer<Context> {
    override fun create(context: Context): Context = context.applicationContext.also { appContext = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

@Volatile
private var appContext: Context? = null

actual fun provideSettings(): Settings {
    val appContext = appContext!!

    val preferencesName = "${appContext.packageName}.settings"
    val delegate = appContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    return SharedPreferencesSettings(delegate)
}
