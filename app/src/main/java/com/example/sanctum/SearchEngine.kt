package com.example.sanctum

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URLEncoder

data class SearchResult(
    val title: String,
    val url: String,
    val snippet: String
)

object SearchEngine {
    suspend fun performSearch(query: String): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val doc = Jsoup.connect("https://html.duckduckgo.com/html/?q=$encodedQuery")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .timeout(5000)
                .get()

            val resultElements = doc.select(".result")
            for (element in resultElements) {
                val titleElement = element.select(".result__title > a.result__a").first()
                val snippetElement = element.select(".result__snippet").first()
                
                if (titleElement != null) {
                    val title = titleElement.text()
                    var url = titleElement.attr("href")
                    if (url.startsWith("//")) {
                        url = "https:$url"
                    }
                    val snippet = snippetElement?.text() ?: ""
                    
                    results.add(SearchResult(title, url, snippet))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        results
    }
}
