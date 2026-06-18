package com.example.sanctum

import android.webkit.WebView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.util.UUID
import android.content.Context

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    var webView: WebView? = null,
    val url: MutableState<String> = mutableStateOf("sanctum://home"),
    val title: MutableState<String> = mutableStateOf("Home"),
    val progress: MutableState<Float> = mutableFloatStateOf(0f),
    val isLoading: MutableState<Boolean> = mutableStateOf(false),
    val canGoBack: MutableState<Boolean> = mutableStateOf(false),
    val canGoForward: MutableState<Boolean> = mutableStateOf(false)
)

class TabManager {
    val tabs = mutableStateListOf<BrowserTab>()
    val activeTabIndex = mutableStateOf(0)

    val activeTab: BrowserTab?
        get() = if (tabs.isNotEmpty() && activeTabIndex.value in tabs.indices) tabs[activeTabIndex.value] else null

    fun addTab(initialUrl: String = "sanctum://home", switchToNew: Boolean = true): BrowserTab {
        val newTab = BrowserTab(url = mutableStateOf(initialUrl))
        tabs.add(newTab)
        if (switchToNew) {
            activeTabIndex.value = tabs.lastIndex
        }
        return newTab
    }

    fun closeTab(index: Int) {
        if (index in tabs.indices) {
            tabs[index].webView?.destroy()
            tabs.removeAt(index)
            if (tabs.isEmpty()) {
                addTab()
            } else if (activeTabIndex.value >= tabs.size) {
                activeTabIndex.value = tabs.lastIndex
            }
        }
    }

    fun switchTab(index: Int) {
        if (index in tabs.indices) {
            activeTabIndex.value = index
        }
    }

    fun saveState(context: Context) {
        val prefs = context.getSharedPreferences("browser_tabs", Context.MODE_PRIVATE)
        val urlsStr = tabs.joinToString("|||") { it.url.value }
        prefs.edit().putString("saved_tabs_str", urlsStr).putInt("active_index", activeTabIndex.value).apply()
    }

    fun loadState(context: Context) {
        val prefs = context.getSharedPreferences("browser_tabs", Context.MODE_PRIVATE)
        val urlsStr = prefs.getString("saved_tabs_str", "") ?: ""
        if (urlsStr.isNotEmpty()) {
            tabs.clear()
            urlsStr.split("|||").forEach { addTab(it, switchToNew = false) }
            activeTabIndex.value = prefs.getInt("active_index", 0).coerceIn(0, Math.max(0, tabs.lastIndex))
        }
    }
}
