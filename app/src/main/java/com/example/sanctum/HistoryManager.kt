package com.example.sanctum

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class HistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

object HistoryManager {
    private const val PREFS_NAME = "sanctum_history"
    private const val KEY_HISTORY = "history_json"

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
        val loaded = mutableListOf<HistoryItem>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                loaded.add(HistoryItem(
                    id = obj.getString("id"),
                    url = obj.getString("url"),
                    title = obj.getString("title"),
                    timestamp = obj.getLong("timestamp")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Sort descending by timestamp
        _history.value = loaded.sortedByDescending { it.timestamp }
    }

    private fun save(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        // Limit history to 1000 items
        val toSave = _history.value.take(1000)
        toSave.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("url", item.url)
                put("title", item.title)
                put("timestamp", item.timestamp)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }

    fun addHistory(context: Context, url: String, title: String) {
        if (url.startsWith("sanctum://") || url.isBlank()) return
        val current = _history.value.toMutableList()
        
        // Deduplicate: If same URL visited recently, bring to top and update timestamp
        val existingIndex = current.indexOfFirst { it.url == url }
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
        }
        
        current.add(0, HistoryItem(url = url, title = title.ifBlank { url }))
        _history.value = current
        save(context)
    }

    fun removeHistory(context: Context, id: String) {
        val current = _history.value.toMutableList()
        current.removeAll { it.id == id }
        _history.value = current
        save(context)
    }

    fun removeAllHistoryForUrl(context: Context, url: String) {
        val current = _history.value.toMutableList()
        val host = try {
            android.net.Uri.parse(url).host
        } catch (e: Exception) {
            null
        }
        current.removeAll { 
            it.url == url || (host != null && try { android.net.Uri.parse(it.url).host == host } catch(ex: Exception) { false }) 
        }
        _history.value = current
        save(context)
    }

    fun clearHistory(context: Context) {
        _history.value = emptyList()
        save(context)
    }
}
