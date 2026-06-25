package com.example.sanctum

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class BookmarkItem(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

object BookmarkManager {
    private const val PREFS_NAME = "sanctum_bookmarks"
    private const val KEY_BOOKMARKS = "bookmarks_json"

    private val _bookmarks = MutableStateFlow<List<BookmarkItem>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkItem>> = _bookmarks.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_BOOKMARKS, "[]") ?: "[]"
        val loaded = mutableListOf<BookmarkItem>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                loaded.add(BookmarkItem(
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
        _bookmarks.value = loaded.sortedByDescending { it.timestamp }
    }

    private fun save(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        _bookmarks.value.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("url", item.url)
                put("title", item.title)
                put("timestamp", item.timestamp)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_BOOKMARKS, jsonArray.toString()).apply()
    }

    fun addBookmark(context: Context, url: String, title: String) {
        if (url.startsWith("sanctum://") || url.isBlank()) return
        val current = _bookmarks.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.url == url }
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
        }
        current.add(0, BookmarkItem(url = url, title = title.ifBlank { url }))
        _bookmarks.value = current
        save(context)
    }

    fun removeBookmark(context: Context, url: String) {
        val current = _bookmarks.value.toMutableList()
        current.removeAll { it.url == url }
        _bookmarks.value = current
        save(context)
    }

    fun isBookmarked(url: String): Boolean {
        return _bookmarks.value.any { it.url == url }
    }
}
