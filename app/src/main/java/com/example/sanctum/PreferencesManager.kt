package com.example.sanctum

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PreferencesManager {
    private const val PREFS_NAME = "sanctum_prefs"

    // Keys
    private const val KEY_SEARCH_ENGINE = "search_engine"
    private const val KEY_THIRD_PARTY_COOKIES = "third_party_cookies"
    private const val KEY_DO_NOT_TRACK = "do_not_track"
    private const val KEY_DESKTOP_MODE = "desktop_mode"
    private const val KEY_THEME = "theme_mode" // "System", "Light", "Dark"

    private lateinit var prefs: SharedPreferences

    private val _searchEngine = MutableStateFlow("DuckDuckGo")
    val searchEngine: StateFlow<String> = _searchEngine.asStateFlow()

    private val _thirdPartyCookies = MutableStateFlow(false)
    val thirdPartyCookies: StateFlow<Boolean> = _thirdPartyCookies.asStateFlow()

    private val _doNotTrack = MutableStateFlow(true)
    val doNotTrack: StateFlow<Boolean> = _doNotTrack.asStateFlow()

    private val _desktopMode = MutableStateFlow(false)
    val desktopMode: StateFlow<Boolean> = _desktopMode.asStateFlow()

    private val _theme = MutableStateFlow("System")
    val theme: StateFlow<String> = _theme.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _searchEngine.value = prefs.getString(KEY_SEARCH_ENGINE, "DuckDuckGo") ?: "DuckDuckGo"
        _thirdPartyCookies.value = prefs.getBoolean(KEY_THIRD_PARTY_COOKIES, false)
        _doNotTrack.value = prefs.getBoolean(KEY_DO_NOT_TRACK, true)
        _desktopMode.value = prefs.getBoolean(KEY_DESKTOP_MODE, false)
        _theme.value = prefs.getString(KEY_THEME, "System") ?: "System"
    }

    fun setSearchEngine(engine: String) {
        prefs.edit().putString(KEY_SEARCH_ENGINE, engine).apply()
        _searchEngine.value = engine
    }

    fun setThirdPartyCookies(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_THIRD_PARTY_COOKIES, enabled).apply()
        _thirdPartyCookies.value = enabled
    }

    fun setDoNotTrack(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DO_NOT_TRACK, enabled).apply()
        _doNotTrack.value = enabled
    }

    fun setDesktopMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DESKTOP_MODE, enabled).apply()
        _desktopMode.value = enabled
    }

    fun setTheme(themeMode: String) {
        prefs.edit().putString(KEY_THEME, themeMode).apply()
        _theme.value = themeMode
    }
}
