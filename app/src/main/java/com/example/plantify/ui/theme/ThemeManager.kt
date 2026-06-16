package com.example.plantify.ui.theme

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    private const val PREF_NAME = "plantify_theme_prefs"
    private const val KEY_DARK_MODE = "is_dark_mode"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_LAST_WEATHER = "last_weather"

    private var appContext: Context? = null

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _lastWeather = MutableStateFlow("Sedang mengambil cuaca...")
    val lastWeather: StateFlow<String> = _lastWeather.asStateFlow()

    fun init(context: Context) {
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        _isDarkMode.value = prefs.getBoolean(KEY_DARK_MODE, false)
        _language.value = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        _lastWeather.value = prefs.getString(KEY_LAST_WEATHER, "Mencari data cuaca...") ?: "Mencari data cuaca..."
    }

    fun setDarkMode(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, isDark).apply()
        _isDarkMode.value = isDark
    }

    fun setLanguage(context: Context, lang: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _language.value = lang
    }

    fun setLastWeather(weatherText: String) {
        appContext?.let { ctx ->
            val prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_LAST_WEATHER, weatherText).apply()
        }
        _lastWeather.value = weatherText
    }
}
