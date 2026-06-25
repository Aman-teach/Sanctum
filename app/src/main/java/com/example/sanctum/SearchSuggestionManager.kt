package com.example.sanctum

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object SearchSuggestionManager {

    suspend fun getSuggestions(query: String): List<String> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val encoded = URLEncoder.encode(query, "UTF-8")
        val urlStr = "https://duckduckgo.com/ac/?q=$encoded&type=list"
        try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 3000
            connection.readTimeout = 3000

            if (connection.responseCode == 200) {
                val responseStr = connection.inputStream.bufferedReader().readText()
                val jsonArray = JSONArray(responseStr)
                if (jsonArray.length() >= 2) {
                    val suggestionsArray = jsonArray.getJSONArray(1)
                    val result = mutableListOf<String>()
                    for (i in 0 until suggestionsArray.length()) {
                        result.add(suggestionsArray.getString(i))
                    }
                    return@withContext result
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }
}
