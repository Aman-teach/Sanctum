package com.example.sanctum

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class NewsItem(
    val title: String,
    val link: String,
    val pubDate: String,
    val imageUrl: String
)

object NewsFetcher {
    suspend fun fetchTechNews(): List<NewsItem> = withContext(Dispatchers.IO) {
        val newsList = mutableListOf<NewsItem>()
        try {
            // Fetch Top Stories IDs from Hacker News
            val topStoriesUrl = URL("https://hacker-news.firebaseio.com/v0/topstories.json")
            val conn = topStoriesUrl.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            
            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val responseStr = reader.readText()
                reader.close()
                
                val idsArray = JSONArray(responseStr)
                // Fetch first 10 stories
                for (i in 0 until minOf(10, idsArray.length())) {
                    val id = idsArray.getInt(i)
                    val itemUrl = URL("https://hacker-news.firebaseio.com/v0/item/$id.json")
                    val itemConn = itemUrl.openConnection() as HttpURLConnection
                    itemConn.requestMethod = "GET"
                    
                    if (itemConn.responseCode == 200) {
                        val itemReader = BufferedReader(InputStreamReader(itemConn.inputStream))
                        val itemStr = itemReader.readText()
                        itemReader.close()
                        
                        val itemJson = JSONObject(itemStr)
                        val title = itemJson.optString("title", "Unknown Title")
                        val url = itemJson.optString("url", "https://news.ycombinator.com/item?id=$id")
                        val score = itemJson.optInt("score", 0)
                        
                        // Hacker news has no images in API, we will just use empty string or a placeholder
                        newsList.add(NewsItem(title, url, "$score points", ""))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        newsList
    }
}
